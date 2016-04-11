package fr.duchess.model

object PredictionResult {
  case class PredictionResult(val user_id:String, val timestamp:Long, val prediction:String)
}
