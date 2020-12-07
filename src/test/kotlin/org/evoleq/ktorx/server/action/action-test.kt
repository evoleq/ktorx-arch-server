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
package org.evoleq.ktorx.server.action

import io.ktor.server.testing.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.builtins.serializer
import org.evoleq.ktorx.response.Response
import org.evoleq.ktorx.server.module.serializers
import org.evoleq.math.cat.structure.x
import org.evoleq.math.cat.suspend.monad.result.Result
import org.evoleq.math.cat.suspend.monad.result.failT
import org.evoleq.math.cat.suspend.monad.result.retT
import org.evoleq.math.cat.suspend.monad.state.ScopedSuspendedState
import kotlin.test.Test
import kotlin.test.assertEquals

class ActionTest {
    @Test
    fun `result action` () = runBlocking{
        val resultAction = ResultAction<Int, String> { result ->
            ScopedSuspendedState { context ->
                result map {
                    "$it"
                } x context
            }
        }
        val resultSuccess = withActionTestContext {
            respondBy(resultAction) on Result.retT(0)
        }.await().first
        require(resultSuccess is Result.Success)
    }
    
    @Test
    fun `return action`() = runBlocking {
        val response = withActionTestContext {
            serializers {
                Boolean::class with Boolean.serializer()
            }
            respondBy(returnAction<Boolean>()) on Response.Success(true)
        }.await().first
        
        require(response is Response.Success)
        assertEquals(true,response.data)
    }
    
    @Test
    fun `receive action`() = runBlocking {
        val expected = "test"
        val result = withActionTestContext({
            createCall {
                setBody(expected)
            }
        }) {
            by(receiveAction<String>())
        }.await().first
        require(result is Result.Success)
        assertEquals(expected,result.value)
    }
    
    @Test
    fun `configure and receive`() = runBlocking {
        val expected = Pair(1, "2")
        val result = withActionTestContext({
            createCall{
                setBody("2")
            }
        }) {
            respondBy(configureAndReceiveAction<Int,String>()) on 1
        }.await().first
        require(result is Result.Success)
        assertEquals(expected, result.value)
    }
    
    @Test
    fun `transform with success`() = runBlocking {
        val expected = Response.Success(Unit)
        val result = withActionTestContext {
            respondBy(transformAction<Unit> { Pair("",0) }) on Result.ret(Unit)
        }.await().first
        require(result is Response.Success)
        assertEquals(expected, result)
    }
    
    @Test
    fun `transform with failure` () = runBlocking {
        val expected = Response.Failure<Unit>(
            "",
            0
        )
        val result = withActionTestContext {
            respondBy(transformAction<Unit> { Pair("",0) }) on Result.failT<Unit>(Exception())
        }.await().first
        require(result is Response.Failure)
        assertEquals(expected, result)
    }
}
