package com.soleel.paymentapp.core.model.enums

enum class DeveloperPreferenceEnum(
    val key: String,
    val displayName: String,
    val displayDescription: String,
    val defaultIsEnabled: Boolean = false
) {
    CONTACTLESS_READER_FALLBACK(
        "CONTACTLESS_READER_FALLBACK",
        "Fallback lector contactless",
        "Usar otro método si falla la lectura sin contacto"
    ),
    CONTACTLESS_READER_INVALID_CARD(
        "CONTACTLESS_READER_INVALID_CARD",
        "Tarjeta inválida contactless",
        "Simula error de tarjeta inválida en contactless"
    ),
    CONTACTLESS_READER_OTHER_ERROR(
        "CONTACTLESS_READER_OTHER_ERROR",
        "Otro error lector contactless",
        "Simula otros errores genéricos en contactless"
    ),

    CONTACT_READER_FALLBACK(
        "CONTACT_READER_FALLBACK",
        "Fallback lector contact",
        "Usar otro método si falla la lectura contact"
    ),
    CONTACT_READER_OTHER_ERROR(
        "CONTACT_READER_OTHER_ERROR",
        "Otro error lector contact",
        "Simula otros errores genéricos en contact"
    ),

    PINPAD_INVALID_KSN(
        "PINPAD_INVALID_KSN",
        "KSN inválido en pinpad",
        "Simula error de KSN inválido en pinpad"
    ),

    PAYMENT_VALIDATION_FAIL_BY_SERVICE_ERROR(
        "PAYMENT_VALIDATION_FAIL_BY_SERVICE_ERROR",
        "Error servicio validación pago",
        "Simula fallo en validación por error de servicio"
    ),
    PAYMENT_VALIDATION_FAIL_BY_ACQUIRER_ERROR(
        "PAYMENT_VALIDATION_FAIL_BY_ACQUIRER_ERROR",
        "Error adquirente validación",
        "Simula fallo en validación por error del adquirente"
    ),
    PAYMENT_VALIDATION_FAIL_BY_BRAND_ERROR(
        "PAYMENT_VALIDATION_FAIL_BY_BRAND_ERROR",
        "Error marca validación",
        "Simula fallo en validación por error de marca"
    ),

    PAYMENT_VALIDATION_FAIL_BY_INVALID_PIN(
        "PAYMENT_VALIDATION_FAIL_BY_INVALID_PIN",
        "PIN inválido en validación",
        "Simula fallo en validación por PIN inválido"
    ),
    PAYMENT_VALIDATION_FAIL_BY_PAYMENT_REJECTED(
        "PAYMENT_VALIDATION_FAIL_BY_PAYMENT_REJECTED",
        "Pago rechazado en validación",
        "Simula rechazo del pago durante validación"
    ),

    PAYMENT_CONFIRMATION_FAIL_BY_SERVICE_ERROR(
        "PAYMENT_CONFIRMATION_FAIL_BY_SERVICE_ERROR",
        "Error servicio confirmación pago",
        "Simula fallo en confirmación por error de servicio"
    ),
    PAYMENT_CONFIRMATION_FAIL_BY_AUTH_ERROR(
        "PAYMENT_CONFIRMATION_FAIL_BY_AUTH_ERROR",
        "Error autenticación confirmación",
        "Simula fallo en confirmación por error de autenticación"
    ),

    PAYMENT_SAVE_FAIL_BY_LOCAL_ERROR(
        "PAYMENT_SAVE_FAIL_BY_LOCAL_ERROR",
        "Error local guardando pago",
        "Simula fallo al guardar pago por error local"
    ),

    SALE_SAVE_FAIL_BY_LOCAL_ERROR(
        "SALE_SAVE_FAIL_BY_LOCAL_ERROR",
        "Error local guardando venta",
        "Simula fallo al guardar venta por error local"
    ),
    SALE_SAVE_FAIL_BY_SERVICE_ERROR(
        "SALE_SAVE_FAIL_BY_SERVICE_ERROR",
        "Error servicio guardando venta",
        "Simula fallo al registrar venta por error de servicio"
    );

    companion object {
        fun fromKey(key: String): DeveloperPreferenceEnum? =
            entries.firstOrNull { it.key == key }
    }
}