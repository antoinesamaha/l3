/*
 * Created on Dec 5, 2007
 */
package b01.l3;

import b01.foc.desc.FocConstructor;
import b01.foc.desc.FocObject;
import b01.foc.list.FocList;
import b01.foc.property.FString;

/**
 * @author 01Barmaja
 */
public class TestGroup extends FocObject {

  public TestGroup(FocConstructor constr) {
    super(constr);
    newFocProperties();
  }
  
  public String getName() {
  	FString name = (FString) getFocProperty(PoolKernelDesc.FLD_NAME);
    return (name != null) ? name.getString() :"";
  }
  
  public void dispose(){
  	super.dispose();
  }

  public FocList getTestLabelList(){
  	FocList list = getPropertyList(TestGroupDesc.FLD_TEST_LABEL_MAP_LIST);
  	list.loadIfNotLoadedFromDB();
  	return list;
  }
}