package fr.duchess.model.classification

import org.apache.spark.SparkContext
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.tree.RandomForest
import org.apache.spark.rdd.RDD


object RandomForestModel{

  class RandomForestModel(val sc:SparkContext, val training:RDD[LabeledPoint], val test:RDD[LabeledPoint]){
    var trainingData:RDD[LabeledPoint] = training
    var testData:RDD[LabeledPoint] = test

    def createModel()={

      var categoricalFeaturesInfo:Map[Int,Int]= Map.empty
      var numTree:Int = 10
      var numClasses:Int = 4
      var featureSubsetStrategy:String = "auto"
      var impurity:String = "gini"
      var maxDepth:Int = 20
      var maxBins:Int = 32
      var randomSeeds:Int = 12345

      var model = RandomForest.trainClassifier(trainingData, numClasses,categoricalFeaturesInfo, numTree, featureSubsetStrategy, impurity,maxDepth, maxBins, randomSeeds)
      model.save(sc, "randomForestModel")

      var accuracy :Double =  1.0 * (
        testData.map(p => (model.predict(p.features), p.label))
                .filter(pl => pl._1 == pl._2)
                .count()
        ) /testData.count()

      accuracy
    }
  }

}