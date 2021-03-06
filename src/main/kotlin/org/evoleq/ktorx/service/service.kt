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
package org.evoleq.ktorx.service

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import org.evoleq.math.cat.marker.MathSpeakDsl
import org.evoleq.math.cat.suspend.monad.result.Result
import org.evoleq.math.cat.suspend.monad.result.failT
import org.evoleq.math.cat.suspend.monad.result.retT
import org.evoleq.math.cat.suspend.monad.state.KlScopedSuspendedState
import org.evoleq.math.cat.suspend.monad.state.ScopedSuspendedState
import org.evoleq.math.cat.suspend.morphism.ScopedSuspended
import org.evoleq.math.cat.suspend.morphism.by


typealias Service<I,O> = ScopedSuspended<I, O>
typealias ResultService<I,O> = Service<Result<I, Throwable>, Result<O, Throwable>>
typealias KlService<C, I, O> = ScopedSuspended<C, Service<I, O>>

//typealias klService<S,I,O>
typealias ServiceState<I,O,T> = ScopedSuspendedState<Service<I, O>,T>
typealias KlServiceState<K,I,O,T> = KlScopedSuspendedState<Service<I, O>, K, T>


fun <I,O,T> ServiceState(
    state: (Service<I, O>)->Pair<T, Service<I, O>>
): ServiceState<I, O, T> =
    ScopedSuspendedState{
        service -> state(service)
    }

@MathSpeakDsl
suspend fun <I,O> apply(service: ScopedSuspended<I,O>): suspend CoroutineScope.(I)->O = by(service)
@MathSpeakDsl
suspend infix fun <I,O> (suspend CoroutineScope.(I)->O).to(input: I): O = coroutineScope{this@to(input)}

@MathSpeakDsl
fun <I, O> Service<I, O>.onResults(): ResultService<I, O> = ScopedSuspended {
    input -> input.bind {
        try{
            Result.retT<O>(apply(this@onResults) to it)
        } catch(throwable : Throwable) {
            Result.failT<O>(throwable)
        }
    }
}