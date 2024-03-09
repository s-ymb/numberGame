package io.github.s_ymb.numbergame.ui.home
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.s_ymb.numbergame.data.AppContainer
import io.github.s_ymb.numbergame.data.CellData
import io.github.s_ymb.numbergame.data.GridData
import io.github.s_ymb.numbergame.data.NumbergameData
import io.github.s_ymb.numbergame.data.NumbergameData.Companion.IMPOSSIBLE_IDX
import io.github.s_ymb.numbergame.data.NumbergameData.Companion.IMPOSSIBLE_NUM
import io.github.s_ymb.numbergame.data.SatisfiedGridData
import io.github.s_ymb.numbergame.data.SatisfiedGridTbl
import io.github.s_ymb.numbergame.data.SavedCellTbl
import io.github.s_ymb.numbergame.data.SavedTbl
import io.github.s_ymb.numbergame.data.ScreenBtnData
import io.github.s_ymb.numbergame.data.ScreenCellData
import io.github.s_ymb.numbergame.data.dupErr
import io.github.s_ymb.numbergame.data.toSatisfiedGrid
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class NumbergameViewModel(
    savedStateHandle: SavedStateHandle,
    private val appContainer: AppContainer) : ViewModel() {

    // Game UI state
    private val _uiState = MutableStateFlow(NumbergameUiState())
    val uiState: StateFlow<NumbergameUiState> = _uiState.asStateFlow()

    private val gridData = GridData()
    private val satisfiedGridData = SatisfiedGridData()         //正解リスト

    // ↓ 保存一覧以外からの遷移の場合はNULL
    private val id: Int ?= savedStateHandle[NumbergameScreenDestination.NumbergameScreenIdArg]

    // TODO 固定セルの初期値と設定できる範囲の検討が必要
    private var fixCellCnt = 30
    private var selectedRow = IMPOSSIBLE_IDX                             //選択中セルの行番号
    private var selectedCol = IMPOSSIBLE_IDX                     //選択中セルの列番号


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

    private fun getSatisfiedList() {
            // 正解リストに登録されている情報を正解リストに追加する
        viewModelScope.launch(Dispatchers.IO) {
            val satisfiedGrids = appContainer.satisfiedGridTblRepository.getAll()
            satisfiedGrids.forEach {
                satisfiedGridData.add(it.toSatisfiedGrid())
            }
        }
    }

    private fun getSaved(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val savedTbl = appContainer.savedTblRepository.get(id)
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
                val savedCellTblList: List<SavedCellTbl> = appContainer.savedCellTblRepository.get(id)
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



    private suspend fun insertSatisfiedGrid(createUser: String = "", gridData: String = "") {
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")
        val currentString = current.format(formatter)

        appContainer.satisfiedGridTblRepository.insert(SatisfiedGridTbl(
                                            createDt = currentString,
                                            createUser = createUser,
                                            gridData = gridData))
    }

    /*
        指定ユーザ・現在時刻でデータをROOMのsaveTbl にインサートする
        saveTbl にインサート時の戻りで列のIDを取得し、９×９のセルで未設定以外のセルの情報を
        savedCellTbl にインサートする
    */
    private suspend fun insertSaved(createUser: String = "", data: Array<Array<CellData>> =
        Array(NumbergameData.NUM_OF_ROW) { Array(NumbergameData.NUM_OF_COL) { CellData(0, false) } })
    {
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")
        val currentString = current.format(formatter)

        // savedTbl に登録
        val insertedId = appContainer.savedTblRepository.insert(
            SavedTbl(createUser = createUser, createDt = currentString)).toInt()
        // saveCellTbl に登録
        for((rowIdx, rowData) in data.withIndex()){
            for((colIdx, cellData) in rowData.withIndex()){
                if(cellData.num != NumbergameData.NUM_NOT_SET){
                    //未設定のデータ以外はSavedCellTbl に保存
                    appContainer.savedCellTblRepository.insert(
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
                                                            ScreenCellData(
                                                                num = NumbergameData.NUM_NOT_SET,
                                                                init = false,
                                                                isSelected = false,
                                                                isSameNum = false
                                                            )
                                                        }
                                                    }
        //ui stateにボタン情報設定用の変数
        val tmpBtn: Array<ScreenBtnData> = Array(NumbergameData.KIND_OF_DATA + 1) {ScreenBtnData(0)}

        // ９×９の数字の配列をui state に設定する
        // 選択中のセルと同じ数字の表示を変える為に選択中のセルの数字を保存
        // TODO 選択中のセルに入力された値がエラーの場合の設定方法を要件等
        val selectedNum =
        if(selectedRow != IMPOSSIBLE_NUM && selectedCol != IMPOSSIBLE_NUM){
            gridData.data[selectedRow][selectedCol].num
        }else{
            IMPOSSIBLE_NUM
        }

        // セルに
        //      ・番号（初期値で編集不可かも）を設定
        //      ・ボタンに表示する表示済みの数字毎の数を集計
        //      ・未設定のセルの数を集計（ゲーム終了判定用）
//        var blankCellCnt: Int = 0                   //未設定セルの数
        for(rowIdx in 0 until NumbergameData.NUM_OF_ROW){
            for(colIdx in 0 until NumbergameData.NUM_OF_COL){
                //セルの設定
                tmpData[rowIdx][colIdx].num = gridData.data[rowIdx][colIdx].num
                tmpData[rowIdx][colIdx].init = gridData.data[rowIdx][colIdx].init
                //数字ボタンの設定
                tmpBtn[gridData.data[rowIdx][colIdx].num].cnt++         //ボタンに表示する、数字毎の数をインクリメント
                // 選択中のセルの場合、
                tmpData[rowIdx][colIdx].isSelected =  (rowIdx == selectedRow && colIdx == selectedCol)

//                if(rowIdx == selectedRow && colIdx == selectedCol){
//                    //セルを選択中にする
//                    tmpData[rowIdx][colIdx].isSelected = true
//                }
                // 選択中のセルと同じ数字の場合（空白以外）、UIで強調表示するためフラグを設定
                tmpData[rowIdx][colIdx].isSameNum = false
                if(selectedNum != NumbergameData.NUM_NOT_SET) {
                    if (selectedNum == gridData.data[rowIdx][colIdx].num) {
                        tmpData[rowIdx][colIdx].isSameNum = true
                    }
                }
//                //未設定セルの数をカウント
//                if(gridData.data[rowIdx][colIdx].num == NumbergameData.NUM_NOT_SET){
//                    //未設定セルの場合、未設定セルの数をカウントアップ
//                    blankCellCnt++
            }
        }
        //未設定セルの数が０個の場合、ゲーム終了とする

        var isGameOver = true

        gridData.data.forEach {
            it.forEach{
                isGameOver = isGameOver && (it.num != NumbergameData.NUM_NOT_SET)
            }
        }

        if(isGameOver){
            // 空欄が０件の場合、ゲーム終了なので未登録の正解データの場合はデータベースに追加する
            val gridStr = StringBuilder()
            for(rowIdx in 0 until NumbergameData.NUM_OF_ROW) {
                val rowStr = StringBuilder()
                for (colIdx in 0 until NumbergameData.NUM_OF_COL) {
                    //セルの設定
                    rowStr.append(gridData.data[rowIdx][colIdx].num.toString())
                }
                gridStr.append(rowStr)
            }
            viewModelScope.launch(Dispatchers.IO) {
                //すでに登録されているデータ数を検索(0 or 1 件)
                val dataCnt = appContainer.satisfiedGridTblRepository.getCnt(gridStr.toString())
                if (0 == dataCnt) {
                    //０件の場合データ追加
                    insertSatisfiedGrid(createUser = "User", gridData = gridStr.toString())
                }
            }
        }
        _uiState.value = NumbergameUiState(
                            currentData = tmpData,
                            currentBtn = tmpBtn,
                            fixCellCnt = fixCellCnt,
                            isGameOver = isGameOver,
                            errBtnMsgID = dupErr.NO_DUP,
                            errBtnNum = 0,
        )
    }


    /*
        画面初期化
     */
    fun resetGame(){
        //固定セル以外は消す
        gridData.clearAll(false)
        //選択中セルの初期化
        selectedCol = IMPOSSIBLE_IDX
        selectedRow = IMPOSSIBLE_IDX

        setGridDataToUiState()
    }

    fun clearGame(){
        // 全てのセルを消す
        gridData.clearAll(true)
        //選択中セルの初期化
        selectedCol = IMPOSSIBLE_IDX
        selectedRow = IMPOSSIBLE_IDX

        setGridDataToUiState()
    }

    /*
        新規ゲーム
     */
    fun newGame(){
        //正解リストより初期値を設定する
        gridData.newGame(satisfiedGridData.getSatisfied(), fixCellCnt)
        //選択中セルの初期化
        selectedCol = IMPOSSIBLE_IDX
        selectedRow = IMPOSSIBLE_IDX

        setGridDataToUiState()

    }

    /*
        正解パターン数検索
    */
    fun searchAnsCnt() {
        val ansCnt = Array(NumbergameData.KIND_OF_DATA + 1) { 0 }
        var retList: MutableList<Array<Array<Int>>>
        if ((selectedRow != IMPOSSIBLE_IDX) && (selectedCol != IMPOSSIBLE_IDX)) {
            for (num in 1..NumbergameData.KIND_OF_DATA) {
                retList = gridData.searchAnswer(selectedRow, selectedCol, num)
                ansCnt[num] = retList.size

                // 見つけた回答をRoom に保存
                retList.forEach {
                    val gridStr = StringBuilder()
                    for (rowIdx in 0 until NumbergameData.NUM_OF_ROW) {
                        val rowStr = StringBuilder()
                        for (colIdx in 0 until NumbergameData.NUM_OF_COL) {
                                //セルの設定
                                rowStr.append(it[rowIdx][colIdx].toString())
                        }
                        gridStr.append(rowStr)
                    }
                    /*
                    for (rowIdx in 0 until NumbergameData.NUM_OF_ROW) {
                        for (colIdx in 0 until NumbergameData.NUM_OF_COL) {
                            //セルの設定
                            gridDataString += it[rowIdx][colIdx].toString()
                        }
                    }
                    */
                    viewModelScope.launch(Dispatchers.IO) {
                        //すでに登録されているデータ数を検索(0 or 1 件)
                        val dataCnt = appContainer.satisfiedGridTblRepository.getCnt(gridStr.toString())
                        if (0 == dataCnt) {
                            //０件の場合データ追加
                            insertSatisfiedGrid(
                                //TODO strings.xml の登録方法は要検討
                                createUser = "検索で見つけた正解",
                                gridData = gridStr.toString()
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
        if((selectedRow != IMPOSSIBLE_IDX) && (selectedCol != IMPOSSIBLE_IDX)) {
            // 画面で数字を入力する場所が選択されていた場合
            val ret = gridData.setData(selectedRow, selectedCol, number, false)
            if (dupErr.NO_DUP == ret) {
                // 設定できた場合、新しいデータでui_state を再構築
                setGridDataToUiState()
            } else {
                val currentUiState = _uiState
                val newUiSatateValue =
                    currentUiState.value.copy(errBtnNum = number, errBtnMsgID = ret)
                _uiState.value = newUiSatateValue

//            _uiState.update { currentUiState ->
//                currentUiState.copy(
//                    errBtnNum = number,
//                    errBtnMsgID = ret,
//                )
//            }
//            val newUiState = _uiState.value
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
        viewModelScope.launch(Dispatchers.IO) {
            //０件の場合データ追加
            insertSaved(createUser = "User", gridData.data)
        }
    }
}