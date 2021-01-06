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