package b01.foc.gui.table.cellControler.renderer.gantChartResourceRenderer;

import java.sql.Date;
import java.util.Calendar;
import b01.foc.calendar.FCalendar;
import b01.foc.gui.table.cellControler.renderer.gantChartRenderer.BasicGanttScale;
import b01.foc.list.FocList;

public class GanttChartResourceScale extends BasicGanttScale {
	
	public GanttChartResourceScale(FCalendar calandar){
		super();
    this.calandar = calandar;
    startDate = new Date(0);
    endDate = new Date(0);
	}
  
  public void calculateStartAndEndDates( FocList focList ){
    long minDate = Long.MAX_VALUE;
    long maxDate = 0;
    
    for(int drawingInfoCount = 0; drawingInfoCount < focList.size(); drawingInfoCount++){
      IGantChartResourceDrawingInfo drawingInfo = (IGantChartResourceDrawingInfo)focList.getFocObject(drawingInfoCount);
      int activityCount = drawingInfo.getActivityCount();
      for(int i = 0; i < activityCount; i++){
        long s = drawingInfo.getActivityStartDateAt(i).getTime();
        long dur = (long)drawingInfo.getActivityDurationAt(i) * 60 * 1000;
        
        
        if(s < minDate && s  > 0 ){
          minDate = s;
        }
        if(s+dur > maxDate){
          maxDate = (long)(s+dur);
        }         
      }
    }
    

    this.startDate = new Date(minDate);
    this.endDate = new Date(maxDate);
    Calendar cal = FCalendar.getInstanceOfJavaUtilCalandar();
    
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
}
