package com.soleel.paymentapp.feature.salesprocess.utils

import com.soleel.paymentapp.core.model.Sale
import com.soleel.paymentapp.feature.salesprocess.SalesProcessUiModel

fun SalesProcessUiModel.toSale(): Sale {
        return Sale(
            calculatorTotal = calculatorTotal,
            tipTotal = tipTotal,
            paymentMethodSelected = paymentMethodSelected,
            cashChangeSelected = cashChangeSelected,
            creditInstalmentsSelected = creditInstalmentsSelected,
            debitChangeSelected = debitChangeSelected
        )
    }