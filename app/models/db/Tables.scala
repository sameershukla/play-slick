package models.db

import play.api.db.slick.Config.driver.simple._

case class Person(id: Option[Int], name: String, email: String, age: Int, sex: String, addressId: Int)
// 1 - 1 with people
case class Address(id: Option[Int], street: String, city: String, state: String)

case class Response(code: Int, message: String)

class Persons(tag: Tag) extends Table[Person](tag, "PERSON") {

  def id = column[Int]("PERSON_ID", O.PrimaryKey)
  def name = column[String]("NAME")
  def age = column[Int]("AGE")
  def sex = column[String]("SEX")
  def email = column[String]("EMAIL")

  def addressId = column[Int]("ADDRESS_ID")

 // def address = foreignKey("ADDRESS_FK", addressId, TableQuery[Addresses])(_.id)
  def nameIndex = index("NAME_INDEX", (name), unique = true)
  def * = (id.?, name, email, age, sex, addressId) <> ((Person.apply _).tupled, Person.unapply)
}

class Addresses(tag: Tag) extends Table[Address](tag, "ADDRESS") {

  def id = column[Int]("ADDRESS_ID", O.PrimaryKey)
  def street = column[String]("STREET")
  def city = column[String]("CITY")
  def state = column[String]("STATE")

  def * = (id.?, street, city, state) <> ((Address.apply _).tupled, Address.unapply)
}

