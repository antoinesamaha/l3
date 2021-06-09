/*
 * Created on 01-Feb-2005
 */
package b01.foc.unit;

/**
 * @author 01Barmaja
 */
public class SquareMeterUnit implements Unit {

  public String getTitle() {
    return "Square meter";
  }

  public String getName() {
    return "Sqm";
  }

  public int getID() {
    return Unit.SQM;
  }
}