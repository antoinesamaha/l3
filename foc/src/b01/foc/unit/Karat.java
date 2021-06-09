/*
 * Created on 01-Feb-2005
 */
package b01.foc.unit;

/**
 * @author 01Barmaja
 */
public class Karat implements Unit {

  public String getTitle() {
    return "Kilogram";
  }

  public String getName() {
    return "Kg";
  }

  public int getID() {
    return Unit.KG;
  }
}