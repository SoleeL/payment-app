import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `kotlin-dsl`
}

group = "com.soleel.paymentapp.buildlogic"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }
}

dependencies {
    implementation(libs.android.gradle.plugin)
    implementation(libs.kotlin.gradle.plugin)
}

gradlePlugin {
    plugins {
        // Application module
        register("androidApplication") {
            id = "paymentapp.android.application"
            implementationClass = "plugins.AndroidApplicationConventionPlugin"
        }

        register("androidApplicationCompose") {
            id = "paymentapp.android.application.compose"
            implementationClass = "plugins.ComposeApplicationConventionPlugin"
        }

        // Library module

        register("androidLibrary") {
            id = "paymentapp.android.library"
            implementationClass = "plugins.AndroidLibraryConventionPlugin"
        }

        register("androidLibraryCompose") {
            id = "paymentapp.android.library.compose"
            implementationClass = "plugins.ComposeLibraryConventionPlugin"
        }

        // Library dependency

        register("androidHilt") {
            id = "paymentapp.android.hilt"
            implementationClass = "plugins.HiltConventionPlugin"
        }

        register("androidRoom") {
            id = "paymentapp.android.room"
            implementationClass = "plugins.RoomConventionPlugin"
        }
    }
}