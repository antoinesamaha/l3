/*
 * Created on 15 fvr. 2004
 */
package b01.foc.property;

import java.text.Format;

import b01.foc.desc.*;

/**
 * @author 01Barmaja
 */
public class FLong extends FProperty {
  protected long lVal;
  private long backupLong = 0;

  public FLong(FocObject focObj, int fieldID, long lVal) {
    super(focObj, fieldID);
    this.lVal = lVal;
  }

  public int hashCode() {
    return (Long.valueOf(lVal).hashCode());
  }

  public int compareTo(FProperty prop) {
  	int comp = 1;
    if (prop != null) {
      if (prop.getLong() > getLong()) {
        comp = -1;
      } else if (prop.getLong() < getLong()) {
        comp = 1;
      } else {
        comp = 0;
      }
    }
    return comp;
  }

  public boolean isEmpty(){
    return lVal == 0;
  }

  public long getLong() {
    return lVal;
  }

  public void setLong(long lVal) {
    if(lVal != this.lVal){
      this.lVal = lVal;
      notifyListeners();
    }
  }

  public String getString() {
    return String.valueOf(lVal);
  }

  public void setString(String str) {
    if (str == null || str.compareTo("") == 0) {
      setLong(0);
    } else {
      setLong(Integer.parseInt(str));
    }
  }

  public void backup() {
   backupLong = this.lVal;
  }

  public void restore() {
    this.lVal = backupLong;
  }

  public double getDouble() {
    return (double) lVal;
  }

  public void setDouble(double dVal) {
    setLong((long) dVal);
  }

  public void setObject(Object obj) {
    if (obj != null) {
      setLong(((Long) obj).longValue());
    }
  }

  public Object getObject() {
    return Long.valueOf(getLong());
  }

  public Object getTableDisplayObject(Format format) {
    return getString();
  }

  public void setTableDisplayObject(Object obj, Format format) {
    setString((String)obj);
  }
}
