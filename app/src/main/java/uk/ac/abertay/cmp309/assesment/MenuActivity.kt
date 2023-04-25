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
        val itemadd = hashMapOf(
            "Name" to item.Name,
            "Price" to item.Price,
            "ItemId" to item.Id
        )

        item.Id?.let {
            db.collection("Basket").document(it)
                .set(itemadd)
                .addOnSuccessListener {
                    Toast.makeText(this,"Item added to basket",Toast.LENGTH_SHORT).show()
                    itemCount++
                }
                .addOnFailureListener {
                    Toast.makeText(this,"Please try another again later",Toast.LENGTH_SHORT).show()
                }
        }


    }

    private fun EventChangeListener(shopId: String? ) {
        db = FirebaseFirestore.getInstance()
        if (shopId != null) {
            db.collection("Menus").document(shopId).collection("Items")
                .addSnapshotListener(object : EventListener<QuerySnapshot> {
                    override fun onEvent(
                        value: QuerySnapshot?,
                        error: FirebaseFirestoreException?)
                    {
                        if (error != null){

                            Log.e("Error",error.message.toString())
                            return
                        }

                        for (dc : DocumentChange in value?.documentChanges!!){

                            if (dc.type == DocumentChange.Type.ADDED){

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
            Toast.makeText(this,"Your basket is empty",Toast.LENGTH_SHORT).show()
        }
        else
        {
            val id = intent.getStringExtra("Id2")
            val name = intent.getStringExtra("Name2")
            val intent2 = Intent(this, BasketActivity::class.java)
            intent2.putExtra("Id", id )
            intent2.putExtra("Name", name )
            startActivity(intent2)
        }

    }
}