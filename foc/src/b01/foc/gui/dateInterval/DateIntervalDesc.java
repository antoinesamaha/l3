package b01.foc.gui.dateInterval;

import java.util.Calendar;

import b01.foc.Globals;
import b01.foc.desc.FocDesc;
import b01.foc.desc.field.FDateField;
import b01.foc.desc.field.FField;
import b01.foc.desc.field.FMultipleChoiceField;
import b01.foc.desc.field.FNumField;
import b01.foc.property.FProperty;
import b01.foc.property.FPropertyListener;

public class DateIntervalDesc extends FocDesc{

  public static final int FLD_FDATE = 1;
  public static final int FLD_LDATE = 2;
  public static final int FLD_WEEK  = 3;
  public static final int FLD_MONTH = 4;
  public static final int FLD_YEAR  = 5;
  
  public DateIntervalDesc() {
    super(DateInterval.class, FocDesc.NOT_DB_RESIDENT, "DATE_INTERVAL", false);
    setGuiDetailsPanelClass(WeeklyIntervalGuiDetailsPanel.class);
    
    FField fField = addReferenceField();
    
    fField = new FDateField("START_DATE", "Start Date", FLD_FDATE, false);
    addField(fField);
    
    fField = new FDateField("END_DATE", "End Date", FLD_LDATE, false);
    addField(fField);
    
    fField = new FNumField("WEEK", "Week", FLD_WEEK, false, 5, 0);    
    fField.addListener(dateListener);
    addField(fField);
    
    FMultipleChoiceField multiFld = new FMultipleChoiceField("MONTH", "Month", FLD_MONTH, false, 5);
    multiFld.setSortItems(false);
    multiFld.addChoice(Calendar.JANUARY, "January");
    multiFld.addChoice(Calendar.FEBRUARY, "February");
    multiFld.addChoice(Calendar.MARCH, "March");
    multiFld.addChoice(Calendar.APRIL, "April");
    multiFld.addChoice(Calendar.MAY, "May");
    multiFld.addChoice(Calendar.JUNE, "June");
    multiFld.addChoice(Calendar.JULY, "July");
    multiFld.addChoice(Calendar.AUGUST, "August");
    multiFld.addChoice(Calendar.SEPTEMBER, "September");
    multiFld.addChoice(Calendar.OCTOBER, "October");
    multiFld.addChoice(Calendar.NOVEMBER, "November");
    multiFld.addChoice(Calendar.DECEMBER, "December");
    multiFld.addListener(dateListener);
    addField(multiFld);
    
    FMultipleChoiceField yearFld = new FMultipleChoiceField("YEAR", "Year", FLD_YEAR, false, 5);
    yearFld.addListener(dateListener);
    yearFld.setSortItems(false);
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(Globals.getApp().getSystemDate());
    for(int i = 0; i <= 16; i++){
      yearFld.addChoice((calendar.get(Calendar.YEAR)-15)+i, ""+((calendar.get(Calendar.YEAR)-15)+i));
    }
    addField(yearFld);
  }

  public FPropertyListener dateListener = new FPropertyListener(){
    public void dispose() {       
    }
    
    public void propertyModified(FProperty property) {
      DateInterval stockReport = (DateInterval) (property != null ? property.getFocObject() : null);
      if(stockReport != null){
        if(stockReport.getPropertyInteger(FLD_WEEK) != 0 && stockReport.getPropertyInteger(FLD_YEAR) != 0){
          stockReport.computeDates();
        }else if (stockReport.getPropertyInteger(FLD_MONTH) != -1 && stockReport.getPropertyInteger(FLD_YEAR) != 0){
          stockReport.setDates();
        }
      }
    }
  };

  private static FocDesc focDesc = null;
   
  public static FocDesc getInstance() {
    if (focDesc==null){
      focDesc = new /*XXX*/DateIntervalDesc();
    }
    return focDesc;
  }
}
