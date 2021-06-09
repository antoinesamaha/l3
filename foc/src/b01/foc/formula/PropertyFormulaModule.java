package b01.foc.formula;

import java.awt.event.ActionListener;
import javax.swing.JMenuItem;
import b01.foc.*;
import b01.foc.desc.*;
import b01.foc.gui.FPopupMenu;
import b01.foc.menu.FMenuList;

/**
 * @author 01Barmaja
 */
public class PropertyFormulaModule extends FocAbstractUniqueModule {

  public PropertyFormulaModule() {
  }
  
  public static JMenuItem addPopUpMenu(FPopupMenu popupMenu, ActionListener listener){
    
    JMenuItem fieldFormulas = new JMenuItem("Field Formulas");
    fieldFormulas.addActionListener(listener);
    
    popupMenu.add(fieldFormulas);
    return fieldFormulas;
  }

  @Override
  public void declareFocObjectsOnce() {
    Application app = Globals.getApp();
    app.declaredObjectList_DeclareDescription(PropertyFormulaDesc.class);
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
  
  private static PropertyFormulaModule module = null;
  public static PropertyFormulaModule getInstance(){
    if(module == null){
      module = new PropertyFormulaModule();
    }
    return module;
  }
}
