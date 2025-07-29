pluginManagement {
    includeBuild("build-logic")
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "paymentapp"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
include(":app")

include(":core:component")
include(":core:model")
include(":core:navigation")
include(":core:ui")

include(":feature:home")

include(":feature:salesprocess:setup")
include(":feature:salesprocess:payment")
include(":feature:salesprocess:outcome")