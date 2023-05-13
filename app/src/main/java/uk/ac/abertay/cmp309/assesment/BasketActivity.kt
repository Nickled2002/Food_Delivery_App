package uk.ac.abertay.cmp309.assesment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.wallet.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import org.json.JSONException



class BasketActivity : Activity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var db : FirebaseFirestore
    private lateinit var basketAdapter : BasketAdapter
    private lateinit var itemsList: ArrayList<Basket>
    private lateinit var paymentsClient: PaymentsClient
    private lateinit var gbutton : ImageButton
    private var totalPrice : Double = 0.0
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
        val text: TextView = findViewById(R.id.Basket_Price_Num)
        val count = intent.getIntExtra("Count",0)
        db = FirebaseFirestore.getInstance()
        db.collection("Basket").document("Total").get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                //get basket total price from firestore when the activity is created
                val document = task.result
                totalPrice = document.get("TotalPrice") as Double
                text.setText(totalPrice.toString())//set it as the text of the text field
            } else {
                Toast.makeText(this, "Please try again later", Toast.LENGTH_SHORT).show()
            }
        }



        //initialise shops
        itemsList = arrayListOf()//create list of items and call the basket adapter
        basketAdapter = BasketAdapter(itemsList, onClickListener = {
                basket -> removeFromBasket(basket,count)
        })

        //attach Adapter with the recyclerview
        recyclerView.adapter = basketAdapter


        eventChangeListener()
        //creat payment client
        paymentsClient = PaymentsProcess.createPaymentsClient(this)
        //g pay button clickable
        gbutton.isClickable = false

        isGooglePayAvailable()


        gbutton.setOnClickListener { requestPayment() }




    }
    private fun removeFromBasket(basket: Basket, count: Int ) {
        basket.ItemId?.let {
            db.collection("Basket").document("Items").collection("Items").document(it)
                .delete()
                .addOnSuccessListener {
                    //delete specific document from the basket list
                    totalPrice -= basket.Price!!//calculate new price
                    val nPrice = hashMapOf(
                        "TotalPrice" to totalPrice
                    )
                    db.collection("Basket").document("Total")
                        .set(nPrice)
                    //update database with new price
                    if (count==1)// if no items in the basket  go to the main menu
                    {
                        val intent3 = Intent(this, StoresActivity::class.java)
                        startActivity(intent3)
                    }
                    else
                    {
                        if (count>1)//if there are still items in the basket reload page
                        {
                            val id = intent.getStringExtra("Id")
                            val name = intent.getStringExtra("Name")
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
                    Toast.makeText(this, "Please try again later", Toast.LENGTH_SHORT).show()//error handling
                }
        }

    }
    private fun eventChangeListener() {
        //checks for items in the collection
        db = FirebaseFirestore.getInstance()
        db.collection("Basket").document("Items").collection("Items")
            .addSnapshotListener(object : EventListener<QuerySnapshot> {
                override fun onEvent(
                    value: QuerySnapshot?,
                    error: FirebaseFirestoreException?
                ) {
                    if (error != null) {
                        //error handling
                        Log.e("Error", error.message.toString())
                        return
                    }

                    for (dc: DocumentChange in value?.documentChanges!!) {

                        if (dc.type == DocumentChange.Type.ADDED) {
                            //and adds them once to the list in the form of objects

                            itemsList.add(dc.document.toObject(Basket::class.java))

                        }
                    }

                    basketAdapter.notifyDataSetChanged()

                }


            })
    }

    fun onclickBack(view: View) {//goes to previous page
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
        if (available) {//if google pay is available make the button clickable
            gbutton.isClickable = true
        } else {//error handling
            gbutton.isClickable = false
            Toast.makeText(this,"Google Pay is not available on this device", Toast.LENGTH_LONG).show()
        }
    }


    private fun requestPayment() {//get payment data
        gbutton.isClickable = false
        val totalPrice : Double = totalPrice

        val paymentDataRequestJson = PaymentsProcess.getRequestJson(totalPrice)
        if (paymentDataRequestJson == null) {
            Toast.makeText(this,"Can't fetch payment data request", Toast.LENGTH_SHORT).show()
            return
        }
        val request = PaymentDataRequest.fromJson(paymentDataRequestJson.toString())
        if (request != null) {
            AutoResolveHelper.resolveTask(//set the request for the payment
                paymentsClient.loadPaymentData(request), this, LPD_REQUEST_CODE)
        }
    }
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {//request results
            // Depending on lpdrequest code
            LPD_REQUEST_CODE -> {
                when (resultCode) {
                    RESULT_OK ->//payment has gone through
                        data?.let { intent ->// successful payments
                            PaymentData.getFromIntent(intent)?.let(::handleSuccess)
                        }

                    RESULT_CANCELED -> {//handle cancelled payments
                        handleFailure()
                    }

                    AutoResolveHelper.RESULT_ERROR -> {//handle declined payment
                        AutoResolveHelper.getStatusFromIntent(data)?.let {
                            handleFailure()
                        }
                    }
                }

                // Re-enables the Google Pay payment button.
                gbutton.isClickable = true
            }

        }
    }

    private fun handleSuccess(paymentData: PaymentData) {
        //prepare intent for next page
        val id = intent.getStringExtra("Id")
        val name = intent.getStringExtra("Name")
        val intent2 = Intent(this, PlacedActivity::class.java)
        intent2.putExtra("Id", id )
        intent2.putExtra("Name", name )
        intent2.putExtra("Submit",false)

        db = FirebaseFirestore.getInstance()//deletes all items in the basket that have been ordered
        db.collection("Basket").document("Items").collection("Items")
            .whereNotEqualTo("Name",null)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val docId = document.id
                    db.collection("Basket").document("Items").collection("Items").document(docId).delete()
                }
            }

        val paymentInformation = paymentData.toJson() ?: return

        try {//creates order record in the database

            val user = FirebaseAuth.getInstance().currentUser?.uid
            db = FirebaseFirestore.getInstance()
            if (user != null) {
                db.collection("Users").document(user)
                    .get()
                    .addOnSuccessListener { document ->
                        val address = document.getString("Address")
                        val billingName = document.getString("Name") + " " + document.getString("Surname")
                        val Ordersadd = hashMapOf(
                            "Name" to billingName,
                            "UserId" to user,
                            "Total" to totalPrice,
                            "RName" to name,
                            "Address" to address
                        )
                        db.collection("Orders").document().set(Ordersadd)
                    }
            }

        } catch (e: JSONException) {//error handling
            Log.e("handlePaymentSuccess", "Error: " + e.toString())
        }


        startActivity(intent2)//start prepared intent
    }
    private fun handleFailure() {//handle failure function
        val id = intent.getStringExtra("Id")
        val name = intent.getStringExtra("Name")
        val intent3 = Intent(this, DeclineActivity::class.java)//go to DeclineActivity
        intent3.putExtra("Id", id )
        intent3.putExtra("Name", name )
        startActivity(intent3)
    }




}