package com.s_ymb.numbergame.ui.satisfiedGrid

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.s_ymb.numbergame.data.SatisfiedGridTbl
import com.s_ymb.numbergame.data.SatisfiedGridTblRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn


class SatisfiedGridTblViewModel(satisfiedGridTblRepository: SatisfiedGridTblRepository) : ViewModel()  {

    /**
     * Holds home ui state. The list of items are retrieved from [ItemsRepository] and mapped to
     * [HomeUiState]
     */
    val satisfiedGridTblUiState: StateFlow<SatisfiedGridTblUiState> =
        satisfiedGridTblRepository.getAllGrids().map { SatisfiedGridTblUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = SatisfiedGridTblUiState()
            )

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}

/**
 * Ui State for HomeScreen
 */
data class SatisfiedGridTblUiState(val satisfiedGridTblList: List<SatisfiedGridTbl> = listOf())

