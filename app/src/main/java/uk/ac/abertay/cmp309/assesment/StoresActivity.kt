package uk.ac.abertay.cmp309.assesment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.*
import com.google.firebase.ktx.Firebase


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
        shopsAdapter = ShopsAdapter(shopsList, onClickListener = {view, shop -> val intent = Intent(this, MenuActivity::class.java); intent.putExtra("Id",shop.StoreId); intent.putExtra("Name",shop.Name); startActivity(intent)  })

        //attach Adapter with the recyclerview
        recyclerView.adapter = shopsAdapter


        EventChangeListener()

    }

    private fun EventChangeListener() {
        //checks for items in the collection
        db = FirebaseFirestore.getInstance()
        db.collection("Shops").orderBy("Rating",Query.Direction.DESCENDING)
            .addSnapshotListener(object : EventListener<QuerySnapshot>{
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

                            shopsList.add(dc.document.toObject(Shop::class.java))

                        }
                    }

                    shopsAdapter.notifyDataSetChanged()

                }


            })




    }

    fun onClickSignOut(view: View) {
        Firebase.auth.signOut()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)


    }

}