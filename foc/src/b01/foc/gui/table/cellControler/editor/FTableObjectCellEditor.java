/*
 * Created on 15 fvr. 2004
 */
package b01.foc.gui.table.cellControler.editor;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;
import javax.swing.table.*;

import b01.foc.*;
import b01.foc.gui.*;
import b01.foc.gui.table.FAbstractTableModel;
import b01.foc.gui.table.FTable;
import b01.foc.gui.table.FTableModel;
import b01.foc.gui.table.cellControler.AbstractCellControler;
import b01.foc.list.*;
import b01.foc.property.*;

/**
 * @author 01Barmaja
 */
public class FTableObjectCellEditor extends AbstractCellEditor implements TableCellEditor, FPropertyListener {

  private AbstractCellControler fatherCellControler = null; 
  private FPanel selPanel = null;
  private FObject selectionProperty = null;
  private JComboBox dummy = new JComboBox();

  public FTableObjectCellEditor(AbstractCellControler fatherCellControler) {
    this.fatherCellControler = fatherCellControler;
  }

  public void dispose(){
    fatherCellControler = null;
    if(selPanel != null){
      selPanel.dispose();
      selPanel = null;
    }
    selectionProperty = null;
    dummy = null;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see javax.swing.table.TableCellEditor#getTableCellEditorComponent(javax.swing.JTable,
   *      java.lang.Object, boolean, int, int)
   */
  public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
    Component comp = null;
    
    if(!isSelected){
      stopCellEditing();
      return null;
    }
    FTable t = (FTable)table;
    if(t.requestToEditCell()){
      comp = fatherCellControler != null ? fatherCellControler.getRenderer().getTableCellRendererComponent(table, value, isSelected, true, row, column) : dummy;
    }
    return comp;
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.swing.CellEditor#getCellEditorValue()
   */
  public Object getCellEditorValue() {
    Object obj = null; 
    if(selectionProperty != null){
      obj = selectionProperty.getTableDisplayObject(null);
    }else{
      obj = "";
    }
    return obj;
    /*
     * FocObject obj = (FocObject) selectionProperty.getObject(); return obj;
     */
  }

  private void goBackIfNeeded() {
    if (selPanel == Globals.getDisplayManager().getCurrentPanel()) {
      Globals.getDisplayManager().goBack();
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.swing.CellEditor#cancelCellEditing()
   */
  public void cancelCellEditing() {
    goBackIfNeeded();    
    super.cancelCellEditing();    
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.swing.CellEditor#shouldSelectCell(java.util.EventObject)
   */
  public boolean shouldSelectCell(EventObject anEvent) {
    boolean ret = true;
    try {
      Object source = anEvent.getSource();

      if (anEvent.getClass() == MouseEvent.class) {
        Object sourceObj = anEvent.getSource();
        if (sourceObj.getClass() == FTable.class) {
          FTable table = (FTable) sourceObj;
          MouseEvent mouseEvent = (MouseEvent) anEvent;
          Point point = mouseEvent.getPoint();
          
          startCellEditing(table, table.rowAtPoint(point), table.columnAtPoint(point));
        }
      }
    } catch (Exception e) {
      Globals.logException(e);
    }

    return ret;
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.swing.CellEditor#stopCellEditing()
   */
  public boolean stopCellEditing() {
    if(selectionProperty != null){
      selectionProperty.removeListener(this);
      
      FocList selectionList = selectionProperty.getPropertySourceList();
      if(selectionList != null){
        selectionList.setSelectionProperty(null);
      }
    }
    goBackIfNeeded();
    return super.stopCellEditing();
  }

  /*
   * (non-Javadoc)
   * 
   * @see b01.foc.property.FPropertyListener#propertyModified(b01.foc.property.FProperty)
   */
  public void propertyModified(FProperty property) {
    stopCellEditing();
  }
  
  public void startCellEditing(FTable table, int row, int col) {
    try {
      //FTableModel model = (FTableModel) table.getModel();
    	FAbstractTableModel model = (FAbstractTableModel)table.getModel();
      int modelCol = model.getTableView().getVisibleColumnIndex(col);
      selectionProperty = (FObject) model.getFProperty(row, modelCol);
      selectionProperty.addListener(this);
      FocList selectionList = selectionProperty.getPropertySourceList();
      selectionList.setSelectionProperty(selectionProperty);
      selectionList.setListRequestingTheSelection(model.getFocList());
      selPanel = selectionList.getSelectionPanel(false);
      Globals.getDisplayManager().getCurrentPanel().setCurrentDefaultFocusComponent(table);
      Globals.getDisplayManager().popupDialog(selPanel, "Select", true);
      
    } catch (Exception e) {
      Globals.logException(e);
    }
  }
  
  public boolean isCellEditable(EventObject anEvent) {
    if (anEvent instanceof MouseEvent) { 
      return ((MouseEvent)anEvent).getClickCount() >= 2;
    }
    return true;
  }
}
