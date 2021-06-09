/*
 * Created on Oct 14, 2004
 */
package b01.foc.currency;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import b01.foc.*;
import b01.foc.desc.*;
import b01.foc.gui.FPanel;
import b01.foc.menu.*;

/**
 * @author 01Barmaja
 */
public class CurrencyModule extends FocAbstractUniqueModule {
	
	private boolean focObjectsAlreadyDeclared = false;
  
  private static CurrencyModule currencyModule = null;
  private static CurrencyModule getInstance(){
  	if(currencyModule == null){
  		currencyModule = new CurrencyModule();
  	}
  	return currencyModule;
  }
  
  public static void includeCurrencyModule(){
  	CurrencyModule.getInstance().declare();
  }
  
  @SuppressWarnings("serial")
	public static FMenuList newConfigurationMenuList(){
    AbstractAction configMenuAction = new AbstractAction(){
      public void actionPerformed(ActionEvent e) {
        FPanel panel = Currencies.getCurrencies().newDetailsPanel(0);
        Globals.getDisplayManager().newInternalFrame(panel);
      }
    };
    
    FMenuList mainMenu = new FMenuList(FocLangKeys.CURR_CURRENCY, "CURRENCY MAIN MENU");
    
    FMenuItem currencies = new FMenuItem(FocLangKeys.CURR_CURRENCIES, "CURRENCY",new FMenuAction(Currency.getFocDesc(), true));
    FMenuItem config = new FMenuItem(FocLangKeys.CURR_CONFIG, "CONFIG CURRENCY",configMenuAction);
    
    mainMenu.addMenu(currencies);
    mainMenu.addMenu(config);
    return mainMenu;
  }
  
  @SuppressWarnings("serial")
	public static FMenuList newRatesMenuList(){
    FMenuList mainMenu = new FMenuList(FocLangKeys.CURR_CURRENCY);
    
    AbstractAction ratesAction = new AbstractAction(){
      public void actionPerformed(ActionEvent e) {
        FPanel panel = DateLine.newBrowsePanel(null, 0);
        Globals.getDisplayManager().newInternalFrame(panel);
      }
    };
    
    FMenuItem currencyRates = new FMenuItem(FocLangKeys.CURR_RATES, ratesAction);
    
    mainMenu.addMenu(currencyRates);
    return mainMenu;
  }

	@Override
	public void declareFocObjectsOnce() {
  	if(!focObjectsAlreadyDeclared){
	    Application app = Globals.getApp();
	    app.declaredObjectList_DeclareObject(Currency.class);
	    app.declaredObjectList_DeclareObject(CurrencyRate.class);
	    app.declaredObjectList_DeclareObject(CurrencyDate.class);
	    app.declaredObjectList_DeclareObject(Currencies.class);
	    focObjectsAlreadyDeclared = true;
  	}
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

	public void declare() {
    Application app = Globals.getApp();
    app.declareModule(this);
    app.setCurrencyModuleIncluded(true);
	}

	public void dispose() {
		
	}
}
