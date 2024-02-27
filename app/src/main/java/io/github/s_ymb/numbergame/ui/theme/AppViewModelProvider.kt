package io.github.s_ymb.numbergame.ui.theme

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import io.github.s_ymb.numbergame.NumbergameApplication
import io.github.s_ymb.numbergame.ui.home.NumbergameViewModel
import io.github.s_ymb.numbergame.ui.satisfiedGrid.SatisfiedGridDetailViewModel
import io.github.s_ymb.numbergame.ui.satisfiedGrid.SatisfiedGridTblViewModel
import io.github.s_ymb.numbergame.ui.savedGrid.SavedGridDetailViewModel
import io.github.s_ymb.numbergame.ui.savedGrid.SavedTblViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {
        // Initializer for NumbergemeScreen
        initializer {
            NumbergameViewModel(
                  this.createSavedStateHandle(),
                  numberGameApplication().container            )
        }


        // Initializer for SatisfiedGridEntryViewModel
 //       initializer {
 //           SatisfiedGridEntryViewModel(
 //               numberGameApplication().container.satisfiedGridTblRepository
 //           )
 //       }

        // Initializer for SatisfiedGridDetailsViewModel
        initializer {
            SatisfiedGridDetailViewModel(
                this.createSavedStateHandle(),
                numberGameApplication().container.satisfiedGridTblRepository
            )
        }

        // Initializer for SatisfiedGridTblViewModel
        initializer {
            SatisfiedGridTblViewModel(
                numberGameApplication().container.satisfiedGridTblRepository)
        }

        // Initializer for SavedGridDetailsViewModel
        initializer {
            SavedGridDetailViewModel(
                this.createSavedStateHandle(),
                numberGameApplication().container
            )
        }

        // Initializer for SavedGridTblViewModel
        initializer {
            SavedTblViewModel(
                numberGameApplication().container.savedTblRepository)
        }
    }
}

/**
 * Extension function to queries for  object and returns an instance of
 * .
 */
fun CreationExtras.numberGameApplication(): NumbergameApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as NumbergameApplication)
