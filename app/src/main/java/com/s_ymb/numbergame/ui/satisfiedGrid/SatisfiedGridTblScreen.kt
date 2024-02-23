package com.s_ymb.numbergame.ui.satisfiedGrid

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.s_ymb.numbergame.NumberGameTopAppBar
import com.s_ymb.numbergame.R
import com.s_ymb.numbergame.data.SatisfiedGridTbl
import com.s_ymb.numbergame.ui.navigation.NavigationDestination
import com.s_ymb.numbergame.ui.theme.AppViewModelProvider
import com.s_ymb.numbergame.ui.theme.NumbergameTheme


object SatisfiedGridTblDestination : NavigationDestination {
    override val route = "SatisfiedGridGroup/satisfiedGrid_Tbl"
    override val titleRes = R.string.satisfiedGrid_tbl_title
}

/**
 * Entry route for Home screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SatisfiedGridTblScreen(
    navigateBack: () -> Unit,
    navigateToSatisfiedGridEntry: () -> Unit,
    navigateToSatisfiedGridDetail: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SatisfiedGridTblViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val satisfiedGridTblUiState by viewModel.satisfiedGridTblUiState.collectAsState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            NumberGameTopAppBar(
                title = stringResource(SatisfiedGridTblDestination.titleRes),
                canNavigateBack = true,
                navigateUp = navigateBack,
                scrollBehavior = scrollBehavior
            )
        },
/*
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    coroutineScope.launch(Dispatchers.IO) {
                        viewModel.onCreateNew()
                    }
                },
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_large))
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.satisfiedGrid_entry_title)
                )
            }
        },

 */
    ) { innerPadding ->
        SatisfiedGridTblBody(
            satisfiedGridTblList = satisfiedGridTblUiState.satisfiedGridTblList,
            onItemClick = navigateToSatisfiedGridDetail,
            modifier = modifier
                .padding(innerPadding)
                .fillMaxSize()
        )
    }
}

@Composable
private fun SatisfiedGridTblBody(
    satisfiedGridTblList: List<SatisfiedGridTbl>,
    onItemClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        if (satisfiedGridTblList.isEmpty()) {
            Text(
                text = stringResource(R.string.no_satisfiedGrid_description),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge
            )
        } else {
            SatisfiedGridTblList(
                satisfiedGridTblList = satisfiedGridTblList,
                onItemClick = { onItemClick(it.gridData) },
                modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.padding_small))
            )
        }
    }
}

@Composable
private fun SatisfiedGridTblList(
    satisfiedGridTblList: List<SatisfiedGridTbl>, onItemClick: (SatisfiedGridTbl) -> Unit, modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier) {
        items(items = satisfiedGridTblList, key = { it.gridData }) { item ->
            SatisfiedGridItem(item = item,
                modifier = Modifier
                    .padding(dimensionResource(id = R.dimen.padding_small))
                    .clickable { onItemClick(item) })
        }
    }
}

@Composable
private fun SatisfiedGridItem(
    item: SatisfiedGridTbl, modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier, elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_large)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_small))
        ) {
            Text(
                text = item.gridData.take(9),
                style = MaterialTheme.typography.titleLarge,
            )
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = item.createUser,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(Modifier.weight(1f))
                Text(
                    text = item.createDt,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}
/*
@Preview(showBackground = true)
@Composable
fun HomeBodyPreview() {
    NumberGameTheme {
        HomeBody(listOf(
            Item(1, "Game", 100.0, 20), Item(2, "Pen", 200.0, 30), Item(3, "TV", 300.0, 50)
        ), onItemClick = {})
    }
}
*/

@Preview(showBackground = true)
@Composable
fun HomeBodyEmptyListPreview() {
    NumbergameTheme {
//        HomeBody(listOf(), onItemClick = {})
    }
}

@Preview(showBackground = true)
@Composable
fun SatisfieGridItemPreview() {
    NumbergameTheme {
        SatisfiedGridItem(
            SatisfiedGridTbl("000000000111111111", "2024/01/01", "User"),
        )
    }
}
