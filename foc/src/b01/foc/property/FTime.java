// Instance
// Static

/*
 * Created on 15 fvr. 2004
 */
package b01.foc.property;

import b01.foc.calendar.FCalendar;
import b01.foc.db.DBManager;
import b01.foc.desc.*;
import b01.foc.*;
import java.sql.Time;
import java.text.*;
import java.util.Calendar;

/**
 * @author 01Barmaja
 */
public class FTime extends FProperty {
  private java.sql.Time time;
  private java.sql.Time backupTime = null;
  private static SimpleDateFormat timeFormat = null;
  private static SimpleDateFormat sqlTimeFormat = null;

  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // Instance
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  
  public FTime(FocObject focObj, int fieldID, java.sql.Time time) {
    super(focObj, fieldID);
    /*if (timeFormat == null) {
      timeFormat = new SimpleDateFormat("HH:mm");
    }
    if (sqlTimeFormat == null) {
      sqlTimeFormat = new SimpleDateFormat("dd/mon/yy HH:mm:ss");
    }*/
    this.time = new java.sql.Time(0);
    if(time != null){
      setTime(time); 
    }
  }
  
  public static String convertTimeToSQLString(java.util.Date date){
    String str = null;
    if (sqlTimeFormat == null) {
      if (Globals.getDBManager().getProvider() == DBManager.PROVIDER_ORACLE){
        str = "01-JAN-70 ";
        sqlTimeFormat = new SimpleDateFormat("dd-MMM-yy H:m:ss");
      }else{
        str = "1970-01-01 ";
        sqlTimeFormat = new SimpleDateFormat("yyyy-MM-dd H:m:ss");
      }
    }
    str = date != null ? sqlTimeFormat.format(date) : sqlTimeFormat.format(new Time(0));
    return str;
  }
  
  private static SimpleDateFormat getTimeFormat(){
    if (timeFormat == null) {
      timeFormat = new SimpleDateFormat("HH:mm");
    }
    return timeFormat;
  }

  public static String convertTimeToDisplayString(java.util.Date date){   
    return date != null ? getTimeFormat().format(date) : getTimeFormat().format(new Time(0));
  }

  public static java.util.Date convertStringToTime(String str){   
  	java.util.Date date = null;
  	
  	try{
	    if(str != null && str.trim().compareTo("") != 0){
	    	date = getTimeFormat().parse(str);
	    }else{
	    	date = new Time(0);
	    }
  	}catch(Exception e){
  		Globals.logException(e);
  	}
    
    return date;
  }

  public boolean checkTime(){
    /*
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    return calendar.get(Calendar.YEAR) > 1970;
    */
    return !isEmpty();
  }
  
  public static boolean isEmpty(java.sql.Time time){
    return time.getTime() == 0;
  }
  
  public boolean isEmpty(){
    //System.out.println("time = "+date.getTime());
    //return date.getTime() < Globals.DAY_TIME;
    return FTime.isEmpty(time);
  }
  
  public java.sql.Time getTime() {
    return (java.sql.Time) time.clone();
  }

  public void setTime(java.sql.Time time) {
    if(time != null && this.time != null && !time.toString().equals(this.time.toString()) && time.getTime() != this.time.getTime()){
      this.time.setTime(time.getTime());
      notifyListeners();
    }
  }

  public String getString() {
    String str = "";
    if(checkTime()){
      str = convertTimeToDisplayString(time);
    }
    return str;
  }

  public String getSqlString() {
      //rr Begin
      if (Globals.getDBManager().getProvider() == DBManager.PROVIDER_ORACLE){
        /*String sysdate ="";
        try{
          String select = "SELECT TO_CHAR(SYSDATE,'DD-MON-YYYY') FROM DUAL";
          Statement stmt = Globals.getDBManager().lockStatement();
          ResultSet resultSet = stmt.executeQuery(select);
          if (resultSet.next()){
            sysdate = resultSet.getString(1);
          }
          Globals.getDBManager().unlockStatement(stmt);
        }catch(Exception e){
          Globals.logException(e);
        }*/
        return "TO_DATE (" + "\"" + convertTimeToSQLString(time) + "\"" + " , "+ "\"DD-MON-YY HH24:MI:SS\")";
      }else{
        //rr End
        return "\"" + time.toString() + "\"";
      }
  }

  public void setSqlStringInternal(String str) {
    try {
      if (str != null && !str.equals("00:00:00")){
      	if(Globals.getDBManager().getProvider() == DBManager.PROVIDER_ORACLE){
      		time = java.sql.Time.valueOf(str.substring(11, (str.length() -2))); //rr
      	}else{
      		time = java.sql.Time.valueOf(str);
      	}
      }
    } catch (Exception e) {
    	Globals.logString(str);
      Globals.logException(e);
    }
  }

  public void setString(String str) {
    try {
      if (str != null) {
        if(str.trim().compareTo("") == 0){
          setTime(new java.sql.Time(0));
        }else{
        	java.util.Date utilDate =convertStringToTime(str);
          long timeInLong = utilDate.getTime();
          java.sql.Time newTime = new java.sql.Time(timeInLong);
          if (utilDate != null) setTime(newTime);
        }
      }
    } catch (Exception e) {
      Globals.logException(e);
    }
  }

  public Object getTableDisplayObject(Format format) {
    /*
    String str = "";
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    if(calendar.get(Calendar.YEAR) > 1970){
      str = getString();
    }
    */
    return getString();
  }
  
  public void setTableDisplayObject(Object obj, Format format) {
    setObject(obj);
  }
  
  public Object getObject(){
    return getString();
  }
  
  public void setObject(Object obj){
    setString((String) obj);
  }
  
  public int compareTo(FProperty prop) {
    java.sql.Time otherTime = null;
    int diff = 0;
    
    if(prop != null){
      otherTime = ((FTime)prop).getTime();
      diff = compareTo(otherTime);
    }
    
    return diff;
  }
  
  public int compareTo(java.sql.Time otherTime) {
    int diff = 1;

    if(otherTime != null){
	    Calendar cal1 = Calendar.getInstance();
	    Calendar cal2 = Calendar.getInstance();
	    cal1.setTime(time);
	    cal2.setTime(otherTime);
	
	    diff = cal1.compareTo(cal2);
    }
    
    return diff;
  }

  public void backup() {
    backupTime = time;
  }
  
  public void restore() {
    time = backupTime != null ? backupTime : time;
  }
}
