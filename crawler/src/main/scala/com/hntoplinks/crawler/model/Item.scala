package com.hntoplinks.crawler.model

import java.time.LocalDateTime

case class Item (
    id: Long,
    itemType: String,
    by: String,
    time: LocalDateTime,
    url: Option[String],
    score: Long,
    title: String,
    descendants: Long,
    parent: Option[Long]
                ) {
  
  def isParent() = {
    !parent.nonEmpty
  }
}
