import context.Context._
import models.{AppAndReview, AppGooglePlayStore, AppUserReview}
import sparkSession.implicits._

import scala.util.Try

object AppPlayStore extends App {
  // Load Apps
  val playstore = sparkSession.read
    .format("csv")
    .option("header", "true")
    .load("src/main/resources/playstore/googleplaystore.csv")
    .select("App", "Rating")
    .as[AppGooglePlayStore]

  // Load Apps' reviews
  val reviews = sparkSession.read
    .format("csv")
    .option("header", "true")
    .load("src/main/resources/playstore/googleplaystore_user_reviews.csv")
    .selectExpr(
      "`AppName` as appName",
      "`Translated_Review` as translatedReview",
      "`Sentiment` as sentiment",
      "`Sentiment_Polarity` as sentimentPolarity"
    )
    .as[AppUserReview]

  // Filter Apps with a rating major to 4.7
  val rating =
    playstore.filter(app => Try(app.rating.toFloat > 4.7).getOrElse(false))

  // Join Apps with a rating major to 4.7 and theirs reviews
  val joinAppsPlaystoreAndReviews = rating
    .join(reviews, rating.col("app") === reviews.col("appName"))
    .select(
      "app",
      "rating",
      "translatedReview",
      "sentiment",
      "sentimentPolarity"
    )
    .as[AppAndReview]

  val bestApps = joinAppsPlaystoreAndReviews
    .filter(app => app.sentiment == "Negative")
    .orderBy($"sentimentPolarity".desc)
    .as[AppAndReview]

  val result = bestApps.select("app", "translatedReview")

  result.show(10)

  sparkSession.sparkContext.stop()
}
