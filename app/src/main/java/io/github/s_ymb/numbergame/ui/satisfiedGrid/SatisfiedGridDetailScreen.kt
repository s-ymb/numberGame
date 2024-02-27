package io.github.s_ymb.numbergame.ui.satisfiedGrid

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
import io.github.s_ymb.numbergame.data.NumbergameData
import io.github.s_ymb.numbergame.data.SatisfiedGrid
import io.github.s_ymb.numbergame.data.toSatisfiedGrid
import io.github.s_ymb.numbergame.ui.navigation.NavigationDestination
import io.github.s_ymb.numbergame.ui.theme.AppViewModelProvider
import kotlinx.coroutines.launch

object SatisfiedGridDetailDestination : NavigationDestination {
    override val route = "SatisfiedGridGroup/satisfiedGrid_details"
    override val titleRes = R.string.satisfiedGrid_detail_title
    const val satisfiedGridIdArg = "itemId"
    val routeWithArgs = "$route/{$satisfiedGridIdArg}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SatisfiedGridDetailScreen(
//    navigateToEditItem: (Int) -> Unit,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SatisfiedGridDetailViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState = viewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    Scaffold(
        topBar = {
            NumberGameTopAppBar(
                title = stringResource(SatisfiedGridDetailDestination.titleRes),
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
        SatisfiedGridDetailsBody(
            satisfiedGridDetailsUiState = uiState.value,
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
            modifier = Modifier
                .padding(innerPadding)
//                .verticalScroll(rememberScrollState())
        )
    }
}

@Composable
private fun SatisfiedGridDetailsBody(
    satisfiedGridDetailsUiState: SatisfiedGridDetailUiState,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(dimensionResource(id = R.dimen.padding_medium)),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_small))
    ) {
        var deleteConfirmationRequired by rememberSaveable { mutableStateOf(false) }
        SatisfiedGridDetail(
            satisfiedGrid = satisfiedGridDetailsUiState.satisfiedGridTbl.toSatisfiedGrid(),
            //modifier = Modifier.fillMaxWidth()
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
    }
}


@Composable
fun SatisfiedGridDetail(
    satisfiedGrid: SatisfiedGrid,
//    modifier: Modifier = Modifier
) /*{
    Card(
        modifier = modifier, colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    ) */  {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
//            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_small))
        ) {
            SatisfiedGridDetailsRow(
                labelResID = R.string.satisfiedGrid_create_user_name,
                itemDetail = satisfiedGrid.createUser,
                modifier = Modifier.padding(
                    horizontal = dimensionResource(
                        id = R.dimen
                            .padding_medium
                    )
                )
            )
            SatisfiedGridDetailsRow(
                labelResID = R.string.satisfiedGrid_create_dt,
                itemDetail = satisfiedGrid.createDt,
                modifier = Modifier.padding(
                    horizontal = dimensionResource(
                        id = R.dimen
                            .padding_medium
                    )
                )
            )
            //グリッドを表示
            NumberGridRow(
                data = satisfiedGrid.data,
//                modifier = Modifier.padding(
//                    horizontal = dimensionResource(
//                        id = R.dimen
//                            .padding_medium
//                    )
//                )
            )
        }
    }
// }

@Composable
private fun SatisfiedGridDetailsRow(
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
    data: Array<IntArray>,
//    modifier: Modifier  = Modifier
) {
    Row(
        verticalAlignment = Alignment.Top,
        modifier = Modifier
    ) {
        Column(
            modifier = Modifier,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            for ((rowIdx,rowData: IntArray) in data.withIndex()) {
                Row()
                {
                    for ((colIdx: Int) in rowData.withIndex()) {
                        val numStr: String = if (rowData[colIdx] != NumbergameData.NUM_NOT_SET)
                                            {
                                                rowData[colIdx].toString()
                                            }else{""}

                        /*
                                                var numStr: String = ""
                                                if (rowData[colIdx] != NumbergameData.NUM_NOT_SET) {
                                                    numStr = rowData[colIdx].toString()
                                                }
                        */
                        Text(
                            text = numStr,
                            textAlign = TextAlign.Center,
                            fontSize = 24.sp,
                            modifier = Modifier
                                .width(32.dp)
                                .padding(0.dp)
                                .background(color = colorResource(R.color.cell_bg_color_init))
                                .border(
                                    border = BorderStroke(
                                                color = Color.Black,
                                                width = 1.dp,
                                            ),
                                    shape = RectangleShape,
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
    onDeleteConfirm: () -> Unit,
    onDeleteCancel: () -> Unit,
    modifier: Modifier = Modifier
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

