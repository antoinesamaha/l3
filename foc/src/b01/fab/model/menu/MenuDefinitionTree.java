package b01.fab.model.menu;

import b01.foc.event.FocEvent;
import b01.foc.event.FocListener;
import b01.foc.list.FocList;
import b01.foc.property.FObject;
import b01.foc.tree.TreeScanner;
import b01.foc.tree.objectTree.FObjectNode;
import b01.foc.tree.objectTree.FObjectTree;

public class MenuDefinitionTree extends FObjectTree {
	
	public MenuDefinitionTree(FocList list, int viewID){

    super();
    setFatherNodeId(MenuDefinitionDesc.FLD_FATHER_MENU);
    setDisplayFieldId(MenuDefinitionDesc.FLD_NAME);
    growTreeFromFocList(list);
    FocListener listListener = new FocListener(){

			public void dispose() {
			}

			public void focActionPerformed(FocEvent evt) {
				if(evt.getID() == FocEvent.ID_ITEM_ADD || evt.getID() == FocEvent.ID_ITEM_REMOVE){
					lockPropertiesForNoneLeafNodes();
				}
			}
    };
    list.addFocListener(listListener);
    listListener.focActionPerformed(new FocEvent(list, FocEvent.composeId(FocEvent.TYPE_LIST, FocEvent.ID_ITEM_ADD), ""));
	}
	
	private void lockPropertiesForNoneLeafNodes(){
  	scan(new TreeScanner<FObjectNode>(){

			public void afterChildren(FObjectNode node) {
				if(!node.isLeaf()){
					MenuDefinition menuDefinition = (MenuDefinition)node.getObject();
					if(menuDefinition != null){
						FObject prop = (FObject)menuDefinition.getFocProperty(MenuDefinitionDesc.FLD_USER_BROWSE_VIEW_DEFINITION);
						if(prop != null){
							prop.setValueLocked(true);
							prop.setObject(null);
						}
						
						prop = (FObject)menuDefinition.getFocProperty(MenuDefinitionDesc.FLD_TABLE_DEFINITION);
						if(prop != null){
							prop.setValueLocked(true);
							prop.setObject(null);
						}
					}
				}
			}

			public boolean beforChildren(FObjectNode node) {
				return true;
			}
    	
    });
  }
}
