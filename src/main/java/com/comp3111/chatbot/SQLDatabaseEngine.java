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
		String result = null;
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			connection = getConnection();
			stmt = connection.prepareStatement("SELECT * FROM facilities WHERE index=?");
			int n = Integer.parseInt(text);
			stmt.setInt(1, n);
			rs = stmt.executeQuery();
			rs.next();
			result = "The opening hour of " + rs.getString(2) + " is:\n" + rs.getString(4);
		}catch(URISyntaxException e1){
			log.info("URISyntaxException: ", e1.toString());
		}catch(SQLException e2) {
			log.info("SQLException: ", e2.toString());
		} finally {
			try {
				rs.close();
				stmt.close();
				connection.close();
			}
			catch (Exception e) {
				log.info("Exception while disconnection: {}", e.toString());
			}
		}

		if(result!=null)
			return result;
		throw new Exception("NOT FOUND");
	}
	 
	 String showFacilitiesChoices() throws Exception {
		String result = null;
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			connection = getConnection();
			stmt = connection.prepareStatement("SELECT * FROM facilities");
			rs = stmt.executeQuery();

			while(rs.next())
			{
				if(result == null)
					result = rs.getInt(1) + ". " + rs.getString(2) + "\n";
				else
					result += rs.getInt(1) + ". " + rs.getString(2) + "\n";
				}
		} catch(URISyntaxException e1) {
			log.info("URISyntaxException: ", e1.toString());
		} catch(SQLException e2) {
			log.info("SQLException: ", e2.toString());
		} finally {
			try {
				rs.close();
				stmt.close();
				connection.close();
			}
			catch (Exception e) {
				log.info("Exception while disconnection: {}", e.toString());
			}
		}

		if(result!=null)
			return result;
		else
			return "NOT FOUND";
	}
	
	
<<<<<<< HEAD

	public void storeAction(String id, String text, String action) throws Exception{
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
=======
	public void storeAction(String id, String text, Action act) throws Exception{
		Connection connection = this.getConnection();
		PreparedStatement stmt = connection.prepareStatement("INSERT INTO mainflow VALUES('"+ id + "'," + "'"+text +"', "+ "'"+act.name() +"' );" );
		ResultSet rs = stmt.executeQuery();
>>>>>>> parent of 99a42a6... add check case for userid in storeAction()
		
		try {		
			connection = this.getConnection();
			//check if the userid aready exit, if yes, update it, else inset a new entry
			stmt = connection.prepareStatement("SELECT count(*) FROM mainflow WHERE userid=" + "id;");
			rs = stmt.executeQuery();
			rs.next();
			if(rs.getInt(1)==0)
				stmt = connection.prepareStatement("INSERT INTO mainflow VALUES('"+ id + "'," + "'"+text +"', "+ "'"+action.name() +"' );" );
			else
				stmt = connection.prepareStatement("UPDATE mainflow SET userinput=" + "'"+text +"',"+ "action=" + "'"+action.name() +"'" + " where userid="+id+";" );
			
			rs = stmt.executeQuery();
			connection.close();
		} catch (Exception e) {
			log.info("Exception while storing: {}", e.toString());
		} finally {
			try {		
				rs.close();
				stmt.close();
				connection.close();	
			} catch (Exception e) {
				log.info("Exception while storing: {}", e.toString());
			}
		}
	}		

	
	
	public String[] nextAction(String id) throws Exception{
		String[] next= new String [2];
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			connection = this.getConnection();
			log.info("userid: {}", id);
			stmt = connection.prepareStatement("SELECT * FROM mainflow where userid = '" + id + "'");
			
			rs = stmt.executeQuery();
			String sCurrentLine;

			// Stupid way to loop til latest match record
			if(rs.next()) {
				do {
					sCurrentLine = rs.getString(1) + ":" + rs.getString(2) +":"+ rs.getString(3) ;
					String[] parts = sCurrentLine.split(":");
					
					if (id.equals(parts[0])) {
						next[0] = parts[1];
						next[1] = parts[2];
					}
				} while (rs.next());
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
