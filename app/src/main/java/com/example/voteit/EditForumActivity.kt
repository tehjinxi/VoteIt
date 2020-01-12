package com.example.voteit

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_edit_forum.*
import kotlinx.android.synthetic.main.forum_list.view.*

class EditForumActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_forum)

        val actionbar = supportActionBar
        actionbar!!.title = "Edit forum"
        actionbar.setDisplayHomeAsUpEnabled(true)

        fetchForum()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    companion object {
        val USER_KEY = "USER_KEY"
    }

    private fun fetchForum() {
        val ref = FirebaseDatabase.getInstance().getReference("/forum")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {

            override fun onDataChange(p0: DataSnapshot) {
                val adapter = GroupAdapter<GroupieViewHolder>()

                p0.children.forEach {
                    Log.d("NewMessage", it.toString())
                    val forum = it.getValue(Forum::class.java)
                    if (forum != null) {
                        adapter.add(ForumItem(forum))
                    }
                }

                adapter.setOnItemClickListener(){ item, view ->
                    val forum = item as ForumItem

                    val intent = Intent (view.context, EditForumDetailActivity::class.java)
                    intent.putExtra(USER_KEY, forum.forum)
                    startActivity(intent)
                    finish()
                }

                recycleView_forumList.adapter = adapter
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }
}

class ForumItem(val forum: Forum): Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.btnForumList.text = forum.title
    }

    override fun getLayout(): Int {
        return R.layout.forum_list
    }
}
