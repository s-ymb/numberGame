package com.s_ymb.numbergame.ui.savedGrid


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
import com.s_ymb.numbergame.data.SavedTbl
import com.s_ymb.numbergame.ui.navigation.NavigationDestination
import com.s_ymb.numbergame.ui.theme.AppViewModelProvider
import com.s_ymb.numbergame.ui.theme.NumbergameTheme


object SavedTblDestination : NavigationDestination {
    override val route = "SavedGridGroup/saved_Tbl"
    override val titleRes = R.string.savedGrid_tbl_title
}

/**
 * Entry route for Home screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SavedTblScreen(
    navigateBack: () -> Unit,
    navigateToSavedGridDetail: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SavedTblViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val savedTblUiState by viewModel.savedTblUiState.collectAsState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            NumberGameTopAppBar(
                title = stringResource(SavedTblDestination.titleRes),
                canNavigateBack = true,
                navigateUp = navigateBack,
                scrollBehavior = scrollBehavior
            )
        },
        ) { innerPadding ->
        SavedTblBody(
            savedTblList = savedTblUiState.savedTblList,
            onItemClick = navigateToSavedGridDetail,
            modifier = modifier
                .padding(innerPadding)
                .fillMaxSize()
        )
    }
}

@Composable
private fun SavedTblBody(
    savedTblList: List<SavedTbl>, onItemClick: (Int) -> Unit, modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        if (savedTblList.isEmpty()) {
            Text(
                text = stringResource(R.string.no_savedGrid_description),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge
            )
        } else {
            SavedTblList(
                savedTblList = savedTblList,
                onItemClick = { onItemClick(it.id) },
                modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.padding_small))
            )
        }
    }
}

@Composable
private fun SavedTblList(
    savedTblList: List<SavedTbl>, onItemClick: (SavedTbl) -> Unit, modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier) {
        items(items = savedTblList, key = { it.id }) { item ->
            SavedItem(item = item,
                modifier = Modifier
                    .padding(dimensionResource(id = R.dimen.padding_small))
                    .clickable { onItemClick(item) })
        }
    }
}

@Composable
private fun SavedItem(
    item: SavedTbl, modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier, elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_large)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_small))
        ) {
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

//@Preview(showBackground = true)
//@Composable
//fun SatisfieGridItemPreview() {
//    NumberGameTheme {
//        SavedGridItem(
//        )
//    }
//}
