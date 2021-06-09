/*
 * Created on Oct 14, 2004
 */
package b01.l3;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import b01.foc.*;
import b01.foc.calendar.FCalendarDesc;
import b01.foc.desc.*;
import b01.foc.gui.FPanel;
import b01.foc.menu.FMenuAction;
import b01.foc.menu.FMenuItem;
import b01.foc.menu.FMenuList;
import b01.l3.connector.LisConnector;
import b01.l3.connector.dbConnector.lisConnectorTables.LisInstrumentMessageDesc;
import b01.l3.connector.dbConnector.lisConnectorTables.LisSampleDesc;
import b01.l3.connector.dbConnector.lisConnectorTables.LisTestDesc;
import b01.l3.data.L3InstrumentGraphDesc;
import b01.l3.data.L3InstrumentMessageDesc;
import b01.l3.data.L3Sample;
import b01.l3.data.L3SampleTestJoinDesc;
import b01.l3.data.L3SampleTestJoinTree;
import b01.l3.data.L3Test;

/**
 * @author 01Barmaja
 */
public class L3KernelModule implements FocModuleInterface {

  public L3KernelModule() {
  }

  @SuppressWarnings("serial")
	public static void declareMenu(FMenuList mainMenu){
    Application app = Globals.getApp();
    FocAppGroup group = (FocAppGroup) app.getAppGroup();
    
    if(group.allowConfiguration()){
      FMenuList config = new FMenuList("Configuration", 'C');
      mainMenu.addMenu(config);
            
      FMenuItem poolKernelItemConfig = new FMenuItem("Pools & Instruments", 'p', new FMenuAction(PoolKernel.getFocDesc(), null, L3Globals.VIEW_CONFIG, true)); 
      config.addMenu(poolKernelItemConfig);  

      FMenuItem lisFileConnectorConfig = new FMenuItem("LIS connector", 'F', new FMenuAction(LisConnector.getFocDesc(), null, L3Globals.VIEW_CONFIG, true));
      config.addMenu(lisFileConnectorConfig);
      
      FMenuItem testsMappingsConfig = new FMenuItem("Dispatching", 'D', new AbstractAction(){
        public void actionPerformed(ActionEvent arg0) {
          FPanel panel = new DispatchingGuiTabbedPanel(L3Globals.VIEW_CONFIG);
          Globals.getDisplayManager().newInternalFrame(panel);
        }
      });
      config.addMenu(testsMappingsConfig);

      FMenuItem testGroupItem = new FMenuItem("Test groups", 'G', new FMenuAction(TestGroupDesc.getInstance(), null, L3Globals.VIEW_CONFIG, true));
      config.addMenu(testGroupItem);
      
      FMenuItem applicationConfig = new FMenuItem("General Configuration", 'A', new AbstractAction(){
        public void actionPerformed(ActionEvent e) {
        	L3Application l3App = L3Application.getAppInstance();
        	FPanel panel = l3App.newDetailsPanel(L3Globals.VIEW_CONFIG);
          Globals.getDisplayManager().newInternalFrame(panel);
        }
      });
      config.addMenu(applicationConfig);
      
      FMenuItem calendarConfig = new FMenuItem("Calendars", 'C', new FMenuAction(FCalendarDesc.getInstance(), null, FocObject.DEFAULT_VIEW_ID, true)); 
      config.addMenu(calendarConfig);  
    }


    
    
   	FMenuList normal = new FMenuList("Monitoring", 'M');
   	mainMenu.addMenu(normal);

    if(group.allowConnection() || group.allowMonitoring()){
	    FMenuItem poolKernelItemNormal = new FMenuItem("Pools & Instruments", 'P', new FMenuAction(PoolKernel.getFocDesc(), null, L3Globals.VIEW_NORMAL, true));
	    normal.addMenu(poolKernelItemNormal);
    	
	    FMenuItem lisFileConnectorMonitoring = new FMenuItem("LIS Connector", 'C', new FMenuAction(LisConnector.getFocDesc(), null, L3Globals.VIEW_NORMAL, true));
	    normal.addMenu(lisFileConnectorMonitoring); 
      
      FMenuList l3SampleTestJoin = new FMenuList("Samples", 'S');
      normal.addMenu(l3SampleTestJoin);
      
      FMenuItem l3SampleTestJoinByInstrument = new FMenuItem("By dispatched Instrument", 'd', new FMenuAction(L3SampleTestJoinDesc.getInstance(), null, L3SampleTestJoinTree.VIEW_BY_DISPATCHED_INSTRUMENT, true));
      l3SampleTestJoin.addMenu(l3SampleTestJoinByInstrument);

      l3SampleTestJoinByInstrument = new FMenuItem("By reception Instrument", 'r', new FMenuAction(L3SampleTestJoinDesc.getInstance(), null, L3SampleTestJoinTree.VIEW_BY_RECEIVED_INSTRUMENT, true));
      l3SampleTestJoin.addMenu(l3SampleTestJoinByInstrument);

      FMenuItem l3SampleTestJoinBySample = new FMenuItem("By Sample", 'S', new FMenuAction(L3SampleTestJoinDesc.getInstance(), null, L3SampleTestJoinTree.DEFAULT_VIEW, true));
      l3SampleTestJoin.addMenu(l3SampleTestJoinBySample); 

      FMenuItem purgeSample = new FMenuItem("Purge samples", 'P', new AbstractAction(){
				public void actionPerformed(ActionEvent e) {
					L3Application.getAppInstance().purge();
				}
      });
      l3SampleTestJoin.addMenu(purgeSample); 

	    FMenuList messagesTables = new FMenuList("Messages", 'M');
	    normal.addMenu(messagesTables);

	    FMenuItem l3MessageTable = new FMenuItem("L3 Messages", '3', new FMenuAction(L3InstrumentMessageDesc.getInstance(), null, L3Globals.VIEW_NORMAL, true));
	    messagesTables.addMenu(l3MessageTable);

	    FMenuItem lisMessageTable = new FMenuItem("Lis Messages", 'i', new FMenuAction(LisInstrumentMessageDesc.getInstance(), null, L3Globals.VIEW_NORMAL, true));
	    messagesTables.addMenu(lisMessageTable);
      
      FMenuItem graphsTables = new FMenuItem("Graphs", 'g', new FMenuAction(L3InstrumentGraphDesc.getInstance(), null, FocObject.DEFAULT_VIEW_ID, true));
      normal.addMenu(graphsTables);
      
	    FMenuList lisTables = new FMenuList("LIS Interface Tables", 'I');
	    normal.addMenu(lisTables);
	    
	    FMenuItem lisDBConnectorSampleMonitoring = new FMenuItem("View LIS Samples", 'S', new FMenuAction(LisSampleDesc.getInstance(), null, L3Globals.VIEW_NORMAL, true));
	    lisTables.addMenu(lisDBConnectorSampleMonitoring);
    
	    FMenuItem lisDBConnectorTestMonitoring = new FMenuItem("View LIS Tests", 'T', new FMenuAction(LisTestDesc.getInstance(), null, L3Globals.VIEW_NORMAL, true));
	    lisTables.addMenu(lisDBConnectorTestMonitoring); 
    
	    FMenuItem testsMappingsConfig = new FMenuItem("Dispatching", 'D', new AbstractAction(){
	      public void actionPerformed(ActionEvent arg0) {
	        FPanel panel = new DispatchingGuiTabbedPanel(L3Globals.VIEW_NORMAL);
	        Globals.getDisplayManager().newInternalFrame(panel);
	      }
	    });
	    normal.addMenu(testsMappingsConfig);
    }
    
    app.setMainAppMenu(mainMenu);
  }  
  
  public void declareFocObjects() {
    Application app = Globals.getApp();
    app.declaredObjectList_DeclareObject(FocAppGroup.class);
    app.declaredObjectList_DeclareObject(PoolKernel.class);
    app.declaredObjectList_DeclareObject(Instrument.class);
    app.declaredObjectList_DeclareObject(L3Test.class);
    app.declaredObjectList_DeclareObject(L3Sample.class);
    app.declaredObjectList_DeclareObject(TestLabelMap.class);
    app.declaredObjectList_DeclareObject(LisConnector.class);
    app.declaredObjectList_DeclareObject(L3Application.class);
    app.declaredObjectList_DeclareDescription(TestGroupDesc.class);
    app.declaredObjectList_DeclareDescription(L3InstrumentGraphDesc.class);
    
    //MESSAGE
    app.declaredObjectList_DeclareDescription(L3InstrumentMessageDesc.class);
    app.declaredObjectList_DeclareDescription(LisInstrumentMessageDesc.class);
    
//  app.declaredObjectList_DeclareObject(L3SampleFilter.class);
//  app.declaredObjectList_DeclareObject(L3TestArchive.class);
//  app.declaredObjectList_DeclareObject(L3SampleArchive.class);
//  app.declaredObjectList_DeclareObject(PoolDBAbstract.class);
  }  
}