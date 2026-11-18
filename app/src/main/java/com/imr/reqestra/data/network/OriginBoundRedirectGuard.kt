package com.imr.reqestra.data.network

import java.io.IOException
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Response

/** Protege contra fuga de cookies, headers o cuerpos en redirects entre orígenes. */
internal class OriginBoundRedirectGuard(private val initialUrl: HttpUrl) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val destination = chain.request().url
        val sameOrigin = destination.scheme == initialUrl.scheme &&
            destination.host == initialUrl.host && destination.port == initialUrl.port
        if (!sameOrigin) {
            throw IOException("Redirección a otro origen bloqueada por seguridad. Abre el destino como una petición nueva para aprobarlo.")
        }
        return chain.proceed(chain.request())
    }
}
