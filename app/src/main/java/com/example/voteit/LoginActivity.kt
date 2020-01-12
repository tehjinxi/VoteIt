package com.example.voteit

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.loginhome.*

class LoginActivity : AppCompatActivity() {
    var email: String = ""
    var password: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.loginhome)


        val actionbar = supportActionBar
        actionbar!!.title = "Login"

        button_login.setOnClickListener{
            checkEmpty()
        }

        button_regis.setOnClickListener{
            val intent = Intent (this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
    private fun checkEmpty(){
        email = findViewById<EditText>(R.id.editText_emailLogin).text.toString()
        password = findViewById<EditText>(R.id.editText_passwordLogin).text.toString()

        if(email.isEmpty() || password.isEmpty()){
            Toast.makeText(this, "Do not leave an empty field", Toast.LENGTH_LONG).show()
            return
        }
        fetchUser()
    }

    companion object {
        val USER_KEY = "USERKEY"
    }

    private fun fetchUser() {
        val ref = FirebaseDatabase.getInstance().getReference("/user")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {

            override fun onDataChange(p0: DataSnapshot) {

                p0.children.forEach {
                    Log.d("NewMessage", it.toString())
                    val user = it.getValue(User::class.java)
                    if (user != null) {
                        login(user)
                    }
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }
    private fun login(user: User){
        if(email == user.email){
            if(password == user.password){

                val ref = FirebaseDatabase.getInstance().getReference("login")
                ref.setValue(user.id)

                val intent = Intent (this, MainActivity::class.java)
                intent.putExtra(USER_KEY, user)
                startActivity(intent)
            }
            else{
                Toast.makeText(this, "Password not match", Toast.LENGTH_LONG).show()
            }
        }
        else{
            Toast.makeText(this, "Email not register", Toast.LENGTH_LONG).show()
        }
    }
}