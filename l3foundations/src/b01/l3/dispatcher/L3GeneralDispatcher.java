/*
 * Created on Jun 14, 2006
 */
package b01.l3.dispatcher;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import b01.foc.Globals;
import b01.foc.calendar.FCalendar;
import b01.foc.db.DBManager;
import b01.foc.desc.FocDesc;
import b01.foc.desc.FocObject;
import b01.foc.list.FocList;
import b01.foc.list.FocListElement;
import b01.foc.list.FocListIterator;
import b01.l3.Instrument;
import b01.l3.InstrumentDesc;
import b01.l3.PoolKernel;
import b01.l3.TestLabelMap;
import b01.l3.TestLabelMapDesc;
import b01.l3.data.L3Sample;
import b01.l3.data.L3Test;
import b01.l3.data.L3TestDesc;
import b01.l3.exceptions.L3Exception;

/**
 * @author 01Barmaja
 */
public class L3GeneralDispatcher extends L3AbstractDispatcher {
	
	private HashMap<String, OneTestDispatcher> mapToOneTestDispatcher = null;
	private FCalendar calendar = null;
	private int       status   = -1;
	
	public static final int STATUS_DAY      = 0;
	public static final int STATUS_HOLIDAY  = 1;
	public static final int STATUS_NIGHT    = 2;

	public L3GeneralDispatcher() throws Exception {
		status = -1;
		calendar = FCalendar.getDefaultCalendar();
		if(calendar == null){
			throw new L3Exception("No default Calendar configured! Cannot proceed...");
		}
		rebuildTest2InstrumentMap();
	}
	
	public void dispose() {
		dispose_Map();
		calendar = null;
	}

	public void dispose_Map() {
		if(mapToOneTestDispatcher != null){
			Iterator iter = mapToOneTestDispatcher.values().iterator();
			while(iter != null && iter.hasNext()){
				OneTestDispatcher oneTestDisp = (OneTestDispatcher) iter.next();
				if(oneTestDisp != null){
					oneTestDisp.dispose();
				}
			}
			mapToOneTestDispatcher.clear();
		}
		mapToOneTestDispatcher = null;
	}
	
	public void rebuildTest2InstrumentMap(){
		dispose_Map();
		mapToOneTestDispatcher = new HashMap<String, OneTestDispatcher>();
		
  	FocList testMapList = ((TestLabelMapDesc)TestLabelMap.getFocDesc()).getList(FocList.FORCE_RELOAD);
  	testMapList.iterate(new FocListIterator(){
			@Override
			public boolean treatElement(FocListElement element, FocObject focObj) {
				TestLabelMap testLabelMap = (TestLabelMap) focObj;
				pushOneTestDispatcher(testLabelMap);
				return false;
			}
  	});
  	
  	recomputeCurrentStatus();
	}

	public void recomputeCurrentStatus(){
		Date date = new Date(System.currentTimeMillis()+Calendar.getInstance().get(Calendar.DST_OFFSET));
		
		boolean holiday  = calendar.isHoliday(date) || calendar.isWeekEnd(date);
    long currentTimeSinceMidnight = FCalendar.getTimeSinceMidnight(date);    
		boolean workTime = calendar.isWorkTime(currentTimeSinceMidnight);
		
		int newStatus = STATUS_DAY;
		if(holiday){
			newStatus = STATUS_HOLIDAY;
		}else if(!workTime){
			newStatus = STATUS_NIGHT;
		}

		if(newStatus != status){
			Globals.logString("Status changed : "+newStatus+" - "+status+ " - Day="+STATUS_DAY+" Holiday="+STATUS_HOLIDAY+" Night="+STATUS_NIGHT);
			status = newStatus;
			recomputeCurrentInstrumentForTests();
		}else{
			Globals.logString("Status remains the same : "+newStatus+" - "+status+ " - Day="+STATUS_DAY+" Holiday="+STATUS_HOLIDAY+" Night="+STATUS_NIGHT);
		}
	}
	
	public void recomputeCurrentInstrumentForTests(){
		if(mapToOneTestDispatcher != null){
			Iterator iter = mapToOneTestDispatcher.values().iterator();
			while(iter != null && iter.hasNext()){
				OneTestDispatcher oneTestDisp = (OneTestDispatcher) iter.next();
				if(oneTestDisp != null){
					oneTestDisp.sortAccordingToStatus();
				}
			}
		}
	}
	
	public Map<String, OneTestDispatcher> getTestToDispatecherMap(){
		if(mapToOneTestDispatcher == null){
			rebuildTest2InstrumentMap();
		}
		return mapToOneTestDispatcher;
	}
	
	public OneTestDispatcher getOneTestDispatcher(TestLabelMap testLabelMap){
		OneTestDispatcher disp1 = null;
		Map<String, OneTestDispatcher> map = getTestToDispatecherMap();
		disp1 = map.get(testLabelMap.getLisTestLabel());
		
		return disp1;
	}
	
	public OneTestDispatcher pushOneTestDispatcher(TestLabelMap testLabelMap){
		OneTestDispatcher disp1 = getOneTestDispatcher(testLabelMap);
		
		if(disp1 == null){
			disp1 = new OneTestDispatcher(this);
			mapToOneTestDispatcher.put(testLabelMap.getLisTestLabel(), disp1);
		}
		
		disp1.addTestLabelMap(testLabelMap);
		return disp1;
	}

	public int getStatus() {
		return status;
	}

	@Override
	public TestLabelMap getInstrumentForTest(L3Test test) {
		TestLabelMap testMap = null;

		OneTestDispatcher oneTestDispatcher = mapToOneTestDispatcher.get(test.getLabel());
		
		Instrument suggInstr = (Instrument) test.getPropertyObject(L3TestDesc.FLD_SUGGESTED_INSTRUMENT);
		if(suggInstr != null){
			testMap = oneTestDispatcher.findTestLabelMapForInstrument(suggInstr);
			if(testMap == null || testMap.getInstrument().isOnHold()){
				suggInstr = null;
			}
		}
		if(suggInstr == null){
			if(oneTestDispatcher != null){
				testMap = oneTestDispatcher.getFirstActive();
			}
		}
		return testMap;
	}
	
	public void dispatchSample(L3Sample sample){
		recomputeCurrentStatus();
		super.dispatchSample(sample);
	}

	@Override
	protected void readProperties(Properties props) {
	
  }

	public void prepareForNewMessage() {
		DBManager dbManager = Globals.getApp().getDBManager();
		FocDesc focDesc = Instrument.getFocDesc();
		
		Statement statement = dbManager.lockStatement();
		try{
			ResultSet resultSet = statement.executeQuery("SELECT "+InstrumentDesc.FNAME_CODE+","+InstrumentDesc.FNAME_ON_HOLD+" FROM "+focDesc.getStorageName());
      while(resultSet != null && resultSet.next()){
    	  String code = resultSet.getString(1);
        int onHold = resultSet.getInt(2);

        Instrument instr = PoolKernel.getInstrumentForAnyPool(code);
        if(instr != null){
        	instr.setPropertyBoolean(InstrumentDesc.FLD_ON_HOLD, onHold == 1);
        }
      }
		}catch(Exception e){
			Globals.logString("Could not update the ON_HOLD STATUS of instruments before dispatching");
			Globals.logException(e);
		}
		dbManager.unlockStatement(statement);
	}
}