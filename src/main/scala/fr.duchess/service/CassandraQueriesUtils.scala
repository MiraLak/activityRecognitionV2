package fr.duchess.service

import com.datastax.spark.connector.CassandraRow
import org.apache.spark.rdd.RDD
import com.datastax.spark.connector.rdd.CassandraRDD
import fr.duchess.ActivityType
import fr.duchess.model.TimeWindow


object CassandraQueriesUtils {

  def getLatestAccelerations(cassandraRowsRDD: CassandraRDD[CassandraRow], user: String, maxAcceleration: Long) : RDD[CassandraRow] =
  {
    cassandraRowsRDD.select("timestamp", "x", "y", "z")
      .where("user_id=?", user)
      .withDescOrder
      .limit(maxAcceleration)
      .repartition(1)
  }

  def getUsers ( cassandraRowsRDD: CassandraRDD[CassandraRow]): Array[String]  =
  {
    cassandraRowsRDD.select("user_id")
      .distinct.map(row => row.toMap)
      .map(row => row.get("user_id").asInstanceOf[String])
      .collect()
  }

  def getTimesForUserAndActivity (cassandraRowsRDD: CassandraRDD[CassandraRow], userId: String, activity: ActivityType ) : RDD[Long]=
  {
    cassandraRowsRDD.select("timestamp", "activity")
      .where("user_id=? AND activity=?", userId, activity.name)
      .map(row => row.toMap)
      .map(row => row.get("timestamp").asInstanceOf[Long])
  }

  def getRangeDataForUserAndActivity(cassandraRowsRDD: CassandraRDD[CassandraRow],  userId: String, activity: ActivityType, timeWindow: TimeWindow, interval: Int, frequency:Long ): RDD[CassandraRow] ={
    cassandraRowsRDD.select("timestamp", "x", "y", "z")
      .where("user_id=? AND activity=? AND timestamp < ? AND timestamp > ?", userId, activity.name, timeWindow.stop + interval * frequency, timeWindow.stop + (interval - 1) * frequency)
      .withAscOrder
  }
}