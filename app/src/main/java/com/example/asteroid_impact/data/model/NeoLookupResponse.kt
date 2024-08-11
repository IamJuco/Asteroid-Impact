package com.example.asteroid_impact.data.model

import com.google.gson.annotations.SerializedName

data class NeoLookupResponse(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("close_approach_data") val closeApproachData: List<CloseApproachData>
)
