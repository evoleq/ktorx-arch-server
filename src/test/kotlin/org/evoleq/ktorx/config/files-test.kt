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