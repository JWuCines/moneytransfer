package eu.jiangwu.moneytransfer

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, StatusCodes}
import com.typesafe.scalalogging.LazyLogging
import eu.jiangwu.moneytransfer.controller.BankService
import eu.jiangwu.moneytransfer.model.{Client, ErrorData, TransferData}
import org.joda.time.format.DateTimeFormat
import eu.jiangwu.moneytransfer.model.ClientJsonSupport._
import eu.jiangwu.moneytransfer.model.TransferDataJsonSupport._
import scala.concurrent.ExecutionContext.Implicits.global
import net.liftweb.json.Serialization.write


trait BankServiceResource extends LazyLogging with SprayJsonSupport {
  lazy val bankService = new BankService()
  implicit val formats = net.liftweb.json.DefaultFormats

  val bankServiceRoutes: Route =
      post {
        path("createAccount") {
          entity(as[Client]) { account =>
            logger.info(s"createAccount Request: ${account.firstname} ${account.lastname}")
            onSuccess(bankService.createAccount(account)) {
              case Some(a) => {
                logger.info(s"createAccount Request accepted: ${a.owner.firstname} ${a.owner.lastname} with id ${a.accountId}")
                complete((StatusCodes.Created, HttpEntity(ContentTypes.`application/json`, write(a.getAmount()))))
              }
              case None => {
                logger.info(s"createAccount Request refused: ${account.firstname} ${account.lastname}")
                complete((StatusCodes.BadRequest, HttpEntity(ContentTypes.`application/json`, write(new ErrorData("Error to create account")))))
              }
            }
          }
        } ~
        path("makeTransfer") {
          entity(as[TransferData]) { transfer =>
            logger.info(s"makeTransfer Request from ${transfer.sender.firstname} ${transfer.sender.lastname} ${transfer.senderAccountId} to ${transfer.receiver.firstname} ${transfer.receiver.lastname} ${transfer.receiverAccountId} with amount ${transfer.amount}")
            onSuccess(bankService.makeTransfer(transfer)) {
              case Some(t: TransferData) => {
                logger.info(s"makeTransfer Request ${t.senderAccountId} to ${t.receiverAccountId} with amount ${t.amount} accepted")
                complete((StatusCodes.Created, HttpEntity(ContentTypes.`application/json`, write(t))))
              }
              case Some(e: ErrorData) => {
                logger.info(s"makeTransfer Request ${transfer.senderAccountId} to ${transfer.receiverAccountId} with amount ${transfer.amount} failed: ${e.error}")
                complete((StatusCodes.BadRequest, HttpEntity(ContentTypes.`application/json`, write(e))))
              }
              case _ => {
                logger.info(s"makeTransfer Request ${transfer.senderAccountId} to ${transfer.receiverAccountId} with amount ${transfer.amount} failed: Accounts not managed")
                complete((StatusCodes.BadRequest, HttpEntity(ContentTypes.`application/json`, write(new ErrorData("Accounts not managed")))))
              }
            }
          }
        }
      } ~
      get {
        path("getAmount") {
          parameters('id) { id =>
            logger.info(s"getAmount Request for ${id}")
            onSuccess(bankService.getAmount(id)) {
              case Some(t) => {
                logger.info(s"getAmount Request for ${id} accepted: ${t.amount}")
                complete((StatusCodes.Created, HttpEntity(ContentTypes.`application/json`,write(t))))
              }
              case None => {
                logger.info(s"getAmount Request for ${id} refused: Account not found")
                complete((StatusCodes.BadRequest, HttpEntity(ContentTypes.`application/json`, write(new ErrorData("Account not found")))))
              }
            }
          }
        } ~
        path("getListTransfer") {
          parameters('id, 'startDate.?, 'endDate.?) { (id, startDate, endDate) =>
            val sDate = if(startDate.isDefined) strToEpoch(startDate.get+"T00:00:00.000") else 0
            val eDate = if(endDate.isDefined) strToEpoch(endDate.get+"T23:59:59.999") else System.currentTimeMillis()
            logger.info(s"getListTransfer Request for ${id}")
            onSuccess(bankService.getListTransfer(id, sDate, eDate)) {
              case Some(t) => {
                logger.info(s"getListTransfer Request for ${t.accountId} accepted: ${t.transferList.length} transfer listed")
                complete((StatusCodes.Created, HttpEntity(ContentTypes.`application/json`, write(t))))
              }
              case None => {
                logger.info(s"getListTransfer Request for ${id} refused: Account not found")
                complete((StatusCodes.BadRequest, HttpEntity(ContentTypes.`application/json`,write(new ErrorData("Account not found")))))
              }
            }
          }
        }
      }
  private def strToEpoch(date: String): Long =
    DateTimeFormat.forPattern("YYYY-MM-dd'T'HH:mm:ss.SSS").parseDateTime(date).getMillis()
}
