/*
 * Created on 14-Feb-2005
 */
package b01.foc.desc.field;

/**
 * @author 01Barmaja
 */
public class FMultipleChoiceItem implements FMultipleChoiceItemInterface{
  private int id = 0;
  private String title = null;

  public FMultipleChoiceItem(int id, String title) {
    this.id = id;
    this.title = title;
  }

  /**
   * @return Returns the id.
   */
  public int getId() {
    return id;
  }

  /**
   * @return Returns the title.
   */
  public String getTitle() {
    return title;
  }

  public String toString() {
    return title;
  }
}
