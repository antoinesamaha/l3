/*
 * Created on Jul 8, 2005
 */
package b01.foc.list;

import b01.foc.event.*;
import b01.foc.desc.*;

import java.util.*;

/**
 * @author 01Barmaja
 */
public class ListOperationComputer implements FocListener{
  private FocList list = null;
  private ArrayList operationArray = null;  
    
  public ListOperationComputer(FocList list){
    this.list = list;
    operationArray = new ArrayList();
  }

  public void dispose() {   
    if(operationArray != null){
      for(int i=0; i<operationArray.size(); i++){
        ListOperation oper = (ListOperation) operationArray.get(i);
        oper.dispose();        
      }
      operationArray.clear();
      operationArray = null;
    }
    list = null;
  }
  
  public void addOperation(ListOperation operation){
    operationArray.add(operation);
  }
  
  public void compute(){
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
  
  public void focActionPerformed(FocEvent evt) {
    compute();
  }
  
  public FocList getList() {
    return list;
  } 
}
