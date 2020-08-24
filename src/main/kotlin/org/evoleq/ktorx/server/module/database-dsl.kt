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

class DatabaseConfiguration : Configuration<Database> {
    lateinit var type: DatabaseType
    lateinit var name: String
    lateinit var host: String
    var port: Int = 3306
    lateinit var scheme: String
    lateinit var params: String
    lateinit var driver: String
    lateinit var user: String
    lateinit var password: String
    
    override fun configure(): Database = Database(
        type,
        name,
        host,
        port,
        scheme,
        params,
        driver,
        user,
        password
    )
}