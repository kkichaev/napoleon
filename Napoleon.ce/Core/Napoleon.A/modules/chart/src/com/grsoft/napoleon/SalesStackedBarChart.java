/**
 * Copyright (C) 2009, 2010 SC 4ViewSoft SRL
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.grsoft.napoleon;

import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.renderer.XYMultipleSeriesRenderer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint.Align;

/**
 * Sales demo bar chart.
 */
public class SalesStackedBarChart extends AbstractDemoChart {
	
	private String name;
	private List<String> titles;
	private List<double[]> values;
	private List<String> labels;
	private int background;
	private List<Integer> colors;

	public SalesStackedBarChart(String name, List<String> titles,
			  List<double[]> values, List<String> labels, 
			  int background, List<Integer> colors){
		this.name = name;
		this.titles = titles;
		this.values = values;
		this.labels = labels;
		this.background = background;
		this.colors = colors;
	}
  /**
   * Returns the chart name.
   * 
   * @return the chart name
   */
  public String getName() {
    return name;
  }

  /**
   * Returns the chart description.
   * 
   * @return the chart description
   */
  public String getDesc() {
    return "";
  }

  /**
   * Executes the chart demo.
   * 
   * @param context the context
   * @return the built intent
   */
  public Intent execute(Context context) {
    String[] titlesarray = new String[titles.size()];
    titles.toArray(titlesarray);
    int[] colorsArray = new int[colors.size()]; 
    
    for(int i = 0; i < colors.size(); i++)
    	colorsArray[i] = colors.get(i);
    
    XYMultipleSeriesRenderer renderer = buildBarRenderer(colorsArray);
    setChartSettings(renderer, getName(), getXCaption(), getYCaption(), 0.5,
        getRangeX(), 0, getRangeY(), Color.GRAY, Color.LTGRAY);
    
    for(int i = 0; i < colors.size(); i++)
    	renderer.getSeriesRendererAt(i).setDisplayChartValues(true);
    
    renderer.setXLabels(0);
    renderer.setYLabels(10);
    renderer.setXLabelsAlign(Align.LEFT);
    renderer.setYLabelsAlign(Align.LEFT);
    renderer.setPanEnabled(true, false);
    renderer.setZoomEnabled(true);
    renderer.setZoomRate(1.1f);
    renderer.setBarSpacing(0.5f);
    renderer.setXLabelsAngle(90.0f);
    renderer.setApplyBackgroundColor(true);
    renderer.setBackgroundColor(background);
    renderer.setMarginsColor(background);
    
    for(int i = 0; i < labels.size(); i++)
    	renderer.addXTextLabel(i + 1.0, labels.get(i));
    
    return ChartFactory.getBarChartIntent(context, buildBarDataset(titlesarray, values), renderer,
        Type.DEFAULT);
  }
  
	private String getYCaption() {
	return "";
	}
	
	private String getXCaption() {
	return "";
	}
	
	private double getRangeX() {
		return labels.size() + 0.5;
	}
	
	private double getRangeY() {
		double result = 0.0;
		for(double[] array : values)
			for(double val : array)
				if(val > result)
					result = val;
		
		return result + 0.5;
	}

}
