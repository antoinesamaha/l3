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

import java.util.*;
import java.text.*;

/**
 * @author 01Barmaja
 */
public class FDate extends FProperty {
  protected java.sql.Date date;
  protected java.sql.Date backupDate = null;
  private static SimpleDateFormat dateFormat = null;
  private static SimpleDateFormat sqlDateFormat = null;

  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // Instance
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  
  public FDate(FocObject focObj, int fieldID, java.sql.Date date) {
    super(focObj, fieldID);
    this.date = new java.sql.Date(0);
    getDateFormat();
    if(date != null){
      setDate(date); 
    }
  }

  public static SimpleDateFormat getDateFormat(){
    if (dateFormat == null) {
      dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    }
    return dateFormat;
  }

  public static String convertDateToSQLString(java.util.Date date){
    if (sqlDateFormat == null) {
      if (Globals.getDBManager().getProvider() == DBManager.PROVIDER_ORACLE){
        sqlDateFormat = new SimpleDateFormat("dd-MMM-yy");
      }else{
        sqlDateFormat = new SimpleDateFormat("yyyy-MM-dd");
      }
    }
    return date != null ? sqlDateFormat.format(date) : sqlDateFormat.format(new Date(0));
  }

  public static String convertDateToDisplayString(java.util.Date date){   
    /*if (dateFormat == null) {
      dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    }*/
    return date != null ? dateFormat.format(date) : dateFormat.format(new Date(0));
  }
  
  public boolean checkDate(){
    /*
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    return calendar.get(Calendar.YEAR) > 1970;
    */
    return !isEmpty();
  }
  
  public static boolean isEmpty(Date date){
	  return date.getTime() < Globals.DAY_TIME && date.getTime() > -Globals.DAY_TIME;
  }
  
  public boolean isEmpty(){
    //System.out.println("time = "+date.getTime());
    //return date.getTime() < Globals.DAY_TIME;
    return FDate.isEmpty(date);
  }
  
  public java.sql.Date getDate() {
    return (java.sql.Date) date.clone();
  }

  public void setDate(java.sql.Date date) {
    
    boolean equalAtMidnight = FCalendar.getTimeAtMidnight(this.date) == FCalendar.getTimeAtMidnight(date);
    
    if(date != null && date.compareTo(this.date) != 0 && !equalAtMidnight){
      this.date.setTime(date.getTime());
      notifyListeners();
    }
  }

  public String getString() {
    String str = "";
    if(checkDate()){
      str = convertDateToDisplayString(date);
    }
    return str;
  }

  public String getSqlString() {
    //rr Begin
    if (Globals.getDBManager().getProvider() == DBManager.PROVIDER_ORACLE){
      return "TO_DATE (" + "\"" + convertDateToSQLString(date) + "\"" + " , "+ "\"DD-MON-YY\")";
    }else{
    //rr End
      return "\"" + convertDateToSQLString(date) + "\"";
    }
  }

  public void setSqlStringInternal(String str) {
    try {
      if (str != null && !str.equals("0000-00-00") && str != ""){
        date = java.sql.Date.valueOf(str.substring(0, 10));
      }
    } catch (Exception e) {
      Globals.logException(e);
    }
  }

  public void setString(String str) {
    try {
      if (str != null) {
        if(str.trim().compareTo("") == 0){
          setDate(new java.sql.Date(0));
        }else{
          java.util.Date utilDate = dateFormat.parse(str);
          if (utilDate != null) setDate(new java.sql.Date(utilDate.getTime()));
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
    Date otherDate = null;
    int diff = 0;
    
    if(prop != null){
      otherDate = ((FDate)prop).getDate();      
      if(otherDate != null){
        int nbDays = (int) (date.getTime() / FCalendar.MILLISECONDS_IN_DAY);
        int otherNbDays = (int) (otherDate.getTime() / FCalendar.MILLISECONDS_IN_DAY);
        diff = nbDays - otherNbDays; 
      }
    }
    
    return diff;
  }
  
  public void backup() {
    if(backupDate == null){
      backupDate = new java.sql.Date(date.getTime());
    }else{
      backupDate.setTime(date.getTime());
    }
  }
  
  public void restore() {
    setDate(backupDate != null ? backupDate : date);
  }  
}
