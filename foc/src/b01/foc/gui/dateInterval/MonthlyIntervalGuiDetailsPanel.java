package b01.foc.gui.dateInterval;
 
import java.awt.Component;
import java.sql.Date;

import b01.foc.desc.FocObject;
import b01.foc.gui.FPanel;

@SuppressWarnings("serial")
public class MonthlyIntervalGuiDetailsPanel extends FPanel{

  private DateInterval stockreport = null;
  public MonthlyIntervalGuiDetailsPanel(FocObject object, int viewID){
    super();
    stockreport = (DateInterval)object;
    setName("By Month");
    add(stockreport, DateIntervalDesc.FLD_MONTH, 0, 0);
    add(stockreport, DateIntervalDesc.FLD_YEAR, 2, 0);
    Component comp = add(stockreport, DateIntervalDesc.FLD_FDATE, 0, 1);
    comp.setEnabled(false);
    comp = add(stockreport, DateIntervalDesc.FLD_LDATE, 2, 1);
    comp.setEnabled(false);
  }

  public void dispose(){
    super.dispose();
    stockreport = null;
  }
  
  public DateInterval getStockreport() {
    return stockreport;
  }
  
  protected Date getFirstDate(){
    return stockreport.getPropertyDate(DateIntervalDesc.FLD_FDATE); 
  }
  
  protected Date getLastDate(){
    return stockreport.getPropertyDate(DateIntervalDesc.FLD_LDATE); 
  }
  
}
