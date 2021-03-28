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

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.drx.configuration.Configuration
import org.evoleq.ktorx.marker.KtorxDsl
import org.evoleq.ktorx.server.action.Action


class LogicalApiConfiguration : Configuration<Api> {
    
    @KtorxDsl
    lateinit var name: String
    
    @KtorxDsl
    private val actions: HashMap<String,Action<*,*>> by lazy { hashMapOf<String, Action<*,* >> ()}
    
    override fun configure(): Api = Api.Logical(name, actions)
    
    @KtorxDsl
    fun actions(configuration: HashMap<String, Action<*,*>>.()->Unit) {
        actions.configuration()
    }
    
    @KtorxDsl
    fun HashMap<String, Action<*,*>>.action(key: String, action: Action<*,*>) {
        this[key] = action
    }
    
    @KtorxDsl
    fun HashMap<String, Action<*,*>>.action(key: String, action: suspend ()-> Action<*,*>) {
        GlobalScope.launch {
            this@action[key] = action()
        }
    }
}

class PhysicalApiConfiguration : Configuration<Api> {
    
    @KtorxDsl
    lateinit var name: String
    
    @KtorxDsl
    lateinit var scheme: String
    
    @KtorxDsl
    lateinit var host: String
    
    @KtorxDsl
    var port: Int = 80
    
    @KtorxDsl
    private val requests: ArrayList<ApiRequest> by lazy { arrayListOf<ApiRequest>()}
    
    @KtorxDsl
    private val contextToRoutesMap: HashMap<String, HashSet<String>> by lazy { hashMapOf() }
    
    override fun configure(): Api = Api.Physical(
        name,
        scheme,
        host,
        port,
        requests,
        contextToRoutesMap
    )
    
    @KtorxDsl
    fun requests(configuration: ArrayList<ApiRequest>.()->Unit) {
        requests.configuration()
    }
    
    @KtorxDsl
    fun ArrayList<ApiRequest>.post(name: String, route: String) {
        add(ApiRequest.Post(name,route))
    }
    
    @KtorxDsl
    fun ArrayList<ApiRequest>.put(name: String, route: String) {
        add(ApiRequest.Put(name,route))
    }
    
    @KtorxDsl
    fun ArrayList<ApiRequest>.patch(name: String, route: String) {
        add(ApiRequest.Patch(name,route))
    }
    
    @KtorxDsl
    fun ArrayList<ApiRequest>.delete(name: String, route: String) {
        add(ApiRequest.Delete(name,route))
    }
    
    @KtorxDsl
    fun ArrayList<ApiRequest>.get(name: String, route: String) {
        add(ApiRequest.Get(name,route))
    }
    
    @KtorxDsl
    fun ArrayList<ApiRequest>.head(name: String, route: String) {
        add(ApiRequest.Head(name,route))
    }
    
    @KtorxDsl
    fun ArrayList<ApiRequest>.options(name: String, route: String) {
        add(ApiRequest.Options(name,route))
    }
    
    @KtorxDsl
    fun contexts(vararg contexts: String, requests: ArrayList<ApiRequest>.()->Unit) {
        with(arrayListOf<ApiRequest>()) {
            requests()
            val routes = map{ request -> "/${this@PhysicalApiConfiguration.name}${ request.route }"}.toHashSet()
            contexts.forEach {
                context -> when(contextToRoutesMap[context]) {
                    null -> contextToRoutesMap[context] = routes
                    else -> contextToRoutesMap[context]!!.addAll(routes)
                }
            }
            this@PhysicalApiConfiguration.requests.addAll(this)
        }
    }
    
}

@KtorxDsl
fun apis(configuration: BoundaryConfiguration.()->Unit): Apis = with(BoundaryConfiguration()) {
    configuration()
    Apis(apis)
}