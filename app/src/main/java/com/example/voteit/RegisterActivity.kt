package com.example.voteit

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_post_forum.*
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.loginhome.*
import java.text.SimpleDateFormat
import java.util.*

class RegisterActivity : AppCompatActivity() {

    var id: String = ""
    var name: String = ""
    var email: String = ""
    var password: String = ""
    var conPassword: String = ""

    var selectedPhotoUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val actionbar = supportActionBar
        actionbar!!.title = "Register"
        actionbar.setDisplayHomeAsUpEnabled(true)

        btnUserPhoto.setOnClickListener{
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }

        button_register.setOnClickListener{
            register()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            // proceed and check what the selected image was....
            selectedPhotoUri = data.data

            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)

            userPhotoView.setImageBitmap(bitmap)

            btnUserPhoto.alpha = 0f
        }
    }

    private fun register(){
        name = findViewById<EditText>(R.id.editText_name).text.toString()
        email = findViewById<EditText>(R.id.editText_email).text.toString()
        password = findViewById<EditText>(R.id.editText_password).text.toString()
        conPassword = findViewById<EditText>(R.id.editText_confirmPassword).text.toString()

        if(name.isEmpty() || email.isEmpty() || password.isEmpty() || conPassword.isEmpty()){
            Toast.makeText(this, "Do not leave an empty field", Toast.LENGTH_LONG).show()
            return
        }

        if(password != conPassword){
            Toast.makeText(this, "Password not match", Toast.LENGTH_LONG).show()
            return
        }

        if(name == "admin"){
            Toast.makeText(this, "Cannot use this username", Toast.LENGTH_LONG).show()
            return
        }

        uploadUserImage()
    }

    private fun uploadUserImage() {
        if (selectedPhotoUri == null){
            Toast.makeText(this, "Please select an image",Toast.LENGTH_LONG).show()
            return
        }

        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/users/$filename")

        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                Log.d("register activity", "Successfully uploaded image: ${it.metadata?.path}")

                ref.downloadUrl.addOnSuccessListener {
                    Log.d("registered", "File Location: $it")
                    savUserData(it.toString())
                }
            }
    }

    private fun savUserData(userImageUrl: String) {
        val databaseForum = FirebaseDatabase.getInstance().getReference("user")

        id = databaseForum.push().key.toString()

        val user = User(id, name, email, password, userImageUrl, "Not Verify", "", "", "")

        databaseForum.child(id).setValue(user)
            .addOnCompleteListener{
                Toast.makeText(this, "Register Successful",Toast.LENGTH_LONG).show()

                onBackPressed()
            }
            .addOnFailureListener{
                Toast.makeText(this, "Failed to register", Toast.LENGTH_SHORT).show()
            }

    }
}
