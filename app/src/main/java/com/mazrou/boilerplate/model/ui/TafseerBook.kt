package com.mazrou.boilerplate.model.ui

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Entity(tableName = "tafseer_book")
data class TafseerBook (
    @PrimaryKey(autoGenerate = false)
    @Expose
    @SerializedName("id")
    val id : Int ,
    @Expose
    @SerializedName("name")
    val name : String ,
    @Expose
    @SerializedName("language")
    val language : String ,
    @Expose
    @SerializedName("author")
    val author : String ,
    @Expose
    @SerializedName("book_name")
    val bookName : String
    ) {
    override fun toString(): String {
        return bookName
    }
}