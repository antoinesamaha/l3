/*
 * Created on Jun 5, 2006
 */
package b01.l3;

import b01.foc.desc.FocDesc;
import b01.foc.desc.FocObject;
import b01.foc.desc.field.FBoolField;
import b01.foc.desc.field.FCharField;
import b01.foc.desc.field.FField;
import b01.foc.desc.field.FIntField;
import b01.foc.desc.field.FObjectField;
import b01.foc.list.FocList;
import b01.foc.list.FocListOrder;
import b01.foc.property.FProperty;
import b01.foc.property.FPropertyListener;
import b01.l3.data.L3TestDesc;

/**
 * @author 01Barmaja
 */
public class TestLabelMapDesc extends FocDesc {

	public static final int FLD_LIS_TEST_LABEL       = 1;
	public static final int FLD_INSTRUMENT_TEST_CODE = 2;
	public static final int FLD_TEST_DESCRIPTION     = 3;
	public static final int FLD_INSTRUMENT           = 4;
	public static final int FLD_DAY_PRIORITY         = 5;
	public static final int FLD_NIGHT_PRIORITY       = 6;
	public static final int FLD_HOLIDAY_PRIORITY     = 7;
	public static final int FLD_CALCULATED           = 8;
	public static final int FLD_ON_HOLD              = 9;
	public static final int FLD_TEST_GROUP           =10;
	
	private FocList list = null;
	
	public TestLabelMapDesc(){
		super(TestLabelMap.class, FocDesc.DB_RESIDENT, "TEST_LABEL_MAP", false);

    FField focFld = addReferenceField();
    
    focFld = new FCharField("LIS_TEST_LABEL", "LIS test label", FLD_LIS_TEST_LABEL, true, 30);
    addField(focFld);	    
    focFld = new FCharField("INSTRUMENT_TEST_CODE", "Instrument test code",FLD_INSTRUMENT_TEST_CODE , false, L3TestDesc.LEN_TEST_LABEL);
    addField(focFld);
    focFld = new FCharField("DESCRIP", "Test description",FLD_TEST_DESCRIPTION, false, L3TestDesc.LEN_TEST_DESCRIPTION);
    addField(focFld);
    
    FObjectField objField = new FObjectField("INSTRUMENT", "Instrument", FLD_INSTRUMENT, false, Instrument.getFocDesc(), "M_");
    objField.setComboBoxCellEditor(InstrumentDesc.FLD_CODE);
    objField.setSelectionList(Instrument.getList(FocList.NONE));
    objField.setNullValueMode(FObjectField.NULL_VALUE_ALLOWED_AND_SHOWN);
    addField(objField);

    FIntField intFld = new FIntField("DAY_PRIORITY", "Day", FLD_DAY_PRIORITY, false, 5);
    addField(intFld);

    intFld = new FIntField("NIGHT_PRIORITY", "Night", FLD_NIGHT_PRIORITY, false, 7);
    addField(intFld);

    intFld = new FIntField("HOLIDAY_PRIORITY", "Holiday", FLD_HOLIDAY_PRIORITY, false, 7);
    addField(intFld);
    
    focFld = new FBoolField("CALCULATED", "Calculated", FLD_CALCULATED, false);
    addField(focFld);
    
    focFld = new FBoolField("ON_HOLD", "On hold", FLD_ON_HOLD, false);
    addField(focFld);
    focFld.addListener(new FPropertyListener(){
			public void dispose() {
			}

			public void propertyModified(FProperty property) {
				TestLabelMap testLabelMap = (TestLabelMap) property.getFocObject();
				if(testLabelMap != null){
					testLabelMap.adjustOnHoldColor();
				}
			}
    });
    
    FObjectField testGroupFld = new FObjectField("GROUP", "Group", FLD_TEST_GROUP, false, TestGroupDesc.getInstance(), "GRP_", this, TestGroupDesc.FLD_TEST_LABEL_MAP_LIST);
    testGroupFld.setNullValueMode(FObjectField.NULL_VALUE_ALLOWED_AND_SHOWN);
    testGroupFld.setComboBoxCellEditor(TestGroupDesc.FLD_NAME);
    testGroupFld.setSelectionList(TestGroupDesc.getList());
    testGroupFld.setDisplayNullValues(false);
    testGroupFld.setNullValueDisplayString("");
    addField(testGroupFld);
	}
	
	public FocList getList(int mode){
		if(list == null){
			//FocListOrder order = new FocListOrder(FLD_LIS_TEST_LABEL, FLD_DAY_PRIORITY);
			FocListOrder order = new FocListOrder(){
				@Override
				public int compareFocObject(FocObject focObj, FocObject otherFocObj) {
					int comp = -1;
					if(focObj != null && otherFocObj != null){
						String s1 = focObj.getPropertyString(FLD_LIS_TEST_LABEL);
						String s2 = otherFocObj.getPropertyString(FLD_LIS_TEST_LABEL);
						int is1 = 0;
						int is2 = 0;
						boolean isNumber1 = false;
						boolean isNumber2 = false;
						try{
							is1 = Integer.valueOf(s1);
							isNumber1 = true;
						}catch(Exception e){
							isNumber1 = false;
						}
						try{
							is2 = Integer.valueOf(s2);
							isNumber2 = true;
						}catch(Exception e){
							isNumber2 = false;
						}
						
						if(isNumber1 && isNumber2){
							comp = is1 - is2;
						}else if(isNumber1){
							comp = -1;
						}else if(isNumber2){
							comp = 1;
						}else{
							comp = s1.compareTo(s2);
						}
						
						if(comp == 0){
							comp = focObj.getPropertyInteger(FLD_DAY_PRIORITY) - otherFocObj.getPropertyInteger(FLD_DAY_PRIORITY); 
						}
					}
					return comp;
				}
			};
			list = getList(list, mode, order);
		}else{
			list = getList(list, mode);
		}
		return list;
	}
}
