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
 * @author
 * http://www.java2s.com/Code/Java/Language-Basics/WindowHandlerdisplaylogmessageinawindowJFrame.htm
 */
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

class LogWindow extends JFrame
{

    private JTextArea textArea = new JTextArea();

    public LogWindow()
    {
        super("");
        setSize(300, 300);
        add(new JScrollPane(textArea));
        setVisible(true);
    }

    public void showInfo(String data)
    {
        textArea.append(data);
        this.validate();
    }
}

class WindowHandler extends Handler
{

    private LogWindow window = null;

    private Formatter formatter = null;

    private Level level = null;

    private static WindowHandler handler = null;

    private WindowHandler()
    {
        LogManager manager = LogManager.getLogManager();
        String className = this.getClass().getName();
        String level = manager.getProperty(className + ".level");
        setLevel(level != null ? Level.parse(level) : Level.INFO);
        if (window == null)
        {
            window = new LogWindow();
        }
    }

    public static synchronized WindowHandler getInstance()
    {
        if (handler == null)
        {
            handler = new WindowHandler();
        }
        return handler;
    }

    public synchronized void publish(LogRecord record)
    {
        String message = null;
        if (!isLoggable(record))
        {
            return;
        }
        message = getFormatter().format(record);
        window.showInfo(message);
    }

    public void close()
    {
    }

    public void flush()
    {
    }
}
