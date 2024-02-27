package io.github.s_ymb.numbergame.ui.savedGrid

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.s_ymb.numbergame.data.SavedTbl
import io.github.s_ymb.numbergame.data.SavedTblRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class SavedTblViewModel(savedTblRepository: SavedTblRepository) : ViewModel()  {
    val savedTblUiState: StateFlow<SavedTblListUiState> =
            savedTblRepository.getAllGrids().map { SavedTblListUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = SavedTblListUiState()
            )

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}

/**
 * Ui State for SatisfiedGridTblScreen
 */
data class SavedTblListUiState(val savedTblList: List<SavedTbl> = listOf())



