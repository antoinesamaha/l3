/*
 * Created on Jun 14, 2006
 */
package b01.l3.data;

import b01.foc.Globals;
import b01.foc.db.SQLDelete;
import b01.foc.db.SQLFilter;
import b01.foc.db.SQLUpdate;
import b01.foc.desc.FocConstructor;
import b01.foc.desc.FocDesc;
import b01.foc.desc.FocObject;
import b01.foc.gui.FPanel;
import b01.foc.list.FocList;
import b01.foc.property.FBoolean;
import b01.foc.property.FDouble;
import b01.foc.property.FString;
import b01.foc.util.FocMath;
import b01.l3.Instrument;

/**
 * @author 01Barmaja
 */
public class L3Test extends FocObject {
	
	private double roundingPrecision = 0;

	public L3Test(String label) {
		super(getFocDesc());
		newFocProperties();
		initFocProperties(label);
	}

	public L3Test(FocConstructor constr) {
		super(constr);
		newFocProperties();
		initFocProperties("");
	}

	private void initFocProperties(String label) {
		setPropertyString(L3TestDesc.FLD_LABEL, label);
	}

	public void dispose() {
		super.dispose();
	}

	public Instrument getInstrument() {
		return (Instrument) getPropertyObject(L3TestDesc.FLD_DISPATCH_INSTRUMENT);
	}

	public void setInstrument(Instrument instrument) {
		setPropertyObject(L3TestDesc.FLD_DISPATCH_INSTRUMENT, instrument);
	}

	public void setStatus(int status) {
		setPropertyMultiChoice(L3TestDesc.FLD_STATUS, status);
	}

	public void setBlocked(boolean blocked) {
		setPropertyBoolean(L3TestDesc.FLD_BLOCKED, blocked);
	}

	public int getStatus() {
		return getPropertyMultiChoice(L3TestDesc.FLD_STATUS);
	}

	public boolean isBlocked() {
		return getPropertyBoolean(L3TestDesc.FLD_BLOCKED);
	}

	public void setNotificationMessage(String message) {
		if (message.length() > L3TestDesc.LEN_MESSAGE) {
			message = message.substring(0, L3TestDesc.LEN_MESSAGE - 1);
		}
		setPropertyString(L3TestDesc.FLD_MESSAGE, message);
	}

	public String getNotificationMessage() {
		return getPropertyString(L3TestDesc.FLD_MESSAGE);
	}

	public void updateStatus(int status) {
		FocDesc focDesc = getThisFocDesc();
		if (focDesc != null) {
			setStatus(status);
			SQLUpdate sqlUpdate = new SQLUpdate(focDesc, this);
			sqlUpdate.addQueryField(L3TestDesc.FLD_STATUS);
			sqlUpdate.execute();
		}
		// backup();
	}

	public void updateBlocked(Boolean blocked) {
		FocDesc focDesc = getThisFocDesc();
		if (focDesc != null) {
			setBlocked(blocked);
			SQLUpdate sqlUpdate = new SQLUpdate(focDesc, this);
			sqlUpdate.addQueryField(L3TestDesc.FLD_BLOCKED);
			sqlUpdate.execute();
		}
		// backup();
	}

	public void updateNotificationMessage(String message) {
		FocDesc focDesc = getThisFocDesc();
		if (focDesc != null) {
			setNotificationMessage(message);
			SQLUpdate sqlUpdate = new SQLUpdate(focDesc, this);
			sqlUpdate.addQueryField(L3TestDesc.FLD_MESSAGE);
			sqlUpdate.execute();
		}
		// backup();
	}

	public StringBuffer toStringBuffer() {
		StringBuffer b = new StringBuffer();
		b.append(" isOk=" + isResultOk() + " Label=" + getLabel() + " Value=" + getValue() + " Notes=" + getValueNotes());
		return b;
	}

	public String getLabel() {
		FString label = (FString) getFocProperty(L3TestDesc.FLD_LABEL);
		return (label != null) ? label.getString() : "";
	}

	public void setLabel(String value) {
		FString label = (FString) getFocProperty(L3TestDesc.FLD_LABEL);
		if (label != null) {
			label.setString(value);
		}
	}

	public String getValueNotes() {
		FString label = (FString) getFocProperty(L3TestDesc.FLD_VALUE_NOTES);
		return (label != null) ? label.getString() : "";
	}

	public void setValueNotes(String value) {
		FString label = (FString) getFocProperty(L3TestDesc.FLD_VALUE_NOTES);
		if (label != null) {
			label.setString(value);
		}
	}

	public boolean isResultOk() {
		FBoolean res = (FBoolean) getFocProperty(L3TestDesc.FLD_RESULT_OK);
		return (res != null) ? res.getBoolean() : null;
	}

	public void setResultOk(boolean resultOk) {
		FBoolean res = (FBoolean) getFocProperty(L3TestDesc.FLD_RESULT_OK);
		if (res != null) {
			res.setBoolean(resultOk);
		}
	}

	public double getValue() {
		FDouble value = (FDouble) getFocProperty(L3TestDesc.FLD_VALUE);
		return (value != null) ? value.getDouble() : 0;
	}

	public void setValue(double value) {
  	FDouble val = (FDouble) getFocProperty(L3TestDesc.FLD_VALUE);
    if(val != null){
      if(getRoundingPrecision() != 0){
      	Globals.logString("Rounding value = "+value+" to precision = "+getRoundingPrecision());
      	value = FocMath.round(value, getRoundingPrecision());
      	Globals.logString("         Rounding result = "+value);
      }
      val.setDouble(value);
    }
  }
	
	public double getRoundingPrecision() {
		return roundingPrecision;
	}

	public void setRoundingPrecision(double roundingPrecision) {
		this.roundingPrecision = roundingPrecision;
	}

	public void setAlarm(int alarm) {
		setPropertyMultiChoice(L3TestDesc.FLD_ALARM, alarm);
	}

	public int getAlarm() {
		return getPropertyMultiChoice(L3TestDesc.FLD_ALARM);
	}

	public void setPriority(String priority) {
		setPropertyString(L3TestDesc.FLD_PRIORITY, priority);
	}

	public String getPriority() {
		return getPropertyString(L3TestDesc.FLD_PRIORITY);
	}

	public void setVerificationPendingFlag(boolean verifPending) {
		setPropertyBoolean(L3TestDesc.FLD_VERIFICATION_PENDING, verifPending);
	}

	public boolean isVerificationPendingFlag() {
		return getPropertyBoolean(L3TestDesc.FLD_VERIFICATION_PENDING);
	}

	public String getUnitLabel() {
		FString unit = (FString) getFocProperty(L3TestDesc.FLD_UNIT_LABEL);
		return (unit != null) ? unit.getString() : "";
	}

	public void setUnitLabel(String unitLabel) {
		FString ul = (FString) getFocProperty(L3TestDesc.FLD_UNIT_LABEL);
		if (ul != null) {
			ul.setString(unitLabel);
		}
	}

	public void copy(L3Test t) {
		setLabel(t.getLabel());
		setValue(t.getValue());
		setValueNotes(t.getValueNotes());
		setResultOk(t.isResultOk());
		setUnitLabel(t.getUnitLabel());
		setPropertyString(L3TestDesc.FLD_MESSAGE, t.getPropertyString(L3TestDesc.FLD_MESSAGE));
	}

	public void copyAndBackup(L3Test t) {
		copy(t);
		backup();
	}

	public void remove() {
		SQLFilter deleteFilter = new SQLFilter(this, SQLFilter.FILTER_ON_SELECTED);
		StringBuffer deleteCondition = new StringBuffer("REF = " + getReference().getInteger());
		deleteFilter.addAdditionalWhere(deleteCondition);
		SQLDelete delete = new SQLDelete(getFocDesc(), deleteFilter);
		delete.execute();
	}

	// ---------------------------------
	// PANEL
	// ---------------------------------
	public FPanel newDetailsPanel(int viewID) {
		return new L3TestGuiDetailsPanel(viewID, this);
	};

	public static FPanel newBrowsePanel(FocList list, int viewID) {
		return new L3TestGuiBrowsePanel(list, viewID);
	}

	// ooooooooooooooooooooooooooooooooooo
	// oooooooooooooooooooooooooooooooooo
	// DESCRIPTION
	// oooooooooooooooooooooooooooooooooo
	// oooooooooooooooooooooooooooooooooo

	private static FocDesc focDesc = null;

	public static FocDesc getFocDesc() {
		if (focDesc == null) {
			focDesc = new L3TestDesc();
		}
		return focDesc;
	}
}
