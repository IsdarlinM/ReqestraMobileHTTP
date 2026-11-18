package com.imr.reqestra.core.donations

/** Sólo enlaces HTTPS generados desde identificadores públicos de botones alojados por PayPal. */
object PayPalDonations {
    private val validId = Regex("[A-Za-z0-9]{6,64}")

    fun hostedUrl(buttonId: String): String? {
        val normalized = buttonId.trim()
        if (!validId.matches(normalized)) return null
        return "https://www.paypal.com/donate/?hosted_button_id=$normalized"
    }
}
