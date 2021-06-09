package b01.foc.gui.table.cellControler.renderer.drawingCellRenderer;

import java.awt.Component;
import javax.swing.JTable;

public class FDrawingColumnHeaderRenderer extends FDrawingCellRenderer {
  
  private JTable table = null;
  private FDrawingHeaderPanel panel = null;
  
  public FDrawingColumnHeaderRenderer(FDrawingScale drawingScale) {
    super(drawingScale);
    panel = new FDrawingHeaderPanel(drawingScale);
  }
  
  public void dispose() {
    super.dispose();
    table = null;
    if( panel != null ){
      panel.dispose();
      panel = null;
    }
  }
  
  public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
    this.table = table;
    panel.set(table);
    return panel;
  }
  
}
