package com.example.biketripapp

import aws.sdk.kotlin.runtime.auth.credentials.StaticCredentialsProvider
import aws.sdk.kotlin.services.dynamodb.DynamoDbClient
import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import aws.sdk.kotlin.services.dynamodb.model.PutItemRequest
import aws.sdk.kotlin.services.dynamodb.model.QueryRequest
import kotlinx.coroutines.runBlocking


class AWSInstance {

    fun sendEntryWrapper(trackGpx: String) {
        return runBlocking {
            sendEntry(trackGpx)
        }
    }

    private suspend fun sendEntry(trackGpx: String) {
        // Create the DynamoDB client
        val dynamoDbClient = DynamoDbClient {
            region = "eu-north-1"
            credentialsProvider = StaticCredentialsProvider {
                accessKeyId = ""
                secretAccessKey = ""
            }
        }

        // val latestTrackId = getLatestTrackId(dynamoDbClient)
        // val newTrackId = (latestTrackId.toInt() + 1).toString().padStart(4, '0')

        // Define the item to be inserted
        val itemValues = mutableMapOf<String, AttributeValue>()
        itemValues["trackId"] = AttributeValue.S("0002")
        itemValues["gpx_track"] = AttributeValue.S(trackGpx)

        // Create a PutItem request
        val request =
            PutItemRequest {
                tableName = "bike-trip-app"
                item = itemValues
            }

        // Use the DynamoDB client to put the item
        dynamoDbClient.use { ddb ->
            ddb.putItem(request)
            println("A new item was placed into the table.")
        }

    }

    private suspend fun getLatestTrackId(dynamoDbClient: DynamoDbClient): String {
        val queryRequest = QueryRequest {
            tableName = "bike-trip-app"
            keyConditionExpression = "trackId > :val"
            expressionAttributeValues = mapOf(":val" to AttributeValue.S("0"))
            scanIndexForward = false
            limit = 1
        }

        val response = dynamoDbClient.query(queryRequest)

        return if (response.items?.isNotEmpty() == true) {
            response.items!![0]["trackId"].toString()
        } else {
            "0000"
        }

    }

}