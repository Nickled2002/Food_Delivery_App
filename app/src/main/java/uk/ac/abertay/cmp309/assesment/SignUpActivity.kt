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
    private lateinit var Name: EditText
    private lateinit var Surname: EditText
    private lateinit var DoB: EditText
    private lateinit var firebaseAuth: FirebaseAuth




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        Email = findViewById(R.id.SignUp_Input_Email)
        Password = findViewById(R.id.SignUp_Input_Password)
        CPassword = findViewById(R.id.SignUp_input_CPassword)
        Name = findViewById(R.id.SignUp_Input_Name)
        Surname = findViewById(R.id.SignUp_Input_Surame)
        DoB = findViewById(R.id.SignUp_Input_DoB)

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
                    val Inname = Name.text.toString()
                    val Insurname = Surname.text.toString()
                    val Indob = DoB.text.toString()

                    if (Inemail.isNotEmpty() && Inpass.isNotEmpty() && Incpass.isNotEmpty() && Inname.isNotEmpty() && Insurname.isNotEmpty() && Indob.isNotEmpty() ) {
                        if (Inpass == Incpass){

                            firebaseAuth.createUserWithEmailAndPassword(Inemail, Inpass).addOnCompleteListener{
                                if(it.isSuccessful){
                                    val user = FirebaseAuth.getInstance().currentUser?.uid
                                    val db = Firebase.firestore
                                    val inputUser = hashMapOf(
                                        "UserId" to user,
                                        "Name" to Inname,
                                        "Surname" to Insurname,
                                        "DoB" to Indob
                                    )
                                    db.collection("Users").add(inputUser).addOnCompleteListener {
                                        if(it.isSuccessful) {
                                            val intent = Intent(this, MainActivity::class.java)
                                            startActivity(intent)
                                        }else
                                        {
                                            Toast.makeText(this, "Service offline. Please try again later.", Toast.LENGTH_LONG).show()
                                        }
                                    }
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