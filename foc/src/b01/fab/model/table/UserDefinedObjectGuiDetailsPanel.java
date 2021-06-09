package b01.fab.model.table;

import java.awt.Component;
import java.util.Iterator;

import javax.swing.border.Border;

import b01.fab.gui.details.GuiDetails;
import b01.fab.gui.details.GuiDetailsComponent;
import b01.foc.desc.FocObject;
import b01.foc.desc.field.FField;
import b01.foc.gui.FGLabel;
import b01.foc.gui.FGTabbedPane;
import b01.foc.gui.FPanel;
import b01.foc.gui.FValidationPanel;
import b01.foc.list.FocList;

@SuppressWarnings("serial")
public class UserDefinedObjectGuiDetailsPanel extends FPanel {
	
	private FocObject focObject = null;
	private GuiDetails detailsViewDefintion = null;
	public UserDefinedObjectGuiDetailsPanel(FocObject focObject, int viewId){
		if(focObject != null){
			GuiDetails detailsViewDefinition = TableDefinition.getDetailsViewDefinitionForFocDescAndViewId(focObject.getThisFocDesc(), viewId);
			this.detailsViewDefintion = detailsViewDefinition;
			init(focObject, detailsViewDefinition);
		}
	}
	
	public UserDefinedObjectGuiDetailsPanel(FocObject focObject, GuiDetails detailsViewDefinition){
		this.detailsViewDefintion = detailsViewDefinition;
		init(focObject, detailsViewDefinition);
	}
	
	public void dispose(){
		super.dispose();
		this.focObject = null;
	}
	
	public void createBorder(){
		if(detailsViewDefintion != null){
			setBorder(detailsViewDefintion.getDescription());
		}
	}
	
	private void init(FocObject focObject, GuiDetails detailsViewDefinition){
		this.focObject = focObject;
		if(detailsViewDefinition != null){
			int viewMode = detailsViewDefinition.getViewMode();
			if(viewMode == GuiDetails.VIEW_MODE_ID_NORMAL){
				init_NORMAL_VIEW(detailsViewDefinition);
			}else{
				init_TABBED_PANEL_VIEW(detailsViewDefinition);
			}
			if(detailsViewDefinition.isShowValidationPanel()){
				FValidationPanel validPanel = showValidationPanel(true);
				if(validPanel != null){
					if(detailsViewDefinition.isAddSubjectToValidationPanel()){
						validPanel.addSubject(focObject);
						validPanel.setValidationType(FValidationPanel.VALIDATION_SAVE_CANCEL);
					}else{
						validPanel.setValidationType(FValidationPanel.VALIDATION_OK);
					}
				}
			}
			setFrameTitle(detailsViewDefinition.getDescription());
			setMainPanelSising(FPanel.FILL_NONE);
		}else{
			init_NO_VIEW_DEFINITION();
			FValidationPanel validPanel = showValidationPanel(true);
			if(validPanel != null){
				validPanel.setValidationType(FValidationPanel.VALIDATION_OK);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private void init_NORMAL_VIEW(GuiDetails detailsViewDefinition){
		FocList guidDetailsFieldList = detailsViewDefinition.getGuiDetailsFieldList();
		Iterator<GuiDetailsComponent> iter = guidDetailsFieldList.focObjectIterator();
		while(iter != null && iter.hasNext()){
			GuiDetailsComponent detailsFieldDefinition = iter.next();
			Component comp = detailsFieldDefinition.getComponentForFocObject(focObject);
			FieldDefinition fieldDefinition = detailsFieldDefinition.getFieldDefinition();
			String title = "";
			boolean withTtile = fieldDefinition !=null && fieldDefinition.getSQLType() != FieldDefinition.TYPE_ID_FLIST_FIELD && (fieldDefinition.getSQLType() != FieldDefinition.TYPE_ID_FOBJECT_FIELD || detailsFieldDefinition.getComponentGuiDetails() == null);
			withTtile = withTtile && fieldDefinition.getSQLType() != FieldDefinition.SQL_TYPE_ID_BOOLEAN;
			if(withTtile){
				FField field = focObject.getThisFocDesc().getFieldByID(fieldDefinition.getID());
				title = field.getTitle();
			}
			add(title, comp, detailsFieldDefinition.getX(), detailsFieldDefinition.getY());
		}
	}
	
	/*@SuppressWarnings("unchecked")
	private void init_TABBED_PANEL_VIEW(GuiDetails detailsViewDefinition){
		FGTabbedPane tabbedPan = new FGTabbedPane();
		FocList guiDetailsFieldList = detailsViewDefinition.getGuiDetailsFieldList();
		
		for(int i=0; i<guiDetailsFieldList.size(); i++){
			GuiDetailsComponent detailsFieldDefinition = (GuiDetailsComponent)guiDetailsFieldList.getFocObject(i);

			FieldDefinition fieldDefinition = detailsFieldDefinition.getFieldDefinition();
			FPanel panel = null;
			if(fieldDefinition != null){
				if(fieldDefinition.getSQLType() == FieldDefinition.TYPE_ID_FOBJECT_FIELD){
					FocObject focObj = focObject.getPropertyObject(fieldDefinition.getID());
					panel = new UserDefinedObjectGuiDetailsPanel(focObj, detailsFieldDefinition.getViewId());
				}
			}else{
				panel = new UserDefinedObjectGuiDetailsPanel(focObject, detailsFieldDefinition.getViewId());
			}
			if(panel != null){
				tabbedPan.add(panel.getFrameTitle(), panel);
			}
		}
		add(tabbedPan, 0, 0);
	}*/
	
	@SuppressWarnings("unchecked")
	private void init_TABBED_PANEL_VIEW(GuiDetails detailsViewDefinition){
		FGTabbedPane tabbedPan = new FGTabbedPane();
		FocList guiDetailsFieldList = detailsViewDefinition.getGuiDetailsFieldList();
		
		for(int i=0; i<guiDetailsFieldList.size(); i++){
			GuiDetailsComponent detailsFieldDefinition = (GuiDetailsComponent)guiDetailsFieldList.getFocObject(i);
			if(detailsFieldDefinition != null){
				FPanel panel = (FPanel) detailsFieldDefinition.getComponentForFocObject(focObject);
				if(panel != null){
					panel.setBorder((Border)null);
					tabbedPan.add(panel.getFrameTitle(), panel);
				}
			}
		}
		add(tabbedPan, 0, 0);
	}
	
	private void init_NO_VIEW_DEFINITION(){
		FGLabel label = new FGLabel("Could not find view definition for object.");
		add(label, 0, 0);
		setFrameTitle("No view definition found");
	}
}
