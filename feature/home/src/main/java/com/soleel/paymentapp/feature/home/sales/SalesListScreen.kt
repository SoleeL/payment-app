package com.soleel.paymentapp.feature.home.sales

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.soleel.paymentapp.core.model.enums.PaymentMethodEnum
import com.soleel.paymentapp.core.model.salelist.SaleListItemUiModel
import com.soleel.paymentapp.core.ui.formatter.FullReadableDateFormat
import com.soleel.paymentapp.core.ui.formatter.FullReadableDateWithHourFormat
import com.soleel.paymentapp.core.ui.utils.LongDevicePreview
import com.soleel.paymentapp.core.ui.utils.WithFakeSystemBars
import com.soleel.paymentapp.core.ui.utils.WithFakeTopAppBar
import com.soleel.paymentapp.core.ui.visualtransformation.CLPCurrencyVisualTransformation
import java.time.LocalDateTime
import java.util.UUID

@Composable
fun SalesListScreen(
    saleListViewModel: SaleListViewModel = hiltViewModel()
) {
    val saleListUiState: SaleListUiState by saleListViewModel.sales.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Historial de Ventas",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .padding(16.dp)
        )

        SaleListColumn(saleListUiState = saleListUiState)
    }
}

@Composable
fun SaleListColumn(saleListUiState: SaleListUiState) {
    when (saleListUiState) {
        is SaleListUiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        is SaleListUiState.Success -> {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                content = {
                    items(
                        items = saleListUiState.sales,
                        itemContent = { sale ->
                            SaleListItem(sale)
                        }
                    )
                }
            )
        }

        is SaleListUiState.Error -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Error al cargar las ventas")
            }
        }
    }
}

@LongDevicePreview
@Composable
private fun SalesListLazyColumnLongPreview() {
    val sales = listOf(
        SaleListItemUiModel(
            saleId = UUID.randomUUID(),
            paymentMethod = PaymentMethodEnum.CASH,
            amount = 1200,
            sequenceNumber = "ergwerfg",
            createdAt = LocalDateTime.now()
        ),
        SaleListItemUiModel(
            saleId = UUID.fromString("123e4567-e89b-12d3-a456-426614174001"),
            paymentMethod = PaymentMethodEnum.CREDIT,
            amount = 5800,
            sequenceNumber = "ergwerfg",
            createdAt = LocalDateTime.now()
        )
    )


    WithFakeSystemBars(
        content = {
            WithFakeTopAppBar(
                content = {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        content = {
                            items(
                                items = sales,
                                itemContent = { sale ->
                                    SaleListItem(sale)
                                }
                            )
                        }
                    )

                }
            )
        }
    )
}


@Composable
fun SaleListItem(
    sale: SaleListItemUiModel
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .height(IntrinsicSize.Min)
        ) {
            Column(

                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                Icon(
                    painter = painterResource(id = sale.paymentMethod.icon),
                    contentDescription = sale.paymentMethod.value,
                    modifier = Modifier.size(32.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = sale.paymentMethod.value,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    Text(
                        text = "ID: ${sale.saleId.toString().takeLast(6)}",
                        style = MaterialTheme.typography.titleMedium // Cambiado
                    )

                    val saleAmountFormated: String = CLPCurrencyVisualTransformation()
                        .filter(AnnotatedString(text = sale.amount.toInt().toString()))
                        .text.toString()

                    Text(
                        text = saleAmountFormated,
                        style = MaterialTheme.typography.titleMedium // Cambiado
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Spacer(Modifier.weight(1f))
                    Text(
                        text = FullReadableDateWithHourFormat(sale.createdAt),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}