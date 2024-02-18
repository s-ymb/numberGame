package com.s_ymb.numbergame.ui.home
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.s_ymb.numbergame.R
import com.s_ymb.numbergame.data.AppContainer
import com.s_ymb.numbergame.data.CellData
import com.s_ymb.numbergame.data.GridData
import com.s_ymb.numbergame.data.NumbergameData
import com.s_ymb.numbergame.data.SatisfiedGrid
import com.s_ymb.numbergame.data.SatisfiedGridList
import com.s_ymb.numbergame.data.SatisfiedGridTbl
import com.s_ymb.numbergame.data.SatisfiedGridTblRepository
import com.s_ymb.numbergame.data.SavedCellTbl
import com.s_ymb.numbergame.data.SavedGrid
import com.s_ymb.numbergame.data.SavedGridTbl
import com.s_ymb.numbergame.data.SavedGridTblRepository
import com.s_ymb.numbergame.data.SavedTbl
import com.s_ymb.numbergame.data.ScreenBtnData
import com.s_ymb.numbergame.data.ScreenCellData
import com.s_ymb.numbergame.data.dupErr
import com.s_ymb.numbergame.data.toSatisfiedGrid
import com.s_ymb.numbergame.data.toSatisfiedGridTbl
import com.s_ymb.numbergame.data.toSavedGrid
import com.s_ymb.numbergame.data.toSavedGridTbl
import com.s_ymb.numbergame.ui.navigation.NavigationDestination
import com.s_ymb.numbergame.ui.savedGrid.SavedDetailDestination
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object NumbergameDestination : NavigationDestination {
    override val route = ""
    override val titleRes = R.string.savedGrid_detail_title
    const val savedIdArg = "itemId"
    val routeWithArgs = "$route/{$savedIdArg}"
}


/*
class NumbergameViewModelFactory(private val appContainer: AppContainer) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NumbergameViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NumbergameViewModel(appContainer) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
*/
/*
class NumbergameViewModelFactory(private val gridData: GridData) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NumbergameViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NumbergameViewModel(gridData) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
*/

class NumbergameViewModel(
    savedStateHandle: SavedStateHandle,
    private val appContainer: AppContainer) : ViewModel() {

    // Game UI state
    private val _uiState = MutableStateFlow(NumbergameUiState())
    public val uiState: StateFlow<NumbergameUiState> = _uiState.asStateFlow()

    private val gridData = GridData()
    private val satisfiedList = SatisfiedGridList()         //正解リスト

    private val satisfiedRepo = appContainer.satisfiedGridTblRepository
//    private val savedRepo = appContainer.savedGridTblRepository
    private val savedTblRepo = appContainer.savedTblRepository
    private val savedCellTblRepo = appContainer.savedCellTblRepository
    // ↓ 保存一覧以外からの遷移の場合はNULL
    private val id: Int ?= savedStateHandle[NumberGameScreenDestination.NumberGameScreenIdArg]


    private var fixCellCnt = 30
    private var selectedRow = NumbergameData.IMPOSSIBLE_IDX                     //選択中セルの行番号
    private var selectedCol = NumbergameData.IMPOSSIBLE_IDX                     //選択中セルの列番号

    private fun getSatisfiedList() {
            // 正解リストに登録されている情報を正解リストに追加する
        viewModelScope.launch(Dispatchers.IO) {
            val satisfiedGrids = satisfiedRepo.getAll()
            satisfiedGrids.forEach {
                satisfiedList.dataList.add(it.toSatisfiedGrid())
            }
        }
    }

    private fun getSaved(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
//            val savedGridTbl = savedRepo.get(id)
            val savedTbl = savedTblRepo.get(id)
            //指定IDのデータが存在すれば
            if(null != savedTbl){
                // とりあえず作成者・作成日は無視するので saveTbl の記述はなし
                // val data: SavedGrid = savedTbl.toSavedGrid()
                // gridData.resumeGame(data.data)
                //

                // SavedCellTbl より 指定IDのデータを取得しGridData にセットする
                val data:  Array<Array<CellData>> =
                                Array(NumbergameData.NUM_OF_ROW)
                                {
                                    Array(NumbergameData.NUM_OF_COL)
                                        { CellData(0, false)
                                    }
                                }
                val savedCellTblList: List<SavedCellTbl> = savedCellTblRepo.get(id)
                savedCellTblList.forEach{
                    data[it.row][it.col].num = it.num
                    data[it.row][it.col].init = it.init
                }
                gridData.resumeGame(data)

                // 画面表示
                setGridDataToUiState()
            }
        }
    }

    init {

        // セルを空に設定
        clearGame()
        // 保存一覧からの遷移の場合はパラメータのidをキーにレポジトリより読み込みgridDataにセットする
        if(id != null) {
            getSaved(id)
        }
        // 正解リストをRoomのレポジトリより読み込み初期リストに追加
        getSatisfiedList()
    }


    private suspend fun insertSatisfiedGrid(createUser: String = "", gridData: String = "") {
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ISO_DATE_TIME
        val currentString = current.format(formatter)

        satisfiedRepo.insert(SatisfiedGridTbl(
                                            createDt = currentString,
                                            createUser = createUser,
                                            gridData = gridData))
    }

/*
    private suspend fun insertSavedGrid(createUser: String = "", gridData: String = "", gridDataIsInit: String = "") {
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ISO_DATE_TIME
        val currentString = current.format(formatter)

        savedRepo.insert(SavedGridTbl(
                                    createDt = currentString,
                                    createUser = createUser,
                                    gridData = gridData,
                                    gridDataIsInit =  gridDataIsInit, ))
    }
*/
    private suspend fun insertSaved(createUser: String = "", data: Array<Array<CellData>> =
        Array(NumbergameData.NUM_OF_ROW) { Array(NumbergameData.NUM_OF_COL) { CellData(0, false) } })
    {
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ISO_DATE_TIME
        val currentString = current.format(formatter)

        // savedTbl に登録
        val insertedId = savedTblRepo.insert(
            SavedTbl(createUser = createUser, createDt = currentString)).toInt()
        // saveCellTbl に登録
        for((rowIdx, rowData) in data.withIndex()){
            for((colIdx, cellData) in rowData.withIndex()){
                if(cellData.num != NumbergameData.NUM_NOT_SET){
                    //未設定のデータ以外はSavedCellTbl に保存
                    savedCellTblRepo.insert(
                        SavedCellTbl(
                                id = insertedId,
                                row = rowIdx,
                                col = colIdx,
                                num = cellData.num,
                                init = cellData.init
                        )
                    )
                }
            }
        }
    }

    /*
        データクラスとviewModelの選択中のセルの情報を基にUIステートに値を設定する
    */
    private fun setGridDataToUiState() {
        //ui state にセル情報設定用の変数
        val tmpData: Array<Array<ScreenCellData>> = Array(NumbergameData.NUM_OF_ROW)
        {
            Array(NumbergameData.NUM_OF_COL)
            {
                ScreenCellData(CellData.NUM_NOT_SET,  false, false, false)
            }
        }
        //ui stateにボタン情報設定用の変数
        val tmpBtn: Array<ScreenBtnData> = Array(NumbergameData.KIND_OF_DATA + 1) {ScreenBtnData(0)}

        // ９×９の数字の配列をui state に設定する
        // 選択中のセルと同じ数字の表示を変える為に選択中のセルの数字を保存
        var selectedNum = CellData.IMPOSSIBLE_NUM       //初期値はあり逢えない数
        if (selectedRow != CellData.IMPOSSIBLE_NUM && selectedCol != CellData.IMPOSSIBLE_NUM){
            //セルが選択されていた場合、選択中のセルの値を保存
            selectedNum = gridData.data[selectedRow][selectedCol].num
        }
        // セルに
        //      ・番号（初期値で編集不可かも）を設定
        //      ・ボタンに表示する表示済みの数字毎の数を集計
        //      ・未設定のセルの数を集計（ゲーム終了判定用）
        var blankCellCnt: Int = 0                   //未設定セルの数
        for(rowIdx in 0 until NumbergameData.NUM_OF_ROW){
            for(colIdx in 0 until NumbergameData.NUM_OF_COL){
                //セルの設定
                tmpData[rowIdx][colIdx].num = gridData.data[rowIdx][colIdx].num
                tmpData[rowIdx][colIdx].init = gridData.data[rowIdx][colIdx].init
                //数字ボタンの設定
                tmpBtn[gridData.data[rowIdx][colIdx].num].cnt++         //ボタンに表示する、数字毎の数をインクリメント
                // 選択中のセルの場合、
                if(rowIdx == selectedRow && colIdx == selectedCol){
                    //セルを選択中にする
                    tmpData[rowIdx][colIdx].isSelected = true
                }
                // 選択中のセルと同じ数字の場合（空白以外）、UIで強調表示するためフラグを設定
                tmpData[rowIdx][colIdx].isSameNum = false
                if(selectedNum != CellData.NUM_NOT_SET) {
                    if (selectedNum == gridData.data[rowIdx][colIdx].num) {
                        tmpData[rowIdx][colIdx].isSameNum = true
                    }
                }
                //未設定セルの数をカウント
                if(gridData.data[rowIdx][colIdx].num == CellData.NUM_NOT_SET){
                    //未設定セルの場合、未設定セルの数をカウントアップ
                    blankCellCnt++
                }
            }
        }
        //未設定セルの数が０個の場合、ゲーム終了とする
        var isGameOver = false
        if(blankCellCnt == 0){
            // 空欄が０件の場合、ゲーム終了なので正解データのが追加できる場合は追加する
            var gridDataString: String = ""
            for(rowIdx in 0 until NumbergameData.NUM_OF_ROW) {
                for (colIdx in 0 until NumbergameData.NUM_OF_COL) {
                    //セルの設定
                    gridDataString += gridData.data[rowIdx][colIdx].num.toString()
                }
            }
            viewModelScope.launch(Dispatchers.IO) {
                //すでに登録されているデータ数を検索(0 or 1 件)
                val dataCnt = satisfiedRepo.getCnt(gridDataString)
                if (0 == dataCnt) {
                    //０件の場合データ追加
                    insertSatisfiedGrid(createUser = "User", gridData = gridDataString)
                }
            }
            isGameOver = true
        }
        _uiState.value = NumbergameUiState(
                            currentData = tmpData,
                            currentBtn = tmpBtn,
                            fixCellCnt = fixCellCnt,
                            isGameOver = isGameOver
                        )
    }

    /*
        画面初期化
     */
    fun resetGame(){
        //固定セル以外は消す
        gridData.clearAll(false)
        //選択中セルの初期化
        selectedCol = NumbergameData.IMPOSSIBLE_IDX
        selectedRow = NumbergameData.IMPOSSIBLE_IDX

        setGridDataToUiState()
    }

    fun clearGame(){
        // 全てのセルを消す
        gridData.clearAll(true)
        //選択中セルの初期化
        selectedCol = NumbergameData.IMPOSSIBLE_IDX
        selectedRow = NumbergameData.IMPOSSIBLE_IDX

        setGridDataToUiState()
    }

    /*
        新規ゲーム
     */
    fun newGame(){
        //正解リストより初期値を設定する
        val satisfiedIdx: Int=  (0 until satisfiedList.dataList.size).random()
        gridData.newGame(satisfiedList.dataList[satisfiedIdx].data, fixCellCnt)
        //選択中セルの初期化
        selectedCol = NumbergameData.IMPOSSIBLE_IDX
        selectedRow = NumbergameData.IMPOSSIBLE_IDX

        setGridDataToUiState()

    }

    /*
        正解パターン数検索
     */
    fun searchAnsCnt() {
        val ansCnt = Array(NumbergameData.KIND_OF_DATA + 1) { 0 }
        var retList: MutableList<Array<Array<Int>>> = mutableListOf()
        if ((selectedRow != NumbergameData.IMPOSSIBLE_IDX) && (selectedCol != NumbergameData.IMPOSSIBLE_IDX)) {
            for (num in 1..NumbergameData.KIND_OF_DATA) {
                retList = gridData.searchAnswer(selectedRow, selectedCol, num)
                ansCnt[num] = retList.size

                // 見つけた回答をRoom に保存
                retList.forEach {
                    var gridDataString: String = ""
                    for (rowIdx in 0 until NumbergameData.NUM_OF_ROW) {
                        for (colIdx in 0 until NumbergameData.NUM_OF_COL) {
                            //セルの設定
                            gridDataString += it[rowIdx][colIdx].toString()
                        }
                    }
                    viewModelScope.launch(Dispatchers.IO) {
                        //すでに登録されているデータ数を検索(0 or 1 件)
                        val dataCnt = satisfiedRepo.getCnt(gridDataString)
                        if (0 == dataCnt) {
                            //０件の場合データ追加
                            insertSatisfiedGrid(
                                createUser = "Search",
                                gridData = gridDataString
                            )
                        }
                    }
                }
            }
        }
        //検索結果をui state に保存
        _uiState.update { currentState ->
            currentState.copy(
                haveSearchResult = true,
                currentSearchResult = ansCnt,
            )
        }
    }


    /*
        ９×９のセルがクリックされた時、セルを選択状態にする
     */
    fun onCellClicked(rowId: Int, colId: Int){
        selectedRow = rowId
        selectedCol = colId
        setGridDataToUiState()
    }

    /*
        番号のボタンが押された場合、選択中のセルに番号を設定する
     */
    fun onNumberBtnClicked(number: Int){
        if((selectedRow != NumbergameData.IMPOSSIBLE_IDX) && (selectedCol != NumbergameData.IMPOSSIBLE_IDX)) {
            val ret = gridData.setData(selectedRow, selectedCol, number, false)
            if (dupErr.NO_DUP == ret) {
                // 設定できた場合、新しいデータでui_state を再構築
                setGridDataToUiState()
            }
            _uiState.update { currentState ->
                currentState.copy(
                    errBtnNum = number,
                    errBtnMsgID = ret,
                )
            }
        }
    }

    /*
        スライダーで新規作成時の固定セルの個数が変更されたときメンバー変数に反映する
    */
    fun setFixCellCnt(fixCnt: Int){
        fixCellCnt = fixCnt
    }

    /*
            一時保存ボタンが押された時の処理
     */
    fun onSaveBtnClicked(){
/*
        var gridDataString: String = ""
        var gridDataIsInitString: String = ""
        for(rowIdx in 0 until NumbergameData.NUM_OF_ROW) {
            for (colIdx in 0 until NumbergameData.NUM_OF_COL) {
                //セルの設定
                gridDataString += gridData.data[rowIdx][colIdx].num.toString()
                if(gridData.data[rowIdx][colIdx].init){
                    // 初期データは "1"
                    gridDataIsInitString += "1"
                }else{
                    gridDataIsInitString += "0"
                }
            }
        }
 */
        viewModelScope.launch(Dispatchers.IO) {
            //０件の場合データ追加
            insertSaved(createUser = "User", gridData.data)
        }
    }
}