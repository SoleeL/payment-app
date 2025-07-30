package com.soleel.paymentapp.core.navigation

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import androidx.navigation.NavType
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


inline fun <reified T : Parcelable> createNavType(): NavType<T> {
    return object : NavType<T>(isNullableAllowed = true) {

        override fun get(bundle: Bundle, key: String): T? {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                bundle.getParcelable(key, T::class.java)
            } else {
                @Suppress("DEPRECATION")
                bundle.getParcelable<T>(key)
            }
        }

        override fun parseValue(value: String): T {
            return Json.decodeFromString<T>(value)
        }

        override fun serializeAsValue(value: T): String {
            return Uri.encode(Json.encodeToString(value))
        }

        override fun put(bundle: Bundle, key: String, value: T) {
            bundle.putParcelable(key, value)
        }
    }
}

inline fun <reified T : Parcelable> createListNavType(): NavType<List<T>> {
    return object : NavType<List<T>>(isNullableAllowed = true) {

        override fun get(bundle: Bundle, key: String): List<T>? {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                bundle.getParcelableArrayList(key, T::class.java)
            } else {
                @Suppress("DEPRECATION")
                bundle.getParcelableArrayList<T>(key)
            }
        }

        override fun parseValue(value: String): List<T> {
            return Json.decodeFromString(value)
        }

        override fun serializeAsValue(value: List<T>): String {
            return Uri.encode(Json.encodeToString(value))
        }

        override fun put(bundle: Bundle, key: String, value: List<T>) {
            bundle.putParcelableArrayList(key, ArrayList(value))
        }
    }
}