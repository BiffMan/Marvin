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
package kutch.biff.marvin.widget.dynamicgrid;

import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import kutch.biff.marvin.utility.FrameworkNode;
import kutch.biff.marvin.widget.GridWidget;

/**
 *
 * @author Patrick Kutch
 */
public class DynamicGrid extends GridWidget
{

    private DynamicGridTransition _objTransition;

    public DynamicGrid()
    {
        super();
        _objTransition = new DynamicGridTransition(DynamicGridTransition.Transition.NONE);
    }

    public boolean ReadTransitionInformation(FrameworkNode baseNode)
    {
        DynamicGridTransition.Transition _TransitionType = DynamicGridTransition.Transition.NONE;
        int _Transition_xGridCount = 10;
        int _Transition_yGridCount = 10;
        int _Transition_Delay = 100;
        Color _Transition_Snapshot_Color_bg = Color.TRANSPARENT;
        Duration _Transition_Duration = Duration.millis(1500);

        for (FrameworkNode node : baseNode.getChildNodes())
        {
            if (node.getNodeName().equalsIgnoreCase("#Text") || node.getNodeName().equalsIgnoreCase("#Comment"))
            {
                continue;
            }

            if (node.getNodeName().equalsIgnoreCase("Transition"))
            {
                String strTransition = node.getTextContent();
                _TransitionType = DynamicGridTransition.VerifyTransitionType(strTransition);
                if (_TransitionType == DynamicGridTransition.Transition.INVALID)
                {
                    LOGGER.severe("Invalid DynamicGrid Grid Transition [" + strTransition + "] specified.  Valid values are: " + DynamicGridTransition.names());
                    return false;
                }

                if (node.hasAttribute("xGrids"))
                {
                    _Transition_xGridCount = node.getIntegerAttribute("xGrids", 0);
                    if (_Transition_xGridCount <= 0)
                    {
                        LOGGER.severe("Invlid xGrids value for Transition: " + node.getAttribute("xGrids"));
                        return false;
                    }
                }
                if (node.hasAttribute("yGrids"))
                {
                    _Transition_yGridCount = node.getIntegerAttribute("yGrids", 0);
                    if (_Transition_yGridCount <= 0)
                    {
                        LOGGER.severe("Invlid yGrids value for Transition: " + node.getAttribute("yGrids"));
                        return false;
                    }
                }
                if (node.hasAttribute("Duration"))
                {
                    int Transition_Duration = node.getIntegerAttribute("Duration", 0);
                    if (Transition_Duration <= 0)
                    {
                        LOGGER.severe("Invlid Duration value for Transition: " + node.getAttribute("Duration"));
                        return false;
                    }
                    _Transition_Duration = Duration.millis(Transition_Duration);
                }
                if (node.hasAttribute("Delay"))
                {
                    _Transition_Delay = node.getIntegerAttribute("Delay", 0);
                    if (_Transition_Delay <= 0)
                    {
                        LOGGER.severe("Invlid Delay value for Transition: " + node.getAttribute("Delay"));
                        return false;
                    }
                }
                // nuking this, s not really needed anymore
//                if (node.hasAttribute("Background"))
//                {
//                    String strColor = node.getAttribute("Background");
//                    try
//                    {
//                        _Transition_Snapshot_Color_bg = Color.web(strColor);
//                    }
//                    catch (Exception e)
//                    {
//                        LOGGER.severe("Invalid Background value for Transition: " + strColor);
//                        return false;
//                    }
//                }
            }
        }

        _objTransition = new DynamicGridTransition(_TransitionType);
        _objTransition.setDelay(_Transition_Delay);
        _objTransition.setDuration(_Transition_Duration);
        _objTransition.setNoOfTilesX(_Transition_xGridCount);
        _objTransition.setNoOfTilesY(_Transition_yGridCount);
        _objTransition.setSnapshotColor(_Transition_Snapshot_Color_bg);

        return true;
    }
    public Color getBackgroundColorForTransition()
    {
        return _objTransition.getSnapshotColor();
    }
    
    public DynamicGridTransition Transition(DynamicGrid objFrom, GridPane parent)
    {
        _objTransition.Transition(objFrom, this, parent);
        return _objTransition;
    }
}
