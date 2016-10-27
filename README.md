# activityRecognitionV2
Remake of activity recognition project in Scala.

There are two main part:
* creation of a prediction model based on a Random Forest algorithm
* Predict the activity in a realtime using the generated prediction model

This project contains an already generated prediction model so you can try it directly.

## Prerequisites
* Cassandra 2.2.2
* Java 8
* Scala 2.11.8

## Start the application
First of all, get the project `https://github.com/MiraLak/accelerometer-rest-to-cassandra` and follow the steps to start it. 
This project will allows you to collect data and exposes it in a Rest API that will be used to recognize the activity.
Then, you can start the app by launching the main class `fr.duchess.service.PredictionService` for the prediction.

## Remember!
Make sure that you use the same network and don't hesitate to update the configurations of cassandra of spark if needed.

