/*
 * Created on 20-Feb-2005
 */
package b01.foc.gui;

import java.awt.Component;

import javax.swing.*;

import b01.foc.ConfigInfo;

/**
 * @author 01Barmaja
 */
@SuppressWarnings("serial")
public class FGTabbedPane extends JTabbedPane {

	public FGTabbedPane() {
		super();
	}
	
	public FGTabbedPane(String name) {
		super();
		setName(name);
		if(ConfigInfo.isUnitDevMode()){
			setToolTipText(name);
		}
	}

	@Override
	public Component add(String title, Component component) {
		Component c = super.add(title, component);
		c.setName(title);
		return c;
	}
	
}
