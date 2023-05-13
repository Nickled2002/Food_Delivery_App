package uk.ac.abertay.cmp309.assesment

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

private var completed = false

class RatingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rating)
        val text: TextView = findViewById(R.id.Rating_Name_Text)
        text.setText(intent.getStringExtra("Name"))
    }
    fun onclickRate(view: View) {
        //get information of the rating bar
        val ratingBar = findViewById<RatingBar>(R.id.Rating_Bar)
        if (ratingBar != null)
        {
            val rating = ratingBar.rating
            val db = Firebase.firestore
            val id = intent.getStringExtra("Id")
            val name = intent.getStringExtra("Name")
            //get current ratin of the store
            if (id != null) {
                val path = db.collection("Shops").document(id)
                var finalrating :Double
                path
                    .get()
                    .addOnSuccessListener { documents ->
                        val numr :Double = documents.get("Rating") as Double
                        var numtot : Long = documents.get("Ratings") as Long
                        finalrating = numr * numtot
                        finalrating += rating
                        numtot += 1
                        finalrating /= numtot
                        finalrating = Math.round(finalrating * 100.0) / 100.0
                        //calcute new rating average and round result to two decimals
                        path.update(mapOf("Rating" to finalrating,"Ratings" to numtot))
                            .addOnSuccessListener {
                                completed = true
                                val intent3 = Intent(this, PlacedActivity::class.java)
                                intent3.putExtra("Id", id )
                                intent3.putExtra("Name", name )
                                intent3.putExtra("Submit", completed)
                                startActivity(intent3)

                            }
                    }
                    .addOnFailureListener {
                        Toast.makeText(this,"Please try again later", Toast.LENGTH_SHORT).show()
                    }
            }


        }else{//empty field error handling
            Toast.makeText(this,"Please rate your order", Toast.LENGTH_SHORT).show()
        }


    }
    fun onclickCancel(view: View) {
        //redirect to placed activity if user changes their mind
        val id = intent.getStringExtra("Id")
        val name = intent.getStringExtra("Name")
        val intent2 = Intent(this, PlacedActivity::class.java)
        intent2.putExtra("Id", id )
        intent2.putExtra("Name", name )
        intent2.putExtra("Submit", completed)
        startActivity(intent2)

    }
}