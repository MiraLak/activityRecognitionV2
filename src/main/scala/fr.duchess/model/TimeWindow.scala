package fr.duchess.model


object TimeWindow {
  class TimeWindow(val start1:Long, val stop1:Long, val intervals1:Long){
    var start:Long = start1
    var stop:Long = stop1
    var intervals: Long = intervals1
  }
}
