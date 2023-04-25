package uk.ac.abertay.cmp309.assesment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.*




class StoresActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var db : FirebaseFirestore
    private lateinit var shopsAdapter : ShopsAdapter
    private lateinit var shopsList: ArrayList<Shop>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stores)
        // getting the recyclerview by its id
        recyclerView = findViewById<RecyclerView>(R.id.Shop_RecyclerView)
        //set layout manager to position items
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)


        //initialise shops
        shopsList = arrayListOf()
        shopsAdapter = ShopsAdapter(shopsList, onClickListener = {view, shop -> val intent = Intent(this, MenuActivity::class.java); intent.putExtra("Id",shop.StoreId); intent.putExtra("Name",shop.Name);intent.putExtra("Id2",shop.StoreId); intent.putExtra("Name2",shop.Name);startActivity(intent)  })

        //attach Adapter with the recyclerview
        recyclerView.adapter = shopsAdapter


        EventChangeListener()











    }

    private fun EventChangeListener() {
        db = FirebaseFirestore.getInstance()
        db.collection("Shops").orderBy("Rating",Query.Direction.DESCENDING)
            .addSnapshotListener(object : EventListener<QuerySnapshot>{
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

                            shopsList.add(dc.document.toObject(Shop::class.java))

                        }
                    }

                    shopsAdapter.notifyDataSetChanged()

                }


            })




    }

}