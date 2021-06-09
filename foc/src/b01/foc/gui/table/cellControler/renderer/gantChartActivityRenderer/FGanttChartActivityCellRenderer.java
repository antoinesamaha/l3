package b01.foc.gui.table.cellControler.renderer.gantChartActivityRenderer;

import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import b01.foc.gui.table.FTable;
import b01.foc.gui.table.cellControler.renderer.gantChartRenderer.BasicGanttScale;

@SuppressWarnings("serial")
public class FGanttChartActivityCellRenderer implements TableCellRenderer {
  
	public static final int COLUMN_WIDTH = 1000;
	private FGanttActivityRowPanel gPanel = null;
	private BasicGanttScale gantScale = null;
	
	public FGanttChartActivityCellRenderer(BasicGanttScale gantScale){
		gPanel = new FGanttActivityRowPanel(gantScale);
		this.gantScale = gantScale;
	}
	  
  public void dispose(){
  	if(this.gPanel != null){
  		this.gPanel.dispose();
  		this.gPanel = null;
  	}
  	
  	this.gantScale = null;
  }
  
  public BasicGanttScale getGantScale(){
  	return this.gantScale;
  }

	/**
	 * TreeCellRenderer method. Overridden to update the visible row.
	 */
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		
		FTable fTable = (FTable)table;
    IGanttChartObjectInfo drawingInfo = (IGanttChartObjectInfo)fTable.getTableModel().getRowFocObject(row);
		gPanel.setTable(table);
    gPanel.setDrawingInfo(drawingInfo);
		return gPanel;
	}

}
