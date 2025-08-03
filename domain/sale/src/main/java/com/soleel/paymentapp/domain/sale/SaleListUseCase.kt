package com.soleel.paymentapp.domain.sale

import com.soleel.paymentapp.core.model.enums.PaymentMethodEnum
import com.soleel.paymentapp.core.model.salelist.SaleListItemUiModel
import com.soleel.paymentapp.data.payment.interfaces.IPaymentLocalDataSource
import com.soleel.paymentapp.data.sale.inteface.ISaleLocalDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject


@Module
@InstallIn(SingletonComponent::class)
abstract class SaleListUseCaseModule {

    @Binds
    abstract fun bindSaleListUseCaseModule(
        impl: SaleListUseCase
    ): ISaleListUseCase
}

fun interface ISaleListUseCase {
    operator fun invoke(): Flow<List<SaleListItemUiModel>>
}

class SaleListUseCase @Inject constructor(
    private val saleRepository: ISaleLocalDataSource,
    private val paymentRepository: IPaymentLocalDataSource
) : ISaleListUseCase {
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun invoke(): Flow<List<SaleListItemUiModel>> {
        return saleRepository.getSales().flatMapLatest { sales ->
            val paymentFlows = sales.map { sale ->
                paymentRepository.getPayment(sale.paymentId.toString())
                    .map { payment -> sale to payment }
            }

            combine(paymentFlows) { pairs ->
                pairs.mapNotNull { (sale, payment) ->
                    payment?.let {
                        SaleListItemUiModel(
                            saleId = sale.id,
                            amount = sale.subtotal + (sale.debitCashback ?: 0),
                            createdAt = sale.createdAt,
                            paymentMethod = it.method,
                            sequenceNumber = it.sequenceNumber
                        )
                    }
                }
            }
        }
    }
}

class SaleListUseCaseMock : ISaleListUseCase {
    override fun invoke(): Flow<List<SaleListItemUiModel>> = flowOf(
        listOf(
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
    )
}