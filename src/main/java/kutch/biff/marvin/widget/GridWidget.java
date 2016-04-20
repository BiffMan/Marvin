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
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import kutch.biff.marvin.datamanager.DataManager;
import kutch.biff.marvin.utility.FrameworkNode;
import kutch.biff.marvin.utility.Utility;

/**
 *
 * @author Patrick Kutch
 */
public class GridWidget extends BaseWidget
{
    protected ArrayList<Widget> _Widgets;
    private GridPane _GridPane = null;
    private int _hGap, _vGap;
    private int _insetTop, _insetBottom, _insetLeft, _insetRight;
    //private Pos _Position; // this could be a problem

    public GridWidget()
    {
        _Widgets = new ArrayList<>();
        _GridPane = new GridPane();

        _hGap = -1;
        _vGap = -1;
        _insetTop = CONFIG.getInsetTop();
        _insetBottom = CONFIG.getInsetLeft();
        _insetLeft = CONFIG.getInsetLeft();
        _insetRight = CONFIG.getInsetRight();

        _Position = Pos.TOP_CENTER; // default for both Tab and Grids
        setDefaultIsSquare(false);
    }
    
    public Image getImage(Color fillColor)
    {
        SnapshotParameters params = new SnapshotParameters();
        params.setFill(fillColor);
        //params.setFill(Color.TRANSPARENT);
        Image image = getGridPane().snapshot(params, null);
        /*
         BufferedImage bufImageARGB = SwingFXUtils.fromFXImage(image, null);
         BufferedImage bufImageRGB = new BufferedImage(bufImageARGB.getWidth(), bufImageARGB.getHeight(), BufferedImage.OPAQUE);

        Graphics2D graphics = bufImageRGB.createGraphics();
        graphics.drawImage(bufImageARGB, 0, 0, null);
        
           WritableImage wr = null;
        if (bufImageARGB != null) {
            wr = new WritableImage(bufImageARGB.getWidth(), bufImageARGB.getHeight());
            PixelWriter pw = wr.getPixelWriter();
            for (int x = 0; x < bufImageARGB.getWidth(); x++) {
                for (int y = 0; y < bufImageARGB.getHeight(); y++) {
                    pw.setArgb(x, y, bufImageARGB.getRGB(x, y));
                }
            }
        }
        return wr;
        */
        return image;
    }
    

    
    protected GridPane getGridPane()
    {
        return _GridPane;
    }
    
    public GridPane getBasePane()
    {
        return _GridPane;
        
    }

    @Override
    public boolean Create(GridPane parentPane, DataManager dataMgr)
    {
        if (CONFIG.isDebugMode())
        {
            getGridPane().gridLinesVisibleProperty().set(true);
        }
        ConfigureDimentions();
        boolean RetVal = true;
        //ApplyCSS();
        
        for (Widget _Widget : _Widgets)
        {
            if (false == _Widget.Create(getGridPane(), dataMgr))
            {
                RetVal = false;
            }
            
        }
        if (false == RetVal)
        {
            return false;
        }

        if (_hGap > -1)
        {
            getGridPane().setHgap(_hGap);
        }
        if (_vGap > -1)
        {
            getGridPane().setVgap(_vGap);
        }

        getGridPane().setAlignment(getPosition());

        getGridPane().setPadding(new Insets(getInsetTop(), getInsetRight(), getInsetBottom(), getInsetLeft()));
        if (parentPane != getGridPane())
        {
            parentPane.add(_GridPane, getColumn(), getRow(), getColumnSpan(), getRowSpan()); // is a cycle since this is the parent of tab
        }
        SetupPeekaboo(dataMgr);
        SetupTaskAction();
        return ApplyCSS();
    }

    @Override
    public javafx.scene.Node getStylableObject()
    {
        return _GridPane;
    }

    @Override
    public ObservableList<String> getStylesheets()
    {
        return _GridPane.getStylesheets();
    }

    public void AddWidget(Widget objWidget)
    {
        _Widgets.add(objWidget);
    }

    public int gethGap()
    {
        return _hGap;
    }

    public void sethGap(int _hGap)
    {
        this._hGap = _hGap;
    }

    public int getvGap()
    {
        return _vGap;
    }

    public void setvGap(int _vGap)
    {
        this._vGap = _vGap;
    }

    public int getInsetTop()
    {
        return _insetTop;
    }

    public void setInsetTop(int _insetTop)
    {
        if (_insetTop >= 0)
        {
            LOGGER.config("Overriding grid insetTop to: " + Integer.toString(getInsetTop()));
            this._insetTop = _insetTop;
        }
    }

    public int getInsetBottom()
    {
        return _insetBottom;
    }

    public void setInsetBottom(int _insetBottom)
    {
        if (_insetBottom >= 0)
        {
            LOGGER.config("Overriding grid insetBottom to: " + Integer.toString(getInsetBottom()));
            this._insetBottom = _insetBottom;
        }
    }

    public int getInsetLeft()
    {
        return _insetLeft;
    }

    public void setInsetLeft(int _insetLeft)
    {
        if (_insetLeft >= 0)
        {
            LOGGER.config("Overriding grid insetLeft to: " + Integer.toString(getInsetLeft()));
            this._insetLeft = _insetLeft;
        }
    }

    public int getInsetRight()
    {
        return _insetRight;
    }

    public void setInsetRight(int _insetRight)
    {
        if (_insetRight >= 0)
        {
            LOGGER.config("Overriding grid insetRight to: " + Integer.toString(getInsetRight()));
            this._insetRight = _insetRight;
        }
    }

    @Override
    public boolean HandleWidgetSpecificSettings(FrameworkNode widgetNode)
    {
        if (widgetNode.getNodeName().equalsIgnoreCase("PaddingOverride"))
        {
            Utility.ValidateAttributes(new String[]
            {
                "top", "bottom", "left", "right"
            }, widgetNode);
            String strTop = "-1";
            String strBottom = "-1";
            String strLeft = "-1";
            String strRight = "-1";
            if (widgetNode.hasAttribute("top"))
            {
                strTop = widgetNode.getAttribute("top");
            }
            if (widgetNode.hasAttribute("bottom"))
            {
                strBottom = widgetNode.getAttribute("bottom");
            }
            if (widgetNode.hasAttribute("left"))
            {
                strLeft = widgetNode.getAttribute("left");
            }
            if (widgetNode.hasAttribute("right"))
            {
                strRight = widgetNode.getAttribute("right");
            }
            try
            {
                setInsetTop(Integer.parseInt(strTop));
                setInsetBottom(Integer.parseInt(strBottom));
                setInsetLeft(Integer.parseInt(strLeft));
                setInsetRight(Integer.parseInt(strRight));
                return true;
            }
            catch (Exception ex)
            {
                LOGGER.severe("Invalid Grid or Tab <PaddingOverride> configuration.");
                return false;
            }
        }

        return false;
    }

    @Override
    public Pos getPosition()
    {
        return _Position;
    }

    @Override
    public void setPosition(Pos _Position)
    {
        this._Position = _Position;
    }

    /**
     * The Grids do alignment different than widgets (kewl eh?) So have an
     * override fn to deal with it.
     *
     * @param alignString - going to be Center,NE,SW,N,S,E,W,SE,NW
     */
    @Override
    public void setAlignment(String alignString)
    {
        super.setAlignment(alignString);
        if (0 == alignString.compareToIgnoreCase("Center"))
        {
            setPosition(Pos.CENTER);
        }
        else if (0 == alignString.compareToIgnoreCase("N"))
        {
            setPosition(Pos.TOP_CENTER);
        }
        else if (0 == alignString.compareToIgnoreCase("NE"))
        {
            setPosition(Pos.TOP_RIGHT);
        }
        else if (0 == alignString.compareToIgnoreCase("E"))
        {
            setPosition(Pos.CENTER_RIGHT);
        }
        else if (0 == alignString.compareToIgnoreCase("SE"))
        {
            setPosition(Pos.BOTTOM_RIGHT);
        }
        else if (0 == alignString.compareToIgnoreCase("S"))
        {
            setPosition(Pos.BOTTOM_CENTER);
        }
        else if (0 == alignString.compareToIgnoreCase("SW"))
        {
            setPosition(Pos.BOTTOM_LEFT);
        }
        else if (0 == alignString.compareToIgnoreCase("W"))
        {
            setPosition(Pos.CENTER_LEFT);
        }
        else if (0 == alignString.compareToIgnoreCase("NW"))
        {
            setPosition(Pos.TOP_LEFT);
        }
        else
        {
            LOGGER.severe("Invalid Grid or Tab Alignment indicated in config file: " + alignString + ". Ignoring.");
        }
    }
    @Override
    public void UpdateTitle(String strTitle)
    {
        LOGGER.warning("Tried to update Title of a Grid to " + strTitle);
    }

}
