package com.example.voteit


import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.createDeviceProtectedStorageContext
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_edit_forum.*
import kotlinx.android.synthetic.main.activity_edit_forum_detail.*
import kotlinx.android.synthetic.main.forum_list.view.*
import kotlinx.android.synthetic.main.fragment_account.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.loginhome.*
import kotlinx.android.synthetic.main.row_posts.view.*
import kotlinx.android.synthetic.main.loginhome.view.*
import kotlinx.android.synthetic.main.row_posts.*
import org.w3c.dom.Comment
import kotlin.coroutines.coroutineContext
import kotlinx.android.synthetic.main.fragment_home.*


/**
 * A simple [Fragment] subclass.
 */

class HomeFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        fetchPost()
        return inflater.inflate(R.layout.fragment_home, container, false)

    }

    private fun fetchPost() {
        val ref = FirebaseDatabase.getInstance().getReference("/forum")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(p0: DataSnapshot) {
                val adapter = GroupAdapter<GroupieViewHolder>()

                p0.children.forEach {
                    Log.d("NewMessage", it.toString())
                    val forum = it.getValue(Forum::class.java)
                    if (forum != null) {
                        adapter.add(Items(forum))
                    }
                }
                homeRecyclerView.adapter = adapter
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    class Items(val forum: Forum) : Item<GroupieViewHolder>() {
        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
            viewHolder.itemView.postTitle.text = forum.title
            viewHolder.itemView.postDesc.text = forum.desc
            viewHolder.itemView.postDate.text = forum.date + " at " + forum.time
            Picasso.get().load(forum.forumImage).into(viewHolder.itemView.postImage)

            var id = ""

            val idRef = FirebaseDatabase.getInstance().getReference("login")
            idRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    id = p0.getValue().toString()
                }

                override fun onCancelled(p0: DatabaseError) {

                }
            })

            ///////////numOfLikes
            val numOfLikes = FirebaseDatabase.getInstance().reference.child("likes").child(forum.id)
            numOfLikes.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    if (p0.exists()) {
                        viewHolder.itemView.postLike.text = p0.childrenCount.toString() + " " + "Up Votes"
                    }
                }
                override fun onCancelled(p0: DatabaseError) {
                }
            })
            ////////////////////////////////////////////////////////////////////////////////////////
            ///////////numOfDislikes
            val numOfDisLikes = FirebaseDatabase.getInstance().reference.child("dislikes").child(forum.id)
            numOfDisLikes.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    if (p0.exists()) {
                        viewHolder.itemView.postDislike.text = p0.childrenCount.toString() + " " + "Down Votes"
                    }
                }
                override fun onCancelled(p0: DatabaseError) {
                }
            })
            ////////////////////////////////////////////////////////////////////////////////////////
            ////////////Isliked
            val likeRef = FirebaseDatabase.getInstance().reference.child("likes").child(forum.id)
            likeRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    if (p0.child(id).exists()) {
                        viewHolder.itemView.likeBtn.text = "Liked"
                        viewHolder.itemView.likeBtn.setCompoundDrawablesWithIntrinsicBounds(
                            R.drawable.ic_liked,
                            0,
                            0,
                            0
                        )
                    }else{
                        viewHolder.itemView.likeBtn.text = "Like"
                        viewHolder.itemView.likeBtn.setCompoundDrawablesWithIntrinsicBounds(
                            R.drawable.ic_like_black,
                            0,
                            0,
                            0
                        
                        )
                    }
                }
                override fun onCancelled(p0: DatabaseError) {
                }
            })
            //////////////////////////////////////////////////////////////////////////////////////////
            ///////IsDisliked
            val dislikeRef = FirebaseDatabase.getInstance().reference.child("dislikes").child(forum.id)
            dislikeRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    if (p0.child(id).exists()) {
                        viewHolder.itemView.dislikeBtn.text = "Disliked"
                        viewHolder.itemView.dislikeBtn.setCompoundDrawablesWithIntrinsicBounds(
                            R.drawable.ic_disliked,
                            0,
                            0,
                            0
                        )
                    }else{
                        viewHolder.itemView.dislikeBtn.text = "Dislike"
                        viewHolder.itemView.dislikeBtn.setCompoundDrawablesWithIntrinsicBounds(
                            R.drawable.ic_dislike_black,
                            0,
                            0,
                            0
                        )
                    }
                }
                override fun onCancelled(p0: DatabaseError) {
                }
            })
            /////////////////////////////////////////////////////////////////////////////////////////
            //set like button
            viewHolder.itemView.likeBtn.setOnClickListener(object : View.OnClickListener {
                val likes = FirebaseDatabase.getInstance().getReference("likes")
                override fun onClick(v: View?) {
                    if (viewHolder.itemView.likeBtn.text == "Like" && viewHolder.itemView.dislikeBtn.text == "Dislike") {
                        viewHolder.itemView.likeBtn.text = "Liked"
                        viewHolder.itemView.likeBtn.setCompoundDrawablesWithIntrinsicBounds(
                            R.drawable.ic_liked,
                            0,
                            0,
                            0
                        )
                        likes.child(forum.id).child(id).setValue(true)

                    } else {
                        viewHolder.itemView.likeBtn.text = "Like"
                        viewHolder.itemView.likeBtn.setCompoundDrawablesWithIntrinsicBounds(
                            R.drawable.ic_like_black,
                            0,
                            0,
                            0
                        )

                        likes.child(forum.id).child(id).removeValue()
                    }
                }
            })
            ///////////////////////////////////////////////////////////////////////////////////////////
            //set dislike button
            viewHolder.itemView.dislikeBtn.setOnClickListener(object : View.OnClickListener {
                val dislikes = FirebaseDatabase.getInstance().getReference("dislikes")
                override fun onClick(v: View?) {
                    if (viewHolder.itemView.likeBtn.text == "Like" && viewHolder.itemView.dislikeBtn.text == "Dislike") {
                        viewHolder.itemView.dislikeBtn.text = "Disliked"
                        viewHolder.itemView.dislikeBtn.setCompoundDrawablesWithIntrinsicBounds(
                            R.drawable.ic_disliked,
                            0,
                            0,
                            0
                        )
                        dislikes.child(forum.id).child(id).setValue(true)
                    } else {
                        viewHolder.itemView.dislikeBtn.text = "Dislike"
                        viewHolder.itemView.dislikeBtn.setCompoundDrawablesWithIntrinsicBounds(
                            R.drawable.ic_dislike_black,
                            0,
                            0,
                            0
                        )

                        dislikes.child(forum.id).child(id).removeValue()
                    }
                }
            })
            ////////////////////////////////////////////////////////////////////////////////////////
        }

        override fun getLayout(): Int {
            return R.layout.row_posts
        }
    }
}