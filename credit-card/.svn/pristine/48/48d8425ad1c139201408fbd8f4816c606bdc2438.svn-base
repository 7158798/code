package com.pay.aile.bill.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

@Configuration
@ConditionalOnProperty(prefix = "environment", value = "dev")
public class MongoDBDevConfig extends AbstractMongoConfiguration {
	// @Value("${mongo.host}")
	// private String dbhost;
	@Value("${mongo.database}")
	private String dbname;
	// @Value("${mongo.port}")
	// private String dbport;
	// @Value("${mongo.password}")
	// private String password;
	// @Value("${mongo.username}")
	// private String username;

	@Value("${mongodb.uri}")
	private String mongodbUri;

	@Override
	public Mongo mongo() throws Exception {
		MongoClientURI connectionString = new MongoClientURI(mongodbUri);
		Mongo mongo = new MongoClient(connectionString);
		return mongo;
	}

	@Override
	protected String getDatabaseName() {
		// TODO Auto-generated method stub
		return null;
	}

}
