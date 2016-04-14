package fr.duchess.model.classification

import org.apache.spark.SparkContext
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.tree.RandomForest
import org.apache.spark.rdd.RDD


class RandomForestModel(val sc: SparkContext, val training: RDD[LabeledPoint], val test: RDD[LabeledPoint]) {
  var trainingData: RDD[LabeledPoint] = training
  var testData: RDD[LabeledPoint] = test

  def createModel() = {

    val categoricalFeaturesInfo: Map[Int, Int] = Map.empty
    val numTree: Int = 10
    val numClasses: Int = 4
    val featureSubsetStrategy: String = "auto"
    val impurity: String = "gini"
    val maxDepth: Int = 20
    val maxBins: Int = 32
    val randomSeeds: Int = 12345

    val model = RandomForest.trainClassifier(trainingData, numClasses, categoricalFeaturesInfo, numTree, featureSubsetStrategy, impurity, maxDepth, maxBins, randomSeeds)
    model.save(sc, "randomForestModel")

    val accuracy: Double = 1.0 * testData.map(p => (model.predict(p.features), p.label))
      .filter(pl => pl._1 == pl._2)
      .count() / testData.count()

    accuracy
  }
}