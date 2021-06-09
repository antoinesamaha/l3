package b01.foc.gui.table.cellControler.renderer.drawingCellRenderer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import javax.swing.JPanel;
import javax.swing.JTable;

@SuppressWarnings("serial")
public class FDrawingHeaderPanel extends JPanel {
	private FDrawingScale drawingScale = null;
	private JTable table = null;
  
	public FDrawingHeaderPanel(FDrawingScale drawingScale) {
    this.drawingScale = drawingScale;
		setBackground(Color.ORANGE);
	}
  
	public void dispose(){
		this.drawingScale = null;
    table = null;
	}
	
	private FDrawingScale getDrawingScale(){
		return this.drawingScale;
	}
	
	public void set(JTable table){
		this.table = table;
		int headerHeight = table.getTableHeader().getHeight();
		Rectangle oldBound = getBounds();
		setBounds(oldBound.x, oldBound.y, oldBound.width, headerHeight);
	}
	
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
	}
  
  public Dimension getPreferredSize(){
    Dimension dimension = super.getPreferredSize();
    dimension.width = drawingScale.getTotalNumberOFPixelsForColumn();
    return dimension;
  }
}
