/*
 * Created on 15 fvr. 2004
 */
package b01.foc.gui.table;

import java.util.*;

import javax.swing.JTable;
import javax.swing.table.TableColumn;
import b01.foc.*;
import b01.foc.desc.*;
import b01.foc.desc.field.*;
import b01.foc.gui.FPanel;
import b01.foc.gui.table.cellControler.AbstractCellControler;
import b01.foc.gui.table.cellControler.DrawingCellControler;
import b01.foc.gui.table.cellControler.GanttChartCellControler;
import b01.foc.gui.table.cellControler.GanttChartActivityCellControler;
import b01.foc.gui.table.cellControler.TreeCellControler;
import b01.foc.gui.table.cellControler.renderer.drawingCellRenderer.FDrawingScale;
import b01.foc.gui.table.cellControler.renderer.gantChartActivityRenderer.FGanttChartActivityColumnHeaderRenderer;
import b01.foc.gui.table.cellControler.renderer.gantChartRenderer.BasicGanttScale;
import b01.foc.gui.treeTable.FGTreeInTable;
import b01.foc.list.filter.FocListFilter;

/**
 * @author 01Barmaja
 */
public class FTableView implements Cloneable {
  private ArrayList<Integer> visibleColumnsIndexes = null;
  private ArrayList<FTableColumn> columns = null;
  private FTable table = null;
  private double columnWidthFactor = 1;
  private int lineNumber = 10;
  private FocListFilter filter = null;
  private int detailPanelViewID = FocObject.DEFAULT_VIEW_ID;
  private int detailPanelViewIDForNewItem = -1;
  private boolean editAfterInsertion = false;
  private String viewKey = null;
  
  private int columnResizingMode = COLUMN_WIDTH_FACTOR_MODE;
  public static final int COLUMN_WIDTH_FACTOR_MODE = 1;
  public static final int COLUMN_AUTO_RESIZE_MODE  = 2;
  private static final String GANTT_COLUMN_TITLE   = "GANTT";
  private static final String DRAWING_COLUMN_TITLE = "DRAWING";
  
  private boolean owner = true;
  private int columnsToFreeze = 0;
  private FColumnGroupHeaderConstructor columnGroupHeaderConstructor = null;
  
  public FTableView() {
    columns = new ArrayList<FTableColumn>();
    if(ConfigInfo.isShowStatusColumn()){
      addStatusColumn();
    }
    columnGroupHeaderConstructor = new FColumnGroupHeaderConstructor(this);
  }
  
  private void setColumns(ArrayList<FTableColumn> columns) {
    this.columns = columns;
  }
  
  public Object clone(){
    Object obj = null;
    try {
      obj = super.clone();
      FTableView ftableView = (FTableView)obj;
      
      ArrayList<FTableColumn> clonedColumns = new ArrayList<FTableColumn>();
      for( int i = 0; i < ftableView.getColumnCount(); i++ ){
        clonedColumns.add((FTableColumn)ftableView.getColumnAt(i).clone());
      }
      ftableView.setColumns(clonedColumns);
    
      ftableView.columnGroupHeaderConstructor = (FColumnGroupHeaderConstructor)ftableView.columnGroupHeaderConstructor.clone();
      ftableView.columnGroupHeaderConstructor.setTableView(ftableView);
      ftableView.columnGroupHeaderConstructor.cloneColumnGroupFatherArray();  
      
    } catch (CloneNotSupportedException e) {
      e.printStackTrace();
    }
    return obj;
  }
  
  public void dispose(){
    if(columns != null){
      for (int i = 0; i < getColumnCount(); i++) {
        FTableColumn tableCol = getColumnAt(i);
        if (tableCol != null) {
          tableCol.dispose();
        }
      }
      columns.clear();
      columns = null;
    }
    
    if(visibleColumnsIndexes != null){
      visibleColumnsIndexes.clear();
      visibleColumnsIndexes = null;
    }
    
    if(table != null){
      table.dispose();
      table = null;
    }
    
    if(filter != null && owner){
      filter.dispose();
      filter = null;
    }
    
    if( columnGroupHeaderConstructor != null ){
      columnGroupHeaderConstructor.dispose();
      columnGroupHeaderConstructor = null;
    }
  }

  public int getDetailPanelViewID(){
  	return detailPanelViewID;
  }

  public void setDetailPanelViewID(int detailPanelViewID){
  	this.detailPanelViewID = detailPanelViewID;
  }
  
  public FTableColumn addColumn(FTableColumn tableCol) {
    columns.add(tableCol);
    return tableCol;
  }

  public FTableColumn insertColumnBefore(FTableColumn tableCol, int index) {
  	columns.add(tableCol);
  	for(int i=columns.size()-1; i>index; i--){
  		columns.set(i, columns.get(i-1));
  	}
    columns.set(index, tableCol);
    return tableCol;
  }

  public FTableColumn addColumn(FocDesc focDesc, int id, FFieldPath fieldPath, String title, int size, boolean editable) {
    FTableColumn column = new FTableColumn(focDesc, fieldPath, id, title, size, editable);
    addColumn(column);
    return column;
  }

  public FTableColumn addColumn(FocDesc focDesc, int id, FField focField) {
    FTableColumn tableCol = addColumn(focDesc, id, FFieldPath.newFieldPath(focField.getID()), focField.getTitle(), focField.getSize(), false);
    return tableCol;
  }

  public FTableColumn addColumn(FocDesc focDesc, FField focField) {
    FTableColumn tableCol = addColumn(focDesc, focField.getID(), FFieldPath.newFieldPath(focField.getID()), focField.getTitle(), focField.getSize(), false);
    return tableCol;
  }
  
  public FTableColumn addColumn(FField field,int id, String title, int size, boolean editable) {
    FTableColumn column = new FTableColumn(field, id, title, size, editable);
    addColumn(column);
    return column;
  }
  
  public FTableColumn addColumn(FocDesc focDesc, int id, String title, int size, boolean editable) {
  	FTableColumn column = new FTableColumn(focDesc, id, title, size, editable);
    addColumn(column);
    return column;
  }  	

  public FTableColumn addColumn(FocDesc focDesc, int id, int size, boolean editable) {
  	FTableColumn column = new FTableColumn(focDesc, id, null, size, editable);
    addColumn(column);
    return column;
  }
  
  
  public FTableColumn addColumn(FocDesc focDesc, int id, String title, boolean editable) {
  	FTableColumn column = new FTableColumn(focDesc, id, title, 0, editable);
    addColumn(column);
    return column;
  }  	

  public FTableColumn addColumn(FocDesc focDesc, int id, boolean editable) {
  	FTableColumn column = new FTableColumn(focDesc, id, null, 0, editable);
    addColumn(column);
    return column;
  }  	

  public FTableColumn addSelectionColumn(){
    return addColumn(null/*theFocDesc*/, FField.SELECTION_FIELD_ID, FFieldPath.newFieldPath(FField.SELECTION_FIELD_ID), "", 3, true);
  }

  public void addStatusColumn(){
    addColumn(null/*theFocDesc*/, FField.STATUS_FIELD_ID, null, "", 3, false);
  }

  public FTableColumn addLineNumberColumn(){
    FTableColumn column = new FTableColumn(FField.getLineNumberField(),FField.LINE_NUMBER_FIELD_ID,"",5,false);
    insertColumnBefore(column, 0);
    return column;
  }

  public void addTreeColumn(FGTreeInTable tree){
  	FTableColumn fCol = addColumn(null/*theFocDesc*/, FField.TREE_FIELD_ID, null, "", 30, false);
  	fCol.setShowConfigurable(false);
  	fCol.setCellEditor(new TreeCellControler(table));
  }
  
  public FTableColumn addGanttChartColumn(BasicGanttScale gantScale){
    return addGanttChartColumn(new GanttChartCellControler(gantScale));
  }

  public FTableColumn addGanttChartActivityColumn(BasicGanttScale gantScale){
    return addGanttChartActivityColumn(gantScale, false);
  }
  
  public FTableColumn addGanttChartActivityColumn(BasicGanttScale gantScale, boolean showExpandCollapseHeaderButton){
    GanttChartActivityCellControler ganttChartActivityCellControler = new GanttChartActivityCellControler(gantScale);
    FGanttChartActivityColumnHeaderRenderer columnHeaderRenderer = (FGanttChartActivityColumnHeaderRenderer)ganttChartActivityCellControler.getColumnHeaderRenderer();
    columnHeaderRenderer.setShowExpandCollapseHeaderButton(showExpandCollapseHeaderButton);
    return addGanttChartColumn(ganttChartActivityCellControler);
  }
  
  public FTableColumn addGanttChartColumn(AbstractCellControler controler){
    FTableColumn fCol = addGanttChartColumn();
    fCol.setCellEditor(controler);
    return fCol;
  }
  
  private FTableColumn addGanttChartColumn(){
    return addColumn(null/*theFocDesc*/, FField.FLD_ID_GANTT_CHART, null, GANTT_COLUMN_TITLE, 30, false);
  }
  
  public FTableColumn addDrawingColumn(FDrawingScale drawingScale){
    FTableColumn fCol = addColumn(null, FField.FLD_ID_DRAWING, null, DRAWING_COLUMN_TITLE, 30, false);
    DrawingCellControler drawingCellController = new DrawingCellControler(drawingScale);
    fCol.setCellEditor(drawingCellController);
    return fCol;
  }
  
  public FTableColumn getColumnAt(int col) {
    FTableColumn tableColumn = null;
    if (columns != null && col >= 0) {
      tableColumn = (FTableColumn) columns.get(col);
    }
    return tableColumn;
  }

  public int getVisibleColumnIndex(int i) {
    int col = i;
    if (visibleColumnsIndexes != null && visibleColumnsIndexes.size() > 0 && i >= 0) {
      Integer intObj = (Integer) visibleColumnsIndexes.get(i);
      col = intObj.intValue();
    }
    return col;
  }

  public FTableColumn getVisibleColumnAt(int i) {
    return getColumnAt(getVisibleColumnIndex(i));
  }

  public int getColumnCount() {
    int count = (columns != null) ? columns.size() : 0;
    return count;
  }

  public int getColumnIndexForId(int id) {
    int index = -1;
    for (int i = 0; i < getColumnCount(); i++) {
      FTableColumn tableCol = getColumnAt(i);
      if (tableCol != null) {
        if (tableCol.getID() == id) {
          index = i;
          break;
        }
      }
    }
    return index;
  }

  public FTableColumn getColumnById(int id) {
    int index = getColumnIndexForId(id);
    return getColumnAt(index);
  }
  
  public boolean containsFieldPath(FFieldPath aFieldPath){
    boolean contains = false;
    for (int i = 0; i < getColumnCount() && !contains; i++){
      FTableColumn col = getColumnAt(i);
      FFieldPath fieldPath = col.getFieldPath();
      if(fieldPath.isEqualTo(aFieldPath)){
        contains = true;
      }
    }
    return contains;
  }
  
  public int getTotalWidth(){
    int totalWidth = 0;
    for(int i=0; i<getColumnCount(); i++){
      FTableColumn tc = getColumnAt(i);
      if(tc != null){
        totalWidth += tc.getPreferredWidth();//tc.getSize();
      }
    }
    return totalWidth ;//* Globals.CHAR_WIDTH;
  }
  
  public FTable getTable() {
    return table;
  }
  
  public void setTable(FTable table) {
    this.table = table;
  }
  
  //BElias save column visibility configuration in data base
  public void setColumnVisibilityAccordinglyToConfig(){
    if(isColumnConfigPersistent()){
      ArrayList<Integer> hidenColumns = ColumnsConfig.getHidenColumnsForView(getViewKey());
      for(int i = 0; i < getColumnCount(); i++){
        FTableColumn col = getColumnAt(i);
        if(col.isShowConfigurable()){
        	col.setShow(!hidenColumns.contains(col.getID()));
        }
      }
      adjustColumnVisibility();
    }
  }
  //EElias
  
  public void adjustColumnVisibility(){
    if(visibleColumnsIndexes == null){
      visibleColumnsIndexes = new ArrayList<Integer>();
      for(int i = 0; i < getColumnCount(); i++){
        visibleColumnsIndexes.add(Integer.valueOf(i));
      }
    }
    
    int order = 0;
    for(int i = 0; i < getColumnCount(); i++){
      FTableColumn fCol = getColumnAt(i);
      if(fCol != null && fCol.isShow()){
        visibleColumnsIndexes.set(order, Integer.valueOf(i));
        fCol.setOrderInView(order++);
      }
    }
    
    for(int i = 0; i < getColumnCount(); i++){
      FTableColumn fCol = getColumnAt(i);
      if(fCol != null && fCol.isShow() != fCol.isVisible()){
        if(fCol.isShow()){
          getTable().getColumnModel().addColumn(fCol.getTableColumn());
          getTable().getColumnModel().moveColumn(getTable().getColumnModel().getColumnCount()-1, fCol.getOrderInView());
        }else{
          getTable().getColumnModel().removeColumn(fCol.getTableColumn());          
        }
        
        fCol.setVisible(fCol.isShow());
      }
    }
  }
  
  public FTableColumn getFTableColumn(TableColumn tableColumn){
    FTableColumn fTableColumn = null;
    boolean found = false;
    for(int i = 0; i < getColumnCount() && !found; i++){
      fTableColumn = getColumnAt(i);
      if(fTableColumn.getTableColumn().equals(tableColumn)){
        found = true;
      }
    }
    return fTableColumn;
  }
  
  public double getColumnWidthFactor() {
    return columnWidthFactor;
  }
  
  public void setColumnWidthFactor(double columnWidthFactor) {
    this.columnWidthFactor = columnWidthFactor;
  }
  
  public int getLineNumber() {
    return lineNumber;
  }
  
  public void setLineNumber(int lineNumber) {
    this.lineNumber = lineNumber;
  }
  
  public FocListFilter getFilter() {
    return filter;
  }
  
  public void setFilter(FocListFilter filter) {
    setFilter(filter, true);
  }
  
  public void setFilter(FocListFilter filter, boolean owner){
    this.filter = filter;
    this.owner = owner;
  }
  
  public void popupColumnConfigurationPanel(FTable table){
    FPanel panel = new TableColumnSelector(table);
    Globals.getDisplayManager().popupDialog(panel, "Column selector", true);
  }

  public String getViewKey() {
    return viewKey;
  }

  public void setViewKey(String viewKey) {
    this.viewKey = viewKey;
  }
  
  public boolean isColumnConfigPersistent(){
    return getViewKey() != null && getViewKey().trim().compareTo("") != 0;
  }

  public int getDetailPanelViewIDForNewItem() {
    return detailPanelViewIDForNewItem;
  }

  public void setDetailPanelViewIDForNewItem(int detailPanelViewIDForNewItem) {
    this.detailPanelViewIDForNewItem = detailPanelViewIDForNewItem;
  }

  public void setDetailPanelViewIDForNewItem(int detailPanelViewIDForNewItem, boolean editAfterInsertion) {
    this.detailPanelViewIDForNewItem = detailPanelViewIDForNewItem;
    setEditAfterInsertion(editAfterInsertion);
  }

  public int getColumnResizingMode() {
    return columnResizingMode;
  }

  public void setColumnResizingMode(int columnResizingMode) {
    this.columnResizingMode = columnResizingMode;
    getTable().setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
  }

  public int getColumnsToFreeze() {
    return columnsToFreeze;
  }

  public void setColumnsToFreeze(int columnsToFreeze) {
    this.columnsToFreeze = columnsToFreeze;
  }
  
  public FColumnGroupHeaderConstructor getColumnGroupHeaderConstructor() {
    return columnGroupHeaderConstructor;
  }

	public boolean isEditAfterInsertion() {
		return editAfterInsertion;
	}

	public void setEditAfterInsertion(boolean editAfterInsertion) {
		this.editAfterInsertion = editAfterInsertion;
	}
}
