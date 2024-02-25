package com.s_ymb.numbergame.ui.satisfiedGrid

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.s_ymb.numbergame.data.SatisfiedGridTbl
import com.s_ymb.numbergame.data.SatisfiedGridTblRepository
import com.s_ymb.numbergame.ui.satisfiedGrid.SatisfiedGridDetailDestination.satisfiedGridIdArg
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SatisfiedGridDetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val satisfiedGridRepository: SatisfiedGridTblRepository,
    ) : ViewModel() {
    private val gridData: String = checkNotNull(savedStateHandle[satisfiedGridIdArg])

    val uiState: StateFlow<SatisfiedGridDetailUiState> =
        satisfiedGridRepository.getGrid(gridData)
            .filterNotNull()
            .map {
                SatisfiedGridDetailUiState(satisfiedGridTbl = it)
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = SatisfiedGridDetailUiState()
            )
    /**
     * Deletes the item from the [satisfiedGridRepository]'s data source.
     */
    fun deleteItem() {
        viewModelScope.launch(Dispatchers.IO) {
            satisfiedGridRepository.delete(uiState.value.satisfiedGridTbl.gridData)
        }
    }


    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

}

data class SatisfiedGridDetailUiState(
    val satisfiedGridTbl: SatisfiedGridTbl = SatisfiedGridTbl()
)

