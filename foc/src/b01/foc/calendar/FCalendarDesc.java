package b01.foc.calendar;

import java.util.Calendar;

import b01.foc.desc.FocDesc;
import b01.foc.desc.field.FBoolField;
import b01.foc.desc.field.FCharField;
import b01.foc.desc.field.FField;
import b01.foc.desc.field.FMultipleChoiceField;
import b01.foc.desc.field.FTimeField;
import b01.foc.list.FocList;
import b01.foc.list.FocListOrder;

public class FCalendarDesc extends FocDesc {
	public static final int FLD_NAME                  = 1;
	public static final int FLD_IS_DEFAULT            = 2;
	public static final int FLD_STARTING_DAY_OF_WEEK  = 3;
	public static final int FLD_IS_MONDAY_WORKDAY     = 4;
	public static final int FLD_IS_TUESDAY_WORKDAY    = 5;
	public static final int FLD_IS_WEDNESDAY_WORKDAY  = 6;
	public static final int FLD_IS_THURSDAY_WORKDAY   = 7;
	public static final int FLD_IS_FRIDAY_WORKDAY     = 8;
	public static final int FLD_IS_SATURDAY_WORKDAY   = 9;
	public static final int FLD_IS_SUNDAY_WORKDAY     = 10;
	public static final int FLD_START_TIME            = 11;
	public static final int FLD_END_TIME              = 12;
	public static final int FLD_HOLIDAYS_LIST         = 13;
		
	public FCalendarDesc(){
		super(FCalendar.class, FocDesc.DB_RESIDENT, "Calendar", false);
		setGuiBrowsePanelClass(FCalendarGuiBrowsePanel.class);	
		setGuiDetailsPanelClass(FCalendarGuiDetailsPanel.class);
		
    FField focFld = addReferenceField();
    
    focFld = new FCharField("NAME", "Name", FLD_NAME, false, 30);    
    focFld.setLockValueAfterCreation(true);
    focFld.setMandatory(true);
    addField(focFld);
    
    focFld = new FBoolField ("ISDEFAULT", "isDefault", FLD_IS_DEFAULT, false );    
    //focFld.setLockValueAfterCreation(true);
    focFld.setMandatory(true);
    addField(focFld);
    
    FMultipleChoiceField multiFld = new FMultipleChoiceField("STARTING_DAY_OF_WEEK", "Starting Day Of Week", FLD_STARTING_DAY_OF_WEEK, false, 5);
    multiFld.setSortItems(false);
    multiFld.addChoice(Calendar.SUNDAY, "Sunday");
    multiFld.addChoice(Calendar.MONDAY, "Monday");
    multiFld.addChoice(Calendar.TUESDAY, "Tuesday");
    multiFld.addChoice(Calendar.WEDNESDAY, "Wednesday");
    multiFld.addChoice(Calendar.THURSDAY, "Thursday");
    multiFld.addChoice(Calendar.FRIDAY, "Friday");
    multiFld.addChoice(Calendar.SATURDAY, "Saturday");
    addField(multiFld);
    
    
    focFld = new FBoolField ("ISMONDAY", "Monday", FLD_IS_MONDAY_WORKDAY, false );    
    focFld.setMandatory(true);
    addField(focFld);
    
    focFld = new FBoolField ("ISTUESDAY", "Tuesday", FLD_IS_TUESDAY_WORKDAY, false );    
    focFld.setMandatory(true);
    addField(focFld);
    
    focFld = new FBoolField ("ISWEDNESDAY", "Wednesday", FLD_IS_WEDNESDAY_WORKDAY, false );    
    focFld.setMandatory(true);
    addField(focFld);
    
    focFld = new FBoolField ("ISTHURDAY", "Thursday", FLD_IS_THURSDAY_WORKDAY, false );    
    focFld.setMandatory(true);
    addField(focFld);
    
    focFld = new FBoolField ("ISFRIDAY", "Friday", FLD_IS_FRIDAY_WORKDAY, false );    
    focFld.setMandatory(true);
    addField(focFld);
    
    focFld = new FBoolField ("ISSATURDAY", "Saturday", FLD_IS_SATURDAY_WORKDAY, false );
    focFld.setMandatory(true);
    addField(focFld);
    
    focFld = new FBoolField ("ISSUNDAY", "Sunday", FLD_IS_SUNDAY_WORKDAY, false );
    focFld.setMandatory(true);
    addField(focFld);
    
    FTimeField timeFld = new FTimeField("START_TIME", "Start time", FLD_START_TIME, false);
    addField(timeFld);
//    timeFld.addListener(new FPropertyListener(){
//			public void dispose() {
//			}
//			public void propertyModified(FProperty property) {
//				FTime timeProp = (FTime)property;
//				Time time = timeProp.getTime();
//				long t = time.getTime();
//				b01.foc.Globals.logString("new time"+t);
//		    Calendar calendar = Calendar.getInstance();
//		    TimeZone timeZone = calendar.getTimeZone();
//		    int i = timeZone.getOffset(System.currentTimeMillis());
//				b01.foc.Globals.logString(" time offset"+i);
//
//			}
//    });

    timeFld = new FTimeField("END_TIME", "End time", FLD_END_TIME, false);
    addField(timeFld);

    //FListField listFld = new FListField("HOLIDAYSLIST", "HOLIDAYSList", FLD_HOLIDAYS_LIST, new FocLinkOne2N(this, HolidayDesc.getInstance()));
    //addField(listFld);
	}
	/*
	private static FocLink holidayLink = null;
	public static FocLink getholidayLink(){
		if(holidayLink == null){
			holidayLink = new FocLinkForeignKey(HolidayDesc.getInstance(), HolidayDesc.FLD_FCALENDAR, true);
		}
		return holidayLink;
	}
	*/
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
			FocListOrder order = new FocListOrder(FLD_NAME);
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
  		focDesc = new /*XXX*/FCalendarDesc();
  	}
	  return focDesc;
	}
}
