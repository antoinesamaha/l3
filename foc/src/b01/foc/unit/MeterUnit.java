/*
 * Created on 01-Feb-2005
 */
package b01.foc.unit;

/**
 * @author 01Barmaja
 */
public class MeterUnit implements Unit {

  public String getTitle() {
    return "Meter";
  }

  public String getName() {
    return "m";
  }

  public int getID() {
    return Unit.METER;
  }
}