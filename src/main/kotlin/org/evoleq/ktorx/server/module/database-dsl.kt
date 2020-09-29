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

class DatabaseConfiguration : Configuration<Database> {
    
    @KtorxDsl
    lateinit var type: DatabaseType
    
    @KtorxDsl
    lateinit var name: String
    
    @KtorxDsl
    lateinit var host: String
    
    @KtorxDsl
    var port: Int = 3306
    
    @KtorxDsl
    lateinit var scheme: String
    
    @KtorxDsl
    lateinit var params: String
    
    @KtorxDsl
    lateinit var driver: String
    
    @KtorxDsl
    lateinit var user: String
    
    @KtorxDsl
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