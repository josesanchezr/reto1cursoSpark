package models

final case class AppUserReview(appName: String,
                               translatedReview: String,
                               sentiment: String,
                               sentimentPolarity: String)
