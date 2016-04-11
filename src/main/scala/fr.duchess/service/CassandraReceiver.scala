package fr.duchess.service

import java.util
import java.util.Date

import com.datastax.spark.connector.{CassandraRow, SparkContextFunctions}
import com.datastax.spark.connector.rdd.CassandraRDD
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD
import org.apache.spark.mllib.linalg.Vector
import org.apache.spark.mllib.tree.model.RandomForestModel
import org.apache.spark.streaming.receiver.Receiver
import org.apache.spark.storage.StorageLevel
import com.datastax.spark.connector._


object CassandraReceiver {

  class CassandraReceiver(val storage:StorageLevel, val cassandraRowsRDD:CassandraRDD[CassandraRow]) extends Receiver[RDD[CassandraRow]](storage:StorageLevel) {

    val RANDOM_FOREST_PREDICTION_MODEL: String = "predictionModel/RandomForest/training_acceleration_3"
    val ACCELERATION_TOTAL: Long = 100l
    val KEYSPACE: String = "activityrecognition"
    val RESULT_TABLE: String = "result"
    val TEST_USER: String = "TEST_USER"
    val ACCELERATION_TABLE: String = "acceleration"

    override def onStart()={
      new Thread(){
        override def run()={ receive()}
      }.start()
    }

    override def onStop()={}

    def receive() = {
      try {
        while (!cassandraRowsRDD.isEmpty) {
          {
            val data:RDD[CassandraRow] = CassandraQueriesUtils.getLatestAccelerations(cassandraRowsRDD, TEST_USER, ACCELERATION_TOTAL)
            this.store(data)
          }
        }
        restart("Trying to connect again")
      }
      catch {
        case t: Throwable => {
          restart("Error receiving data", t)
        }
      }
    }
  }


}