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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import kutch.biff.marvin.configuration.Configuration;
import kutch.biff.marvin.datamanager.DataManager;
import kutch.biff.marvin.logger.MyLogger;
import kutch.biff.marvin.task.TaskManager;
import kutch.biff.marvin.utility.FrameworkNode;
import kutch.biff.marvin.utility.Utility;
import kutch.biff.marvin.widget.widgetbuilder.WidgetBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author Patrick Kutch
 */
abstract public class BaseWidget implements Widget
{

    public static String DefaultWidgetDirectory = "Widget";
    private static int _WidgetCount = 0;
    private static final ArrayList<BaseWidget> _WidgetList = new ArrayList<>();
    protected final static Logger LOGGER = Logger.getLogger(MyLogger.class.getName());
    protected static Configuration CONFIG = Configuration.getConfig();
    protected TaskManager TASKMAN = TaskManager.getTaskManager();
    private final int _WidgetNumber;
    private double _Height;
    private double _Width;
    private int _Row, _RowSpan;
    private int _Column, _ColumnSpan;
    private int _DecimalPlaces;
    private String _FileCSS;
    private String _MinionID;
    private String _Namespace;
    private String _Title;
    private String _UnitsOverride;
    private String _StyleID;
    private List<String> _StyleOverride;
    private HPos _HorizontalPosition;
    private VPos _VerticalPosition;
    protected Pos _Position;
    private String _PeekabooID;
    private String _PeekabooNamespace;
    private String _PeekabooHideStr;
    private String _PeekabooShowStr;
    private Boolean _PeekabooShowDefault;
    private String _TaskID;
    protected boolean _InitiallyEnabled;
    private String _DefinintionFileDirectory;
    private boolean _Paused;
    private boolean _DefaultIsSquare;
    private boolean _MouseHasBeenSetup;
    protected String _strAlignment;

    public BaseWidget()
    {
        BaseWidget.CONFIG = Configuration.getConfig();
        BaseWidget._WidgetCount++;
        _WidgetNumber = BaseWidget.getWidgetCount();
        _Height = 0;
        _Width = 0;
        _Row = 0;
        _RowSpan = 1;
        _Column = 0;
        _ColumnSpan = 1;
        _DecimalPlaces = 0;
        _FileCSS = null;
        _MinionID = null;
        _Namespace = null;
        _UnitsOverride = null;
        _TaskID = null;
        _Title = "";
        _StyleID = null;
        _StyleOverride = new ArrayList<>();
        _HorizontalPosition = HPos.CENTER;
        _VerticalPosition = VPos.CENTER;
        _PeekabooShowDefault = true;
        _PeekabooID = null;
        _PeekabooNamespace = null;
        _DefinintionFileDirectory = DefaultWidgetDirectory;
        _PeekabooHideStr = "Hide";
        _PeekabooShowStr = "Show";
        _InitiallyEnabled = true;
        _Position = Pos.CENTER;
        _Paused = false;
        _DefaultIsSquare = true;
        _WidgetList.add(this);
        _MouseHasBeenSetup = false;
        _strAlignment = "Center";
    }

    public static int getWidgetCount()
    {
        return _WidgetCount;
    }

    @Override
    public boolean SupportsEnableDisable()
    {
        return false;
    }

    public List<String> getStyleOverride()
    {
        return _StyleOverride;
    }

    public void AddAdditionalStyleOverride(String newOverride)
    {
        _StyleOverride.add(newOverride);
    }

    public void setInitiallyEnabled(boolean enabled)
    {
        _InitiallyEnabled = enabled;
    }

    public void setStyleOverride(List<String> _StyleOverride)
    {
        this._StyleOverride = _StyleOverride;
    }

    public void SetEnabled(boolean enabled)
    {

    }

    public String getStyleID()
    {
        return _StyleID;
    }

    public void setStyleID(String _StyleID)
    {
        this._StyleID = _StyleID;
    }

    public String getUnitsOverride()
    {
        return _UnitsOverride;
    }

    public void setUnitsOverride(String _UnitsOverride)
    {
        this._UnitsOverride = _UnitsOverride;
    }

    @Override
    public String toString()
    {
        return toString(true);
    }

    public String toString(boolean SingleLine)
    {
        String strCR = "\n";
        if (true == SingleLine)
        {
            strCR = " ";
        }
        StringBuilder retStr = new StringBuilder();

        retStr.append(getName());
        if (null != getMinionID())
        {
            retStr.append(strCR);
            retStr.append("MinionSrc ID=");
            retStr.append(getMinionID());
            if (null != getNamespace())
            {
                retStr.append(" Namespace=");
                retStr.append(getNamespace());
            }
        }
        if (null != getTaskID())
        {
            retStr.append(strCR);
            retStr.append("Task ID: ");
            retStr.append(getTaskID());
        }

        retStr.append(strCR);
        retStr.append("Config Size : ");
        retStr.append("[");
        if (CONFIG.getScaleFactor() != 1.0)
        {
            retStr.append("(");
            retStr.append(Integer.toString((int) _Width));
            retStr.append("x");
            retStr.append(Integer.toString((int) _Height));
            retStr.append(")-> ");
        }

        retStr.append(Integer.toString((int) (getWidth() * CONFIG.getScaleFactor())));
        retStr.append("x");
        retStr.append(Integer.toString((int) (getHeight() * CONFIG.getScaleFactor())));
        retStr.append("]");
        retStr.append(" ");

        Region objRegion = getRegionObject();
        if (null != objRegion)
        {
            retStr.append(strCR);
            retStr.append("Actual Size : ");
            retStr.append("[");
            retStr.append(Integer.toString((int) objRegion.getWidth()));
            retStr.append("x");
            retStr.append(Integer.toString((int) objRegion.getHeight()));
            retStr.append("]");
        }
        return retStr.toString();
    }

    public int getDecimalPlaces()
    {
        return _DecimalPlaces;
    }

    public void setDecimalPlaces(int _DecimalPlaces)
    {
        this._DecimalPlaces = _DecimalPlaces;
    }

    public String getTitle()
    {
        return _Title;
    }

    public void setTitle(String title)
    {
        _Title = title;
    }

    public String getMinionID()
    {
        return _MinionID;
    }

    public void setMinionID(String _ID)
    {
        this._MinionID = _ID;
    }

    public String getNamespace()
    {
        return _Namespace;
    }

    public void setNamespace(String _Namespace)
    {
        this._Namespace = _Namespace;
    }

    public double getHeight()
    {
        return _Height;
    }

    public void setHeight(double _Height)
    {

        this._Height = _Height;
    }

    public double getWidth()
    {
        return _Width;
    }

    public void setWidth(double _Width)
    {
        this._Width = _Width;
    }

    public int getRow()
    {
        return _Row;
    }

    public int getRowSpan()
    {
        return _RowSpan;
    }

    public void setRow(int _Row)
    {
        this._Row = _Row;
    }

    public void setRowSpan(int _RowSpan)
    {
        if (_RowSpan < 1)
        {
            LOGGER.severe("rowSpan set to invalid value of " + Integer.toString(_Row) + ". Ignoring.");
            return;
        }
        this._RowSpan = _RowSpan;
    }

    public int getColumn()
    {
        return _Column;
    }

    public int getColumnSpan()
    {
        return _ColumnSpan;
    }

    public void setColumn(int _Column)
    {
        this._Column = _Column;
    }

    public void setColumnSpan(int _Column)
    {
        if (_Column < 1)
        {
            LOGGER.severe("colSpan set to invalid value of " + Integer.toString(_Column) + ". Ignoring.");
            return;
        }
        this._ColumnSpan = _Column;
    }

    public String getBaseCSSFilename()
    {
        return _FileCSS;
    }

    public void setBaseCSSFilename(String _FileCSS)
    {
        this._FileCSS = _FileCSS;
    }

    public String getPeekabooID()
    {
        return _PeekabooID;
    }

    public void setPeekabooID(String _PeekabooID)
    {
        this._PeekabooID = _PeekabooID;
    }

    public String getPeekabooNamespace()
    {
        return _PeekabooNamespace;
    }

    public void setPeekabooNamespace(String _PeekabooNamespace)
    {
        this._PeekabooNamespace = _PeekabooNamespace;
    }

    public String getPeekabooHideStr()
    {
        return _PeekabooHideStr;
    }

    public void setPeekabooHideStr(String _PeekabooHideStr)
    {
        this._PeekabooHideStr = _PeekabooHideStr;
    }

    public String getPeekabooShowStr()
    {
        return _PeekabooShowStr;
    }

    public void setPeekabooShowStr(String _PeekabooShowStr)
    {
        this._PeekabooShowStr = _PeekabooShowStr;
    }

    public Boolean isPeekabooShowDefault()
    {
        return _PeekabooShowDefault;
    }

    public void setPeekabooShowDefault(Boolean _PeekabooShowDefault)
    {
        this._PeekabooShowDefault = _PeekabooShowDefault;
    }

    public boolean IsPaused()
    {
        return _Paused;

    }

    public void SetInitialValue(String value)
    {
        LOGGER.warning("Tried to set Initial Value of [" + value + "] for Widget that does not support it");
    }

    public static String convertToFileURL(String filename)
    {
        String path = filename;

        if (File.separatorChar == '/')
        {
        }
        else
        {
            path = path.replace(File.separatorChar, '/');
        }
        if (!path.startsWith("/"))
        {
            path = "/" + path;
        }
        String retVal = "file:." + path;

        return retVal;
    }

    private void HandleRemoteTitleUpdate(FrameworkNode node)
    {
        String strTitle = node.getTextContent();
        if (strTitle.length() > 0)
        {
            LOGGER.info("Updating Widget Tilte via Peekaboo RemoteTitleUpdate to: " + strTitle);
            UpdateTitle(strTitle);
        }
        else
        {
            LOGGER.warning("Received Peekaboo Marvin request for new Title, but no String Title Given");
        }
    }

    private void HandleRemoteStyleOverride(FrameworkNode node)
    {
        if (WidgetBuilder.HandleStyleOverride(this, node))
        {
            ApplyCSS();
        }
    }
    public final static String ESCAPE_CHARS = ":&";
    public final static List<String> ESCAPE_STRINGS = Collections.unmodifiableList(Arrays.asList(new String[]
    {
        "&#58;", "&amp;"
    }));

    private static String UNICODE_LOW = "" + ((char) 0x20); //space
    private static String UNICODE_HIGH = "" + ((char) 0x7f);

    //should only use for the content of an attribute or tag      
    public static String toEscaped(String content)
    {
        String result = content;

        if ((content != null) && (content.length() > 0))
        {
            boolean modified = false;
            StringBuilder stringBuilder = new StringBuilder(content.length());
            for (int i = 0, count = content.length(); i < count; ++i)
            {
                String character = content.substring(i, i + 1);
                int pos = ESCAPE_CHARS.indexOf(character);
                if (pos > -1)
                {
                    stringBuilder.append(ESCAPE_STRINGS.get(pos));
                    modified = true;
                }
                else
                {
                    if ((character.compareTo(UNICODE_LOW) > -1)
                            && (character.compareTo(UNICODE_HIGH) < 1))
                    {
                        stringBuilder.append(character);
                    }
                    else
                    {
                        stringBuilder.append("&#" + ((int) character.charAt(0)) + ";");
                        modified = true;
                    }
                }
            }
            if (modified)
            {
                result = stringBuilder.toString();
            }
        }

        return result;
    }

    private void HandleMarvinPeekaboo(String strRequest)
    {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db;
        Document doc;
        FrameworkNode baseNode;

        // Can't pass XML within XML, so for this, change < for [ and ] for >
        String strMassaged = strRequest.substring("Marvin:".length()).replace('[', '<').replace(']', '>');
        //strMassaged = toEscaped(strMassaged);
        try
        {
            try
            {
                db = dbf.newDocumentBuilder();
            }
            catch (ParserConfigurationException ex)
            {
                LOGGER.severe(ex.toString());
                return;
            }

            doc = db.parse(new ByteArrayInputStream(strMassaged.getBytes()));
            NodeList appStuff = doc.getChildNodes();
            baseNode = new FrameworkNode(appStuff.item(0));
        }
        catch (SAXException | IOException ex)
        {
            LOGGER.severe(ex.toString());
            LOGGER.severe("Invalid Peekaboo Marvin data received: " + strRequest);
            LOGGER.severe(strMassaged);
            return;
        }

        if (baseNode.getNodeName().equalsIgnoreCase("StyleOverride"))
        {
            HandleRemoteStyleOverride(baseNode);
        }
        else if (baseNode.getNodeName().equalsIgnoreCase("Title"))
        {
            HandleRemoteTitleUpdate(baseNode);
        }
        else
        {
            LOGGER.warning("Received unknown Peekaboo Marvin data: " + strRequest);
        }
    }

    protected void SetupPeekaboo(DataManager dataMgr)
    {
        if (null == getPeekabooID() || null == getPeekabooNamespace())
        {
            return;
        }
        getStylableObject().setVisible(_PeekabooShowDefault);
        dataMgr.AddListener(getPeekabooID(), getPeekabooNamespace(), new ChangeListener()
        {
            @Override
            public void changed(ObservableValue o, Object oldVal, Object newVal)
            {
                String strPeek = newVal.toString();
                if (0 == strPeek.compareToIgnoreCase(getPeekabooHideStr())
                        || (strPeek.equalsIgnoreCase("Hide")))
                {
                    getStylableObject().setVisible(false);
                }
                else if (0 == strPeek.compareToIgnoreCase(getPeekabooShowStr())
                        || (strPeek.equalsIgnoreCase("Show")))
                {
                    getStylableObject().setVisible(true);
                }
                // Some widgets (Buttons) can be enable and disable too, so let's override this.
                else if (true == SupportsEnableDisable() && strPeek.equalsIgnoreCase("Enable") || strPeek.equalsIgnoreCase("Disable"))
                {
                    if (strPeek.equalsIgnoreCase("Enable"))
                    {
                        SetEnabled(true);
                    }
                    else
                    {
                        SetEnabled(false);
                    }
                }
                else if (strPeek.equalsIgnoreCase("Pause"))
                {
                    _Paused = true;
                }
                else if (strPeek.equalsIgnoreCase("Resume"))
                {
                    _Paused = false;
                }
                else if (strPeek.substring(0, "Marvin:".length()).equalsIgnoreCase("Marvin:"))
                {
                    HandleMarvinPeekaboo(strPeek);
                }
                else
                {
                    LOGGER.severe("Received invalid Peekaboo option, must be either 'Show' or 'Hide', not " + strPeek);
                }
            }
        });
    }

    public static String convertToFileOSSpecific(String filename)
    {
        String path = filename;

        if (File.separatorChar == '/') // linux
        {
            path = path.replace('\\', File.separatorChar);
        }
        else // windows box
        {
            path = path.replace('/', File.separatorChar);
        }
        return path;
    }

    protected String GetCSS_File()
    {
        String strFile = getBaseCSSFilename();
        if (null != getBaseCSSFilename())
        {
            File file = new File(strFile); // first look for fully qualified path

            if (false == file.exists())
            { // if didn't find, look in same directory that widget was defined in
                strFile = getDefinintionFileDirectory() + File.separatorChar + getBaseCSSFilename();
                file = new File(strFile);

                if (false == file.exists())
                {
                    LOGGER.severe("Unable to locate Stylesheet: " + getBaseCSSFilename());
                    return null;
                }
            }

            return convertToFileURL(strFile);
        }
        return null;
    }

    public static boolean HandleCommonDefinitionFileConfig(BaseWidget widget, FrameworkNode node)
    {
        if (node.getNodeName().equalsIgnoreCase("#Text") | node.getNodeName().equalsIgnoreCase("#comment"))
        {
            return true;
        }

        if (node.getNodeName().equalsIgnoreCase("Widget")) // root
        {
            return true;
        }

        if (node.getNodeName().equalsIgnoreCase("Style"))
        {
            String str = node.getTextContent();
            widget.setBaseCSSFilename(str);

            Utility.ValidateAttributes(new String[]
            {
                "ID"
            }, node);
            if (node.hasAttribute("ID"))// get the style ID if ther is one in the wiget defintion file
            {
                //LOGGER.config("Wiget has CSS ID of [" + node.getAttribute("ID") + "] defined in widget definition file");
                widget.setStyleID(node.getAttribute("ID"));
            }
            return true;
        }
        else if (node.getNodeName().equalsIgnoreCase("UnitsOverride"))
        {
            String str = node.getTextContent();
            widget.setUnitsOverride(str);
            return true;
        }
        return false;
    }

    protected boolean ApplyCSS()
    {
        if (null != GetCSS_File())
        {
            //getStylesheets().clear();

            boolean fRet = true;
            LOGGER.config("Applying Stylesheet: " + GetCSS_File() + " to Widget.");
            fRet = getStylesheets().add(GetCSS_File());
            if (false == fRet)
            {
                LOGGER.severe("Failed to apply Stylesheet " + GetCSS_File());
                return false;
            }
        }
        if (null != getStyleID())
        {
            getStylableObject().setId(getStyleID());
        }

        return ApplyStyleOverrides(getStylableObject(), getStyleOverride());
    }

    // nukes old styles string and replaces with these.  Used for dynamic data widgets
    public boolean ApplyOverrides()
    {
        if (null == getStylableObject())
        {
            return true; // audio widget has no 
        }
        String StyleString = "";
        for (String Style : getStyleOverride())
        {
            StyleString += Style + ";";
        }
        getStylableObject().setStyle(StyleString);

        return true;
    }

    public String getTaskID()
    {
        return _TaskID;
    }

    public void setTaskID(String _TaskID)
    {
        this._TaskID = _TaskID;
    }

    /**
     * *
     * If no user configured size, get defaults from widget
     *
     * @param objRegion
     */
    protected void PreConfigDimensions(Region objRegion)
    {
        if (true == getDefaultIsSquare())
        {
            if (getWidth() > 0 && getHeight() <= 0)
            {
                setHeight(_Width);
            }
            else if (getWidth() <= 0 && getHeight() > 0)
            {
                setWidth(_Height);
            }
        }
    }

    protected void ConfigureDimentions()
    {
        Region regionNode = getRegionObject();
        if (null == regionNode)
        {
            LOGGER.severe(getName() + " : Should NOT BE here, NULL Widgetd pass to Config Dimensions");
            return;
        }
        PreConfigDimensions(regionNode);
        if (getWidth() > 0)
        {
            regionNode.setPrefWidth(getWidth());
            regionNode.setMinWidth(getWidth());
            regionNode.setMaxWidth(getWidth());
        }
        if (getHeight() > 0)
        {
            regionNode.setPrefHeight(getHeight());
            regionNode.setMinHeight(getHeight());
            regionNode.setMaxHeight(getHeight());
        }
    }

    public boolean HandleWidgetSpecificSettings(FrameworkNode widgetNode)
    {
        return false;
    }

    public void HandleWidgetSpecificAttributes(FrameworkNode widgetNode)
    {

    }

    public String[] GetCustomAttributes()
    {
        return null;
    }

    static protected boolean ApplyStyleOverrides(javafx.scene.Node widget, List<String> Styles)
    {
        //return true;
        if (null == widget)
        {
            return true; // audio widget has no 
        }
        String StyleString = widget.getStyle();
        for (String Style : Styles)
        {
            StyleString += Style + ";";
            LOGGER.config("Adding Custom Style String to Widget: " + Style);
        }
        if (StyleString.length() > 1)
        {
            LOGGER.config("StyleString before: " + widget.getStyle() + " After: " + StyleString);
        }
        widget.setStyle(StyleString);

        return true;
    }

    public HPos getHorizontalPosition()
    {
        return _HorizontalPosition;
    }

    protected void setHorizontalPosition(HPos _HorizontalPosition)
    {
        this._HorizontalPosition = _HorizontalPosition;
    }

    public VPos getVerticalPosition()
    {
        return _VerticalPosition;
    }

    protected void setVerticalPosition(VPos _VerticalPosition)
    {
        this._VerticalPosition = _VerticalPosition;
    }

    public String getAlignment()
    {
        return _strAlignment;
    }

    public void setAlignment(String alignString)
    {
        _strAlignment = alignString;
        if (0 == alignString.compareToIgnoreCase("Center"))
        {
            setHorizontalPosition(HPos.CENTER);
            setVerticalPosition(VPos.CENTER);
            _Position = Pos.CENTER;
        }
        else if (0 == alignString.compareToIgnoreCase("N"))
        {
            setHorizontalPosition(HPos.CENTER);
            setVerticalPosition(VPos.TOP);
            _Position = Pos.TOP_CENTER;
        }
        else if (0 == alignString.compareToIgnoreCase("NE"))
        {
            setHorizontalPosition(HPos.RIGHT);
            setVerticalPosition(VPos.TOP);
            _Position = Pos.TOP_RIGHT;
        }
        else if (0 == alignString.compareToIgnoreCase("E"))
        {
            setHorizontalPosition(HPos.RIGHT);
            setVerticalPosition(VPos.CENTER);
            _Position = Pos.CENTER_RIGHT;
        }
        else if (0 == alignString.compareToIgnoreCase("SE"))
        {
            setHorizontalPosition(HPos.RIGHT);
            setVerticalPosition(VPos.BOTTOM);
            _Position = Pos.BOTTOM_RIGHT;
        }
        else if (0 == alignString.compareToIgnoreCase("S"))
        {
            setHorizontalPosition(HPos.CENTER);
            setVerticalPosition(VPos.BOTTOM);
            _Position = Pos.BOTTOM_CENTER;
        }
        else if (0 == alignString.compareToIgnoreCase("SW"))
        {
            setHorizontalPosition(HPos.LEFT);
            setVerticalPosition(VPos.BOTTOM);
            _Position = Pos.BOTTOM_LEFT;
        }
        else if (0 == alignString.compareToIgnoreCase("W"))
        {
            setHorizontalPosition(HPos.LEFT);
            setVerticalPosition(VPos.CENTER);
            _Position = Pos.CENTER_LEFT;
        }
        else if (0 == alignString.compareToIgnoreCase("NW"))
        {
            setHorizontalPosition(HPos.LEFT);
            setVerticalPosition(VPos.TOP);
            _Position = Pos.TOP_LEFT;
        }
        else
        {
            LOGGER.severe("Invalid Alignment indicated in config file: " + alignString + ". Ignoring.");
        }
    }

    public String getDefinintionFileDirectory()
    {
        return _DefinintionFileDirectory;
    }

    public void setDefinintionFileDirectory(String DefinintionFileDirectory)
    {
        if (null != DefinintionFileDirectory)
        {
            _DefinintionFileDirectory = DefinintionFileDirectory;
        }
    }

    public void ConfigureAlignment()
    {
        Node objStylable = getStylableObject();
        if (objStylable != null)
        {
            GridPane.setValignment(getStylableObject(), getVerticalPosition());
            GridPane.setHalignment(getStylableObject(), getHorizontalPosition());
        }
    }

    public EventHandler<MouseEvent> SetupTaskAction()
    {
        if (false == _MouseHasBeenSetup) // quick hack, as I call this from MOST widgets, but now want it from all.  Will eventually remove from individual widgets.
        {
            BaseWidget objWidget = this;
            if (null != getTaskID() || CONFIG.isDebugMode()) // only do if a task to setup, or if debug mode
            {
                EventHandler<MouseEvent> eh = new EventHandler<MouseEvent>()
                {
                    @Override
                    public void handle(MouseEvent event)
                    {
                        if (event.isShiftDown() && CONFIG.isDebugMode())
                        {
                            LOGGER.info(objWidget.toString(true));
                        }
                        else if (null != getTaskID() && true == CONFIG.getAllowTasks())
                        {
                            TASKMAN.PerformTask(getTaskID());
                        }
                    }
                };
                getStylableObject().setOnMouseClicked(eh);
                _MouseHasBeenSetup = true;
                return eh;
            }
        }
        return null;
    }

    @Override
    public void HandleCustomStyleOverride(FrameworkNode styleNode)
    {

    }

    @Override
    public Region getRegionObject()
    {
        try
        {
            return (Region) getStylableObject();
        }
        catch (Exception ex)
        {
            return null;
        }
    }

    public boolean getDefaultIsSquare()
    {
        return _DefaultIsSquare;
    }

    protected void setDefaultIsSquare(boolean _DefaultIsSquare)
    {
        this._DefaultIsSquare = _DefaultIsSquare;
    }

    public Pos getPosition()
    {
        return _Position;
    }

    public void setPosition(Pos newPosition)
    {
        _Position = newPosition;
    }

    public static ArrayList<BaseWidget> getWidgetList()
    {
        return _WidgetList;
    }

    public int getWidgetNumber()
    {
        return _WidgetNumber;
    }

    public String getName()
    {
        String strList[] = this.getClass().toString().split("\\."); // Need the \\ as delimeter for period
        String retStr = "Something baaaad happened";
        if (strList.length > 1)
        {
            retStr = strList[strList.length - 1] + " [#" + Integer.toString(getWidgetNumber()) + "]";
        }
        return retStr;
    }

    /**
     * Sets range for widget - not valid for all widgets
     *
     * @param rangeNode
     * @return
     */
    public boolean HandleValueRange(FrameworkNode rangeNode)
    {
        LOGGER.severe(getName() + " does not use the <Value Range> tag");
        return false;
    }
}
