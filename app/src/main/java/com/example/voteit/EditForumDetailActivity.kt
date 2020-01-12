package com.example.voteit

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_edit_forum_detail.*

class EditForumDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_forum_detail)

        val forum = intent.getParcelableExtra<Forum>(EditForumActivity.USER_KEY)
        //supportActionBar?.title = forum.title

        val actionbar = supportActionBar
        actionbar!!.title = "Edit forum detail"
        actionbar.setDisplayHomeAsUpEnabled(true)

        edit_title.setText(forum.title)
        edit_desc.setText(forum.desc)
        Picasso.get().load(forum.forumImage).into(forum_image)

        btn_edit.setOnClickListener{
            editForum(forum)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun editForum(forum: Forum){
        val databaseForum = FirebaseDatabase.getInstance().getReference("forum")
        val id = forum.id
        val title = edit_title.text.toString()
        val desc = edit_desc.text.toString()
        val forum = Forum(id, title, desc, forum.date, forum.time, forum.forumImage)

        if(title.isEmpty() || desc.isEmpty()){
            Toast.makeText(this, "Please do not leave an empty field", Toast.LENGTH_LONG).show()
            return
        }

        databaseForum.child(id).setValue(forum)
            .addOnCompleteListener{
                Toast.makeText(this, "Forum Updated", Toast.LENGTH_LONG).show()
                val intent = Intent (this, EditForumActivity::class.java)
                startActivity(intent)
                finish()
            }
            .addOnFailureListener{
                Toast.makeText(this, "Failed to update forum", Toast.LENGTH_SHORT).show()
            }
    }
}
