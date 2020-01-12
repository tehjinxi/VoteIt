package com.example.voteit

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.fragment_history.*
import kotlinx.android.synthetic.main.row_history.view.*
import kotlinx.android.synthetic.main.row_posts.view.*

/**
 * A simple [Fragment] subclass.
 */
class HistoryFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        fetchForum()
        return inflater.inflate(R.layout.fragment_history, container, false)

    }

    private fun fetchForum() {
        val ref = FirebaseDatabase.getInstance().getReference("/forum")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(p0: DataSnapshot) {
                val adapter = GroupAdapter<GroupieViewHolder>()

                p0.children.forEach {
                    Log.d("NewMessage", it.toString())
                    val forum = it.getValue(Forum::class.java)
                    if (forum != null) {
                        adapter.add(ForumItem(forum))
                    }
                }

                recycleView_history.adapter = adapter
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }


    class ForumItem(val forum: Forum) : Item<GroupieViewHolder>() {
        override fun bind(viewHolder: GroupieViewHolder, position: Int) {

            val unicode1 = 0x1F44D
            val unicode2 = 0x1F44E
            var id = ""

            val idRef = FirebaseDatabase.getInstance().getReference("login")
            idRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    id = p0.getValue().toString()
                }

                override fun onCancelled(p0: DatabaseError) {

                }
            })

            val likeRef = FirebaseDatabase.getInstance().reference.child("likes").child(forum.id)
            likeRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    if (p0.child(id).exists()) {
                        viewHolder.itemView.historyTitle.text = forum.title
                        viewHolder.itemView.historyDate.text = forum.date + " at " + forum.time
                        viewHolder.itemView.historyDesc.text = "You have up voted this post recently"
                    }
                }
                override fun onCancelled(p0: DatabaseError) {
                }
            })

            val dislikeRef = FirebaseDatabase.getInstance().reference.child("dislikes").child(forum.id)
            dislikeRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    if (p0.child(id).exists()) {
                        viewHolder.itemView.historyTitle.text = forum.title
                        viewHolder.itemView.historyDate.text = forum.date + " at " + forum.time
                        viewHolder.itemView.historyDesc.text = "You have down voted this post recently"
                    }
                }
                override fun onCancelled(p0: DatabaseError) {
                }
            })


        }

        override fun getLayout(): Int {
            return R.layout.row_history
        }
    }
}