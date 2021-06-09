/*
 * Created on 14 fvr. 2004
 */
package b01.foc.gui;

import b01.foc.*;
import java.awt.*;

import javax.swing.*;


/**
 * @author 01Barmaja
 */
public class FLoginPanel extends FPanel {
  public void paintComponents(Graphics g)  {
    ImageIcon img = Globals.getApp().getFocIcons().getLoginIcon();
    g.drawImage(img.getImage(), 0, 0, null);
    super.paintComponent(g);    
  }
  public void paint(Graphics g) {
    ImageIcon img = Globals.getApp().getFocIcons().getLoginIcon();
    g.drawImage(img.getImage(), 0, 0, null);
    setOpaque(false);
    super.paint(g);
  }
  public void print(Graphics g) {
    ImageIcon img = Globals.getApp().getFocIcons().getLoginIcon();
    g.drawImage(img.getImage(), 0, 0, null);
    super.print(g);
  }
}
