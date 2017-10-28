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

    @GET("/user/by_id/{id}")
    fun getUser(@Path("id") id: Int): Observable<User>

    @GET("/user/search")
    fun searchUsers(@Query("query") query: String): Observable<List<User>>

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

    @GET("/group/leave")
    fun leaveGroup(
            @Query("authenticationKey") authenticationKey: String,
            @Query("groupId") groupId: Int
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

    @GET("/group/joined")
    fun getJoinedGroups(@Query("authenticationKey") authenticationKey: String)
            : Observable<List<Group>>

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

    @GET("/problem/judged")
    fun getMyJudgedProblems(
            @Query("authenticationKey") authenticationKey: String,
            @Query("groupId") groupId: Int
    ): Observable<List<Problem>>

    @GET("/problem/judging")
    fun getMyJudgingProblems(
            @Query("authenticationKey") authenticationKey: String,
            @Query("groupId") groupId: Int
    ): Observable<List<Problem>>

    @GET("/problem/collecting")
    fun getMyCollectingProblems(
            @Query("authenticationKey") authenticationKey: String,
            @Query("groupId") groupId: Int
    ): Observable<List<Problem>>

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

    @GET("/problem/pass")
    fun passProblem(
            @Query("authenticationKey") authenticationKey: String,
            @Query("problemId") problemId: Int
    ): Observable<Unit>

    @GET("/problem/open")
    fun openProblem(
            @Query("authenticationKey") authenticationKey: String,
            @Query("problemId") problemId: Int
    ): Observable<ProblemOpenResponse>

    @FormUrlEncoded
    @POST("/solution/create")
    fun createSolution(
            @Field("authenticationKey") authenticationKey: String,
            @Field("text") text: String,
            @Field("problemId") problemId: Int,
            @Field("imageIds[]") imageIds: IntArray,
            @Field("attachedItemId") attachedItemId: Int
    ): Observable<IDResponse>

    @FormUrlEncoded
    @POST("/solution/{id}")
    fun getSolution(
            @Field("authenticationKey") authenticationKey: String,
            @Path("id") id: Int
    ): Observable<Solution>

    @FormUrlEncoded
    @POST("/solution/judge")
    fun judgeSolution(
            @Field("authenticationKey") authenticationKey: String,
            @Field("id") id: Int,
            @Field("isAccepted") isAccepted: Boolean
    ): Observable<Unit>

    @GET("/my_solution/judged")
    fun getJudgedMySolutions(
            @Query("authenticationKey") authenticationKey: String,
            @Query("groupId") groupId: Int
    ): Observable<List<Solution>>

    @GET("/my_solution/unjudged")
    fun getUnjudgedMySolutions(
            @Query("authenticationKey") authenticationKey: String,
            @Query("groupId") groupId: Int
    ): Observable<List<Solution>>

    @GET("/my_items")
    fun getMyItems(
            @Query("authenticationKey") authenticationKey: String,
            @Query("groupId") groupId: Int
    ): Observable<List<ItemStack>>

    @GET("/ranking")
    fun getRanking(
            @Query("authenticationKey") authenticationKey: String,
            @Query("groupId") groupId: Int
    ): Observable<Ranking>

    @POST("/comment/create")
    fun createComment(
            @Field("authenticationKey") authenticationKey: String,
            @Field("solutionId") solutionId: Int,
            @Field("text") text: String,
            @Field("imageIds[]") imageIds: IntArray,
            @Field("replyTo") replyTo: Int
    )
}