package b01.fab.model.menu;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import javax.swing.AbstractAction;
import b01.fab.gui.browse.GuiBrowse;
import b01.fab.gui.details.GuiDetails;
import b01.fab.model.table.TableDefinition;
import b01.fab.model.table.UserDefinedObjectGuiBrowsePanel;
import b01.fab.model.table.UserDefinedObjectGuiDetailsPanel;
import b01.foc.Globals;
import b01.foc.desc.FocConstructor;
import b01.foc.desc.FocDesc;
import b01.foc.desc.FocObject;
import b01.foc.gui.FPanel;
import b01.foc.list.FocLinkSimple;
import b01.foc.list.FocList;
import b01.foc.menu.FMenu;
import b01.foc.menu.FMenuItem;
import b01.foc.menu.FMenuList;
import b01.foc.tree.FNode;
import b01.foc.tree.TreeScanner;

public class MenuDefinition extends FocObject {
	
	public static final int VIEW_DEFAULT = 1;
	public static final String USER_DEFINED_MENU_LIST_TITLE = "User menus";
	private AbstractAction abstractAction = null;
	
	public MenuDefinition(FocConstructor constr){
		super(constr);
		newFocProperties();
	}
	
	public void dispose(){
		super.dispose();
		this.abstractAction = null;
	}
	
	public String getName(){
		return getPropertyString(MenuDefinitionDesc.FLD_NAME);
	}
	
	public void setName(String name){
		setPropertyString(MenuDefinitionDesc.FLD_NAME, name);
	}
	
	public MenuDefinition getFatherMenu(){
		return (MenuDefinition)getPropertyObject(MenuDefinitionDesc.FLD_FATHER_MENU);
	}
	
	public void setFatherMenu(MenuDefinition menuDefinition){
		setPropertyObject(MenuDefinitionDesc.FLD_FATHER_MENU,menuDefinition);
	}
	
	public void setTableDefinition(TableDefinition tableDefinition){
		setPropertyObject(MenuDefinitionDesc.FLD_TABLE_DEFINITION, tableDefinition);
	}
	
	public TableDefinition getTableDefinition(){
		return (TableDefinition) getPropertyObject(MenuDefinitionDesc.FLD_TABLE_DEFINITION);
	}
	
	public void setUserBrowseViewDefinition(GuiBrowse definition){
		setPropertyObject(MenuDefinitionDesc.FLD_USER_BROWSE_VIEW_DEFINITION, definition);
	}
	
	public GuiBrowse getBrowseViewDefinition(){
		return (GuiBrowse)getPropertyObject(MenuDefinitionDesc.FLD_USER_BROWSE_VIEW_DEFINITION);
	}
	
	public void setDetailsViewDefinition(GuiDetails details){
		setPropertyObject(MenuDefinitionDesc.FLD_USER_DETAILS_VIEW_DEFINITION, details);
	}
	
	public GuiDetails getDetailsViewDefinition(){
		return (GuiDetails) getPropertyObject(MenuDefinitionDesc.FLD_USER_DETAILS_VIEW_DEFINITION);
	}
	
	@SuppressWarnings("serial")
	public AbstractAction getAbstractAction(){
		if(abstractAction == null){
			abstractAction = new AbstractAction(){
				public void actionPerformed(ActionEvent e) {
					TableDefinition tableDefinition = MenuDefinition.this.getTableDefinition();
					if(tableDefinition != null){
						FocDesc desc = Globals.getApp().getFocDescByName(tableDefinition.getName());
						if(desc != null){
							FocLinkSimple linkSimple = new FocLinkSimple(desc);
							FocList list = new FocList(linkSimple);
							list.loadIfNotLoadedFromDB();
							FPanel panel = null;
							if(tableDefinition.isSingleInstance()){
								FocObject focObjectToDisplay = null;
								if(list.size() > 1){
									Globals.getDisplayManager().popupMessage("Waring mulitple row found in DB for a static instance");
									focObjectToDisplay = list.getFocObject(0);
								}else if(list.size() == 1){
									focObjectToDisplay = list.getFocObject(0);
								}else if(list.size() == 0){
									focObjectToDisplay = list.newEmptyItem();
									focObjectToDisplay.forceControler(true);
									focObjectToDisplay.validate(false);
								}
								if(focObjectToDisplay != null){
									//panel = new UserDefinedObjectGuiDetailsPanel(focObjectToDisplay, MenuDefinition.this.getDetailsViewDefinition().getViewId());
									panel = new UserDefinedObjectGuiDetailsPanel(focObjectToDisplay, MenuDefinition.this.getDetailsViewDefinition().getReference().getInteger());
								}
							}else{
								//panel = new UserDefinedObjectGuiBrowsePanel(list, MenuDefinition.this.getBrowseViewDefinition().getViewId());
								panel = new UserDefinedObjectGuiBrowsePanel(list, MenuDefinition.this.getBrowseViewDefinition().getReference().getInteger());
							}
							if(panel != null){
								Globals.getDisplayManager().newInternalFrame(panel);
							}else{
								Globals.getDisplayManager().popupMessage("No panel to display. \n " +
										"Please selcte a view definition for this menu");
							}
						}
					}
				}
			};
		}
		return abstractAction;
	}
	
	//Old methode
	/*public static void fillUserDefinedMenuList(FMenuList rootMenuList){
		FocList menuDefinitionList = MenuDefinitionDesc.getList(FocList.LOAD_IF_NEEDED);
		if(menuDefinitionList.size() > 0){
			MenuDefinitionTree menuDefinitionTree = new MenuDefinitionTree(menuDefinitionList, MenuDefinition.DEFAULT_VIEW_ID);
			FObjectRootNode rootNode = (FObjectRootNode)menuDefinitionTree.getRoot();
			rootNode.setTitle(MenuDefinition.USER_DEFINED_MENU_LIST_TITLE);
			fillFatherMenuFromNode(rootNode, rootMenuList);
		}
	}
	
	private static void fillFatherMenuFromNode(FNode node, FMenuList fatherMenu){
		if(node != null){
			FMenu menu = null;
			if(node.isLeaf()){
				MenuDefinition menuDefinition = (MenuDefinition)node.getObject();
				menu = new FMenuItem(node.getTitle(), ' ', menuDefinition != null ? menuDefinition.getAbstractAction() : null);
			}else{
				menu = new FMenuList(node.getTitle(), ' ');
				for(int i = 0; i < node.getChildCount(); i++){
					FNode childNode = node.getChildAt(i);
					fillFatherMenuFromNode(childNode, (FMenuList)menu);
				}
			}
			menu.setMnemonic(fatherMenu.getMnemonicForMenu(menu));
			fatherMenu.addMenu(menu);
		}
	}*/
	
	public static void fillUserDefinedMenuList(FMenuList rootMenuList){
		FocList menuDefinitionList = MenuDefinitionDesc.getList(FocList.LOAD_IF_NEEDED);
		if(menuDefinitionList.size() > 0){
			MenuDefinitionTree menuDefinitionTree = new MenuDefinitionTree(menuDefinitionList, MenuDefinition.DEFAULT_VIEW_ID);
			
			menuDefinitionTree.scan(new MenuDefinitionTreeScaner(rootMenuList));
		}
	}
	
	private static class MenuDefinitionTreeScaner implements TreeScanner<FNode>{
		private ArrayList<FMenu> nodesArray = null;
		
		public MenuDefinitionTreeScaner(FMenuList rootMenuList){
			nodesArray = new ArrayList<FMenu>();
			nodesArray.add(rootMenuList);
		}
		
		private void addMenuToArray(FMenu menu){
			nodesArray.add(menu);
		}
		
		private FMenu getCurrentFatherMenu(){
			return nodesArray.get(nodesArray.size() - 1);
		}
		
		public void afterChildren(FNode node){
			if(!node.isRoot()){
				nodesArray.remove(nodesArray.size() - 1);
			}
		}

		public boolean beforChildren(FNode node) {
			if(!node.isRoot()){
				FMenu menu = null;
				if(node.isLeaf()){
					MenuDefinition menuDefinition = (MenuDefinition)node.getObject();
					menu = new FMenuItem(node.getTitle(), ' ', menuDefinition != null ? menuDefinition.getAbstractAction() : null);
				}else{
					menu = new FMenuList(node.getTitle(), ' ');
				}
				menu.setMnemonic(((FMenuList)getCurrentFatherMenu()).getMnemonicForMenu(menu));
				((FMenuList)getCurrentFatherMenu()).addMenu(menu);
				addMenuToArray(menu);
			}
			return true;
		}
	}
}
