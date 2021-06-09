package b01.foc.gui.table.cellControler.renderer.drawingCellRenderer;

import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JPanel;
import javax.swing.JTable;

@SuppressWarnings("serial")
public class FDrawingRowPanel extends JPanel {
	private IFDrawingObjectInfo drawingInfo = null;
  private FDrawingScale drawingScale = null;
  private JTable table = null;
  
	public FDrawingRowPanel(FDrawingScale drawingScale){
		this.drawingScale = drawingScale;
  }
	
	public void dispose(){
		drawingInfo = null;
    drawingScale = null;
    table = null;
	}
  
  @Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
    
    int rowHeight = this.table.getRowHeight();
    drawingInfo.draw(g, drawingScale, rowHeight);
	}
  
  public Dimension getPreferredSize(){
    Dimension dimension = super.getPreferredSize();
    dimension.width = drawingScale.getTotalNumberOFPixelsForColumn();
    return dimension;
  }

  public void setDrawingInfo(IFDrawingObjectInfo drawingInfo) {
    this.drawingInfo = drawingInfo;
  }

  public void setTable(JTable table) {
    this.table = table;
  }
}
