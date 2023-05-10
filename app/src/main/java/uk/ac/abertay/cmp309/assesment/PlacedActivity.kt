package uk.ac.abertay.cmp309.assesment

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class PlacedActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_placed)
    }
    fun onclickHome(view: View) {
        val intent = Intent(this, StoresActivity::class.java)
        startActivity(intent)

    }
    fun onclickRate(view: View) {
        //TODO: get restaurant id from old page

    }
}