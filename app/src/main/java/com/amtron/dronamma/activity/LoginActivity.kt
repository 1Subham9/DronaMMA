package com.amtron.dronamma.activity

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.amtron.dronamma.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    private lateinit var auth: FirebaseAuth
    private lateinit var fireStore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        supportActionBar?.title = "Login"

        auth = FirebaseAuth.getInstance()
        fireStore = FirebaseFirestore.getInstance()

        sharedPreferences = getSharedPreferences("Drona", MODE_PRIVATE)
        editor = sharedPreferences.edit()

        binding.addNewUser.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.login.setOnClickListener {
            val email = binding.email.text.toString().trim()
            val password = binding.enterPassword.text.toString().trim()

            if (email.isEmpty()) {
                binding.email.error = "Email is required"
                binding.email.requestFocus()
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                binding.enterPassword.error = "Password is required"
                binding.enterPassword.requestFocus()
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val uid = user?.uid
                    saveDataInSharedPref(uid.toString())
                    Toast.makeText(this, "Authentication successful.", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun saveDataInSharedPref(uid: String) {
        val dRef = fireStore.collection("Users").document(uid)

        dRef.get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val data = documentSnapshot.data
                    editor.putString("user", Gson().toJson(data))
                    editor.apply()

                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    // Handle the case where the document does not exist
                    Toast.makeText(this, "User data not found.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                // Handle exceptions
                Toast.makeText(this, "Error getting user data.", Toast.LENGTH_SHORT).show()
            }
    }
}
