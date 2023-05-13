package uk.ac.abertay.cmp309.assesment

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class DeclineActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_decline)
    }

    fun onclickRetry(view: View) {//redirect to menu activity to retry order
        val id = intent.getStringExtra("Id")
        val name = intent.getStringExtra("Name")
        val intent2 = Intent(this, MenuActivity::class.java)
        intent2.putExtra("Id", id )
        intent2.putExtra("Name", name )
        startActivity(intent2)

    }

    fun onclickHome(view: View) {//redirect to home page
        val intent = Intent(this, StoresActivity::class.java)
        startActivity(intent)

    }
}