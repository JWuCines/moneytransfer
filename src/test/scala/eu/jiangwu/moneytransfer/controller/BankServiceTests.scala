package eu.jiangwu.moneytransfer.controller

import eu.jiangwu.moneytransfer.model.Client
import org.scalatest.FlatSpec

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

class BankServiceTests extends FlatSpec {
  implicit val executionContext: ExecutionContext = ExecutionContext.Implicits.global
  val bankService = new BankService()

  "New BankService" should "have 0 accounts" in {
    bankService.getNumberAccount == 0
  }

  "New BankService adding a new account" should "have accountId GB00TEST00000000000001" in {
    var res = false
    bankService.createAccount(new Client("Test3", "Test3")).onComplete {
      case Success(account) => res = account.map(a => a.accountId).getOrElse("") == "GB00TEST00000000000001"
      case Failure(_) =>
    }
    res && bankService.getNumberAccount == 1
  }

  "GB00TEST00000000000001" should "be an accountable id" in {
    bankService.isAccountableId("GB00TEST00000000000001")
  }

  "GB00GUEST0000000000001" should "not be an accountable id" in {
    !bankService.isAccountableId("GB00GUEST0000000000001")
  }
}
