package b01.fab;

import java.awt.event.ActionEvent;
import java.util.Iterator;

import javax.swing.AbstractAction;

import b01.fab.gui.browse.GuiBrowseDesc;
import b01.fab.gui.browse.GuiBrowseColumnDesc;
import b01.fab.gui.details.GuiDetailsDesc;
import b01.fab.gui.details.GuiDetailsComponentDesc;
import b01.fab.model.filter.FilterDefinition;
import b01.fab.model.filter.FilterDefinitionDesc;
import b01.fab.model.filter.FilterDefinitionGuiBrowsePanel;
import b01.fab.model.filter.FilterFieldDefinitionDesc;
import b01.fab.model.filter.UserDefinedFilterTabbedPanel;
import b01.fab.model.menu.MenuDefinition;
import b01.fab.model.menu.MenuDefinitionDesc;
import b01.fab.model.menu.MenuDefinitionGuiTreePanel;
import b01.fab.model.table.FieldDefinitionDesc;
import b01.fab.model.table.TableDefinition;
import b01.fab.model.table.TableDefinitionDesc;
import b01.fab.parameterSheet.ParameterSheetSelectorDesc;
import b01.foc.Application;
import b01.foc.Globals;
import b01.foc.desc.FocModuleInterface;
import b01.foc.gui.FPanel;
import b01.foc.list.FocLinkSimple;
import b01.foc.list.FocList;
import b01.foc.menu.FMenuAction;
import b01.foc.menu.FMenuItem;
import b01.foc.menu.FMenuList;

public class FabModule implements FocModuleInterface {

	private boolean objectsAlreadyDeclared = false;
	private boolean menueAlreadyDeclared = false;
  private static FabModule fabModule = null;
  
	private FabModule(){
	}
	
	public static FabModule getInstance(){
		if(fabModule == null){
			fabModule = new FabModule();
		}
		return fabModule;
	}
	
	public void declareFocObjects() {
		if(!objectsAlreadyDeclared){
			Application app = Globals.getApp();
	
			app.declaredObjectList_DeclareObject(TableDefinitionDesc.class);
	    app.declaredObjectList_DeclareObject(FieldDefinitionDesc.class);
	    app.declaredObjectList_DeclareObject(MenuDefinitionDesc.class);
	    
	    app.declaredObjectList_DeclareObject(GuiDetailsDesc.class);
	    app.declaredObjectList_DeclareObject(GuiDetailsComponentDesc.class);
	    
	    app.declaredObjectList_DeclareObject(GuiBrowseDesc.class);
	    app.declaredObjectList_DeclareObject(GuiBrowseColumnDesc.class);
	    app.declaredObjectList_DeclareObject(FilterDefinitionDesc.class);
	    app.declaredObjectList_DeclareObject(FilterFieldDefinitionDesc.class);
	    declareUserDefinedTables(app);
	    declareUserDefinedFilters(app);
	    objectsAlreadyDeclared = true;
		}
	}
	
	@SuppressWarnings("unchecked")
	private void declareUserDefinedTables(Application app){
		FocLinkSimple tableDefinitionLink = new FocLinkSimple(TableDefinitionDesc.getInstance());
		FocList tablesList = new FocList(tableDefinitionLink,null);
		tablesList.loadIfNotLoadedFromDB();
		Iterator<TableDefinition> iter = tablesList.focObjectIterator();
		while(iter != null && iter.hasNext()){
			TableDefinition tableDefinition = iter.next();
			DBFocDescDeclaration focDescDeclaration = DBFocDescDeclaration.getDbFocDescDeclaration(tableDefinition);
			app.declaredObjectList_DeclareDescription(focDescDeclaration);
		}
	}
	
	@SuppressWarnings("unchecked")
	private void declareUserDefinedFilters(Application app){
		FocLinkSimple filterDefinitionLink = new FocLinkSimple(FilterDefinitionDesc.getInstance());
		FocList filterDefinitionList = new FocList(filterDefinitionLink);
		filterDefinitionList.loadIfNotLoadedFromDB();
		Iterator<FilterDefinition> iter = filterDefinitionList.focObjectIterator();
		while(iter != null && iter.hasNext()){
			FilterDefinition filterDefinition = iter.next();
			FilterFocDescDeclaration focDescDeclaration = FilterFocDescDeclaration.getFilterFocDescDeclaration(filterDefinition);
			app.declaredObjectList_DeclareDescription(focDescDeclaration);
		}
	}
	
	@SuppressWarnings("serial")
	public void declareMenu(FMenuList rootMenuList) {
		if(!menueAlreadyDeclared){
			FMenuList manageDataModelList = new FMenuList("Custom Application",'a');
			FMenuItem manageDateModelItem = new FMenuItem("Objects / Tables",'O',new AbstractAction(){
				public void actionPerformed(ActionEvent e) {
					FPanel panel = new FocApplicationBuilderGuiDetailsPanel(null,0);
					Globals.getDisplayManager().newInternalFrame(panel);
				}
			});
			manageDataModelList.addMenu(manageDateModelItem);
			
			FMenuItem manageUserMenusItem = new FMenuItem("Menus",'M',new AbstractAction(){
				public void actionPerformed(ActionEvent e) {
					FPanel panel = new MenuDefinitionGuiTreePanel(MenuDefinitionDesc.getList(FocList.LOAD_IF_NEEDED),0);
					Globals.getDisplayManager().newInternalFrame(panel);
				}
			});
			manageDataModelList.addMenu(manageUserMenusItem);
			
			FMenuList manageUserFiltersList = new FMenuList("Filters", 'F');
			
			FMenuItem defineUserFiltersItem = new FMenuItem("Filter Structures", 'S', new AbstractAction(){
				public void actionPerformed(ActionEvent e) {
					FPanel panel = new FilterDefinitionGuiBrowsePanel(null, FilterDefinition.VIEW_ID_DEFAULT);
					Globals.getDisplayManager().newInternalFrame(panel);
				}
			});
			manageUserFiltersList.addMenu(defineUserFiltersItem);
			
			FMenuItem createUserFilterItem = new FMenuItem("Filter Instances", 'C', new AbstractAction(){
	
				public void actionPerformed(ActionEvent e) {
					FPanel panel = new UserDefinedFilterTabbedPanel();
					Globals.getDisplayManager().newInternalFrame(panel);
				}
				
			});
			manageUserFiltersList.addMenu(createUserFilterItem);
			
			manageDataModelList.addMenu(manageUserFiltersList);
			
	    FMenuItem menu = new FMenuItem("Parameter objects / tables", 'P', new FMenuAction(ParameterSheetSelectorDesc.getInstance(), true));
	    manageDataModelList.addMenu(menu);
			
			rootMenuList.addMenu(manageDataModelList);
			declareUserDefinedMenus(rootMenuList);
			menueAlreadyDeclared = true;
		}
	}
	
	public static String getTableDefinitionTableStorageName(){
		return "TABLE_DEFINITION";
	}
	
	public static String getFieldDefinitionTableStorageName(){
		return "FIELD_DEFINITION";
	}
	
	public void declareUserDefinedMenus(FMenuList rootMenuList){
		MenuDefinition.fillUserDefinedMenuList(rootMenuList);
	}
}
