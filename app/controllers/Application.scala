package controllers


import play.api.mvc.Controller
import play.api.libs.json.Json
import play.api.mvc.Action
import java.sql.SQLException
import scala.util.Failure
import scala.util.Success
import scala.util.Try
import play.api.Play.current
import play.api.db.slick._
import play.api.db.slick.Config.driver.simple._
import play.api.libs.json._
import scala.None
import play.api.libs.functional.syntax._
import java.util.concurrent.ExecutionException
import java.sql.SQLException

import models.db.Person
import models.db.Address
import models.db.Persons
import models.db.Addresses
import play.api.libs.functional.syntax._
import play.api.Logger
import play.api.data.Form
import play.api.data.Forms._

object Application extends Controller {

  
  val persons = TableQuery[Persons]
  val addresses = TableQuery[Addresses]

  case class AddressModel(street: String, city: String, state: String)
  /**
   * This is the one time activity, just need to populate db and 
   */
 /* DB.withSession { implicit s: Session =>

    persons.ddl.create
    addresses.ddl.create

    
    val am = AddressModel("Sus", "Pune", "MH")
    val a = Address(Some(1), am.street, "Pune", "MH")
    //val p = Person(Some(9), "SAM", "S@S.com" 32, "Male", a.id.get)
    val p = Person(Some(9), "SAM", "s@s.com", 32, "male", a.id.get)

    persons += p
    addresses += a
   // phones.insertAll(Phone(Some(10), "0755", "4007821", p.id.get), Phone(Some(11), "020", "8806492183", p.id.get), Phone(Some(12), "0755", "9893573674", p.id.get))
  }*/

  /**
   * Displays animals in position order.
   */
  def index = Action {
    Ok
  }

  /**
   * Updates an animal’s position. Use a form so we can generate a URL without
   * parameters in the template, and do validation.
   */
  def reposition = Action { implicit request =>
    Logger.trace("setPosition")

    Ok
  }
}