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
package kutch.biff.marvin.logger;

/**
 *
 * @author Lars Vogal - borrowed with pride!
 * http://www.vogella.com/tutorials/Logging/article.html
 */
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MyLogger
{
    static private FileHandler _fileHTML;
    static private Formatter _formatterHTML;

    static public void setup(String fileName) throws IOException
    {

        try
        {
        // Get the global logger to configure it
            Logger logger =  Logger.getLogger(MyLogger.class.getName());

            logger.setLevel(Level.SEVERE);

            _fileHTML = new FileHandler(fileName);

            // create HTML Formatter
            _formatterHTML = new MyHtmlFormatter();
            _fileHTML.setFormatter(_formatterHTML);

            logger.addHandler(_fileHTML);
        }
        catch (Exception ex)
        {
            // Likely tried 2 run two instances with same log file (so in same directory)
        }
    }
}
