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



import org.jetbrains.kotlin.cli.common.environment.setIdeaIoUseFallback
import org.jetbrains.kotlin.cli.common.repl.KotlinJsr223JvmScriptEngineFactoryBase
import org.jetbrains.kotlin.cli.common.repl.ScriptArgsWithTypes
import org.jetbrains.kotlin.script.jsr223.KotlinJsr223JvmLocalScriptEngine
import org.jetbrains.kotlin.script.jsr223.KotlinStandardJsr223ScriptTemplate
import java.io.File
import java.nio.file.Files
import java.util.stream.Collectors
import javax.script.Bindings
import javax.script.ScriptContext
import javax.script.ScriptEngine
import kotlin.script.experimental.jvm.util.scriptCompilationClasspathFromContextOrStdlib

val template = KotlinStandardJsr223ScriptTemplate::class
var appJarPath = "/app"
/**
 * classpath to be used by script engine.
 * Called in [org.drx.hexarch.application.SetProjectDataActivity]
 */
val scriptingClasspath: List<File>  by lazy{
    setIdeaIoUseFallback()
    val classPathString = ""
    val classpath = Files.list(File(classPathString).toPath())
        .map { it.toFile() }
        .filter{ with(it.name){
            endsWith("kt") ||
                endsWith("java")
        } }
        .collect(Collectors.toCollection { mutableListOf<File>() })
    classpath.addAll(
        scriptCompilationClasspathFromContextOrStdlib(
            "kotlin-stdlib.jar",
            "kotlin-script-util.jar",
            "kotlin-script-runtime.jar",
            "kotlin-stdlib-jdk8.jar",
            "kotlin-compiler-embeddable.jar",
            //"exposed-core-0.20.2.jar",
            //"exposed-dao-0.20.2.jar",
            //"exposed-jdbc-0.20.2.jar",
            wholeClasspath = true
        )
    )
    classpath
}



class ScriptEngineFactory : KotlinJsr223JvmScriptEngineFactoryBase() {
    override fun getScriptEngine(): ScriptEngine {
        
        return KotlinJsr223JvmLocalScriptEngine(
            this,
            scriptingClasspath,
            template.qualifiedName!!,
            { ctx, types ->
                ScriptArgsWithTypes(arrayOf(ctx.getBindings(ScriptContext.ENGINE_SCOPE)), types ?: emptyArray())
            },
            arrayOf(Bindings::class)
        )
    }
}



fun evalScript(script: String): Any?  {
    
    val sM = ScriptEngineFactory()
    val sE = sM.scriptEngine
    
    return with(sE) {
        eval(script)
    }
}

fun evalScript(script: String, args: Map<String, Any?>): Any? {
    var s = script
    args.entries.forEach {
        s = s.replace("\${${it.key}}",when(it.value){
            is String -> "\"${it.value}\""
            else -> if(it.value != null) it.value.toString() else "null"
        })
    }
    return evalScript(s)
}

