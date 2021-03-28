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
package org.evoleq.ktorx.server.module

import org.evoleq.ktorx.marker.KtorxDsl
import org.evoleq.ktorx.server.action.Action

/**
 * data class holding information about APIs in the [Boundary] of the [Application]
 */
data class Apis(private val apis: HashMap<String, Api> = hashMapOf()): Map<String, Api> by apis

/**
 * API-Description
 */
sealed class Api(open val name: String) {
    /**
     * Logical Api Description
     */
    data class Logical(
        override val name: String,
        private val actions: HashMap<String,Action<*,*>>
    ): Api( name ), Map<String,Action<*,*>> by actions
    
    /**
     * Physical Api Description
     */
    data class Physical(
        override val name: String,
        val scheme: String,
        val host: String,
        val port: Int,
        val requests: ArrayList<ApiRequest>,
        val contextToRoutesMap: HashMap<String, HashSet<String>>
    ): Api(name), List<ApiRequest> by requests
}

sealed class ApiRequest(open val name: String,open val route: String){
    data class Delete(override val name: String,override val route: String) : ApiRequest(name,route)
    data class Get(override val name: String,override val route: String) : ApiRequest(name,route)
    data class Head(override val name: String,override val route: String) : ApiRequest(name,route)
    data class Options(override val name: String,override val route: String) : ApiRequest(name,route)
    data class Post(override val name: String,override val route: String) : ApiRequest(name,route)
    data class Put(override val name: String,override val route: String) : ApiRequest(name,route)
    data class Patch(override val name: String,override val route: String) : ApiRequest(name,route)
}

@KtorxDsl
suspend fun Apis.action(api: String, action: String): Action<*,*> = (this[api]!! as Api.Logical)[action]!!