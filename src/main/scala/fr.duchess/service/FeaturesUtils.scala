package fr.duchess.service

import org.apache.spark.api.java.JavaRDD
import org.apache.spark.mllib.linalg.{Vector, Vectors}
import org.apache.spark.mllib.stat.{MultivariateStatisticalSummary, Statistics}
import org.apache.spark.rdd.RDD


object FeaturesUtils {
  private var summary: MultivariateStatisticalSummary = null

  class FeaturesUtils(data: RDD[Vector]){
    summary = Statistics.colStats(data)

    /**
      * @return array [ (1 / n ) * ∑ |b - mean_b|, for b in {x,y,z} ]
      **/
    def computeAvgAbsDifference(data: RDD[Array[Double]], mean: Array[Double]): Array[Double] = {
      val abs: RDD[Vector] = data.map(acceleration => Array(
        Math.abs(acceleration(0)-mean(0) ),
        Math.abs(acceleration(1)-mean(1) ),
        Math.abs(acceleration(2)-mean(2) )))
        .map(Vectors.dense)

      Statistics.colStats(abs).mean.toArray
    }

    /**
      * @return Double resultant = 1/n * ∑ √(x² + y² + z²)
      */
    def computeResultantAcc(data: RDD[Array[Double]]): Double = {
      val squared: JavaRDD[Vector] = data.map(acceleration => Math.pow(acceleration(0), 2)
        +Math.pow(acceleration(1), 2)
        +Math.pow(acceleration(2), 2) )
        .map(Math.sqrt)
        .map(sum => Vectors.dense(Array(sum)
        ))
      Statistics.colStats(squared.rdd).mean.toArray(0)
    }

    /**
      * @return Double[] standard deviation  = √ 1/n * ∑ (x - u)² with u = mean x
      */
    def computeStandardDeviation(data: RDD[Array[Double]], mean: Array[Double]): Array[Double] = {

      val squared: JavaRDD[Vector] = data.map(acceleration => Array(
        Math.pow(acceleration(0) -mean(0), 2),
        Math.pow(acceleration(1) -mean(1), 2),
        Math.pow(acceleration(2) -mean(2), 2)
      )).map(sum => Vectors.dense(sum))

      val meanDiff: Array[Double] = Statistics.colStats(squared.rdd).mean.toArray
      if (meanDiff.length > 0)  Array[Double](Math.sqrt(meanDiff(0)), Math.sqrt(meanDiff(1)), Math.sqrt(meanDiff(2)))
      else Array[Double](0.0, 0.0, 0.0)
    }

    def computeDifferenceBetweenAxes(mean: Array[Double]): Double = {
      mean(0) - mean(1)
    }

    def computeMean: Array[Double] = {
      summary.mean.toArray
    }

    def computeVariance: Array[Double] = {
      summary.variance.toArray
    }

    def computeAvgTimeBetweenPeak(data: RDD[Array[Long]]): Double = {
      val max: Array[Double] = summary.max.toArray
      val filtered_y: RDD[Long] = data.filter(record => record(1) > 0.9 * max(1) ).map(record => record(0) ).sortBy(time => time, true, 1)

      if (filtered_y.count > 1) {
        val firstElement: Long = filtered_y.first
        val lastElement: Long = filtered_y.sortBy(time => time, false, 1).first
        val firstRDD: RDD[Long] = filtered_y.filter(record => record > firstElement)
        val secondRDD: RDD[Long] = filtered_y.filter(record => record < lastElement)
        val product: RDD[Vector] = firstRDD.zip(secondRDD).map(pair => pair._1 - pair._2).filter(value => value > 0).map(line => Vectors.dense(line))
        return Statistics.colStats(product).mean.toArray(0)
      }
      return 0.0
    }
  }


}