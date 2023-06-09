package com.example.second34_2.api

import com.example.second34_2.data.Faculties
import com.example.second34_2.data.Faculty
import com.example.second34_2.data.Group
import com.example.second34_2.data.Student
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ServerAPI {
    // *******************************************
    //              About Faculty
    // *******************************************
    @GET("faculties")
    fun getFaculty(): Call<Faculties>

    @POST("faculties")
    fun postFaculty(@Body faculties: List<Faculty>): Call<Faculties>

    // *******************************************
    //              About Group
    // *******************************************
    @GET("groups")
    suspend fun getGroups(@Query("faculty_id") id: Long): Call<List<Group>>

    @POST("groups")
    fun postGroups(@Body post: POST): Response<POST>

    // *******************************************
    //              About Student
    // *******************************************
    @GET("students")
    suspend fun getStudents(@Query("group_id") id: Long): Call<List<Student>>

    @POST("students")
    fun postStudents(@Body post: POST): Response<POST>
}