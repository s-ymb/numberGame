package io.github.s_ymb.numbergame.ui.savedGrid

import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.s_ymb.numbergame.NumberGameTopAppBar
import io.github.s_ymb.numbergame.R
import io.github.s_ymb.numbergame.data.CellData
import io.github.s_ymb.numbergame.data.NumbergameData
import io.github.s_ymb.numbergame.data.SavedCellTbl
import io.github.s_ymb.numbergame.ui.navigation.NavigationDestination
import io.github.s_ymb.numbergame.ui.theme.AppViewModelProvider
import kotlinx.coroutines.launch

object SavedDetailDestination : NavigationDestination {
    override val route = "SavedGridGroup/savedGrid_details"
    override val titleRes = R.string.savedGrid_detail_title
    const val savedIdArg = "itemId"
    val routeWithArgs = "$route/{$savedIdArg}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedGridDetailScreen(
    navigateBack: () -> Unit,
    navigateToNumbergameScreen: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SavedGridDetailViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val savedTblUiState = viewModel.savedTblUiState.collectAsState()
    val savedCellTblUiState = viewModel.savedCellTblUiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    Scaffold(
        topBar = {
            NumberGameTopAppBar(
                title = stringResource(SavedDetailDestination.titleRes),
                canNavigateBack = true,
                navigateUp = navigateBack
            )
            /*        }, floatingActionButton = {
                        FloatingActionButton(
                            onClick = { navigateToEditItem(uiState.value.itemDetails.id) },
                            shape = MaterialTheme.shapes.medium,
                            modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_large))

                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = stringResource(R.string.edit_item_title),
                            )
                        }*/
        }, modifier = modifier
    ) { innerPadding ->
        SavedGridDetailsBody(
            savedTblUiState = savedTblUiState.value,
            savedCellTblUiState = savedCellTblUiState.value,
            onDelete = {
                // Note: If the user rotates the screen very fast, the operation may get cancelled
                // and the item may not be deleted from the Database. This is because when config
                // change occurs, the Activity will be recreated and the rememberCoroutineScope will
                // be cancelled - since the scope is bound to composition.
                coroutineScope.launch {
                    viewModel.deleteItem()
                    navigateBack()
                }
            },
            onResume = {navigateToNumbergameScreen(savedTblUiState.value.savedTbl.id)},
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        )
    }
}

@Composable
private fun SavedGridDetailsBody(
    savedTblUiState: SavedTblUiState,
    savedCellTblUiState: SavedCellTblUiState,
    onDelete: () -> Unit,
    onResume: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(dimensionResource(id = R.dimen.padding_medium)),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_small))
    ) {
        var deleteConfirmationRequired by rememberSaveable { mutableStateOf(false) }
        var resumeConfirmationRequired by rememberSaveable { mutableStateOf(false) }
        SavedGridDetail(
            savedTblUiState = savedTblUiState,
            savedCellTblList = savedCellTblUiState.savedCellTblList,
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedButton(
            onClick = { deleteConfirmationRequired = true },
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.delete))
        }
        if (deleteConfirmationRequired) {
            DeleteConfirmationDialog(
                onDeleteConfirm = {
                    deleteConfirmationRequired = false
                    onDelete()
                },
                onDeleteCancel = { deleteConfirmationRequired = false },
                modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_medium))
            )
        }
        OutlinedButton(
            onClick = { resumeConfirmationRequired = true },
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.resume))
        }
        if (resumeConfirmationRequired) {
            ResumeConfirmationDialog(
                onResumeConfirm = {
                    resumeConfirmationRequired = false
                    onResume()
                },
                onResumeCancel = { resumeConfirmationRequired = false },
                modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_medium))
            )
        }
    }
}


@Composable
fun SavedGridDetail(
    savedTblUiState: SavedTblUiState,
    savedCellTblList: List<SavedCellTbl>,
    modifier: Modifier = Modifier
) {
//    Card(
//        modifier = modifier, colors = CardDefaults.cardColors(
//            containerColor = MaterialTheme.colorScheme.primaryContainer,
//            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
//        )
//    )

    Column(
        modifier = modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
//            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_small))
    ) {
        SavedGridDetailsRow(
            labelResID = R.string.create_user_name,
            itemDetail = savedTblUiState.savedTbl.createUser,
            modifier = Modifier.padding(
                horizontal = dimensionResource(
                    id = R.dimen
                        .padding_medium
                )
            )
        )
        SavedGridDetailsRow(
            labelResID = R.string.create_dt,
            itemDetail = savedTblUiState.savedTbl.createDt,
            modifier = Modifier.padding(
                horizontal = dimensionResource(
                    id = R.dimen
                        .padding_medium
                )
            )
        )
        //グリッドを表示
        NumberGridRow(
            dataList = savedCellTblList,
//                modifier = Modifier.padding(
//                    horizontal = dimensionResource(
//                        id = R.dimen
//                            .padding_medium
//                    )
//                )
        )
    }
}

@Composable
private fun SavedGridDetailsRow(
    @StringRes labelResID: Int, itemDetail: String, modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        Text(text = stringResource(labelResID))
        Spacer(modifier = Modifier.weight(1f))
        Text(text = itemDetail, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun NumberGridRow(
    dataList: List<SavedCellTbl>,
//    modifier: Modifier = Modifier,
) {
    // 空のアレイを作成し、引数のリストに含まれるデータで更新する
    val data : Array<Array<CellData>> =
    Array(NumbergameData.NUM_OF_ROW) { Array(NumbergameData.NUM_OF_COL) { CellData(0, false) } }

    dataList.forEach{
        data[it.row][it.col].num = it.num
        data[it.row][it.col].init = it.init
    }

    Row(
        verticalAlignment = Alignment.Top,
//        modifier = modifier
        //.background(color= Color.Yellow)
    ) {
        Column(
//            modifier = Modifier,
//                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
//            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_small))
        ) {
            for ((rowIdx: Int, rowData: Array<CellData>) in data.withIndex()) {
                Row ()
//                    modifier = Modifier.padding(0.dp)
                {
                    for ((colIdx: Int) in rowData.withIndex()) {
                        val numStr: String = if (rowData[colIdx].num != NumbergameData.NUM_NOT_SET)
                        {
                            rowData[colIdx].num.toString()
                        }else{
                            ""
                        }
                        var bgColor: Color = colorResource(R.color.cell_bg_color_default)
                        if (rowData[colIdx].init) {
                            //初期設定されたセルの場合は背景色をグレーに
                            bgColor = colorResource(R.color.cell_bg_color_init)
                        }
                        Text(
                            text = numStr,
                            textAlign = TextAlign.Center,
                            fontSize = 24.sp,
                            modifier = Modifier
                                .width(32.dp)
                                .padding(0.dp)
                                .border(
                                    border = BorderStroke(
                                                    color = Color.Black,
                                                    width = 1.dp,
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
                                modifier = Modifier
                                    .size(8.dp)
                            )
                        }
                    }
                }
                if (rowIdx % 3 == 2) {
                    //平方毎にスペースを開ける
                    Spacer(
                        modifier = Modifier
                            .size(8.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun DeleteConfirmationDialog(
    onDeleteConfirm: () -> Unit, onDeleteCancel: () -> Unit, modifier: Modifier = Modifier
) {
    AlertDialog(onDismissRequest = { /* Do nothing */ },
        title = { Text(stringResource(R.string.attention)) },
        text = { Text(stringResource(R.string.delete_question)) },
        modifier = modifier,
        dismissButton = {
            TextButton(onClick = onDeleteCancel) {
                Text(text = stringResource(R.string.no))
            }
        },
        confirmButton = {
            TextButton(onClick = onDeleteConfirm) {
                Text(text = stringResource(R.string.yes))
            }
        }
    )
}

@Composable
private fun ResumeConfirmationDialog(
    onResumeConfirm: () -> Unit, onResumeCancel: () -> Unit, modifier: Modifier = Modifier
) {
    AlertDialog(onDismissRequest = { /* Do nothing */ },
        title = { Text(stringResource(R.string.attention)) },
        text = { Text(stringResource(R.string.resume_question)) },
        modifier = modifier,
        dismissButton = {
            TextButton(onClick = onResumeCancel) {
                Text(text = stringResource(R.string.no))
            }
        },
        confirmButton = {
            TextButton(onClick = onResumeConfirm) {
                Text(text = stringResource(R.string.yes))
            }
        }
    )
}
