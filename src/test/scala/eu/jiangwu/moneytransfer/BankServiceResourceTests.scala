package eu.jiangwu.moneytransfer

import akka.http.scaladsl.model.{ContentTypes, StatusCode}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import eu.jiangwu.moneytransfer.model._
import org.scalatest.{Matchers, WordSpec}
import eu.jiangwu.moneytransfer.model.AccountJsonSupport._
import eu.jiangwu.moneytransfer.model.ErrorDataJsonSupport._
import eu.jiangwu.moneytransfer.model.TransferDataJsonSupport._
import net.liftweb.json.Serialization.write


class BankServiceResourceTests extends WordSpec with Matchers with BankServiceResource with ScalatestRouteTest {
  implicit val formatsLift = net.liftweb.json.DefaultFormats

  "Bank Service Resource" should {
    "add new account on POST (/createAccount) and return a json with accountId GB00TEST00000000000001" in {
      val client = write(new Client("testFN", "testLN"))(formatsLift)
      Post(s"/createAccount").withEntity(ContentTypes.`application/json`, client) ~> bankServiceRoutes ~> check {
        status shouldBe StatusCode.int2StatusCode(201)
        responseAs[AccountWithoutTransfer].accountId shouldEqual "GB00TEST00000000000001"
      }
    }
    "return amount 0 with accountId GB00TEST00000000000001" in {
      Get(s"/getAmount?id=GB00TEST00000000000001") ~> bankServiceRoutes ~> check {
        status shouldBe StatusCode.int2StatusCode(201)
        responseAs[AccountWithoutTransfer].amount shouldBe 0
      }
    }
    "return transferlist empty with accountId GB00TEST00000000000001" in {
      Get(s"/getListTransfer?id=GB00TEST00000000000001") ~> bankServiceRoutes ~> check {
        status shouldBe StatusCode.int2StatusCode(201)
        responseAs[AccountTransferList].transferList.length shouldBe 0
      }
    }
    "return error for transfer money as sender GB00TEST00000000000001 because insufficient funds" in {
      val transferdata = new TransferData(new Client("testFN", "testLN"),"GB00TEST00000000000001", new Client("testFN2", "testLN2"), "GB00GUEST0000000000002",100)
      Post(s"/makeTransfer").withEntity(ContentTypes.`application/json`, write(transferdata)(formatsLift)) ~> bankServiceRoutes ~> check {
        status shouldBe StatusCode.int2StatusCode(400)
        responseAs[ErrorData].error shouldBe "insufficient funds"
      }
    }
    "return ok for transfer money as receiver GB00TEST00000000000001" in {
      val transferdata = new TransferData(new Client("testFN2", "testLN2"),"GB00GUEST0000000000001", new Client("testFN", "testLN"), "GB00TEST00000000000001",100)
      Post(s"/makeTransfer").withEntity(ContentTypes.`application/json`, write(transferdata)(formatsLift)) ~> bankServiceRoutes ~> check {
        status shouldBe StatusCode.int2StatusCode(201)
        responseAs[TransferData].receiverAccountId shouldBe transferdata.receiverAccountId
      }
    }
    "return amount 100 with accountId GB00TEST00000000000001 after transfer" in {
      Get(s"/getAmount?id=GB00TEST00000000000001") ~> bankServiceRoutes ~> check {
        status shouldBe StatusCode.int2StatusCode(201)
        responseAs[AccountWithoutTransfer].amount shouldBe 100
      }
    }
  }
}
