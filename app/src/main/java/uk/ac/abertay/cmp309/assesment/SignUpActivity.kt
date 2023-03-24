package uk.ac.abertay.cmp309.assesment

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SignUpActivity : AppCompatActivity() {

    private lateinit var Email: EditText
    private lateinit var CPassword: EditText
    private lateinit var Password: EditText
    private lateinit var firebaseAuth: FirebaseAuth




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        Email = findViewById(R.id.SignUp_Input_Email)
        Password = findViewById(R.id.SignUp_Input_Password)
        CPassword = findViewById(R.id.SignUp_input_CPassword)
        firebaseAuth = FirebaseAuth.getInstance()

    }

    fun onClick(view: View?) {

        if (view != null) {
            when(view.id) {
                R.id.TextButton_SignUp -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }
                R.id.SignUp_Button_SignUp -> {
                    val Inemail = Email.text.toString()
                    val Inpass = Password.text.toString()
                    val Incpass = CPassword.text.toString()
                    if (Inemail.isNotEmpty() && Inpass.isNotEmpty() && Incpass.isNotEmpty() ) {
                        if (Inpass == Incpass){

                            firebaseAuth.createUserWithEmailAndPassword(Inemail, Inpass).addOnCompleteListener{
                                if(it.isSuccessful){
                                    val user = FirebaseAuth.getInstance().currentUser
                                    val db = Firebase.firestore

                                    val intent = Intent(this, MainActivity::class.java)
                                    startActivity(intent)
                                }else{
                                    Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                                }
                            }
                        }else{
                            Toast.makeText(this, "Password and Confirm Password do not match", Toast.LENGTH_SHORT).show()
                        }
                    }else {
                        Toast.makeText(this, "Fields are empty", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}