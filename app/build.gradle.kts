plugins {
    alias(libs.plugins.paymentapp.android.application)
    alias(libs.plugins.paymentapp.android.application.compose)
    alias(libs.plugins.paymentapp.android.hilt)
}

android {
    namespace = "com.soleel.paymentapp"
}

dependencies {
    implementation(projects.core.model)
    implementation(projects.core.navigation)
    implementation(projects.core.ui)

    implementation(projects.feature.home)
    implementation(projects.feature.salesprocess2)
}