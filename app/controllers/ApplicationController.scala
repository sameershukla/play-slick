package controllers

import play.api.mvc.Controller
import play.api.libs.json._
import play.api.libs.json.JsPath
import daos.PeopleAddress
import play.api.mvc.Action
import daos.DAO
import scala.util.Success
import scala.util.Failure
import models.db.Response
import play.api.libs.functional.syntax._
import models.db.Person
import models.db.Address
import scala.collection.mutable.ListBuffer
import java.sql.SQLException
import play.api.libs.json.Reads._
import play.api.data.validation.ValidationError
import play.mvc.Result

object ApplicationController extends Controller {

  /**
   * JSON Writes for Rendering JSON Response
   */
  implicit val personResponse = Json.writes[Person]
  implicit val addressResp = Json.writes[Address]
  implicit val response = Json.writes[Response]
  implicit val peopleAddress = Json.writes[PeopleAddress]

  /**
   * Model generated after JSON reads
   */
  case class PersonModel(name: String, email: String, age: Int, sex: String)
  case class AddressModel(street: String, city: String, state: String)
  case class PersonAddress(personModel: PersonModel, addressModel: AddressModel)

  /**
   * Read creates a PersonAddress object as PersonModel and AddressModel as composite objects
   */
  implicit val personAddressReads: Reads[PersonAddress] = (
    (JsPath \ "personalInfo").read(
      (
        (JsPath \ "name").read(minLength[String](5)) and
        (JsPath \ "email").read(email keepAnd minLength[String](5)) and
        (JsPath \ "age").read[Int] and
        (JsPath \ "sex").read[String])(PersonModel.apply _)) and
      (JsPath \ "addressInfo").read(
        (
          (JsPath \ "street").read(minLength[String](5)) and
          (JsPath \ "city").read[String] and
          (JsPath \ "state").read[String])(AddressModel.apply _)))(PersonAddress.apply _)

  /**
   * Find All, this method fetches the TupleList from DAO
   * and then display all the details in json
   */
  def find = Action { request =>
    val buf = new ListBuffer[PeopleAddress]
    DAO.findAll match {
      case Success(x) => x.foreach { case (p, a) => buf += PeopleAddress(p, a) }
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

  def insert = Action(parse.json) { request =>
    val json = request.body
    personAddressReads.reads(json).fold(
      invalid => BadRequest(JsError.toFlatJson(invalid)),
      valid => {
        val personAddressModel = json.as[PersonAddress]
        val r = scala.util.Random
        val addressModel = personAddressModel.addressModel
        val address = Address(Some(r.nextInt(100)), addressModel.street, addressModel.city, addressModel.state)
        val personModel = personAddressModel.personModel
        val person = Person(Some(r.nextInt(100)), personModel.name, personModel.email, personModel.age, personModel.sex, address.id.get)
        DAO.insert(person, address) match {
          case Success(x) if (x == 1) => Ok(Json.toJson(Response(200, "Person Created Successfully")))
          case Failure(t: SQLException) if (t.getSQLState == "23000") => Ok(Json.toJson(Response(409, "User already exists in the system")))
          case Failure(x) => Ok(Json.toJson(Response(500, "Internal Server Error")))
        }
      })
  }

}
