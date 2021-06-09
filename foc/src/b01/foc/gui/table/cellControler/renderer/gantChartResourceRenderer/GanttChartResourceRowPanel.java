package b01.foc.gui.table.cellControler.renderer.gantChartResourceRenderer;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.sql.Date;
import java.util.ArrayList;
import b01.foc.calendar.FCalendar;
import b01.foc.gui.table.cellControler.renderer.gantChartRenderer.BasicGanttChartRowPanel;
import b01.foc.gui.table.cellControler.renderer.gantChartRenderer.BasicGanttScale;

@SuppressWarnings("serial")
public class GanttChartResourceRowPanel extends BasicGanttChartRowPanel {
	private IGantChartResourceDrawingInfo drawingInfo = null;
	
	public GanttChartResourceRowPanel(BasicGanttScale gantScale){
		super(gantScale);
	}
	
	public void dispose(){
		super.dispose();
		this.drawingInfo = null;
	}
	
	private ArrayList<Color> getColorArrayForActiviyAt(int i){
		return this.drawingInfo.getColorArrayForActivityAt(i);
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		fillActivities(g);
		drawWhiteAreasForNonWorkingTimesAndGrayForNonWorkingDays(g);
		drawActivitiesLabel(g);
	}
	
	private void fillActivities(Graphics g){
		int activityCount = this.drawingInfo.getActivityCount();
		for(int i = 0; i < activityCount; i++){
			int activityX = gantScale.getPixelsForDate(this.drawingInfo.getActivityStartDateAt(i));				
			//int activityWidth = gantScale.getPixelsForMinutes(this.drawingInfo.getActivityDurationAt(i));
			g.setColor(getColorArrayForActiviyAt(i).get(0));
			
			Date lastDate = new Date(this.drawingInfo.getActivityStartDateAt(i).getTime() + ((long)drawingInfo.getActivityDurationAt(i) * 60 * 1000));
			int activityX2 = gantScale.getPixelsForDate(lastDate);
			int activityWidth = activityX2 - activityX;
			g.fillRect(activityX, 0, activityWidth, this.table.getRowHeight()-0);
			//g.fillRect(activityX, 2, activityWidth, this.table.getRowHeight()-4);
			
		}
	}
	
	private void drawActivitiesLabel(Graphics g){
		int activityCount = this.drawingInfo.getActivityCount();
		for(int i = 0; i < activityCount; i++){
			int activityX = gantScale.getPixelsForDate(this.drawingInfo.getActivityStartDateAt(i));				
			
			Date lastDate = new Date(this.drawingInfo.getActivityStartDateAt(i).getTime() + ((long)drawingInfo.getActivityDurationAt(i) * 60 * 1000));
			int activityX2 = gantScale.getPixelsForDate(lastDate);
			int activityWidth = activityX2 - activityX;
			
			String label = this.drawingInfo.getActivityLabelAt(i);
			Graphics gForLabel = g.create();
			Font smalerFont = gForLabel.getFont();
			FontMetrics fontMetrics = gForLabel.getFontMetrics(smalerFont);
			int labelWidth = fontMetrics.stringWidth(label);
			while(labelWidth > activityWidth){
				float smalerFontSize = (float)(smalerFont.getSize() - 1);
				smalerFont = smalerFont.deriveFont(smalerFontSize);
				fontMetrics = gForLabel.getFontMetrics(smalerFont);
				labelWidth = fontMetrics.stringWidth(label);
			}
			
			gForLabel.setFont(smalerFont);
			gForLabel.setColor(Color.RED);
			double labelHeigtDouble = fontMetrics.getStringBounds(label, gForLabel).getHeight();
			labelHeigtDouble = Math.ceil(labelHeigtDouble);
			int labelHeight = (int)labelHeigtDouble;
			int labelY = (table.getRowHeight() + labelHeight)/2 - 4;
			//int labelY = table.getRowHeight()/2 +  5;
			int labelX = (activityWidth - labelWidth)/2;
			gForLabel.drawString(label, activityX + labelX, labelY);
		}
	}
	
	private void drawWhiteAreasForNonWorkingTimesAndGrayForNonWorkingDays(Graphics g){
		Date date = (Date)gantScale.getStartDate().clone();
		Date lastDate = gantScale.getEndDate();
		while(date.getTime() < lastDate.getTime()){
			int x = gantScale.getPixelsForDate(date);
			int x24 = gantScale.getPixelsForDate(new Date(date.getTime() + ((FCalendar.MILLISECONDS_IN_DAY / 24) * 24)));
			if(gantScale.getCalandar().isWorkingDay(date)){
				g.setColor(Color.WHITE);
				int x8 = gantScale.getPixelsForDate(new Date(date.getTime() + ((FCalendar.MILLISECONDS_IN_DAY / 24) * 8)));
				g.fillRect(x, 0                         , x8 - x, 2*(table.getRowHeight()/5) + 1);
				g.fillRect(x, 3*(table.getRowHeight()/5), x8 - x, 2*(table.getRowHeight()/5) + 1);
				
				int x17 = gantScale.getPixelsForDate(new Date(date.getTime() + ((FCalendar.MILLISECONDS_IN_DAY / 24) * 17)));
				
				g.fillRect(x17, 0                         , x24 - x17, 2*(table.getRowHeight()/5) + 1);
				g.fillRect(x17, 3*(table.getRowHeight()/5), x24 - x17, 2*(table.getRowHeight()/5) + 1);
				
				int xVar = x8;
				int xStep = (x17 - x8) / 9;
				g.setColor(new Color(192, 192, 192, 100));
				while(xVar < x17){
					g.drawLine(xVar, 0, xVar, table.getRowHeight());
					xVar += xStep;
				}
			}else{
				g.setColor(Color.LIGHT_GRAY);
				g.fillRect(x, 0, x24 - x, 2*(table.getRowHeight()/5) + 1);
				g.fillRect(x, 3*(table.getRowHeight()/5), x24 - x, 2*(table.getRowHeight()/5) + 1);
			}
			g.setColor(Color.GRAY);
			g.drawLine(x, 0, x, table.getRowHeight());
			
			date.setTime(date.getTime()+FCalendar.MILLISECONDS_IN_DAY);
		}
	}
	
  public void setDrawingInfo(IGantChartResourceDrawingInfo drawingInfo) {
    this.drawingInfo = drawingInfo;
  }
	
}
