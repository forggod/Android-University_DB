package com.example.second34_2.api

import com.example.second34_2.data.Faculties
import com.example.second34_2.data.Group
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ServerAPI {
    @GET("?faculty")
    fun getFaculty(): Call<Faculties>

    @GET("?groups")
    suspend fun getGroups(@Query("faculty_id") id: Long): Call<List<Group>>
}