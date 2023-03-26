package com.hntoplinks.crawler.model

import java.time.LocalDateTime

case class ItemEntity(
    id: Long,
    comhead: String,
    comment: Int,
    postdate: LocalDateTime,
    hnid: Long,
    lastUpdate: LocalDateTime,
    points: Int,
    title: String,
    posturl: String,
    hnUser: String,
);
