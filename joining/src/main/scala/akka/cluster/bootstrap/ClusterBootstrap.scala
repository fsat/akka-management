/*
 * Copyright (C) 2016 Lightbend Inc. <http://www.lightbend.com>
 */
package akka.cluster.bootstrap

import java.util.concurrent.atomic.AtomicReference

import akka.Done
import akka.actor.{ ActorSystem, ExtendedActorSystem, Extension, ExtensionId, ExtensionIdProvider, Props }
import akka.cluster.Cluster
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.{ Future, Promise }
import scala.concurrent.duration._

final class ClusterBootstrap(system: ExtendedActorSystem) extends Extension {
  import system.dispatcher

  private sealed trait BootstrapStep
  private case object NotRunning extends BootstrapStep
  private case object Initializing extends BootstrapStep
  private case object Initialized extends BootstrapStep
  private final val bootstrapStep = new AtomicReference[BootstrapStep]()

  // FIXME
  val settings = ClusterBootstrapSettings(system.settings.config)

  // TODO load the right one dependinf on config / env?
  private val bootstrapManager = system.systemActorOf(BootstrapManager.props, "clusterBootstrapManager")

  def start(): Future[Done] =
    if (bootstrapStep.compareAndSet(NotRunning, Initializing)) {
      implicit val initTimeout: Timeout = Timeout(400.days) // configure? or what should it do really? try until forever meh?

      val initCompleted = Promise[Done]()

      (bootstrapManager ? BootstrapManager.Protocol.InitiateBootstraping).mapTo[
          BootstrapManager.Protocol.BootstrapingCompleted]

      initCompleted.future
    } else Future.failed(new Exception("Already in progress"))
}

object ClusterBootstrap extends ExtensionId[ClusterBootstrap] with ExtensionIdProvider {
  override def lookup: ClusterBootstrap.type = ClusterBootstrap

  override def get(system: ActorSystem) = super.get(system)

  override def createExtension(system: ExtendedActorSystem): ClusterBootstrap = new ClusterBootstrap(system)
}
