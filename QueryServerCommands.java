package de.torui.coflsky.network;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import de.torui.coflsky.CoflSky;
import de.torui.coflsky.minecraft_integration.CoflSessionManager;
import de.torui.coflsky.minecraft_integration.PlayerDataProvider;

public class QueryServerCommands {
	
	private static Gson gson = new GsonBuilder().create();
	
	public static String QueryCommands() {
		
		String queryResult = GetRequest(CoflSky.CommandUri);
		
		if(queryResult != null) {
			CommandInfo[] commands = gson.fromJson(queryResult, CommandInfo[].class);
			
			System.out.println(">>> "+Arrays.toString(commands));
			
			StringBuilder sb = new StringBuilder();
			
			if(commands.length>0) {
				for(CommandInfo cm : commands) {
					sb.append(cm + "\n");
				}
			}
			return sb.toString().trim();
			
		}
		
		return "§4ERROR: Could not connect to command server!";
	}
	
	private static class CommandInfo {
		
		public String subCommand;
		public String description;
		
		public CommandInfo() {}
		
		public CommandInfo(String subCommand, String description) {
			super();
			this.subCommand = subCommand;
			this.description = description;
		}

		@Override
		public String toString() {
			return subCommand + ": " + description;
		}
		
		
		
	}
	private static String GetRequest(String uri) {
		
		try {
			System.out.println("Get request");
			URL url = new URL(uri);
	    	HttpURLConnection con;
			con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			
			//con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
			con.setRequestProperty("Accept", "application/json");
			con.setRequestProperty("User-Agent", "CoflMod");
			//con.setDoInput(true);
			con.setDoInput(true);

			// ...

			/*OutputStream os = con.getOutputStream();
			byte[] bytes = ("[\"" + getUsername() + "\"]").getBytes("UTF-8");
			os.write(bytes);
			os.close();
			*/
			System.out.println("InputStream");
			 InputStream in = new BufferedInputStream(con.getInputStream());
			 ByteArrayOutputStream result = new ByteArrayOutputStream();
			 byte[] buffer = new byte[1024];
			 for (int length; (length = in.read(buffer)) != -1; ) {
			     result.write(buffer, 0, length);
			 }
			 // StandardCharsets.UTF_8.name() > JDK 7
			 String resString =  result.toString("UTF-8");
			 
			 System.out.println("Result= " + resString);
			 return resString;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	public static String PostRequest(String uri,  String data) {
		try {
			String username = PlayerDataProvider.getUsername();
			URL url = new URL(uri);
			HttpURLConnection con;
			con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("POST");

			con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
			con.setRequestProperty("Accept", "application/json");
			con.setRequestProperty("User-Agent", "CoflMod");
			con.setRequestProperty("conId", CoflSessionManager.GetCoflSession(username).SessionUUID);
			con.setRequestProperty("uuid",username);
			con.setDoInput(true);
			con.setDoOutput(true);
			// ...

			OutputStream os = con.getOutputStream();
			byte[] bytes = data.getBytes("UTF-8");
			os.write(bytes);
			os.close();

			InputStream in = new BufferedInputStream(con.getInputStream());
			ByteArrayOutputStream result = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			for (int length; (length = in.read(buffer)) != -1; ) {
				result.write(buffer, 0, length);
			}
			// StandardCharsets.UTF_8.name() > JDK 7
			String resString =  result.toString("UTF-8");

			return resString;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}
}

//The provided code is a Java class QueryServerCommands that belongs to the package de.torui.coflsky.network. This class is designed to handle network communication with a remote server, specifically for querying and sending commands. Here's a detailed breakdown of its functionality:

//Class Overview
//Imports:
//java.io.*, java.net.HttpURLConnection, java.net.URL: For handling network communication and input/output operations.
//com.google.gson.Gson, com.google.gson.GsonBuilder: For parsing JSON data.
//de.torui.coflsky.CoflSky, de.torui.coflsky.minecraft_integration.CoflSessionManager, de.torui.coflsky.minecraft_integration.PlayerDataProvider: Custom classes likely part of the CoflSky project, dealing with session management and player data in a Minecraft integration context.
//Methods
//QueryCommands()
//Purpose: Sends a GET request to retrieve available commands from the server and formats the response.
//Process:
//Calls GetRequest(CoflSky.CommandUri) to send a GET request to the specified URI.
//Parses the JSON response into an array of CommandInfo objects.
//Constructs a formatted string of commands.
//Returns the formatted string or an error message if the request fails.
//CommandInfo (Nested Static Class)
//Fields:
//subCommand: The sub-command string.
//description: Description of the sub-command.
//Constructor: Initializes the fields.
//toString() Method: Provides a string representation of the command information in the format "subCommand: description".
//GetRequest(String uri)
//Purpose: Sends a GET request to the specified URI and returns the response as a string.
//Process:
//Opens a connection to the given URL.
//Sets request properties (e.g., Accept, User-Agent).
//Reads the input stream from the connection.
//Converts the input stream to a string and returns it.
//Returns null if an exception occurs.
//PostRequest(String uri, String data)
//Purpose: Sends a POST request with the given data to the specified URI and returns the response as a string.
//Process:
//Retrieves the username using PlayerDataProvider.getUsername().
//Opens a connection to the given URL.
//Sets request properties (e.g., Content-Type, Accept, User-Agent, conId, uuid).
//Writes the data to the output stream of the connection.
//Reads the input stream from the connection.
//Converts the input stream to a string and returns it.
//Returns null if an exception occurs.
//Usage and Workflow
//QueryCommands(): Used to fetch and display commands available from the server.
//GetRequest(): Helper method to perform GET requests.
//PostRequest(): Helper method to perform POST requests with specific data, such as user session information.
//Example Workflow
//QueryCommands(): This method will be called to fetch commands from the server:

//Sends a GET request to CoflSky.CommandUri.
//Parses the JSON response into CommandInfo objects.
//Formats and returns the commands.
//PostRequest(): This method can be used to send data (e.g., player actions, session updates) to the server:

//Sends a POST request with the provided data to the specified URI.
//Reads and returns the response from the server.
//Error Handling
//Both GetRequest and PostRequest methods handle exceptions by printing stack traces and returning null in case of an error. This ensures the program does not crash and can provide feedback on connectivity issues.
//This class is a utility for interacting with a server, particularly in a gaming or Minecraft modding context, allowing for dynamic command retrieval and data posting.
