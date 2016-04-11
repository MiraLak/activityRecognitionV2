package fr.duchess.service

import com.datastax.spark.connector.CassandraRow
import fr.duchess.ActivityType
import fr.duchess.service.FeaturesUtils.FeaturesUtils
import org.apache.spark.mllib.linalg.{Vector, Vectors}
import org.apache.spark.mllib.tree.model.{DecisionTreeModel, RandomForestModel}
import org.apache.spark.rdd.RDD

object FeaturesService {
  def computeFeatures(data: RDD[CassandraRow]): Vector = {
    var features: Array[Double] = new Array[Double](15)
    if (data.count > 0) {
      val accelerationData: RDD[Array[Double]] = data.map(row => row.toMap)
        .map(row => Array(row.get("x").asInstanceOf[Double],row.get("y").asInstanceOf[Double],row.get("z").asInstanceOf[Double]))
      val vectorsXYZ: RDD[Vector] = accelerationData.map(Vectors.dense)

      val timestampAndY: RDD[Array[Long]] = data.map(row => row.toMap)
        .map(row => Array(row.get("timestamp").asInstanceOf[Long],row.get("y").asInstanceOf[Long]))

      val feature: FeaturesUtils = new FeaturesUtils(vectorsXYZ)
      val mean: Array[Double] = feature.computeMean
      val variance: Array[Double] = feature.computeVariance
      val standardDeviation: Array[Double] = feature.computeStandardDeviation(accelerationData, mean)
      val avgAbsDiff: Array[Double] = feature.computeAvgAbsDifference(accelerationData, mean)
      val resultant: Double = feature.computeResultantAcc(accelerationData)
      val avgTimePeak: Double = feature.computeAvgTimeBetweenPeak(timestampAndY)
      val difference: Double = feature.computeDifferenceBetweenAxes(mean)
      features = Array[Double](mean(0), mean(1), mean(2), variance(0), variance(1), variance(2), standardDeviation(0), standardDeviation(1), standardDeviation(2), avgAbsDiff(0), avgAbsDiff(1), avgAbsDiff(2), resultant, avgTimePeak, difference)
    }
    Vectors.dense(features)
  }

  def predict(model: DecisionTreeModel, feature: Vector): String = {
    val prediction: Double = model.predict(feature)
    ActivityType.fromPrediction(prediction.toInt)
  }

  def predict(model: RandomForestModel, feature: Vector): String = {
    val prediction: Double = model.predict(feature)
    ActivityType.fromPrediction(prediction.toInt)
  }

}