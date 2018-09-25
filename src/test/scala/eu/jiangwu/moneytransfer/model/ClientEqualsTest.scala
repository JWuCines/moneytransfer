package eu.jiangwu.moneytransfer.model

import org.scalatest._

class ClientEqualsTest extends FlatSpec {
  "The comparison of two Client object with the same first name and last name" should  "return true" in {
    val client1: Client = new Client("Test", "Test")
    val client2: Client = new Client("Test", "Test")
    client1.equals(client2)
  }
}
