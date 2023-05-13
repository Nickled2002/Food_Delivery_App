package uk.ac.abertay.cmp309.assesment

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.math.roundToInt

class PlacedActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_placed)
        val text: TextView = findViewById(R.id.Accept_Name)
        text.text = intent.getStringExtra("Name")
        val db = FirebaseFirestore.getInstance()
        val id = intent.getStringExtra("Id")
        if (id != null) {//calculate average time to get to user
            db.collection("Shops").document(id).get()
                .addOnSuccessListener {
                    val distance = it.getDouble("Distance")
                    val mintotalTime = distance?.times(18)
                    val x = mintotalTime?.roundToInt()
                    val maxtotalTime = distance?.times(20)
                    val y = maxtotalTime?.roundToInt()
                    val time = "($x-$y mins)"
                    val timetext: TextView = findViewById(R.id.Accept_Soon_Text)
                    timetext.text = time

                }
        }

    }
    fun onclickHome(view: View) {
        val intent2 = Intent(applicationContext, StoresActivity::class.java)
        startActivity(intent2)

    }
    fun onclickRate(view: View) {
        //checks if the user has already inputted the rating
        val submit = intent.getBooleanExtra("Submit", false)
        if (submit)
        {//if yes notify user
            Toast.makeText(this, "Rating already submitted.", Toast.LENGTH_SHORT).show()
        }else {
            //redirect user to rating page
            val id = intent.getStringExtra("Id")
            val name = intent.getStringExtra("Name")
            val intent3 = Intent(this, RatingActivity::class.java)
            intent3.putExtra("Id", id)
            intent3.putExtra("Name", name)
            startActivity(intent3)
        }


    }
}