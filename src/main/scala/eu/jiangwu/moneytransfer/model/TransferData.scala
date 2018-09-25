package eu.jiangwu.moneytransfer.model

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.DefaultJsonProtocol
import ClientJsonSupport._

case class TransferData(sender: Client, senderAccountId: String, receiver: Client, receiverAccountId: String, amount: Double) {
  val timestamp: Long = System.currentTimeMillis()
  val transferId: String = f"$timestamp%014d" + "-" + Math.abs(sender.hashCode).formatted("%010d")  + "-" + Math.abs(receiver.hashCode).formatted("%010d")
}

object TransferDataJsonSupport extends DefaultJsonProtocol with SprayJsonSupport {
  implicit val transferDataFormats = jsonFormat(TransferData, "sender", "senderAccountId", "receiver", "receiverAccountId", "amount")
}