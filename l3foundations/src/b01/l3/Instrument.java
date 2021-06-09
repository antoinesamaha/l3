/*
 * Created on Jun 5, 2006
 */
package b01.l3;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.util.Iterator;
import java.util.Properties;

import javax.comm.SerialPort;

import b01.foc.list.FocLinkForeignKey;
import b01.foc.Application;
import b01.foc.ConfigInfo;
import b01.foc.Globals;
import b01.foc.IExitListener;
import b01.foc.db.SQLFilter;
import b01.foc.db.SQLSelect;
import b01.foc.db.SQLUpdate;
import b01.foc.desc.FocConstructor;
import b01.foc.desc.FocDesc;
import b01.foc.desc.FocObject;
import b01.foc.desc.field.FField;
import b01.foc.gui.FPanel;
import b01.foc.list.FocList;
import b01.foc.property.FBoolean;
import b01.foc.property.FInt;
import b01.foc.property.FMultipleChoice;
import b01.foc.property.FString;
import b01.l3.data.L3Message;
import b01.l3.data.L3Sample;
import b01.l3.data.L3SampleTestJoinDesc;
import b01.l3.data.L3SampleTestJoinFilter;
import b01.l3.data.L3TestDesc;
import b01.l3.exceptions.L3Exception;
import b01.l3.exceptions.L3InstrumentDoesNotRespondTryLaterException;
import b01.l3.exceptions.L3TryLaterException;
import b01.sbs.BService;
import b01.sbs.BServiceClient;
import b01.sbs.BServiceInterface;
import b01.sbs.BServiceServer;
import b01.sbs.LogInterface;

/**
 * @author 01Barmaja
 */
public class Instrument extends FocObject implements Runnable, MessageListener,
		IExitListener, BServiceInterface {
	private IDriver driver = null;
	private Thread senderThread = null;
	private Properties properties = null;
	private boolean autoRefresh = false;
	private L3SampleTestJoinFilter sampleTestFilterReadyToSend = null;
	private L3SampleTestJoinFilter sampleTestFilterPendingTestsToBeResentWithNewTests = null;
	private Application application = null;
	private FocList supportedTestList = null;

	private boolean logBufferDetails = false;

	private int DELAY_POLLING_FOR_DB_SAMPLES_TO_SEND = 10000;
	private int DELAY_FOR_SAMPLE_DISPAY_LIST_AUTOMATIC_REFRESH = 10000;
	private int DELAY_TO_TRY_AGAIN_THE_DRIVER_RESERVE = 2000;
	private int DELAY_TO_TRY_LATER = 2000;
	private int DELAY_DRIVER_TIME_OUT_FOR_RESPONSE = 30000;

	private void initFromProperties(Properties props) throws Exception {
		setCode(props.getProperty("instrument.code"));
		setName(props.getProperty("instrument.name"));
		setDriverClassName(props.getProperty("instrument.driver"));
	}

	public void resetDefaults() {
		setPropertyMultiChoice(InstrumentDesc.FLD_MODE,
				InstrumentDesc.INSTRUMENT_MODE_AUTOMATIC);
		setPropertyInteger(
				InstrumentDesc.FLD_DELAY_POLLING_FOR_DB_SAMPLES_TO_SEND,
				DELAY_POLLING_FOR_DB_SAMPLES_TO_SEND);
		setPropertyInteger(
				InstrumentDesc.FLD_DELAY_FOR_SAMPLE_DISPAY_LIST_AUTOMATIC_REFRESH,
				DELAY_FOR_SAMPLE_DISPAY_LIST_AUTOMATIC_REFRESH);
		setPropertyInteger(
				InstrumentDesc.FLD_DELAY_TO_TRY_AGAIN_THE_DRIVER_RESERVE,
				DELAY_TO_TRY_AGAIN_THE_DRIVER_RESERVE);
		setPropertyInteger(InstrumentDesc.FLD_DELAY_TO_TRY_LATER,
				DELAY_TO_TRY_LATER);
		setPropertyInteger(
				InstrumentDesc.FLD_DELAY_DRIVER_TIME_OUT_FOR_RESPONSE,
				DELAY_DRIVER_TIME_OUT_FOR_RESPONSE);
	}

	private void initialiseFocProperties() {
		resetDefaults();
		setPropertyMultiChoice(InstrumentDesc.FLD_SERIAL_BAUDE_RATE, 9600);
		setPropertyMultiChoice(InstrumentDesc.FLD_SERIAL_PARITY,
				SerialPort.PARITY_NONE);
		setPropertyMultiChoice(InstrumentDesc.FLD_SERIAL_DATA_BITS,
				SerialPort.DATABITS_8);
		setPropertyMultiChoice(InstrumentDesc.FLD_SERIAL_STOP_BIT,
				SerialPort.STOPBITS_1);
	}

	public Instrument(Properties props) throws Exception {
		super(getFocDesc());
		newFocProperties();
		initialiseFocProperties();
		initFromProperties(props);
	}

	public Instrument(FocConstructor constr) {
		super(constr);
		newFocProperties();
		initialiseFocProperties();
		forceControler(true);
	}

	public Instrument(File file) throws Exception {
		super(getFocDesc());
		Properties props = new Properties();
		FileInputStream in = new FileInputStream(file);
		props.load(in);
		in.close();

		initFromProperties(props);
	}

	public void dispose() {
		// try{
		// disconnect();
		// }catch(Exception e){
		// logException(e);
		// }

		if (driver != null) {
			driver.dispose();
			driver = null;
			properties = null;
		}
		senderThread = null;

		if (supportedTestList != null) {
			supportedTestList.dispose();
			supportedTestList = null;
		}

		if (service != null) {
			service.dispose();
			service = null;
		}
		super.dispose();
	}

	private boolean isResendAllPendingTests() {
		boolean resend = false;
		try {
			resend = (getDriver() != null) ? getDriver()
					.isResendAllPendingTests() : false;
		} catch (Exception e) {
			Globals.logException(e);
		}
		return resend;
	}

	public static void adjustColor(FocObject object, int launchedFLD,
			int connectedFLD, int onHoldFLD) {
		if (object != null) {
			FBoolean boolProp = (FBoolean) object.getFocProperty(onHoldFLD);
			if (boolProp != null) {
				if (boolProp.getBoolean()) {
					boolProp.setBackground(Color.GRAY);
				} else {
					boolProp.setBackground(null);
				}
			}

			boolProp = (FBoolean) object.getFocProperty(connectedFLD);
			if (boolProp != null) {
				if (boolProp.getBoolean()) {
					boolProp.setBackground(Color.GREEN);
				} else {
					boolProp.setBackground(null);
				}
			}

			boolProp = (FBoolean) object.getFocProperty(launchedFLD);
			if (boolProp != null) {
				if (boolProp.getBoolean()) {
					boolProp.setBackground(Color.GREEN);
				} else {
					boolProp.setBackground(null);
				}
			}
		}
	}

	public boolean isOnHold() {
		return getPropertyBoolean(InstrumentDesc.FLD_ON_HOLD);
	}

	public void setAutoRefresh(boolean auto) {
		autoRefresh = auto;
	}

	public boolean isAutoRefresh() {
		return autoRefresh;
	}

	private void setAsExitListener() {
		if (application == null) {
			application = Globals.getApp();
			application.addExitListener(this);
		}
	}

	public Properties getProperties() throws Exception {
		if (properties == null) {
			Properties tempProperties = new Properties();

			if (getPropertiesFilePath() != null
					&& getPropertiesFilePath().compareTo("") != 0) {
				FileInputStream in = new FileInputStream(
						getPropertiesFilePath());
				tempProperties.load(in);
				in.close();
			}
			tempProperties.put("serialPort.name",
					getPropertyString(InstrumentDesc.FLD_SERIAL_PORT_NAME));

			FMultipleChoice multiProp = (FMultipleChoice) getFocProperty(InstrumentDesc.FLD_SERIAL_BAUDE_RATE);
			tempProperties.put("serialPort.baudrate", multiProp.getString());

			multiProp = (FMultipleChoice) getFocProperty(InstrumentDesc.FLD_SERIAL_DATA_BITS);
			tempProperties.put("serialPort.databits", multiProp.getString());

			multiProp = (FMultipleChoice) getFocProperty(InstrumentDesc.FLD_SERIAL_PARITY);
			tempProperties.put("serialPort.parity", multiProp.getString());

			multiProp = (FMultipleChoice) getFocProperty(InstrumentDesc.FLD_SERIAL_STOP_BIT);
			tempProperties.put("serialPort.stopbit", multiProp.getString());

			// We assign the properties only if everything when fine without
			// exception.
			properties = tempProperties;
			FocList supportedTestList = getSupportedTestList();
			for (int i = 0; i < supportedTestList.size(); i++) {
				properties.put(
						"test."
								+ ((TestLabelMap) supportedTestList
										.getFocObject(i)).getLisTestLabel(),
						((TestLabelMap) supportedTestList.getFocObject(i))
								.getInstrumentTestCode());
			}

			String str = properties.getProperty("log.bufferDetails");
			if (str != null && str.compareTo("1") == 0) {
				logBufferDetails = true;
			}

			if (getPropertyBoolean(InstrumentDesc.FLD_IS_EMULATOR)) {
				String relatedInstr = (String) getPropertyString(InstrumentDesc.FLD_RELATED_INSTRUMENT);
				if (relatedInstr != null
						&& relatedInstr.trim().compareTo("") != 0) {
					tempProperties.put("relatedInstrument.code", relatedInstr);
				}
			}
		}
		return properties;
	}

	public boolean isLogBufferDetails() {
		return logBufferDetails;
	}

	public boolean sendWithDriverReservation(L3Message message)
			throws Exception {
		boolean error = true;
		IDriver driver = getDriver();
		if (!driver.reserve()) {
			try {
				send(message);
				driver.release();
				error = false;
			} catch (Exception e) {
				driver.release();
				throw e;
			}
		}
		return error;
	}

	private void send(L3Message message) throws Exception {
		logString("Message before send:" + message.toStringBuffer());
		// IF we set the status to sending, then we need to reset the status
		// (fisDamagedSamples!!!) upon restart.
		// This logic is useless!!! we move the status when it is needed.
		// Iterator sampleIterator = message.sampleIterator();
		// while (sampleIterator.hasNext()){
		// ((L3Sample)sampleIterator.next()).updateStatusForTests(L3TestDesc.TEST_STATUS_SENDING_TO_INSTRUMENT);
		// }
		driver.send(message);
		Iterator sampleIterator = message.sampleIterator();
		while (sampleIterator.hasNext()) {
			((L3Sample) sampleIterator.next())
					.updateStatusForTests(L3TestDesc.TEST_STATUS_ANALYSING);
		}
	}

	public void addMessageListener(MessageListener listener) {
		if (driver != null) {
			driver.addListener(listener);
		}
	}

	public void removeMessageListener(MessageListener listener) {
		if (driver != null) {
			driver.removeListener(listener);
		}
	}

	public boolean isEmulator() {
		return getPropertyBoolean(InstrumentDesc.FLD_IS_EMULATOR);
	}

	public void logException(Exception e) {
		Globals.logString(getName() + "->Exception");
		Globals.logException(e);
	}

	public synchronized void logString(String str) {
		Globals.logString(getName() + "->" + str);
	}

	public synchronized void logString(StringBuffer str) {
		if (ConfigInfo.isLogDetails() || !isEmulator()) {
			Globals.logString(getName() + "->" + str);
		}
	}

	private boolean runContent() {
		int size = 0;
		boolean driverSaidToTryLater = false;
		logString("POLLING runContent.Before IsConnected");
		if (isConnected()) {
			logString("POLLING runContent.Inside IsConnected");
			L3Message messageReadyToSend = null;
			driverSaidToTryLater = false;
			L3Message message;
			size = 1;
			while (size > 0 && !driverSaidToTryLater) {
				logString("POLLING runContent.Inside while 1");
				if (messageReadyToSend != null
						&& messageReadyToSend.getNumberOfSamples() > 0) {
					int i = 0;
					while (i < messageReadyToSend.getNumberOfSamples()
							&& !driverSaidToTryLater) {
						logString("POLLING runContent.Inside while 2");

						Globals.logDetail(getName()
								+ " trying to reserve driver");
						if (!driver.reserve()) {
							message = new L3Message();
							L3Sample sample = messageReadyToSend.getSample(i);
							// sample.updateStatus(L3SampleDesc.SAMPLE_STATUS_SENDING_TO_INSTRUMENT);
							message.addSample(sample);// we have to test the
														// status of the sample;
														// if it is blocked we
														// dont add
							try {
								send(message);
								i++;
							} catch (L3TryLaterException e) {
								logString("L3TryLaterException Driver suspended comunication.");
								driverSaidToTryLater = true;
							} catch (L3InstrumentDoesNotRespondTryLaterException e) {
								logString("L3InstrumentDoesNotRespondTryLaterException Driver not responding");
								logString(e.getMessage());
								driverSaidToTryLater = true;
							} catch (Exception e) {
								i++;
								logException(e);
								sample.updateBlockedForTests(true);
								sample.updateStatusForTests(L3TestDesc.TEST_STATUS_RESULT_AVAILABLE);
								sample.updateNotificationMessageForTests("WHEN SEND TO INST:"
										+ e.getMessage());
							}
							driver.release();
						} else {
							try {
								Thread.sleep(getPropertyInteger(InstrumentDesc.FLD_DELAY_TO_TRY_AGAIN_THE_DRIVER_RESERVE));
							} catch (InterruptedException e) {
								Globals.logException(e);
							}
						}
					}
				}

				if (messageReadyToSend != null) {
					messageReadyToSend.dispose();
					messageReadyToSend = null;
				}
				if (!driverSaidToTryLater) {
					sampleTestFilterReadyToSend.setActive(true);
					messageReadyToSend = sampleTestFilterReadyToSend
							.convertToMessage();
					size = messageReadyToSend != null ? messageReadyToSend
							.getNumberOfSamples() : 0;
					// Here we are adding to the samples to send, the already
					// sent tests but do not have results yet
					if (size > 0 && isResendAllPendingTests()) {
						for (int i = 0; i < messageReadyToSend
								.getNumberOfSamples(); i++) {
							L3Sample sample = messageReadyToSend.getSample(i);
							L3SampleTestJoinFilter pendingList = getSampleTestList_PendingTestsToBeResentWithNewTests();
							pendingList.setSampleID(sample.getId());
							pendingList.setActive(true);
							pendingList.addAllTestsToSameSample(sample);
						}
					}
				}
			}
		}
		return driverSaidToTryLater;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		int delay = getPropertyInteger(InstrumentDesc.FLD_DELAY_POLLING_FOR_DB_SAMPLES_TO_SEND);
		if (sampleTestFilterReadyToSend == null) {
			sampleTestFilterReadyToSend = getSampleListToSend();
		}
		boolean driverSaidToTryLater = false;

		while (true) {
			try {
				logString("POLLING Before RunContect");
				driverSaidToTryLater = runContent();
				
				int theDelay = delay;
				if (driverSaidToTryLater) {
					theDelay = Math.max(theDelay, getPropertyInteger(InstrumentDesc.FLD_DELAY_TO_TRY_LATER));
				}

				try {
					DriverSerialPort driverSerial = (DriverSerialPort) getDriver();
					if (driverSerial.shouldResetConnection()) {
						Globals.logString("!!! Resetting the Connection ...");
						Globals.logString("		Switch OFF ...");
						switchOff();
						Globals.logString("		Done Off");
						Globals.logString("		Switch On  ...");
						switchOn();
						Globals.logString("		Done On");
						Globals.logString("Done Reset");
					}
				} catch (Exception e) {
					Globals.logException(e);
				}

				logString("POLLING Before Sleep " + theDelay);
				Thread.sleep(theDelay);
			} catch (Exception e) {
				Globals.logException(e);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see b01.l3.MessageListener#messageReceived(b01.l3.data.L3Message)
	 */
	public void messageReceived(L3Message message) {
		try {
			getDriver().makeSpecialCommentTreatmentBeforeSendingToLIS(message);
		} catch (Exception e) {
			Globals.logException(e);
		}
		if(Globals.getDBManager() != null){
			//This is the normal case only while testing the DBManager can be null so we just print a log
			message.upgradeMessageSamples_ToResultsAvailable(this);
		}else{
			//We print a log only in testing. Testing is the only case where DBManager == null
			StringBuffer stringBuffer = message.toStringBuffer();
			String str = stringBuffer.toString();
			Globals.logString(str);
		}
	}

	// ooooooooooooooooooooooooooooooooooo
	// oooooooooooooooooooooooooooooooooo
	// GET SET
	// oooooooooooooooooooooooooooooooooo
	// oooooooooooooooooooooooooooooooooo

	public String getCode() {
		return getFocProperty(InstrumentDesc.FLD_CODE).getString();
	}

	public void setCode(String cd) {
		FString cod = (FString) getFocProperty(InstrumentDesc.FLD_CODE);
		if (cod != null) {
			cod.setString(cd);
		}
	}

	public String getName() {
		return getFocProperty(InstrumentDesc.FLD_NAME).getString();
	}

	public void setName(String name) {
		FString n = (FString) getFocProperty(InstrumentDesc.FLD_NAME);
		if (n != null) {
			n.setString(name);
		}
	}

	public String getPropertiesFilePath() {
		return getFocProperty(InstrumentDesc.FLD_PROPERTIES_FILE_PATH)
				.getString();
	}

	public void setPropertiesFilePath(String path) {
		FString p = (FString) getFocProperty(InstrumentDesc.FLD_PROPERTIES_FILE_PATH);
		if (p != null) {
			p.setString(path);
		}
	}

	public void setDriverClassName(String clName) {
		FString cl = (FString) getFocProperty(InstrumentDesc.FLD_DRIVER_CLASS_NAME);
		if (cl != null) {
			cl.setString(clName);
		}
	}

	public Pool getPool() {
		// return pool;
		return (PoolKernel) getMasterObject();
	}

	public boolean isConnected() {
		return ((FBoolean) getFocProperty(InstrumentDesc.FLD_CONNECTED))
				.getBoolean();
	}

	public void setConnected(boolean connected) {
		((FBoolean) getFocProperty(InstrumentDesc.FLD_CONNECTED))
				.setBoolean(connected);
	}

	public int getMode() {
		return ((FInt) getFocProperty(InstrumentDesc.FLD_MODE)).getInteger();
	}

	public void setMode(int mode) {
		((FInt) getFocProperty(InstrumentDesc.FLD_MODE)).setInteger(mode);
	}

	public boolean isWaitForResultConfirmation() {
		FBoolean wait = (FBoolean) getFocProperty(InstrumentDesc.FLD_WAIT_FOR_RESULT_CONFIRMATION);
		return (wait != null) ? wait.getBoolean() : null;
	}

	public void setWaitForResultConfirmation(boolean wait) {
		FBoolean w = (FBoolean) getFocProperty(InstrumentDesc.FLD_WAIT_FOR_RESULT_CONFIRMATION);
		if (w != null) {
			w.setBoolean(wait);
		}
	}

	public void updateConnected(boolean connected) {
		FocDesc focDesc = getThisFocDesc();
		if (focDesc != null /* && isConnected() != connected */) {
			setConnected(connected);
			SQLUpdate sqlUpdate = new SQLUpdate(focDesc, this);
			sqlUpdate.addQueryField(InstrumentDesc.FLD_CONNECTED);
			sqlUpdate.execute();
		}
	}

	public void refreshConnected() {
		FocDesc focDesc = getThisFocDesc();
		if (focDesc != null /* && isConnected() != connected */) {
			// setConnected(connected);
			SQLFilter filter = new SQLFilter(this,
					SQLFilter.FILTER_ON_IDENTIFIER);
			SQLSelect sqlSelect = new SQLSelect(this, Instrument.getFocDesc(),
					filter);
			sqlSelect.addQueryField(FField.REF_FIELD_ID);
			sqlSelect.addQueryField(InstrumentDesc.FLD_CONNECTED);
			sqlSelect.execute();
			adjustColor(this, InstrumentDesc.FLD_LAUNCHED,
					InstrumentDesc.FLD_CONNECTED, InstrumentDesc.FLD_ON_HOLD);
		}
	}

	public void refreshLaunched() {
		boolean launched = false;
		try {
			launched = !getService().ping();
		} catch (Exception e) {
			Globals.logString("Normal exception in refreshLaunched");
			launched = false;
		}
		setPropertyBoolean(InstrumentDesc.FLD_LAUNCHED, launched);
		if (!launched) {
			setPropertyBoolean(InstrumentDesc.FLD_CONNECTED, false);
			updateConnected(false);
			adjustColor(this, InstrumentDesc.FLD_LAUNCHED,
					InstrumentDesc.FLD_CONNECTED, InstrumentDesc.FLD_ON_HOLD);
		}
	}

	public IDriver getDriver() throws Exception {
		if (driver == null) {
			String driverClassName = getPropertyString(InstrumentDesc.FLD_DRIVER_CLASS_NAME);
			Class driverClass = (Class) (DriverFactory.getInstance()
					.getDriver(driverClassName));
			if (driverClass == null) {
				throw new L3Exception("Driver not found. Driver class name = ("
						+ driverClassName + ")");
			}
			// The TempDriver is very important
			// In case an exception occures before the end of that function,
			// the driver would still = null, and this is what we want.
			IDriver tempDriver = (IDriver) driverClass.newInstance();
			if (tempDriver != null) {
				tempDriver.init(this, getProperties());
			}
			driver = tempDriver;
		}
		return driver;
	}

	public L3SampleTestJoinFilter getSampleListToSend() {
		L3SampleTestJoinFilter filter = L3SampleTestJoinDesc
				.newListWithFilter();
		filter.setInstrumentStatus(this, L3TestDesc.TEST_STATUS_AVAILABLE_IN_L3);
		return filter;
	}

	public L3SampleTestJoinFilter getSampleTestList_PendingTestsToBeResentWithNewTests() {
		if (sampleTestFilterPendingTestsToBeResentWithNewTests == null) {
			sampleTestFilterPendingTestsToBeResentWithNewTests = L3SampleTestJoinDesc
					.newListWithFilter();
			sampleTestFilterPendingTestsToBeResentWithNewTests
					.setInstrumentStatus(this, L3TestDesc.TEST_STATUS_ANALYSING);
		}
		return sampleTestFilterPendingTestsToBeResentWithNewTests;
	}

	public FocList getSupportedTestList() {
		if (supportedTestList == null) {
			FocLinkForeignKey link = new FocLinkForeignKey(
					TestLabelMap.getFocDesc(), TestLabelMapDesc.FLD_INSTRUMENT,
					true);
			supportedTestList = new FocList(this, link, null);
			supportedTestList.setFatherSubject(this);
		}
		supportedTestList.loadIfNotLoadedFromDB();

		return supportedTestList;
	}

	// For archive
	/*
	 * public void setCommitedSamplesAsUnused(int status){ perjeSamples(); }
	 */

	public void replyToExit() {
		try {
			switchOff();
			forceControler(true);
			validate(false);
		} catch (Exception e) {
			Globals.logException(e);
		}
	}

	// ooooooooooooooooooooooooooooooooooo
	// oooooooooooooooooooooooooooooooooo
	// LIST
	// oooooooooooooooooooooooooooooooooo
	// oooooooooooooooooooooooooooooooooo

	private static FocList list = null;

	public static FocList getList(int mode) {
		list = getList(list, getFocDesc(), mode);
		return list;
	}

	// ooooooooooooooooooooooooooooooooooo
	// oooooooooooooooooooooooooooooooooo
	// FOC
	// oooooooooooooooooooooooooooooooooo
	// oooooooooooooooooooooooooooooooooo

	@Override
	public FPanel newDetailsPanel(int viewID) {
		// return new InstrumentGuiDetailsPanel(viewID, this);
		detailsPanel = new InstrumentGuiDetailsPanel(viewID, this);
		return detailsPanel;
	}

	public static FPanel newBrowsePanel(FocList list, int viewID) {
		return new InstrumentGuiBrowsePanel(list, viewID);
	}

	private static FocDesc focDesc = null;

	public static FocDesc getFocDesc() {
		if (focDesc == null) {
			focDesc = new InstrumentDesc();
		}
		return focDesc;
	}

	// ------------------------------------------
	// ------------------------------------------
	// SERVICE Implementation
	// ------------------------------------------
	// ------------------------------------------

	private BService service = null;

	public boolean exit() {
		Globals.getApp().exit(true);
		return true;
	}

	public boolean isOn() {
		return isConnected();
	}

	public void refreshSwitchStatus() {
		refreshConnected();
	}

	public void refreshLaunchStatus() {
		refreshLaunched();
	}

	public boolean switchOff() {
		int numberOfAttempts = 0;
		int maxNbrOfAttempts = 1;
		boolean error = true;
		try {
			if (driver != null) {
				while (error && numberOfAttempts < maxNbrOfAttempts) {
					numberOfAttempts++;
					if (!driver.reserve()) {
						setConnected(false);
						driver.disconnect();
						error = false;
						driver.release();
						updateConnected(false);
						Globals.logString(getCode() + " : Disconnected");
					} else if (numberOfAttempts < maxNbrOfAttempts) {
						try {
							Thread.sleep(getPropertyInteger(InstrumentDesc.FLD_DELAY_TO_TRY_AGAIN_THE_DRIVER_RESERVE));
						} catch (Exception e) {
							logException(e);
						}
					}
				}
			}
		} catch (Exception e) {
			error = true;
			logException(e);
		}
		return error;
	}

	public boolean switchOn() {
		boolean error = false;
		try {
			getDriver().connect();
			if (senderThread == null) {
				senderThread = new Thread(this);
				senderThread.start();
			}
			setAsExitListener();
			updateConnected(true);
			Globals.logString(getCode() + " : Connected");
		} catch (Exception e) {
			logException(e);
			error = true;
		}
		return error;
	}

	public BService getService() {
		if (service == null) {
			if (L3Application.getAppInstance().getBackgroundTask() == L3Application.BACKGROUND_TASK_DRIVER) {
				int port = getPropertyInteger(InstrumentDesc.FLD_SERVICE_PORT);
				if (port > 0) {
					service = new BServiceServer(this, port, new LogInterface() {
								public void logException(Exception e) {
									((Instrument) getThis()).logException(e);
								}

								public void logString(String str) {
									((Instrument) getThis()).logString(str);
								}
							});
				}
			} else {
				PoolKernel pool = (PoolKernel) getPool();
				String host = pool
						.getPropertyString(PoolKernelDesc.FLD_SERVICE_HOST);

				L3Application l3App = L3Application.getAppInstance();
				service = new BServiceClient(
						this,
						host,
						getPropertyInteger(InstrumentDesc.FLD_SERVICE_PORT),
						l3App.getPropertyString(L3ApplicationDesc.FLD_REMOTE_LAUNCHER_HOST),
						l3App.getPropertyInteger(L3ApplicationDesc.FLD_REMOTE_LAUNCHER_PORT));
				((BServiceClient) service).setLogInterface(new LogInterface() {
					public void logException(Exception e) {
						((Instrument) getThis()).logException(e);
					}

					public void logString(String str) {
						((Instrument) getThis()).logString(str);
					}
				});
			}
		}
		return service;
	}

	public String getLaunchCommand() {
		L3Application l3App = L3Application.getAppInstance();
		String appDir = l3App
				.getPropertyString(L3ApplicationDesc.FLD_APPLICATION_DIRECTORY);
		return appDir + "/runDriver.bat " + getCode();
	}

}