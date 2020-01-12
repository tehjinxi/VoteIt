package com.example.voteit
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Forum (
    val id: String,
    val title:String,
    val desc:String,
    val date: String,
    val time: String,
    val forumImage: String
): Parcelable  {
    constructor() : this("", "", "", "", "", "")
}