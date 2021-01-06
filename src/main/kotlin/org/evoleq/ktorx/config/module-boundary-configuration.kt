package org.evoleq.ktorx.config

import org.evoleq.ktorx.server.module.BoundaryConfiguration
import java.io.File

fun moduleBoundaryConfiguration(
    projectRoot: File,
    configPath: String,
    moduleName: String,
    imports: HashSet<String> = defaultImports
): BoundaryConfiguration.()->Unit {
    val boundaryConfigFolder = File(projectRoot.absolutePath+ "/$configPath/$moduleName/boundary")
    return boundaryConfiguration(
        imports.addAllFluent(defaultImports),
        *boundaryConfigFolder.files().filter{it.name.endsWith(".kts")}.toTypedArray()
    )
}