package eu.jiangwu.moneytransfer.model

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.typesafe.scalalogging.LazyLogging
import ClientJsonSupport._
import TransferDataJsonSupport._
import spray.json.DefaultJsonProtocol

import scala.collection.mutable.ListBuffer

case class AccountTransferList(accountId: String, owner: Client, transferList: List[TransferData])
case class AccountWithoutTransfer(accountId: String, owner: Client, amount: Double)

class Account(val accountId: String, val owner: Client) extends LazyLogging {
  private var amount: Double = 0
  private val transfers: ListBuffer[TransferData] = ListBuffer.empty[TransferData]

  def addTransfer(transfer: TransferData): Boolean = {
    if(accountId.equals(transfer.senderAccountId) && this.amount >= transfer.amount) {
      logger.debug(s"addTransfer ${transfer.transferId} as sender - initial amount: $amount")
      transfers += transfer
      this.amount -= transfer.amount
      logger.debug(s"addTransfer ${transfer.transferId} as sender - final amount: $amount")
      true
    }
    else if(accountId.equals(transfer.receiverAccountId)) {
      logger.debug(s"addTransfer ${transfer.transferId} as receiver initial amount: $amount")
      transfers += transfer
      this.amount += transfer.amount
      logger.debug(s"addTransfer ${transfer.transferId} as receiver - final amount: $amount")
      true
    }
    else false
  }

  def getAmount(): AccountWithoutTransfer = new AccountWithoutTransfer(accountId, owner, amount)

  def getTransfers(start: Long = 0, end: Long = System.currentTimeMillis()) = {
    new AccountTransferList(accountId, owner, transfers.toList.filter(t => t.timestamp > start && t.timestamp < end))
  }
}

object AccountJsonSupport extends DefaultJsonProtocol with SprayJsonSupport {
  implicit val accountWithoutTransferFormats = jsonFormat(AccountWithoutTransfer, "accountId", "owner", "amount")
  implicit val accountTransferListFormats = jsonFormat(AccountTransferList, "accountId", "owner", "transferList")
}
