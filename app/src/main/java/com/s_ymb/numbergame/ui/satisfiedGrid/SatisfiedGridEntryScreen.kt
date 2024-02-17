package com.s_ymb.numbergame.ui.satisfiedGrid

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.s_ymb.numbergame.R
import com.s_ymb.numbergame.ui.navigation.NavigationDestination
import com.s_ymb.numbergame.ui.theme.AppViewModelProvider

object SatisfiedGridEntryDestination : NavigationDestination {
    override val route = "SatisfiedGridGroup/satisfiedGrid_entry"
    override val titleRes = R.string.satisfiedGrid_entry_title
}

@Composable
fun SatisfiedGridEntryScreen(
    @OptIn(ExperimentalMaterial3Api::class)
        navigateBack: () -> Unit,
        modifier: Modifier = Modifier,
        viewModel: SatisfiedGridEntryViewModel = viewModel(factory = AppViewModelProvider.Factory)
    ) {

}