package com.kurume_nct.studybattle.client

import com.kurume_nct.studybattle.model.*
import io.reactivex.Observable
import okhttp3.MultipartBody
import retrofit2.http.*

/**
 * Created by hanah on 7/31/2017.
 */
interface Server {

    @FormUrlEncoded
    @POST("/register")
    fun register(
            @Field("displayName") displayName: String,
            @Field("userName") userName: String,
            @Field("password") password: String,
            @Field("iconImageId") iconImageId: Int
    ): Observable<Unit>

    @FormUrlEncoded
    @POST("/register")
    fun register(
            @Field("displayName") displayName: String,
            @Field("userName") userName: String,
            @Field("password") password: String
    ): Observable<Unit>

    @FormUrlEncoded
    @POST("/login")
    fun login(
            @Field("userName") userName: String,
            @Field("password") password: String
    ): Observable<LoginResult>

    @FormUrlEncoded
    @POST("/verify_authentication")
    fun verifyAuthentication(
            @Field("authenticationKey") authenticationKey: String
    ): Observable<User>

    @FormUrlEncoded
    @POST("/group/new")
    fun createGroup(
            @Field("authenticationKey") authenticationKey: String,
            @Field("name") name: String
    ): Observable<Group>

    @FormUrlEncoded
    @POST("/group/join")
    fun joinGroup(
            @Field("authenticationKey") authenticationKey: String,
            @Field("groupId") groupId: Int
    ): Observable<Unit>

    @FormUrlEncoded
    @POST("/group/attach")
    fun attachToGroup(
            @Field("authenticationKey") authenticationKey: String,
            @Field("groupId") groupId: Int,
            @Field("userId") userId: Int
    ): Observable<Unit>

    @FormUrlEncoded
    @POST("/group/{id}")
    fun getGroup(
            @Path("id") groupId: Int,
            @Field("authenticationKey") authenticationKey: String
    ): Observable<Group>

    @Multipart
    @POST("/image/upload")
    fun uploadImage(
            @Part() image: MultipartBody.Part
    ): Observable<Image>

    @GET("/image_by_id/{id}")
    fun getImageById(
            @Path("id") id: Int
    ): Observable<Image>

    @POST("/problem/create")
    @Headers(
            "Accept: application/JSON",
            "Content-type: application/JSON",
            "Data-Type: JSON",
            "Script-Charset: utf-8"
    )
    fun createProblem(
            @Body body: String
    ): Observable<IDResponse>

    @FormUrlEncoded
    @POST("/problem/{id}")
    fun getProblem(
            @Field("authenticationKey") authenticationKey: String,
            @Path("id") id: Int
    ): Observable<Problem>

    @FormUrlEncoded
    @POST("/problem/assigned")
    fun getAssignedProblems(
            @Field("authenticationKey") authenticationKey: String,
            @Field("groupId") groupId: Int
    ): Observable<List<Problem>>

    @FormUrlEncoded
    @POST("/problem/request_new")
    fun requestNewProblem(
            @Field("authenticationKey") authenticationKey: String,
            @Field("groupId") groupId: Int
    ): Observable<ProblemRequestResponse>

    @FormUrlEncoded
    @POST("/solution/create")
    fun createSolution(
            @Field("authenticationKey") authenticationKey: String,
            @Field("text") text: String,
            @Field("problemId") problemId: Int,
            @Field("imageIds[]") imageIds: IntArray
    ): Observable<IDResponse>

    @FormUrlEncoded
    @POST("/solution/{id}")
    fun getSolution(
            @Field("authenticationKey") authenticationKey: String,
            @Path("id") id: Int
    ): Observable<Solution>
}