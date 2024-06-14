package de.torui.coflsky.commands;

import com.google.gson.annotations.SerializedName;

public class RawCommand {
	@SerializedName("type")
	private String Type;
	
	@SerializedName("data")
	private String Data;
	
	public RawCommand(String type, String data) {
		this.Type = type;
		this.Data=data;
	}

	public String getType() {
		return Type;
	}

	public void setType(String type) {
		Type = type;
	}

	public String getData() {
		return Data;
	}

	public void setData(String data) {
		Data = data;
	}
	
}

//This Java class RawCommand is designed to represent a simple data structure for storing and manipulating raw command data. Here's a breakdown of its functionality:

//Class Overview
//RawCommand Class
//Imports:

//com.google.gson.annotations.SerializedName: Annotation to specify the JSON attribute name for fields during serialization and deserialization.
//Fields:

//Type: Represents the type of the raw command.
//Data: Represents the actual data associated with the command.
//Constructor:

//RawCommand(String type, String data): Initializes a RawCommand object with the given type and data.
//Methods:

//Getters and Setters:
//getType(): Returns the type of the raw command.
//setType(String type): Sets the type of the raw command.
//getData(): Returns the data associated with the raw command.
//setData(String data): Sets the data associated with the raw command.
//Purpose and Usage
//Data Representation:

//RawCommand is used to encapsulate raw command information.
//Type typically represents the type or category of the command (e.g., "command", "query", "action").
//Data holds the specific details or payload of the command in string format.
//Serialization:

//The @SerializedName annotation on fields (Type and Data) allows mapping between Java object properties and JSON attributes during serialization and deserialization using Gson library.
//This facilitates easy conversion of RawCommand instances to JSON format for transmission or storage.
//Flexibility:

//By providing getters and setters for Type and Data, the class enables manipulation and retrieval of command details in a controlled manner.
//Users of this class can create instances with specific command types and associated data, ensuring consistency and clarity in command processing.

//Summary
//The RawCommand class provides a straightforward mechanism for representing and manipulating raw command data in a structured manner. It leverages Gson annotations for seamless JSON serialization and deserialization, making it suitable for applications where commands need to be transmitted, stored, or processed in JSON format.
