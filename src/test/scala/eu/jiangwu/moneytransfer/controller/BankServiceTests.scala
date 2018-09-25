package eu.jiangwu.moneytransfer.controller

import akka.http.scaladsl.server.Directives._
import eu.jiangwu.moneytransfer.model.Client
import org.scalatest.FlatSpec

import scala.concurrent.ExecutionContext

class BankServiceTests extends FlatSpec {
  implicit val executionContext: ExecutionContext = ExecutionContext.Implicits.global
  val bankService = new BankService()

  "New BankService" should "have 0 accounts" in {
    bankService.getNumberAccount == 0
  }

  /*"New BankService adding a new account" should "have accountId GB00TEST00000000000001" in {
    val accountId = onSuccess(bankService.createAccount(new Client("Test3", "Test3"))) {
      case Some(action) => Some(action.accountId)
      case _ => None
    }
    bankService.getNumberAccount == 1 && bankService.
  }*/
}
