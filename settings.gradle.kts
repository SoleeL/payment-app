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

include(":core:common")
include(":core:component")
include(":core:model")
include(":core:navigation")
include(":core:ui")

include(":data:preferences")

include(":domain:payment")
include(":domain:reading")

include(":feature:home")
include(":feature:salesprocess")
include(":domain:sale")
