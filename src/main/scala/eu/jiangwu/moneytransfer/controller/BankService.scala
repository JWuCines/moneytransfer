package eu.jiangwu.moneytransfer.controller

import com.typesafe.scalalogging.LazyLogging
import eu.jiangwu.moneytransfer.model._

import scala.collection.mutable
import scala.concurrent.{ExecutionContext, Future}

class BankService(implicit val executionContext: ExecutionContext) extends LazyLogging {

  private val accountIdPrefix = "GB00TEST"
  private var accountIdSuffix: Integer = 1

  private val accounts: mutable.HashMap[String, Account] = mutable.HashMap.empty

  def getNumberAccount = accounts.size

  def createAccount(owner: Client): Future[Option[Account]] = {
    val accountId = accountIdPrefix + f"$accountIdSuffix%014d"
    Future {
      accounts += accountId -> new Account(accountId, owner)
      accountIdSuffix += 1
      accounts.get(accountId)
    }
  }

  def makeTransfer(transfer: TransferData): Future[Option[Any]] =
    Future {
      if(!accounts.contains(transfer.senderAccountId)) {
        if(accounts.contains(transfer.receiverAccountId)) {
          accounts.get(transfer.receiverAccountId).map(_.addTransfer(transfer))
          logger.debug(s"Transfer ${transfer.transferId} executed")
          Some(transfer)
        } else {
            logger.debug(s"Transfer ${transfer.transferId} rejected: accounts not managed")
            None
        }
      }
      else {
        if(!accounts.contains(transfer.receiverAccountId) && isAccountableId(transfer.receiverAccountId)) {
          logger.debug(s"Transfer ${transfer.transferId} rejected: receiver not found")
          Some(new ErrorData("receiver not found"))
        } else {
          if(accounts.get(transfer.senderAccountId).exists(_.addTransfer(transfer))) {
            accounts.get(transfer.receiverAccountId).map(_.addTransfer(transfer))
            logger.debug(s"Transfer ${transfer.transferId} executed")
            Some(transfer)
          } else {
              logger.debug(s"Transfer ${transfer.transferId} rejected: insufficient funds")
              Some(new ErrorData("insufficient funds"))
          }
        }
      }
    }

  def getAmount(accountId: String): Future[Option[AccountWithoutTransfer]] =
    Future(accounts.get(accountId).map(_.getAmount()).orElse(None))


  def getListTransfer(accountId: String, start: Long, end: Long): Future[Option[AccountTransferList]] =
    Future(accounts.get(accountId).map(_.getTransfers(start, end)).orElse(None))

  def isAccountableId(accountId: String): Boolean = accountId.startsWith(accountIdPrefix)
}