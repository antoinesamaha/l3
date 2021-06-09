/*
 * Created on Oct 14, 2004
 */
package b01.foc.list;

import b01.foc.desc.field.*;
import b01.foc.property.*;
import b01.foc.desc.*;
import b01.foc.event.*;

import java.util.*;

/**
 * @author 01Barmaja
 */
public class FocListListener {
  private FocList list = null;
  private InternalListListener listListener = null;
  private InternalPropertyListener propertyListener = null;  
  private ArrayList propertiesToListenTo = null;
  private HashMap mapObjectListeners = null;
  private ArrayList listeners = null;

  //-----------------------------------------------------------
  //-----------------------------------------------------------
  //-----------------------------------------------------------
  //Operation computing
  //-----------------------------------------------------------
  //-----------------------------------------------------------
  //-----------------------------------------------------------
  private ArrayList operationArray = null;

  private ArrayList getOperationArray(boolean create){
    if(operationArray == null && create){
      operationArray = new ArrayList();
    }
    return operationArray;
  }
  
  public void addOperation(ListOperation operation){
    ArrayList operationArray = getOperationArray(true);
    operationArray.add(operation);
  }

  public void computeOperations(){
    ArrayList operationArray = getOperationArray(false);
    
    if(operationArray != null){
      for(int i=0; i<operationArray.size(); i++){
        ListOperation sumCol = (ListOperation) operationArray.get(i);
        sumCol.reset();
      }    
      
      Iterator iter = list.focObjectIterator();
      while(iter != null && iter.hasNext()){
        FocObject obj = (FocObject) iter.next();
        if(obj != null){
          for(int i=0; i<operationArray.size(); i++){
            ListOperation sumCol = (ListOperation) operationArray.get(i);
            sumCol.treatObject(obj);
          }    
        }
      }
  
      for(int i=0; i<operationArray.size(); i++){
        ListOperation sumCol = (ListOperation) operationArray.get(i);
        sumCol.sendResult();
      }
    }
  }
  
  private void disposeOperationArray(){
    if(operationArray != null){
      for(int i=0; i<operationArray.size(); i++){
        ListOperation sumCol = (ListOperation) operationArray.get(i);
        sumCol.dispose();
      }
    }
  }
  
  //-----------------------------------------------------------
  //-----------------------------------------------------------
  //-----------------------------------------------------------
  
  /**
   * @param list
   * 
   * List listener that automatically plugs listeners on each property of each object the FocList.
   *  
   * through the addProperty() method we can add properties to listen to.
   * 
   */
  public FocListListener(FocList list){
    this.list = list;
    propertiesToListenTo = new ArrayList();
    listListener = new InternalListListener(this);
    propertyListener = new InternalPropertyListener(this);
    mapObjectListeners = new HashMap();
    listeners = new ArrayList();
  }
  
/*  public Object clone() throws CloneNotSupportedException {
    Object obj = null;
    obj = super.clone();
    FocListListener focListListener = (FocListListener)obj;
    ArrayList clonnedPropertiesToListenTo = new ArrayList();
    for( int i = 0; i < focListListener.propertiesToListenTo.size(); i++){
      FFieldPath fieldPath = (FFieldPath)focListListener.propertiesToListenTo.get(i);
      if( fieldPath != null ){
        clonnedPropertiesToListenTo.add(fieldPath.clone());  
      }
    }
    focListListener.propertiesToListenTo = clonnedPropertiesToListenTo;
    
    return obj;
  }*/
  
  
  public void dispose(){
    removeAllObjectListeners();
 
    if(mapObjectListeners != null){
      Iterator iter = mapObjectListeners.values().iterator();
      while(iter != null && iter.hasNext()){
        ArrayList listeners = (ArrayList) iter.next();
        if(listeners != null){
          for(int i=listeners.size()-1; i>=0; i--){
            FSmartPropertyListener propListener = (FSmartPropertyListener)listeners.get(i);
            if(propListener != null){
              propListener.dispose();
              propListener = null;
            }
          }      
        }
      }
      mapObjectListeners.clear();      
      mapObjectListeners = null;
    }
    
    if(listListener != null){
      listListener.dispose();
      listListener = null;
    }
 
    if(propertyListener != null){
      propertyListener.dispose();
      propertyListener = null;
    }

    if(propertiesToListenTo != null){
      for(int i=0; i<propertiesToListenTo.size(); i++){
        FFieldPath path = (FFieldPath) propertiesToListenTo.get(i);
        if (path != null){
          path.dispose();
        }
      }
      propertiesToListenTo.clear();
      propertiesToListenTo = null;
    }
     
    if(listeners != null){
      for(int i=0; i<listeners.size(); i++){
        FocListener listener = (FocListener) listeners.get(i);
        listener.dispose();
      }
      listeners.clear();
      listeners = null;
    }
    
    disposeOperationArray();
    operationArray = null;
    
    list = null;
  }
  
  public void addProperty(FFieldPath fieldPath){
    propertiesToListenTo.add(fieldPath);
  }
  
  public FocList getFocList() {
    return list;
  }
  
  public void setFocList(FocList list) {
    this.list = list;
  }

  private void removeObjectListeners(FocObject obj){
    ArrayList listeners = (ArrayList) mapObjectListeners.get(obj);
    if(listeners != null){
      for(int i=listeners.size()-1; i>=0; i--){
        FSmartPropertyListener propListener = (FSmartPropertyListener)listeners.get(i);
        if(propListener != null){
          propListener.removeListener(propertyListener);
          propListener.dispose();
        }
      }      
    }
    mapObjectListeners.remove(obj);
  }

  private void addObjectListeners(FocObject obj){
    ArrayList listeners = (ArrayList) mapObjectListeners.get(obj);
    if(listeners == null){
      listeners = new ArrayList();
      for(int i=0; i<propertiesToListenTo.size(); i++){
        FFieldPath fieldPath = (FFieldPath)propertiesToListenTo.get(i);
        FSmartPropertyListener propListener = new FSmartPropertyListener(obj, fieldPath);
        propListener.addListener(propertyListener);
        listeners.add(propListener);
      }
      
      mapObjectListeners.put(obj, listeners);
    }     
  }
  
  public void startListening(){
    //BElias
    /*if(list != null){
      Iterator iter = list.focObjectIterator();
      
      while(iter != null && iter.hasNext()){
        FocObject obj = (FocObject) iter.next();
        addObjectListeners(obj);
      }      
    }*/
    //EElias
    list.iterate(new FocListIterator(0){
      @Override
      public boolean treatElement(FocListElement element, FocObject focObj) {
        addObjectListeners(focObj);
        return false;
      }
      
    });
  }

  public void removeAllObjectListeners(){
    if(list != null){
      Iterator iter = list.focObjectIterator();
      
      while(iter != null && iter.hasNext()){
        FocObject obj = (FocObject) iter.next();
        removeObjectListeners(obj);
      }
    }
  }

  public void addListener(FocListener listener){
    if(listener != null){
      listeners.add(listener);
    }
  }

  public void removeListener(FocListener listener){
    if(listener != null){
      listeners.remove(listener);
    }
  }

  public void notifyListeners(){
    if(!list.isSleepListeners()){
      for(int i=0; i<listeners.size(); i++){
        FocListener focListener = (FocListener)listeners.get(i);
        if(focListener != null){
          focListener.focActionPerformed(null);
        }
      }
      computeOperations();
    }
  }
  
  public class InternalListListener implements FocListener{
    FocListListener listListener = null;
    
    public InternalListListener(FocListListener listListener){
      this.listListener = listListener;
      //if( listListener.getFocList() != null ){
        listListener.getFocList().addFocListener(this);  
      //}
      
    }
    
    public void dispose(){
      if(listListener != null){
        FocList focList = listListener.getFocList();
        if(focList != null){
          focList.removeFocListener(this);
        }
      }
      listListener = null;      
    }
    
    public void focActionPerformed(FocEvent evt) {
      FocObject obj = (FocObject) evt.getEventSubject();
      switch(evt.getID()){
        case FocEvent.ID_ITEM_ADD:
          listListener.addObjectListeners(obj);
          listListener.notifyListeners();
          break;
        case FocEvent.ID_ITEM_REMOVE:
          listListener.removeObjectListeners(obj);
          listListener.notifyListeners();
          break;
        case FocEvent.ID_WAIK_UP_LIST_LISTENERS:
          listListener.notifyListeners();
          break;
      }
    }
  }
  
  public class InternalPropertyListener implements FPropertyListener{
    FocListListener listListener = null;
    
    public InternalPropertyListener(FocListListener listListener){
      this.listListener = listListener;
    }
    
    public void dispose(){
      listListener = null;
    }
    
    public void propertyModified(FProperty prop) {
      if(listListener != null){
        listListener.notifyListeners();
      }
    }
  }
}
