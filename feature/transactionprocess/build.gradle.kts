plugins {
    alias(libs.plugins.paymentapp.android.library)
    alias(libs.plugins.paymentapp.android.library.compose)
}

android {
    namespace = "com.soleel.paymentapp.feature.transactionprocess"
}

dependencies {
    implementation(projects.core.ui)
}