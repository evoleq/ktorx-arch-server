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
package org.evoleq.ktorx.data

import org.drx.dynamics.Singleton
import org.evoleq.ktorx.server.module.BoundaryConfiguration

/**
 * Abstract the environment of an application
 */
open class Environment {
    companion object {
        private lateinit var innerType: EnvironmentType
    
        /**
         * Type of the [Environment]: dev, prod, test, ...
         */
        var Type: EnvironmentType
            get() = innerType
            set(value) {
                if (!::innerType.isInitialized) {
                    innerType = value
                }
            }
        val boundaryConfigurations: Singleton<ImmutableMap<String, BoundaryConfiguration.() -> Unit>> by lazy {
            Singleton<ImmutableMap<String, BoundaryConfiguration.() -> Unit>>()
        }
    }
}

/**
 * Type of the [Environment]: dev, prod, test, ...
 */
interface EnvironmentType { val name: String }

/**
 * UnknownEnvironmentTypeException
 */
class UnknownEnvironmentTypeException(private val type: EnvironmentType? = null) : Exception(
    """|Unknown environment type${if(type != null){": ${type.name}"}else{""}}
    """.trimMargin()
)