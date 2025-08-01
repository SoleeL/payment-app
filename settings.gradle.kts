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

include(":data:payment")
include(":data:preferences")
include(":data:sale")

include(":domain:payment")
include(":domain:pinpad")
include(":domain:reading")
include(":domain:sale")

include(":feature:home")
include(":feature:salesprocess")

include(":core:database")
