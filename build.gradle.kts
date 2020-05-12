plugins {
    //kotlin("multiplatform")
    
    //application
    java
    kotlin("jvm")  version Config.Versions.kotlin
    
    id("org.jetbrains.kotlin.plugin.serialization") version Config.Versions.kotlin
    //id("com.google.protobuf")
    id ("com.github.hierynomus.license") version "0.15.0"
    `maven-publish`
    maven
    id("org.drx.kotlin-algebraic-types-plugin") version Config.Versions.algebraicTypes
}

group = Config.Projects.KtorxServerArch.group
version = Config.Projects.KtorxServerArch.version


buildscript{
    repositories {
        mavenLocal()
        mavenCentral()
        jcenter()
        maven { url = uri ("https://plugins.gradle.org/m2/") }
    }
    
    dependencies{
        classpath( Config.Dependencies.kotlinGradlePlugin )
        //classpath( Config.Dependencies.shadow )
    }
}


configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

repositories {
    mavenLocal()
    mavenCentral()
    jcenter()
    maven { url = uri ("https://plugins.gradle.org/m2/") }
}

dependencies {
    implementation( "org.evoleq:ktorx-jvm:1.0.0" )
    implementation( "org.evoleq:ktorx-response-jvm:1.0.0"  )
    
    // evoleq
    implementation(Config.Dependencies.evoleqCore)
    //implementation(Config.Dependencies.dynamics)
    implementation(Config.Dependencies.configurations)
    
    implementation("org.evoleq:mathcat-result-jvm:1.0.0")
    implementation("org.evoleq:mathcat-core-jvm:1.0.0")
    implementation("org.evoleq:mathcat-structure-jvm:1.0.0")
    
    implementation(Config.Dependencies.kotlinStandardLibrary)
    implementation(Config.Dependencies.coroutines)
    implementation(Config.Dependencies.kotlinSerializationRuntime)
    
    // ktor
    implementation( Config.Dependencies.ktorServerNetty )
    implementation( Config.Dependencies.ktorHtmlBuilder )
    implementation( Config.Dependencies.logback )
    implementation ("org.slf4j:slf4j-nop:1.7.25")
    
    implementation("io.ktor:ktor-serialization:${Config.Versions.ktor}")
    //implementation("io.ktor:ktor-gson:${Config.Versions.ktor}")
    
    //implementation("io.ktor:ktor-auth:${Config.Versions.ktor}")
    //implementation("io.ktor:ktor-auth-jwt:${Config.Versions.ktor}")

    
    implementation("io.ktor:ktor-websockets:${Config.Versions.ktor}")
    implementation("io.ktor:ktor-client-core:${Config.Versions.ktor}")
    implementation("io.ktor:ktor-client-cio:${Config.Versions.ktor}")
    implementation("io.ktor:ktor-client-serialization-jvm:${Config.Versions.ktor}")
    implementation("io.ktor:ktor-client-json-jvm:${Config.Versions.ktor}")
    
    /*
     */
    
    testImplementation ("io.ktor:ktor-server-test-host:${Config.Versions.ktor}" )
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = Config.Projects.KtorxServerArch.group
            artifactId = Config.Projects.KtorxServerArch.artefactId
            version = Config.Projects.KtorxServerArch.version
            
            from(components["java"])
        }
    }
}
