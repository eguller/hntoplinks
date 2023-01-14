package com.hntoplinks.crawler

import com.hntoplinks.crawler.model.Item

import scala.concurrent.Future
import sttp.client3.*
import sttp.client3.upicklejson._
import upickle.default._

object HnApiClient {
  val baseUrl = "https://hacker-news.firebaseio.com/v0"
  val client = SimpleHttpClient()
  def fetchItem(id: Long): Item = {
    Nil.init
  }

  def fetchLast(): Long = {
    val response = client.send(basicRequest.get(uri"${baseUrl}/maxitem.json").response(asJson[HttpBinResponse]))
    //response.body match {
    //  case Left(e) ⇒ println("hello")
    //  case Right(r) ⇒ r.
    //}
    1L
  }

  def fetchTop(): List[Long] = {
List(1L)
  }

  def fetchBest(): List[Long] = {
List(1L)
  }

  def fetchNew(): List[Long] = {
List(1L)
  }

  def fetchChanged(): List[Long] = {
List(1L)
  }
}
