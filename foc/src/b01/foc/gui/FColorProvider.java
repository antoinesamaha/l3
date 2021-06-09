package b01.foc.gui;

import java.awt.Color;
import java.awt.GradientPaint;
import java.util.ArrayList;

public class FColorProvider {
	private static ArrayList<Color> colorArray = null;
	
	private static ArrayList<Color> getColorArray(){
		if(colorArray == null){
			colorArray = new ArrayList<Color>();
			
			colorArray.add(new Color(128,	208,	255));
			colorArray.add(new Color(128,	255,	255));
			colorArray.add(new Color(128,	255,	159));
			colorArray.add(new Color(144,	255,	128));
			colorArray.add(new Color(191,	255,  128));
			colorArray.add(new Color(240,	255,	128));
			colorArray.add(new Color(255,	223,	128));
			colorArray.add(new Color(255,	174,	128));
			
			/*
			colorArray.add(new Color(165, 165, 10));
			colorArray.add(new Color(203, 203, 20));
			colorArray.add(new Color(230, 230, 40));
			colorArray.add(new Color(255, 255, 66));

			colorArray.add(new Color(165, 165, 10));
			colorArray.add(new Color(203, 203, 20));
			colorArray.add(new Color(230, 230, 40));
			colorArray.add(new Color(255, 255, 66));
			
			colorArray.add(new Color(165, 165, 10));
			colorArray.add(new Color(203, 203, 20));
			colorArray.add(new Color(230, 230, 40));
			colorArray.add(new Color(255, 255, 66));
			
			colorArray.add(new Color(165, 165, 10));
			colorArray.add(new Color(203, 203, 20));
			colorArray.add(new Color(230, 230, 40));
			colorArray.add(new Color(255, 255, 66));
			*/
		}
		return colorArray;
	}
	
	public static Color getColorAt(int at){
		return getColorArray().get(at % getColorArray().size());
	}
	
	private static GradientPaint GP_BLUE_CLEAR = null;
	public static GradientPaint getGradientPaintBlueClear(){
		if(GP_BLUE_CLEAR == null){
			GP_BLUE_CLEAR = new GradientPaint(0.0f, 0.0f, new Color(0x22, 0xFF, 0xFF), 0.0f, 0.0f, new Color(0x88, 0xFF, 0xFF));
		}
		return GP_BLUE_CLEAR;
	}
	
	private static GradientPaint GP_BLUE = null;
	public static GradientPaint getGradientPaintBlue(){
		if(GP_BLUE == null){
			GP_BLUE = new GradientPaint(0.0f, 0.0f, new Color(0x22, 0x22, 0xFF), 0.0f, 0.0f, new Color(0x88, 0x88, 0xFF));
		}
		return GP_BLUE;
	}

	private static GradientPaint GP_RED = null;
	public static GradientPaint getGradientPaintRed(){
		if(GP_RED == null){
			GP_RED = new GradientPaint(0.0f, 0.0f, new Color(0xFF, 0x22, 0x22), 0.0f, 0.0f, new Color(0xFF, 0x88, 0x88));
		}
		return GP_RED;
	}

	private static GradientPaint GP_ROSE = null;
	public static GradientPaint getGradientPaintRose(){
		if(GP_ROSE == null){
			//GP_ROSE = new GradientPaint(0.0f, 0.0f, new Color(0xFF, 0x77, 0x77), 0.0f, 0.0f, new Color(0xFF, 0xBB, 0xBB));
			GP_ROSE = new GradientPaint(0.0f, 0.0f, new Color(0xFF, 0x22, 0xFF), 0.0f, 0.0f, new Color(0xFF, 0x88, 0xFF));
		}
		return GP_ROSE;
	}

	private static GradientPaint GP_YELLOW = null;
	public static GradientPaint getGradientPaintYellow(){
		if(GP_YELLOW == null){
			GP_YELLOW = new GradientPaint(0.0f, 0.0f, new Color(0xFF, 0xFF, 0x22), 0.0f, 0.0f, new Color(0xFF, 0xFF, 0x88));
		}
		return GP_YELLOW;
	}

	private static GradientPaint GP_GREEN = null;
	public static GradientPaint getGradientPaintGreen(){
		if(GP_GREEN == null){
			GP_GREEN = new GradientPaint(0.0f, 0.0f, new Color(0x22, 0xFF, 0x22), 0.0f, 0.0f, new Color(0x88, 0xFF, 0x88));
		}
		return GP_GREEN;
	}

}
