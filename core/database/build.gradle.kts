plugins {
    alias(libs.plugins.paymentapp.android.library)
    alias(libs.plugins.paymentapp.android.hilt)
    alias(libs.plugins.paymentapp.android.room)
}

android {
    namespace = "com.soleel.paymentapp.core.database"
}

dependencies {
    api(projects.core.model)
}