package com.soleel.paymentapp.feature.home.sales

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.soleel.paymentapp.core.common.result.Result
import com.soleel.paymentapp.core.common.result.asResult
import com.soleel.paymentapp.core.model.salelist.SaleListItemUiModel
import com.soleel.paymentapp.domain.sale.ISaleListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject


sealed interface SaleListUiState {
    data object Loading : SaleListUiState
    data class Success(val sales: List<SaleListItemUiModel>) : SaleListUiState
    data object Error : SaleListUiState
}

@HiltViewModel
class SaleListViewModel @Inject constructor(
    private val saleListUseCase: ISaleListUseCase
) : ViewModel() {

    private val _sales: Flow<SaleListUiState> = saleListUseCase()
            .asResult()
            .map(transform = this::getData)

    private fun getData(saleListResult: Result<List<SaleListItemUiModel>>): SaleListUiState {
        return when (saleListResult) {
            Result.Loading -> SaleListUiState.Loading
            is Result.Success<List<SaleListItemUiModel>> -> SaleListUiState.Success(saleListResult.data)
            is Result.Error -> SaleListUiState.Error
        }
    }

    val sales: StateFlow<SaleListUiState> = _sales
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
            initialValue = SaleListUiState.Loading
        )
}