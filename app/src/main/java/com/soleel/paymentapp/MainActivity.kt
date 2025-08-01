package com.soleel.paymentapp

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.soleel.paymentapp.core.ui.theme.PaymentAppTheme
import dagger.hilt.android.AndroidEntryPoint
import android.content.Intent
import com.soleel.paymentapp.core.model.intentsale.IntentSaleStatusEnum
import com.soleel.paymentapp.feature.home.HomeGraph
import com.soleel.paymentapp.feature.salesprocess.SalesProcessGraph
import com.soleel.paymentapp.util.validateIntentSaleArgs

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var startDestination: Any = HomeGraph // default
        var isExternalSale: Boolean = false
        if (intent?.action == "com.soleel.paymentapp.PROCESS_INTENT_TO_SALE") {
            val extras = intent.extras

            val commerceId: String = extras?.getString("commerceId", "") ?: ""
            val source: String = extras?.getString("source", "") ?: ""
            val totalAmount: Int = extras?.getInt("totalAmount", 0) ?: 0
            val paymentMethod: Int = extras?.getInt("paymentMethod", -1) ?: -1
            val cashChange: Int = extras?.getInt("cashChange", -1) ?: -1
            val creditInstalments: Int = extras?.getInt("creditInstalments", -1) ?: -1
            val debitChange: Int = extras?.getInt("debitChange", -1) ?: -1

            val bundle: Bundle? = validateIntentSaleArgs(
                commerceId = commerceId,
                source = source,
                totalAmount = totalAmount,
                paymentMethod = paymentMethod,
                cashChange = cashChange,
                creditInstalments = creditInstalments,
                debitChange = debitChange
            )

            if (bundle != null) {
                val resultIntent: Intent = Intent()
                resultIntent.putExtras(bundle)
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
                return
            }

            startDestination = SalesProcessGraph(
                totalAmount = totalAmount,
                source = source,
                paymentMethod = paymentMethod,
                cashChange = cashChange,
                creditInstalments = creditInstalments,
                debitChange = debitChange
            )

            isExternalSale = true
        }

//        startDestination = SalesProcessGraph(
//            totalAmount = 10000,
//            source = "OTRA APP",
//            paymentMethod = -1,
//            cashChange = -1,
//            creditInstalments = -1,
//            debitChange = -1
//        )

        enableEdgeToEdge()
        setContent {
            PaymentAppTheme {
                PaymentAppNavigationGraph(
                    startDestination = startDestination,
                    finishWithResult = if (isExternalSale) ::finishWithResult else null
                )
            }
        }
    }

    private fun finishWithResult(
        saleId: String? = null,
        status: IntentSaleStatusEnum,
        message: String? = null,
        errorCode: String? = null
    ) {
        val resultIntent: Intent = Intent()
        val bundle: Bundle = Bundle()
        bundle.putString("saleId", saleId)
        bundle.putInt("status", status.id)
        bundle.putString("message", message)
        bundle.putString("errorCode", errorCode)
        resultIntent.putExtras(bundle)
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }
}