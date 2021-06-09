/*
 * Created on 25-Apr-2005
 */
package b01.foc.db;

import b01.foc.Globals;
import b01.foc.desc.*;
import java.util.*;

/**
 * @author 01Barmaja
 */
public class DBIndex {
  private String name = null;
  private FocDesc focDesc = null;
  private boolean unique = false;
  private ArrayList<Integer> fieldList = null;
  
  public DBIndex(String name, FocDesc focDesc, boolean unique, boolean modifyIndexName){
    this.name = name;
    //rr Begin
    if (modifyIndexName && Globals.getDBManager() != null && Globals.getDBManager().getProvider() == DBManager.PROVIDER_ORACLE){
      this.name = name+"_"+focDesc.getStorageName();
    }
    //rr End
    
    this.focDesc = focDesc;
    this.unique = unique;
    fieldList = new ArrayList<Integer>();
  }

  public DBIndex(String name, FocDesc focDesc, boolean unique){
    this(name, focDesc, unique, true);
  }

  public int compareTo(DBIndex other){
    int compare = name.compareTo(other.getName());
    if(compare == 0){
      int u1= unique ? 1 : 0;
      int u2= other.unique ? 1 : 0;
      compare = u1 - u2;
    }
    if(compare == 0){
      compare = fieldList.size() - other.fieldList.size();
    }
    if(compare == 0){
      for(int i=0; i<fieldList.size() && compare == 0; i++){
        int fieldId = ((Integer)fieldList.get(i)).intValue();
        int otherfieldId = ((Integer)other.fieldList.get(i)).intValue();
        compare = fieldId - otherfieldId;  
      }
    }
    return compare;
  }
  
  public void addField(int fieldID){
    fieldList.add(Integer.valueOf(fieldID));
  }
  
  /**
   * @return Returns the name.
   */
  public String getName() {
    return name;
  }
  
  /**
   * @return Returns the focDesc.
   */
  public FocDesc getFocDesc() {
    return focDesc;
  }
  
  /**
   * @return Returns the unique.
   */
  public boolean isUnique() {
    return unique;
  }
  
  public int getFieldCount(){
    return fieldList.size();
  }
  
  public int getFieldAt(int i){
    return ((Integer)fieldList.get(i)).intValue();
  }
}
