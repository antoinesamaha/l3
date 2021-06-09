/*
 * Created on Jul 8, 2005
 */
package b01.foc.list;

import b01.foc.event.*;
import b01.foc.desc.field.*;

/**
 * @author 01Barmaja
 */
public class ListOrderListener implements FocListener{
  private FocList list = null;
    
  public ListOrderListener(FocList list){
    this.list = list;
  }

  public void dispose(){
    list = null;
  }
  
  public void compute(){
    list.sort();
  }
  
  public void focActionPerformed(FocEvent evt) {
    compute();
  }
  
  public FocListListener newFocListListener(){
    FocListListener listListener = null; 
    if(list != null){
      FocListOrder listOrder = (FocListOrder) list.getListOrder();
      if(listOrder != null){
        listListener = new FocListListener(list);
        for(int i=0; i<listOrder.getFieldsNumber(); i++){
          FFieldPath fieldPath = listOrder.getFieldAt(i);
          listListener.addProperty(fieldPath);
        }
        listListener.addListener(this);
        listListener.startListening();
      }
    }
    return listListener;
  }
}
