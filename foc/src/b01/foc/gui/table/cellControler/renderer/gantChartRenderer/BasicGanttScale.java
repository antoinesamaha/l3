package b01.foc.gui.table.cellControler.renderer.gantChartRenderer;

import java.sql.Date;
import java.sql.Time;

import b01.foc.calendar.FCalendar;
import b01.foc.calendar.FCalendarDesc;

public class BasicGanttScale {
  
  protected int NBR_PIXELS_PER_DAY = 0;
  protected double NBR_PIXELS_PER_MINUTE = 0;
  protected double NIGHT_MINUTES_DIVIDER = 0;
  
  protected Date startDate = null;
  protected Date endDate = null;
  protected FCalendar calandar = null;
  
  private static final int PIXEL_WIDTH_HOUR = 600;
  private static final int PIXEL_WIDTH_DAY  = 75;
  private static final int PIXEL_WIDTH_WEEK  = 10;
  
  public static final int VIEW_DAILY  = 1;
  public static final int VIEW_HOURLY = 2;
  public static final int VIEW_WEEKLY = 3;
  
  private int ganttColumnsView = 0;
  
  public BasicGanttScale(){
    setGanttColumnsView(VIEW_HOURLY);
  }
  
  public void dispose(){
    startDate = null;
    endDate = null;
    calandar = null;
  }
  
  public FCalendar getCalandar(){
    return this.calandar;
  }
  
  public int getTotalNumberOFPixelsForColumn(){
    int totalPixels = getPixelsForDate(endDate) - getPixelsForDate(startDate);
    return totalPixels; 
  }

  public int getPixelsForMinutes(double duration){
    return (int)(duration * NBR_PIXELS_PER_MINUTE); 
  }
  
  public int getPixelsForDate(Date date){
    long dateTimeMilli = date != null ? date.getTime() : 0;
    long startDateMilli = startDate != null ? startDate.getTime() : 0;
    long sinceStartMilli = dateTimeMilli - startDateMilli;
    double sinceStartMinutes_Night = 0;
    double sinceStartMinutes_Day = (int)((sinceStartMilli) / (60 * 1000));
    
    long sinceStartMilli_Night = 0;
    long sinceStartMilli_Day = 0;
    FCalendar fCal = getCalandar();
    if(fCal != null){
      int numberOfDays = (int)(sinceStartMilli / FCalendar.MILLISECONDS_IN_DAY);
      int numberOfMilliSecond = (int)(sinceStartMilli % FCalendar.MILLISECONDS_IN_DAY);
      double nonWorkingHourInDay = fCal.getNumberOfNonWorkingHoursInDay();
      
      sinceStartMilli_Night = (long)((numberOfDays * nonWorkingHourInDay) * FCalendar.MILLISECONDS_IN_HOUR);
      
      Time startTime = fCal.getPropertyTime(FCalendarDesc.FLD_START_TIME);
      long startSinceMidnight = FCalendar.getTimeSinceMidnight(startTime);
      Time endTime = fCal.getPropertyTime(FCalendarDesc.FLD_END_TIME);
      long endSinceMidNight = FCalendar.getTimeSinceMidnight(endTime);
      
      long nonWorkingBeforStartMilli = numberOfMilliSecond > startSinceMidnight ? startSinceMidnight : numberOfMilliSecond;
      long nonWorkingAfterFinsihMilli = numberOfMilliSecond > endSinceMidNight ? numberOfMilliSecond - endSinceMidNight : 0;
      
      sinceStartMilli_Night = sinceStartMilli_Night + nonWorkingBeforStartMilli + nonWorkingAfterFinsihMilli;
      sinceStartMilli_Day = sinceStartMilli - sinceStartMilli_Night;
      
      
      sinceStartMinutes_Night = (int)((sinceStartMilli_Night) / (60 * 1000));;
      sinceStartMinutes_Day = (int)((sinceStartMilli_Day) / (60 * 1000));
    }
    
    //we should split sinceStartMinutes to 2 values 
    //sinceStartMinutes_Night
    //sinceStartMinutes_Day
    //return getPixelsForMinutes(sinceStartMinutes_Day) + (getPixelsForMinutes(sinceStartMinutes_NIGHT)/4);
    
    return (int)(getPixelsForMinutes(sinceStartMinutes_Day) + (getPixelsForMinutes(sinceStartMinutes_Night)/NIGHT_MINUTES_DIVIDER)); 
  }

  public Date getEndDate() {
    return endDate;
  }

  public Date getStartDate() {
    return startDate;
  }

  public int getGanttColumnsView() {
    return ganttColumnsView;
  }

  public void setGanttColumnsView(int ganttColumnsView) {
    this.ganttColumnsView = ganttColumnsView;
    if( ganttColumnsView == VIEW_HOURLY ){
      NBR_PIXELS_PER_DAY = PIXEL_WIDTH_HOUR;
    }else if( ganttColumnsView == VIEW_DAILY ){
      NBR_PIXELS_PER_DAY = PIXEL_WIDTH_DAY;
    }else if( ganttColumnsView == VIEW_WEEKLY ){
      NBR_PIXELS_PER_DAY = PIXEL_WIDTH_WEEK;
    }
    NBR_PIXELS_PER_MINUTE = ((double)NBR_PIXELS_PER_DAY) / (24*60);
    NIGHT_MINUTES_DIVIDER = 1000;  
  }
  
}
