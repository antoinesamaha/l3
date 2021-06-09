/*
 * Created on 01-Feb-2005
 */
package b01.foc.unit;

/**
 * @author 01Barmaja
 */
public class CubicMeterUnit implements Unit {

  public String getTitle() {
    return "Cubic meter";
  }

  public String getName() {
    return "Cum";
  }

  public int getID() {
    return Unit.CUM;
  }
}