plugins {
    alias(libs.plugins.paymentapp.android.library)
    alias(libs.plugins.paymentapp.android.library.compose)
    alias(libs.plugins.paymentapp.android.hilt)
}

android {
    namespace = "com.soleel.paymentapp.feature.salesprocess.outcome"
}

dependencies {
    implementation(projects.core.ui)
    implementation(projects.core.model)
}