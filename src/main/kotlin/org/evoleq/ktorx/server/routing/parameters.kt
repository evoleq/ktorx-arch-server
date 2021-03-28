package org.evoleq.ktorx.server.routing

import io.ktor.application.*
/*
import org.evoleq.ktorx.server.action.Action
import org.evoleq.math.cat.structure.x
import org.evoleq.math.cat.suspend.monad.result.ResultT
import org.evoleq.math.cat.suspend.monad.result.retT
import org.evoleq.math.cat.suspend.monad.state.ScopedSuspendedState
*/
abstract class ParameterReader<Params> {
    abstract val readFrom: suspend (ApplicationCall) -> Params
}

/* TODO think about it
suspend fun <Input, Params> ParameterReader<Params>.toAction(): Action<ResultT<Input>, ResultT<Pair<Params, Input> >> = Action {
    result -> ScopedSuspendedState { context -> result bind {
            ResultT.retT(readFrom(context.call) x it)
        } x context
    }
}

 */