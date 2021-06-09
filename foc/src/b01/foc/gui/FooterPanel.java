package b01.foc.gui;

import b01.foc.*;

import java.awt.event.*;
import java.awt.*;
import javax.swing.*;

/**
 * JFrame object, is the only frame of the application
 */
public class FooterPanel extends JPanel {

  private JButton goBack;
  private JTextField textField;

  /**
   * Constructor, setts the title, and the window closing istener
   */
  public FooterPanel() {
    setLayout(new GridBagLayout());

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.gridwidth = 1;
    gbc.insets.left = 0;
    gbc.insets.right = 0;
    gbc.ipadx = 0;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.weightx = 0;
    /*
    GoBackListener goBackListener = new GoBackListener();
    this.getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "goBack");
    this.getActionMap().put("goBack", goBackListener);
    */
    
    /*
    goBack = new JButton("Back ", Globals.getIcons().getBackIcon());
    goBack.setBorder(BorderFactory.createEtchedBorder());
    goBack.addActionListener(goBackListener);
    add(goBack, gbc);
    */
    gbc.gridx = 1;
    gbc.gridy = 0;
    gbc.gridwidth = 1;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.weightx = 1;
    gbc.fill = GridBagConstraints.BOTH;
    textField = new JTextField(30);
    textField.setEditable(false);
    textField.setBorder(BorderFactory.createLoweredBevelBorder());
    add(textField, gbc);

    setBackground(Color.lightGray);
    setBorder(BorderFactory.createLoweredBevelBorder());
  }

  public void setFrameFooter(String str) {
    textField.setText(str);
  }

  private class GoBackListener extends AbstractAction implements ActionListener {
    public void actionPerformed(ActionEvent evt) {
      Globals.getDisplayManager().goBack();
    }
  }
}