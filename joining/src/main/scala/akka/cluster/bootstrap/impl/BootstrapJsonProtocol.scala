/*
 * Copyright (C) 2016 Lightbend Inc. <http://www.lightbend.com>
 */
package akka.cluster.bootstrap.impl

import akka.actor.{ Address, AddressFromURIString }
import akka.cluster.bootstrap.impl.BootstrapJsonProtocol.SeedNode
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.{ DefaultJsonProtocol, JsString, JsValue, RootJsonFormat }

trait BootstrapJsonProtocol extends SprayJsonSupport with DefaultJsonProtocol {

  implicit object AddressFormat extends RootJsonFormat[Address] {
    override def read(json: JsValue): Address = json match {
      case JsString(s) ⇒ AddressFromURIString.parse(s)
      case invalid ⇒ throw new IllegalArgumentException(s"Illegal address value! Was [$invalid]")
    }

    override def write(obj: Address): JsValue = JsString(obj.toString)
  }
  implicit val SeedNodeFormat = jsonFormat1(SeedNode)
}

object BootstrapJsonProtocol extends DefaultJsonProtocol {
  final case class SeedNode(address: Address)
}
