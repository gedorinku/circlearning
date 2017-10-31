package com.kurume_nct.studybattle.client

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.kurume_nct.studybattle.model.*
import io.reactivex.Observable
import io.reactivex.Single
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import org.joda.time.DateTime
import org.joda.time.Duration
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.InputStream
import java.lang.reflect.Type


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

    fun getUser(id: Int) = server.getUser(id)

    /**
     * <code>query</code>を<code>userName</code>の部分文字列の一つに含むユーザーを列挙します。
     */
    fun searchUsers(query: String) = server.searchUsers(query)

    fun createGroup(name: String) = server.createGroup(authenticationKey, name)

    fun joinGroup(id: Int) = server.joinGroup(authenticationKey, id)

    fun joinGroup(group: Group) = joinGroup(group.id)

    fun leaveGroup(groupId: Int) = server.leaveGroup(authenticationKey, groupId)

    fun attachToGroup(groupId: Int, userId: Int) = server.attachToGroup(authenticationKey, groupId, userId)

    fun attachToGroup(group: Group, user: User) = attachToGroup(group.id, user.id)

    fun getGroup(id: Int) = server.getGroup(id, authenticationKey)

    fun getGroup(group: Group) = getGroup(group.id)

    fun getJoinedGroups() = server.getJoinedGroups(authenticationKey)

    fun uploadImage(inputStream: InputStream, type: String): Observable<Image> =
            Single.fromCallable {
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

                imagePart
            }
                    .toObservable()
                    .flatMap {
                        server.uploadImage(it)
                    }

    fun uploadImage(uri: Uri, context: Context): Observable<Image> {
        val file = File(getPathFromUri(context, uri))
        val contentResolver = context.contentResolver
        val type = contentResolver.getType(uri)
        val imagePart = MultipartBody.Part.create(
                Headers.of(mapOf("Content-Disposition" to "form-data; name=\"image\"; filename=\"hoge\"")),
                RequestBody.create(
                        MediaType.parse(type),
                        file
                )
        )

        return server.uploadImage(imagePart)
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

    fun getMyChallengePhaseProblems(groupId: Int) = server.getMyChallengePhaseProblems(authenticationKey, groupId)

    fun getMyJudgingProblems(groupId: Int) = server.getMyJudgingProblems(authenticationKey, groupId)

    fun getMyCollectingProblems(groupId: Int) = server.getMyCollectingProblems(authenticationKey, groupId)

    /**
     * 公開されている正誤判定に文句をつけるフェーズの問題一覧を取得します。
     */
    fun getChallengePhaseProblems(groupId: Int) = server.getChallengePhaseProblems(authenticationKey, groupId)

    /**
     * 公開されている採点が終了した問題一覧を取得します。
     */
    fun getJudgedProblems(groupId: Int) = server.getJudgedProblems(authenticationKey, groupId)

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

    /**
     * 割り当てられた問題をパスします。
     */
    fun passProblem(problemId: Int) = server.passProblem(authenticationKey, problemId)

    /**
     * 問題を開きます。
     */
    fun openProblem(problemId: Int) = server.openProblem(authenticationKey, problemId)

    fun createSolution(
            text: String, problem: Problem, imageIds: List<Int>, item: Item
    ): Observable<IDResponse> = createSolution(text, problem.id, imageIds, item)

    fun createSolution(
            text: String, problemId: Int, imageIds: List<Int>, item: Item
    ): Observable<IDResponse> {
        val gson = Gson()
        val values = mapOf("authenticationKey" to authenticationKey,
                "text" to text,
                "problemId" to problemId,
                "imageIds" to imageIds,
                "attachedItemId" to item.id)
        val body = gson.toJson(values)
        return server.createSolution(body)
    }

    fun getSolution(solution: Solution) = getSolution(solution.id)

    fun getSolution(solutionId: Int) = server.getSolution(authenticationKey, solutionId)

    fun judgeSolution(solutionId: Int, accepted: Boolean)
            = server.judgeSolution(authenticationKey, solutionId, accepted)

    fun getJudgedMySolutions(group: Group) = getJudgedMySolutions(group.id)

    fun getJudgedMySolutions(groupId: Int) = server.getJudgedMySolutions(authenticationKey, groupId)

    fun getUnjudgedMySolutions(group: Group) = getUnjudgedMySolutions(group.id)

    fun getUnjudgedMySolutions(groupId: Int) = server.getUnjudgedMySolutions(authenticationKey, groupId)

    fun getMyItems(groupId: Int) = server.getMyItems(authenticationKey, groupId)

    fun getRanking(groupId: Int) = server.getRanking(authenticationKey, groupId)

    fun createComment(solutionId: Int, text: String, imageIds: List<Int>, replyTo: Int = 0)
            = server.createComment(authenticationKey, solutionId, text, imageIds.toIntArray(), replyTo)


    private fun getPathFromUri(context: Context, uri: Uri): String? {
        val isAfterKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
        // DocumentProvider
        Log.e(javaClass.simpleName, "uri:" + uri.authority)
        if (isAfterKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            when {
                "com.android.externalstorage.documents" == uri.authority -> {// ExternalStorageProvider
                    val docId = DocumentsContract.getDocumentId(uri)
                    val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    val type = split[0]
                    return if ("primary".equals(type, ignoreCase = true)) {
                        """${Environment.getExternalStorageDirectory()}/${split[1]}"""
                    } else {
                        """/stroage/$type/${split[1]}"""
                    }
                }
                "com.android.providers.downloads.documents" == uri.authority -> {// DownloadsProvider
                    val id = DocumentsContract.getDocumentId(uri)
                    val contentUri = ContentUris.withAppendedId(
                            Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(id)!!)
                    return getDataColumn(context, contentUri, null, null)
                }
                "com.android.providers.media.documents" == uri.authority -> {// MediaProvider
                    val docId = DocumentsContract.getDocumentId(uri)
                    val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    val type = split[0]
                    var contentUri: Uri? = null
                    contentUri = MediaStore.Files.getContentUri("external")
                    val selection = "_id=?"
                    val selectionArgs = arrayOf(split[1])
                    return getDataColumn(context, contentUri, selection, selectionArgs)
                }
                else -> {
                }
            }
        } else if ("content".equals(uri.scheme, ignoreCase = true)) {//MediaStore
            return getDataColumn(context, uri, null, null)
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {// File
            return uri.path
        }
        return null
    }

    private fun getDataColumn(context: Context, uri: Uri?, selection: String?,
                      selectionArgs: Array<String>?): String? {
        var cursor: Cursor? = null
        val projection = arrayOf(MediaStore.Files.FileColumns.DATA)
        try {
            cursor = context.contentResolver.query(
                    uri!!, projection, selection, selectionArgs, null)
            if (cursor != null && cursor.moveToFirst()) {
                val cindex = cursor.getColumnIndexOrThrow(projection[0])
                return cursor.getString(cindex)
            }
        } finally {
            if (cursor != null)
                cursor.close()
        }
        return null
    }
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