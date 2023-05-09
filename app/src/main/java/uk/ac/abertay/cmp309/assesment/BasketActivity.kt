package uk.ac.abertay.cmp309.assesment

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
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

        val count = intent.getIntExtra("Count",0)

        //initialise shops
        itemsList = arrayListOf()
        basketAdapter = BasketAdapter(itemsList, onClickListener = {
                basket -> RemoveFromBasket(basket,count)
        })

        //attach Adapter with the recyclerview
        recyclerView.adapter = basketAdapter


        EventChangeListener()
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

    fun onClickC(view: View) {

    }
}