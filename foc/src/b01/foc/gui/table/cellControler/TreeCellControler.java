/*
 * Created on 24-Apr-2005
 */
package b01.foc.gui.table.cellControler;

import javax.swing.table.*;

import b01.foc.gui.table.FTable;
import b01.foc.gui.table.cellControler.editor.FTreeCellEditor;
import b01.foc.gui.table.cellControler.renderer.FTreeCellRenderer;
import b01.foc.gui.treeTable.FTreeTableModel;

/**
 * @author 01Barmaja
 */
public class TreeCellControler extends AbstractCellControler{
  
  private FTreeCellEditor editor   = null;//To allow beater all selection when get focus by mouse
  private FTreeCellRenderer renderer = null;//To allow editable not editable appearence
  public TreeCellControler(FTable table){
    editor = new FTreeCellEditor(table);
    renderer = new FTreeCellRenderer(table != null ? (FTreeTableModel)table.getModel() : null);
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
  
  public void setTable(FTable table){
    if(table != null){
      editor.setTable(table);
      renderer.setTreeTableModel(table != null ? (FTreeTableModel)table.getModel() : null);
    }
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
    return 0;
  }
  
  public void editRequested(FTable table, int row, int col){
    //We don't need to react to this. 
    //By default when it is a text field the cell is edited when key is typed
  }
}
