package uk.ac.abertay.cmp309.assesment

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var Email: EditText
    private lateinit var Password: EditText

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Email = findViewById(R.id.Input_Email)
        Password = findViewById(R.id.Input_Pass)
        firebaseAuth = FirebaseAuth.getInstance()
    }

    fun onClick(view: View?) {

        if (view != null) {


            when (view.id) {
                R.id.TextButton_SignUp -> {//redirect to sign up activity
                    val intent = Intent(this, SignUpActivity::class.java)
                    startActivity(intent)
                }
                R.id.Button_Login -> {
                    val Inemail = Email.text.toString()
                    val Inpass = Password.text.toString()
                    if (Inemail.isNotEmpty() && Inpass.isNotEmpty()) {//authenticate user
                        firebaseAuth.signInWithEmailAndPassword(Inemail,Inpass).addOnCompleteListener{
                            if (it.isSuccessful){//if credentials correct
                                val intent = Intent(this, LocationActivity::class.java)//redirect user
                                startActivity(intent)
                            }else{//notify user of error
                                Toast.makeText(this, "Incorrect password or email", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }else{//notify for empty fields
                        Toast.makeText(this, "Some fields are empty", Toast.LENGTH_SHORT).show()
                    }


                }

            }
        }
    }


}



