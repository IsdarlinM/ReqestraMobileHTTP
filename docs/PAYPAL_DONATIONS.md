# Voluntary Donations to Reqestra — PayPal

**Version:** Reqestra 3.3 (Android and Web). The app **does not process payments or verify transactions**. It opens a donation page hosted directly on paypal.com.

## Owner-provided button configuration

The owner provided the administration URL `https://www.paypal.com/donate/buttons/manage/DZW75X5SML2RJ`.
Its final segment, `DZW75X5SML2RJ`, was configured as a candidate `hosted_button_id` in the web client and as a default public value for Android, based on PayPal's hosted-button ID format. A nonempty local Gradle property `paypalHostedButtonId` overrides that default.

**Verification pending:** the administration URL is NOT for donors and does not prove that the administration identifier matches the public button ID. The owner must open `https://www.paypal.com/donate/?hosted_button_id=DZW75X5SML2RJ` in a private session, confirm that it displays an active Reqestra donation page, and check that the beneficiary is the intended account before publication or distribution.

If PayPal displays a different ID in its generated **Share / Embed** code, replace the value in `web/config.js` and the default in `app/build.gradle.kts`, or run `scripts/configure-paypal.sh` to update Android and Web locally before building.

## 1. What is integrated

- Android: **Support Project · PayPal** in the tools menu, with an explanation screen and external button.
- Web: **Donate / Support** section with an external button.
- Both clients validate an alphanumeric **public** hosted-button ID and construct a fixed URL on `www.paypal.com`.
- Without an ID, the payment action remains disabled and displays an explanation.
- Donations do not unlock features, accounts, badges, subscriptions, services, or content.
- Reqestra has no infrastructure to collect card numbers, PayPal passwords, donor emails, or payment receipts.

## 2. Create a button for your own account

1. Sign in to https://www.paypal.com/donate/buttons using the intended PayPal receiving account.
2. Create a donation page or button if your account and country are eligible. Options, fees, and eligibility vary by region and account category.
3. Carefully review the name, currency, description, and beneficiary in PayPal.
4. Copy the generated `hosted_button_id`. It is a public link identifier, not a private key.
5. Manually verify that PayPal shows the correct account before enabling the Reqestra button.

Official reference: https://developer.paypal.com/sdk/donate/

If PayPal generates an incompatible link type, do not force it into this integration. Review the official URL before adapting and validating the integration. This version supports only the hosted `donate/?hosted_button_id` format.

## Configure both clients from Termux

After creating your PayPal donation button, configure Android and Web with **one command**:

    cd ~/ReqestraMobileHTTP
    git pull --ff-only origin main
    bash scripts/configure-paypal.sh

The wizard requests only the **public** `hosted_button_id` of your own button, validates its format, and updates `~/.gradle/gradle.properties` and `web/config.js`. It does not request your password, API key, or secrets.

Verify that the generated URL opens the PayPal donation page for **your receiving account**. You may then commit only `web/config.js` to GitHub if you want your public web site to accept donations. This script neither publishes a site nor builds an APK automatically.

## 3. Configure Android

On your build computer or Termux, open the local user-level Gradle properties file:

    mkdir -p ~/.gradle
    nano ~/.gradle/gradle.properties

Add your real ID (do NOT use this sample value):

    paypalHostedButtonId=YOUR_PUBLIC_ID

Then build:

    cd ~/ReqestraMobileHTTP
    ./gradlew --no-daemon testDebugUnitTest assembleDebug

Android constructs the URL from `BuildConfig.PAYPAL_HOSTED_BUTTON_ID`. No PayPal SDK, client secret, or additional Android permission is required, because payment opens in the system browser.

**Warning:** BuildConfig is embedded in the distributed APK and can be read by others. Include only the public ID, **never** private keys, OAuth tokens, or API credentials. Avoid committing it to source control if the owner does not wish to publish the ID on GitHub.

Without configuration, the button is disabled.

## 4. Configure the web client

Edit `web/config.js`:

    window.REQESTRA_CONFIG = Object.freeze({
      paypalHostedButtonId: "YOUR_PUBLIC_ID",
    });

Republish `web/config.js`. The ID is delivered to every browser because it is public. The button opens a new tab on PayPal's official domain with `rel=noopener noreferrer`.

Without configuration, Reqestra does not open a generic checkout or request financial information.

## 5. Donor experience

1. Open the donation section in the app or web client.
2. Read the voluntary-contribution notice.
3. If an ID is configured, choose **Donate with PayPal**.
4. Verify the official paypal.com domain, beneficiary, and terms.
5. Complete or cancel the contribution on PayPal. Reqestra does not learn or store the outcome.

Never display "Payment completed" merely because the button was selected. Verifying transactions and issuing receipts would require an additional official server-side integration, which this version **does not** include.

## 6. Store policies and legal considerations

Contributions to a creator without digital benefits may fall outside Google Play Billing when Google's published conditions are met. However, Google Play may evaluate the actual implementation and policies can change. Review the current rules before distribution:

- https://support.google.com/googleplay/android-developer/answer/10281818
- https://support.google.com/googleplay/android-developer/answer/9858738

**Do not offer in-app benefits in exchange for donations**, such as features, badges, or subscriptions; this may change how the transaction is classified and require store billing.

Do not describe contributions as "charitable donations" or promise tax deductions without appropriate accreditation. PayPal determines its own donation eligibility terms; recipients must verify applicable tax obligations, local rules, and app-store policies. This document is technical guidance, not legal or tax advice.

## 7. Security and testing

- [ ] The URL starts exactly with `https://www.paypal.com/donate/`.
- [ ] The ID contains only letters or digits, 6–64 characters.
- [ ] The UI cannot supply an arbitrary external payment URL.
- [ ] Without an ID, the button is disabled on Android and Web.
- [ ] Reqestra does not ask donors for credentials or card numbers.
- [ ] The web client uses `target=_blank` with `rel=noopener noreferrer`.
- [ ] All features remain available when donations are disabled.
- [ ] The receiving account is visually verified on PayPal before distribution.
- [ ] Current Google Play and PayPal policies are reviewed for the target market.

## 8. References

- PayPal button creation: https://www.paypal.com/donate/buttons
- PayPal Donate SDK: https://developer.paypal.com/sdk/donate/
- Google Play guidance on contributions: https://support.google.com/googleplay/android-developer/answer/10281818

Do not upload PayPal credentials or attempt to pass them through a hosted-button URL.
