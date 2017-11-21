/*
 * Copyright (C) 2016 Lightbend Inc. <http://www.lightbend.com>
 */
package akka.cluster.bootstrap

import akka.actor.{ ExtendedActorSystem, Extension, ExtensionId, ExtensionIdProvider }
import akka.cluster.Cluster
import akka.cluster.bootstrap.impl.BootstrapJsonProtocol
import akka.cluster.bootstrap.impl.BootstrapJsonProtocol.SeedNode
import akka.http.scaladsl.server.Route

final class ClusterBootstrapRoutes(settings: ClusterBootstrapSettings) extends BootstrapJsonProtocol {
  import akka.http.scaladsl.server.Directives._

  private def routeGetSeedNodes: Route = extractExecutionContext { implicit ec ⇒
    val seeds = List.empty[SeedNode]
    complete(seeds)
  }

  // TODO ip whitelist feature?
  val routes = {
    // TODO basePath
    // val basePath = if (pathPrefixName.isEmpty) rawPathPrefix(pathPrefixName) else pathPrefix(pathPrefixName)

    concat(
      (get & path("bootstrap" / "seed-nodes"))(routeGetSeedNodes)
    )
  }

}
