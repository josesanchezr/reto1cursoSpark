package models

case class AppAndReview(app: String,
                        rating: String,
                        translatedReview: String,
                        sentiment: String,
                        sentimentPolarity: String)
