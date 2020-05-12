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

import org.evoleq.ktorx.server.action.Action
import org.evoleq.ktorx.marker.KtorxDsl


data class Apis(private val apis: HashMap<String, Api>): Map<String, Api> by apis

sealed class Api(open val name: String) {
    data class Logical(
        override val name: String,
        private val actions: HashMap<String,Action<*,*>>
    ): Api( name ), Map<String,Action<*,*>> by actions
    data class Physical(
        override val name: String,
        val baseUrl: String,
        val port: Int
    ): Api(name)
}

sealed class ApiRequest(open val route: String){
    data class Delete(override val route: String) : ApiRequest(route)
    data class Get(override val route: String) : ApiRequest(route)
    data class Head(override val route: String) : ApiRequest(route)
    data class Options(override val route: String) : ApiRequest(route)
    data class Post(override val route: String) : ApiRequest(route)
    data class Put(override val route: String) : ApiRequest(route)
    data class Patch(override val route: String) : ApiRequest(route)
}

@KtorxDsl
suspend fun Apis.action(api: String, action: String): Action<*,*> = (this[api]!! as Api.Logical)[action]!!