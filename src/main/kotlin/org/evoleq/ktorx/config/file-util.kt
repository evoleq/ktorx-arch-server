package org.evoleq.ktorx.config

import java.io.File

/**
 * List all files contained in a directory recursively.
 */
fun File.files(): ArrayList<File> = when{
    isFile -> arrayListOf(this)
    else -> {
        val result = arrayListOf<File>()
        listFiles()?.forEach {
            if(it.isFile) {
                result.add(it)
            } else {
                result.addAll(
                    it.files()
                )
            }
        }
        result
    }
}