package org.evoleq.ktorx.config

import org.evoleq.ktorx.data.ImmutableMap
import org.evoleq.ktorx.server.module.BoundaryConfiguration
import java.io.File

fun boundaryConfigurations(
    projectRoot: File,
    configPath: String,
    moduleNames: List<String>,
    imports: HashSet<String> = defaultImports
): ImmutableMap<String, BoundaryConfiguration.() -> Unit> =
    ImmutableMap(hashMapOf(
        *moduleNames.map {
            name -> Pair(
                name,
                moduleBoundaryConfiguration(
                    projectRoot,
                    configPath,
                    name,
                    imports.addAllFluent(defaultImports)
                )
            )
        }.toTypedArray()
    ))