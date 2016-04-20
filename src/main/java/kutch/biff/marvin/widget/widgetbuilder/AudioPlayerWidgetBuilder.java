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
import kutch.biff.marvin.widget.AudioPlayerWidget;

/**
 *
 * @author Patrick Kutch
 */
public class AudioPlayerWidgetBuilder
{
    private final static Logger LOGGER = Logger.getLogger(MyLogger.class.getName());
    public static AudioPlayerWidget Build(FrameworkNode masterNode, String widgetDefFilename )
    {
        AudioPlayerWidget objWidget =  new AudioPlayerWidget();
        for (FrameworkNode node :masterNode.getChildNodes())
        {
            if (MediaPlayerWidgetBuilder.ParseDefinitionFile(objWidget, node))
            {
                
            }
            else
            {
                LOGGER.severe("Unknown Tag <" +node.GetNode()+"> in Video Player Widget definition file");
                return null;
            }
        }
        return objWidget;     
    }
}
