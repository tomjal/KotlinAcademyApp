package org.kotlinacademy.backend.usecases

import org.kotlinacademy.Endpoints
import org.kotlinacademy.backend.Config
import org.kotlinacademy.backend.Config.baseUrl
import org.kotlinacademy.backend.errors.MissingElementError
import org.kotlinacademy.backend.repositories.db.ArticlesDatabaseRepository
import org.kotlinacademy.backend.repositories.email.EmailRepository
import org.kotlinacademy.backend.repositories.network.dto.NotificationResult
import org.kotlinacademy.data.*
import java.net.URLDecoder

object EmailUseCase {

    suspend fun sendInfoAboutFeedback(feedback: Feedback) {
        val articlesDatabaseRepository = ArticlesDatabaseRepository.get()
        val emailRepository = EmailRepository.get()
        val articleTitle = feedback.newsId?.let { articlesDatabaseRepository.getArticle(it) }?.title
        val feedbackTo = articleTitle ?: "Kotlin Academy"
        emailRepository.emailToAdmin("New feedback", """
                |New feedback to $feedbackTo <br>
                |Rating: ${feedback.rating} <br>
                |Comment: <br>
                |${feedback.comment} <br>
                |<br>
                |Suggestions: <br>
                |${feedback.suggestions}
            """)
    }

    suspend fun sendNotificationResult(result: NotificationResult) {
        val emailRepository = EmailRepository.get()
        emailRepository.emailToAdmin("Notification report", """
                |Success: ${result.success} <br>
                |Failure: ${result.failure}
            """)
    }

    suspend fun askForAcceptation(info: Info) {
        val emailRepository = EmailRepository.get()
        emailRepository.emailToAdmin("Request for info acceptation", """
                |Title: ${info.title} <br>
                |Description: ${info.description} <br>
                |Image: <img src="${info.imageUrl}"> <br>
                |Sources: <img src="${info.sources}"> <br>
                |URL: ${info.url} <br>
                |Author: ${info.author} <br>
                |Author URL: ${info.authorUrl} <br>
                |Occurrence: ${info.dateTime.toDateFormatString()} <br>
                |${makeButtons(info.id, Endpoints.info)}
                |${baseUrl}submit-info?title=${info.title.toUrlParam()}&url=${info.url.toUrlParam()}&image-url=${info.imageUrl.toUrlParam()}&description=${info.description.toUrlParam()}&sources=${info.sources.toUrlParam()}&author=${info.author.toUrlParam()}&author-url=${info.authorUrl.toUrlParam()}
            """)
    }

    suspend fun askForAcceptation(puzzler: Puzzler) {
        val emailRepository = EmailRepository.get()
        emailRepository.emailToAdmin("Request for article acceptation", """
                |Title: ${puzzler.title} <br>
                |Level: ${puzzler.level} <br>
                |Question: ${puzzler.question} <br>
                |Answers: <br> ${puzzler.answers} <br>
                |Correct answer: ${puzzler.correctAnswer} <br>
                |Explanation: <br> ${puzzler.explanation} <br>
                |Author: ${puzzler.author} <br>
                |Author URL: ${puzzler.authorUrl} <br>
                |Addet at: ${puzzler.dateTime.toDateFormatString()} <br>
                |${makeButtons(puzzler.id, Endpoints.puzzler)}
                |${baseUrl}submit-puzzler?title=${puzzler.title.toUrlParam()}&level=${puzzler.level.toUrlParam()}&question=${puzzler.question.toUrlParam()}&answers=${puzzler.answers.toUrlParam()}&author=${puzzler.author.toUrlParam()}&correct-answer=${puzzler.correctAnswer.toUrlParam()}&explanation=${puzzler.explanation.toUrlParam()}&author-url=${puzzler.authorUrl.toUrlParam()}
            """)
    }

    private fun String?.toUrlParam() = URLDecoder.decode(this.orEmpty())

    private fun makeButtons(id: Int, type: String) =
            """$baseUrl$type/$id/${Endpoints.accept}?secret-hash=${Config.secretHash} <br>
              |$baseUrl$type/$id/${Endpoints.reject}?secret-hash=${Config.secretHash} <br>""".trimMargin()

    private suspend fun EmailRepository.emailToAdmin(title: String, textHtml: String) {
        val adminEmail = Config.adminEmail ?: throw MissingElementError("ADMIN_EMAIL env var")
        val message = textHtml.trimMargin()
        sendHtmlEmail(adminEmail, title, message)
    }
}