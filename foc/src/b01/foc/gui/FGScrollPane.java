package b01.foc.gui;

import java.awt.Component;
import javax.swing.JScrollPane;

@SuppressWarnings("serial")
public class FGScrollPane extends JScrollPane {

  private Component view = null;
  
  public FGScrollPane(Component view) {
    super(view);
    this.view = view;
  }

  public Component getView() {
    return view;
  }
  
  public void dispose(){
    view = null;
  }
}
