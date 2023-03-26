package com.hntoplinks.crawler.model

import com.hntoplinks.crawler.HnApiClient
import com.hntoplinks.crawler.repositories.ItemRepository
import org.scalatest.flatspec.AnyFlatSpec

import java.time.{LocalDate, LocalDateTime}
import scala.concurrent.ExecutionContext.Implicits.global

class ItemSpec extends AnyFlatSpec {

  "domain name" should "be extracted from url" in {
    val item = randomItemWithUrl("http://www.example.com")
    assert(item.extractDomainNameFromUrl() == "example.com")
  }

  "read top stories and save in db" should "be successful" in {
    for {
      topItemsOpt <- HnApiClient.getTopStories()
      itemOpt <- HnApiClient.getItem(topItemsOpt.get.head)
    } yield {
      val item = itemOpt.get
      ItemRepository.save(item)
      val dbItem = ItemRepository.findByHnId(item.id)
      assert(dbItem.nonEmpty)
    }
  }

  private def randomItemWithUrl(url: String) = {
    Item(
      -1L,
      itemType="story",
      by="me",
      url=Option(url),
      time=LocalDateTime.now(),
      text="hello",
      parent=Option.empty,
      poll=Option.empty,
      kids=List.empty,
      score=20,
      title="title",
      descendants = 10,
    )
  }
}
