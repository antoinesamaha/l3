package b01.foc.gui.dateInterval;

import java.sql.Date;
import java.util.Calendar;

import b01.foc.Globals;
import b01.foc.calendar.FCalendar;
import b01.foc.desc.FocConstructor;
import b01.foc.desc.FocObject;

public class DateInterval extends FocObject{
  
  private DateIntervalGuiDetailsPanel dateInternalPanel = null;
  
  public DateInterval(FocConstructor constr) {
    super(constr);
    newFocProperties();
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(Globals.getApp().getSystemDate());
    setPropertyDouble(DateIntervalDesc.FLD_YEAR, calendar.get(Calendar.YEAR));
  }
  
  private int getWeek(){
    return (int)getPropertyDouble(DateIntervalDesc.FLD_WEEK);
  }
  
  private int getYear(){
    return (int)getPropertyDouble(DateIntervalDesc.FLD_YEAR);
  }
  
  private int getMonth(){
    return (int)getPropertyDouble(DateIntervalDesc.FLD_MONTH);
  }
  
  public Date getFirstDate(){
    dateInternalPanel.setDatesAccordingToSelection();
    Date firstDate = getPropertyDate(DateIntervalDesc.FLD_FDATE);
    return firstDate; 
  }
  
  public Date getLastDate(){
    dateInternalPanel.setDatesAccordingToSelection();
    Date lastDate = getPropertyDate(DateIntervalDesc.FLD_LDATE);
    return lastDate; 
  }
  
  //temporary function
  public void computeDates(){
    int week = getWeek();
    int year = getYear();
    FCalendar fcal = FCalendar.getDefaultCalendar();
    Date []date = fcal.getStartingEndingDatesOfWeek(week, year);
    setPropertyDate(DateIntervalDesc.FLD_FDATE, date[0]);
    setPropertyDate(DateIntervalDesc.FLD_LDATE, date[1]);    
  }

  //temporary function
  public void setDates() {
    int month = getMonth();
    int year = getYear();

    Calendar Calnow = Calendar.getInstance();
    Calnow.set(year, month, 1);
    Date firstDate = new Date(Calnow.getTimeInMillis());
    firstDate = new Date(FCalendar.getTimeAtMidnight(firstDate));
    Calnow.set(year, (month) + 1, 1);
    Date lastDate = new Date(Calnow.getTimeInMillis());
    lastDate = FCalendar.shiftDate(Calnow, lastDate, -1);
    lastDate = new Date(FCalendar.getTimeAtMidnight(lastDate));
    
    setPropertyDate(DateIntervalDesc.FLD_FDATE, firstDate);
    setPropertyDate(DateIntervalDesc.FLD_LDATE, lastDate);    
    
  }
  
  public DateIntervalGuiDetailsPanel getDateInternalPanel() {
    return dateInternalPanel;
  }

  public void setDateInternalPanel(DateIntervalGuiDetailsPanel dateInternalPanel) {
    this.dateInternalPanel = dateInternalPanel;
  }
  
 /* public JasperPrint fillReport() {
    JasperPrint jrPrint = null;
    JRFocListDataSource dataSource = null;
    try{
      
      FGTabbedPane tabbedPane = stockReportGuiPanel.getTabbedPane();
      int selectedTabIndex = tabbedPane.getSelectedIndex();
      switch (selectedTabIndex) {
      case 0:
        stockReportGuiPanel.setStockreport(stockReportGuiPanel.getMonthlyReportPanel().getStockreport());
        break;
      case 1:
        stockReportGuiPanel.setStockreport(stockReportGuiPanel.getWeeklyReportPanel().getStockreport());
        break;
      case 2:
        stockReportGuiPanel.setStockreport(stockReportGuiPanel.getDateReportPanel().getStockreport());
        break;
      default:
        break;
      }
      
      FocListWithFilter focListWithFilter = new FocListWithFilter(StockMovementFilterDesc.getInstance(), new FocLinkSimple(StockMovementCopyDesc.getInstance()));
      StockMovementFilter filter = (StockMovementFilter) focListWithFilter.getFocListFilter();
      filter.setFirstDate(getFirstDate());
      filter.setLastDate(getLastDate());
      
      StockMovementFilterFocList stockMovementFilterFocList = new StockMovementFilterFocList(focListWithFilter);
      stockMovementFilterFocList.refillFicticiousFocListFromFilterList();
      FocList reportList = stockMovementFilterFocList.getFocList();
      
      dataSource = new JRFocListDataSource(reportList);
      jrPrint = JasperFillManager.fillReport(Globals.getInputStream(reportFile), null, dataSource);
    }catch(Exception e){
      Globals.logException(e);
    }
    return jrPrint;
  }*/
}
