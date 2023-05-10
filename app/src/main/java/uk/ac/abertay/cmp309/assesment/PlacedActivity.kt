package uk.ac.abertay.cmp309.assesment

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast

class PlacedActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_placed)
        val text: TextView = findViewById(R.id.Accept_Name)
        text.setText(intent.getStringExtra("Name"))
    }
    fun onclickHome(view: View) {
        val intent2 = Intent(applicationContext, StoresActivity::class.java)
        startActivity(intent2)

    }
    fun onclickRate(view: View) {
        var submit = intent.getBooleanExtra("Submit", false)
        if (submit)
        {
            Toast.makeText(this, "Rating already submitted.", Toast.LENGTH_SHORT).show()
        }else {
            var id = intent.getStringExtra("Id")
            val name = intent.getStringExtra("Name")
            val intent3 = Intent(this, RatingActivity::class.java)
            intent3.putExtra("Id", id)
            intent3.putExtra("Name", name)
            startActivity(intent3)
        }


    }
}