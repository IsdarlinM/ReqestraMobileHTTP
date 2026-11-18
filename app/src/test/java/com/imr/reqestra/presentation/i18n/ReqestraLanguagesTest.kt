package com.imr.reqestra.presentation.i18n

import org.junit.Assert.assertEquals
import org.junit.Test

class ReqestraLanguagesTest {
    @Test fun deviceLanguagesAreNormalized() {
        assertEquals("en", ReqestraLanguages.normalize("it-IT"))
        assertEquals("es", ReqestraLanguages.normalize("es-DO"))
        assertEquals("ru", ReqestraLanguages.normalize("ru-RU"))
        assertEquals("zh", ReqestraLanguages.normalize("zh-CN"))
        assertEquals("pt", ReqestraLanguages.normalize("pt-BR"))
        assertEquals("fr", ReqestraLanguages.normalize("fr-FR"))
    }
    @Test fun statusAndValidationMessagesAreTranslatedWithoutChangingParameters() {
        assertEquals("Selected", ReqestraLanguages.translate("Seleccionado", "en"))
        assertEquals("已选中", ReqestraLanguages.translate("Seleccionado", "zh"))
        assertEquals("Создать", ReqestraLanguages.translate("Crear", "ru"))
        assertEquals("Запрос сохранён в Lab.", ReqestraLanguages.translate("Petición guardada en Lab.", "ru"))
        assertEquals("Tests", ReqestraLanguages.translate("Pruebas", "fr"))
        assertEquals("Bearer abc", ReqestraLanguages.translate("Bearer abc", "fr"))
    }
    @Test fun aboutHelpIsAvailableForAllSixLanguages() {
        val translations = mapOf(
            "en" to "About & Help",
            "es" to "Acerca de y ayuda",
            "ru" to "О приложении и помощь",
            "zh" to "关于与帮助",
            "pt" to "Sobre e ajuda",
            "fr" to "À propos et aide",
        )
        for ((language, expected) in translations) {
            assertEquals(expected, ReqestraLanguages.translate("Acerca de y ayuda", language))
            assertEquals(true, ReqestraLanguages.translate("Guía rápida", language).isNotBlank())
            assertEquals(true, ReqestraLanguages.translate("Documentación", language).isNotBlank())
        }
    }
    @Test fun navigationTranslationsAreAvailable() {
        assertEquals("Studio", ReqestraLanguages.translate("Estudio", "en"))
        assertEquals("Estudio", ReqestraLanguages.translate("Estudio", "es"))
        assertEquals("Студия", ReqestraLanguages.translate("Estudio", "ru"))
        assertEquals("工作台", ReqestraLanguages.translate("Estudio", "zh"))
        assertEquals("Estúdio", ReqestraLanguages.translate("Estudio", "pt"))
        assertEquals("Studio", ReqestraLanguages.translate("Estudio", "fr"))
    }
}
