plugins {
    java
    kotlin("jvm")  version Config.Versions.kotlin
    id("org.jetbrains.kotlin.plugin.serialization") version Config.Versions.kotlinSerializationPlugin
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
    implementation(Config.Dependencies.kotlinStandardLibrary)
    implementation(Config.Dependencies.coroutines)

    // evoleq/configurations
    implementation(Config.Dependencies.configurations)
    implementation(Config.Dependencies.dynamics)

    // evoleq/mathcat
    implementation("org.evoleq:mathcat-result-jvm:${Config.Versions.mathcat}")
    implementation("org.evoleq:mathcat-core-jvm:${Config.Versions.mathcat}")
    implementation("org.evoleq:mathcat-structure-jvm:${Config.Versions.mathcat}")
    implementation("org.evoleq:mathcat-morphism-jvm:${Config.Versions.mathcat}")
    implementation("org.evoleq:mathcat-state-jvm:${Config.Versions.mathcat}")

    // evoleq/ktorx
    implementation( "org.evoleq:ktorx-jvm:${Config.Projects.Ktorx.version}" )
    implementation( "org.evoleq:ktorx-response-jvm:${Config.Projects.KtorxResponse.version}"  )

    testImplementation( "org.evoleq:ktorx-jvm:${Config.Projects.Ktorx.version}" )
    testImplementation( "org.evoleq:ktorx-response-jvm:${Config.Projects.KtorxResponse.version}"  )
    testImplementation( "org.evoleq:ktorx-server-arch-test:${Config.Projects.KtorxResponse.version}"  )


    
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
    
    
    // kotlin scripting
    api ("org.jetbrains.kotlin:kotlin-script-util:${Config.Versions.kotlin}")
    api ("org.jetbrains.kotlin:kotlin-script-runtime:${Config.Versions.kotlin}")
    api ("org.jetbrains.kotlin:kotlin-compiler-embeddable:${Config.Versions.kotlin}")
    api ("org.jetbrains.kotlin:kotlin-scripting-compiler-embeddable:${Config.Versions.kotlin}")
    
    /*
     */
    
    testImplementation ("io.ktor:ktor-server-test-host:${Config.Versions.ktor}" )
}

tasks {
    val publishMavenToBintray by register("publishMavenToBintray") {
        group = "publishing-to-bintray"
        description = "Publishes a Maven publication to the external Maven repository."
        dependsOn(
            "publishMavenPublicationToMavenRepository"
        )
    }
}

val bintrayUser: String by project
val bintrayApiKey: String by project
val licenseName: String by project
val licenseUrl: String by project
val drxDevId: String by project
val florianSchmidtName: String by project
val florianSchmidtMail: String by project
val organisationName: String by project
val repositoryName: String by project
val ktorxServerArchGitUrl: String by project
val gitUrl: String = ktorxServerArchGitUrl
val packageName = project.name

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = Config.Projects.KtorxServerArch.group
            artifactId = Config.Projects.KtorxServerArch.artefactId
            version = Config.Projects.KtorxServerArch.version
            
            from(components["java"])
    
            pom {
                licenses {
                    license {
                        name.set(licenseName)
                        url.set(licenseUrl)
                    }
                }
                developers {
                    developer {
                        id.set(drxDevId)//developerId)
                        name.set(florianSchmidtName)//developerName)
                        email.set(florianSchmidtMail)//developerEmail)
                    }
                }
                organization {
                    name.set(organisationName)//developerOrg)
                }
                scm {
                    connection.set(gitUrl)
                    developerConnection.set(gitUrl)
                    //url.set(siteUrl)
                }
            }
        }
    }
    
    repositories {
        maven {
            //name = it
            url = uri(
                "https://api.bintray.com/maven/$organisationName/$repositoryName/$packageName/;publish=1;override=1;"
            )
            credentials {
                username = bintrayUser
                password = bintrayApiKey
            }
        }
    }
}


//apply(from = "../publish.mpp.gradle.kts")
