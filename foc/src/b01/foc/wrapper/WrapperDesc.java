package b01.foc.wrapper;

import java.util.ArrayList;

import b01.foc.desc.FocDesc;
import b01.foc.desc.field.FCharField;
import b01.foc.desc.field.FField;
import b01.foc.desc.field.FObjectField;

public abstract class WrapperDesc extends FocDesc {
	
	private WrapperLevel rootLevel = null;
	private int nextWrappedObjectFieldId = -1;
	
	public WrapperDesc(){
		super(Wrapper.class, FocDesc.NOT_DB_RESIDENT, "WRAPER", false);
	}
	
	public void dispose(){
		super.dispose();
		rootLevel = null;
	}
	
	protected void setRootWrapperLevel(WrapperLevel rootWrapperLevel){
		this.rootLevel = rootWrapperLevel;
	}
	
	public WrapperLevel getRootWrapperLevel(){
		return this.rootLevel;
	}
	
	protected void fillFocDesc(){
		fillFocDescWithGeneralFields();
		fillFocDescWithWrappedObjects();
	}
	
	private void fillFocDescWithGeneralFields(){
		FField focFld = new FCharField ("NODE_NAME", "Node Name", FField.FLD_NAME, false, 20);
    addField(focFld);
    setWithObjectTree();
	}
	
	private void fillFocDescWithWrappedObjects(){
		WrapperLevel rootWrapperLevel = getRootWrapperLevel();
		if(rootWrapperLevel != null){
			addLevelToDesc(rootWrapperLevel);
		}
	}
	
	private void addLevelToDesc(WrapperLevel wrapperLevel){
		if(wrapperLevel != null){
			FField field = newObjectField(wrapperLevel.getFocDesc());;
			wrapperLevel.setObjectFieldID(field.getID());
			ArrayList<WrapperLevel> childrenLevelArray = wrapperLevel.getChildrenLevelList();
			if(childrenLevelArray != null){
				for(int i = 0; i < childrenLevelArray.size(); i++){
					WrapperLevel childLevel = childrenLevelArray.get(i);
					if(childLevel != null){
						addLevelToDesc(childLevel);
					}
				}
			}
		}
	}
	
  public FObjectField newObjectField(FocDesc desc){
    int currentFieldID = getNextFieldId();
    FObjectField objField = new FObjectField(desc.getStorageName(), desc.getStorageName(), currentFieldID , false, desc, desc.getStorageName()+"_");
    addField(objField);
    return objField; 
  }
  
  private void initializeNextWrappedObjectFieldId(){
  	int maxFiedlId = 0;
  	int fieldsSize = getFieldsSize();
  	for(int i = 0; i < fieldsSize; i++){
  		FField field = getFieldAt(i);
  		if(field != null){
  			int fieldID = field.getID();
  			if(fieldID > maxFiedlId){
  				maxFiedlId = fieldID;
  			}
  		}
  	}
  	this.nextWrappedObjectFieldId = maxFiedlId + 1;
  }
  
  private int getNextFieldId(){
  	if(nextWrappedObjectFieldId < 0){
  		initializeNextWrappedObjectFieldId();
  	}
  	return nextWrappedObjectFieldId++;
  }

}
