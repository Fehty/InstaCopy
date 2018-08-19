package com.fehty.instacopy.Activity.API

import com.fehty.instacopy.Activity.Data.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiMethods {

    @GET("/all")
    fun getAllPhotos(): Call<List<MessageData>>

    @GET("/feed/user/{userId}")
    fun getUserMessagesById(@Path("userId") userId: Int): Call<List<MessageData>>

    @GET("/feed/user_messages")
    fun getUserMessagesByToken(@Query("token") token: String): Call<List<MessageData>>

    @GET("/user/profile")
    fun getUserNameEmailByUserId(@Query("userId") userId: Int): Call<UserProfileData>

    @GET("/user/profile")
    fun getUserNameEmailByToken(@Query("token") token: String): Call<UserProfileData>

    @POST("/register")
    fun register(@Body registrationData: RegistrationData): Call<String>

    @POST("/login")
    fun login(@Body loginData: LoginData): Call<UserTokenData>

    @POST("/message/{messageId}/comment")
    fun addComment(@Path("messageId") messageId: Int, @Body comment: AddCommentData, @Query("token") token: String): Call<String>

    @POST("/like/{messageId}")
    fun addLike(@Path("messageId") messageId: Int, @Query("token") token: String): Call<List<UsersWhoLikedPostData>>

    @FormUrlEncoded
    @POST("/edit")
    fun editMessage(@Field("messageId") messageId: Int, @Field("text") text: String): Call<MessageData>

    @FormUrlEncoded
    @POST("/delete")
    fun deleteMessage(@Field("messageId") messageId: Int): Call<MessageData>

    @Multipart
    @POST("/upload")
    fun uploadMessage(@Part("text") text: RequestBody, @Part file: MultipartBody.Part, @Part("token") token: RequestBody): Call<String>

    @Multipart
    @PUT("/user/avatar")
    fun setUserAvatar(@Part("token") token: RequestBody, @Part file: MultipartBody.Part): Call<String>

    @PUT("/user/profile")
    fun changeUserName(@Query("token") token: String, @Body newName: ChangeUserNameData): Call<Void>
}