package com.hntoplinks.crawler.repositories

import com.hntoplinks.crawler.model.Item
import com.hntoplinks.crawler.model.ItemEntity
import com.hntoplinks.crawler.utils.DbMappers.mapItemEntity
import scalikejdbc.{AutoSession, DBSession, scalikejdbcSQLInterpolationImplicitDef}

import java.sql.Connection
import java.time.LocalDateTime

/**
 * id         bigint NOT NULL,
 * comhead    character varying(4096),
 * comment    integer,
 * postdate   timestamp without time zone,
 * hnid       bigint NOT NULL,
 * lastupdate timestamp without time zone,
 * points     integer,
 * title      character varying(4096),
 * posturl    character varying(4096),
 * hnuser     character varying(4096),
 * CONSTRAINT item_pkey PRIMARY KEY (id),
 * CONSTRAINT item_hnid_key UNIQUE (hnid)
 */
object ItemRepository {
  def save(item: Item)(implicit s: DBSession = AutoSession): ItemEntity = {
      sql"""insert into item (id, comhead, comment, postdate, hnid, lastupdate, points, title, posturl, hnuser)
         values (nextval('hibernate_sequence'), ${item.extractDomainNameFromUrl()}, ${item.descendants}, ${item.time}, ${item.id}, now(), ${item.score}, ${item.title}, ${item.url}, ${item.by})
         on conflict (hnid)
            do
            update set
                comhead = ${item.extractDomainNameFromUrl()},
                comment = ${item.descendants},
                postdate = ${item.time}
                lastupdate = ${LocalDateTime.now()},
                points = ${item.score},
                title = ${item.title},
                posturl = ${item.url},
                hnuser = ${item.by}
        returning *
         """
        .map(mapItemEntity).single.apply().get
  }
  
  def findById(id: Long)(implicit s: DBSession = AutoSession): ItemEntity = {
    sql"""select * from item where hnid = $id"""
      .map(mapItemEntity).single.apply().get
  }
  
  def findByHnId(hnId: Long)(implicit s: DBSession = AutoSession): Option[ItemEntity] = {
    sql"""select * from item where hnid = $hnId"""
      .map(mapItemEntity).single.apply()
  }
}
