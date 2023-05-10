package uk.ac.abertay.cmp309.assesment

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class RatingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rating)
        val text: TextView = findViewById(R.id.Rating_Name_Text)
        text.setText(intent.getStringExtra("Name"))
    }
}