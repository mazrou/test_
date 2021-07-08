package com.mazrou.boilerplate.network.network_response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.mazrou.boilerplate.model.database.Reader


data class ReaderResponse (
    @Expose
    @SerializedName("data")
    val data : List<Reader>
    )