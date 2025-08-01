plugins {
    alias(libs.plugins.paymentapp.android.library)
    alias(libs.plugins.paymentapp.android.library.compose)
}

android {
    namespace = "com.soleel.paymentapp.core.component"
}

dependencies {
    implementation(projects.core.ui)
    implementation(projects.core.model)
}