import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm") version "2.2.20"
    kotlin("plugin.serialization") version "2.2.20"
    id("org.jetbrains.compose.hot-reload") version "1.0.0"
    id("org.jetbrains.compose") version "1.10.0"
    id("org.jetbrains.kotlin.plugin.compose") version "2.2.20"
}

dependencies {
    implementation(kotlin("reflect"))
    implementation("org.jetbrains.compose.runtime:runtime:1.10.0")
    implementation("org.jetbrains.compose.foundation:foundation:1.10.0")
    implementation("org.jetbrains.compose.ui:ui:1.10.0")
    implementation("org.jetbrains.compose.components:components-resources:1.10.0")
    implementation("org.jetbrains.compose.ui:ui-tooling-preview:1.10.0")
    implementation("io.github.vinceglb:filekit-dialogs-compose:0.12.0")

    implementation("com.composables:composeunstyled:1.49.6")
    implementation("com.composables:composeunstyled-theming:1.49.6")
    implementation("com.composables:composeunstyled-primitives:1.49.6")
    implementation("com.composables:composeunstyled-platformtheme:1.49.6")

    implementation(compose.desktop.currentOs) {
        exclude("org.jetbrains.compose.material")
        exclude("org.jetbrains.compose.material3")
    }
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.10.2")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.10.0")

    testImplementation(kotlin("test"))
}


compose.desktop {
    application {
        mainClass = "io.github.seggan.glagolitsa.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "io.github.seggan.glagolitsa"
            packageVersion = "1.0.0"
        }
    }
}
