package com.example.voteit

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_edit_account.*
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.fragment_account.*
import java.util.*


class AccountActivity : AppCompatActivity() {

    private val PERMISSION_CODE = 1000;
    private val IMAGE_CAPTURE_CODE = 1001
    var selectedPhotoUri: Uri? = null
    var image_uri: Uri? = null
    var logId =""
    var userStatus = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_account)

        val actionbar = supportActionBar
        actionbar!!.title = "Edit Account"
        actionbar.setDisplayHomeAsUpEnabled(true)


        val idref = FirebaseDatabase.getInstance().getReference("login")
        idref.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                logId = p0.getValue().toString()
                val ref = FirebaseDatabase.getInstance().getReference("user").child( logId )
                ref.addValueEventListener(object: ValueEventListener {
                    override fun onDataChange(p0: DataSnapshot) {
                        if(p0.child("userImage").getValue().toString() != ""){
                            btnUserPic.alpha = 0f
                            Picasso.get().load(p0.child("userImage").getValue().toString()).into(editUserImg)
                        }
                        editUserName.text = p0.child("name").getValue().toString()
                        editUserIC.setText(p0.child("userIC").getValue().toString())
                        editUserPhone.setText(p0.child("userPhone").getValue().toString())
                        editUserEmail.setText(p0.child("email").getValue().toString())
                        editPass.setText(p0.child("password").getValue().toString())
                        userStatus = p0.child("userStatus").getValue().toString()
                        if(p0.child("userIcImage").getValue().toString() != ""){
                            btnCamera.alpha = 0f
                            Picasso.get().load(p0.child("userIcImage").getValue().toString()).into(editIcImgView)
                        }
                    }

                    override fun onCancelled(p0: DatabaseError) {

                    }
                })
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })

        btnUserPic.setOnClickListener{
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }

        btnCamera.setOnClickListener{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if (checkSelfPermission(Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_DENIED ||
                    checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_DENIED){
                    //permission was not enabled
                    val permission = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    //show popup to request permission
                    requestPermissions(permission, PERMISSION_CODE)
                }
                else{
                    //permission already granted
                    openCamera()
                }
            }
            else{
                //system os is < marshmallow
                openCamera()
            }
        }

        btnEditAccount.setOnClickListener{

            if(selectedPhotoUri != null || image_uri != null){
                var first = ""
                var second = ""

                if(selectedPhotoUri != null){
                    val filename = UUID.randomUUID().toString()
                    val ref = FirebaseStorage.getInstance().getReference("/users/$filename")

                    ref.putFile(selectedPhotoUri!!)
                        .addOnSuccessListener {
                            Log.d("register activity", "Successfully uploaded image: ${it.metadata?.path}")

                            ref.downloadUrl.addOnSuccessListener {
                                Log.d("registered", "File Location: $it")
                                savUserData(it.toString(),"")
                            }
                        }
                }

                if(image_uri != null){
                    if(image_uri != null){
                        val filename = UUID.randomUUID().toString()
                        val ref = FirebaseStorage.getInstance().getReference("/users/$filename")

                        ref.putFile(image_uri!!)
                            .addOnSuccessListener {
                                Log.d("register activity", "Successfully uploaded image: ${it.metadata?.path}")

                                ref.downloadUrl.addOnSuccessListener {
                                    Log.d("registered", "File Location: $it")
                                    savUserData("", it.toString())
                                }
                            }
                    }
                }
            }else{
                savUserData("","")
            }
    }

        btnCancel.setOnClickListener{
            finish()
        }

    }


    private fun savUserData(userImageUrl: String, userIcUrl: String) {
        val databaseForum = FirebaseDatabase.getInstance().getReference("user")

        val ref = FirebaseDatabase.getInstance().getReference("user")
        val name = editUserName.text
        val ic = editUserIC.text
        val phone = editUserPhone.text
        val email = editUserEmail.text
        val password = editPass.text

        val user = User(logId, name.toString(), email.toString(), password.toString(), userImageUrl, userStatus, userIcUrl, ic.toString(), phone.toString())

        ref.child(logId).setValue(user)
            .addOnCompleteListener{
                Toast.makeText(this, "Account Updated", Toast.LENGTH_LONG).show()
                finish()
            }
            .addOnFailureListener{
                Toast.makeText(this, "Failed to update account", Toast.LENGTH_SHORT).show()
            }
        onBackPressed()
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

            editUserImg.setImageBitmap(bitmap)

            btnUserPic.alpha = 0f
        }
        if (requestCode == 1 && resultCode == Activity.RESULT_OK){
            //set image captured to image view
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, image_uri)
            editIcImgView.setImageBitmap(bitmap)
            btnCamera.alpha = 0f
        }
    }

    private fun openCamera() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "New Picture")
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera")
        image_uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        //camera intent
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri)
        startActivityForResult(cameraIntent, 1)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        //called when user presses ALLOW or DENY from Permission Request Popup
        when(requestCode){
            PERMISSION_CODE -> {
                if (grantResults.size > 0 && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED){
                    //permission from popup was granted
                    openCamera()
                }
                else{
                    //permission from popup was denied
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }



}