package eu.jiangwu.moneytransfer

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.ExecutionContext

object Main extends App with BankServiceResource with LazyLogging {
  val config = ConfigFactory.load()
  val host = "localhost"
  val port = 33333

  implicit val system = ActorSystem("bank-transfer-system")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContext = system.dispatcher

  Http().bindAndHandle(handler = bankServiceRoutes, interface = host, port = port) map { binding =>
    logger.info(s"REST interface bound to ${binding.localAddress}") } recover { case ex =>
    logger.error(s"REST interface could not bind to $host:$port", ex.getMessage)
  }
}
