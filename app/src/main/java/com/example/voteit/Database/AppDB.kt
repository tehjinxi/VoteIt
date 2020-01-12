package com.example.voteit.Database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast
import androidx.core.content.contentValuesOf
import kotlin.coroutines.coroutineContext


val DATABASE_NAME="MyDB"
val TABLE_NAME="Members"
val COL_NAME="name"
val COL_IC="ic"
val COL_EMAIL="email"
val COL_PASS="pass"
val COL_PHONE="phone"
val COL_IMG="image"

class DataHandle(context: Context):SQLiteOpenHelper(context, DATABASE_NAME,null,1) {
    override fun onCreate(db: SQLiteDatabase?) {
        val createTable="CREATE TABLE "+ TABLE_NAME+"("+
                COL_IC+" VARCHAR(12),"+
                COL_PASS+" VARCHAR(20),"+
                COL_NAME+" VARCHAR(20),"+
                COL_EMAIL+" VARCHAR(50),"+
                COL_PHONE+" VARCHAR(20),"+
                COL_IMG+" VARCHAR(100))";

        db?.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }

    fun insertData(member: Member){
        val db=this.writableDatabase
        var cv=ContentValues()
        cv.put(COL_IC,member.ic)
        cv.put(COL_PASS,member.pass)
        cv.put(COL_NAME,member.name)
        cv.put(COL_EMAIL,member.email)
        cv.put(COL_PHONE,member.phone)
        cv.put(COL_IMG,member.image)
        db.insert(TABLE_NAME,null,cv)
    }

    fun readData(ic:String,pass:String):Boolean{
        var columns= arrayOf(COL_IC)
        var selection="$COL_IC=? AND $COL_PASS=?"
        var selectionArgs= arrayOf(ic,pass)

        val db=this.readableDatabase

        val cursor = db.query(
            TABLE_NAME, //Table to query
            columns, //columns to return
            selection, //columns for the WHERE clause
            selectionArgs, //The values for the WHERE clause
            null,  //group the rows
            null, //filter by row groups
            null) //The sort order

        val cursorCount = cursor.count
        cursor.close()
        db.close()

        if (cursorCount > 0)
            return true

        return false

    }

    }
