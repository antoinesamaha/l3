package b01.foc.gui.table.cellControler.renderer.gantChartActivityRenderer;

import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JTable;
import b01.foc.gui.table.cellControler.renderer.gantChartRenderer.BasicGanttScale;
import b01.foc.gui.table.cellControler.renderer.gantChartRenderer.GanttColumnHeaderPanel;

public class FGanttChartActivityColumnHeaderRenderer extends FGanttChartActivityCellRenderer {
  
  private JTable table = null;
  private HeaderListener listener = null;
  private GanttColumnHeaderPanel panel = null;
  private boolean showExpandCollapseHeaderButton = false;
  
  public FGanttChartActivityColumnHeaderRenderer(BasicGanttScale gantScale) {
    super(gantScale);
    panel = new GanttColumnHeaderPanel(getGantScale());
  }
  
  public void dispose() {
    super.dispose();
    table = null;
    listener = null;
    if( panel != null ){
      panel.dispose();
      panel = null;
    }
  }
  
  private HeaderListener getHeaderListener(){
    if( listener == null ){
      listener = new HeaderListener();
    }
    return listener;
  }
  
  public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
    this.table = table;
    panel.set(table);
    
    if( isShowExpandCollapseHeaderButton() ){
      table.getTableHeader().addMouseListener(getHeaderListener());
      boolean isPressed = (column == pushedColumn);
      panel.getHideOrShowButton().getModel().setPressed(isPressed);
      panel.getHideOrShowButton().getModel().setArmed(isPressed);  
    }
    
    return panel;
  }
  
  public boolean isShowExpandCollapseHeaderButton() {
    return showExpandCollapseHeaderButton;
  }

  public void setShowExpandCollapseHeaderButton(boolean showExpandCollapseHeaderButton) {
    this.showExpandCollapseHeaderButton = showExpandCollapseHeaderButton;
  }
  
  private int pushedColumn = -1;
  
  public void setPressedColumn(int col) {
    pushedColumn = col;
  }
  
  class HeaderListener extends MouseAdapter {
    
    public void mousePressed(MouseEvent e) {
      Point columnMousePoint = e.getPoint();
      int col = table.getTableHeader().columnAtPoint(columnMousePoint);
      Rectangle headerRect = table.getTableHeader().getHeaderRect(col);
      Rectangle buttonRect = panel.getHideOrShowButton().getBounds();
      
      buttonRect.x += headerRect.x;
      buttonRect.y += headerRect.y;
      
      if (buttonRect.contains(columnMousePoint)) {
        setPressedColumn(col);
        table.getTableHeader().repaint();
      }
    }
    
    public void mouseReleased(MouseEvent e) {
      setPressedColumn(-1);
      table.getTableHeader().repaint();
    }
  }
}
