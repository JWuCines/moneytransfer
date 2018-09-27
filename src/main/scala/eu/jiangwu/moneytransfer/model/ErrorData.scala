package eu.jiangwu.moneytransfer.model

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.DefaultJsonProtocol

case class ErrorData(error: String)

object ErrorDataJsonSupport extends DefaultJsonProtocol with SprayJsonSupport {
  implicit val errorDataFormats = jsonFormat(ErrorData, "error")
}
