package com.example.voteit

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_manage_forum.*
import kotlinx.android.synthetic.main.fragment_account.btnPostForum

class ManageForumActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_forum)

        //actionbar
        val actionbar = supportActionBar
        //set actionbar title
        actionbar!!.title = "Manage forum"
        //set back button
        actionbar.setDisplayHomeAsUpEnabled(true)
        //actionbar.setDisplayHomeAsUpEnabled(true)

        btnPostForum.setOnClickListener{
            //call activity
            val intent = Intent (this, PostForumActivity::class.java)
            startActivity(intent)
        }

        btnEditForum.setOnClickListener{
            //call activity
            val intent = Intent (this, EditForumActivity::class.java)
            startActivity(intent)
        }

        btnDeleteForum.setOnClickListener{
            //call activity
            val intent = Intent (this, DeleteForumActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
