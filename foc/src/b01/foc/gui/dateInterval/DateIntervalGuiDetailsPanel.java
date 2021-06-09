package b01.foc.gui.dateInterval;

import b01.foc.desc.FocConstructor;
import b01.foc.desc.FocObject;
import b01.foc.gui.FGTabbedPane;
import b01.foc.gui.FPanel;

@SuppressWarnings("serial")
public class DateIntervalGuiDetailsPanel extends FPanel {

  private DateInterval dateInterval = null;

  private FGTabbedPane tabbedPane = null;
  private MonthlyIntervalGuiDetailsPanel monthlyReportPanel = null;
  private WeeklyIntervalGuiDetailsPanel weeklyReportPanel = null;
  private TwoDaysIntervalGuiDetailsPanel dateReportPanel = null;

  public static final int TAB_MONTH = 0;
  public static final int TAB_WEEK  = 1;
  public static final int TAB_DATES = 2;
  
  public DateIntervalGuiDetailsPanel(FocObject object, int viewID) {
    super();
    dateInterval = (DateInterval)object;
    dateInterval.setDateInternalPanel(this);
    tabbedPane = new FGTabbedPane();

    FocConstructor constr = new FocConstructor(DateIntervalDesc.getInstance(), null);
    DateInterval report = new DateInterval(constr);
    monthlyReportPanel = new MonthlyIntervalGuiDetailsPanel(report, viewID);
    tabbedPane.add("Monthly", monthlyReportPanel);

    constr = new FocConstructor(DateIntervalDesc.getInstance(), null);
    report = new DateInterval(constr);
    weeklyReportPanel = new WeeklyIntervalGuiDetailsPanel(report, viewID);
    tabbedPane.add("Weekly", weeklyReportPanel);

    constr = new FocConstructor(DateIntervalDesc.getInstance(), null);
    report = new DateInterval(constr);
    dateReportPanel = new TwoDaysIntervalGuiDetailsPanel(report, viewID);
    tabbedPane.add("Dates Interval", dateReportPanel);

    add(tabbedPane, 0, 0);
  }

  public void dispose() {
    super.dispose();
    monthlyReportPanel = null;
    weeklyReportPanel  = null;
    dateReportPanel    = null;
    tabbedPane         = null;
    dateInterval        = null;
  }

  private MonthlyIntervalGuiDetailsPanel getMonthlyReportPanel() {
    return monthlyReportPanel;
  }

  private WeeklyIntervalGuiDetailsPanel getWeeklyReportPanel() {
    return weeklyReportPanel;
  }

  private TwoDaysIntervalGuiDetailsPanel getDateReportPanel() {
    return dateReportPanel;
  }

  public void setDatesAccordingToSelection(){
    int selectedTabIndex = tabbedPane.getSelectedIndex();
    switch (selectedTabIndex) {
    case TAB_MONTH:
      dateInterval.setPropertyDate(DateIntervalDesc.FLD_FDATE, getMonthlyReportPanel().getFirstDate());
      dateInterval.setPropertyDate(DateIntervalDesc.FLD_LDATE, getMonthlyReportPanel().getLastDate());
      break;
    case TAB_WEEK:
      dateInterval.setPropertyDate(DateIntervalDesc.FLD_FDATE, getWeeklyReportPanel().getFirstDate());
      dateInterval.setPropertyDate(DateIntervalDesc.FLD_LDATE, getWeeklyReportPanel().getLastDate());
      break;
    case TAB_DATES:
      dateInterval.setPropertyDate(DateIntervalDesc.FLD_FDATE, getDateReportPanel().getFirstDate());
      dateInterval.setPropertyDate(DateIntervalDesc.FLD_LDATE, getDateReportPanel().getLastDate());
      break;
    default:
      break;
    }
  }
}
