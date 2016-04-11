package fr.duchess.service

import java.util.Date

import com.datastax.spark.connector.{CassandraRow, SparkContextFunctions}
import com.datastax.spark.connector.rdd.CassandraRDD
import fr.duchess.model.PredictionResult.PredictionResult
import fr.duchess.service.CassandraReceiver.CassandraReceiver
import org.apache.spark.SparkConf
import org.apache.spark.mllib.tree.model.RandomForestModel
import org.apache.spark.rdd.RDD
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.dstream.ReceiverInputDStream
import org.apache.spark.streaming.{Durations, StreamingContext}

object PredictionService{
    val RANDOM_FOREST_PREDICTION_MODEL: String = "predictionModel/RandomForest/training_acceleration_3"
    val ACCELERATION_TOTAL: Long = 100l
    val KEYSPACE: String = "activityrecognition"
    val RESULT_TABLE: String = "result"
    val TEST_USER: String = "TEST_USER"
    val ACCELERATION_TABLE: String = "acceleration"

    def main(args: Array[String]) {
      val sparkConf: SparkConf = new SparkConf().setAppName("User's physical activity recognition").set("spark.cassandra.connection.host", "127.0.0.1").setMaster("local[*]")
      predictWithRealTimeStreaming(sparkConf)
    }

    private def predictWithRealTimeStreaming(sparkConf: SparkConf) {

      val ssc = new StreamingContext(sparkConf, Durations.seconds(5))

      val cassandraRowsRDD: CassandraRDD[CassandraRow] = new SparkContextFunctions(ssc.sparkContext).cassandraTable(KEYSPACE, ACCELERATION_TABLE)
      val model: RandomForestModel = RandomForestModel.load(ssc.sparkContext, RANDOM_FOREST_PREDICTION_MODEL)

      val cassandraReceiver: ReceiverInputDStream[RDD[CassandraRow]] = ssc.receiverStream(new CassandraReceiver(StorageLevel.MEMORY_ONLY, cassandraRowsRDD))
      cassandraReceiver.map(rdd => {
        val predict: String = FeaturesService.predict(model,FeaturesService.computeFeatures(rdd))
        //val predictions: List[PredictionResult] = List(new PredictionResult(TEST_USER, new Date().getTime, predict))
        //val result:RDD[PredictionResult] = ssc.sparkContext.parallelize(predictions)
        //result.saveToCassandra(KEYSPACE, "result")
        System.out.println("Predicted activity = " + predict)
      })

      cassandraReceiver.print
      //System.out.println("Predicted activity = " + result)
      ssc.start
      ssc.awaitTermination
    }

    case class PredictionResultA(val user_id:String, val timestamp:Long, val prediction:String)
}