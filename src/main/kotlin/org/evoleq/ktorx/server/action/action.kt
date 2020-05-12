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

import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.serialization.DefaultJsonConfiguration
import io.ktor.util.pipeline.PipelineContext
import kotlinx.serialization.*
import kotlinx.serialization.json.Json
import org.drx.evoleq.type.KlScopedSuspendedState
import org.drx.evoleq.type.ScopedSuspendedState
import org.evoleq.ktorx.response.Response
import org.evoleq.ktorx.marker.KtorxDsl
import org.evoleq.ktorx.response.transform
import org.evoleq.ktorx.response.Serializers
import org.evoleq.math.cat.suspend.monad.result.Result

typealias Context = PipelineContext<Unit, ApplicationCall>
typealias Action<I, O> = KlScopedSuspendedState<Context, I, O>

@Suppress("FunctionName")
@KtorxDsl
fun <I,O> Action(arrow: (I) -> ScopedSuspendedState<Context,O>): Action<I,O> = KlScopedSuspendedState {
        input -> arrow(input)
}

@Suppress("FunctionName")
@KtorxDsl
fun <I,O> ResultAction(arrow: (Result<I,Throwable>) -> ScopedSuspendedState<Context,Result<O,Throwable>>): Action<Result<I,Throwable>,Result<O,Throwable>> = KlScopedSuspendedState {
        input -> arrow(input)
}

typealias ActionReader<I,O> = suspend ()->Action<I,O>

//fun<I,O> Action(read:  suspend ()->Action<I,O>): ActionReader<I,O> = read
@KtorxDsl
inline fun <reified D : Any> receiveAction(): Action<Unit, Result<D, Throwable>> = KlScopedSuspendedState {
     _->ScopedSuspendedState{context ->
        Pair(
            try{
                val data:D = context.call.receive() //as D
                Result.Success<D, Throwable>(data)
            } catch(throwable : Throwable){
                Result.Failure<D, Throwable>(throwable)
            },
            context
        )
    }
}

@KtorxDsl
fun <Data: Any> transformAction(failureTransformation: (Throwable)->Pair<String,Int>): Action<Result<Data, Throwable>, Response<Data>> = KlScopedSuspendedState {
        result -> ScopedSuspendedState{context -> Pair(
                result.transform(failureTransformation),
                context
            )}
        }

@KtorxDsl
inline fun<reified Data: Any> returnAction(): Action<Response<Data>, Response<Data>> = KlScopedSuspendedState { response ->
    ScopedSuspendedState { context ->
        val json = Json(DefaultJsonConfiguration.copy(prettyPrint = true))
            .stringify(
                Response.serializer(Serializers[Data::class] as KSerializer<Data>),
                response
            )
        context.call.respond(json)
        Pair(
            response,
            context
        )
    }
}
