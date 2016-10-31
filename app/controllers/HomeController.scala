package controllers

import javax.inject._
import play.api.mvc._
import javax.inject.Inject

import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.Json
import play.modules.reactivemongo.{MongoController, ReactiveMongoApi, ReactiveMongoComponents}
import reactivemongo.bson.{BSONObjectID, BSONDocument}
import repos._

object ApiFields {
  val Id    = "_id"
  val Name  = "name"
  val Email = "email"
}

@Singleton
class HomeController @Inject() extends Controller {

  def index = Action {
    Ok("Your new application is ready.")
  }

}

class ApiController @Inject()(val reactiveMongoApi: ReactiveMongoApi) extends Controller
  with MongoController with ReactiveMongoComponents {
  def ApiRepo = new ApiRepoImpl(reactiveMongoApi)

    def index = Action.async { implicit request =>
      ApiRepo.find().map(users => Ok(Json.toJson(users)))
    }

    def create = Action.async(BodyParsers.parse.json) { implicit request =>
      val name = (request.body \ ApiFields.Name).as[String]
      val email = (request.body \ ApiFields.Email).as[String]
      ApiRepo.save(BSONDocument(
        ApiFields.Name -> name,
        ApiFields.Email -> email
      )).map(result => Created)
    }

  }

