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

import io.ktor.application.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.util.pipeline.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import org.evoleq.ktorx.marker.KtorxDsl
import org.evoleq.ktorx.response.Response
import org.evoleq.ktorx.response.Serializers
import org.evoleq.ktorx.response.transform
import org.evoleq.math.cat.structure.x
import org.evoleq.math.cat.suspend.monad.result.Result
import org.evoleq.math.cat.suspend.monad.state.KlScopedSuspendedState
import org.evoleq.math.cat.suspend.monad.state.ScopedSuspendedState

typealias Context = PipelineContext<Unit, ApplicationCall>
typealias Action<I, O> = KlScopedSuspendedState<Context, I, O>
typealias ResultAction<I, O> = Action<Result<I,Throwable>,Result<O,Throwable>>

@Suppress("FunctionName")
@KtorxDsl
fun <I,O> Action(arrow: suspend CoroutineScope.(I) -> ScopedSuspendedState<Context,O>): Action<I,O> = KlScopedSuspendedState {
        input -> arrow(input)
}

@Suppress("FunctionName")
@KtorxDsl
fun <I,O> ResultAction(arrow: suspend CoroutineScope.(Result<I,Throwable>) -> ScopedSuspendedState<Context,Result<O,Throwable>>): ResultAction<I, O> = KlScopedSuspendedState {
        input -> arrow(input)
}

typealias ActionReader<I,O> = suspend ()->Action<I,O>

@KtorxDsl
inline fun <reified D : Any> receiveAction(): Action<Unit, Result<D, Throwable>> = KlScopedSuspendedState {
     _->ScopedSuspendedState{
            context -> try{
                val data:D = context.call.receive() //as D
                Result.Success<D, Throwable>(data)
            } catch(throwable : Throwable){
                Result.Failure<D, Throwable>(throwable)
            } x context
    }
}

@KtorxDsl
inline fun <C : Any, reified D : Any> configureAndReceiveAction(): Action<C, Result<Pair<C, D>, Throwable>> = KlScopedSuspendedState {
    config ->ScopedSuspendedState{
        context -> try{
            if(D::class == Unit::class){
                Result.Success<Pair<C, D>, Throwable>((config x Unit as D))
            } else {
                val data: D = context.call.receive() //as D
                Result.Success<Pair<C, D>, Throwable>((config x data))
            }
        } catch(throwable : Throwable){
            Result.Failure<Pair<C, D>, Throwable>(throwable)
        } x context
    }
}


@KtorxDsl
fun <C : Any> configureGetAction(): Action<C, Result<Pair<C, Unit>, Throwable>> = KlScopedSuspendedState {
    config ->ScopedSuspendedState{
        context -> Result.Success<Pair<C, Unit>, Throwable>((config x Unit))x context
    }
}

@KtorxDsl
fun <Data: Any> transformAction(
    failureTransformation: (Throwable)->Pair<String,Int> = { throwable -> (throwable.message?:"No message provided") x 500 }
): Action<Result<Data, Throwable>, Response<Data>> = KlScopedSuspendedState {
    result -> ScopedSuspendedState{
        context -> try{
                result.transform(failureTransformation)
            } catch(throwable: Throwable){
                Response.Failure<Data>(
                    message = throwable.message?:"Throwable@transformAction: No message provided",
                    code = 500
                )
            } x context
    }
}

@KtorxDsl
inline fun<reified Data: Any> returnAction(): Action<Response<Data>, Response<Data>> = KlScopedSuspendedState {
    response -> ScopedSuspendedState { context ->
        try {
            val json = Json{prettyPrint = true}.encodeToString(
                Response.serializer(Serializers[Data::class] as KSerializer<Data>),
                response
            )
            context.call.respond(json)
            response x context
        } catch(exception: Exception) {
            exception.printStackTrace()
            val message = exception.message?:"Exception@returnAction: No message provided"
            val code = 500
            val returnFailure = Response.Failure<Data>(
                message = message,
                code = code
            )
            context.call.respond(
                Json(){prettyPrint = true}.encodeToString(
                    Response.serializer(Int.serializer()),
                    Response.Failure<Int>(
                        message,
                        code
                    )
                )
            )
            returnFailure x context
        }
    }
}

