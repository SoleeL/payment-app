plugins {
    alias(libs.plugins.paymentapp.android.library)
    alias(libs.plugins.paymentapp.android.hilt)
}

android {
    namespace = "com.soleel.paymentapp.domain.payment"
}

dependencies {
    implementation(projects.core.model)

    implementation(projects.data.preferences)
}

