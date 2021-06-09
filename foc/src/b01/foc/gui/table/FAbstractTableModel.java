/*
 * Created on 16 fvr. 2004
 */
package b01.foc.gui.table;

import b01.foc.desc.FocObject;
import b01.foc.desc.field.FField;
import b01.foc.desc.field.FFieldPath;
import b01.foc.desc.field.FIntField;
import b01.foc.dragNDrop.FocTransferable;
import b01.foc.event.FocEvent;
import b01.foc.event.FocListener;
import b01.foc.list.FocList;
import b01.foc.list.FocListListener;
import b01.foc.list.filter.FocListFilter;
import b01.foc.property.*;
import java.awt.*;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.*;
 
/**
 * @author 01Barmaja
 */
public abstract class FAbstractTableModel extends AbstractTableModel implements Cloneable {

//public abstract int getRowCount();
	public abstract FocObject getRowFocObject(int i);
	public abstract void      afterTableConstruction(FTable table);
	public abstract Color     getDefaultBackgroundColor(Color bg, Component comp, JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column);
	public abstract FocList   getFocList();
	
  protected FTableView tableView = null;
  private FIntField lineNumberField = null; 
  private FInt lineNumberProperty = null;
  
  public FAbstractTableModel(){
		super();
		tableView = new FTableView();
	}

  public void dispose(){
    if(tableView != null){
      tableView.dispose();
      tableView = null;
    }
    if(lineNumberProperty != null){
      lineNumberProperty.dispose();
      lineNumberProperty = null;
    }
    if(lineNumberField != null){
      lineNumberField.dispose();
      lineNumberField = null;
    }
    disposeTableCellModificationListener();
  }
  
  public Object clone() throws CloneNotSupportedException {
    Object obj = super.clone();
    FAbstractTableModel abstractTableModel = (FAbstractTableModel)obj;
    FTableView tableView = abstractTableModel.getTableView();
    tableView = (FTableView)tableView.clone();
    abstractTableModel.setTableView(tableView);
    //abstractTableModel.tableCellModificationListener = (FocListListener)abstractTableModel.tableCellModificationListener.clone();
  
    return obj;
  }
  
	public void setRowHeight(int rowHeight) {
	}
  
  public void setTableView(FTableView tableView) {
    this.tableView = tableView;
  }
  
  public FTableView getTableView() {
    return tableView;
  }
  
  public FocListFilter getFilter(){
    FocListFilter listFilter = null;
    FTableView tableView = getTableView();
    if(tableView != null){
      listFilter = tableView.getFilter();
    }
    return listFilter;
  }
  
  protected FInt getLineNumberProperty(int row){
    if(lineNumberField == null){
      lineNumberField = new FIntField(FField.LINE_NUMBER_FIELD_LBL, "Line number", FField.LINE_NUMBER_FIELD_ID, false, 6);
    }
    if(lineNumberProperty == null){
      lineNumberProperty = new FInt(null, FField.LINE_NUMBER_FIELD_ID, -1);
      lineNumberProperty.setFocField(lineNumberField);
      lineNumberProperty.setBackground(Color.ORANGE);
    }
    lineNumberProperty.setInteger(row+1);
    return lineNumberProperty;
  }

  public FProperty getSpecialFProperty(FTableColumn tc, FocObject rowObject, int row, int col){
  	FProperty objectProperty = null;
    if (tc.getID() == FField.LINE_NUMBER_FIELD_ID){
      objectProperty = getLineNumberProperty(row);     
    }
    return objectProperty; 
  }
  
  public FProperty getFProperty(int row, int col) {
    FProperty objectProperty = null;

    if (tableView != null) {
      FocObject rowObject = getRowFocObject(row);
      if (rowObject != null) {
        FTableColumn tc = (FTableColumn) tableView.getColumnAt(col);
        //FTableColumn tc = (FTableColumn) tableView.getVisibleColumnAt(col);
        if (tc != null) {	
        	objectProperty = getSpecialFProperty(tc, rowObject, row, col);
        	if(objectProperty == null){
	          // In this case we should go through the field path
	          // Until we get to the property
	          FocObject curObj = rowObject;
	          FFieldPath fieldPath = tc.getFieldPath();
	          if(fieldPath != null){
	          	objectProperty = fieldPath.getPropertyFromObject(rowObject);
	          }
        	}
        }
      }
    }
    return objectProperty;
  }
	  
  // AbstractTableModel
  // ------------------
  public int getColumnCount() {
    //System.out.println("get colomn count");
    return (tableView != null) ? tableView.getColumnCount() : 0;
  }

  public String getColumnName(int column) {
    String name = "";
    if (tableView != null) {
      FTableColumn tableColumn = tableView.getColumnAt(column);
      if (tableColumn != null) {
        name = tableColumn.getTitle();
      }
    }
    return name;
  }

  @SuppressWarnings("unchecked")
	public Class getColumnClass(int c) {
  	Object o = getValueAt(0, c);
    return o == null ? Object.class : o.getClass();
  }
	
  public boolean isCellEditable(int row, int col) {
    boolean editable = false;
    if (tableView != null) {
      FTableColumn tableColumn = tableView.getColumnAt(col);
      if (tableColumn != null) {
        editable = tableColumn.getEditable();
        if(editable){
          FProperty prop = getFProperty(row, col);
          if(prop != null){
            editable = !prop.isValueLocked();
          }
        }
      }
    }
    return editable;
  }
  
  public Color getCellColor(int row, int col) {
    Color color = null;
    FProperty prop = getFProperty(row, col);    
    if(prop != null){    
      color = prop.getBackground();
    }
    return color;
  }
	
  public Object getValueAt(int row, int col) {
    Object obj = null;
    FProperty prop = getFProperty(row, col);
    FTableColumn tc = (FTableColumn) tableView.getColumnAt(col);
    if(prop == null){
    	//Globals.logString("Prop == null for row = "+row+" Col = "+col);      
      prop = getFProperty(row, col);
    }
    if (prop != null) obj = prop.getTableDisplayObject(tc.getFormat());
    return obj;
  }

  //BElie
  public void setValueAt(Object aValue, int row, int column) {
    FProperty prop = getFProperty(row, column);
    FTableColumn tc = (FTableColumn) tableView.getColumnAt(column);

    if (prop != null && tc != null) {
      prop.setTableDisplayObject(aValue, tc.getFormat());
    }
  }
  
  private FocListListener tableCellModificationListener = null;
  
  public void disposeTableCellModificationListener(){
    if(tableCellModificationListener != null){
      tableCellModificationListener.dispose();
      tableCellModificationListener = null;
    }
  }
  
  public void plugListListenerToCellPropertiesMoifications(){
    FTableView view = getTableView();
    if (view != null) {
      FocListener focListener = new FocListener(){
        public void focActionPerformed(FocEvent evt) {
          fireTableRowsUpdated(0, getRowCount());
          SwingUtilities.invokeLater(new Runnable(){
            public void run() {
              if( tableView != null && tableView.getTable() != null ){
                if( tableView.getColumnResizingMode() == FTableView.COLUMN_AUTO_RESIZE_MODE){
                  AutofitTableColumns.autoResizeTable(tableView.getTable(), true, true);    
                }
              }
            }
          });
        }

        public void dispose() {
          // TODO Auto-generated method stub
          
        }
      };
      
      
      disposeTableCellModificationListener();
      tableCellModificationListener = new FocListListener(getFocList());
      
      tableCellModificationListener.addListener(focListener);
      
      for (int i = 0; i < view.getColumnCount(); i++) {
        FTableColumn fCol = view.getColumnAt(i);
        if (fCol != null) {
          tableCellModificationListener.addProperty(fCol.getFieldPath());
        }
      }
      
      
      tableCellModificationListener.startListening();
    }
  }
  //EElie
  
  // --------------------------
  // Drag implementation
  // --------------------------
  public void fillSpecificDragInfo(FocTransferable focTransferable){
  	if(focTransferable != null){
	  	int selectedRow = focTransferable.getTableSourceRow();
	  	FocObject sourceFocObject = getRowFocObject(selectedRow);
	  	focTransferable.setSourceFocObject(sourceFocObject);
  	}
  }
}