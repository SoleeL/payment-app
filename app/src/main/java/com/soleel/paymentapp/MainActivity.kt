package com.soleel.paymentapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.soleel.paymentapp.core.model.intentsale.IntentSaleRequestExternal
import com.soleel.paymentapp.core.model.intentsale.IntentSaleResultExternal
import com.soleel.paymentapp.core.model.intentsale.IntentSaleResultInternal
import com.soleel.paymentapp.core.model.intentsale.IntentSaleStatusEnum
import com.soleel.paymentapp.core.model.intentsale.toExternal
import com.soleel.paymentapp.core.ui.theme.PaymentAppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        handleIntentIfNeeded(intent)

        enableEdgeToEdge()
        setContent(
            content = {
                PaymentAppTheme(
                    content = {
                        PaymentAppNavigationGraph()
                    }
                )
            }
        )
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let { handleIntentIfNeeded(it) }
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

            if (request.totalAmount <= 0 || request.commerceId.isBlank()) {
                sendIntentResult(
                    IntentSaleResultInternal(
                        status = IntentSaleStatusEnum.ERROR,
                        message = "Parámetros inválidos",
                        errorCode = "ERR_INVALID_PARAMS"
                    )
                )
                return
            }

            // TODO Implementar el flujo de venta
        }
    }

    private fun sendIntentResult(intentSaleResultInternal: IntentSaleResultInternal) {
        val intentSaleResultExternal : IntentSaleResultExternal = intentSaleResultInternal.toExternal()
        val json = Json.encodeToString(intentSaleResultExternal)
        val resultIntent = Intent().apply {
            putExtra("result", json)
        }
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }

}