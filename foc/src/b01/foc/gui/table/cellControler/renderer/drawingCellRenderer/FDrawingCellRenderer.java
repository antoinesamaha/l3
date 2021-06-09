package b01.foc.gui.table.cellControler.renderer.drawingCellRenderer;

import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import b01.foc.gui.table.FTable;

@SuppressWarnings("serial")
public class FDrawingCellRenderer implements TableCellRenderer {
  
	private FDrawingRowPanel gPanel = null;
	private FDrawingScale drawingScale = null;
	
	public FDrawingCellRenderer(FDrawingScale drawingScale){
		gPanel = new FDrawingRowPanel(drawingScale);
		this.drawingScale = drawingScale;
	}
	  
  public void dispose(){
  	if(this.gPanel != null){
  		this.gPanel.dispose();
  		this.gPanel = null;
  	}
  	
  	this.drawingScale = null;
  }
  
  public FDrawingScale getDrawingScale(){
  	return this.drawingScale;
  }

	/**
	 * TreeCellRenderer method. Overridden to update the visible row.
	 */
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		
		FTable fTable = (FTable)table;
    IFDrawingObjectInfo drawingInfo = (IFDrawingObjectInfo)fTable.getTableModel().getRowFocObject(row);
		gPanel.setTable(table);
    gPanel.setDrawingInfo(drawingInfo);
		return gPanel;
	}

}
