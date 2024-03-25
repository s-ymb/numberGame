package io.github.s_ymb.numbergame.ui.home

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.view.animation.LinearInterpolator
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.s_ymb.numbergame.R
import io.github.s_ymb.numbergame.data.EndAnimationDataInit
import io.github.s_ymb.numbergame.data.NumbergameData
import io.github.s_ymb.numbergame.data.ScreenBtnData
import io.github.s_ymb.numbergame.data.ScreenCellData
import io.github.s_ymb.numbergame.ui.navigation.NavigationDestination
import io.github.s_ymb.numbergame.ui.theme.AppViewModelProvider

object NumbergameScreenDestination : NavigationDestination {
    override val route = "NumbergameScreen"
    override val titleRes = R.string.number_game_screen_title
    const val NumbergameScreenIdArg = "itemId"
    val routeWithArgs = "$route/{$NumbergameScreenIdArg}"
}

/**
 * 　数独のメイン画面
 */
//@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun GameScreen(
    navigateToSatisfiedGridTbl: () -> Unit,
    navigateToSavedGridTbl: () -> Unit,
    modifier: Modifier = Modifier,
    gameViewModel: NumbergameViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val gameUiState by gameViewModel.uiState.collectAsState()
    val toastUiState by gameViewModel.toastUiState.collectAsState()
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier,
    ) {
        // グリッド表示
        if (gameUiState.isGameOver){
                // ゲーム終了時はアニメーションを出す画面を呼び出す
                GameOverNumberGridLayout(
                    uiState = gameUiState,
                    onNewGameBtnClicked = { gameViewModel.newGame() },
//                    currentData = gameUiState.currentData,
//                    isNewSatisfied = gameUiState.isNewSatisfied,
                    modifier = modifier,
                )
        } else {
            // 通常時は設定された数字を９×９のテキストボックスで表示する
            NumberGridLayout(
                onCellClicked = { rowId, colId -> gameViewModel.onCellClicked(rowId, colId) },
                currentData = gameUiState.currentData,
                currentDataOrgName = gameUiState.currentDataOrgName,
//                currentDataOrgCreateDt = gameUiState.currentDataOrgCreateDt,
                modifier = modifier,
            )

        }

        // 数字ボタン表示
        NumBtnLayout(
            onNumBtnClicked = { num: Int -> gameViewModel.onNumberBtnClicked(num) },
            currentBtn = gameUiState.currentBtn,
            modifier = modifier,
        )
        Spacer(
            modifier = Modifier
                .size(8.dp)
            //.background(color=Color.Red)
        )

        // エラーメッセージ用のToast を表示
        if(toastUiState.showToast) {
            val context = LocalContext.current
            val toast = Toast.makeText(context, toastUiState.toastMsg, Toast.LENGTH_LONG)
            // TODO 設定しても下端に出るので置いておく
            // toast.setGravity(Gravity.TOP, 0, 0);
            toast.show()
            // toast 表示済に更新
            gameViewModel.toastShown()
        }
//                val toast = Toast.makeText(context, msg, Toast.LENGTH_LONG)
//                //toast.setGravity(Gravity.TOP, 0, 0);
//                toast.show()


        // とりあえず検索結果を表示するレイアウトを入れる
        if (gameUiState.haveSearchResult) {
            SearchResultLayout(
                searchResult = gameUiState.currentSearchResult,
            )
        }
        // スライダーを表示
        SliderLayout(
            defaultPos = gameUiState.blankCellCnt.toFloat(),
            onValueChangeFinished = { num: Int -> gameViewModel.setBlankCellCnt(num) },
            modifier = modifier,
        )

        // 機能ボタン表示
        FunBtnLayout(
            onNewGameBtnClicked = { gameViewModel.newGame() },
            onResetGameBtnClicked = { gameViewModel.resetGame() },
            onClearGameBtnClicked = { gameViewModel.clearGame() },
            onSearchGameBtnClicked = { gameViewModel.searchAnsCnt() },
            modifier = modifier,
        )

        //追加機能ボタン表示
        SaveBtnLayout(
//            onGoSatisfiedGridTbl = { navigateToSatisfiedGridTbl() },
            onGoSavedGridTbl = {navigateToSavedGridTbl()},
            onSavedBtnClicked = {asChallenge:Boolean -> gameViewModel.onSaveBtnClicked(asChallenge) },
            modifier = modifier,
        )

        // 正解一覧ボタンと終了ボタン表示
        EndBtnLayout(
            onGoSatisfiedGridTbl = { navigateToSatisfiedGridTbl() },
        )

    }
}
/*
        ９×９の２次元グリッドを描画
 */
@Composable
fun NumberGridLayout(
    onCellClicked: (Int, Int) -> Unit,
    currentData: Array<Array<ScreenCellData>>,
    currentDataOrgName: String,
//    currentDataOrgCreateDt: String,
    modifier: Modifier = Modifier
) {
    if(currentDataOrgName != "" ) {
        Text(
            text = stringResource(R.string.number_game_org_satisfied_title) + currentDataOrgName,
            textAlign = TextAlign.Center,
            fontSize = 12.sp,
        )
    }
    for ((rowIdx: Int, rowData: Array<ScreenCellData>) in currentData.withIndex()) {
        Row(
            verticalAlignment = Alignment.Top,
        ) {
            for ((colIdx: Int, cell: ScreenCellData) in rowData.withIndex()) {
                var borderWidth: Int
                borderWidth = 2
                var borderColor: Color = colorResource(R.color.cell_border_color_not_selected)
                var textColor: Color= Color.Black
                var fWeight: FontWeight = FontWeight.Light
                if (cell.isSelected) {
                    // 選択済みのセルは表示枠を変更
                    borderWidth = 4
                    borderColor = colorResource(R.color.cell_border_color_selected)
                }

                if(cell.isSameNum){
                    // 選択済みのセルと同じ数字の場合、
                    // 文字を太字に設定
                    fWeight = FontWeight.ExtraBold
                    // テキストの色を設定
                    textColor = colorResource(R.color.cell_text_color_same_num)
//                    textColor = Color.Red
                }

                var bgColor: Color = colorResource(R.color.cell_bg_color_default)
                if (cell.init) {
                    //初期設定されたセルの場合は背景色をグレーに
                    bgColor = colorResource(R.color.cell_bg_color_init)
                }
                var numStr: String
                numStr = ""
                if (cell.num != NumbergameData.NUM_NOT_SET) {
                    numStr = cell.num.toString()
                }
                Text(
                    text = numStr,
                    color = textColor,
                    textAlign = TextAlign.Center,
                    fontSize = 24.sp,
                    fontWeight = fWeight,
                    modifier = modifier
                        .width(38.dp)
                        .padding(0.dp)
                        .border(
                            border = BorderStroke(
                                width = borderWidth.dp,
                                color = borderColor
                            ),
                            shape = RectangleShape,
                        )
                        .background(
                            color = bgColor,
                        )
                        .clickable {
                            // クリックされたテキスト
                            onCellClicked(rowIdx, colIdx)
                        },

                )
                if (colIdx % 3 == 2 && colIdx != 8) {
                    //平方毎にスペースを開ける
                    Spacer(
                        modifier = modifier
                            .size(4.dp)
                    )
                }
            }
        }
        if (rowIdx % 3 == 2) {
            //平方毎にスペースを開ける
            Spacer(
                modifier = modifier
                    .size(4.dp)
            )
        }
    }
}

/*
        ゲーム終了時の９×９の２次元グリッドを描画
        TODO 中身は殆ど通常時の内容なので統合を考える必要があるか要検討
 */
@Composable
fun GameOverNumberGridLayout(
    onNewGameBtnClicked: () -> Unit,
    uiState: NumbergameUiState,
//    currentData: Array<Array<ScreenCellData>>,
//    isNewSatisfied: Boolean,
    modifier: Modifier = Modifier
) {
    val stage = remember{ mutableIntStateOf(0) }
    val animStart = remember{ mutableStateOf(false) }

    // アニメーションの種類の選択方法も
    val animationNo =
        if(1 == uiState.sameSatisfiedCnt){
            1               //不発のアニメーション（すでに登録済みの正解パターンだったので不発の演出）
        }else{
            // 空欄が０件の場合、ゲーム終了なので未登録の正解データの場合はデータベースに追加する
            // TODO 定数の扱い　dataCnt の関連事項

            0               //爆発のアニメーション sameSatisfiedCnt = -1 件数を検索中の場合もとりあえず０で流す。1でも同じ結果だが…
        }

    val initValue = EndAnimationDataInit.animationData[animationNo].init
    val targetValue = EndAnimationDataInit.animationData[animationNo].data.size - 1
    val setDuration = EndAnimationDataInit.animationData[animationNo].duration
    val animator = ValueAnimator.ofInt(initValue, targetValue).apply{
        duration = setDuration
        interpolator = LinearInterpolator()
        addUpdateListener{
            stage.intValue = it.animatedValue as Int
        }
    }

    // 空欄が０件の場合、ゲーム終了なので未登録の正解データの場合はデータベースに追加する
    // TODO 定数の扱い　dataCnt の関連事項
    //正解の件数判定が終了していてアニメーションがスタートしていない場合開始する
    if(!animStart.value) {
        animator.start()
        animStart.value = true
    }
    //アニメーションの最後の状態になったらダイアログを表示
    if(stage.intValue == targetValue){
        FinalDialog(
            onNewGameBtnClicked,
        )
    }



    for ((rowIdx: Int, rowData: Array<ScreenCellData>) in uiState.currentData.withIndex()) {
        Row(
            verticalAlignment = Alignment.Top,
        ) {
            for ((colIdx: Int, cell: ScreenCellData) in rowData.withIndex()) {
                var borderWidth: Int
                borderWidth = 2
                var borderColor: Color = colorResource(R.color.cell_border_color_not_selected)
                var textColor: Color= Color.Black
                var fWeight: FontWeight = FontWeight.Light
                if (cell.isSelected) {
                    // 選択済みのセルは表示枠を変更
                    borderWidth = 4
                    borderColor = colorResource(R.color.cell_border_color_selected)
                }

                if(cell.isSameNum){
                    // 選択済みのセルと同じ数字の場合、
                    // 文字を太字に設定
                    fWeight = FontWeight.ExtraBold
                    // テキストの色を設定
                    textColor = colorResource(R.color.cell_text_color_same_num)
//                    textColor = Color.Red
                }

                var bgColor: Color = colorResource(R.color.cell_bg_color_default)
                if (cell.init) {
                    //初期設定されたセルの場合は背景色をグレーに
                    bgColor = colorResource(R.color.cell_bg_color_init)
                }
                val numStr: String =
                    if (cell.num != NumbergameData.NUM_NOT_SET) {
                        cell.num.toString()
                    }else{
                        ""
                    }

                //TODO アニメーションのタイプの場合分けの記述
                if(0L != EndAnimationDataInit.animationData[animationNo].data[stage.intValue][rowIdx][colIdx]){
                    bgColor = Color(EndAnimationDataInit.animationData[animationNo].data[stage.intValue][rowIdx][colIdx])
                }
                Text(
                    text = numStr,
                    color = textColor,
                    textAlign = TextAlign.Center,
                    fontSize = 24.sp,
                    fontWeight = fWeight,
                    modifier = modifier
                        .width(38.dp)
                        .padding(0.dp)
                        .border(
                            border = BorderStroke(
                                width = borderWidth.dp,
                                color = borderColor
                            ),
                            shape = RectangleShape,
                        )
                        .background(
                            color = bgColor,
                        )
                    )
                if (colIdx % 3 == 2 && colIdx != 8) {
                    //平方毎にスペースを開ける
                    Spacer(
                        modifier = modifier
                            .size(4.dp)
                    )
                }
            }
        }
        if (rowIdx % 3 == 2) {
            //平方毎にスペースを開ける
            Spacer(
                modifier = modifier
                    .size(4.dp)
            )
        }
    }

}


/*
        数字入力ボタンを表示
 */
@Composable
fun NumBtnLayout(
    onNumBtnClicked: (Int) -> Unit,
    currentBtn: Array<ScreenBtnData>,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.Bottom,
        modifier = modifier,
    ) {
        //ボタンの編集可否を設定
        for (btnNum in 1..5) {
            // 選択中のセルの設定可否を初期設定
            var btnEnabled = true
            // 数字が９個設定してある数字ボタンは使用不可
            if (currentBtn[btnNum].cnt == NumbergameData.MAX_NUM_CNT) {
                btnEnabled = false
            }

            // ボタン押下エラー時の処理

            Button(
                onClick = { onNumBtnClicked(btnNum) },
                enabled = btnEnabled,
            ) {
                Text(text = btnNum.toString())
                Text(
                    text = currentBtn[btnNum].cnt.toString(),
                    fontSize = 9.sp,
                    textAlign = TextAlign.End,
                )
            }
        }
    }
    Row(
        verticalAlignment = Alignment.Bottom,
        modifier = modifier,
        //.background(color= Color.Yellow)
    ) {
        // ６～９のボタン　と　削除ボタン
        for (btnNum in 6..9) {
            // 数字が９個設定してある数字ボタンは使用不可
            var btnEnabled = true
            if (currentBtn[btnNum].cnt == NumbergameData.MAX_NUM_CNT) {
                btnEnabled = false
            }
            Button(
                onClick = { onNumBtnClicked(btnNum) },
                enabled = btnEnabled,
            ) {
                Text(text = btnNum.toString())
                Text(
                    text = currentBtn[btnNum].cnt.toString(),
                    fontSize = 9.sp,
                    textAlign = TextAlign.End,
                )
            }
        }
        //削除ボタン
        Button(
            onClick = { onNumBtnClicked(NumbergameData.NUM_NOT_SET) },
        ) {
            Text(text = stringResource(R.string.btn_num_delete))
        }
    }
}

/*
       機能ボタン（新規・クリア）を表示
 */
@Composable
fun FunBtnLayout(
    onNewGameBtnClicked: () -> Unit,
    onResetGameBtnClicked: () -> Unit,
    onClearGameBtnClicked: () -> Unit,
    onSearchGameBtnClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier,
    ) {
        Row(
            verticalAlignment = Alignment.Top,
        ) {
            //新規ボタン
            Button(
                onClick = { onNewGameBtnClicked() }
            ) {
                Text(text = stringResource(R.string.btn_new))
            }
            //リセットボタン
            Button(
                onClick = { onResetGameBtnClicked() }
            ) {
                Text(text = stringResource(R.string.btn_init_data))
            }
            //クリアボタン
            Button(
                onClick = { onClearGameBtnClicked() }
            ) {
                Text(text = stringResource(R.string.btn_clear_data))
            }
            //検索ボタン
            Button(
                onClick = { onSearchGameBtnClicked() }
            ) {
                Text(text = stringResource(R.string.btn_search))
            }
        }
    }
}

/*
    固定セルの個数を選択するスライダー表示
 */
@Composable
private fun SliderLayout(
    defaultPos: Float,
    onValueChangeFinished: (Int) -> Unit,
    modifier: Modifier = Modifier,
){
    var sliderPosition by remember { mutableFloatStateOf(defaultPos) }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier,
    ) {
            Slider(
                value = sliderPosition,
                // TODO 固定セルの初期値と設定できる範囲の検討が必要
                valueRange = 30f..60f,
                onValueChange = { sliderPosition = it },
                onValueChangeFinished ={onValueChangeFinished(sliderPosition.toInt())},
                //steps = 3,

            )
        val sliderTxt: String = stringResource(R.string.slider_title1) + sliderPosition.toInt().toString() + stringResource(R.string.slider_title2)
        Text(
            text = sliderTxt,
            fontSize = 12.sp,
        )
    }

}


/*
    検索結果表示欄
*/

@Composable
private fun SearchResultLayout(
    searchResult: Array<Int>,
    modifier: Modifier = Modifier,
){
    // TODO strings.xml に移動する
    Text(
        text = stringResource(R.string.search_result_title),
        textAlign = TextAlign.Center,
        fontSize = 16.sp,
    )
    Row(
        verticalAlignment = Alignment.Top,
        modifier = modifier,
        //.background(color= Color.Yellow)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(text = stringResource(R.string.search_result_num))
            Text(text = stringResource(R.string.search_result_cnt))
        }
        for (colIdx in 1..9) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = modifier,
            ) {
                Text(
                    text = colIdx.toString(),
                    textAlign = TextAlign.Center,
                    modifier = Modifier         //修正
                        .width(33.dp)
                        .padding(0.dp)
                        .border(
                            border = BorderStroke(
                                width = 1.dp,
                                color = Color.Black
                            ),
                            shape = RectangleShape,
                        )
                )

                Text(
                    text = searchResult[colIdx].toString(),
                    textAlign = TextAlign.Center,
                    modifier = Modifier                 //
                        .width(33.dp)
                        .padding(0.dp)
                        .border(
                            border = BorderStroke(
                                width = 1.dp,
                                color = Color.Black
                            ),
                            shape = RectangleShape,
                        )
                )
            }
        }
    }
}

/*
        保存ボタンを表示
 */

@Composable
private fun SaveBtnLayout(
    onGoSavedGridTbl: () -> Unit,
    onSavedBtnClicked: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
){
        Row(
            verticalAlignment = Alignment.Top,
            modifier = modifier,
        ) {
            //戦歴ボタン
            Button(
                onClick = { onGoSavedGridTbl() }
            ) {
                Text(text = stringResource(R.string.btn_save_list))
            }
            // 現状を一時保存する
            Button(
                onClick = { onSavedBtnClicked(false) }
            ) {
                Text(text = stringResource(R.string.btn_save))
            }
            // 現状を問題データとして保存
            Button(
                onClick = { onSavedBtnClicked(true) }
            ) {
                Text(text = stringResource(R.string.btn_save_challenge))
            }
        }
//    }
}

/*
 * 終了ボタン
 */
@Composable
fun EndBtnLayout(
    onGoSatisfiedGridTbl: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier,
    ) {
        Row(
            verticalAlignment = Alignment.Top,
            modifier = modifier,
        ) {
            Button(
                onClick = { onGoSatisfiedGridTbl() }
            ) {
                Text(text = stringResource(R.string.btn_satisfied_list))
            }
            //終了ボタン
            val activity = (LocalContext.current as Activity)
            Button(
                onClick = { activity.finish() }
            ) {
                Text(text = stringResource(R.string.btn_exit))
            }
        }
    }
}


/*
 * 終了確認ダイアログ
 */
@Composable
private fun FinalDialog(
    onNewGameBtnClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val activity = (LocalContext.current as Activity)
    AlertDialog(
        modifier = modifier,
        onDismissRequest = {
            // Dismiss the dialog when the user clicks outside the dialog or on the back
            // button. If you want to disable that functionality, simply use an empty
            // onCloseRequest.
        },
        title = { Text(text = stringResource(R.string.congratulations)) },
        dismissButton = {
            TextButton(
                onClick = {
                    activity.finish()
                }
            ) {
                Text(text = stringResource(R.string.btn_exit))
            }
        },
        confirmButton = {
            TextButton(onClick = onNewGameBtnClicked) {
                Text(text = stringResource(R.string.btn_new))
            }
        }
    )

}

