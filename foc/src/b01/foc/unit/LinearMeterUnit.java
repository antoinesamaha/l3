/*
 * Created on 01-Feb-2005
 */
package b01.foc.unit;

/**
 * @author 01Barmaja
 */
public class LinearMeterUnit implements Unit {

  public String getTitle() {
    return "Linear meter";
  }

  public String getName() {
    return "Lm";
  }

  public int getID() {
    return Unit.LM;
  }
}