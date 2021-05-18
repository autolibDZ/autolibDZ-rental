package com.sil1.autolibdz_rental.data.api

import com.sil1.autolibdz_rental.data.model.Borne
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*


interface ServiceProvider {
    // Getting all bornes of our system
    @GET("api/bornes/all")
    fun getAllBornes(/*@Header("Authorization") token: String*/): Call<List<Borne>>
}