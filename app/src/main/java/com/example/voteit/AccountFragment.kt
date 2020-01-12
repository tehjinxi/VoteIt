package com.example.voteit


import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_account.*
import kotlinx.android.synthetic.main.row_posts.view.*

/**
 * A simple [Fragment] subclass.
 */
class AccountFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val idref = FirebaseDatabase.getInstance().getReference("login")
        idref.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {

                val ref = FirebaseDatabase.getInstance().getReference("user").child( p0.getValue().toString() )
                ref.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(p0: DataSnapshot) {

                        if(p0.child("userImage").getValue().toString() != ""){
                            Picasso.get().load(p0.child("userImage").getValue().toString()).into(userImg)
                        }else{
                            userImg.setImageResource(R.drawable.blank_profile_picture_973460_1280)
                        }
                        userName.text = p0.child("name").getValue().toString()
                        userIC.text = p0.child("userIC").getValue().toString()
                        userEmail.text = p0.child("email").getValue().toString()
                        userPhone.text = p0.child("userPhone").getValue().toString()
                        userStatus.text = p0.child("userStatus").getValue().toString()

                        if(userName.text != "admin"){
                            btnPostForum.visibility = View.GONE
                        }
                    }

                    override fun onCancelled(p0: DatabaseError) {

                    }
                })
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })



        btnEdit.setOnClickListener {
            val intent = Intent(activity, AccountActivity::class.java)
            activity?.startActivity(intent)
        }

        btnPostForum.setOnClickListener {

            //call fragment
            //val fragment = PostForumFragment()
            //val fragmentTransaction = activity?.supportFragmentManager?.beginTransaction()
            //fragmentTransaction?.replace(R.id.fragmentContainer, fragment)
            //fragmentTransaction?.commit()

            //call activity
            val intent = Intent(activity, ManageForumActivity::class.java)
            activity?.startActivity(intent)
        }

        btnLogOut.setOnClickListener{
            val ref = FirebaseDatabase.getInstance().getReference("login")
            ref.removeValue()

            val intent = Intent(activity, LoginActivity::class.java)
            activity?.startActivity(intent)
        }
    }
}
