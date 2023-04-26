package com.amtron.dronamma.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.amtron.dronamma.R
import com.amtron.dronamma.databinding.ActivityRegisterBinding
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    private lateinit var fireBaseAuth: FirebaseAuth
    private lateinit var firebaseFireSore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_register)


        fireBaseAuth = FirebaseAuth.getInstance()
        firebaseFireSore = FirebaseFirestore.getInstance()


        binding.register.setOnClickListener {

            fireBaseAuth
                .createUserWithEmailAndPassword(binding.email.text.toString(), binding.enterPassword.text.toString()).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // User was created successfully

                        val user: FirebaseUser? = fireBaseAuth.currentUser

                        val userInfo = mutableMapOf<String, Any>()
                        userInfo["isAdmin"] = 0
                        userInfo["UserEmail"] = binding.email.toString()

                        user?.let { it1 -> firebaseFireSore.collection("Users").document(it1.uid) }
                            ?.set(userInfo)
                            ?.addOnSuccessListener {
                                // Document was successfully written to Firestore

                                val intent = Intent(this, HomeActivity::class.java)
                                startActivity(intent)
                            }
                            ?.addOnFailureListener { e ->
                                // Document write failed
                                Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
                            }






                        Toast.makeText(this, "User Created Successfully", Toast.LENGTH_SHORT).show()
                    } else {
                        // User creation failed
                        Toast.makeText(this,"Failed to Create Account", Toast.LENGTH_SHORT).show()
                    }
                }
        }


    }
}


