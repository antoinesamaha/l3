/*
 * Created on Aug 30, 2005
 */
package b01.foc.date;

import b01.foc.db.SQLSelectDistinct;
import b01.foc.desc.*;
import b01.foc.db.*;
import b01.foc.property.*;

import java.sql.Date;
import java.util.*;

/**
 * @author 01Barmaja
 */
public class DateMap {
  private HashMap dateMap = null;
  
  public DateMap(){
    dateMap = new HashMap();
  }
  
  public void put(Date date){
    put(date, null);
  }
  
  public void put(Date date, Object obj){
    DateItem item = new DateItem(date, obj);
    dateMap.put(date, item);
  }

  public Object get(Date date){
    DateItem item = (DateItem) dateMap.get(date);
    return item != null ? item.getObject() : null;
  }
  
  public Set keySet(){
    return dateMap.keySet();
  }
  
  public Collection values() {
    return dateMap.values();
  }
  
  public Iterator iterator(){
    return new DateIterator(this);    
  }
  
  public void fillFromTableColumn(FocDesc focDesc, int fieldID){
    SQLSelectDistinct select = new SQLSelectDistinct(focDesc, fieldID, (SQLFilter) null);
    select.execute();
    for(int l=0; l<select.getLineNumber(); l++){
      FDate dateProp = (FDate) select.getPropertyAt(l, 0);
      put(dateProp.getDate());
    }
  }
}
