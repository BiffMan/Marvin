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
package kutch.biff.marvin.widget.widgetbuilder;

import java.util.logging.Logger;
import kutch.biff.marvin.logger.MyLogger;
import kutch.biff.marvin.utility.FrameworkNode;
import kutch.biff.marvin.widget.BaseWidget;
import kutch.biff.marvin.widget.WebWidget;

/**
 *
 * @author Patrick Kutch
 */
public class WebWidgetBuilder
{
    private final static Logger LOGGER = Logger.getLogger(MyLogger.class.getName());

    public static WebWidget Build(FrameworkNode masterNode, String widgetDefFilename)
    {
        WebWidget _widget = new WebWidget();
        
        for (FrameworkNode node :masterNode.getChildNodes())
        {
            if (BaseWidget.HandleCommonDefinitionFileConfig(_widget,node))
            {
            }
            else if (node.getNodeName().equalsIgnoreCase("#comment"))
            {
            }
            else if (node.getNodeName().equalsIgnoreCase("ReverseContent"))
            {
                String str = node.getTextContent();
                if (0 == str.compareToIgnoreCase("True"))
                {
                    _widget.SetReverseContent(true);
                }
                else if (0 == str.compareToIgnoreCase("False"))
                {
                    _widget.SetReverseContent(false);
                }
                else
                {
                    LOGGER.severe("Invalid Web Widget Definition File.  ReverseContent should be True or False, not:" + str);
                    return null;                
                }
            }
        }
        return _widget;
    }
}
