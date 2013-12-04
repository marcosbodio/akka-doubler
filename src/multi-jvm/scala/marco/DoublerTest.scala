package marco

import scala.collection.JavaConverters.mapAsJavaMapConverter
import scala.collection.JavaConverters.seqAsJavaListConverter
import scala.concurrent.duration.DurationInt
import com.typesafe.config.ConfigFactory
import akka.remote.testkit.MultiNodeConfig
import akka.remote.testkit.MultiNodeSpec
import akka.testkit.ImplicitSender
import marco.actor.Doubler
import multinode.ScalaTestMultiNodeSpec

object DoublerTestConfig extends MultiNodeConfig {

  import scala.collection.JavaConverters._

  // define common configuration for all nodes
  commonConfig(ConfigFactory.parseMap(Map(
    "akka.loggers" -> List("akka.event.slf4j.Slf4jLogger").asJava,
    "akka.loglevel" -> "DEBUG",
//    "akka.log-dead-letters" -> 50,
//    "akka.log-dead-letters-during-shutdown" -> "off",
//    "akka.remote.log-sent-messages" -> "on",
//    "akka.remote.log-received-messages" -> "on",
    "akka.actor.provider" -> "akka.remote.RemoteActorRefProvider",
    "akka.remote.netty.tcp.hostname" -> "localhost").asJava))
  // define nodes
  val node1 = role("node-1")
  val node2 = role("node-2")
  val node3 = role("node-3")
  // define custom configuration for node 1
  nodeConfig(node1)(ConfigFactory.parseMap(Map(
    "akka.remote.netty.tcp.port" -> 3001).asJava))
  // define custom configuration for node 2    
  nodeConfig(node2)(ConfigFactory.parseMap(Map(
    "akka.remote.netty.tcp.port" -> 3002).asJava))
  // define custom configuration for node 3
  nodeConfig(node3)(ConfigFactory.parseMap(Map(
    "akka.remote.netty.tcp.port" -> 3003).asJava))

}

class DoublerTestMultiJvmNode1 extends DoublerTest
class DoublerTestMultiJvmNode2 extends DoublerTest
class DoublerTestMultiJvmNode3 extends DoublerTest

class DoublerTest extends MultiNodeSpec(DoublerTestConfig) with ScalaTestMultiNodeSpec with ImplicitSender {

  import scala.collection.JavaConverters._

  import DoublerTestConfig._

  def initialParticipants = roles.size

  "a Doubler " must {
    "double input values " in {
      (4 to 100) foreach (input ⇒ test(input))
    }
  }

  def test(input: Integer) = {
    enterBarrier("start " + input);

    runOn(node1) {
      log.info("test from node {} with input = {}", node(node1), input)
      val nodes = List(node1, node2, node3).map(role ⇒ node(role).address)
      log.debug("nodes are {}", nodes)
      val doubler = system.actorOf(Doubler.mkProps(nodes.asJava))
      assert(doubler != null)
      doubler ! input
      val output = expectMsg(2 minutes, 2 * input)
      log.info("got {}", output)
      enterBarrier("output on " + node(node1) + " for " + input)
      enterBarrier("output on " + node(node2) + " for " + input)
      enterBarrier("output on " + node(node3) + " for " + input)
    }

    runOn(node2) {
      enterBarrier("output on " + node(node1) + " for " + input)
      log.info("test from node {} with input = {}", node(node2), input)
      val nodes = List(node2, node3, node1).map(role ⇒ node(role).address)
      log.debug("nodes are {}", nodes)
      val doubler = system.actorOf(Doubler.mkProps(nodes.asJava))
      assert(doubler != null)
      doubler ! input
      val output = expectMsg(2 minutes, 2 * input)
      log.info("got {}", output)
      enterBarrier("output on " + node(node2) + " for " + input)
      enterBarrier("output on " + node(node3) + " for " + input)
    }

    runOn(node3) {
      enterBarrier("output on " + node(node1) + " for " + input)
      enterBarrier("output on " + node(node2) + " for " + input)
      log.info("test from node {} with input = {}", node(node3), input)
      val nodes = List(node3, node1, node2).map(role ⇒ node(role).address)
      log.debug("nodes are {}", nodes)
      val doubler = system.actorOf(Doubler.mkProps(nodes.asJava))
      assert(doubler != null)
      doubler ! input
      val output = expectMsg(2 minutes, 2 * input)
      log.info("got {}", output)
      enterBarrier("output on " + node(node3) + " for " + input)
    }

    enterBarrier("end " + input)
  }
}
