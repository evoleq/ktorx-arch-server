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

import org.drx.configuration.Configuration
import org.evoleq.ktorx.marker.KtorxDsl
import kotlin.reflect.KClass

open class BoundaryConfiguration : Configuration<Boundary> {
    @KtorxDsl
    val isos: HashMap<KClass<*>, Isomorphism<*, *>> = hashMapOf()
    
    @KtorxDsl
    val apis: HashMap<String, Api> = hashMapOf()
    
    @KtorxDsl
    val databases: HashMap<String, Database> = hashMapOf()
    
    @KtorxDsl
    val translations: TypedTranslations = TypedTranslations()
    
    override fun configure(): Boundary = with(Boundary(
        Apis(apis),
        Isomorphisms(isos),
        Databases(databases),
        translations
    )) {
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
        this
    }
    /*
    @KtorxDsl
    infix fun <Data1: Any, Data2: Any> KClass<Data1>.iso(other: KClass<Data2>) {
        isos[this] = this isomorphic  other
    }

     */
    @KtorxDsl
    inline infix fun <reified Data1: Any, Data2: Any> Transformation<Data1, Data2>.inverts(other: Transformation<Data2, Data1>) {
        isos[Data1::class] = Isomorphism(this,other)
    }
    @KtorxDsl
    fun api(key: String, api: ()-> Api) {
        apis[key] = api()
    }
    
    @KtorxDsl
    fun apis(configuration: HashMap<String, Api>.()->Unit) {
        apis.configuration()
    }
    @KtorxDsl
    fun HashMap<String, Api>.physical(key: String,configuration: PhysicalApiConfiguration.()->Unit) =
        with(PhysicalApiConfiguration()) {
            configuration()
            this@physical[key] = configure()
        }
    
    @KtorxDsl
    fun HashMap<String, Api>.logical(key: String,configuration: LogicalApiConfiguration.()->Unit) =
        with(LogicalApiConfiguration()) {
            configuration()
            this@logical[key] = configure()
        }
    
    @KtorxDsl
    fun database(key: String, configuration: DatabaseConfiguration.()->Unit) = with(DatabaseConfiguration()){
        configuration()
        databases[key] = configure()
    }
    
    @KtorxDsl
    fun databases(configuration: HashMap<String, Database>.()->Unit) {
        databases.configuration()
    }
    
    @KtorxDsl
    fun HashMap<String, Database>.database(key: String, configuration: DatabaseConfiguration.()->Unit) = with(DatabaseConfiguration()){
        configuration()
        this@database[key] = configure()
    }
    
    @KtorxDsl
    inline fun <reified Type> translations(noinline translations: Translations.()->Unit) =
        with(Translations()) {
            translations()
            this@BoundaryConfiguration.translations[Type::class] = this
        }
    
    
}

@KtorxDsl
fun boundary(configuration: BoundaryConfiguration.()->Unit): Boundary = with(BoundaryConfiguration()) {
    configuration()
    configure()
}