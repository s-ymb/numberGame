package com.s_ymb.numbergame.ui.home

import android.annotation.SuppressLint
import android.app.Activity
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
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import com.s_ymb.numbergame.R
import com.s_ymb.numbergame.data.NumbergameData
import com.s_ymb.numbergame.data.ScreenBtnData
import com.s_ymb.numbergame.data.ScreenCellData
import com.s_ymb.numbergame.data.dupErr
import com.s_ymb.numbergame.ui.navigation.NavigationDestination
import com.s_ymb.numbergame.ui.theme.AppViewModelProvider

object NumbergameScreenDestination : NavigationDestination {
    override val route = "NumbergameScreen"
    override val titleRes = R.string.number_game_screen_title
    const val NumbergameScreenIdArg = "itemId"
    val routeWithArgs = "$route/{$NumbergameScreenIdArg}"
}

/**
 * Entry route for Home screen
 */
//@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun GameScreen(
    navigateToSatisfiedGridTbl: () -> Unit,
    navigateToSavedGridTbl: () -> Unit,
//    modifier: Modifier = Modifier,
    gameViewModel: NumbergameViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val gameUiState by gameViewModel.uiState.collectAsState()
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
//        modifier = modifier
    ) {
        // グリッド表示
        NumberGridLayout(
            onCellClicked = { rowId, colId -> gameViewModel.onCellClicked(rowId, colId) },
            currentData = gameUiState.currentData,
        )
        // 数字ボタン表示
        NumBtnLayout(
            onNumBtnClicked = { num: Int -> gameViewModel.onNumberBtnClicked(num) },
            currentBtn = gameUiState.currentBtn,
        )
        Spacer(
            modifier = Modifier
                .size(8.dp)
            //.background(color=Color.Red)
        )
        //数字選択時に入力値エラーが発生した場合toastで表示
        if (gameUiState.errBtnMsgID != dupErr.NO_DUP) {
            val context = LocalContext.current
            val msg = when (gameUiState.errBtnMsgID) {
                dupErr.ROW_DUP -> context.getString(R.string.err_btn_row_dup)
                dupErr.COL_DUP -> context.getString(R.string.err_btn_col_dup)
                dupErr.SQ_DUP -> context.getString(R.string.err_btn_sq_dup)
                else -> ""
            }
            val toast = Toast.makeText(context, msg, Toast.LENGTH_LONG)
            //toast.setGravity(Gravity.TOP, 0, 0);
            toast.show()
        }


        // 機能ボタン表示
        FunBtnLayout(
            onNewGameBtnClicked = { gameViewModel.newGame() },
            onResetGameBtnClicked = { gameViewModel.resetGame() },
            onClearGameBtnClicked = { gameViewModel.clearGame() },
            onSearchGameBtnClicked = { gameViewModel.searchAnsCnt() },
        )
        // とりあえず検索結果を表示するレイアウトを入れる
        if (gameUiState.haveSearchResult) {
            SearchResultLayout(
                searchResult = gameUiState.currentSearchResult,
            )
        }

        // スライダーを表示
        SliderLayout(
            defaultPos = gameUiState.fixCellCnt.toFloat(),
            onValueChangeFinished = { num: Int -> gameViewModel.setFixCellCnt(num) },
        )

        //追加機能ボタン表示
        OptBtnLayout(
            onGoSatisfiedGridTbl = { navigateToSatisfiedGridTbl() },
            onGoSavedGridTbl = {navigateToSavedGridTbl()},
            onSavedBtnClicked = {gameViewModel.onSaveBtnClicked() },
        )

        //ゲーム終了確認ダイアログ表示
        if (gameUiState.isGameOver) {
            FinalDialog(
                onNewGameBtnClicked = { gameViewModel.newGame() },
            )
        }
        EndBtnLayout(
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
    modifier: Modifier = Modifier
) {
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
                            .size(8.dp)
                    )
                }
            }
        }
        if (rowIdx % 3 == 2) {
            //平方毎にスペースを開ける
            Spacer(
                modifier = modifier
                    .size(8.dp)
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
) {
    Row(
        verticalAlignment = Alignment.Bottom,
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
            Text(text = "削除")
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
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            verticalAlignment = Alignment.Top,
        ) {
            //新規ボタン
            Button(
                onClick = { onNewGameBtnClicked() }
            ) {
                Text(text = "新規")
            }
            //リセットボタン
            Button(
                onClick = { onResetGameBtnClicked() }
            ) {
                Text(text = "初期化")
            }
            //クリアボタン
            Button(
                onClick = { onClearGameBtnClicked() }
            ) {
                Text(text = "全消去")
            }
            //検索ボタン
            Button(
                onClick = { onSearchGameBtnClicked() }
            ) {
                Text(text = "検索")
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
){
    var sliderPosition by remember { mutableFloatStateOf(defaultPos) }
    Column {
            Slider(
                value = sliderPosition,
                valueRange = 30f..60f,
                onValueChange = { sliderPosition = it },
                onValueChangeFinished ={onValueChangeFinished(sliderPosition.toInt())},
                //steps = 3,

            )
            val sliderTxt: String = "新規作成時　設定：" + sliderPosition.toInt().toString() + "個"
            Text(text = sliderTxt)
    }

}


/*
    検索結果表示欄
*/

@Composable
private fun SearchResultLayout(
    searchResult: Array<Int>,
){
    Row(
        verticalAlignment = Alignment.Top,
        //.background(color= Color.Yellow)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(text = "設定値")
            Text(text = "正解数")
        }
        for (colIdx in 1..9) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
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

@Composable
private fun OptBtnLayout(
    onGoSatisfiedGridTbl: () -> Unit,
    onGoSavedGridTbl: () -> Unit,
    onSavedBtnClicked: () -> Unit,
){
    val checked = remember { mutableStateOf(false) }
    Row(
        verticalAlignment = Alignment.Top,
    ) {
        Checkbox(
            modifier = Modifier
                .size(24.dp),
            checked = checked.value,
            onCheckedChange = { checked.value = it },
        )
        Text("機能表示")
    }
    if(checked.value){
        Row(
            verticalAlignment = Alignment.Top,
        ) {
            //戦歴ボタン
            Button(
                onClick = { onGoSatisfiedGridTbl() }
            ) {
                Text(text = "正解一覧")
            }
            Button(
                onClick = { onSavedBtnClicked() }
            ) {
                Text(text = "一時保存")
            }
            Button(
                onClick = { onGoSavedGridTbl() }
            ) {
                Text(text = "保存一覧")
            }
        }
    }
}

/*
 * 終了ボタン
 */
@Composable
fun EndBtnLayout(
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            verticalAlignment = Alignment.Top,
        ) {
            //終了ボタン
            val activity = (LocalContext.current as Activity)
            Button(
                onClick = { activity.finish() }
            ) {
                Text(text = "終了")
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
) {
    val activity = (LocalContext.current as Activity)
    AlertDialog(
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
                Text(text = stringResource(R.string.exit))
            }
        },
        confirmButton = {
            TextButton(onClick = onNewGameBtnClicked) {
                Text(text = stringResource(R.string.play_again))
            }
        }
    )

}

