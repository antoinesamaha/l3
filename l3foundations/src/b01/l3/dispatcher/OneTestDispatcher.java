package b01.l3.dispatcher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import b01.foc.Globals;
import b01.l3.Instrument;
import b01.l3.InstrumentDesc;
import b01.l3.TestLabelMap;
import b01.l3.TestLabelMapDesc;

public class OneTestDispatcher {
	private L3GeneralDispatcher     dispatcher          = null;
	private ArrayList<TestLabelMap> testLabelArray      = null;
	
	public OneTestDispatcher(L3GeneralDispatcher dispatcher){
		this.dispatcher = dispatcher;
		testLabelArray  = new ArrayList<TestLabelMap>();
	}
	
	public void dispose(){
		if(testLabelArray != null){
			testLabelArray.clear();
		}
		testLabelArray = null;
		dispatcher = null;
	}
	
	public void addTestLabelMap(TestLabelMap testLabelMap){
		if(testLabelArray != null && testLabelMap != null){
			testLabelArray.add(testLabelMap);
		}
	}
	
	public TestLabelMap getFirstActive(){
		TestLabelMap testLabel = null;
		
		for(int i=0; i<testLabelArray.size() && testLabel == null; i++){
			TestLabelMap currTestLabel = testLabelArray.get(i);
			Instrument instr = (Instrument) currTestLabel.getPropertyObject(TestLabelMapDesc.FLD_INSTRUMENT);
			if(!instr.getPropertyBoolean(InstrumentDesc.FLD_ON_HOLD) && !currTestLabel.getPropertyBoolean(TestLabelMapDesc.FLD_ON_HOLD)){
				testLabel = currTestLabel ;
			}
		}
		return testLabel;
	}

	public TestLabelMap findTestLabelMapForInstrument(Instrument instrument){
		TestLabelMap foundTestLabel = null;
		
		for(int i=0; i<testLabelArray.size() && foundTestLabel == null; i++){
			TestLabelMap currTestLabel = testLabelArray.get(i);
			Instrument instr = (Instrument) currTestLabel.getPropertyObject(TestLabelMapDesc.FLD_INSTRUMENT);
			if(instr.getCode().compareTo(instrument.getCode()) == 0){
				foundTestLabel = currTestLabel ;
			}
		}
		return foundTestLabel;
	}

	public void sortAccordingToStatus(){
		int status = dispatcher.getStatus();
			
		int field = TestLabelMapDesc.FLD_DAY_PRIORITY;
		switch(status){
		case L3GeneralDispatcher.STATUS_HOLIDAY:
			field = TestLabelMapDesc.FLD_HOLIDAY_PRIORITY;
			break;
		case L3GeneralDispatcher.STATUS_NIGHT:
			field = TestLabelMapDesc.FLD_NIGHT_PRIORITY;
			break;
		}
		
		final int fldToUse = field;

		Collections.sort(testLabelArray, new Comparator<TestLabelMap>(){
			public int compare(TestLabelMap o1, TestLabelMap o2) {
				return o1.getPropertyInteger(fldToUse) - o2.getPropertyInteger(fldToUse);
			}
		});
		
		for(int i=0; i<testLabelArray.size(); i++){
			TestLabelMap map = testLabelArray.get(i);
			Instrument instr = (Instrument)map.getPropertyObject(TestLabelMapDesc.FLD_INSTRUMENT);
			Globals.logDetail("Instr in order ("+i+"): "+instr.getPropertyString(InstrumentDesc.FLD_CODE));
		}
	}
}
