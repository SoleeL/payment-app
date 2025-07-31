package com.soleel.paymentapp.feature.salesprocess.utils

import com.soleel.paymentapp.core.model.Sale
import com.soleel.paymentapp.feature.salesprocess.SalesProcessUiModel

fun SalesProcessUiModel.toSale(): Sale {
        return Sale(
            totalAmount = totalAmount,
            tipTotal = tipTotal,
            paymentMethodSelected = paymentMethodSelected,
            cashChangeSelected = cashChangeSelected,
            creditInstalmentsSelected = creditInstalmentsSelected,
            debitChangeSelected = debitChangeSelected
        )
    }