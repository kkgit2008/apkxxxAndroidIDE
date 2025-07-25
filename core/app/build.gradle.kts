/*
 *  This file is part of AndroidIDE.
 *
 *  AndroidIDE is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  AndroidIDE is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *   along with AndroidIDE.  If not, see <https://www.gnu.org/licenses/>.
 */

@file:Suppress("UnstableApiUsage")

import com.itsaky.androidide.build.config.BuildConfig
import com.itsaky.androidide.desugaring.utils.JavaIOReplacements.applyJavaIOReplacements
import com.itsaky.androidide.plugins.AndroidIDEAssetsPlugin

import java.io.FileInputStream
import java.util.Properties

plugins {
  id("com.itsaky.androidide.core-app")
  id("com.android.application")
  id("kotlin-android")
  id("kotlin-kapt")
  id("kotlin-parcelize")
  id("androidx.navigation.safeargs.kotlin")
  id("com.itsaky.androidide.desugaring")
}

apply {
  plugin(AndroidIDEAssetsPlugin::class.java)
}

buildscript {
  dependencies {
    classpath(libs.logging.logback.core)
    classpath(libs.composite.desugaringCore)
  }
}

android {
  namespace = BuildConfig.packageName

    // ****** 设置自定义签名 ******
    signingConfigs {
        create("releaseee") {
            val localProperties = Properties()
            val localPropertiesFile = rootProject.file("local.properties")
            enableV1Signing = true // 启用 V1 签名
            enableV2Signing = true // 启用 V2 签名 (推荐，Android 7.0+)
            enableV3Signing = true // 启用 V3 签名 (推荐，Android 9+)
            if (localPropertiesFile.exists()) {
                localProperties.load(FileInputStream(localPropertiesFile))

                val storeFilePath = localProperties.getProperty("storeFile")
                val storePasswordValue = localProperties.getProperty("storePassword")
                val keyAliasValue = localProperties.getProperty("keyAlias")
                val keyPasswordValue = localProperties.getProperty("keyPassword")

                if (storeFilePath != null && storePasswordValue != null && 
                    keyAliasValue != null && keyPasswordValue != null) {
                    storeFile = file(storeFilePath)
                    storePassword = storePasswordValue
                    keyAlias = keyAliasValue
                    keyPassword = keyPasswordValue
                } else {
                    logger.error("There is sth wrong with file content:local.properties !")
                }
            } else {
                logger.error("File not exist:local.properties !")
            }
        }
    }

        buildTypes {
            debug {
                signingConfig = signingConfigs.getByName("releaseee")
                //applicationIdSuffix '.debug'
                versionNameSuffix = "-debug"
            }
            release {
                signingConfig = signingConfigs.getByName("releaseee")

            }
        }
    // ****** 设置自定义签名 ******

  defaultConfig {
    applicationId = BuildConfig.packageName
    vectorDrawables.useSupportLibrary = true
  }

  androidResources {
    generateLocaleConfig = true
  }

  buildTypes {
    release {
      isShrinkResources = true
    }
  }

  lint {
    abortOnError = false
    disable.addAll(arrayOf("VectorPath", "NestedWeights", "ContentDescription", "SmallSp"))
  }
}

kapt {
  arguments {
    arg("eventBusIndex", "${BuildConfig.packageName}.events.AppEventsIndex")
  }
}

desugaring {
  replacements {
    includePackage(
      "org.eclipse.jgit",
    )

    applyJavaIOReplacements()
  }
}

dependencies {
  debugImplementation(libs.common.leakcanary)

  // Annotation processors
  kapt(libs.common.glide.ap)
  kapt(libs.google.auto.service)
  kapt(projects.annotation.processors)

  implementation(libs.common.editor)
  implementation(libs.common.utilcode)
  implementation(libs.common.glide)
  implementation(libs.common.jsoup)
  implementation(libs.common.kotlin.coroutines.android)
  implementation(libs.common.retrofit)
  implementation(libs.common.retrofit.gson)
  implementation(libs.common.charts)
  implementation(libs.common.hiddenApiBypass)
  implementation(libs.aapt2.common)

  implementation(libs.google.auto.service.annotations)
  implementation(libs.google.gson)
  implementation(libs.google.guava)

  // Git
  implementation(libs.git.jgit)

  // AndroidX
  implementation(libs.androidx.splashscreen)
  implementation(libs.androidx.annotation)
  implementation(libs.androidx.appcompat)
  implementation(libs.androidx.cardview)
  implementation(libs.androidx.constraintlayout)
  implementation(libs.androidx.coordinatorlayout)
  implementation(libs.androidx.drawer)
  implementation(libs.androidx.grid)
  implementation(libs.androidx.nav.fragment)
  implementation(libs.androidx.nav.ui)
  implementation(libs.androidx.preference)
  implementation(libs.androidx.recyclerview)
  implementation(libs.androidx.transition)
  implementation(libs.androidx.vectors)
  implementation(libs.androidx.animated.vectors)
  implementation(libs.androidx.work)
  implementation(libs.androidx.work.ktx)
  implementation(libs.google.material)
  implementation(libs.google.flexbox)

  // Kotlin
  implementation(libs.androidx.core.ktx)
  implementation(libs.common.kotlin)

  // Dependencies in composite build
  implementation(libs.composite.appintro)
  implementation(libs.composite.desugaringCore)
  implementation(libs.composite.javapoet)

  // Local projects here
  implementation(projects.core.actions)
  implementation(projects.core.common)
  implementation(projects.core.indexingApi)
  implementation(projects.core.indexingCore)
  implementation(projects.core.lspApi)
  implementation(projects.core.projects)
  implementation(projects.core.resources)
  implementation(projects.editor.impl)
  implementation(projects.editor.lexers)
  implementation(projects.event.eventbus)
  implementation(projects.event.eventbusAndroid)
  implementation(projects.event.eventbusEvents)
  implementation(projects.java.javacServices)
  implementation(projects.java.lsp)
  implementation(projects.logging.idestats)
  implementation(projects.logging.logsender)
  implementation(projects.termux.application)
  implementation(projects.termux.view)
  implementation(projects.termux.emulator)
  implementation(projects.termux.shared)
  implementation(projects.tooling.api)
  implementation(projects.tooling.pluginConfig)
  implementation(projects.utilities.buildInfo)
  implementation(projects.utilities.lookup)
  implementation(projects.utilities.preferences)
  implementation(projects.utilities.templatesApi)
  implementation(projects.utilities.templatesImpl)
  implementation(projects.utilities.treeview)
  implementation(projects.utilities.uidesigner)
  implementation(projects.utilities.xmlInflater)
  implementation(projects.xml.aaptcompiler)
  implementation(projects.xml.lsp)
  implementation(projects.xml.utils)

  // This is to build the tooling-api-impl project before the app is built
  // So we always copy the latest JAR file to assets
  compileOnly(projects.tooling.impl)

  testImplementation(projects.testing.unitTest)
  androidTestImplementation(projects.testing.androidTest)
}
