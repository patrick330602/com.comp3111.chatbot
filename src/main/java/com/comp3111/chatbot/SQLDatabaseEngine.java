package com.comp3111.chatbot;

import lombok.extern.slf4j.Slf4j;
import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.*;
import java.net.URISyntaxException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;

@Slf4j
public class SQLDatabaseEngine {
	 String openingHourSearch(String text) throws Exception {
		//Write your code here
		String result = null;
		try {
			Connection connection = getConnection();
			PreparedStatement stmt = connection.prepareStatement(
					"SELECT * FROM facilities WHERE index=?");
			int n = Integer.parseInt(text);
			stmt.setInt(1, n);
			ResultSet rs = stmt.executeQuery();
			rs.next();
			result = "The opening hour of " + rs.getString(2) + "is:" + rs.getString(4);
			
			rs.close();
			stmt.close();
			connection.close();
		}catch(URISyntaxException e1){
			log.info("URISyntaxException: ", e1.toString());
		}catch(SQLException e2) {
			log.info("SQLException: ", e2.toString());
		}

		if(result!=null)
			return result;
		throw new Exception("NOT FOUND");
	}
	 
	 String showFacilitiesChoices() throws Exception {
		//Write your code here
		String result = null;
		try {
			Connection connection = getConnection();
			PreparedStatement stmt = connection.prepareStatement(
					"SELECT * FROM facilities");
			ResultSet rs = stmt.executeQuery();
			while(rs.next())
			{
				if(result == null)
					result = rs.getInt(1) + ". " + rs.getString(2) + "\n";
				else
					result += rs.getInt(1) + ". " + rs.getString(2) + "\n";
			}
			
			rs.close();
			stmt.close();
			connection.close();
		}catch(URISyntaxException e1){
			log.info("URISyntaxException: ", e1.toString());
		}catch(SQLException e2) {
			log.info("SQLException: ", e2.toString());
		}

		if(result!=null)
			return result;
		else
			return "NOT FOUND";
	}
	
	
	public void storeAction(String id, String text, Action act) throws Exception{
		Connection connection = this.getConnection();
		//check if the userid aready exit, if yes, update it, else inset a new entry
		PreparedStatement stmt = connection.prepareStatement("SELECT count(*) FROM mainflow WHERE userid=" + "id;");
		ResultSet rs = stmt.executeQuery();
		rs.next();
		if(rs.getInt(1)==0)
			stmt = connection.prepareStatement("INSERT INTO mainflow VALUES('"+ id + "'," + "'"+text +"', "+ "'"+act.name() +"' );" );
		else
			stmt = connection.prepareStatement("UPDATE mainflow SET userinput=" + "'"+text +"',"+ "action=" + "'"+act.name() +"'" + " where userid="+id+";" );

		ResultSet rs = stmt.executeQuery();
		
		try {		

			
			}
			catch (Exception e) {
				log.info("Exception while storing: {}", e.toString());
			}
		
		finally {
		try {		
			rs.close();
			stmt.close();
			connection.close();
			
			}
			catch (Exception e) {
				log.info("Exception while storing: {}", e.toString());
			}
		}
		}		

	
	
	public String[] nextAction(String id) throws Exception{
		String[] next= new String [2];
		
		Connection connection = this.getConnection();
		PreparedStatement stmt = connection.prepareStatement("SELECT * FROM mainflow");
		ResultSet rs = stmt.executeQuery();
		
		try {
			
			String sCurrentLine;
			while (rs.next()) {

				sCurrentLine = rs.getString(1) + ":" + rs.getString(2) +":"+ rs.getString(3) ;
				String[] parts = sCurrentLine.split(":");
				
				if (id.equals(parts[0])) {
					next[0] = parts[1];
					next[1] = Action.valueOf(parts[2]).name();
				}
				
			}
		}
		catch (Exception e) {
			log.info("Exception while connection: {}", e.toString());
		}
		finally {
			try {
				rs.close();
				stmt.close();
				connection.close();
			}
			catch (Exception e) {
				log.info("Exception while disconnection: {}", e.toString());
			}
		}				
		
		return next;
	}
	
	
	private Connection getConnection() throws URISyntaxException, SQLException {
		Connection connection;
		URI dbUri = new URI(System.getenv("DATABASE_URL"));

		String username = dbUri.getUserInfo().split(":")[0];
		String password = dbUri.getUserInfo().split(":")[1];
		String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath() +  "?ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory";

		log.info("Username: {} Password: {}", username, password);
		log.info ("dbUrl: {}", dbUrl);
		
		connection = DriverManager.getConnection(dbUrl, username, password);

		return connection;
	}
}
