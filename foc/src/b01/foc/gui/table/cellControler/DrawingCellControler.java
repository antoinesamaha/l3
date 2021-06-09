package b01.foc.gui.table.cellControler;

import javax.swing.table.*;
import b01.foc.gui.table.FTable;
import b01.foc.gui.table.cellControler.renderer.drawingCellRenderer.FDrawingCellRenderer;
import b01.foc.gui.table.cellControler.renderer.drawingCellRenderer.FDrawingColumnHeaderRenderer;
import b01.foc.gui.table.cellControler.renderer.drawingCellRenderer.FDrawingScale;

/**
 * @author 01Barmaja
 */
public class DrawingCellControler extends AbstractCellControler {
  
  private FDrawingCellRenderer renderer = null;//To allow editable not editable appearence
  private FDrawingColumnHeaderRenderer columnHeaderRenderer = null;
  
  public DrawingCellControler(FDrawingScale drawingScale){
  	renderer = new FDrawingCellRenderer(drawingScale);
  	columnHeaderRenderer = new FDrawingColumnHeaderRenderer(drawingScale);
  }
  	
  /* (non-Javadoc)
   * @see b01.foc.gui.table.cellControler.AbstractCellControler#dispose()
   */
  public void dispose() {
    if(renderer != null){
      renderer.dispose();
      renderer = null;
    }
    if(columnHeaderRenderer != null){
    	columnHeaderRenderer.dispose();
    	columnHeaderRenderer = null;
    }
  }
  
  /* (non-Javadoc)
   * @see b01.foc.gui.table.editor.CellEditorInterface#getEditor()
   */
  public TableCellEditor getEditor() {
    return null;
  }
  
  /* (non-Javadoc)
   * @see b01.foc.gui.table.editor.CellEditorInterface#getRenderer()
   */
  public TableCellRenderer getRenderer() {
    return renderer;
  }
  
  @Override
	public TableCellRenderer getColumnHeaderRenderer() {
		return columnHeaderRenderer;
	}

  public int getRendererSupplementSize(){
    return 0;
  }
  
  public void editRequested(FTable table, int row, int col){
  }
}
