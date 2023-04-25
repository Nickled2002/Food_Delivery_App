package uk.ac.abertay.cmp309.assesment

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.*

class MenuActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var db : FirebaseFirestore
    private lateinit var itemsAdapter : ItemsAdapter
    private lateinit var itemsList: ArrayList<Item>

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
        itemsAdapter = ItemsAdapter(itemsList)

        //attach Adapter with the recyclerview
        recyclerView.adapter = itemsAdapter

        EventChangeListener(shopsid)
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

    fun onclick(view: View) {
        val intent = Intent(this, StoresActivity::class.java)
        startActivity(intent)
    }
}