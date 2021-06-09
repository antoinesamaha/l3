package b01.l3;

import b01.foc.desc.FocDesc;
import b01.foc.desc.field.FCharField;
import b01.foc.desc.field.FField;
import b01.foc.list.FocLinkSimple;
import b01.foc.list.FocList;

/**
 * @author 01Barmaja
 */
public class TestGroupDesc extends FocDesc {
  
  public static final int FLD_NAME = 1;
  public static final int FLD_TEST_LABEL_MAP_LIST = 2;
  
  public TestGroupDesc(){
    super(TestGroup.class, FocDesc.DB_RESIDENT, "TEST_GROUP", true);
    setGuiBrowsePanelClass(TestGroupGuiBrowsePanel.class);
    setGuiDetailsPanelClass(TestGroupGuiDetailsPanel.class);

    FField focFld = addReferenceField();
    
    focFld = new FCharField("NAME", "Group name", FLD_NAME, true, FCharField.NAME_LEN);
    focFld.setLockValueAfterCreation(true);     
    addField(focFld);
  }

  private static FocList list = null;
  
  public static FocList getList(){
  	if(list == null){
  		list = new FocList(new FocLinkSimple(getInstance()));
  		list.setDirectlyEditable(true);
  	}
  	return list;
  }
  
  private static TestGroupDesc focDesc = null;
  
  public static FocDesc getInstance(){
  	if(focDesc == null){
  		focDesc = new TestGroupDesc();
  	}
  	return focDesc;
  }
}
