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

data class Database(
    val type: DatabaseType,
    val name: String,
    val host: String,
    val port: Int,
    val scheme: String,
    val params: String,
    val driver: String,
    val user: String,
    val password: String
)

data class Databases(
    private val databases: HashMap<String,Database>
) : Map<String,Database> by databases

sealed class DatabaseType {
    object H2 : DatabaseType()
    object  MySql : DatabaseType()
    object MariaDb : DatabaseType()
}

fun Database.url(): String = "$scheme://$host:$port/$name$params".replace(
    "//~:$port",
    "~"
).replace("//mem:$port/","mem:")