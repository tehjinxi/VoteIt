package com.example.voteit

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_delete_forum_detail.*
import kotlinx.android.synthetic.main.activity_edit_forum_detail.forum_image
import com.google.firebase.storage.FirebaseStorage

class DeleteForumDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_delete_forum_detail)

        val forum = intent.getParcelableExtra<Forum>(EditForumActivity.USER_KEY)
        //supportActionBar?.title = forum.title

        val actionbar = supportActionBar
        actionbar!!.title = "Delete forum detail"
        actionbar.setDisplayHomeAsUpEnabled(true)

        titleDetail.text = forum.title
        descDetail.text = forum.desc
        Picasso.get().load(forum.forumImage).into(forum_image)

        btn_delete.setOnClickListener{
            val alert = AlertDialog.Builder(this)

            alert.setIcon(R.drawable.baseline_delete_outline_black_18dp)
            alert.setTitle("Delete Confirmation")
            alert.setMessage("Want to delete " + forum.title + "?")
            alert.setPositiveButton("Yes") { dialog, id ->
                deleteForum(forum)
            }
            alert.setNegativeButton("No") { dialog, id ->

            }
            alert.show()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun deleteForum(forum: Forum){

        val photoRef = FirebaseStorage.getInstance().getReferenceFromUrl(forum.forumImage)

        val databaseForum = FirebaseDatabase.getInstance().getReference("forum") .child(forum.id)
        databaseForum.removeValue()
            .addOnCompleteListener{
                photoRef.delete()

                Toast.makeText(this, "Forum deleted", Toast.LENGTH_LONG).show()
                val intent = Intent (this, DeleteForumActivity::class.java)
                startActivity(intent)
                finish()

            }
            .addOnFailureListener{
                Toast.makeText(this, "Failed to delete forum", Toast.LENGTH_SHORT).show()
            }
    }
}
