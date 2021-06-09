/*
 * Created on 22-May-2005
 */
package b01.foc.menu;

import java.awt.Component;

import b01.foc.gui.*;
import javax.swing.*;

/**
 * @author 01Barmaja
 */
public class MenuBarConstructor implements MenuConstructor {
  private MainFrame mainFrame = null;
  private JMenuBar menuBar = null;
  private JMenuItem currentMenuItem = null;
  
  public MenuBarConstructor(MainFrame mainFrame, JMenuBar menuBar){
    this.menuBar = menuBar;
    this.mainFrame = mainFrame;
  }
  
  public JMenuBar getMenuBar() {
    return menuBar;
  }
  
  public void setMenuBar(JMenuBar menuBar) {
    this.menuBar = menuBar;
  }
  
  public void addMenuList(FMenuList menuList, boolean isMainMenu){
    if(menuList != null){
      JMenu jMenu = null;
      if(!isMainMenu){      
        String title = menuList.getTitle();
        int mnemonic = menuList.getMnemonic();
        jMenu = new JMenu(title);
        jMenu.setEnabled(menuList.isEnabled());
        //String str = "\u0628";
        //Character c = Character.valueOf('\u0628');
        //Character c = Character.valueOf('?');
        //Globals.logString(c.charValue());
        //String str = String.valueOf(c);
        /*
        try{
          String str2 = new String(str.getBytes("iso8859-1"), "windows-1256");
          Globals.logString(str+ " " + str2);
        }catch(Exception ee){
          Globals.logException(ee);
        }
        */

        //String c_cat_id=new String((request.getParameter("c_cat_id")).getBytes("iso8859-1"),"windows-1256");
        
        //jMenu.setLocale(MultiLanguage.getCurrentLocale());
        //Globals.logString("Font name "+jMenu.getFont().getName());
        //jMenu.setFont(new Font("Arial", Font.PLAIN, 12));
        //jMenu.setText(str);
        //Globals.logString("Font name "+jMenu.getFont().getName() + " "+ str);
        if(mnemonic >= 0) jMenu.setMnemonic(mnemonic);
        addJMenuItem(jMenu);
      }        
      JMenuItem backupCurrentMenuItem = currentMenuItem;
      currentMenuItem = jMenu;
      
      for(int i=0; i<menuList.menuCount(); i++){
        FMenu menu = menuList.getMenu(i);
        if(menu.isList()){
          addMenuList((FMenuList) menu, false);
        }else{
          addMenuItem((FMenuItem) menu);
        }
      }
      
      currentMenuItem = backupCurrentMenuItem;
    }
  }
  
  public void addMenuItem(FMenuItem menuItem){
    if(menuItem != null){
      String title = menuItem.getTitle();
      int mnemonic = menuItem.getMnemonic();
      
      if(menuItem.isSeparator()){
      	JSeparator jMenu = new JSeparator();
      	addJMenuItem(jMenu);
      }else{
	      JMenuItem jMenu = new JMenuItem(title);
	      jMenu.setName("MENU."+title);
	      jMenu.setEnabled(menuItem.isEnabled());
	      if(mnemonic >= 0) jMenu.setMnemonic(mnemonic);
	      addJMenuItem(jMenu);
	      jMenu.addActionListener(menuItem.getAction());
      }
    }
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
  
  private void addJMenuItem(Component jMenuItem){
    if(currentMenuItem != null){
      currentMenuItem.add(jMenuItem);
    }else{
      menuBar.add(jMenuItem);
    }
  }
  
  public void showMenu(){
    mainFrame.setJMenuBar(menuBar);
    mainFrame.setVisible(false);
    mainFrame.setVisible(true);
    //Globals.getDisplayManager().newInternalFrame(new FPanel());
    //Globals.getDisplayManager().goBackDontCheckDialogs();    
  }
}
