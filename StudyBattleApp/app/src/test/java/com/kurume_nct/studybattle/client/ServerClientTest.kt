package com.kurume_nct.studybattle.client

import com.kurume_nct.studybattle.model.Air
import com.kurume_nct.studybattle.model.Solution
import org.joda.time.DateTime
import org.joda.time.Duration
import org.junit.Assert.assertEquals
import org.junit.BeforeClass
import org.junit.Test
import java.io.InputStream
import java.net.URL
import java.security.MessageDigest
import java.security.SecureRandom


/**
 * Created by gedorinku on 2017/09/23.
 */
class ServerClientTest {

    companion object {

        val random = SecureRandom()
        val displayName = "里中チエ"
        val userName = generateTestUserName()
        val password = generateTestUserPassword()
        val client = ServerClient("http://localhost:8080")

        @JvmStatic
        @BeforeClass
        fun registerTestUser() {
            client
                    .register(displayName, userName, password)
                    .flatMap { client.login(userName, password) }
                    .blockingSubscribe()
        }

        private fun generateTestUserName(): String {
            val prefix = "chie_"
            return prefix + printHex(generateRandomBytes(4))
        }

        private fun generateTestUserPassword(): String = printHex(generateRandomBytes(16))

        private fun generateRandomBytes(size: Int): ByteArray {
            val randomBytes = ByteArray(size)
            random.nextBytes(randomBytes)
            return randomBytes
        }

        private fun printHex(bytes: ByteArray): String
                = bytes.map { String.format("%02x", it) }.joinToString("")

        private fun hashContent(inputStream: InputStream): String {
            val md5 = MessageDigest.getInstance("MD5")
            inputStream.use {
                while (true) {
                    val temp = it.read()
                    if (temp == -1) {
                        break
                    }
                    md5.update(temp.toByte())
                }
            }

            return printHex(md5.digest())
        }
    }

    @Test
    fun verifyAuthenticationKeyTest() {
        val testSubscriber = client
                .verifyAuthentication()
                .test()

        testSubscriber.awaitTerminalEvent()
        testSubscriber
                .assertNoErrors()
                .assertNoTimeout()

        val user = testSubscriber.values()[0]
        assertEquals(userName, user.userName)
        assertEquals(displayName, user.displayName)
    }

    @Test
    fun createAndJoinGroupTest() {
        val groupName = "kamesan_" + printHex(generateRandomBytes(4))

        val group = {
            val group = client.createGroup(groupName)
                    .blockingFirst()
            assertEquals(groupName, group.name)
            assert(0 < group.id)


            val testSubscriber = client
                    .joinGroup(group)
                    .test()

            testSubscriber.awaitTerminalEvent()
            testSubscriber
                    .assertNoErrors()
                    .assertNoTimeout()

            group
        }()

        val getJoinedGroups = {
            val testSubscriber = client
                    .getJoinedGroups()
                    .test()
            testSubscriber.awaitTerminalEvent()
            testSubscriber.assertNoErrors()
            testSubscriber.assertNoTimeout()

            testSubscriber.values()[0]
        }

        val joinedGroups = getJoinedGroups()
        assert(joinedGroups.any { it == group })

        val leaveGroup: (Int) -> Unit = { groupId ->
            val testSubscriber = client
                    .leaveGroup(groupId)
                    .test()
            testSubscriber.awaitTerminalEvent()
            testSubscriber.assertNoErrors()
            testSubscriber.assertNoTimeout()
        }

        joinedGroups.map { leaveGroup(it.id) }
        assert(getJoinedGroups().isEmpty())
    }

    @Test
    fun uploadImageTest() {
        val fileName = "icon.png"
        val classLoader = javaClass.classLoader

        val image = {
            val testSubscriber = client
                    .uploadImage(classLoader.getResourceAsStream(fileName), "image/png")
                    .test()

            testSubscriber.awaitTerminalEvent()
            testSubscriber
                    .assertNoErrors()
                    .assertNoTimeout()
                    .values()[0]
        }()

        val origin = hashContent(classLoader.getResourceAsStream(fileName))
        val upload = hashContent(URL(image.url).openStream())
        assertEquals(origin, upload)

        val image2 = {
            val testSubscriber = client
                    .getImageById(image.id)
                    .test()

            testSubscriber.awaitTerminalEvent()
            testSubscriber
                    .assertNoErrors()
                    .assertNoTimeout()
                    .values()[0]
        }()

        assertEquals(image, image2)
    }

    @Test
    fun createProblemAndSolutionTest() {
        val groupId = {
            val groupName = "kamesan_" + printHex(generateRandomBytes(4))
            client.createGroup(groupName)
                    .map {
                        client.joinGroup(it.id)
                                .blockingSubscribe()
                        it.id
                    }
                    .blockingFirst()
        }()

        val problem = {
            val problemTitle = "hoge"
            val problemText = "うぇい\nそいい\nabc"
            val startsAt = DateTime.now() - Duration.standardMinutes(1)
            val duration = Duration.standardHours(1)

            val testSubscriber = client
                    .createProblem(problemTitle, problemText, emptyList(), startsAt, duration, groupId, Solution(text = "想定解だよだよ"))
                    .test()

            testSubscriber.awaitTerminalEvent()
            val problem = testSubscriber
                    .assertNoErrors()
                    .assertNoTimeout()
                    .values()[0]

            assertEquals(problemTitle, problem.title)
            assertEquals(problemText, problem.text)
            assertEquals(startsAt.millis, problem.startsAtTime.millis)
            assertEquals(duration, problem.duration)

            problem
        }()

        val solutionText = "英語は世界中の多くの人間によって作られる\n" +
                "スポーツ研究会だ。"

        val testSubscriber = client
                .createSolution(solutionText, problem, emptyList(), Air)
                .test()
        testSubscriber.awaitTerminalEvent()

        val solution = testSubscriber
                .assertNoErrors()
                .assertNoTimeout()
                .values()[0]

       // assertEquals(solutionText, solution.text)
        //assertEquals(problem.id, solution.problemId)
    }
}