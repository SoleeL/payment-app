package com.soleel.paymentapp.feature.salesprocess.setup.debitchangeselection

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.soleel.paymentapp.core.component.NumberKeyboardButtonUiState
import com.soleel.paymentapp.core.component.SelectionItemType
import com.soleel.paymentapp.core.component.SelectionItemUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
open class DebitChangeSelectionViewModel @Inject constructor() : ViewModel() {


    private var onNavigate: ((Int) -> Unit)? = null

    fun setNavigationCallback(callback: (Int) -> Unit) {
        onNavigate = callback
    }
    private var _debitChangeUiState: List<SelectionItemUiState> by mutableStateOf(
        listOf(
            SelectionItemUiState(
                value = "5000",
                type = SelectionItemType. ,
                isEnabledEvaluator = { true },
                onClick = { println("Pressed 1") }
            ),
            SelectionItemUiState(
                value = "10000",
                type = SelectionItemType.OPTION,
                isEnabledEvaluator = { true },
                onClick = { println("Pressed 2") }
            ),
            SelectionItemUiState(
                value = "15000",
                type = SelectionItemType.OPTION,
                isEnabledEvaluator = { true },
                onClick = { println("Pressed 2") }
            ),
            SelectionItemUiState(
                value = "20000",
                type = SelectionItemType.OPTION,
                isEnabledEvaluator = { true },
                onClick = { println("Pressed 2") }
            ),

            SelectionItemUiState(
                value = "Sin vuelto",
                type = SelectionItemType.ACTION,
                isEnabledEvaluator = { true },
                onClick = { println("Pressed 2") }
            )
        )
    )

    val debitChangeUiState: List<SelectionItemUiState> get() = _debitChangeUiState

}

