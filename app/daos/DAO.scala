package daos

import scala.None
import scala.collection.mutable.ListBuffer
import scala.util.Try
import models.db.Address
import models.db.Addresses
import models.db.Person
import models.db.Persons
import play.api.Play.current
import play.api.db.slick._
import play.api.db.slick.Config.driver.simple._
import java.sql.SQLException

//case class PeopleAddress(person: Person, address: Address, phones: List[Phone])
case class PeopleAddress(person: Person, address: Address)

/**
 *  BaseDAO
 */
trait BaseDAO {

  def insert(person: Person, address: Address): Try[Int]
  def delete(name: String): Try[Int]
  def findAll:Try[List[(Person, Address)]]
  def findAddressByName(name: String): Try[Address]

}

/**
 * BaseDaoImpl
 */
object DAO extends BaseDAO {

  /**
   * TableQuery
   */
  val persons = TableQuery[Persons]
  val addresses = TableQuery[Addresses]

  /**
   * Insert
   */
  def insert(person: Person, address: Address): Try[Int] = {
    DB.withTransaction { implicit s: Session =>
      Try {
        addresses += address
        persons += person
      }
    }
  }

  /**
   * Delete
   */
  def delete(name: String): Try[Int] = {
    DB.withTransaction { implicit s: Session =>
      Try {
        val personEntity = persons.filter(x => x.name === name)
        val person = personEntity.list.head
        val addressEntity = addresses.filter { x => x.id === person.addressId }.delete
        personEntity.delete
      }
    }
  }

  /**
   * FindAll details
   */
  def findAll: Try[List[(Person, Address)]] = {
    DB.withSession { implicit s: Session =>
      val output = for {
        add <- addresses 
        per <- persons if(add.id === per.addressId)
      } yield (per, add)

      Try(output.list)
    }

  }

  /**
   * Fetch Address by Person Name
   */
  def findAddressByName(name: String): Try[Address] = {
    DB.withSession { implicit s: Session =>
      val innerjoin = for {
        p <- persons
        a <- addresses if (p.name === name && p.addressId === a.id)
      } yield (a)
      Try(innerjoin.list.head)
    }
  }
}