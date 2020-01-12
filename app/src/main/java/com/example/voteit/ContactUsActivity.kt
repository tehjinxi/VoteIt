package com.example.voteit

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class ContactUsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_us)

        //actionbar
        val actionbar = supportActionBar
        //set actionbar title
        actionbar!!.title = "Contact us"
        //set back button
        actionbar.setDisplayHomeAsUpEnabled(true)
        //actionbar.setDisplayHomeAsUpEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
