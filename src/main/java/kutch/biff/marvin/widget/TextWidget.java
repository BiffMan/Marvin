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

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import kutch.biff.marvin.datamanager.DataManager;
import kutch.biff.marvin.utility.FrameworkNode;
import static kutch.biff.marvin.widget.BaseWidget.LOGGER;

/**
 *
 * @author Patrick Kutch
 */
public class TextWidget extends BaseWidget
{

    private String _TextString;
    private Label _TextControl;
    private static Label testLabel = null;

    private boolean _ScaleToFitBounderies;

    public TextWidget()
    {
        _TextString = null;
        _TextControl = new Label();
        _TextControl.setAlignment(Pos.CENTER);
        _ScaleToFitBounderies = false;
        setDefaultIsSquare(false);
    }

    public static Label getTestLabel()
    {
        if (null == testLabel)
        {
            testLabel = new Label("");
        }
        return testLabel;
    }

    @Override
    public boolean Create(GridPane pane, DataManager dataMgr)
    {
        if (null != _TextString)
        {
            _TextControl.setText(_TextString);

        }
        ConfigureDimentions();
        _TextControl.setScaleShape(getScaleToFitBounderies());
        ConfigureAlignment();
        SetupPeekaboo(dataMgr);
        pane.add(_TextControl, getColumn(), getRow(), getColumnSpan(), getRowSpan());

//        if (null == testLabel)
//        {
//            testLabel = new Label("");
//            pane.add(testLabel, getColumn(), getRow(), getColumnSpan(), getRowSpan());
//        }
        dataMgr.AddListener(getMinionID(), getNamespace(), new ChangeListener()
        {
            @Override
            public void changed(ObservableValue o, Object oldVal, Object newVal)
            {
                if (IsPaused())
                {
                    return;
                }

                _TextString = newVal.toString();
                if (_TextString.length() < 2) // seems a single character won't display - bug in Java
                {
                    _TextString += " ";
                }
                _TextControl.setText(_TextString);
            }
        });

        SetupTaskAction();
        return ApplyCSS();
    }

    @Override
    public void SetInitialValue(String value)
    {
        _TextString = value;
    }

    @Override
    public void ConfigureAlignment()
    {
        super.ConfigureAlignment();
        _TextControl.setAlignment(getPosition());
    }

    @Override
    public javafx.scene.Node getStylableObject()
    {
        return _TextControl;
    }

    @Override
    public ObservableList<String> getStylesheets()
    {
        return _TextControl.getStylesheets();
    }

    public boolean getScaleToFitBounderies()
    {
        return _ScaleToFitBounderies;
    }

    public void setScaleToFitBounderies(boolean _ScaleToFitBounderies)
    {
        this._ScaleToFitBounderies = _ScaleToFitBounderies;
    }

    @Override
    public void HandleCustomStyleOverride(FrameworkNode styleNode)
    {
        if (styleNode.hasAttribute("ScaleToShape"))
        {
            String str = styleNode.getAttribute("ScaleToShape");
            if (0 == str.compareToIgnoreCase("True"))
            {
                setScaleToFitBounderies(true);
            }
            else if (0 == str.compareToIgnoreCase("False"))
            {
                setScaleToFitBounderies(false);
            }
            else
            {
                LOGGER.severe("Invalid StyleOVerride Elment ScaleToShape for Text .  ScaleToShape should be True or False, not:" + str);
            }
        }
    }
    @Override
    public void UpdateTitle(String strTitle)
    {
        LOGGER.warning("Tried to update Title of a TextWidget to " + strTitle);
    }
}
