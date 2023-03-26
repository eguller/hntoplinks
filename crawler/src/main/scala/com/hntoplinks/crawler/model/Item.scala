package com.hntoplinks.crawler.model

import java.time.LocalDateTime

/**
id: The item's unique id.
deleted:	true if the item is deleted.
type: 	The type of item. One of "job", "story", "comment", "poll", or "pollopt".
by:The username of the item's author.
time:	Creation date of the item, in Unix Time.
text:	The comment, story or poll text. HTML.
dead:	true if the item is dead.
parent:	The comment's parent: either another comment or the relevant story.
poll:	The pollopt's associated poll.
kids:	The ids of the item's comments, in ranked display order.
url:	The URL of the story.
score:	The story's score, or the votes for a pollopt.
title:	The title of the story, poll or job. HTML.
parts:	A list of related pollopts, in display order.
descendants:	In the case of stories or polls, the total comment count.
*/
case class Item (
    id: Long,
    deleted: Boolean = false,
    @upickle.implicits.key("type")
    itemType: String,
    by: String,
    time: LocalDateTime,
    text: String,
    dead: Boolean = false,
    parent: Option[Long],
    poll: Option[Long],
    kids: List[Long],
    url: Option[String],
    score: Long,
    title: String,
    descendants: Long,
) {

  def isParent() = {
    parent.isEmpty
  }
  
  def extractDomainNameFromUrl(): String = {
    val url = this.url.getOrElse("")
    val findDomainRegex = """^(?:https?:\/\/)?(?:[^@\n]+@)?(?:www\.)?([^:\/\n?]+)""".r
    val domain = findDomainRegex.findAllIn(url).group(1)
    domain
  }
}
