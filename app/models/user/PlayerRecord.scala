package models.user

import java.util.UUID

import akka.actor.ActorRef

final case class PlayerRecord(
  userId: UUID,
  name: String,
  var connectionId: Option[UUID],
  var connectionActor: Option[ActorRef])
