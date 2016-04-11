package fr.duchess

sealed trait ActivityType {
  def order: Int

  def name: String

  def fromPrediction(num: Int): String = {
    if (num == order) name else "!!ERROR!! Activity not defined"
  }

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

  def fromPrediction(num: Int): String = {
    if (num == WALKING.order) WALKING.name
    if (num == JOGGING.order) JOGGING.name
    if (num == STANDING.order) STANDING.name
    if (num == SITTING.order) SITTING.name
    else "!!ERROR!! Activity not defined"
  }
}




