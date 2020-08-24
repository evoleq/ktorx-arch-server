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
import org.evoleq.math.cat.structure.x
import org.evoleq.math.cat.suspend.monad.state.ScopedSuspendedState
import kotlin.test.Test

class BoundaryTest {
    @Test fun `configure boundary`() {
        val boundary = boundary {
            apis{
                physical("api_one") {
                    name = "api_one"
                    host = "localhost"
                    port = 80
                    scheme = "http"
                    requests {
                        get("home", "/")
                        get("user", "/user")
                    }
                }
                logical("api_two") {
                    name = "api_two"
                    actions {
                        action("action_1", Action<Unit,Unit>{ ScopedSuspendedState {context -> Unit x context }})
                    }
                }
            }
            
            databases {
                database("db_one") {
                    type = DatabaseType.MySql
                    name = "db_one"
                    host = "one_db"
                    port = 3306
                    scheme = "jdbc:mysql"
                    params = "?useSSL=false"
                    driver = "com.mysql.jdbc.Driver"
                    user = "user"
                    password = "password"
                }
                database("db_two") {
                    type = DatabaseType.MySql
                    name = "db_two"
                    host = "two_db"
                    port = 3306
                    scheme = "jdbc:mysql"
                    params = "?useSSL=false"
                    driver = "com.mysql.jdbc.Driver"
                    user = "user"
                    password = "password"
                }
            }
        }
        
        assert(boundary.apis.size == 2)
        val physicalApi = boundary.apis["api_one"]!!
        assert(physicalApi is Api.Physical)
        with(physicalApi as Api.Physical) {
            assert(requests.size == 2)
        }
        
        val logicalApi = boundary.apis["api_two"]!!
        assert(logicalApi is Api.Logical)
        with(logicalApi as Api.Logical) {
            assert(logicalApi.size == 1)
        }
        assert(boundary.databases.size == 2)
    }
}