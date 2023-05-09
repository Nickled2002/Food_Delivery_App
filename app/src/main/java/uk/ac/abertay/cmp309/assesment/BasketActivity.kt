package uk.ac.abertay.cmp309.assesment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.wallet.*
import com.google.firebase.firestore.*
import org.json.JSONException
import org.json.JSONObject



class BasketActivity : Activity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var db : FirebaseFirestore
    private lateinit var basketAdapter : BasketAdapter
    private lateinit var itemsList: ArrayList<Basket>
    private lateinit var paymentsClient: PaymentsClient
    private lateinit var gbutton : RelativeLayout
    private val LPD_REQUEST_CODE = 991

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
                basket -> removeFromBasket(basket,count)
        })

        //attach Adapter with the recyclerview
        recyclerView.adapter = basketAdapter


        eventChangeListener()

        paymentsClient = PaymentsProcess.createPaymentsClient(this)

        isGooglePayAvailable()
        gbutton.isClickable = true
        //gbutton.setOnClickListener { requestPayment() }




    }
    private fun removeFromBasket(basket: Basket, count: Int ) {
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
    private fun eventChangeListener() {
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
    //Determining if button is available to be shown
    private fun isGooglePayAvailable() {
        val isReadyToPayJson = PaymentsProcess.isReadyToPayRequest() ?: return
        val request = IsReadyToPayRequest.fromJson(isReadyToPayJson.toString()) ?: return

        val task = paymentsClient.isReadyToPay(request)
        task.addOnCompleteListener { completedTask ->
            try {
                completedTask.getResult(ApiException::class.java)?.let(::setGooglePayAvailable)
            } catch (exception: ApiException) {
                // Process error
                Log.w("isReadyToPay failed", exception)
            }
        }

    }

    private fun setGooglePayAvailable(available: Boolean) {
        if (available) {
            gbutton.visibility = View.VISIBLE
        } else {
            Toast.makeText(this,"Google Pay is not available on this device", Toast.LENGTH_LONG).show()
        }
    }


    private fun requestPayment() {
        gbutton.isClickable = false
        val totalPrice : Long = 0 //TODO: Get Total Price

        val paymentDataRequestJson = PaymentsProcess.getRequestJson(totalPrice)
        if (paymentDataRequestJson == null) {
            Toast.makeText(this,"Can't fetch payment data request", Toast.LENGTH_SHORT).show()
            return
        }
        val request = PaymentDataRequest.fromJson(paymentDataRequestJson.toString())
        if (request != null) {
            AutoResolveHelper.resolveTask(
                paymentsClient.loadPaymentData(request), this, LPD_REQUEST_CODE)
        }
    }
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            // Value passed in AutoResolveHelper
            LPD_REQUEST_CODE -> {
                when (resultCode) {
                    RESULT_OK ->
                        data?.let { intent ->
                            PaymentData.getFromIntent(intent)?.let(::handleSuccess)
                        }

                    RESULT_CANCELED -> {
                        // The user cancelled the payment attempt
                    }

                    AutoResolveHelper.RESULT_ERROR -> {
                        AutoResolveHelper.getStatusFromIntent(data)?.let {
                            handleFailure(it.statusCode)
                        }
                    }
                }

                // Re-enables the Google Pay payment button.
                gbutton.isClickable = true
            }

        }
    }

    private fun handleSuccess(paymentData: PaymentData) {
        val paymentInformation = paymentData.toJson() ?: return

        try {
            // Token will be null if PaymentDataRequest was not constructed using fromJson(String).
            val paymentMethodData = JSONObject(paymentInformation).getJSONObject("paymentMethodData")
            val billingName = paymentMethodData.getJSONObject("info")
                .getJSONObject("billingAddress").getString("name")
            Log.d("BillingName", billingName)

            //TODO: new activity and add to firestore as orders empty basket

            // Logging token string.
            Log.d("GooglePaymentToken", paymentMethodData
                .getJSONObject("tokenizationData")
                .getString("token"))

        } catch (e: JSONException) {
            Log.e("handlePaymentSuccess", "Error: " + e.toString())
        }



    }
    private fun handleFailure(statusCode: Int) {
        Log.w("loadPaymentData failed", String.format("Error code: %d", statusCode))
    }




}