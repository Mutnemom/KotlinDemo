package mutnemom.android.kotlindemo.services

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Streaming

interface RetrofitInterface {

    @GET("files/Node-Android-Chat.zip")
    @Streaming
    fun downloadFile(): Call<ResponseBody>

}
