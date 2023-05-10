package uk.ac.abertay.cmp309.assesment

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast

private var completed = false

class RatingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rating)
        val text: TextView = findViewById(R.id.Rating_Name_Text)
        text.setText(intent.getStringExtra("Name"))
    }
    fun onclickRate(view: View) {
        val rBar = findViewById<RatingBar>(R.id.Rating_Bar)
        if (rBar != null)
        {
            val msg = rBar.rating.toString()
            Toast.makeText(this,
                "Rating is: "+msg, Toast.LENGTH_SHORT).show()

        }else{
            Toast.makeText(this,"Please rate your order", Toast.LENGTH_SHORT).show()
        }


    }
    fun onclickCancel(view: View) {
        val id = intent.getStringExtra("Id")
        val name = intent.getStringExtra("Name")
        val intent2 = Intent(this, PlacedActivity::class.java)
        intent2.putExtra("Id", id )
        intent2.putExtra("Name", name )
        intent2.putExtra("Submit", completed)
        startActivity(intent2)

    }
}