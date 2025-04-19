dependencies 
    
    {
    // AndroidX Core Libraries
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    // Lifecycle Components
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)

    // Navigation Components
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // Animation
    implementation("com.airbnb.android:lottie:6.3.0")

    // Material Design
    implementation("com.google.android.material:material:1.11.0")

    // Networking
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.google.code.gson:gson:2.10.1")

    // Toast Library
    implementation("com.github.GrenderG:Toasty:1.5.2")

    // Image Loading and Processing
    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")
    implementation("jp.wasabeef:glide-transformations:4.3.0")
    implementation("jp.co.cyberagent.android:gpuimage:2.1.0")

    // Pull-to-Refresh
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")

    implementation("com.squareup.okhttp3:logging-interceptor:4.9.0")

    // implementation("androidx.loader:loader:2.1.0")
    }

setting gradel:

    pluginManagement {
        repositories {
            google {
                content {
                    includeGroupByRegex("com\\.android.*")
                    includeGroupByRegex("com\\.google.*")
                    includeGroupByRegex("androidx.*")
                }
            }
            mavenCentral()
            maven { url = uri("https://jitpack.io") }
            gradlePluginPortal()
        }
    }
    dependencyResolutionManagement {
        repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
        repositories {
            google()
            mavenCentral()
            maven { url = uri("https://jitpack.io") }
        }
    }
    
    rootProject.name = "UTS_elsid"
    include(":app")


gradel properties :

    org.gradle.jvmargs=-Xmx2048m -XX:MaxPermSize=512m -Dfile.encoding=UTF-8
