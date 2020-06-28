package com.example.text2speech.ServerHelper

import android.util.Log
import android.widget.Toast
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

class Helper {
    interface APIService {
        @GET("/users/{user}")
        fun greetUser(@Path("user") user: String): Call<ResponseBody>

        @Headers("Content-type: application/json")
        @POST("/api/post_some_data")
        fun getVectors(@Body body: JsonObject): Call<ResponseBody>
    }

    companion object {
        private val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.0.134:5000")
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build()

        var service = retrofit.create(APIService::class.java)
    }

    fun POST(jsonObj:JsonObject):JSONObject{
        var msg:String? = ""
        service.getVectors(jsonObj).enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("service post",t.message)
            }

            override fun onResponse(
                call: Call<ResponseBody>,
                response: retrofit2.Response<ResponseBody>
            ) {
                msg = response.body()?.string()
                println("---TTTT :: POST msg from server :: " + msg)
            }

        })
        return JSONObject(msg!!)
    }

    fun GET():JSONObject{
        var msg:String? = ""
        service.greetUser("HI").enqueue( object:Callback<ResponseBody>{
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("service get", t.message)
            }

            override fun onResponse(
                call: Call<ResponseBody>,
                response: retrofit2.Response<ResponseBody>
            ) {
                if (response.isSuccessful) {
                    msg = response.body()?.string()
                    println("---TTTT :: GET msg from server :: " + msg)

                }
            }

        })
        return JSONObject(msg!!)
    }


}