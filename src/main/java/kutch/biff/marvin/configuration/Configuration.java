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

package kutch.biff.marvin.configuration;

import java.util.logging.Logger;
import javafx.beans.property.DoubleProperty; 
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Side;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;
import kutch.biff.marvin.AboutBox;
import kutch.biff.marvin.logger.MyLogger;
 
/** 
 *
 * @author Patrick Kutch
 */
public class Configuration
{
    private final static Logger LOGGER = Logger.getLogger(MyLogger.class.getName());
    private static Configuration _Config=null;
    private boolean _DebugMode = false;
    private boolean _KioskMode = false;
    private String _Address=null;
    private int _port=0;
    private String _AppTitle = null;
    private String _CSSFile = null;
    private int _insetTop,_insetBottom,_insetLeft,_insetRight;
    
    private SimpleDoubleProperty _ScaleProperty;
    private SimpleDoubleProperty _CurrWidthProperty;
    private SimpleDoubleProperty _CurrHeightProperty;
    private double _topOffset,_bottomOffset;
    private boolean _AutoScale;
    private int _Width,_Height;
    private int _CreationWidth,_CreationHeight;
    public String TitleSuffix;
    private MenuBar _MenuBar;
    boolean _AllowTasks;
    boolean _ShowMenuBar;
    boolean fAboutCreated ;
    private int HeartbeatInterval;
    private TabPane _Pane;
    private long _GuiTimerUpdateInterval;
    private Stage _AppStage;
    private double _AppBorderWidth;
    private double _LastLiveDataReceived;
    private double _LastRecordedDataReceived;
    private String _ApplicationID;
    private Side   _Side;
    private boolean _IgnoreWebCerts;
    private int _MaxPacketSize;
    private boolean _EnableScrollBars;

    public Configuration()
    {
        //LOGGER.setLevel(Level);
        _insetTop = 0;
        _insetBottom = 0;
        _insetLeft= 0;
        _insetRight = 0;
        _AutoScale = false;
        _Width=0;
        _Height=0;
        _CreationWidth=0;
        _CreationHeight=0;
        _AllowTasks = true;
        _ShowMenuBar = false;
        TitleSuffix = "";
        fAboutCreated = false;
        HeartbeatInterval = 5; // 5 secs
        _AppBorderWidth = 8;
        _topOffset=0;
        _bottomOffset=0;
        _GuiTimerUpdateInterval = 350;
        _ScaleProperty = new SimpleDoubleProperty(1.0);
        _CurrWidthProperty =new SimpleDoubleProperty(); 
        _CurrHeightProperty =new SimpleDoubleProperty(); 
        Configuration._Config = this;
        _IgnoreWebCerts = false;
        _ApplicationID = "";
        _Side = Side.TOP;
        
        _LastLiveDataReceived = 0;
        _LastRecordedDataReceived = 0;
        _MaxPacketSize = 8*1024;
        _EnableScrollBars = false;
        
//        _CurrWidthProperty.addListener(new ChangeListener(){
//        @Override public void changed(ObservableValue o,Object oldVal, 
//                 Object newVal){
//             System.out.println("Width has changed! -- " + newVal.toString());
//        }
//      });
//        _CurrHeightProperty.addListener(new ChangeListener(){
//        @Override public void changed(ObservableValue o,Object oldVal, 
//                 Object newVal){
//             System.out.println("Height has changed! -- " + newVal.toString());
//        }
//      });
//        

    }

    public boolean getEnableScrollBars()
    {
        return _EnableScrollBars;
    }

    public void setEnableScrollBars(boolean _EnableScrollBars)
    {
        this._EnableScrollBars = _EnableScrollBars;
    }

    public void SetApplicationID(String newID)
    {
        _ApplicationID = newID;
    }
    
    public String GetApplicationID()
    {
        return _ApplicationID;
    }
    
    public void OnLiveDataReceived()
    {
        _LastLiveDataReceived = System.currentTimeMillis();
    }
    
    public void OnRecordedDataReceived()
    {
        _LastRecordedDataReceived = System.currentTimeMillis();
    }
    public void DetermineMemorex()
    {
        double timeCompare = 10000; // 10 seconds
        double currTime = System.currentTimeMillis();
        boolean Live = false;
        boolean Recorded = false;
        if (_LastLiveDataReceived + timeCompare > currTime )
        {
            Live = true;
        }
        if (_LastRecordedDataReceived + timeCompare > currTime)
        {
            Recorded = true;
        }
        if (Live && Recorded)
        {
            TitleSuffix = " {Live and Recorded}";
        }
        else if (Live)
        {
            TitleSuffix = " {Live}";
        }
        else if (Recorded)
        {
            TitleSuffix = " {Recorded}";
        }
        else
        {
            TitleSuffix = "";
        }
    }
    public SimpleDoubleProperty getCurrentWidthProperty()
    {
        return _CurrWidthProperty;
    }

    public SimpleDoubleProperty getCurrentHeightProperty()
    {
        return _CurrHeightProperty;
    }

    
    public Stage getAppStage()
    {
        return _AppStage;
    }

    public void setAppStage(Stage _AppStage)
    {
        this._AppStage = _AppStage;
    }

    public DoubleProperty getScaleProperty()
    {
        return _ScaleProperty;
    }
    public long getTimerInterval()
    {
        return _GuiTimerUpdateInterval;
    }
    public void setTimerInterval(long newVal)
    {
        _GuiTimerUpdateInterval = newVal;
    }
    
    public int getHeartbeatInterval()
    {
        return HeartbeatInterval;
    }

    public void setHeartbeatInterval(int HeartbeatInterval)
    {
        this.HeartbeatInterval = HeartbeatInterval;
    }

    public TabPane getPane()
    {
        return _Pane;
    }

    public void setPane(TabPane _Pane)
    {
        this._Pane = _Pane;
    }

    public int getWidth()
    {
        return _Width;
    }

    public void setWidth(int _Width)
    {
        this._Width = _Width;
    }

    public int getHeight()
    {
        return _Height;
    }

    public void setHeight(int _Height)
    {
        this._Height = _Height;
    }

    public boolean getAllowTasks()
    {
        return _AllowTasks;
    }

    public boolean getShowMenuBar()
    {
        return _ShowMenuBar;
    }

    public void setShowMenuBar(boolean _ShowMenuBar)
    {
        this._ShowMenuBar = _ShowMenuBar;
    }

    public void setAllowTasks(boolean _AllowTasks)
    {
        this._AllowTasks = _AllowTasks;
    }

    public double getScaleFactor()
    {
        return _ScaleProperty.getValue();
    }

    public void setScaleFactor(double _ScaleFactor)
    {
        LOGGER.config("Setting Application Scale Factor to: " + Double.toString(_ScaleFactor));

        _ScaleProperty.setValue(_ScaleFactor);
    }

    
    public int getInsetTop()
    {
        return _insetTop;
    }

    public void setInsetTop(int _insetTop)
    {
        if (_insetTop >=0)
        {
            LOGGER.config("Setting application insetTop to: " + Integer.toString(getInsetTop()));
            this._insetTop = _insetTop;
        }
    }

    public int getInsetBottom()
    {
        return _insetBottom;
    }

    public void setInsetBottom(int _insetBottom)
    {
        if (_insetBottom >=0)
        {
            LOGGER.config("Setting application insetBottom to: " + Integer.toString(getInsetBottom()));
            this._insetBottom = _insetBottom;
        }
    }

    public int getInsetLeft()
    {
        return _insetLeft;
    }

    public void setInsetLeft(int _insetLeft)
    {
        if (_insetLeft >=0)
        {
            LOGGER.config("Setting application insetLeft to: " + Integer.toString(getInsetLeft()));
            this._insetLeft = _insetLeft;
        }
    }

    public int getInsetRight()
    {
        return _insetRight;
    }

    public void setInsetRight(int _insetRight)
    {
        if (_insetRight >=0)
        {
            LOGGER.config("Setting application insetRight to: " + Integer.toString(getInsetRight()));
            this._insetRight = _insetRight;
        }
    }
    
    
    public static Configuration getConfig()
    {
        return _Config;
    }

    public String getAddress()
    {
        return _Address;
    }

    public void setAddress(String _Address)
    {
        this._Address = _Address;
    }

    public int getPort()
    {
        return _port;
    }

    public void setPort(int _port)
    {
        this._port = _port;
    }

    public String getAppTitle()
    {
        return _AppTitle;
    }

    public void setAppTitle(String _AppTitle)
    {
        this._AppTitle = _AppTitle;
    }

    public String getCSSFile()
    {
        return _CSSFile;
    }

    public void setCSSFile(String _CSSFie)
    {
        this._CSSFile = _CSSFie;
    }

    public boolean isDebugMode()
    {
        return _DebugMode;
    }

    public void setDebugMode(boolean DebugMode)
    {
        this._DebugMode = DebugMode;
    }

    public MenuBar getMenuBar()
    {
        if (null != _MenuBar && false ==  fAboutCreated)
        {
            AddAbout();
        }
        return _MenuBar;
    }

    public void setMenuBar(MenuBar _MenuBar)
    {
        this._MenuBar = _MenuBar;
    }

    public boolean getKioskMode()
    {
        return _KioskMode;
    }

    public void setKioskMode(boolean _KioskMode)
    {
        this._KioskMode = _KioskMode;
    }
    
    void AddAbout()
    {
        Menu objMenu = new Menu("About");
        MenuItem item = new MenuItem("About");
        fAboutCreated = true;
        
        item.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent t)
            {
                AboutBox.ShowAboutBox();
            }
        });
        objMenu.getItems().add(item);
        _MenuBar.getMenus().add(objMenu);
    }

    public boolean isAutoScale()
    {
        return _AutoScale;
    }

    public void setAutoScale(boolean _AutoScale)
    {
        this._AutoScale = _AutoScale;
    }

    public int getCreationWidth()
    {
        return _CreationWidth;
    }

    public void setCreationWidth(int _CreationWidth)
    {
        this._CreationWidth = _CreationWidth;
    }

    public int getCreationHeight()
    {
        return _CreationHeight;
    }

    public void setCreationHeight(int _CreationHeight)
    {
        this._CreationHeight = _CreationHeight;
    }

    public double getBottomOffset()
    {
        return _bottomOffset;
    }

    public void setBottomOffset(double _bottomOffset)
    {
        this._bottomOffset = _bottomOffset;
    }

    public double getTopOffset()
    {
        return _topOffset;
    }

    public void setTopOffset(double _topOffset)
    {
        this._topOffset = _topOffset;
    }

    public double getAppBorderWidth()
    {
        return _AppBorderWidth;
    }

    public void setAppBorderWidth(double _AppBorderWidth)
    {
        this._AppBorderWidth = _AppBorderWidth;
    }

    public Side getSide()
    {
        return _Side;
    }

    public void setSide(Side _Side)
    {
        this._Side = _Side;
    }

    public boolean getIgnoreWebCerts()
    {
        return _IgnoreWebCerts;
    }

    public void setIgnoreWebCerts(boolean _IgnoreWebCerts)
    {
        this._IgnoreWebCerts = _IgnoreWebCerts;
    }

    public int getMaxPacketSize()
    {
        return _MaxPacketSize;
    }

    public void setMaxPacketSize(int _MaxPacketSize)
    {
        this._MaxPacketSize = _MaxPacketSize;
    }
    
}
