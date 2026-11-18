package com.imr.reqestra.core.donations

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class PayPalDonationsTest {
    @Test fun buildsOnlyTrustedPayPalDonateUrl() {
        assertEquals(
            "https://www.paypal.com/donate/?hosted_button_id=ABC123DEF456",
            PayPalDonations.hostedUrl("ABC123DEF456"),
        )
        assertNull(PayPalDonations.hostedUrl(""))
        assertNull(PayPalDonations.hostedUrl("https://evil.example"))
        assertNull(PayPalDonations.hostedUrl("ABC&redirect=bad"))
    }
}
