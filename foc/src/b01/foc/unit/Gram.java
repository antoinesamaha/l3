/*
 * Created on 01-Feb-2005
 */
package b01.foc.unit;

/**
 * @author 01Barmaja
 */
public class Gram implements Unit {

  public String getTitle() {
    return "Gram";
  }

  public String getName() {
    return "Gr";
  }

  public int getID() {
    return Unit.GRAM;
  }
}