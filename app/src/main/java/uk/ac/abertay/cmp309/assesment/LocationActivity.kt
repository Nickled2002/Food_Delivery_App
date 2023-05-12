package uk.ac.abertay.cmp309.assesment

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*


class LocationActivity : AppCompatActivity() {
    private lateinit var address: EditText
    private val LOCATION_PERMISSION_REQUEST = 1
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        val user = FirebaseAuth.getInstance().currentUser?.uid
        val db = Firebase.firestore
        address = findViewById(R.id.Edit_Text_Address)
        if (user != null) {
            db.collection("Users").document(user)
                .get()
                .addOnSuccessListener { document ->
                    val added = document.getBoolean("Added")
                    if (added!!)
                    {
                        val intent = Intent(this, StoresActivity::class.java)
                        startActivity(intent)
                    }

                }

        }
    }
    private fun converter (latitude : Double, longitude : Double) : String {
        val geoCoder = Geocoder(this, Locale.getDefault())
        val toaddress = geoCoder.getFromLocation(latitude, longitude, 1)
        return toaddress?.get(0)?.locality ?: String()

    }


    fun onclickGet(view: View) {
        val permission = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
        if (permission != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST
            )
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location : Location? ->
                if (location != null) {

                    val latitude = location.latitude
                    val longitude = location.longitude
                    val toaddress = converter(latitude,longitude)
                    address.setText("Bell st Dundee DD1 1HG.")
                    //address.setText(toaddress)

                }

            }
            .addOnFailureListener {
                Toast.makeText(this,"Please connect to the network and try again",Toast.LENGTH_SHORT).show()
            }
    }


    fun onclickSubmit(view: View) {
        val inAddy = address.text.toString()
        val user = FirebaseAuth.getInstance().currentUser?.uid
        if (inAddy.isNotEmpty()) {
            val db = Firebase.firestore
            val data = hashMapOf(
                "Added" to true,
                "Address" to inAddy
            )
            if (user != null) {
                db.collection("Users").document(user)
                    .set(data, SetOptions.merge())
                    .addOnSuccessListener {
                        val intent = Intent(this, StoresActivity::class.java)
                        startActivity(intent)
                    }
                    .addOnFailureListener {
                        Toast.makeText(this,"Please enter your address",Toast.LENGTH_SHORT).show()
                    }
            }


        }else{
            Toast.makeText(this,"Please enter your address",Toast.LENGTH_SHORT).show()
        }

    }


}