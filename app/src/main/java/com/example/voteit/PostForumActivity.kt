package com.example.voteit

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
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
import java.text.SimpleDateFormat
import java.util.*

class PostForumActivity : AppCompatActivity() {

    var id: String = ""
    var title: String = ""
    var desc: String = ""
    var postDate: String = ""
    var postTime: String = ""

    var selectedPhotoUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_forum)

        //actionbar
        val actionbar = supportActionBar
        //set actionbar title
        actionbar!!.title = "Post forum"
        //set back button
        actionbar.setDisplayHomeAsUpEnabled(true)
        //actionbar.setDisplayHomeAsUpEnabled(true)

        btnPostForumPhoto.setOnClickListener{
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }

        btnPost.setOnClickListener{
            postForum()
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

            forumPhotoView.setImageBitmap(bitmap)

            btnPostForumPhoto.alpha = 0f
        }
    }

    private fun postForum(){
        title = findViewById<EditText>(R.id.editForumTitle).text.toString()
        desc = findViewById<EditText>(R.id.editForumDesc).text.toString()

        if(title.isEmpty() || desc.isEmpty()){
            Toast.makeText(this, "Do not leave an empty field",Toast.LENGTH_LONG).show()
            return
        }

        uploadImageToFirebaseStorage()
    }

    private fun uploadImageToFirebaseStorage() {
        if (selectedPhotoUri == null){
            Toast.makeText(this, "Please select an image",Toast.LENGTH_LONG).show()
            return
        }

        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")

        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                Log.d("Post forum activity", "Successfully uploaded image: ${it.metadata?.path}")

                ref.downloadUrl.addOnSuccessListener {
                    Log.d("post forum", "File Location: $it")
                    saveForumToFirebaseDatabase(it.toString())
                }
            }
    }

    private fun saveForumToFirebaseDatabase(profileImageUrl: String) {
        val databaseForum = FirebaseDatabase.getInstance().getReference("forum")

        id = databaseForum.push().key.toString()
        val date = Calendar.getInstance()
        postDate = SimpleDateFormat("d MMMM Y").format(date.time)
        postTime = SimpleDateFormat("H:m").format(date.time)

        val forum = Forum(id, title, desc, postDate, postTime, profileImageUrl)

        databaseForum.child(id).setValue(forum)
            .addOnCompleteListener{
                Toast.makeText(this, "Forum Posted",Toast.LENGTH_LONG).show()

                onBackPressed()
            }
            .addOnFailureListener{
                Toast.makeText(this, "Failed to post forum", Toast.LENGTH_SHORT).show()
            }

    }
}
