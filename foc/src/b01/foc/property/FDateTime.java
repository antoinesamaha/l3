// Instance
// Static

/*
 * Created on 15 fvr. 2004
 */
package b01.foc.property;

import b01.foc.db.DBManager;
import b01.foc.desc.*;
import b01.foc.desc.field.FDateTimeField;
import b01.foc.*;

import java.util.*;
import java.text.*;

/**
 * @author 01Barmaja
 */
public class FDateTime extends FDate {

  private static SimpleDateFormat dateTimeFormat    = null;
  private static SimpleDateFormat sqlDateTimeFormat = null;
  private boolean timeRelevant = false;
  
  public FDateTime(FocObject focObj, int fieldID, java.sql.Date date) {
    super(focObj, fieldID, date);
    getDateTimeFormat();
    if (sqlDateTimeFormat == null) {
      if (Globals.getDBManager()!= null && Globals.getDBManager().getProvider() == DBManager.PROVIDER_ORACLE){
        sqlDateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      }else{
        sqlDateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
      }
    }
    FDateTimeField field = (FDateTimeField) getFocField();
    if(field != null){
    	timeRelevant = field.isTimeRelevant();
    }
  }

  public boolean isTimeRelevant(){
  	return timeRelevant;
  }
  
  public void setTimeRelevant(boolean timeRelevant){
  	this.timeRelevant = timeRelevant;
  }

  public SimpleDateFormat getThisFormat(){
  	if(isTimeRelevant()){
  		return getDateTimeFormat();
  	}else{
  		return FDate.getDateFormat();
  	}
  }
  
  public static SimpleDateFormat getDateTimeFormat(){
    if(dateTimeFormat == null) dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
  	return dateTimeFormat;
  }
  
  public static String convertDateToSQLString(java.util.Date date){
    return date != null ? sqlDateTimeFormat.format(date) : sqlDateTimeFormat.format(new Date(0));
  }

  public static String convertDateToDisplayString(java.util.Date date){   
    return date != null ? dateTimeFormat.format(date) : dateTimeFormat.format(new Date(0));
  }
  
  public boolean checkDate(){
    return !isEmpty();
  }
  
  public void setDate(java.sql.Date date) {
    if(date.compareTo(this.date) != 0){
      this.date.setTime(date.getTime());
      notifyListeners();
    }
  }

  public String getString() {
    String str = "";
    if(checkDate()){
    	Date d = date != null ? date : new Date(0);
    	str = getThisFormat().format(d);
    }
    return str;
  }

  public String getSqlString() {
    //rr Begin
    if (Globals.getDBManager().getProvider() == DBManager.PROVIDER_ORACLE){
      return "TO_DATE (" + "\"" + convertDateToSQLString(date) + "\"" + " , "+ "\"YYYY-MM-DD HH24:MI:SS\")";
    }else{
    //rr End
      return "\"" + convertDateToSQLString(date) + "\"";
    }
  }

  public void setSqlStringInternal(String str) {
    try {
      if (str != null && !str.equals("0000-00-00 00:00:00") && str != ""){
        date = java.sql.Date.valueOf(str.substring(0, 10));
        int hours = Integer.valueOf(str.substring(11, 13));
        int mins = Integer.valueOf(str.substring(14, 16));
        date.setTime(date.getTime()+(hours*60+mins)*60*1000);
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
          java.util.Date utilDate = null;
          try{
          	utilDate = dateTimeFormat.parse(str);
          }catch(ParseException e){
          	utilDate = FDate.getDateFormat().parse(str);
          }
          if (utilDate != null) setDate(new java.sql.Date(utilDate.getTime()));
        } 
      }
    } catch (Exception e) {
      Globals.logException(e);
    }
  }
    
  public int compareTo(FProperty prop) {
    Date otherDate = null;
    int diff = 0;
    
    if(prop != null){
      otherDate = ((FDateTime)prop).getDate();      
      if(otherDate != null){
        diff = date.compareTo(otherDate);
      }
    }
    
    return diff;
  }
}
