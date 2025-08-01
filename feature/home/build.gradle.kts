plugins {
    alias(libs.plugins.paymentapp.android.library)
    alias(libs.plugins.paymentapp.android.library.compose)
    alias(libs.plugins.paymentapp.android.hilt)
}

android {
    namespace = "com.soleel.paymentapp.feature.home"
}

dependencies {
    implementation(projects.core.common)
    implementation(projects.core.model)
    implementation(projects.core.ui)

    implementation(projects.data.preferences)

    implementation(projects.domain.sale)
}