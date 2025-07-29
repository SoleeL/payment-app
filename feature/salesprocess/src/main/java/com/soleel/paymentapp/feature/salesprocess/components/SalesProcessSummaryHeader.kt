package com.soleel.paymentapp.feature.salesprocess.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.soleel.paymentapp.core.model.PaymentMethodEnum
import com.soleel.paymentapp.core.ui.utils.LongDevicePreview
import com.soleel.paymentapp.core.ui.utils.WithFakeSystemBars
import com.soleel.paymentapp.core.ui.utils.WithFakeTopAppBar
import com.soleel.paymentapp.core.ui.visualtransformation.CLPCurrencyVisualTransformation


@LongDevicePreview
@Composable
private fun SalesProcessSummaryLongPreview() {

    WithFakeSystemBars(
        content = {
            WithFakeTopAppBar(
                content = {
                    SalesProcessSummaryHeader(
                        calculatorTotal = 2000f,
                        tipTotal = 200f,
                        paymentMethodSelected = PaymentMethodEnum.CREDIT,
//                        cashChangeSelected = 10000f,
                        creditInstalmentsSelected = 0,
//                        debitChangeSelected = 5000f
                    )
                }
            )
        }
    )
}

@Composable
fun SalesProcessSummaryHeader(
    calculatorTotal: Float,
    tipTotal: Float? = null,
    paymentMethodSelected: PaymentMethodEnum? = null,
    cashChangeSelected: Float? = null,
    creditInstalmentsSelected: Int? = null,
    debitChangeSelected: Float? = null
) {
    val calculatorTotalAmountCLP: String = CLPCurrencyVisualTransformation()
        .filter(AnnotatedString(text = calculatorTotal.toInt().toString()))
        .text.toString()

    var salesProcessTotal: Float = calculatorTotal

    var tipTotalAmountCLP: String = ""
    if (tipTotal != null) {
        salesProcessTotal += tipTotal
        tipTotalAmountCLP = CLPCurrencyVisualTransformation()
            .filter(AnnotatedString(text = tipTotal.toInt().toString()))
            .text.toString()
    }

    var debitChangeSelectedAmountCLP: String = ""
    if (debitChangeSelected != null) {
        salesProcessTotal += debitChangeSelected
        debitChangeSelectedAmountCLP = CLPCurrencyVisualTransformation()
            .filter(AnnotatedString(text = debitChangeSelected.toInt().toString()))
            .text.toString()
    }

    val salesProcessTotalAmountCLP: String = CLPCurrencyVisualTransformation()
        .filter(AnnotatedString(text = salesProcessTotal.toInt().toString()))
        .text.toString()

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        content = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                content = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        content = {
                            Text(
                                text = "Total a pagar"
                            )

                            Text(
                                text = buildAnnotatedString(
                                    builder = {
                                        withStyle(
                                            style = SpanStyle(fontWeight = FontWeight.Bold),
                                            block = {
                                                append(salesProcessTotalAmountCLP)
                                            }
                                        )
                                    }
                                ),
                                style = MaterialTheme.typography.headlineLarge,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    )
                }
            )

            if (tipTotalAmountCLP.isNotBlank() || debitChangeSelectedAmountCLP.isNotBlank()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    content = {
                        Text(
                            text = buildAnnotatedString(
                                builder = {
                                    append("Calculadora: " )
                                    withStyle(
                                        style = SpanStyle(fontWeight = FontWeight.Bold),
                                        block = {
                                            append(calculatorTotalAmountCLP)
                                        }
                                    )
                                }
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                )
            }

            if (tipTotalAmountCLP.isNotBlank()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    content = {
                        Text(
                            text = buildAnnotatedString(
                                builder = {
                                    append("Propina: " )
                                    withStyle(
                                        style = SpanStyle(fontWeight = FontWeight.Bold),
                                        block = {
                                            append(tipTotalAmountCLP)
                                        }
                                    )
                                }
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                )
            }

            if (paymentMethodSelected != null) {
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    content = {
                        Text(
                            text = buildAnnotatedString(
                                builder = {
                                    append("Metodo de pago: " )
                                    withStyle(
                                        style = SpanStyle(fontWeight = FontWeight.Bold),
                                        block = {
                                            append(paymentMethodSelected.value)
                                        }
                                    )
                                }
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                )
            }

            if (debitChangeSelectedAmountCLP.isNotBlank()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    content = {
                        Text(
                            text = buildAnnotatedString(
                                builder = {
                                    append("Vuelto: " )
                                    withStyle(
                                        style = SpanStyle(fontWeight = FontWeight.Bold),
                                        block = {
                                            append(debitChangeSelectedAmountCLP)
                                        }
                                    )
                                }
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                )
            }

            if (cashChangeSelected != null) {
                val cashChangeSelectedAmountCLP: String = CLPCurrencyVisualTransformation()
                    .filter(AnnotatedString(text = cashChangeSelected.toInt().toString()))
                    .text.toString()

                val cashGiven: Float = cashChangeSelected - salesProcessTotal

                val cashGivenAmountCLP: String = CLPCurrencyVisualTransformation()
                    .filter(AnnotatedString(text = cashGiven.toInt().toString()))
                    .text.toString()

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    content = {
                        Text(
                            text = buildAnnotatedString {
                                append("Cliente paga con: ")
                                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                                    append(cashChangeSelectedAmountCLP)
                                }
                            },
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    content = {
                        Text(
                            text = buildAnnotatedString {
                                append("Vuelto a entregar: ")
                                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                                    append(cashGivenAmountCLP)
                                }
                            },
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                )
            }

            if (creditInstalmentsSelected != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    content = {
                        Text(
                            text = buildAnnotatedString(
                                builder = {
                                    append("Cuotas: ")
                                    withStyle(
                                        style = SpanStyle(fontWeight = FontWeight.Bold),
                                        block = {
                                            if (creditInstalmentsSelected > 0) {
                                                append(creditInstalmentsSelected.toString())
                                            } else {
                                                append("sin cuotas")
                                            }
                                        }
                                    )
                                }
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                )
            }
        }
    )
}
