package org.evoleq.ktorx.config

import io.ktor.application.*
import org.evoleq.ktorx.data.ImmutableMap
import org.evoleq.ktorx.server.module.BoundaryConfiguration
import java.io.File


fun ApplicationEnvironment.boundaryConfigurations(): ImmutableMap<String, BoundaryConfiguration.() -> Unit> {
    val path = config.property("ktor.config.path").getString()
    val imports = try{config.property("ktor.config.imports").getList()}catch (ex: Exception){
        listOf<String>()
    }.toHashSet()
    imports.addAll(defaultImports)
    val modules = config.property("ktor.config.modules").getList()
        .map{ module -> module to File("$path/module/${module}/boundary")
                                        .files()
                                        .filter{ it.name.endsWith(".kts") }
        }
        .map{it.first to boundaryConfiguration(imports, *it.second.toTypedArray())}
    return ImmutableMap(hashMapOf(*modules.toTypedArray()))
}
