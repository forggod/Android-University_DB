package com.example.second34_2.api

import com.example.second34_2.data.*
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ServerAPI {
    @GET("faculties")
    fun getFaculty(): Call<List<Faculty>>

    @GET("groups")
    fun getGroups(): Call<List<Group>>

    @GET("students")
    fun getStudents(): Call<List<Student>>

    @POST("faculties")
    fun postFaculty(@Body faculties: List<Faculty>): Call<Faculties>

    @POST("groups")
    fun postGroups(@Body groups: List<Group>): Call<Groups>

    @POST("students")
    fun postStudents(@Body students: List<Student>): Call<Students>
}