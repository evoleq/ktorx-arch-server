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
package org.evoleq.ktorx.server.module

import org.evoleq.ktorx.marker.KtorxDsl

@KtorxDsl
inline fun <reified Type> TypedTranslations.translations(noinline translations: Translations.()->Unit) =
    with(Translations()) {
        translations()
        this@translations[Type::class] = this
    }

inline fun <reified Type> TypedTranslations.translations() = this[Type::class]

@KtorxDsl
@Suppress("unchecked_cast")
inline fun <reified Name, S: Any, T: Any> Translations.translate(noinline translation: (S)->T) {
    this@translate[Name::class] = translation as (Any)->Any
}

@KtorxDsl
@Suppress("unchecked_cast")
inline fun <reified Name, S: Any, T: Any> Translations.translate(): (S)->T {
    return this@translate[TranslationID.ClassId(Name::class)] as (S)->T
}

@KtorxDsl
@Suppress("unchecked_cast")
fun < S: Any, T: Any> Translations.translate(name: String, translation: (S)->T) {
    this@translate[TranslationID.StringId(name)] = translation as (Any)->Any
}

@KtorxDsl
@Suppress("unchecked_cast")
fun < S: Any, T: Any> Translations.translate(name: String): (S)->T {
    return this@translate[TranslationID.StringId(name)] as (S)->T
}

@KtorxDsl
@Suppress("unchecked_cast")
fun < S: Any, T: Any> Translations.translate(name: Int, translation: (S)->T) {
    this@translate[TranslationID.IntId(name)] = translation as (Any)->Any
}

@KtorxDsl
@Suppress("unchecked_cast")
fun < S: Any, T: Any> Translations.translate(name: Int): (S)->T {
    return this@translate[TranslationID.IntId(name)] as (S)->T
}

inline fun <reified Type> Boundary.translations(): Translations = translations[Type::class]

inline fun <reified Type, reified Name, S:Any, T:Any> Boundary.translate(): (S)->T =
    with(translations<Type>()){
        translate<Name, S, T>()
    }

fun translations(configuration: Translations.()->Unit): Translations = with(Translations()) {
    configuration()
    this
}

