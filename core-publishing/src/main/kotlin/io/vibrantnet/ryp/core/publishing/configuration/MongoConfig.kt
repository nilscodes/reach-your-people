package io.vibrantnet.ryp.core.publishing.configuration

import com.mongodb.reactivestreams.client.MongoClient
import com.mongodb.reactivestreams.client.MongoClients
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories

@Configuration
class MongoConfig : AbstractReactiveMongoConfiguration() {
    override fun getDatabaseName(): String {
        return "ryp"
    }

    override fun reactiveMongoClient(): MongoClient {
        return MongoClients.create()
    }
}