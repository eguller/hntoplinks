package com.hntoplinks.crawler

import com.hntoplinks.crawler.model.Item
import sttp.client3.*
import sttp.client3.upicklejson.*
import upickle.default.*

import java.time.LocalDateTime
import scala.concurrent.{ExecutionContext, Future}

object HnApiClient {
  private val baseUrl = "https://hacker-news.firebaseio.com/v0"
  private val backend = HttpClientFutureBackend()

  implicit private val localDateTimeReader: Reader[LocalDateTime] = reader[Long].map(LocalDateTime.ofEpochSecond(_, 0, java.time.ZoneOffset.UTC))
  implicit private val localDateTimeWrite: Writer[LocalDateTime] = writer[Long].comap(_.toEpochSecond(java.time.ZoneOffset.UTC))
  implicit private val itemRw: ReadWriter[Item] = macroRW[Item]

  def getItem(id: Long)(implicit ec: ExecutionContext): Future[Option[Item]] = {
    val request = basicRequest
      .get(uri"$baseUrl/item/$id.json")
      .response(asJson[Item])

    request.send(backend).map(_.body).map(_.toOption)
  }

  def getTopStories()(implicit ec: ExecutionContext): Future[Option[Seq[Long]]] = {
    val request = basicRequest
      .get(uri"$baseUrl/topstories.json")
      .response(asJson[Seq[Long]])
    request.send(backend).map(_.body).map(_.toOption)
  }

  def getNewStories()(implicit ec: ExecutionContext): Future[Option[Seq[Long]]] = {
    val request = basicRequest
      .get(uri"$baseUrl/newstories.json")
      .response(asJson[Seq[Long]])
    request.send(backend).map(_.body).map(_.toOption)
  }

  def getBestStories()(implicit ec: ExecutionContext): Future[Option[Seq[Long]]] = {
    val request = basicRequest
      .get(uri"$baseUrl/beststories.json")
      .response(asJson[Seq[Long]])
    request.send(backend).map(_.body).map(_.toOption)
  }

  def getAskStories()(implicit ec: ExecutionContext): Future[Option[Seq[Long]]] = {
    val request = basicRequest
      .get(uri"$baseUrl/askstories.json")
      .response(asJson[Seq[Long]])
    request.send(backend).map(_.body).map(_.toOption)
  }

  def getShowStories()(implicit ec: ExecutionContext): Future[Option[Seq[Long]]] = {
    val request = basicRequest
      .get(uri"$baseUrl/showstories.json")
      .response(asJson[Seq[Long]])
    request.send(backend).map(_.body).map(_.toOption)
  }

  def getJobStories()(implicit ec: ExecutionContext): Future[Option[Seq[Long]]] = {
    val request = basicRequest
      .get(uri"$baseUrl/jobstories.json")
      .response(asJson[Seq[Long]])
    request.send(backend).map(_.body).map(_.toOption)
  }

  def getUpdates(implicit ec: ExecutionContext): Future[Option[Seq[Long]]] = {
    val request = basicRequest
      .get(uri"$baseUrl/updates.json")
      .response(asJson[Seq[Long]])
        request.send(backend).map(_.body).map(_.toOption)
  }

  def getMaxItems(implicit ec: ExecutionContext): Future[Option[Long]] = {
    val request = basicRequest
      .get(uri"$baseUrl/maxitem.json")
      .response(asJson[Long])
    request.send(backend).map(_.body).map(_.toOption)
  }
}
