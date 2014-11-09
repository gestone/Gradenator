/**
 * This file is part of GraphView.
 *
 * GraphView is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GraphView is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with GraphView.  If not, see <http://www.gnu.org/licenses/lgpl.html>.
 *
 * Copyright Jonas Gehring
 */

package com.jjoe64.graphview;

import java.util.ArrayList;
import java.util.List;

/**
 * a graphview series.
 * holds the data, description and styles
 */
public class GraphViewSeries {
	/**
	 * graph series style: color and thickness
	 */
	static public class GraphViewSeriesStyle {
		public int color = 0xff0077cc;
		public int thickness = 3;
		private ValueDependentColor valueDependentColor;

		public GraphViewSeriesStyle() {
			super();
		}
		public GraphViewSeriesStyle(int color, int thickness) {
			super();
			this.color = color;
			this.thickness = thickness;
		}
		
		public ValueDependentColor getValueDependentColor() {
			return valueDependentColor;
		}
		
		/**
		 * the color depends on the value of the data.
		 * only possible in BarGraphView
		 * @param valueDependentColor
		 */
		public void setValueDependentColor(ValueDependentColor valueDependentColor) {
			this.valueDependentColor = valueDependentColor;
		}
	}

	final String description;
	final GraphViewSeriesStyle style;
	GraphViewDataInterface[] values;
	private final List<GraphView> graphViews = new ArrayList<GraphView>();

    /**
     * create a series with predefined values
     * @param values the values must be in the correct order! x-value has to be ASC. First the lowest x value and at least the highest x value.
     */
	public GraphViewSeries(GraphViewDataInterface[] values) {
		description = null;
		style = new GraphViewSeriesStyle();
		this.values = values;
        checkValueOrder();
	}

    /**
     * create a series with predefined options
     * @param description the name of the series
     * @param style custom style. can be null for default styles
     * @param values the values must be in the correct order! x-value has to be ASC. First the lowest x value and at least the highest x value.
     */
	public GraphViewSeries(String description, GraphViewSeriesStyle style, GraphViewDataInterface[] values) {
		super();
		this.description = description;
		if (style == null) {
			style = new GraphViewSeriesStyle();
		}
		this.style = style;
		this.values = values;
        checkValueOrder();
    }

	/**
	 * this graphview will be redrawn if data changes
	 * @param graphView
	 */
	public void addGraphView(GraphView graphView) {
		this.graphViews.add(graphView);
	}

	/**
	 * add one data to current data
     * the values must be in the correct order! x-value has to be ASC. First the lowest x value and at least the highest x value.
	 * @param value the new data to append
	 * @param scrollToEnd true => graphview will scroll to the end (maxX)
	 * @deprecated please use {@link #appendData(GraphViewDataInterface, boolean, int)} to avoid memory overflow
	 */
	@Deprecated
	public void appendData(GraphViewDataInterface value, boolean scrollToEnd) {
        if (values.length > 0 && value.getX() < values[values.length-1].getX()) {
            throw new IllegalArgumentException("new x-value must be greater then the last value. x-values has to be ordered in ASC.");
        }
		GraphViewDataInterface[] newValues = new GraphViewDataInterface[values.length + 1];
		int offset = values.length;
		System.arraycopy(values, 0, newValues, 0, offset);

		newValues[values.length] = value;
		values = newValues;
		for (GraphView g : graphViews) {
			if (scrollToEnd) {
				g.scrollToEnd();
			}
		}
	}

	/**
	 * add one data to current data
     * the values must be in the correct order! x-value has to be ASC. First the lowest x value and at least the highest x value.
     *
	 * @param value the new data to append. Important: the new value must be higher then the last value (x).
	 * @param scrollToEnd true => graphview will scroll to the end (maxX)
	 * @param maxDataCount if max data count is reached, the oldest data value will be lost
	 */
	public void appendData(GraphViewDataInterface value, boolean scrollToEnd, int maxDataCount) {
        if (values.length > 0 && value.getX() < values[values.length-1].getX()) {
            throw new IllegalArgumentException("new x-value must be greater then the last value. x-values has to be ordered in ASC.");
        }
		synchronized (values) {
			int curDataCount = values.length;
			GraphViewDataInterface[] newValues;
			if (curDataCount < maxDataCount) {
				// enough space
				newValues = new GraphViewDataInterface[curDataCount + 1];
				System.arraycopy(values, 0, newValues, 0, curDataCount);
				// append new data
				newValues[curDataCount] = value;
			} else {
				// we have to trim one data
				newValues = new GraphViewDataInterface[maxDataCount];
				System.arraycopy(values, curDataCount-maxDataCount+1, newValues, 0, maxDataCount-1);
				// append new data
				newValues[maxDataCount-1] = value;
			}
			values = newValues;
		}

		// update linked graph views
		for (GraphView g : graphViews) {
			if (scrollToEnd) {
				g.scrollToEnd();
			}
		}
	}

	/**
	 * @return series styles. never null
	 */
	public GraphViewSeriesStyle getStyle() {
		return style;
	}

	/**
	 * you should use {@link GraphView#removeSeries(GraphViewSeries)}
	 * @param graphView
	 */
	public void removeGraphView(GraphView graphView) {
		graphViews.remove(graphView);
	}

	/**
	 * clears the current data and set the new.
     *
	 * redraws the graphview(s)
	 * @param values the values must be in the correct order! x-value has to be ASC. First the lowest x value and at least the highest x value.
	 */
	public void resetData(GraphViewDataInterface[] values) {
		this.values = values;
        checkValueOrder();
        for (GraphView g : graphViews) {
			g.redrawAll();
		}
	}

    private void checkValueOrder() {
        if (values.length>1) {
            double lx = values[0].getX();
            for (int i=1;i<values.length;i++) {
                if (values[i].getX() != Double.NaN) {
                    if (lx > values[i].getX()) {
                        throw new IllegalArgumentException("The order of the values is not correct. X-Values have to be ordered ASC. First the lowest x value and at least the highest x value.");
                    }
                    lx = values[i].getX();
                }
            }
        }
    }
}
