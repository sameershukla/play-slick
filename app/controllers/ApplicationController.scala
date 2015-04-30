package controllers

import play.api.mvc.Controller
import models.db.Person
import play.api.libs.json._
import play.api.libs.json.JsPath
import models.db.Address
import daos.PeopleAddress
import play.api.mvc.Action
import daos.DAO
import scala.util.Success
import scala.util.Failure
import models.db.Response
import models.db.Person
import play.api.libs.functional.syntax._
import models.db.Person
import models.db.Address
import models.db.Person
import models.db.Address
import scala.collection.mutable.ListBuffer
import daos.PeopleAddress
import daos.PeopleAddress
import daos.PeopleAddress
import java.sql.SQLException

object ApplicationController extends Controller {

  /**
   * JSON Writes for Rendering JSON Response
   */
  implicit val personResponse = Json.writes[Person]
  implicit val addressResp = Json.writes[Address]
  implicit val response = Json.writes[Response]
  implicit val peopleAddress = Json.writes[PeopleAddress]

  implicit val personFormat = Json.format[Person]
  implicit val addressFormat = Json.format[Address]

  /**
   * Model generated after JSON reads
   */
  case class PersonModel(name: String, email: String, age: Int, sex: String)
  case class AddressModel(street: String, city: String, state: String)

  implicit val personReads: Reads[PersonModel] = (
    (JsPath \ "name").read[String] and
    (JsPath \ "email").read[String] and
    (JsPath \ "age").read[Int] and
    (JsPath \ "sex").read[String])(PersonModel.apply _)

  implicit val addressReads: Reads[AddressModel] = (
    (JsPath \ "street").read[String] and
    (JsPath \ "city").read[String] and
    (JsPath \ "state").read[String])(AddressModel.apply _)

  /**
   * Find All, this method fetches the TupleList from DAO
   * and then display all the details in json
   */
  def find = Action { request =>
    val buf = new ListBuffer[PeopleAddress]
    DAO.findAll match {
      case Success(x) => x.foreach {
        case (p, a) => buf += PeopleAddress(p, a)
      }
      case Failure(x) => Ok(Json.toJson(Response(409, "No Record Found")))
    }

    Ok(Json.obj("persondetails" -> buf))
  }

  /**
   * Find Person Address By Name
   */
  def findAddressByUserName(name: String) = Action { request =>
    DAO.findAddressByName(name) match {
      case Success(x) => Ok(Json.toJson(x))
      case Failure(x) => Ok(Json.toJson(Response(409, "Can't find address of given user")))
    }
  }

  /**
   * Delete Operation
   */
  def removeDetails(name: String) = Action { request =>
    DAO.delete(name) match {
      case Success(x) if (x == 1) => Ok(Json.toJson(Response(200, "Person Removed From the system")))
      case Failure(x)             => Ok(Json.toJson(Response(409, "Person Not Found")))
    }

  }

  /**
   * Insert Person
   */
  def insert = Action(parse.json) { request =>
    val json = request.body
    val personalInfo = (json \ "personalInfo")
    val personModel = personalInfo.as[PersonModel]
    //val placeResult: JsResult[PersonModel] = json.validate[PersonModel]
    val addressModel = (json \ "addressInfo").as[AddressModel]
    val r = scala.util.Random
    val address = Address(Some(r.nextInt(100)), addressModel.street, addressModel.city, addressModel.state)
    val person = Person(Some(r.nextInt(100)), personModel.name, personModel.email, personModel.age, personModel.sex, address.id.get)
    DAO.insert(person, address) match {
      case Success(x) if (x == 1) => Ok(Json.toJson(Response(200, "Person Created Successfully")))
      case Failure(t: SQLException) if (t.getSQLState == "23000") => Ok(Json.toJson(Response(409, "User already exists in the system")))
      case Failure(x) => Ok(Json.toJson(Response(500, "Internal Server Error")))
    }
  }
}