package uk.ac.abertay.cmp309.assesment

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.wallet.IsReadyToPayRequest
import com.google.android.gms.wallet.PaymentsClient
import com.google.android.gms.wallet.Wallet
import com.google.android.gms.wallet.WalletConstants
import com.google.firebase.firestore.*
import org.json.JSONArray
import org.json.JSONObject

class BasketActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var db : FirebaseFirestore
    private lateinit var basketAdapter : BasketAdapter
    private lateinit var itemsList: ArrayList<Basket>
    private lateinit var paymentsClient: PaymentsClient
    private lateinit var gbutton : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_basket)
        // getting the recyclerview by its id
        recyclerView = findViewById<RecyclerView>(R.id.BasketRecyclerView)
        //set layout manager to position items
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        gbutton = findViewById(R.id.googlePayButton)

        val count = intent.getIntExtra("Count",0)

        //initialise shops
        itemsList = arrayListOf()
        basketAdapter = BasketAdapter(itemsList, onClickListener = {
                basket -> RemoveFromBasket(basket,count)
        })

        //attach Adapter with the recyclerview
        recyclerView.adapter = basketAdapter


        EventChangeListener()

        paymentsClient = createPaymentsClient(this)

        val readyToPayRequest =
            IsReadyToPayRequest.fromJson(baseRequest.toString())

        val readyToPayTask = paymentsClient.isReadyToPay(readyToPayRequest)
        readyToPayTask.addOnCompleteListener { task ->
            try {
                task.getResult(ApiException::class.java)?.let(::setGooglePayAvailable)
            } catch (exception: ApiException) {
                // Error determining readiness to use Google Pay.
                Toast.makeText(this,"Please try again later",Toast.LENGTH_SHORT).show()
            }
        }


    }
    private fun RemoveFromBasket(basket: Basket, count: Int ) {
        basket.ItemId?.let {
            db.collection("Basket").document(it)
                .delete()
                .addOnSuccessListener {
                    if (count==1)
                    {
                        val intent3 = Intent(this, StoresActivity::class.java)
                        startActivity(intent3)
                    }
                    else
                    {
                        if (count>1)
                        {
                            val id = intent.getStringExtra("Id2")
                            val name = intent.getStringExtra("Name2")
                            val ncount = count - 1
                            val intent2 = Intent(this, BasketActivity::class.java)
                            intent2.putExtra("Id", id )
                            intent2.putExtra("Name", name )
                            intent2.putExtra("Count",ncount)
                            startActivity(intent2)
                        }
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Please try again later", Toast.LENGTH_SHORT).show()
                }
        }

    }
    private fun EventChangeListener() {
        db = FirebaseFirestore.getInstance()
        db.collection("Basket")
            .addSnapshotListener(object : EventListener<QuerySnapshot> {
                override fun onEvent(
                    value: QuerySnapshot?,
                    error: FirebaseFirestoreException?
                ) {
                    if (error != null) {

                        Log.e("Error", error.message.toString())
                        return
                    }

                    for (dc: DocumentChange in value?.documentChanges!!) {

                        if (dc.type == DocumentChange.Type.ADDED) {

                            itemsList.add(dc.document.toObject(Basket::class.java))

                        }
                    }

                    basketAdapter.notifyDataSetChanged()

                }


            })
    }

    fun onclickBack(view: View) {
        val id = intent.getStringExtra("Id")
        val name = intent.getStringExtra("Name")
        val intent2 = Intent(this, MenuActivity::class.java)
        intent2.putExtra("Id", id )
        intent2.putExtra("Name", name )
        startActivity(intent2)

    }

    fun createPaymentsClient(activity: Activity): PaymentsClient {
        val walletOptions = Wallet.WalletOptions.Builder()
            .setEnvironment(WalletConstants.ENVIRONMENT_TEST).build()
        return Wallet.getPaymentsClient(activity, walletOptions)
    }

    private val baseRequest = JSONObject().apply {
        put("apiVersion", 2)
        put("apiVersionMinor", 0)
        put("allowedPaymentMethods",  JSONArray().put(baseCardPaymentMethod()))
    }

    private val allowedCardNetworks = JSONArray(listOf(
        "VISA",
        "MASTERCARD"))

    private val allowedCardAuthMethods = JSONArray(listOf(
        "PAN_ONLY",
        "CRYPTOGRAM_3DS"))

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

    private fun setGooglePayAvailable(available: Boolean) {
        if (available) {
            gbutton.visibility = View.VISIBLE
            gbutton.setOnClickListener { requestPayment() }
        } else {
            Toast.makeText(this,"Please try again later",Toast.LENGTH_SHORT).show()
        }
    }

    //This is where you can add the gateway and merchant id to an account but for the assignment example will provide dummy values
    private val tokenizationSpecification = JSONObject().apply {
        put("type", "PAYMENT_GATEWAY")
        put("parameters", JSONObject(mapOf(
            "gateway" to "example",
            "gatewayMerchantId" to "exampleGatewayMerchantId")))
    }

    private val cardPaymentMethod = JSONObject().apply {
        put("type", "CARD")
        put("tokenizationSpecification", tokenizationSpecification)
        put("parameters", JSONObject().apply {
            put("allowedCardNetworks", JSONArray(listOf("VISA", "MASTERCARD")))
            put("allowedAuthMethods", JSONArray(listOf("PAN_ONLY", "CRYPTOGRAM_3DS")))
            put("billingAddressRequired", true)
            put("billingAddressParameters", JSONObject(mapOf("format" to "FULL")))
        })
    }
    //This is where you change the values
    private val transactionInfo = JSONObject().apply {
        put("totalPrice", "123.45")
        put("totalPriceStatus", "FINAL")
        put("currencyCode", "EUR")
    }

    private fun requestPayment() {
        // TODO: Perform transaction
    }



}