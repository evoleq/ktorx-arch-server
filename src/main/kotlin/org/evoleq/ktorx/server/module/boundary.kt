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
import org.evoleq.math.cat.structure.x
import kotlin.reflect.KClass

data class Boundary(
    val apis: Apis,
    val isos: Isomorphisms,
    val databases: Databases,
    val translations: TypedTranslations
) {
    internal val contextToRoutes: HashMap<String, HashSet<String>> by lazy {
        hashMapOf()
    }
    /*
    init {
        apis.values.filterIsInstance<Api.Physical>().forEach {
            it.contextToRoutesMap.entries.forEach { entry ->
                val context = entry.key
                with(contextToRoutes[context]){
                    when(this) {
                        null -> contextToRoutes[context] = entry.value
                        else -> contextToRoutes[context]!!.addAll(entry.value)
                    }
                }
            }
        }
    }
    
     */
    
    
    
    fun addRouteToContext(context: String, route: String) {
        with(contextToRoutes[context]){
            when(this) {
                null -> contextToRoutes[context] = hashSetOf(route)
                else -> contextToRoutes[context]!!.add(route)
            }
        }
    }
    
    fun displayContextToRoutesMap() {
        with(contextToRoutes.entries) {
            forEach {
                println(
                    """
                |context = ${it.key}
                |    ${it.value.joinToString("\n    ") { r -> r }}
            """.trimMargin()
                )
            }
        }
    }
    
    @KtorxDsl
    infix fun String.isInContext(context: String): Boolean =
        with(contextToRoutes[context]){
            when(this) {
                null -> false
                else -> fold(false) {
                    acc, route -> acc || this@isInContext matchesAsRoute route
                }
            }
        }
        /*
        apis.values.filterIsInstance<Api.Physical>().fold(false){
                acc, api: Api.Physical -> acc || with(api.contextToRoutesMap[context]){
            when(this) {
                null -> false
                else -> api.contextToRoutesMap[context]!!.fold(false){
                        acc, route -> acc || this@isInContext matchesAsRoute route
                }
            }
        }
        }
        
         */
    
}


@KtorxDsl
data class Transformation<Data1,Data2>(val transformation: (Data1)->Data2): (Data1)->Data2 by transformation
@KtorxDsl
data class Isomorphisms(private val isos: HashMap<KClass<*>, Isomorphism<*, *>>): Map<KClass<*>, Isomorphism<*, *>> by isos
@KtorxDsl
data class Isomorphism<Data1,Data2>(val push: Transformation<Data1, Data2>, val pull: Transformation<Data2, Data1>): (Data1)->Data2 by push
@KtorxDsl
fun <Data1, Data2> Isomorphism<Data1, Data2>.invert(): Isomorphism<Data2, Data1> = Isomorphism(pull,push)
/*
@KtorxDsl
infix fun <Data1: Any, Data2: Any> KClass<Data1>.isomorphic(other: KClass<Data2>): Isomorphism<Data1, Data2> = Isomorphism(
    Transformation { data1: Data1 ->
        Json(JsonConfiguration.Default).parse(
            Serializers[this] as KSerializer<Data2>,
            Json(JsonConfiguration.Default).stringify(
                Serializers[other] as KSerializer<Data1>,
                data1
            )
        )
    },
    Transformation {data2: Data2 ->
        Json(JsonConfiguration.Default).parse(
            Serializers[other] as KSerializer<Data1>,
            Json(JsonConfiguration.Default).stringify(
                Serializers[this] as KSerializer<Data2>,
                data2
            )
        )
    }

)

@KtorxDsl
inline fun <reified Data1,reified Data2> Isomorphism(): Isomorphism<Data1, Data2> = Isomorphism(
    Transformation { data1: Data1 ->
        Json(JsonConfiguration.Default).parse(
            Serializers[Data2::class] as KSerializer<Data2>,
            Json(JsonConfiguration.Default).stringify(
                Serializers[Data1::class] as KSerializer<Data1>,
                data1
            )
        )
    },
    Transformation {data2: Data2 ->
        Json(JsonConfiguration.Default).parse(
            Serializers[Data2::class] as KSerializer<Data1>,
            Json(JsonConfiguration.Default).stringify(
                Serializers[Data1::class] as KSerializer<Data2>,
                data2
            )
        )
    }

)
*/
@KtorxDsl
fun Boundary.findApiByUrl(url: String): Api.Physical = with(apis.values.filterIsInstance<Api.Physical>()){
    find {
        url.startsWith("/${it.name}") ||
            it.find{ request -> url.startsWith("/${request.name}") } != null
    }!!
}
@KtorxDsl
fun Api.Physical.baseUrl() = "$scheme://$host:$port"

@KtorxDsl
fun String.fixRoute() = when(startsWith("/")) {
    true -> this
    else -> "/$this"
}

@KtorxDsl
fun Boundary.match(url: String): String = findApiByUrl(url).baseUrl()

@KtorxDsl
fun Boundary.route(apiName: String, routeName: String): String = with(
    apis.filterValues { it is Api.Physical }[apiName] as Api.Physical
) {
    baseUrl() + "/$apiName" +
    requests.find{
       it.name == routeName
    }?.route!!.fixRoute()
}
/*
data class Isomorphism<in Data1 : Any,out  Data2 : Any>(val klazz1: KClass<in Data1>, val klazz2: KClass<out Data2>) {
    fun pull(data2: Data2): Data1 =
        Json(JsonConfiguration.Default).parse(
            Serializers[klazz1] as KSerializer<Data1>,
            Json(JsonConfiguration.Default).stringify(
                Serializers[klazz2] as KSerializer<Data2>,
                data2
            )
        )
    fun push(data2: Data1): Data2 =
        Json(JsonConfiguration.Default).parse(
            Serializers[klazz2] as KSerializer<Data2>,
            Json(JsonConfiguration.Default).stringify(
                Serializers[klazz1] as KSerializer<Data1>,
                data2
            )
        )

}

 */
/*
data class Isomorphisms(val isos: HashMap<KClass<*>, Isomorphism<*,*>>) {
    inline fun<reified Data1, Data2> pull(): (Data2)->Data1 = {data2 -> isos[Data1::class]!!.pull(data2) as Data1}
    inline fun<reified Data1, Data2> push(): (Data1)->Data2 = {data1: Data1 -> isos[Data1::class]!!.push(data1) as Data2}
}

 */

//inline fun <reified Data1,reified Data2> Isomorphism(): Isomorphism<Data1,Data2> =
/**
 * Set parameters of a route
 */
@KtorxDsl
fun String.setParameters(parameters: HashMap<String, String>): String {
    var tmp = this
    parameters.forEach { (key, value) ->
        tmp = tmp.replace("{$key}", value)
    }
    return tmp
}

infix fun String.matchesAsRoute(other: String): Boolean {
    val l1 = arrayListOf(*split("/").filter{it != ""}.toTypedArray())
    val l2 = arrayListOf(*other.split("/").filter{it != ""}.toTypedArray())
    if(l1.size != l2.size){
        return false
    }
    return l1.mapIndexed { index, item ->
        item x l2[index]
    }.fold(true) {
        acc, pair -> acc && if(
            pair.first.startsWith("{") ||
            pair.second.startsWith("{")
        ) {
            true
        } else {
            pair.first == pair.second
        }
    }
}