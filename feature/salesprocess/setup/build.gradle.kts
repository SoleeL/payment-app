plugins {
    alias(libs.plugins.paymentapp.android.library)
    alias(libs.plugins.paymentapp.android.library.compose)
    alias(libs.plugins.paymentapp.android.hilt)
}

android {
    namespace = "com.soleel.paymentapp.feature.salesprocess.setup"
}

dependencies {
    implementation(projects.core.component)
    implementation(projects.core.model)
    implementation(projects.core.ui)
}