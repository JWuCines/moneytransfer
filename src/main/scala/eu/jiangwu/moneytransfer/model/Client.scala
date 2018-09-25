package eu.jiangwu.moneytransfer.model

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.DefaultJsonProtocol


case class Client(firstname: String, lastname: String) {
  private val id: String = "GB"+firstname.hashCode+""+lastname.hashCode

  override def equals(obj: Any): Boolean =  {
    if(!obj.isInstanceOf[Client]) false
    else {
      obj.asInstanceOf[Client].id.equals(this.id)
    }
  }

  override def hashCode(): Int = this.id.hashCode
}

object ClientJsonSupport extends DefaultJsonProtocol with SprayJsonSupport {
  implicit val clientFormats = jsonFormat(Client, "firstname", "lastname")
}

