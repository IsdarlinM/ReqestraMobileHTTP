package com.imr.reqestra.core.variables

/** Sustituciones limitadas a variables explícitas; no evalúa expresiones ni comandos. */
object VariableTemplate {
    private val identifier = Regex("[A-Za-z][A-Za-z0-9_]{0,63}")
    private val placeholder = Regex("""\{\{([^{}]+)\}\}""")
    private const val MAX_RESULT_LENGTH = 6 * 1024 * 1024

    fun requireName(name: String) {
        require(identifier.matches(name)) {
            "Nombre de variable inválido. Usa letras, números y guiones bajos."
        }
    }

    fun resolve(input: String, variables: Map<String, String>): String {
        require(input.length <= MAX_RESULT_LENGTH) { "Plantilla demasiado grande." }
        val expanded = placeholder.replace(input) { match ->
            val name = match.groupValues[1].trim()
            requireName(name)
            variables[name] ?: error("Falta la variable {{$name}} en el entorno activo.")
        }
        require(expanded.length <= MAX_RESULT_LENGTH) { "La petición expandida supera el límite permitido." }
        return expanded
    }
}
