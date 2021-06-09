package b01.l3.dispatcher;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Properties;
import b01.foc.Globals;
import b01.foc.list.FocList;
import b01.l3.Instrument;
import b01.l3.PoolKernel;
import b01.l3.TestLabelMap;
import b01.l3.data.L3Sample;
import b01.l3.data.L3Test;
import b01.l3.data.L3TestDesc;

public abstract class L3AbstractDispatcher implements IDispatcher{
	public abstract TestLabelMap getInstrumentForTest(L3Test test);
	
	private ArrayList<SecondaryInstrument> secondaryArray = null;
	
	public L3AbstractDispatcher(){
		Properties props = loadPropertiesFromFile();
		if(props != null){
			readProperties(props);
			props.clear();
			props = null;
		}
	}

	public void dispose(){
		if(secondaryArray != null){
			for(int i=0; i<secondaryArray.size(); i++){
				SecondaryInstrument si = secondaryArray.get(i);
				if(si != null) si.dispose();
			}
			secondaryArray.clear();
			secondaryArray = null;
		}
	}

	private Properties loadPropertiesFromFile(){
		Properties props = null;
		try{
			File propertiesFile = new File("properties/dispatcher.properties");
			if(propertiesFile.exists()){
			  FileInputStream in = new FileInputStream(propertiesFile);
			  props = new Properties();
			  props.load(in);
			  readProperties(props);
			  in.close();
			}
		}catch(Exception e){
			Globals.logException(e);
		}
		return props;
	}

	protected void readProperties(Properties props){
		int i = 1;
		SecondaryInstrument si = null;
		do{
			if(si != null){
				if(secondaryArray == null){
					secondaryArray = new ArrayList<SecondaryInstrument>();
				}
				secondaryArray.add(si);
			}
			si = new SecondaryInstrument(props, i++);
		}while(si.isValid());
		
		if(si != null){
			si.dispose();
			si = null;
		}
	}
	
	public void init(ArrayList<PoolKernel> poolList){
	}
	
	public void dispatchSample(L3Sample sample){
		FocList testList = sample.getTestList();
		for(int i=0; i<testList.size(); i++){
			L3Test test = (L3Test) testList.getFocObject(i);
			if(test != null){
				TestLabelMap testMap = getInstrumentForTest(test);
				if(testMap != null){
					Instrument instr = testMap.getInstrument();
					Globals.logDetail("Dispatching "+((L3Test)testList.getFocObject(i)).getLabel()+" to "+instr.getCode());
					test.setInstrument(instr);
					test.setPropertyString(L3TestDesc.FLD_DESCRIPTION, testMap.getDescription());
				}else{
					test.setBlocked(true);
					test.setNotificationMessage("Could not dispatch. Check if test configured in dispatcher.");
					Globals.logDetail("Could not Dispatch "+test.getLabel());
				}
			}
		}
		
		if(secondaryArray != null){
			for(int i=0; i<secondaryArray.size(); i++){
				SecondaryInstrument secInstr = (SecondaryInstrument) secondaryArray.get(i);
        
				if(secInstr != null) {
					boolean containsAll = true;
					String primaryCode = secInstr.getPrimaryCode();
					
					testList = sample.getTestList();
					for(i=0; i<testList.size() && containsAll; i++){
						L3Test test = (L3Test) testList.getFocObject(i);
						if(test != null && test.getInstrument() == null){
							TestLabelMap testMap = getInstrumentForTest(test);
							Instrument instr = testMap.getInstrument();
							if(instr != null && instr.getCode().compareTo(primaryCode) == 0){
								containsAll = secInstr.containsTest(test.getLabel());
							}
						}
					}
					
					if(containsAll){
						String newInstCode = secInstr.getSecondaryCode();
						PoolKernel pool = PoolKernel.getPoolForInstrument(newInstCode);
						Instrument subInstr = pool.getInstrument(newInstCode);

						for(i=0; i<testList.size() && containsAll; i++){
							L3Test test = (L3Test) testList.getFocObject(i);
							if(test != null && test.getInstrument() == null){
								TestLabelMap testMap = getInstrumentForTest(test);
								Instrument instr = testMap.getInstrument();
								if(instr != null && instr.getCode().compareTo(primaryCode) == 0){
									test.setInstrument(subInstr);
								}
							}
						}
					}
				}
			}
		}
	}
}
