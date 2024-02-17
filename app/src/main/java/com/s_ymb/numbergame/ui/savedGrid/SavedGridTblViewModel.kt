package com.s_ymb.numbergame.ui.savedGrid

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.s_ymb.numbergame.data.SavedGridTbl
import com.s_ymb.numbergame.data.SavedGridTblRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class SavedGridTblViewModel(savedGridTblRepository: SavedGridTblRepository) : ViewModel()  {

    /**
     * Holds home ui state. The list of items are retrieved from [ItemsRepository] and mapped to
     * [HomeUiState]
     */
    val savedGridTblUiState: StateFlow<SavedGridTblUiState> =
        savedGridTblRepository.getAllGrids().map { SavedGridTblUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = SavedGridTblUiState()
            )

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}

/**
 * Ui State for HomeScreen
 */
data class SavedGridTblUiState(val savedGridTblList: List<SavedGridTbl> = listOf())


