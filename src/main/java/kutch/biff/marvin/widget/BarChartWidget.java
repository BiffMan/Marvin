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
import java.util.HashMap;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.Chart;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.GridPane;
import kutch.biff.marvin.datamanager.DataManager;
import kutch.biff.marvin.utility.FrameworkNode;
import kutch.biff.marvin.utility.SeriesDataSet;
import kutch.biff.marvin.utility.SeriesSet;
import kutch.biff.marvin.utility.Utility;

/**
 *
 * @author Patrick Kutch
 */
public class BarChartWidget extends LineChartWidget
{
    @SuppressWarnings("unchecked")
    protected HashMap<String, SeriesSet> _SeriesMap;
    protected ArrayList<String> _SeriesOrder;
    private final CategoryAxis _xAxis;
    private final boolean _HorizontalChart;

    public BarChartWidget(boolean Horizontal)
    {
        _SeriesMap = new HashMap<>();
        _SeriesOrder = new ArrayList<>();
        _xAxis = new CategoryAxis();
        _HorizontalChart = Horizontal;
    }

    @Override
    public boolean Create(GridPane pane, DataManager dataMgr)
    {
        CreateBarChart();
        ConfigureDimentions();
        ConfigureAlignment();
        SetupPeekaboo(dataMgr);

        pane.add(getChart(), getColumn(), getRow(), getColumnSpan(), getRowSpan());
        setupListeners(dataMgr);

        SetupTaskAction();
        return ApplyCSS();
    }

    private void CreateBarChart()
    {
        CreateChart();
        _xAxis.setLabel(getxAxisLabel());
        
        _xAxis.setAnimated(false);  // for some reason for this chart, it defaults to true!
    }

    @SuppressWarnings("unchecked")
    protected void setupListeners(DataManager dataMgr)
    {
        if (0 == _SeriesOrder.size())
        {
            setupListenersForSingleSource(dataMgr);
            return;
        }
        for (String key : _SeriesOrder)
        {
            SeriesSet objSeriesSet = _SeriesMap.get(key);
            XYChart.Series objSeries = new XYChart.Series();
            objSeries.setName(objSeriesSet.getTitle());
            for (SeriesDataSet objDs : objSeriesSet.getSeriesList())
            {
                XYChart.Data objData;
                if (isHorizontal())
                {
                    objData = new XYChart.Data<>(0, objDs.getTitle());
                }
                else
                {
                    objData = new XYChart.Data<>(objDs.getTitle(), 0);
                }

                objSeries.getData().add(objData);

                dataMgr.AddListener(objDs.getID(), objDs.getNamespace(), new ChangeListener()
                {
                    @Override
                    public void changed(ObservableValue o, Object oldVal, Object newVal)
                    {
                        if (IsPaused())
                        {
                            return;
                        }
                        String strVal = newVal.toString();
                        double newValue;
                        try
                        {
                            newValue = Double.parseDouble(strVal);
                        }
                        catch (NumberFormatException ex)
                        {
                            LOGGER.severe("Invalid data for Line Chart received: " + strVal);
                            return;
                        }
                        if (isHorizontal())
                        {
                            objData.XValueProperty().set(newValue);
                        }
                        else
                        {
                            objData.YValueProperty().set(newValue);
                        }
                    }
                });

            }
            ((BarChart) getChart()).getData().add(objSeries);

        }
    }

    @SuppressWarnings("unchecked")
    private void setupListenersForSingleSource(DataManager dataMgr)
    {
        XYChart.Series<String, Number> objSeries = new XYChart.Series<>();
        for (int iLoop = 0; iLoop < getxAxisMaxCount(); iLoop++)
        {
            XYChart.Data objData = new XYChart.Data<>(Integer.toString(iLoop), 0);
            objSeries.getData().add(objData);
        }
        ((BarChart) getChart()).getData().add(objSeries);
        dataMgr.AddListener(getMinionID(), getNamespace(), new ChangeListener()
        {
            @Override
            public void changed(ObservableValue o, Object oldVal, Object newVal)
            {
                if (IsPaused())
                {
                    return;
                }

                String[] strList = newVal.toString().split(",");
                if (strList.length != getxAxisMaxCount())
                {
                    LOGGER.severe("Received " + Integer.toString(strList.length) + " items for a Bar Chart , however Widget only defined for " + Integer.toString(getxAxisMaxCount()));
                    return;
                }
                int index = 0;
                for (String strValue : strList)
                {
                    double newValue;
                    try
                    {
                        newValue = Double.parseDouble(strValue);
                    }
                    catch (NumberFormatException ex)
                    {
                        LOGGER.severe("Invalid data for Bar Chart received: " + strValue);
                        return;
                    }

                    XYChart.Data objData = objSeries.getData().get(index++);
                    objData.setYValue(newValue);
                }
            }
        });

    }

    @Override
    public javafx.scene.Node getStylableObject()
    {
        return ((BarChart) (getChart()));
    }

    @Override
    public ObservableList<String> getStylesheets()
    {
        return ((BarChart) (getChart())).getStylesheets();
    }

    @Override
    protected Chart CreateChartObject()
    {
        if (isHorizontal())
        {
            this.getyAxis().setTickLabelRotation(90);
            return new BarChart<Number, String>(getyAxis(), _xAxis);
        }

        return new BarChart<String, Number>(_xAxis, getyAxis());
    }

    @Override
    public boolean HandleWidgetSpecificSettings(FrameworkNode node)
    {
        if (true == HandleChartSpecificAppSettings(node))
        {
            return true;
        }

        if (node.getNodeName().equalsIgnoreCase("Series"))
        {
            String Label;
            String ID;
            if (node.hasAttribute("Label"))
            {
                Label = node.getAttribute("Label");
            }
            else
            {
                Label = "";
            }
            if (node.hasAttribute("ID"))
            {
                ID = node.getAttribute("ID");
            }
            else
            {
                LOGGER.severe("Series declaration for BarChart Widget requires an ID");
                return false;
            }
            if (_SeriesMap.containsKey(ID.toUpperCase()))
            {
                LOGGER.severe("Seried ID must be unique per Bar Chart, repeat found: " + ID);
                return false;
            }
            _SeriesMap.put(ID.toUpperCase(), new SeriesSet(Label));
            _SeriesOrder.add(ID.toUpperCase());

            return true;
        }
        else if (node.getNodeName().equalsIgnoreCase("SeriesSet"))
        {
            return ReadSeriesSet(node);
        }

        return false;
    }

    protected boolean ReadSeriesSet(FrameworkNode setNode)
    {
        String title = "";
        if (setNode.hasAttributes())
        {
            Utility.ValidateAttributes(new String[]
            {
                "Title"
            }, setNode);
            if (setNode.hasAttribute("Title"))
            {
                title = setNode.getAttribute("Title");
            }
        }
        for (FrameworkNode node : setNode.getChildNodes())
        {
            if (node.getNodeName().equalsIgnoreCase("MinionSrc"))
            {
                Utility.ValidateAttributes(new String[]
                {
                    "ID", "Namespace", "SeriesID"
                }, node);

                String ID, Namespace, SeriesID;
                if (node.hasAttribute("ID"))
                {
                    ID = node.getAttribute("ID");
                }
                else
                {
                    LOGGER.severe("BarChart Series defined with invalid MinionSrc - no ID");
                    return false;
                }
                if (node.hasAttribute("Namespace"))
                {
                    Namespace = node.getAttribute("Namespace");
                }
                else
                {
                    LOGGER.severe("BarChartSeries defined with invalid MinionSrc - no Namespace");
                    return false;
                }
                if (node.hasAttribute("SeriesID"))
                {
                    SeriesID = node.getAttribute("SeriesID");
                }
                else
                {
                    LOGGER.severe("BarChartSeries defined with invalid MinionSrc - no SeriesID");
                    return false;
                }
                if (false == _SeriesMap.containsKey(SeriesID.toUpperCase()))
                {
                    LOGGER.severe("BarChartSeries defined with invalid MinionSrc - the Series ID has not been defined in a <Series> section. SeriesID=" + SeriesID);
                    return false;
                }
                SeriesDataSet objDS = new SeriesDataSet(title, ID, Namespace);
                _SeriesMap.get(SeriesID.toUpperCase()).AddSeries(objDS);
            }
        }

        return true;
    }

    public boolean isHorizontal()
    {
        return _HorizontalChart;
    }

    protected CategoryAxis getAxis_X()
    {
        return this._xAxis;
    }
}
