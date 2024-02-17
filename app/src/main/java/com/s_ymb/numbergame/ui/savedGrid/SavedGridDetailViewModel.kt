package com.s_ymb.numbergame.ui.savedGrid

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.s_ymb.numbergame.data.SatisfiedGridTbl
import com.s_ymb.numbergame.data.SatisfiedGridTblRepository
import com.s_ymb.numbergame.data.SavedGridTbl
import com.s_ymb.numbergame.data.SavedGridTblRepository
import com.s_ymb.numbergame.ui.satisfiedGrid.SatisfiedGridDetailDestination
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SavedGridDetailViewModel (
    savedStateHandle: SavedStateHandle,
    private val savedGridRepository: SavedGridTblRepository,
    ) : ViewModel() {
        private val id: Int = checkNotNull(savedStateHandle[SavedGridDetailDestination.savedGridIdArg])

        val uiState: StateFlow<SavedGridDetailUiState> =
            savedGridRepository.getGrid(id)
                .filterNotNull()
                .map {
                    SavedGridDetailUiState(savedGridTbl = it)
                }.stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                    initialValue = SavedGridDetailUiState()
                )
    /**
     * Deletes the item from the [ItemsRepository]'s data source.
     */
    suspend fun deleteItem() {
        viewModelScope.launch(Dispatchers.IO) {
            savedGridRepository.delete(uiState.value.savedGridTbl.id)
        }
    }


    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}


data class SavedGridDetailUiState(
    val savedGridTbl: SavedGridTbl = SavedGridTbl()
)
