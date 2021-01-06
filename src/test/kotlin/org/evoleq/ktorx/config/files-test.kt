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

import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals

class FilesTest {
    @Test
    fun testFiles() {
        val timestamp = System.currentTimeMillis()
        val file = File("/tmp/test_$timestamp")
        val dir1 = File(file, "dir1")
        val dir2 = File(file, "dir2")
        val f1_1 = File(dir1,"1")
        val f1_2 = File(dir1,"2")
        val f1_3 = File(dir1,"3")
        val f2_1 = File(dir2,"1")
        val f2_2 = File(dir2,"2")
        val f2_3 = File(dir2,"3")
        val f = File(file, "1")
    
        file.mkdir()
        dir1.mkdirs()
        dir2.mkdirs()
        f1_1.createNewFile()
        f1_2.createNewFile()
        f1_3.createNewFile()
        f2_1.createNewFile()
        f2_2.createNewFile()
        f2_3.createNewFile()
        f.createNewFile()
        val list = file.files().map{it.name}
        println(list)
        assertEquals(7,list.size)
        file.deleteRecursively()
    }
}