package com.soleel.paymentapp.feature.salesprocess.utils

class ValidateIntenSaleData {
}


    private fun handleIntentIfNeeded(intent: Intent) {
        if (intent.action == "com.soleel.paymentapp.PROCESS_INTENT_TO_SALE") {
            val rawJson = intent.getStringExtra("payload") ?: run {
                sendIntentResult(
                    IntentSaleResultInternal(
                        status = IntentSaleStatusEnum.ERROR,
                        message = "Falta el campo payload",
                        errorCode = "ERR_MISSING_PAYLOAD"
                    )
                )
                return
            }

            val json: Json = Json(builderAction = { ignoreUnknownKeys = true })

            val request = try {
                json.decodeFromString<IntentSaleRequestExternal>(rawJson)
            } catch (e: Exception) {
                sendIntentResult(
                    IntentSaleResultInternal(
                        status = IntentSaleStatusEnum.ERROR,
                        message = "JSON inválido",
                        errorCode = "ERR_INVALID_JSON"
                    )
                )
                return
            }

            if (request.commerceId.isBlank()) {
                sendIntentResult(
                    IntentSaleResultInternal(
                        status = IntentSaleStatusEnum.ERROR,
                        message = "Parametro 'id de comercio' invalido",
                        errorCode = "ERR_INVALID_PARAMS"
                    )
                )
                return
            }

            if (request.totalAmount <= 0 || request.totalAmount > 9999999) {
                sendIntentResult(
                    IntentSaleResultInternal(
                        status = IntentSaleStatusEnum.ERROR,
                        message = "Parametro 'Total a pagar' invalido",
                        errorCode = "ERR_INVALID_PARAMS"
                    )
                )
                return
            }

            when (request.paymentMethod) {
                -1 -> {
                    if (request.cashChange != -1) {
                        sendIntentResult(
                            IntentSaleResultInternal(
                                status = IntentSaleStatusEnum.ERROR,
                                message = "Parametro 'Vuelto para efectivo' invalido cuando esta habilitada la seleccion manual del 'Metodo de pago'",
                                errorCode = "ERR_INVALID_PARAMS"
                            )
                        )
                        return
                    }
                    if (request.creditInstalments != -1) {
                        sendIntentResult(
                            IntentSaleResultInternal(
                                status = IntentSaleStatusEnum.ERROR,
                                message = "Parametro 'Cantidad de cuotas' invalido cuando esta habilitada la seleccion manual del 'Metodo de pago'",
                                errorCode = "ERR_INVALID_PARAMS"
                            )
                        )
                        return
                    }
                    if (request.debitChange != -1) {
                        sendIntentResult(
                            IntentSaleResultInternal(
                                status = IntentSaleStatusEnum.ERROR,
                                message = "Parametro 'Vuelto con debito' invalido cuando esta habilitada la seleccion manual del 'Metodo de pago'",
                                errorCode = "ERR_INVALID_PARAMS"
                            )
                        )
                        return
                    }
                }

                1 -> {
                    if (request.cashChange != -1 &&
                        request.cashChange < request.totalAmount
                    ) {
                        sendIntentResult(
                            IntentSaleResultInternal(
                                status = IntentSaleStatusEnum.ERROR,
                                message = "Parametro 'Vuelto para efectivo' es menor que el 'Total a pagar'",
                                errorCode = "ERR_INVALID_PARAMS"
                            )
                        )
                    }
                }

                2 -> {
                    if (request.creditInstalments != -1 &&
                        request.creditInstalments != 0 &&
                        request.creditInstalments > 13
                    ) {
                        sendIntentResult(
                            IntentSaleResultInternal(
                                status = IntentSaleStatusEnum.ERROR,
                                message = "Parametro 'Cantidad de cuotas' invalido cuando esta habilitada la seleccion manual del 'Metodo de pago'",
                                errorCode = "ERR_INVALID_PARAMS"
                            )
                        )
                    }
                }

                3 -> {
                    if (request.debitChange != -1 &&
                        request.debitChange != 0 &&
                        request.debitChange > 0
                    ) {
                        sendIntentResult(
                            IntentSaleResultInternal(
                                status = IntentSaleStatusEnum.ERROR,
                                message = "Parametro 'Cantidad de cuotas' invalido cuando esta habilitada la seleccion manual del 'Metodo de pago'",
                                errorCode = "ERR_INVALID_PARAMS"
                            )
                        )
                    }
                }

                else -> {
                    sendIntentResult(
                        IntentSaleResultInternal(
                            status = IntentSaleStatusEnum.ERROR,
                            message = "Parametro 'Metodo de pago' invalido",
                            errorCode = "ERR_INVALID_PARAMS"
                        )
                    )
                    return
                }
            }

            val saleRoute = SalesProcessGraph(request.totalAmount.toFloat())

            lifecycleScope.launch {
                mainViewModel.navigateTo(saleRoute)
            }
        }
    }