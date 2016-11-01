package repos

import javax.inject.Inject

import play.api.libs.json.{JsObject, Json}
import play.modules.reactivemongo.ReactiveMongoApi
import play.modules.reactivemongo.json._
import play.modules.reactivemongo.json.collection.JSONCollection
import reactivemongo.api.ReadPreference
import reactivemongo.api.commands.WriteResult
import reactivemongo.bson.{BSONDocument, BSONObjectID}

import scala.concurrent.{ExecutionContext, Future}

trait ApiRepo {
  def find()(implicit ec: ExecutionContext): Future[List[JsObject]]

  def save(selector: BSONDocument)(implicit ec: ExecutionContext): Future[WriteResult]
}

class MongoApiRepo @Inject()(reactiveMongoApi: ReactiveMongoApi) extends ApiRepo {

  def collection = reactiveMongoApi.db.collection[JSONCollection]("users")

  override def find()(implicit ec: ExecutionContext): Future[List[JsObject]] = {
    val genericQueryBuilder = collection.find(Json.obj())
    val cursor = genericQueryBuilder.cursor[JsObject](ReadPreference.Primary)
    cursor.collect[List]()
  }

  override def save(document: BSONDocument)(implicit ec: ExecutionContext): Future[WriteResult] = {
    collection.update(BSONDocument("_id" -> document.get("_id").getOrElse(BSONObjectID.generate)), document, upsert = true)
  }

}