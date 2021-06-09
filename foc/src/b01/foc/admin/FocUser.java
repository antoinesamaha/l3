// INSTANCE
//    MAIN
//    PANEL
// LIST
// LOGIN
// DESCRIPTION

/*
 * Created on 20-May-2005
 */
package b01.foc.admin;

import b01.foc.*;
import b01.foc.db.*;
import b01.foc.desc.*;
import b01.foc.desc.field.*;
import b01.foc.gui.*;
import b01.foc.list.*;
import b01.foc.property.*;

import java.util.*;
import java.awt.*;
import java.awt.image.BufferedImage;

import javax.swing.*;

/**
 * @author 01Barmaja
 */
public class FocUser extends FocObject {

  public static final int LOGIN_VIEW_ID = 2;
  public static final int SET_PASSWORD_VIEW_ID = 3;
  public static final int CHANGE_PASSWORD_VIEW_ID = 4;
  public static final int CHANGE_LANGUAGE_VIEW_ID = 5;
  public static final int USER_GROUP_INFO_VIEW_ID = 6;

  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // INSTANCE
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

  // ---------------------------------
  //    MAIN
  // ---------------------------------

  //private static FocGroup defaultGroup = null;

  public FocUser(FocConstructor constr) {
    super(constr);

    //COMMENTAIRE
    /*
     FocConstructor grpConstr = new FocConstructor(FocGroup.getFocDesc(), constr, this);
     FocGroup defaultGroup = null;
     FocList groupList = FocGroup.getList(FocList.LOAD_IF_NEEDED);
     Iterator iter = groupList.focObjectIterator();
     while(iter != null && iter.hasNext()){
     defaultGroup = (FocGroup) iter.next();
     break;
     }
     */
    //COMMENTAIRE
    
    newFocProperties();
    //new FString(this, FocUserDesc.FLD_NAME, "");
    //new FPassword(this, FocUserDesc.FLD_PASSWORD, "");
    //new FMultipleChoice(this, FocUserDesc.FLD_FONT_SIZE, 0);
    //new FObject(this, FocUserDesc.FLD_GROUP, null/*FocGroup.getAnyGroup()*/, FocGroupDesc.FLD_NAME);
    /*if (MultiLanguage.isMultiLanguage()) {
      new FMultipleChoice(this, FocUserDesc.FLD_LANGUAGE, MultiLanguage.getCurrentLanguage().getId());
    }*/
    setPropertyString(FocUserDesc.FLD_NAME, "");
    setPropertyString(FocUserDesc.FLD_PASSWORD, "");
    setPropertyMultiChoice(FocUserDesc.FLD_FONT_SIZE, 0);
    //setPropertyObject(FocUserDesc.FLD_GROUP, FocGroupDesc.FLD_NAME);
    setPropertyMultiChoice(FocUserDesc.FLD_LANGUAGE, MultiLanguage.getCurrentLanguage().getId());
  }

  public String getName() {
    FString pName = (FString) getFocProperty(FocUserDesc.FLD_NAME);
    return pName.getString();
  }

  public void setName(String name) {
    FString pName = (FString) getFocProperty(FocUserDesc.FLD_NAME);
    pName.setString(name);
  }

  public String getPassword() {
    FPassword pPass = (FPassword) getFocProperty(FocUserDesc.FLD_PASSWORD);
    return pPass.getString();
  }

  public void setPassword(String password) {
    FPassword pPass = (FPassword) getFocProperty(FocUserDesc.FLD_PASSWORD);
    pPass.setString(password);
  }

  public FocGroup getGroup() {
    FObject pGroup = (FObject) getFocProperty(FocUserDesc.FLD_GROUP);
    return pGroup != null ? (FocGroup) pGroup.getObject_CreateIfNeeded() : null;
  }

  public FocObject getAppGroup() {
    FocGroup group = getGroup();
    return group != null ? group.getAppGroup() : null;
  }

  public int getFontSize() {
    int size = ConfigInfo.getFontSize();
    FMultipleChoice choice = (FMultipleChoice) getFocProperty(FocUserDesc.FLD_FONT_SIZE);
    if (choice != null) {
      size = choice.getInteger();
    }
    return size;
  }

  public int getRightsLevel() {
    int lvl = 1;
    FocGroup group = getGroup();
    if (group != null) {
      lvl = group.getRightsLevel();
    }
    return lvl;
  }

  // ---------------------------------
  //    PANEL
  // ---------------------------------

  public static void userLoginCheck(String userName, String encryptedPassword) {
    FocUser dbUser = FocUser.findUser(userName);
    String typedPassword = encryptedPassword;
    if (dbUser != null && typedPassword.startsWith(dbUser.getPassword())) {
      Globals.getApp().setUser(dbUser);
      if (dbUser.getName().compareTo(AdminModule.ADMIN_USER) == 0) {
        Globals.getApp().setLoginStatus(Application.LOGIN_ADMIN);
      } else {
        Globals.getApp().setLoginStatus(Application.LOGIN_VALID);
      }
    } else {
      Globals.getApp().setLoginStatus(Application.LOGIN_WRONG);
    }
  }

  /*public FPanel newDetailsPanel(int viewID) {
    FPanel panel = new FocUserGuiDetailsPanel(this, viewID);
    return panel;
  }*/

  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // LIST
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

  public static FocList list = null;

  public static FocList newList() {
    FocLink link = new FocLinkSimple(focDesc);
    list = new FocList(link);
    return list;
  }

  public static FocList getList(int mode) {
    if (list == null) {
      list = newList();
      FocListOrder listOrder = new FocListOrder();
      listOrder.addField(FFieldPath.newFieldPath(FocUserDesc.FLD_NAME));
      list.setListOrder(listOrder);
      //list.setDirectImpactOnDatabase(true);      
    }

    if (mode == FocList.FORCE_RELOAD) {
      list.reloadFromDB();
    } else if (mode == FocList.LOAD_IF_NEEDED) {
      list.loadIfNotLoadedFromDB();
    }

    if (mode != FocList.NONE) {
      FocUser user = (FocUser) list.searchByProperyStringValue(FocUserDesc.FLD_NAME, AdminModule.ADMIN_USER);
      if (user != null) {
        list.remove(user);
        list.removeSubject(user);
      }
    }
    return list;
  }

  public static FocList getList() {
    return getList(FocList.LOAD_IF_NEEDED);
  }

  public static void printDebug(FocList list) {
    if (list != null) {
      Globals.logString("Users list ......");
      Iterator iter = list.focObjectIterator();
      while (iter != null && iter.hasNext()) {
        FocUser user = (FocUser) iter.next();
        FObject groupProp = (FObject) user.getFocProperty(FocUserDesc.FLD_GROUP);

        FocGroup group = user.getGroup();
        Globals.logString("User " + user.getName() + " group " + groupProp.getLocalReferenceToString() + " " + group.toString_Super() + " " + groupProp.getLocalReferenceInt() + " " + group.getReference().getInteger() + " " + group.getName());
      }
    }
  }

  public static FocUser newUser(String name) {
    FocUser focUser = new FocUser(new FocConstructor(FocUser.getFocDesc(), null, null));
    focUser.setName(name);
    return focUser;
  }

  public static FocUser findUser(String name) {
    FocUser dbUser = null;
    FocUser focUser = newUser(name);
    FocLinkSimple link = new FocLinkSimple(FocUser.getFocDesc());
    SQLFilter filter = new SQLFilter(focUser, SQLFilter.FILTER_ON_KEY);
    FocList list = new FocList(null, link, filter);
    list.loadIfNotLoadedFromDB();
    Iterator iter = list.focObjectIterator();
    if (iter != null && iter.hasNext()) {
      dbUser = (FocUser) iter.next();
      //This line works arround a Bug that does not fetch the right group
      FocGroup group = dbUser.getGroup();
    }
    return dbUser;
  }

  public static void deleteAdminUser() {
    SQLFilter filter = new SQLFilter(newUser(AdminModule.ADMIN_USER), SQLFilter.FILTER_ON_KEY);
    SQLDelete del = new SQLDelete(FocUser.getFocDesc(), filter);
    del.execute();
  }

  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // LOGIN
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

  private static FPanel logPanel = null;

  private static boolean firstTime = true;

  @SuppressWarnings("serial")
  public static FPanel getLoginPanel() {

    JPanel jPanel = new JPanel() {

      public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (firstTime) {
          logPanel.refreshFocus();
          firstTime = false;
        }

        /*        if(logPanel == null){
         FocUser user = new FocUser(new FocConstructor(FocUser.getFocDesc(), null, null));
         user.setDbResident(false);
         logPanel = user.newDetailsPanel(LOGIN_VIEW_ID);
         }
         */
        BufferedImage backgroundImage = Globals.getIcons().getClientLogoImage();//getBackgroundImage();
        //if(backgroundImage != null) g.drawImage(backgroundImage, 0, 0, backgroundImage.getWidth(), backgroundImage.getHeight(), this);
        Dimension dim = Globals.getDisplayManager().getNavigator().getViewportDimension();
        if (backgroundImage != null)
          g.drawImage(backgroundImage, 0, 0, (int) dim.getWidth(), (int) dim.getHeight(), this);

        //logPanel.paint(g);
        /*
         Dimension dim = logPanel.getPreferredSize();
         Point pt = Globals.getIcons().getTopLeftPointCenteredInBackground(dim);        	
         //g.translate(-pt.x, -pt.y);
         logPanel.setBounds(10, 10, 1000, 1000);
         
         g.translate(200, 200);
         //g.copyArea(x, y, width, height, dx, dy)
         //g.setClip(0, 0, 1000, 1000);
         logPanel.update(g);
         logPanel.paint(g);
         */
      }
    };

    jPanel.setLayout(new GridBagLayout());

    GridBagConstraints c = new GridBagConstraints();

    c.gridwidth = 1;
    c.gridheight = 1;
    c.insets.bottom = 0;
    c.insets.top = 0;
    c.insets.left = 0;
    c.insets.right = 0;
    c.weightx = 0.99;
    c.weighty = 0.99;
    c.fill = GridBagConstraints.NONE;
    c.anchor = GridBagConstraints.NORTHWEST;

    c.gridx = 0;
    c.gridy = 0;

    FocConstructor constr = new FocConstructor(FocUser.getFocDesc(), null, null);
    FocUser user = (FocUser) constr.newItem();
    user.setDbResident(false);
    logPanel = user.newDetailsPanel(LOGIN_VIEW_ID);

    //logPanel.add(new JTextField(30), 0, 0);

    //JLabel label = new JLabel("TEST TEST");
    //label.setOpaque(false);
    //logPanel.add(label, 0, 1);

    //logPanel.setOpaque(false);
    //logPanel.setBackground(null);

    jPanel.add(new JPanel() {

      Dimension dim = new Dimension(550, 50);

      public void paintComponent(Graphics g) {
        //super.paintComponent(g);
      }

      @Override
      public Dimension getPreferredSize() {
        return dim;
      }

    }, c);

    c.gridx = 1;
    c.gridy = 1;
    jPanel.add(logPanel, c);

    c.gridx = 2;
    c.gridy = 2;
    jPanel.add(new JPanel() {
      Dimension dim = new Dimension(100, 100);

      public void paintComponent(Graphics g) {
        //super.paintComponent(g);
      }

      @Override
      public Dimension getPreferredSize() {
        return dim;
      }
    }, c);

    //    Dimension dim = logPanel.getPreferredSize();
    //    Point pt = Globals.getIcons().getTopLeftPointCenteredInBackground(dim);
    //    logPanel.setBounds(pt.x, pt.y, (int)dim.getWidth(), (int)dim.getHeight());
    //    jPanel.add(logPanel);
    //    logPanel.setBounds(pt.x, pt.y, (int)dim.getWidth(), (int)dim.getHeight());

    FPanel mainPanel = new FPanel();
    mainPanel.add(jPanel, 0, 0, 1, 1, 0.9, 0.9, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH);

    return mainPanel;
  }

  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // DESCRIPTION
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

  private static FocDesc focDesc = null;

  public static final String DB_TABLE_NAME = FocUserDesc.DB_TABLE_NAME;

  public static final String FLDNAME_NAME = FocUserDesc.FLDNAME_NAME;

  public static final int FLD_NAME = FocUserDesc.FLD_NAME;

  public static final int FLD_PASSWORD = FocUserDesc.FLD_PASSWORD;

  public static final int FLD_GROUP = FocUserDesc.FLD_GROUP;

  public static final int FLD_LANGUAGE = FocUserDesc.FLD_LANGUAGE;

  public static final int FLD_FONT_SIZE = FocUserDesc.FLD_FONT_SIZE;

  public static FocDesc getFocDesc() {
    if (focDesc == null) {
      focDesc = new FocUserDesc();
    }
    return focDesc;
  }
}
