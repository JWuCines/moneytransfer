package eu.jiangwu.moneytransfer.model

import org.scalatest.FlatSpec

class AccountTest extends FlatSpec {
    val account = new Account("GBTESTTESTTESTTEST", new Client("Test", "Test"))

    "Amount for new account" should "have amount 0" in {
      account.getAmount().amount == 0.0
    }

    "TransferList for new account" should "return empty with size 0" in {
      val transfers = account.getTransfers().transferList
      transfers.isEmpty && transfers.length == 0
    }

    "Transfer as sender for new account of amount 100" should "return false, because insufficient funds" in {
      //sender: Client, senderAccountId: String, receiver: Client, receiverAccountId: String, amount: Double
      val client2 = new Client("test2" , "test2")
      val transfer = new TransferData(account.owner, account.accountId, client2, "GBTESTTESTTEST2", 100)
      !account.addTransfer(transfer)
    }

    "Transfer as receiver for new account of amount 100" should "return true with amount of 100 and transferList with 1 element" in {
      //sender: Client, senderAccountId: String, receiver: Client, receiverAccountId: String, amount: Double
      val client2 = new Client("test2" , "test2")
      val transfer = new TransferData(client2, "GBTESTTESTTEST2", account.owner, account.accountId, 100)
      account.addTransfer(transfer) && account.getAmount().amount == 0 && account.getTransfers().transferList.length ==1
    }
}
