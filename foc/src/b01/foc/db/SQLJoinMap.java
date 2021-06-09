package b01.foc.db;

import java.util.HashMap;
import java.util.Iterator;

public class SQLJoinMap {
  
  private HashMap<String, SQLJoin> joinMap = null;
  private int aliasCount = 1;
  private static String MAIN_TABLE_ALIAS = "M";
  
  public SQLJoinMap(){
    
  }
  
  public SQLJoin addJoin (SQLJoin join){
    if(joinMap == null){
      joinMap = new HashMap<String, SQLJoin>();
    }
    SQLJoin newJoin = joinMap.get(join.getKey());
    if(newJoin == null){
      newJoin = join;
      joinMap.put(newJoin.getKey(), newJoin);      
      newJoin.setNewAlias("T"+aliasCount++);
    }
    return newJoin;
  }
  
  public int size(){
    return joinMap != null ? joinMap.size() :0;  
  }
  
  public void clearJoinMap(){
    if(joinMap != null){
      joinMap.clear();
    }
  }
  
  public String getMainTableAlias(){
    return MAIN_TABLE_ALIAS;
  }
  
  public Iterator getJoinMapIterator(){
  return joinMap.values().iterator();
  }
}
