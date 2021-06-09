package b01.foc.calendar;

import b01.foc.*;
import b01.foc.desc.*;
import b01.foc.menu.FMenuAction;
import b01.foc.menu.FMenuItem;
import b01.foc.menu.FMenuList;

/**
 * @author 01Barmaja
 */
public class CalendarModule extends FocAbstractUniqueModule {

  public CalendarModule() {
  }
  
  public static FMenuItem addMenu(FMenuList list){
    FMenuItem calendarItem = new FMenuItem("Calendar", 'C', new FMenuAction(FCalendarDesc.getInstance(), true));
    list.addMenu(calendarItem);
    return calendarItem;
  }

  @Override
  public void declareFocObjectsOnce() {
    Application app = Globals.getApp();
    app.declaredObjectList_DeclareDescription(FCalendarDesc.class);
    app.declaredObjectList_DeclareDescription(HolidayDesc.class);
  }
  
  public void declare() {
    Application app = Globals.getApp();
    app.declareModule(this);
  }

  public void addApplicationMenu(FMenuList menuList) {
  }

  public void addConfigurationMenu(FMenuList menuList) {
  }

  public void afterAdaptDataModel() {
  }

  public void afterApplicationEntry() {
  }

  public void afterApplicationLogin() {
  }

  public void beforeAdaptDataModel() {
  }

  public void dispose() {
  }
  
  private static CalendarModule module = null;
  public static CalendarModule getInstance(){
    if(module == null){
      module = new CalendarModule();
    }
    return module;
  }
}
