package uk.ac.abertay.cmp309.assesment

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.*

class MenuActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var db : FirebaseFirestore
    private lateinit var itemsAdapter : ItemsAdapter
    private lateinit var itemsList: ArrayList<Item>
    private var itemCount = 0
    private var totalPrice = 0.0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)
        val text: TextView = findViewById(R.id.Menu_Name)
        val shopsid = intent.getStringExtra("Id")

        text.setText(intent.getStringExtra("Name"))
        // getting the recyclerview by its id
        recyclerView = findViewById<RecyclerView>(R.id.MenuRecyclerView)
        //set layout manager to position items
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)


        //initialise shops
        itemsList = arrayListOf()
        itemsAdapter = ItemsAdapter(itemsList, onClickListener = {item -> AddToBasket(item)})

        //attach Adapter with the recyclerview
        recyclerView.adapter = itemsAdapter

        EventChangeListener(shopsid)
    }
    private fun AddToBasket(item: Item ) {
        //make hash of selected item
        val itemadd = hashMapOf(
            "Name" to item.Name,
            "Price" to item.Price,
            "ItemId" to item.Id
        )

        //add item to the database
        item.Id?.let {
            db.collection("Basket").document("Items").collection("Items").document(it)
                .set(itemadd)
                .addOnSuccessListener {
                    Toast.makeText(this,"Item added to basket",Toast.LENGTH_SHORT).show()
                    itemCount++
                    totalPrice += item.Price!!
                }
                .addOnFailureListener {
                    //error handling notify user
                    Toast.makeText(this,"Please try another again later",Toast.LENGTH_SHORT).show()
                }
        }


    }

    private fun EventChangeListener(shopId: String? ) {
        //checks for items in the collection
        db = FirebaseFirestore.getInstance()
        if (shopId != null) {
            db.collection("Menus").document(shopId).collection("Items")
                .addSnapshotListener(object : EventListener<QuerySnapshot> {
                    override fun onEvent(
                        value: QuerySnapshot?,
                        error: FirebaseFirestoreException?)
                    {
                        if (error != null){
                            //error handling
                            Log.e("Error",error.message.toString())
                            return
                        }

                        for (dc : DocumentChange in value?.documentChanges!!){

                            if (dc.type == DocumentChange.Type.ADDED){
                                //and adds them once to the list in the form of objects

                                itemsList.add(dc.document.toObject(Item::class.java))

                            }
                        }

                        itemsAdapter.notifyDataSetChanged()

                    }


                })
        }




    }

    fun onclickBack(view: View) {
        val intent = Intent(this, StoresActivity::class.java)
        startActivity(intent)
    }

    fun onClickB(view: View) {
        if (itemCount==0)
        {
            //if basket is empty notify user
            Toast.makeText(this,"Your basket is empty",Toast.LENGTH_SHORT).show()
        }
        else
        {
            val priceadd = hashMapOf(
                "TotalPrice" to totalPrice
            )
            //adds total price to the basket
            db.collection("Basket").document("Total")
                .set(priceadd)
                .addOnSuccessListener {
                    val id = intent.getStringExtra("Id")
                    val name = intent.getStringExtra("Name")
                    //redirect user to basket activity
                    val intent2 = Intent(this, BasketActivity::class.java)
                    intent2.putExtra("Id", id )
                    intent2.putExtra("Name", name )
                    intent2.putExtra("Count", itemCount)
                    startActivity(intent2)
                }
                .addOnFailureListener {
                    //error handling
                    Toast.makeText(this,"Basket unavailable. Try again later",Toast.LENGTH_SHORT).show()
                }

        }

    }
}