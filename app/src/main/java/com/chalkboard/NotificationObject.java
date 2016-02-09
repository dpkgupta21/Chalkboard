package com.chalkboard;

public class NotificationObject{
	private String Type;
	private String Message;
	
	
	private String heading1;
	private String heading2;
	private String heading3;
	private String image;
	private String timestamp;
	
	
	public String getHeading1() {
		return heading1;
	}
	public void setHeading1(String heading1) {
		this.heading1 = heading1;
	}
	public String getHeading2() {
		return heading2;
	}
	public void setHeading2(String heading2) {
		this.heading2 = heading2;
	}
	public String getHeading3() {
		return heading3;
	}
	public void setHeading3(String heading3) {
		this.heading3 = heading3;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public String getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	public String getType() {
		return Type;
	}
	public void setType(String type) {
		Type = type;
	}
	public String getMessage() {
		return Message;
	}
	public void setMessage(String message) {
		Message = message;
	}
	
}