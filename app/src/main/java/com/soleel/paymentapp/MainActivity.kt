package com.soleel.paymentapp

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.soleel.paymentapp.core.ui.theme.PaymentAppTheme
import dagger.hilt.android.AndroidEntryPoint
import android.content.Intent
import com.soleel.paymentapp.feature.home.HomeGraph
import com.soleel.paymentapp.feature.salesprocess.SalesProcessGraph
import com.soleel.paymentapp.util.validateIntentSaleArgs

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var startDestination: Any = HomeGraph // default
        if (intent?.action == "com.soleel.paymentapp.PROCESS_INTENT_TO_SALE") {
            val extras = intent.extras

            val commerceId: String = extras?.getString("commerceId", "") ?: ""
            val totalAmount: Int = extras?.getInt("totalAmount", 0) ?: 0
            val paymentMethod: Int = extras?.getInt("paymentMethod", -1) ?: -1
            val cashChange: Int = extras?.getInt("cashChange", -1) ?: -1
            val creditInstalments: Int = extras?.getInt("creditInstalments", -1) ?: -1
            val debitChange: Int = extras?.getInt("debitChange", -1) ?: -1

            val bundle: Bundle? = validateIntentSaleArgs(
                commerceId = commerceId,
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
                paymentMethod = paymentMethod,
                cashChange = cashChange,
                creditInstalments = creditInstalments,
                debitChange = debitChange
            )
        }

//        startDestination = SalesProcessGraph(
//            totalAmount = 10000,
//            paymentMethod = -1,
//            cashChange = -1,
//            creditInstalments = -1,
//            debitChange = -1
//        )

        enableEdgeToEdge()
        setContent(
            content = {
                PaymentAppTheme(
                    content = {
                        PaymentAppNavigationGraph(startDestination)
                    }
                )
            }
        )
    }
}