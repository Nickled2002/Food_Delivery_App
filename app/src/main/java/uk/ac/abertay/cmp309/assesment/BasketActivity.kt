package uk.ac.abertay.cmp309.assesment

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.*

class BasketActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var db : FirebaseFirestore
    private lateinit var basketAdapter : BasketAdapter
    private lateinit var itemsList: ArrayList<Basket>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_basket)
        // getting the recyclerview by its id
        recyclerView = findViewById<RecyclerView>(R.id.BasketRecyclerView)
        //set layout manager to position items
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)



        //initialise shops
        itemsList = arrayListOf()
        basketAdapter = BasketAdapter(itemsList, onClickListener = {})

        //attach Adapter with the recyclerview
        recyclerView.adapter = basketAdapter


        EventChangeListener()
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

    fun onClickC(view: View) {

    }
}