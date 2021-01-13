package application;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class timetable {
	private String date;
	private String day;
	private String start="";
	private String end="";
	private BooleanProperty apply=new SimpleBooleanProperty();
	
	private long time;
	private long endTime;
	private String fullDate;
	private String fullEndDate;
	SimpleDateFormat dateFormat = new SimpleDateFormat("dd/M/yyyy HH:mm:ss");
	public timetable(String date, String day) {
		this.date=date;
		this.day=day;
	}
	public timetable(String date, String day,String start, String end) {
		this.date=date;
		this.day=day;
		this.start=start;
		this.end=end;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getDay() {
		return day;
	}
	public void setDay(String day) {
		this.day = day;
	}
	public String getStart() {
		return start;
	}
	public void setStart(String start) {
		
		this.start = start;
	}
	public String getEnd() {
		return end;
	}
	public void setEnd(String end) {
		this.end = end;
	}
	public BooleanProperty isApply() {
		return apply;
	}
	public void setApply(BooleanProperty apply) {
		this.apply = apply;
	}
	public final BooleanProperty changedProperty() {
	    return this.apply;
	}
	public long getTime() {
		
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
	public String getFullDate() {
		return fullDate;
	}
	public void setFullDate(String fullDate) {
		try {
		    
		    java.util.Date parsedDate = dateFormat.parse(fullDate);
		    Timestamp timestamp = new java.sql.Timestamp(parsedDate.getTime());
		    setTime(timestamp.getTime());
		} catch(Exception e) { //this generic but you can control another types of exception
		    // look the origin of excption 
		}
		this.fullDate = fullDate;
	}
	public String getFullEndDate() {
		return fullEndDate;
	}
	public void setFullEndDate(String fullEndDate) {
			try {
		    java.util.Date parsedDate = dateFormat.parse(fullEndDate);
		    Timestamp timestamp = new java.sql.Timestamp(parsedDate.getTime());
		    setEndTime(timestamp.getTime());
		} catch(Exception e) { //this generic but you can control another types of exception
		    // look the origin of excption 
		}
		this.fullEndDate = fullEndDate;
	}
	public long getEndTime() {
		return endTime;
	}
	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}
}
