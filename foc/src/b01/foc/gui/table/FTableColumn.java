/*
 * Created on 15 fvr. 2004
 */
package b01.foc.gui.table;

import java.awt.Color;
import java.text.Format;

import b01.foc.desc.field.*;
import b01.foc.gui.table.cellControler.*;
import b01.foc.*;
import b01.foc.desc.*;

import javax.swing.table.*;

/**
 * @author 01Barmaja
 */
public class FTableColumn implements Cloneable /* extends TableColumn */{
  //we need one of these parameters
  //1-
  private FocDesc focDesc = null;
  private FFieldPath fieldPath = null;
  //2-
  private FField field = null;
  //End
  
  private int id;
  private String title = "";
  private boolean editable = false;
  private int size = 0;
  private int redererSupplementSize = 0;
  private TableColumn tableColumn = null;//This is the instance of the real tableColumn only available after construct of the table
  private AbstractCellControler cellEditor = null;
  private int orderInView = -1;
  private boolean visible = true;//Tells if the column is actually set to visible or not
  private boolean show = true;//Tells if the view would like to hide the column
  private boolean showConfigurable = true;//Tells if the view can decide weather to show or not the column
  private Color headerBackColor = null;//The column header backColor
  private boolean allowAutoResizing = true;
  
  public void init(FocDesc focDesc, FFieldPath fieldPath, FField field, int id, String title, int size, boolean editable) {
    this.focDesc = focDesc;
    this.title = title;
    this.id = id;
    this.editable = editable;
    this.fieldPath = fieldPath;
    this.size = size;
    this.field = field;
    
    this.allowAutoResizing = true;
    cellEditor = null;
    headerBackColor = Color.ORANGE;
    FField fField = getField();
    if(fField != null){
      this.setCellEditor(fField.getTableCellEditor(null));
      
      if(this.size == 0){
        this.size = fField.getFieldDisplaySize();
      }else{
        this.allowAutoResizing = false;
      }
      if(this.title == null){
      	this.title = fField.getTitle();
      }
    }
  }
  
  public Object clone(){
    Object obj = null;
    try {
      obj = super.clone();
    } catch (CloneNotSupportedException e) {
      e.printStackTrace();
    }
    return obj;
  }
  
  public void dispose(){
    focDesc = null;
    if(fieldPath != null){
      fieldPath.dispose();
      fieldPath = null;
    }
    field = null;
    title = null;
    
    tableColumn = null;//This is the instance of the real tableColumn only available after construct of the table
    
    if(cellEditor != null){
      cellEditor.dispose();
      cellEditor = null;
    }
  }
  
  public FTableColumn(FocDesc focDesc, FFieldPath fieldPath, int id, String title, int size, boolean editable) {
    init(focDesc, fieldPath, null, id, title, size, editable);
  }

  public FTableColumn(FocDesc focDesc, FFieldPath fieldPath, int id, String title, boolean editable) {
    init(focDesc, fieldPath, null, id, title, 0, editable);
  }
  
  public FTableColumn(FField field, int id, String title, int size, boolean editable) {
    init(null, null, field, id, title, size, editable);
  }

  public FTableColumn(FocDesc focDesc, int id, String title, int size, boolean editable) {
    init(focDesc, FFieldPath.newFieldPath(id), null, id, title, size, editable);
  }

  private FField getField(){
    FField retField = field;
    
    if(retField == null && focDesc!=null && fieldPath != null){
      retField = fieldPath.getFieldFromDesc(focDesc);
    }
    return retField;
  }
  
  public String getTitle() {
    return title;
  }

  public int getID() {
    return id;
  }
  
  public void setId(int id){
    this.id = id;
  }

  public boolean getEditable() {
    return editable;
  }

  /**
   * @param b
   */
  public void setEditable(boolean b) {
    editable = b;
  }

  /**
   * @return
   */
  public FFieldPath getFieldPath() {
    return fieldPath;
  }

  /**
   * @return Returns the preferredWidth.
   */
  public int getPreferredWidth() {  
    return (size + redererSupplementSize) * Globals.getDisplayManager().getCharWidth();
  }
  
  public void setPreferredWidth(int sizeInPixels){
    size = (sizeInPixels / Globals.getDisplayManager().getCharWidth()) - redererSupplementSize;  
  	if( getTableColumn() != null ){
      getTableColumn().setPreferredWidth(sizeInPixels);  
    }
  }

  /**
   * @param preferredWidth
   *          The preferredWidth to set.
   */
  public void setSize(int size) {
    this.size = size ;
    setAllowAutoResizing(false);
  }
  
  /**
   * @param title The title to set.
   */
  public void setTitle(String title) {
    this.title = title;
  }
  
  /**
   * @return Returns the tableColumn.
   */
  public TableColumn getTableColumn() {
    return tableColumn;
  }
  
  /**
   * @param tableColumn The tableColumn to set.
   */
  public void setTableColumn(TableColumn tableColumn) {
    this.tableColumn = tableColumn;
  }
  
  /**
   * @return Returns the cellEditor.
   */
  public AbstractCellControler getCellEditor() {
    return cellEditor;
  }
  
  /**
   * @param cellEditor The cellEditor to set.
   */
  public void setCellEditor(AbstractCellControler cellEditor) {
    this.cellEditor = cellEditor;
    redererSupplementSize = cellEditor != null ? cellEditor.getRendererSupplementSize() : 0; 
  }
  
  public int getSize() {
    return size;
  }
  
  public Format getFormat() {
    return cellEditor != null ? cellEditor.getFormat() : null;
  }
  
  public void setFormat(Format format) {
    if(cellEditor != null) cellEditor.setFormat(format);
  }
  
  public boolean isShow() {
    return show;
  }
  
  public void setShow(boolean show) {
    this.show = show;
  }
  
	public boolean isShowConfigurable() {
		return showConfigurable;
	}

	public void setShowConfigurable(boolean showConfigurable) {
		this.showConfigurable = showConfigurable;
	}
	
  public boolean isVisible() {
    return visible;
  }
  
  public void setVisible(boolean visible) {
    this.visible = visible;
  }
  
  public void setHeaderBackColor(Color backColor){
  	this.headerBackColor = backColor;
  }
  
  public Color getHeaderBackColor(){
  	return this.headerBackColor;
  }
  
  public int getOrderInView() {
    return orderInView;
  }
  
  public void setOrderInView(int orderInView) {
    this.orderInView = orderInView;
  }

  public boolean isAllowAutoResizing() {
    return allowAutoResizing;
  }

  private void setAllowAutoResizing(boolean allowAutoResizing) {
    this.allowAutoResizing = allowAutoResizing;
  }
}
