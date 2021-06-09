package b01.foc.gui.table.cellControler.renderer.gantChartActivityRenderer;

import java.sql.Date;
import java.util.Calendar;
import b01.foc.calendar.FCalendar;
import b01.foc.desc.FocObject;
import b01.foc.gui.table.cellControler.renderer.gantChartRenderer.BasicGanttScale;
import b01.foc.tree.FNode;
import b01.foc.tree.FTree;
import b01.foc.tree.TreeScanner;

public class GanttActivityScale extends BasicGanttScale {
  
  public static final int MODE_FORWARD = 1;
  public static final int MODE_REVERSE = 2;
  public static final int MODE_BOTH    = 3;
  private int schedulingMode = MODE_BOTH;
  
  public GanttActivityScale(FCalendar calendar){
    super();
    this.calandar = calendar;
		//NBR_PIXELS_PER_DAY = 600;
		//NBR_PIXELS_PER_MINUTE = ((double)NBR_PIXELS_PER_DAY) / (24*60);
    startDate = new Date(0);
    endDate = new Date(0);
	}
  
  private boolean isNoOrZeroDate(Date date){
    return FCalendar.getTimeAtMidnight(date) == FCalendar.getTimeAtMidnight(new Date(0));
  }
  
  public void calculateStartAndEndDates( FTree tree ){
    
    this.startDate = new Date(Long.MAX_VALUE);
    this.endDate = new Date(0);
    
    tree.scan(new TreeScanner<FNode>(){

      public void afterChildren(FNode node) {
        FocObject focObj = (FocObject)node.getObject();
        if( focObj != null ){
          IGanttChartObjectInfo gantChartObjectInfo = (IGanttChartObjectInfo)focObj;
          if( getSchedulingMode() == MODE_FORWARD || getSchedulingMode() == MODE_BOTH ){
            if( gantChartObjectInfo.getMinimumStartDate() != null && gantChartObjectInfo.getMinimumStartDate().compareTo(GanttActivityScale.this.startDate) < 0 ){
              if( !isNoOrZeroDate(gantChartObjectInfo.getMinimumStartDate())){
                GanttActivityScale.this.startDate = gantChartObjectInfo.getMinimumStartDate();  
              }
            }
            
            if( gantChartObjectInfo.getMinimumEndDate() != null && gantChartObjectInfo.getMinimumEndDate().compareTo(GanttActivityScale.this.endDate) > 0 ) {
              if( !isNoOrZeroDate(gantChartObjectInfo.getMinimumEndDate())){
                GanttActivityScale.this.endDate = gantChartObjectInfo.getMinimumEndDate();  
              }
            }
          }
          
          if( getSchedulingMode() == MODE_REVERSE || getSchedulingMode() == MODE_BOTH ){
            
            if( gantChartObjectInfo.getMaximumStartDate() != null && gantChartObjectInfo.getMaximumStartDate().compareTo(GanttActivityScale.this.startDate) < 0 ){
              if( !isNoOrZeroDate(gantChartObjectInfo.getMaximumStartDate())){
                GanttActivityScale.this.startDate = gantChartObjectInfo.getMaximumStartDate();  
              }
            }
            
            if( gantChartObjectInfo.getMaximumEndDate() != null && gantChartObjectInfo.getMaximumEndDate().compareTo(GanttActivityScale.this.endDate) > 0 ) {
              if( !isNoOrZeroDate(gantChartObjectInfo.getMaximumEndDate())){
                GanttActivityScale.this.endDate = gantChartObjectInfo.getMaximumEndDate();  
              }
            }
          }
        }
        
      }

      public boolean beforChildren(FNode node) {
        return true;
      }
      
    });
    
  
    Calendar cal = FCalendar.getInstanceOfJavaUtilCalandar();
    
    if( startDate.getTime() == Long.MAX_VALUE ){
      startDate.setTime(0);
    }
    
    cal.setTime(startDate);
    cal.set(Calendar.HOUR_OF_DAY, cal.getMinimum(Calendar.HOUR_OF_DAY));
    cal.set(Calendar.MINUTE, cal.getMinimum(Calendar.MINUTE));
    cal.set(Calendar.SECOND, cal.getMinimum(Calendar.SECOND));
    this.startDate = new Date(cal.getTimeInMillis());
    
    cal.setTime(endDate);
    cal.set(Calendar.HOUR_OF_DAY, cal.getMaximum(Calendar.HOUR_OF_DAY));
    cal.set(Calendar.MINUTE, cal.getMaximum(Calendar.MINUTE));
    cal.set(Calendar.SECOND, cal.getMaximum(Calendar.SECOND));
    this.endDate = new Date(cal.getTimeInMillis());
  }

  public int getSchedulingMode() {
    return schedulingMode;
  }

  public void setSchedulingMode(int schedulingMode) {
    this.schedulingMode = schedulingMode;
  }
  
}
