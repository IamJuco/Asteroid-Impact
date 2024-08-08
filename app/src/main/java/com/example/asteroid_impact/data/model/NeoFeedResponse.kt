package com.example.asteroid_impact.data.model

import com.google.gson.annotations.SerializedName

data class NeoFeedResponse(
    @SerializedName("near_earth_objects") val nearEarthObjects: Map<String, List<NearEarthObject>>
)

data class NearEarthObject(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("close_approach_data") val closeApproachData: List<CloseApproachData>
)

data class CloseApproachData(
    @SerializedName("close_approach_date") val closeApproachDate: String, // 소행성이 지구에 가장 가까워진 날짜, YYYY-MM-DD 형식
    @SerializedName("epoch_date_close_approach") val epochDateCloseApproach: Long, // close_approach_date의 날짜를 초 형식으로 변환한 값
    @SerializedName("miss_distance") val missDistance: MissDistance // 소행성과 지구의 거리, 지구와 충돌 가능성을 평가
)

data class MissDistance(
    @SerializedName("astronomical") val astronomical: String, // 천문단위 (AU)로 표시된 거리, 1AU = 지구와 태양 간의 평균 거리
    @SerializedName("kilometers") val kilometers: String // 킬로미터 단위, NEO와 지구간의 실제 거리
)
