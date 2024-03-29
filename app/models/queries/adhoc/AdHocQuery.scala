package models.queries.adhoc

import java.util.UUID

import org.joda.time.LocalDateTime

final case class AdHocQuery(
  id: UUID,
  title: String,
  author: UUID,
  sql: String,
  created: LocalDateTime,
  updated: LocalDateTime)
