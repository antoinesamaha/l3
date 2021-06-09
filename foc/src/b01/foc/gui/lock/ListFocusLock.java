/*
 * Created on Sep 11, 2005
 */
package b01.foc.gui.lock;

import b01.foc.list.*;
import b01.foc.desc.*;
import b01.foc.gui.table.FTable;

/**
 * @author 01Barmaja
 */
public class ListFocusLock extends FocusLock {
  FTable table = null;
  FocList list = null; 
  FocObject obj = null;
  
  public ListFocusLock(FTable table, FocList list, FocObject obj){
    this.table = table;
    this.list = list;
    this.obj = obj;
  }
  
  public void dispose(){
    table = null;
    list = null; 
    obj = null;
  }
  
  public Object getLockObject(){
    return obj;
  }
  
  public boolean shouldHoldFocus(boolean displayMessage){
    //We start by testing if editing because this case is the case of combo boxes in the new added row
    boolean holdFocus = !table.isEditing() && !obj.isContentValid(displayMessage);
    int row = list.getRowForObject(obj);
    //b01.foc.Globals.logString("obj row ="+row+" curr row ="+table.getSelectedRow()+ " hold="+holdFocus);
    if(table.getSelectedRow() != row && row >= 0){
      //holdFocus = !obj.isContentValid(true);
      if(holdFocus){
        table.setRowSelectionInterval(row, row);//ici
      }
    }
    return holdFocus;
  }

  /* (non-Javadoc)
   * @see b01.foc.gui.lock.FocusLock#stopEditing()
   */
  public void stopEditing() {
    if(table.isEditing()){
      table.getCellEditor().stopCellEditing();
    }
  }
}
