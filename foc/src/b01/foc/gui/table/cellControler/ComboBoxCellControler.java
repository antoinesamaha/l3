/*
 * Created on 24-Apr-2005
 */
package b01.foc.gui.table.cellControler;

import b01.foc.gui.*;
import b01.foc.gui.table.*;
import b01.foc.gui.table.cellControler.editor.FDefaultComboBoxCellEditor;
import b01.foc.gui.table.cellControler.renderer.FComboBoxRenderer;

import java.util.Iterator;
import javax.swing.table.*;

/**
 * @author 01Barmaja
 */
public class ComboBoxCellControler extends AbstractCellControler{
  private FDefaultComboBoxCellEditor editor = null;//To allow beater all selection when get focus by mouse 
  private FComboBoxRenderer renderer = null;//To allow editable not editable appearence

  private void init(FGAbstractComboBox combo){
    editor = new FDefaultComboBoxCellEditor(combo);
    editor.setClickCountToStart(2);
    renderer = new FComboBoxRenderer();
  }
  
  public ComboBoxCellControler(Iterator choices, boolean sort){
    FGComboBox combo = new FGComboBox(choices, sort);
    init(combo);
  }

  public ComboBoxCellControler(int fieldID){
    FGObjectComboBox combo = new FGObjectComboBox(fieldID);
    init(combo);
  }

  public ComboBoxCellControler(int fieldID, FTableView tableView){
    FGObjectComboBox combo = new FGObjectComboBox(fieldID, tableView);
    init(combo);
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
      if (table.editCellAt(row, col)){
        table.getEditorComponent().requestFocusInWindow();
      }
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
