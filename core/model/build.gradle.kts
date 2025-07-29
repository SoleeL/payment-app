plugins {
    alias(libs.plugins.paymentapp.android.library)
}

android {
    namespace = "com.soleel.paymentapp.core.model"
}

dependencies {
    implementation(projects.core.ui)
}