package com.mazrou.boilerplate.model.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Entity(tableName = "reader")
data class Reader(
    @Expose
    @PrimaryKey(autoGenerate = false)
    @SerializedName("identifier")
    val identifier : String ,
    @Expose
    @SerializedName("englishName")
    val englishName : String
){
    override fun toString(): String {
        return englishName
    }
}

