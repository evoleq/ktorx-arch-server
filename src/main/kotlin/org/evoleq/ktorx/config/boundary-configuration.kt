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

val defaultImports  = hashSetOf<String>(
"import org.evoleq.ktorx.marker.KtorxDsl",
"import org.evoleq.ktorx.server.module.*"
)

@Suppress("unchecked_cast")
fun boundaryConfiguration(
    imports: HashSet<String> = defaultImports,
    vararg files: File
): BoundaryConfiguration.()->Unit {
    val tab = "    "
    val configs= files
        .map { it.readLines() }
        .map { list ->
            list.filter { line -> when(line.trim().startsWith("import")){
                true -> {
                    imports.add(line.trim())
                    false
                }
                false -> true
                } }
            .joinToString("\n") { it }
        }
        .joinToString("\n$tab","\n$tab", "\n$tab") { it }
        
    //val configs: String = files.map { it.readText() }.joinToString("\n$tab","\n$tab", "\n$tab") { it }
    val script = """
        |${imports.joinToString("\n") { it }}
        |
        |@KtorxDsl
        |fun BoundaryConfiguration.boundary() {
        |$configs
        |}
        |
        |val boundaryConfiguration: BoundaryConfiguration.()->Unit = {
        |    boundary()
        |}
        |boundaryConfiguration
    """.trimMargin()
    
    //println(script)
    
    return evalScript(script) as BoundaryConfiguration.()->Unit
}

fun ApplicationEnvironment.boundaryConfigurations(): ImmutableMap<String, BoundaryConfiguration.() -> Unit> {
    val path = config.property("ktor.config.path").getString()
    val imports = try{config.property("ktor.config.imports").getList()}catch (ex: Exception){
        listOf<String>()}.toHashSet()
    imports.addAll(defaultImports)
    val modules = config.property("ktor.config.modules").getList()
        .map{ module -> module to File("$path/module/${module}/boundary").listFiles()!!.filter{ it.isFile && it.name.endsWith(".kts") }}
        .map{it.first to boundaryConfiguration(imports, *it.second.toTypedArray())}
    return ImmutableMap(hashMapOf(*modules.toTypedArray()))
}