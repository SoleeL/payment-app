plugins {
    alias(libs.plugins.paymentapp.android.library)
    alias(libs.plugins.paymentapp.android.hilt)
}

android {
    namespace = "com.soleel.paymentapp.data.preferences"
}

dependencies {
    implementation(projects.core.model)

    // TODO: Sacar de aqui -> Mover al catalogo de versiones
    implementation(libs.datastore.preferences)
}