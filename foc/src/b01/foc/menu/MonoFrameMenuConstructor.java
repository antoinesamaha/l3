/*
 * Created on 23-May-2005
 */
package b01.foc.menu;

import b01.foc.*;
import b01.foc.gui.*;

/**
 * @author 01Barmaja
 */
public class MonoFrameMenuConstructor implements MenuConstructor{
  
  private FPanel panel = null;
  private int y = 0;
  
  public MonoFrameMenuConstructor(){
    panel = new FPanel();
    panel.setMainPanelSising(FPanel.FILL_NONE);
  }
  
  public void showMenu(){
    Globals.getDisplayManager().changePanel(panel);
  }
  
  public void addMenuList(FMenuList menuList, boolean isMainMenu){
    if(!isMainMenu){
      FGLabel label = new FGLabel(menuList.getTitle());
      panel.add(label, 0, y, java.awt.GridBagConstraints.EAST);
    }

    for(int i=0; i<menuList.menuCount(); i++){
      FMenu menu = menuList.getMenu(i);
      if(menu.isList()){
        addMenuList((FMenuList) menu, false);
      }else{
        addMenuItem((FMenuItem) menu);
      }
    }
  }
  
  public void addMenuItem(FMenuItem menuItem){
    FGButton button = new FGButton(menuItem.getTitle());
    button.addActionListener(menuItem.getAction());
    panel.add(button, 1, y++, 1, 1, java.awt.GridBagConstraints.CENTER, java.awt.GridBagConstraints.BOTH);
  }
  
  public void addMainMenu(FMenu menu){
    if(menu != null){
      if(menu.isList()){
        addMenuList((FMenuList)menu, true);
      }else{
        addMenuItem((FMenuItem)menu);
      }
    }
  }  
}
