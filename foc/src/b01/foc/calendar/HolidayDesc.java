package b01.foc.calendar;

import java.sql.Date;

import b01.foc.desc.FocDesc;
import b01.foc.desc.field.FCharField;
import b01.foc.desc.field.FDateField;
import b01.foc.desc.field.FField;
import b01.foc.desc.field.FObjectField;
import b01.foc.list.FocList;
import b01.foc.list.FocListOrder;
import b01.foc.property.FProperty;
import b01.foc.property.FPropertyListener;

public class HolidayDesc extends FocDesc{

	public static final int FLD_START_DATE           = 1;
  public static final int FLD_END_DATE             = 2;
	public static final int FLD_REASON               = 3;
	public static final int FLD_FCALENDAR            = 4;
		
	public HolidayDesc(){
		super(Holiday.class, FocDesc.DB_RESIDENT, "Holiday", false);
		setGuiBrowsePanelClass(HolidayGuiBrowsePanel.class);	
		//setGuiDetailsPanelClass(HolidayGuiDetailsPanel.class);
		
    FField focFld = addReferenceField();
    
    focFld = new FDateField("HOLIDAY_DATE", "Start Date", FLD_START_DATE,  false);    
    //focFld.setLockValueAfterCreation(true);
    focFld.setMandatory(true);
    focFld.addListener(getDatePropertyListener());
    addField(focFld);
    
    focFld = new FDateField("HOLIDAY_END_DATE", "End Date", FLD_END_DATE,  false);    
    //focFld.setLockValueAfterCreation(true);
    focFld.setMandatory(true);
    addField(focFld);
    
    focFld = new FCharField("REASON", "Holidays", FLD_REASON,  false, 30);    
    //focFld.setLockValueAfterCreation(true);
    focFld.setMandatory(true);
    addField(focFld);
    
    FObjectField objFld = new FObjectField("FCALENDAR", "FCalendar", FLD_FCALENDAR, false, FCalendarDesc.getInstance(), "FCALENDAR_", this, FCalendarDesc.FLD_HOLIDAYS_LIST);
    objFld.setNullValueMode(FObjectField.NULL_VALUE_ALLOWED_AND_SHOWN);
    objFld.setSelectionList(FCalendarDesc.getList(FocList.NONE));
    objFld.setMandatory(true);
    addField(objFld);
	}

  public FPropertyListener getDatePropertyListener(){
    return new FPropertyListener(){
      public void dispose() {
      }

      public void propertyModified(FProperty property) {
        if(property != null && !property.isLastModifiedBySetSQLString()){
          Holiday holidayDate = (Holiday) property.getFocObject();
          Date startDate = holidayDate.getPropertyDate(HolidayDesc.FLD_START_DATE);
          holidayDate.setPropertyDate(HolidayDesc.FLD_END_DATE, startDate);
        }
      }
    };
  }

	//ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // SINGLE LIST
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
	
	private static FocList list = null;
	public static FocList getList(int mode){
		list = getInstance().getList(list, mode);
		list.setDirectlyEditable(true);
		list.setDirectImpactOnDatabase(false);
		if(list.getListOrder() == null){
			FocListOrder order = new FocListOrder(FLD_START_DATE);
			list.setListOrder(order);
		}
		return list;		
	}
	
	//ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // SINGLE INSTANCE
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

  private static FocDesc focDesc = null;
  
  public static FocDesc getInstance() {
  	if (focDesc==null){
  		focDesc = new /*XXX*/HolidayDesc();
  	}
	  return focDesc;
	}
}
