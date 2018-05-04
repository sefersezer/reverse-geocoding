package com.paritus.findnearestroad.dbtool;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;


public class MysqlInstance {
	
	
	private Connection connection;	
	private ResultSet resultSet;
	private String query;
	static final Logger logger = Logger.getLogger(MysqlInstance.class);
	
	public MysqlInstance(String query){
		try {
			String dbUrl = "jdbc:mysql://localhost/caddesokak";
			String dbClass = "com.mysql.jdbc.Driver";
			Statement statement;
			String username = "root";
			String password = "123123";
			
			Class.forName(dbClass);
			connection = DriverManager.getConnection(dbUrl, username, password);
			statement = connection.createStatement();
			this.query=query;
			resultSet=statement.executeQuery(query);
			
		} catch (ClassNotFoundException e) {
			logger.error(e);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			logger.error(e);
		}
	}

	public ResultSet getResultSet() {
		return resultSet;
	}

	
	
}
