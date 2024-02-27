package io.github.s_ymb.numbergame.ui.satisfiedGrid

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.s_ymb.numbergame.data.SatisfiedGridTbl
import io.github.s_ymb.numbergame.data.SatisfiedGridTblRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn


class SatisfiedGridTblViewModel(satisfiedGridTblRepository: SatisfiedGridTblRepository) : ViewModel()  {

    /**
     * Holds home ui state. The list of items are retrieved from [SatisfiedGridTblRepository] and mapped to
     * [SatisfiedGridTblUiState]
     */
    private val satisfiedGridTblRepo = satisfiedGridTblRepository
    val satisfiedGridTblUiState: StateFlow<SatisfiedGridTblUiState> =
        satisfiedGridTblRepo.getAllGrids().map { SatisfiedGridTblUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = SatisfiedGridTblUiState()
            )

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

/*
    suspend fun onCreateNew(){
        val satisfied: SatisfiedGridData = SatisfiedGridData()
        // 新規の正解パターンを作成する
        var retList: MutableList<Array<Array<Int>>> = satisfied.createNew()


        val satisfiedGridTbl: SatisfiedGridTbl = SatisfiedGridTbl()
       // satisfiedGridTblRepository に登録
        satisfiedGridTblRepo.insert(satisfiedGridTbl)
    }

 */
}

/**
 * Ui State for HomeScreen
 */
data class SatisfiedGridTblUiState(val satisfiedGridTblList: List<SatisfiedGridTbl> = listOf())

