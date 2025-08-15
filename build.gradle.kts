// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.detekt)
}

val ktlintFormatting = libs.detekt.ktlint.formatting
val twitterComposeRules = libs.detekt.compose.rules
subprojects {
    apply {
        plugin("io.gitlab.arturbosch.detekt")
    }

    detekt {
        config.from(rootProject.files("config/detekt/detekt.yml"))
        // We can use ./gradlew detektBaseline to generate a baseline.xml and ignore current issues.
        // baseline = file("${rootProject.projectDir}/config/baseline.xml")
    }

    dependencies {
        // To generate the config file including these plugins, we need to run the command using a
        // module name like this:
        //      ./gradlew :app:detektGenerateConfig
        // We don't know why but without the module the config file will only have the default
        // detekt rules.
        detektPlugins(ktlintFormatting)
        detektPlugins(twitterComposeRules)
    }
}