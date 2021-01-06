/**
 * Copyright (c) 2020 Dr. Florian Schmidt
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
