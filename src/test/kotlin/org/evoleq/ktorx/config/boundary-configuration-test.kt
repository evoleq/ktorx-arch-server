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
package org.evoleq.ktorx.config

import org.evoleq.ktorx.server.module.boundary
import java.io.File
import kotlin.test.Test
import kotlin.test.assertTrue

class BoundaryConfigurationTest {
    
    fun loadFile(name: String):File {
        val resouceFolder = File(BoundaryConfigurationTest::class.java.getResource(".").path.replace(
            "/out/test/classes/",
            "/src/test/resources/"
        ))
        return File(resouceFolder, name)
    }
    
    @Test
    fun readConfigFromFiles() {
        
        val file: File = File("/tmp/test")
        file.writeText("import java.io.File\n" +
            "\n" +
            "apis{\n" +
            "    physical(\"users\") {\n" +
            "        name = \"users\"\n" +
            "        scheme = \"http\"\n" +
            "        host = \"management_server\"\n" +
            "        port = 8081\n" +
            "        requests {\n" +
            "            post(\"login\", \"/login\")\n" +
            "            delete(\"logout\",\"/logout\")\n" +
            "            post(\"register\", \"/register\")\n" +
            "            post(\"confirm-registration\", \"/confirm-registration\")\n" +
            "            post(\"set-password\", \"/set-password\")\n" +
            "            get(\"id\",\"/id\")\n" +
            "            get(\"profile/get\",\"/profile/get\")\n" +
            "            post(\"profile/post\",\"/profile/post\")\n" +
            "            patch(\"profile/patch\",\"/profile/patch\")\n" +
            "        }\n" +
            "    }\n" +
            "    physical(\"authenticator\") {\n" +
            "        name = \"authenticator\"\n" +
            "        scheme = \"http\"\n" +
            "        host = \"management_server\"\n" +
            "        port = 8081\n" +
            "        requests {\n" +
            "            post(\"authenticate\", \"/authenticate\")\n" +
            "        }\n" +
            "    }\n" +
            "    physical(\"chat/socket\") {\n" +
            "        name = \"chat/socket\"\n" +
            "        scheme = \"ws\"\n" +
            "        host = \"chat_socket\"\n" +
            "        port = 8081\n" +
            "        requests {\n" +
            "            get(\"/\", \"/\")\n" +
            "        }\n" +
            "    }\n" +
            "    physical(\"chat/api\"){\n" +
            "        name = \"chat/api\"\n" +
            "        scheme = \"http\"\n" +
            "        host = \"chat_api\"\n" +
            "        port = 8081\n" +
            "        requests {\n" +
            "            post(\n" +
            "                name = \"add-member-to-chat-group\",\n" +
            "                route = \"/chat-groups/add-member\"\n" +
            "            )\n" +
            "        }\n" +
            "    }\n" +
            "    physical(\"play\") {\n" +
            "        name = \"play\"\n" +
            "        scheme = \"http\"\n" +
            "        host = \"management_server\"\n" +
            "        port = 8081\n" +
            "        requests {\n" +
            "            get(\"success\", \"/success\")\n" +
            "            get(\"fail\", \"/fail\")\n" +
            "        }\n" +
            "    }\n" +
            "}")
        
        
        val translations = """
            |    translations<Any>{
            |    }
        """.trimMargin()
        val file2 = File("/tmp/test2")
        file2.writeText(translations)
        val boundaryConfiguration = boundaryConfiguration(defaultImports, file, file2)
        
        val boundary = boundary(boundaryConfiguration)
        
        println(boundary)
        
        
        assertTrue(file != null)
    }
}