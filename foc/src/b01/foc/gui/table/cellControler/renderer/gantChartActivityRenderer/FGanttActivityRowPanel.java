package b01.foc.gui.table.cellControler.renderer.gantChartActivityRenderer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Polygon;
import b01.foc.gui.table.cellControler.renderer.gantChartRenderer.BasicGanttChartRowPanel;
import b01.foc.gui.table.cellControler.renderer.gantChartRenderer.BasicGanttScale;

@SuppressWarnings("serial")
public class FGanttActivityRowPanel extends BasicGanttChartRowPanel {
	private IGanttChartObjectInfo drawingInfo = null;
  private static final int BOTTOM_UP_EDGES = 1;
  private static final int TOP_DOWN_EDGES  = 2;
  
  private static final Color COLOR_FORWARD_ACTIVITY            = Color.GREEN;
  private static final Color COLOR_CRITICAL_ACTIVITY           = Color.RED;
  private static final Color COLOR_BACKWARD_ACTIVITY           = Color.BLUE;
  private static final Color COLOR_NONE_ACTIVITY               = Color.GRAY;
  private static final Color COLOR_OVERLAPPING_NONE_ACTIVITIES = Color.BLACK;
  
	public FGanttActivityRowPanel(BasicGanttScale gantScale){
		super(gantScale);
  }
	
	public void dispose(){
		super.dispose();
		this.drawingInfo = null;
	}
  
  @Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
    drawActivities(g);
    drawNoneActivities(g);
	}
  
  private void drawForwardActivities(Graphics g){
    g.setColor(COLOR_FORWARD_ACTIVITY);
    int activityStart = gantScale.getPixelsForDate(this.drawingInfo.getMinimumStartDate());        
    int activityEnd = gantScale.getPixelsForDate(this.drawingInfo.getMinimumEndDate());
    int activityWidth = activityEnd - activityStart;
    if( this.drawingInfo.isInCriticalPath() ){
      g.setColor(COLOR_CRITICAL_ACTIVITY);
    }
    g.fillRect(activityStart, 0, activityWidth, (int)(this.table.getRowHeight()*0.3));
    drawActivityEdges(g, activityStart, activityEnd, TOP_DOWN_EDGES);
  }
  
  private void drawReverseActivities(Graphics g){
    int activityStart = gantScale.getPixelsForDate(this.drawingInfo.getMaximumStartDate());        
    int activityEnd = gantScale.getPixelsForDate(this.drawingInfo.getMaximumEndDate());
    int activityWidth = activityEnd - activityStart;
    g.setColor(COLOR_BACKWARD_ACTIVITY);
    g.fillRect(activityStart, (int)(this.table.getRowHeight()*0.7), activityWidth, this.table.getRowHeight()-0);  
    drawActivityEdges(g, activityStart, activityEnd, BOTTOM_UP_EDGES);
  }
  
  private void drawActivities(Graphics g){
    if( this.drawingInfo.isActivity() ){
      if( this.drawingInfo.getMinimumStartDate() != null && this.drawingInfo.getMinimumEndDate() != null ){
        if( ((GanttActivityScale)gantScale).getSchedulingMode() == GanttActivityScale.MODE_FORWARD ||
            ((GanttActivityScale)gantScale).getSchedulingMode() == GanttActivityScale.MODE_BOTH){
          drawForwardActivities(g);  
        }  
      }
      
      if( this.drawingInfo.getMaximumStartDate() != null && this.drawingInfo.getMaximumEndDate() != null ){
        if( ((GanttActivityScale)gantScale).getSchedulingMode() == GanttActivityScale.MODE_REVERSE ||
            ((GanttActivityScale)gantScale).getSchedulingMode() == GanttActivityScale.MODE_BOTH){
          drawReverseActivities(g);  
        }
      }
    }
	}
  
  private void drawNoneActivities(Graphics g){
    
    if(!this.drawingInfo.isActivity()){
      
      int activityMinStart = 0;        
      int activityMinEnd   = 0;
      int activityMinWidth = 0;
      
      if( this.drawingInfo.getMinimumStartDate() != null && this.drawingInfo.getMinimumEndDate() != null ){
        if( ((GanttActivityScale)gantScale).getSchedulingMode() == GanttActivityScale.MODE_FORWARD ||
            ((GanttActivityScale)gantScale).getSchedulingMode() == GanttActivityScale.MODE_BOTH){
          activityMinStart = gantScale.getPixelsForDate(this.drawingInfo.getMinimumStartDate());        
          activityMinEnd   = gantScale.getPixelsForDate(this.drawingInfo.getMinimumEndDate());
          activityMinWidth = activityMinEnd - activityMinStart;
          g.setColor(COLOR_NONE_ACTIVITY);
          g.fillRect(activityMinStart, 0, activityMinWidth, (int)(this.table.getRowHeight()*0.3));
          drawActivityEdges(g, activityMinStart, activityMinEnd, TOP_DOWN_EDGES);  
        }
      }
      
      int activityMaxStart = 0;        
      int activityMaxEnd   = 0;
      int activityMaxWidth = 0;
      
      if( this.drawingInfo.getMaximumStartDate() != null && this.drawingInfo.getMaximumEndDate() != null ){
        if( ((GanttActivityScale)gantScale).getSchedulingMode() == GanttActivityScale.MODE_REVERSE ||
            ((GanttActivityScale)gantScale).getSchedulingMode() == GanttActivityScale.MODE_BOTH){
          activityMaxStart = gantScale.getPixelsForDate(this.drawingInfo.getMaximumStartDate());        
          activityMaxEnd   = gantScale.getPixelsForDate(this.drawingInfo.getMaximumEndDate());
          activityMaxWidth = activityMaxEnd - activityMaxStart;
          g.setColor(COLOR_NONE_ACTIVITY);
          g.fillRect(activityMaxStart, 0, activityMaxWidth, (int)(this.table.getRowHeight()*0.3));
          drawActivityEdges(g, activityMaxStart, activityMaxEnd, TOP_DOWN_EDGES);  
        }
      }
      
      if( ((GanttActivityScale)gantScale).getSchedulingMode() == GanttActivityScale.MODE_BOTH ){
        g.setColor(COLOR_OVERLAPPING_NONE_ACTIVITIES);
        if( activityMaxStart < activityMinStart && activityMaxEnd > activityMinEnd ){
          g.fillRect(activityMinStart, 0, (activityMinEnd-activityMinStart), (int)(this.table.getRowHeight()*0.3));
        }else if( activityMinStart < activityMaxStart && activityMinEnd > activityMaxStart ){
          g.fillRect(activityMaxStart, 0, (activityMinEnd-activityMaxStart), (int)(this.table.getRowHeight()*0.3));
        }else if( activityMaxStart < activityMinStart && activityMaxEnd > activityMinStart ){
          g.fillRect(activityMinStart, 0, (activityMaxEnd-activityMinStart), (int)(this.table.getRowHeight()*0.3));
        }
      }
    }
  }
  
  private void drawActivityEdges(Graphics g, int activityStart, int activityEnd, int type){
    int edgeWidth = 2;
    if( type == BOTTOM_UP_EDGES ){
      int length = (int)(this.table.getRowHeight()*0.6);
      g.fillRect(activityStart, length, edgeWidth, this.table.getRowHeight());
      g.fillRect(activityEnd-edgeWidth, length, edgeWidth, this.table.getRowHeight());  
      Polygon triangle = new Polygon(new int[]{activityStart-edgeWidth,activityStart+(edgeWidth*2),activityStart+(edgeWidth/2)}, new int[]{length,length,(int)(this.table.getRowHeight()*0.2)}, 3);  
      g.fillPolygon(triangle);
      triangle.translate((activityEnd-activityStart)-edgeWidth, 0);
      g.fillPolygon(triangle);
    }else if( type == TOP_DOWN_EDGES ){
      int length = (int)(this.table.getRowHeight()*0.4);
      g.fillRect(activityStart, 0, edgeWidth, length);
      g.fillRect(activityEnd-edgeWidth, 0, edgeWidth, length);
      Polygon triangle = new Polygon(new int[]{activityStart-edgeWidth,activityStart+(edgeWidth*2),activityStart+(edgeWidth/2)}, new int[]{length,length,(int)(this.table.getRowHeight()*0.8)}, 3);  
      g.fillPolygon(triangle);
      triangle.translate((activityEnd-activityStart)-edgeWidth, 0);
      g.fillPolygon(triangle);
    }
    
  }
  
  public Dimension getPreferredSize(){
    Dimension dimension = super.getPreferredSize();
    dimension.width = gantScale.getTotalNumberOFPixelsForColumn();
    return dimension;
  }

  public void setDrawingInfo(IGanttChartObjectInfo drawingInfo) {
    this.drawingInfo = drawingInfo;
  }
}
