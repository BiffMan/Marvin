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
package kutch.biff.marvin.task;

import java.util.logging.Logger;
import kutch.biff.marvin.logger.MyLogger;

/**
 *
 * @author Patrick Kutch
 */
public class Parameter
{
    private String _value;
    private final static Logger LOGGER = Logger.getLogger(MyLogger.class.getName());
    
    public Parameter()
    {
       
    }
    public Parameter(String val)
    {
        _value = val;
    }
    
    @Override
    public String toString()
    {
        if (_value.charAt(0)=='@' && null != PromptManager.getPromptManager().getPrompt(_value.substring(1)))
        {
            BasePrompt objPrompt = PromptManager.getPromptManager().getPrompt(_value.substring(1));
            if (objPrompt.PerformPrompt())
            {
                return objPrompt.GetPromptedValue();
            }
            LOGGER.info("Prompted value was cancelled");
            return null;
        }
        return _value;
    }
    
}
