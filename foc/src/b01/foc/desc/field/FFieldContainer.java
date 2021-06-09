/*
 * Created on Jun 27, 2005
 */
package b01.foc.desc.field;

import java.util.*;

/**
 * @author 01Barmaja
 */
public class FFieldContainer {
  private ArrayList list = null;
  private HashMap<Integer, FField> map = null;
  
  public FFieldContainer(){
    list = new ArrayList();
    map = new HashMap<Integer, FField>();
  }
  
  public void dispose(){
    if( list != null ){
      list.clear();
      list = null;
    }
    
    if( map != null ){
      map.clear();
      map = null;
    }
  }
  
  public void add(FField field){
    list.add(field);
    map.put(field.getID(), field);
  }
  
  public void remove(FField field){
    list.remove(field);
    map.remove(field.getID());
  }
  
  public int size(){
    return list.size();
  }

  public FField get(int i){
    return (list != null) ? (FField) list.get(i) : null;
  }

  public FField getByID(int id){
    return map.get(id);
  }

  public FField getByName(String name){
    FField found = null;
    for(int i=0; i<size(); i++){
      FField field = get(i);
      if(name.compareTo(field.getName()) == 0){
        found = field;
        break;
      }
    }
    return found;
  }  
}
