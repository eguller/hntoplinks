package com.hntoplinks.crawler.utils

import com.hntoplinks.crawler.model.ItemEntity
import scalikejdbc.WrappedResultSet

object DbMappers {
  def mapItemEntity(rs: WrappedResultSet): ItemEntity = {
      ItemEntity(
        rs.long("id"),
        rs.string("comhead"),
        rs.int("comment"),
        rs.timestamp("postdate").toLocalDateTime,
        rs.long("hnid"),
        rs.timestamp("lastupdate").toLocalDateTime,
        rs.int("points"),
        rs.string("title"),
        rs.string("posturl"),
        rs.string("hnuser"))
  }
}
