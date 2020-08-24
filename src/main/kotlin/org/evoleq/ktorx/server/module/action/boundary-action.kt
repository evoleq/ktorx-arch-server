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
package org.evoleq.ktorx.server.module.action

import kotlinx.coroutines.CoroutineScope
import org.evoleq.ktorx.marker.KtorxDsl
import org.evoleq.ktorx.server.module.Boundary
import org.evoleq.math.cat.suspend.monad.result.ResultT
import org.evoleq.math.cat.suspend.monad.state.KlScopedSuspendedState
import org.evoleq.math.cat.suspend.monad.state.ScopedSuspendedState

typealias BoundaryAction<I,O> = KlScopedSuspendedState<Boundary, I, O>
typealias BoundaryResultAction<I,O> = BoundaryAction<ResultT<I>, ResultT<O>>

@KtorxDsl
@Suppress("FunctionName")
fun <I,O> BoundaryAction(arrow: suspend CoroutineScope.(I)->ScopedSuspendedState<Boundary, O>): BoundaryAction<I, O> =
    KlScopedSuspendedState(arrow)

@KtorxDsl
@Suppress("FunctionName")
fun <I,O> BoundaryResultAction(arrow: suspend CoroutineScope.(ResultT<I>)->ScopedSuspendedState<Boundary, ResultT<O>>): BoundaryResultAction<I, O> =
    BoundaryAction(arrow)