package com.example.voteit
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class User(
    val id: String,
    val name: String,
    val email: String,
    val password: String,
    val userImage: String,
    val userStatus: String,
    val userIcImage: String,
    val userIC: String,
    val userPhone: String
): Parcelable {
    constructor() : this("", "", "", "", "", "",
        "", "", "")
}