/*
 * Created on 24-Apr-2005
 */
package b01.foc.gui.table.cellControler;

import b01.foc.gui.table.FTable;
import b01.foc.gui.table.cellControler.editor.FTableObjectCellEditor;
import b01.foc.gui.table.cellControler.renderer.FComboBoxRenderer;

import javax.swing.table.*;

/**
 * @author 01Barmaja
 */
public class ObjectCellControler extends AbstractCellControler{
  private FTableObjectCellEditor editor = null; 
  private FComboBoxRenderer renderer = null;
  
  public ObjectCellControler(){
    editor = new FTableObjectCellEditor(this);
    renderer = new FComboBoxRenderer();
  }
  
  /* (non-Javadoc)
   * @see b01.foc.gui.table.editor.CellEditorInterface#getEditor()
   */
  public TableCellEditor getEditor() {
    return editor;
  }
  
  /* (non-Javadoc)
   * @see b01.foc.gui.table.editor.CellEditorInterface#getRenderer()
   */
  public TableCellRenderer getRenderer() {
    return renderer;
  }
  
  @Override
	public TableCellRenderer getColumnHeaderRenderer() {
		// TODO Auto-generated method stub
		return null;
	}  
  
  public int getRendererSupplementSize(){
    return 2;
  }
  
  public void editRequested(FTable table, int row, int col){
    if(table != null && row >= 0 && col >= 0){
      
      editor.startCellEditing(table, row, col);
      /*
      if (table.editCellAt(row, col)){
        table.getEditorComponent().requestFocusInWindow();
      }
      */
      
    }
  }
  
  /* (non-Javadoc)
   * @see b01.foc.gui.table.cellControler.AbstractCellControler#dispose()
   */
  public void dispose() {
    if(editor != null){
      editor.dispose();
      editor = null;
    }
    if(renderer != null){
      renderer.dispose();
      renderer = null;
    }
  }
}
