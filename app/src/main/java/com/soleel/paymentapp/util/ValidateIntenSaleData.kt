package com.soleel.paymentapp.util

import android.os.Bundle
import com.soleel.paymentapp.core.model.intentsale.IntentSaleStatusEnum


fun validateIntentSaleArgs(
    commerceId: String,
    totalAmount: Int,
    paymentMethod: Int,
    cashChange: Int,
    creditInstalments: Int,
    debitChange: Int
): Bundle? {
    val bundle: Bundle = Bundle()

    if (commerceId.isBlank()) {
        bundle.putString("saleId", null)
        bundle.putInt("status", IntentSaleStatusEnum.ERROR.id)
        bundle.putString("message", "Parametro 'id de comercio' invalido",)
        bundle.putString("errorCode", "ERR_INVALID_PARAMS")
        return bundle
    }

    // TODO: Realizar una validacion de que el commerceId corresponde al del terminal

    if (totalAmount <= 0 || totalAmount > 9999999) {
        bundle.putString("saleId", null)
        bundle.putInt("status", IntentSaleStatusEnum.ERROR.id)
        bundle.putString("message", "Parametro 'Total a pagar' invalido")
        bundle.putString("errorCode", "ERR_INVALID_PARAMS")
        return bundle
    }

    when (paymentMethod) {
        -1 -> {
            if (cashChange != -1 || creditInstalments != -1 || debitChange != -1) {
                bundle.putString("saleId", null)
                bundle.putInt("status", IntentSaleStatusEnum.ERROR.id)
                bundle.putString("message", "Parametros invalidos cuando el metodo de pago es automático")
                bundle.putString("errorCode", "ERR_INVALID_PARAMS")
                return bundle
            }
        }

        1 -> { // efectivo
            if (cashChange != -1 && cashChange < totalAmount) {
                bundle.putString("saleId", null)
                bundle.putInt("status", IntentSaleStatusEnum.ERROR.id)
                bundle.putString("message", "Parametro 'Vuelto para efectivo' es menor que el 'Total a pagar'")
                bundle.putString("errorCode", "ERR_INVALID_PARAMS")
                return bundle
            }
        }

        2 -> { // crédito
            if (creditInstalments != -1 && (creditInstalments == 0 || creditInstalments > 13)) {
                bundle.putString("saleId", null)
                bundle.putInt("status", IntentSaleStatusEnum.ERROR.id)
                bundle.putString("message", "Parametro 'Cantidad de cuotas' invalido para tarjeta de crédito")
                bundle.putString("errorCode", "ERR_INVALID_PARAMS")
                return bundle
            }
        }

        3 -> { // débito
            if (debitChange != -1 && debitChange > 0) {
                bundle.putString("saleId", null)
                bundle.putInt("status", IntentSaleStatusEnum.ERROR.id)
                bundle.putString("message", "Parametro 'Vuelto con debito' no debe ser mayor que 0")
                bundle.putString("errorCode", "ERR_INVALID_PARAMS")
                return bundle
            }
        }

        else -> {
            bundle.putString("saleId", null)
            bundle.putInt("status", IntentSaleStatusEnum.ERROR.id)
            bundle.putString("message", "Metodo de pago invalido")
            bundle.putString("errorCode", "ERR_INVALID_PARAMS")
            return bundle
        }
    }

    return null
}