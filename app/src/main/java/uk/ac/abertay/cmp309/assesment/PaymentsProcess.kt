package uk.ac.abertay.cmp309.assesment

import android.app.Activity
import com.google.android.gms.wallet.PaymentsClient
import com.google.android.gms.wallet.Wallet
import com.google.android.gms.wallet.WalletConstants
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

object PaymentsProcess {

    //setting up base request
    private val baseRequest = JSONObject().apply {
        put("apiVersion", 2)
        put("apiVersionMinor", 0)
    }
    //add the gateway and merchant id to an account but for the assignment example will provide dummy values
    private fun gatewayTokenizationSpecification() : JSONObject {
        return JSONObject().apply {
            put("type", "PAYMENT_GATEWAY")
            put(
                "parameters", JSONObject(
                    mapOf(
                        "gateway" to "example",
                        "gatewayMerchantId" to "exampleGatewayMerchantId"
                    )
                )
            )
        }
    }
    //determine the networks allowed
    private val allowedCardNetworks = JSONArray(listOf(
        "AMEX",
        "DISCOVER",
        "INTERAC",
        "JCB",
        "MASTERCARD",
        "VISA"))
    //determine the cards auth methods
    private val allowedCardAuthMethods = JSONArray(listOf(
        "PAN_ONLY",
        "CRYPTOGRAM_3DS"))
    //input factors determined in a JSONObject
    private fun baseCardPaymentMethod(): JSONObject {
        return JSONObject().apply {

            val parameters = JSONObject().apply {
                put("allowedAuthMethods", allowedCardAuthMethods)
                put("allowedCardNetworks", allowedCardNetworks)
                put("billingAddressRequired", true)
                put("billingAddressParameters", JSONObject().apply {
                    put("format", "FULL")
                })
            }

            put("type", "CARD")
            put("parameters", parameters)
        }
    }
    //binds card payment methods with the results of the gateway function
    private fun cardPaymentMethod(): JSONObject {
        val cardPaymentMethod = baseCardPaymentMethod()
        cardPaymentMethod.put("tokenizationSpecification", gatewayTokenizationSpecification())

        return cardPaymentMethod
    }

    fun isReadyToPayRequest(): JSONObject? {
        //checks the readiness of the payment system
        return try {
            baseRequest.apply {
                put("allowedPaymentMethods", JSONArray().put(baseCardPaymentMethod()))
            }

        } catch (e: JSONException) {
            null
        }
    }

    //merchant info is set as "example" for the assessment
    private val merchantInfo = JSONObject().apply {
        put("merchantName", "Example Merchant")
        put("merchantId", "01234567890123456789")
    }

    //sets the information for the payment client
    fun createPaymentsClient(activity: Activity): PaymentsClient {
        val walletOptions = Wallet.WalletOptions.Builder()
            .setEnvironment(WalletConstants.ENVIRONMENT_TEST).build()
        return Wallet.getPaymentsClient(activity, walletOptions)
    }

    //set info of ammount owed for the payment
    @Throws(JSONException::class)
    private fun transactionInfo(price: String): JSONObject {
        return JSONObject().apply {
            put("totalPrice", price)
            put("totalPriceStatus", "FINAL")
            put("countryCode", "GR")
            put("currencyCode", "EUR")
        }

    }

    //set up Json request
    fun getRequestJson(price: Double): JSONObject? {
        return try {
            baseRequest.apply {
                put("allowedPaymentMethods", JSONArray().put(cardPaymentMethod()))
                put("transactionInfo", transactionInfo(price.toString()))
                put("merchantInfo", merchantInfo)
            }
        } catch (e: JSONException){
            null
        }
    }

}