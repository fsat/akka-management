/*
 * Copyright (C) 2016 Lightbend Inc. <http://www.lightbend.com>
 */
package akka.cluster.bootstrap

import akka.stream.scaladsl.{ Sink, Source }
import com.typesafe.config.Config
import scala.concurrent.duration._

import scala.concurrent.duration.FiniteDuration

// FIXME make easy to binary evolve (kaze class)
// FIXME don't hardcode
final case class ClusterBootstrapSettings(
    requiredContactPoints: Int = 3, // TODO perhaps no default at all for this one, make people choose?
    joinOnceStableFor: FiniteDuration = 10.seconds,
    dnsResolveTimeout: Duration = 30.seconds
) {
  require(requiredContactPoints > 2, "Number of contact points is strongly recommended to be greater than")
}

object ClusterBootstrapSettings {
  def apply(c: Config): ClusterBootstrapSettings =
    // FIXME actual impl
    new ClusterBootstrapSettings()

}
