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

import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import org.drx.configuration.Configuration
import org.evoleq.ktorx.marker.KtorxDsl
import org.evoleq.ktorx.response.Serializers
import kotlin.reflect.KClass

data class Boundary(
    val apis: Apis,
    val isos: Isomorphisms
)

open class BoundaryConfiguration : Configuration<Boundary> {
    val isos: HashMap<KClass<*>, Isomorphism<*, *>> = hashMapOf()
    val apis: HashMap<String, Api> = hashMapOf()

    override fun configure(): Boundary = Boundary(
        Apis(apis),
        Isomorphisms(isos)
    )
    @KtorxDsl
    infix fun <Data1: Any, Data2: Any> KClass<Data1>.iso(other: KClass<Data2>) {
        isos[this] = this isomorphic  other
    }
    @KtorxDsl
    inline infix fun <reified Data1: Any, Data2: Any> Transformation<Data1, Data2>.inverts(other: Transformation<Data2, Data1>) {
        isos[Data1::class] = Isomorphism(this,other)
    }
}
@KtorxDsl
fun boundary(configuration: BoundaryConfiguration.()->Unit): Boundary = with(BoundaryConfiguration()) {
    configuration()
    configure()
}
@KtorxDsl
data class Transformation<Data1,Data2>(val transformation: (Data1)->Data2): (Data1)->Data2 by transformation
@KtorxDsl
data class Isomorphisms(private val isos: HashMap<KClass<*>, Isomorphism<*, *>>): Map<KClass<*>, Isomorphism<*, *>> by isos
@KtorxDsl
data class Isomorphism<Data1,Data2>(val push: Transformation<Data1, Data2>, val pull: Transformation<Data2, Data1>): (Data1)->Data2 by push
@KtorxDsl
fun <Data1, Data2> Isomorphism<Data1, Data2>.invert(): Isomorphism<Data2, Data1> = Isomorphism(pull,push)
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