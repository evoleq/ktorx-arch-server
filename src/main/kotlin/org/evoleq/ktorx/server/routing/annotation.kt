package org.evoleq.ktorx.server.routing

import org.evoleq.ktorx.marker.KtorxDsl

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.EXPRESSION)
@KtorxDsl
annotation class Action<Params,In,Out>(val hint: String = "")