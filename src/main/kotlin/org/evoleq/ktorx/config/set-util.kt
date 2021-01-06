package org.evoleq.ktorx.config

fun <T> HashSet<T>.addAllFluent(set: HashSet<T>): HashSet<T> {
    addAll(set)
    return this
}