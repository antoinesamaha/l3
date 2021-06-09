package b01.foc.desc;

import b01.foc.menu.FMenuList;

/**
 * @author 01Barmaja
 */
public interface FocUniqueModule extends FocModuleInterface{
  public abstract void declare();
  public abstract void dispose();
  public abstract void afterApplicationLogin();
  public abstract void afterApplicationEntry();  
  public abstract void beforeAdaptDataModel();
  public abstract void afterAdaptDataModel();
  public abstract void addConfigurationMenu(FMenuList menuList);
  public abstract void addApplicationMenu(FMenuList menuList);  
}
