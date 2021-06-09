package b01.foc.gui.table.cellControler.renderer;

import java.awt.Graphics;

import javax.swing.JComboBox;
import javax.swing.JTable;

@SuppressWarnings("serial")
public class FGComboBoxInTable extends JComboBox{

	private JTable table = null;
	
	public FGComboBoxInTable(JTable table) {
		super();
		this.table = table;
	}
	
	public void dispose(){
		table = null;
	}

	@Override
	public void setBounds(int x, int y, int width, int height) {
		super.setBounds(x, y, width, height+getShiftDistance());
	}

	@Override
	public void paint(Graphics g) {
		g.translate(0, -getShiftDistance());
		super.paint(g);
	}

	private int getShiftDistance(){
		int distance = 0;
		int preferredHeight = getPreferredSize().height;
		if(table.getRowHeight() < preferredHeight){
			distance = preferredHeight - table.getRowHeight();
			if(distance % 2 == 1){
				distance += 1;
			}
			distance = distance / 2;
		}
		return distance;
	}
}
