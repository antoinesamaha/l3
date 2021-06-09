package b01.foc.calendar;

import java.sql.Date;

import b01.foc.desc.FocConstructor;
import b01.foc.desc.FocObject;

public class Holiday extends FocObject {

	public Holiday(FocConstructor constr){
	  super(constr);
	  newFocProperties();
	} 
	
	public void setStartDate(Date date){
		setPropertyDate(HolidayDesc.FLD_START_DATE, date);
	}
	
	public Date getStartDate(){
		return getPropertyDate(HolidayDesc.FLD_START_DATE);
	}

	public void setEndDate(Date date){
		setPropertyDate(HolidayDesc.FLD_END_DATE, date);
	}
	
	public Date getEndDate(){
		return getPropertyDate(HolidayDesc.FLD_END_DATE);
	}	
	
	public void setReason(String reason){
		setPropertyString(HolidayDesc.FLD_REASON, reason);
	}
	
	public String getReason(){
		return getPropertyString(HolidayDesc.FLD_REASON);
	}
}
