/*
 * Created on May 7, 2006
 */
package b01.l3.astm;

import java.util.Date;

/**
 * @author 01Barmaja
 */
public class AstmRecord {
  private Object[] fieldValues = null;
  
  public AstmRecord(int fieldArrayLength){
    fieldValues = new Object[fieldArrayLength];
  }
  
  public String getString(int field){
    return (String)fieldValues[field];
  }
  
  public void setString(int field, String value){
    fieldValues[field] = value;
  }  
  
  public Date getDate(int field){
    return (Date)fieldValues[field];
  }
  
  public void setDate(int field, Date value){
    fieldValues[field] = value;
  }
  
  public Long getLong(int field){
    return (Long) fieldValues[field];
  }
  
  public void setLong(int field, long value){
    Long l = Long.valueOf(value);
    fieldValues[field] = l ;
  }
  
  public Double getDouble(int field){
    return (Double)fieldValues[field];
  }
  
  public void setDouble(int field, double value){
    fieldValues[field] = Double.valueOf(value);
  }    
  
  public Character getCharacter(int field){
    return (Character)fieldValues[field];
  }
  
  public void setCharacter(int field, char value){
    fieldValues[field] = Character.valueOf(value);
  }
}
