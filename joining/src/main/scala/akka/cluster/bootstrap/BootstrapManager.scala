/*
 * Copyright (C) 2016 Lightbend Inc. <http://www.lightbend.com>
 */
package akka.cluster.bootstrap

import akka.actor.{ Actor, ActorLogging, ActorRef, Address, Props }
import akka.annotation.InternalApi
import akka.cluster.ClusterEvent.CurrentClusterState
import scala.collection.immutable

/** INTERNAL API */
@InternalApi
// FIXME this is so far the DNS SVC one (service name)
private[bootstrap] object BootstrapManager {
  def props: Props = Props[BootstrapManager]

  object Protocol {
    final case class InitiateBootstraping(serviceName: String) // TODO service name may be a leak of the Kubernetes impl? or not really...
    final case class BootstrapingCompleted(state: CurrentClusterState)
  }

}

class BootstrapManager(settings: ClusterBootstrapSettings) extends Actor with ActorLogging {
  import BootstrapManager.Protocol._
  import com.lightbend.dns.locator.{ ServiceLocator ⇒ DnsServiceLocator }

  // TODO swappable bootstrap implementations here
  val dnsLocator: ActorRef = context.actorOf(DnsServiceLocator.props, "dnsServiceLocator")

  override def receive = {
    case InitiateBootstraping(serviceName) ⇒
      val replyTo = sender()

      dnsLocator ! DnsServiceLocator.GetAddresses(serviceName)
      context
        .setReceiveTimeout(settings.dnsResolveTimeout) // FIXME not nice since the other one has internal timeouts too
      context become bootstraping(replyTo)
  }

  def bootstraping(ref: ActorRef): Receive = {
    case DnsServiceLocator.Addresses(addresses) ⇒
      log.info("Discovered {} services", addresses.size)
  }
}
