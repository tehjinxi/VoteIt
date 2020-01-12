package com.example.voteit

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Account (
    val ic: String,
    val email:String,
    val name:String,
    val password: String,
    val phone: String
):Parcelable{
    constructor() : this("", "", "", "", "")
}