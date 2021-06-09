package b01.foc.gui.dateInterval;

import java.sql.Date;

import b01.foc.desc.FocObject;
import b01.foc.gui.FPanel;

@SuppressWarnings("serial")
public class TwoDaysIntervalGuiDetailsPanel extends FPanel{

  private DateInterval stockreport = null;
  
  public TwoDaysIntervalGuiDetailsPanel(FocObject object, int viewID){
    super();
    stockreport = (DateInterval) object;
    setName("By Day");
    add(stockreport, DateIntervalDesc.FLD_FDATE, 0, 0);
    add(stockreport, DateIntervalDesc.FLD_LDATE, 2, 0);
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
