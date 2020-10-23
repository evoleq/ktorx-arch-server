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

import kotlin.reflect.KClass

sealed class TranslationID {
    data class ClassId(val key: KClass<*>) : TranslationID()
    data class IntId(val key: Int): TranslationID()
    data class StringId(val key: String): TranslationID()
}

data class Translations(
    private val translationsClassId: HashMap<TranslationID.ClassId,(Any)->Any> = hashMapOf(),
    private val translationsIntId: HashMap<TranslationID.IntId,(Any)->Any> = hashMapOf(),
    private val translationsStringId: HashMap<TranslationID.StringId,(Any)->Any> = hashMapOf()
) {
    operator fun get(key: TranslationID.ClassId): (Any)->Any = translationsClassId[key]!!
    operator fun get(key: TranslationID.IntId): (Any)->Any = translationsIntId[key]!!
    operator fun get(key: TranslationID.StringId): (Any)->Any = translationsStringId[key]!!
    
    operator fun set(key: TranslationID.ClassId, translation: (Any)->Any) {
        translationsClassId[key] = translation
    }
    operator fun set(key: KClass<*>, translation: (Any)->Any) {
        translationsClassId[TranslationID.ClassId(key)] = translation
    }
    operator fun set(key: TranslationID.IntId, translation: (Any)->Any) {
        translationsIntId[key] = translation
    }
    operator fun set(key: TranslationID.StringId, translation: (Any)->Any) {
        translationsStringId[key] = translation
    }
    
    
}

data class TypedTranslations(
    val translations: HashMap<TranslationID.ClassId, Translations> = hashMapOf()
) {
    operator fun get(type: TranslationID.ClassId): Translations = translations[type]!!
    operator fun get(type: KClass<*>): Translations = translations[TranslationID.ClassId(type)]!!
    operator fun set(type: TranslationID.ClassId, translations: Translations) {
        this.translations[type] = translations
    }
    operator fun set(type: KClass<*>, translations: Translations) {
        this.translations[TranslationID.ClassId(type)] = translations
    }
}

