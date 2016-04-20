/*
 * ##############################################################################
 * #  Copyright (c) 2016 by Patrick Kutch https://github.com/PatrickKutch
 * # 
 * # Licensed under the Apache License, Version 2.0 (the "License");
 * #  you may not use this file except in compliance with the License.
 * #  You may obtain a copy of the License at
 * # 
 * #      http://www.apache.org/licenses/LICENSE-2.0
 * # 
 * #  Unless required by applicable law or agreed to in writing, software
 * #  distributed under the License is distributed on an "AS IS" BASIS,
 * #  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * #  See the License for the specific language governing permissions and
 * #  limitations under the License.
 * ##############################################################################
 * #    File Abstract: 
 * #
 * #
 * ##############################################################################
 */
package kutch.biff.marvin.widget;

import java.util.ArrayList;
import javafx.scene.chart.Chart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import kutch.biff.marvin.utility.FrameworkNode;
import kutch.biff.marvin.utility.SeriesDataSet;

/**
 *
 * @author Patrick Kutch
 */
abstract public class BaseChartWidget extends BaseWidget
{
    private Chart _chart;    
    //private AreaChart<Number,Number> _chart;    
    private final ArrayList<SeriesDataSet> _Series;
    private NumberAxis _xAxis;
    private NumberAxis _yAxis;
    private String xAxisLabel,yAxisLabel;
    private int xAxisMaxCount,yAxisMaxCount;
    double yAxisMaxValue,yAxisMinValue;
    private boolean _Animated;
    private int xAxisMinorTick,yAxisMinorTick;
    private double xAxisMajorTick,yAxisMajorTick;
    private boolean xAxisTickVisible,yAxisTickVisible;
    
    public BaseChartWidget()
    {
        xAxisLabel="";
        yAxisLabel = "";
        xAxisMaxCount = 20;
        yAxisMaxCount = 0;
        yAxisMaxValue = 100;
        yAxisMinValue = 0;
        _Animated = false;
        yAxisMajorTick = 0;
        yAxisMinorTick = 0;
        xAxisMajorTick=0;
        xAxisMinorTick = 0;
        xAxisTickVisible=true;
        yAxisTickVisible = true;
        
        _Series = new ArrayList<>();      
        _chart = null;
        setDefaultIsSquare(false);
    }
    
    /**
     * Routine nukes 1st entry, shifts everything left
     * @param series
     * @param Max 
     */
    @SuppressWarnings("unchecked")
    protected void ShiftSeries(XYChart.Series series,int Max)
    {
        if (series.getData().size() < Max)
        {
            return;
        }
        for (int iLoop =0; iLoop < series.getData().size()-1; iLoop++)
        {
            XYChart.Data point = (XYChart.Data)series.getData().get(iLoop+1);
            point.setXValue(iLoop);
        }
        series.getData().remove(0);
    }    
    
    abstract Chart CreateChartObject();
    protected Chart getChart()
    {
        if (null == _chart)
        {
            LOGGER.severe("Accessing chart object before created");
        }
        return _chart;
    }
    
   
    protected void CreateChart()
    {
        _xAxis = new NumberAxis(0d,xAxisMaxCount-1,xAxisMajorTick);
        _yAxis = new NumberAxis(yAxisMinValue,yAxisMaxValue,yAxisMajorTick);
        
        _xAxis.setTickLabelsVisible(xAxisTickVisible);
        _yAxis.setTickLabelsVisible(yAxisTickVisible);
        
        // Widget specifies interval, Java wants # of ticks, so convert
        int yTickCount = 0;
        if (yAxisMinorTick > 0)
        {
            yTickCount = (int) (yAxisMajorTick / yAxisMinorTick);
        }
        int xTickCount = 0;
        if (xAxisMinorTick > 0)
        {
            xTickCount = (int) (xAxisMajorTick / xAxisMinorTick);
        }
        
        _yAxis.minorTickCountProperty().set(yTickCount);
        _xAxis.minorTickCountProperty().set(xTickCount);
        
        
        _chart = CreateChartObject();
        
        _chart.setTitle(getTitle());
        
        _xAxis.setLabel(xAxisLabel);
        _yAxis.setLabel(yAxisLabel);
        _chart.setAnimated(_Animated);
    }
    
    public ArrayList<SeriesDataSet> getSeries()
    {
        return _Series;
    }

    public NumberAxis getxAxis()
    {
        return _xAxis;
    }

    public NumberAxis getyAxis()
    {
        return _yAxis;
    }

    public String getxAxisLabel()
    {
        return xAxisLabel;
    }

    public String getyAxisLabel()
    {
        return yAxisLabel;
    }

    public int getxAxisMaxCount()
    {
        return xAxisMaxCount;
    }

    public double getyAxisMaxValue()
    {
        return yAxisMaxValue;
    }

    public void setxAxis(NumberAxis _xAxis)
    {
        this._xAxis = _xAxis;
    }

    public void setyAxis(NumberAxis _yAxis)
    {
        this._yAxis = _yAxis;
    }

    public void setxAxisLabel(String xAxisLabel)
    {
        this.xAxisLabel = xAxisLabel;
    }

    public void setyAxisLabel(String yAxisLabel)
    {
        this.yAxisLabel = yAxisLabel;
    }

    public void setxAxisMaxCount(int xAxisMaxCount)
    {
        this.xAxisMaxCount = xAxisMaxCount;
    }

    public void setyAxisMaxValue(double yAxisMaxValue)
    {
        this.yAxisMaxValue = yAxisMaxValue;
    }

    public boolean isxAxisTickVisible()
    {
        return xAxisTickVisible;
    }

    public void setxAxisTickVisible(boolean xAxisTickVisible)
    {
        this.xAxisTickVisible = xAxisTickVisible;
    }

    public boolean isyAxisTickVisible()
    {
        return yAxisTickVisible;
    }

    public void setyAxisTickVisible(boolean yAxisTickVisible)
    {
        this.yAxisTickVisible = yAxisTickVisible;
    }
    
    public boolean HandleChartSpecificAppSettings(FrameworkNode node)
    {
        if (node.getNodeName().equalsIgnoreCase("xAxis"))
        {
            if (node.hasAttribute("Label"))
            {
                setxAxisLabel(node.getAttribute("Label"));
            }
            if (node.hasAttribute("MaxEntries"))
            {
                String strVal = node.getAttribute("MaxEntries");
                try
                {
                    setxAxisMaxCount(Integer.parseInt(strVal));
                }
                catch (NumberFormatException ex)
                {
                    LOGGER.severe("Invalid value for chart MaxEntires: " + strVal + " - ignoring.");
                    return false;
                }
            }
            // For a bar chart, isn't max entries, is count.  Is same thing, but gramatically Count is better
            if (node.hasAttribute("Count"))
            {
                String strVal = node.getAttribute("Count");
                try
                {
                    setxAxisMaxCount(Integer.parseInt(strVal));
                }
                catch (NumberFormatException ex)
                {
                    LOGGER.severe("Invalid value for chart Count: " + strVal + " - ignoring.");
                    return false;
                }
            }
            return true;
        }
        if (node.getNodeName().equalsIgnoreCase("yAxis"))
        {
            if (node.hasAttribute("Label"))
            {
                yAxisLabel = node.getAttribute("Label");
            }
            if (node.hasAttribute("MaxValue"))
            {
                String strVal = node.getAttribute("MaxValue");
                try
                {
                    yAxisMaxValue = Double.parseDouble(strVal);
                }
                catch (NumberFormatException ex)
                {
                    LOGGER.severe("Invalid value for chart MaxValue: " + strVal + " - ignoring.");
                    return false;
                }
            }
            if (node.hasAttribute("MinValue"))
            {
                String strVal = node.getAttribute("MinValue");
                try
                {
                    yAxisMinValue = Double.parseDouble(strVal);
                }
                catch (NumberFormatException ex)
                {
                    LOGGER.severe("Invalid value for chart MinValue: " + strVal + " - ignoring.");
                    return false;
                }
            }
            if (node.hasAttribute("Count"))
            {
                String strVal = node.getAttribute("Count");
                try
                {
                    setyAxisMaxCount(Integer.parseInt(strVal));
                }
                catch (NumberFormatException ex)
                {
                    LOGGER.severe("Invalid value for chart Count: " + strVal + " - ignoring.");
                    return false;
                }
            }
            
            return true;
        }    
        return false;
    }    

    public boolean isAnimated()
    {
        return _Animated;
    }

    public int getyAxisMaxCount()
    {
        return yAxisMaxCount;
    }

    public void setyAxisMaxCount(int yAxisMaxCount)
    {
        this.yAxisMaxCount = yAxisMaxCount;
    }

    public void setAnimated(boolean _Animated)
    {
        this._Animated = _Animated;
    }

    public double getyAxisMajorTick()
    {
        return yAxisMajorTick;
    }

    public void setyAxisMajorTick(double yAxisMajorTick)
    {
        this.yAxisMajorTick = yAxisMajorTick;
    }

    public int getyAxisMinorTick()
    {
        return yAxisMinorTick;
    }

    public void setyAxisMinorTick(int yAxisMinorTick)
    {
        this.yAxisMinorTick = yAxisMinorTick;
    }

    public double getxAxisMajorTick()
    {
        return xAxisMajorTick;
    }

    public void setxAxisMajorTick(double xAxisMajorTick)
    {
        this.xAxisMajorTick = xAxisMajorTick;
    }

    public int getxAxisMinorTick()
    {
        return xAxisMinorTick;
    }

    public void setxAxisMinorTick(int xAxisMinorTick)
    {
        this.xAxisMinorTick = xAxisMinorTick;
    }
    /**
     * Sets range for widget - not valid for all widgets
     * @param rangeNode
     * @return
     */

    @Override
    public boolean HandleValueRange(FrameworkNode rangeNode)
    {
        double Min = -1234.5678;
        double Max = -1234.5678;
        if (rangeNode.hasAttribute("Min"))
        {
            Min = rangeNode.getDoubleAttribute("Min", Min);
            if (Min == -1234.5678)
            {
                return false;
            }
            this.yAxisMinValue = Min;
        }
        if (rangeNode.hasAttribute("Max"))
        {
            Max = rangeNode.getDoubleAttribute("Max", Max);
            if (Max == -1234.5678)
            {
                return false;
            }
            this.yAxisMaxValue = Max;
        }
        return true;
    }
    @Override
    public void UpdateTitle(String strTitle)
    {
        _chart.setTitle(strTitle);
    }
    
}
