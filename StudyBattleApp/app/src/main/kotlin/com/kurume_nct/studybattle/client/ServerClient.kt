package com.kurume_nct.studybattle.client

import android.content.Context
import android.net.Uri
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.kurume_nct.studybattle.model.*
import io.reactivex.Observable
import okhttp3.*
import org.joda.time.DateTime
import org.joda.time.Duration
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.InputStream
import java.lang.reflect.Type
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor




/**
 * Created by hanah on 7/31/2017.
 */
class ServerClient(authenticationKey: String = "") {

    private val server: Server

    var authenticationKey: String = authenticationKey
        private set

    init {
        val gson = GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)
                .create()
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        val httpClient = OkHttpClient.Builder()
        httpClient.addInterceptor(logging)

        val retrofit = Retrofit.Builder()
                .baseUrl("http://studybattle.dip.jp:8080")
                //.baseUrl("http://localhost:8080")
                .addConverterFactory(StringConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(httpClient.build())
                .build()
        server = retrofit.create(Server::class.java)
    }

    fun register(displayName: String, userName: String, password: String, iconImageId: Int): Observable<Unit>
            = server.register(displayName, userName, password, iconImageId)

    fun register(displayName: String, userName: String, password: String): Observable<Unit>
            = server.register(displayName, userName, password)

    fun login(userName: String, password: String)
            = server
            .login(userName, password)
            .map {
                authenticationKey = it.authenticationKey
                it
            }!!

    /**
     * authentication keyを検証して、自身のユーザー情報を返します。
     */
    fun verifyAuthentication(authenticationKey: String = this.authenticationKey)
            = server.verifyAuthentication(authenticationKey)

    /**
     * <code>query</code>を<code>userName</code>の部分文字列の一つに含むユーザーを列挙します。
     */
    fun searchUsers(query: String) = server.searchUsers(query)

    fun createGroup(name: String) = server.createGroup(authenticationKey, name)

    fun joinGroup(id: Int) = server.joinGroup(authenticationKey, id)

    fun joinGroup(group: Group) = joinGroup(group.id)

    fun attachToGroup(groupId: Int, userId: Int) = server.attachToGroup(authenticationKey, groupId, userId)

    fun attachToGroup(group: Group, user: User) = attachToGroup(group.id, user.id)

    fun getGroup(id: Int) = server.getGroup(id, authenticationKey)

    fun getGroup(group: Group) = getGroup(group.id)

    fun getJoinedGroups() = server.getJoinedGroups(authenticationKey)

    fun uploadImage(inputStream: InputStream, type: String): Observable<Image> {
        val bytes = inputStream.use {
            val buffer = mutableListOf<Byte>()
            while (true) {
                val temp = it.read()
                if (temp == -1) {
                    break
                }
                buffer.add(temp.toByte())
            }
            buffer.toByteArray()
        }

        val fileExtension = type.substring("image/".length)
        val imagePart = MultipartBody.Part.create(
                Headers.of(mapOf("Content-Disposition" to "form-data; name=\"image\"; filename=\"hoge.$fileExtension\"")),
                RequestBody.create(
                        MediaType.parse(type),
                        bytes
                )
        )

        return server.uploadImage(imagePart)
    }

    fun uploadImage(uri: Uri, context: Context): Observable<Image> {
        val contentResolver = context.contentResolver
        return uploadImage(contentResolver.openInputStream(uri), contentResolver.getType(uri))
    }

    fun getImageById(id: Int) = server.getImageById(id)

    fun getImageById(image: Image) = getImageById(image.id)

    fun createProblem(
            title: String, text: String, imageIds: List<Int>, startsAt: DateTime, duration: Duration, groupId: Int, assumedSolution: Solution
    ): Observable<Problem> {
        val gson = Gson()
        val body = mapOf(
                "authenticationKey" to authenticationKey,
                "title" to title,
                "text" to text,
                "imageIds" to imageIds,
                "startsAt" to startsAt.toString(),
                "durationMillis" to duration.millis,
                "groupId" to groupId,
                "assumedSolution" to assumedSolution
        )
        val hoge = gson.toJson(body)
        println(hoge)
        return server
                .createProblem(hoge)
                .flatMap { server.getProblem(authenticationKey, it.id) }
    }

    fun getProblem(id: Int): Observable<Problem> = server.getProblem(authenticationKey, id)

    fun getMyJudgedProblems(groupId: Int) = server.getMyJudgedProblems(authenticationKey, groupId)

    fun getMyJudgingProblems(groupId: Int) = server.getMyJudgingProblems(authenticationKey, groupId)

    fun getMyCollectingProblems(groupId: Int) = server.getMyCollectingProblems(authenticationKey, groupId)

    fun getAssignedProblems(group: Group) = getAssignedProblems(group.id)

    fun getAssignedProblems(groupId: Int) = server.getAssignedProblems(authenticationKey, groupId)

    /**
     * 新しい問題を自分に割り当てるよう要求します。
     */
    fun requestNewProblem(group: Group) = requestNewProblem(group.id)

    /**
     * 新しい問題を自分に割り当てるよう要求します。
     */
    fun requestNewProblem(groupId: Int) = server.requestNewProblem(authenticationKey, groupId)

    fun createSolution(
            text: String, problem: Problem, imageIds: List<Int>
    ): Observable<Solution> = createSolution(text, problem.id, imageIds)

    fun createSolution(
            text: String, problemId: Int, imageIds: List<Int>
    ): Observable<Solution> =
            server
                    .createSolution(authenticationKey, text, problemId, imageIds.toIntArray())
                    .flatMap {
                        server.getSolution(authenticationKey, it.id)
                    }

    fun getSolution(solution: Solution) = getSolution(solution.id)

    fun getSolution(solutionId: Int) = server.getSolution(authenticationKey, solutionId)

    fun getJudgedMySolutions(group: Group) = getJudgedMySolutions(group.id)

    fun getJudgedMySolutions(groupId: Int) = server.getJudgedMySolutions(authenticationKey, groupId)

    fun getUnjudgedMySolutions(group: Group) = getUnjudgedMySolutions(group.id)

    fun getUnjudgedMySolutions(groupId: Int) = server.getUnjudgedMySolutions(authenticationKey, groupId)
}

private class StringConverterFactory : Converter.Factory() {

    override fun responseBodyConverter(type: Type, annotations: Array<Annotation>, retrofit: Retrofit): Converter<ResponseBody, *>? {
        if (String::class.java == type) {
            return Converter<ResponseBody, String> { value -> value.string() }
        }
        return null
    }

    override fun requestBodyConverter(type: Type, parameterAnnotations: Array<Annotation>, methodAnnotations: Array<Annotation>, retrofit: Retrofit): Converter<*, RequestBody>? {
        if (String::class.java == type) {
            return Converter<String, RequestBody> { value -> RequestBody.create(MEDIA_TYPE, value) }
        }

        return null
    }

    companion object {
        private val MEDIA_TYPE = MediaType.parse("text/plain")

        fun create(): StringConverterFactory {
            return StringConverterFactory()
        }
    }

}