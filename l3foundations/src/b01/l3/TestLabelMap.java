// LISTENERS
// GET SET
// FOC

/*
 * Created on Jun 5, 2006
 */
package b01.l3;

import java.awt.Color;

import b01.foc.desc.FocConstructor;
import b01.foc.desc.FocDesc;
import b01.foc.desc.FocObject;
import b01.foc.gui.FPanel;
import b01.foc.list.FocLinkSimple;
import b01.foc.list.FocList;
import b01.foc.list.FocListOrder;
import b01.foc.property.FBoolean;
import b01.foc.property.FString;

/**
 * @author 01Barmaja
 */
public class TestLabelMap extends FocObject {
  
	public final static int VIEW_BROWSE_ALL         = 2;
	public final static int VIEW_BROWSE_ALL_NO_EDIT = 3;
	
  public TestLabelMap(FocConstructor constr) {
    super(constr);
    newFocProperties();
  }
  
  public boolean isCalculated(){
  	return getPropertyBoolean(TestLabelMapDesc.FLD_CALCULATED);
  }
  
	public void adjustOnHoldColor(){
    FBoolean onHoldProp = (FBoolean) getFocProperty(TestLabelMapDesc.FLD_ON_HOLD);
    if(onHoldProp != null){
      boolean onHold = onHoldProp.getBoolean();
      if(onHold){
      	onHoldProp.setBackground(Color.GRAY);
      }else{
      	onHoldProp.setBackground(null);
      }
    }
	}
  
  public static String getLisTestLabel(int testCode){
    String res = "";
    FocList list = new FocList(new FocLinkSimple(getFocDesc()));
    for (int i = 0; i < list.size(); i++){
      TestLabelMap tlm = (TestLabelMap)list.getFocObject(i);
      if (tlm.getInstrumentTestCode().equals(testCode)){
        res = String.valueOf(tlm.getLisTestLabel());
        break;
      }
    }
    return res;
  }
  
  public void dispose(){
  	super.dispose();
  }
  
  public void logException(Exception e){
  	b01.foc.Globals.logException(e);
  }
  
  public int getDayPriority(){
    return getPropertyInteger(TestLabelMapDesc.FLD_DAY_PRIORITY);
  }

  public void setDayPriority(int proprity){
    setPropertyInteger(TestLabelMapDesc.FLD_DAY_PRIORITY, proprity);
  }
  
  public int getNightPriority(){
    return getPropertyInteger(TestLabelMapDesc.FLD_NIGHT_PRIORITY);
  }

  public void setNightPriority(int proprity){
    setPropertyInteger(TestLabelMapDesc.FLD_NIGHT_PRIORITY, proprity);
  }
  
  public int getHolidayPriority(){
    return getPropertyInteger(TestLabelMapDesc.FLD_HOLIDAY_PRIORITY);
  }

  public void setHolidayPriority(int proprity){
    setPropertyInteger(TestLabelMapDesc.FLD_HOLIDAY_PRIORITY, proprity);
  }
  
  /* (non-Javadoc)
   * @see java.lang.Runnable#run()
   */

  /* (non-Javadoc)
   * @see b01.l3.MessageListener#messageReceived(b01.l3.data.L3Message)
   */

  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // LISTENERS
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // GET SET
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  
  public String getLisTestLabel() {
  	return getFocProperty(TestLabelMapDesc.FLD_LIS_TEST_LABEL).getString();
  }
  
  public void setLisTestLabel(String name) {
  	FString lbl = (FString) getFocProperty(TestLabelMapDesc.FLD_LIS_TEST_LABEL);
    if(lbl != null){
      lbl.setString(name);
    }
  }

  public Instrument getInstrument() {
  	return (Instrument) getPropertyObject(TestLabelMapDesc.FLD_INSTRUMENT);
  }

  public String getInstrumentTestCode() {
  	return getFocProperty(TestLabelMapDesc.FLD_INSTRUMENT_TEST_CODE).getString();
  }
  
  public void setCode(String cd) {
  	FString cod = (FString) getFocProperty(TestLabelMapDesc.FLD_INSTRUMENT_TEST_CODE);
    if(cod != null){
      cod.setString(cd);
    }
  }

  public String getDescription() {
  	return getFocProperty(TestLabelMapDesc.FLD_TEST_DESCRIPTION).getString();
  }
  
  public void setDescription(String description) {
  	FString des = (FString) getFocProperty(TestLabelMapDesc.FLD_TEST_DESCRIPTION);
    if(des != null){
      des.setString(description);
    }
  }

  @Override
  public void duplicationModification(FocObject source) {
  	setPropertyObject(TestLabelMapDesc.FLD_INSTRUMENT, null);
  	setPropertyInteger(TestLabelMapDesc.FLD_DAY_PRIORITY, source.getPropertyInteger(TestLabelMapDesc.FLD_DAY_PRIORITY) + 1);
  	setPropertyInteger(TestLabelMapDesc.FLD_NIGHT_PRIORITY, source.getPropertyInteger(TestLabelMapDesc.FLD_NIGHT_PRIORITY) + 1);
  	setPropertyInteger(TestLabelMapDesc.FLD_HOLIDAY_PRIORITY, source.getPropertyInteger(TestLabelMapDesc.FLD_HOLIDAY_PRIORITY) + 1);
  	//list.sort();
  	//TestLabelMap.rearrangePriorityList(list);
  }
  
  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // LIST
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

  private static FocListOrder order = null;
  
  public static FocList newFocListLinked(Instrument instrument){
  	if(order == null){
  		order = new FocListOrder(TestLabelMapDesc.FLD_LIS_TEST_LABEL, TestLabelMapDesc.FLD_DAY_PRIORITY);	
  	}
    FocList testMappingList = new FocList(instrument, ((InstrumentDesc)Instrument.getFocDesc()).getInstrumentSupportedTestLink(), null);
    testMappingList.setListOrder(order);
    return testMappingList;
  }
  
  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // FOC
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
	
	@Override
	public FPanel newDetailsPanel(int viewID) {
		return new TestLabelMapGuiDetailsPanel(viewID, this);
	}
	
	public static FPanel newBrowsePanel(FocList list, int viewID) {
		return new TestLabelMapGuiBrowsePanel(list, viewID); 
	}
  
  private static FocDesc focDesc = null;

	public static FocDesc getFocDesc() {
	  if (focDesc == null) {
	  	focDesc = new TestLabelMapDesc();
	  }
	  return focDesc;
	}
}
