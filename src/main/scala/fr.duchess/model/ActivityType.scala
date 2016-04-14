package fr.duchess.model

sealed trait ActivityType {
  def order: Int
  def name: String
}

case object WALKING extends ActivityType {
  val order = 0
  val name = "Walking"
}

case object JOGGING extends ActivityType {
  val order = 1
  val name = "Jogging"
}

case object STANDING extends ActivityType {
  val order = 2
  val name = "Standing"
}

case object SITTING extends ActivityType {
  val order = 3
  val name = "Sitting"
}

object ActivityType {

  def fromPrediction(num: Int): String = num match {
    case WALKING.order => WALKING.name
    case JOGGING.order => JOGGING.name
    case STANDING.order => STANDING.name
    case SITTING.order => SITTING.name
    case _ => "!!ERROR!! Activity not defined"
  }


}




