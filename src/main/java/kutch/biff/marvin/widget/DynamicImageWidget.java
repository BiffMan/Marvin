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

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import kutch.biff.marvin.datamanager.DataManager;
import kutch.biff.marvin.task.MarvinTask;
import kutch.biff.marvin.utility.CircularList;
import kutch.biff.marvin.utility.FrameworkNode;
import kutch.biff.marvin.utility.Utility;
import static kutch.biff.marvin.widget.BaseWidget.convertToFileOSSpecific;

/**
 * Displays an image, but it can be changed. Is a list of images each with a
 * 'nam' associated with it. DataPoint comes in with a name and is displayed.
 *
 * @author Patrick Kutch
 */
public class DynamicImageWidget extends StaticImageWidget
{

    private HashMap<String, String> _ImageFilenames;
    private ArrayList<String> _ImageFileNames;
    private CircularList<String> _ListID;
    private String _CurrentKey;
    private boolean _AutoAdvance;
    private boolean _AutoLoopWithAdvance;
    private int _AutoAdvanceInterval;
    private static int _AutoAdvanceImageNumber=0;

    public DynamicImageWidget()
    {
        _ImageFilenames = new HashMap<>();
        _ImageFileNames = new ArrayList<>();
        _CurrentKey = null;
        _ListID = new CircularList<>();
        setDefaultIsSquare(false);
        _AutoAdvance = false;
        _AutoLoopWithAdvance = false;
        _AutoAdvanceInterval = 0;
    }

    /**
     *
     * @param pane
     * @param dataMgr
     * @return
     */
    @Override
    public boolean Create(GridPane pane, DataManager dataMgr)
    {
        if (setupImages())
        {
            ConfigureAlignment();
            pane.add(_ImageView, getColumn(), getRow(), getColumnSpan(), getRowSpan());
            SetupPeekaboo(dataMgr);
            
            if (_AutoAdvance)
            {
                if (null == getMinionID() || null == getNamespace())
                {
                    String ID = Integer.toBinaryString(DynamicImageWidget._AutoAdvanceImageNumber);
                    DynamicImageWidget._AutoAdvanceImageNumber ++;
                    
                    if (null == getMinionID())
                    {
                        setMinionID(ID);
                    }
                    if (null == getNamespace())
                    {
                        setNamespace(ID);
                    }
                }
                MarvinTask mt = new MarvinTask();
                mt.AddDataset(getMinionID(), getNamespace(), "Next");
                TASKMAN.AddPostponedTask(mt, _AutoAdvanceInterval);
            }            
            

            dataMgr.AddListener(getMinionID(), getNamespace(), new ChangeListener()
            {
                @Override
                public void changed(ObservableValue o, Object oldVal, Object newVal)
                {
                    if (IsPaused())
                    {
                        return;
                    }

                    String strVal = newVal.toString().replaceAll("(\\r|\\n)", "");

                    String key;

                    if (strVal.equalsIgnoreCase("Next")) // go to next image in the list
                    {
                        key = _ListID.GetNext();

                    }
                    else if (strVal.equalsIgnoreCase("Previous")) // go to previous image in the list
                    {
                        key = _ListID.GetPrevious();
                    }
                    else
                    {
                        key = strVal; // expecting an ID
                        _ListID.get(key); // just to keep next/prev alignment
                    }
                    key = key.toLowerCase();
                    if (_ImageFilenames.containsKey(key))
                    {
                        if (!key.equalsIgnoreCase(_CurrentKey)) // no reason to re-load if it is already loaded
                        {
                            _CurrentKey = key;
                            Image Img = new Image(_ImageFilenames.get(key));
                            _ImageView.setImage(Img);
                        }
                    }
                    else
                    {
                        LOGGER.warning("Received unknown ID for dynamic Image: [" + getNamespace() + ":" + getMinionID() + "] : " + strVal);
                        return;
                    }
                    if (_AutoAdvance)
                    {
                        if (!_AutoLoopWithAdvance && _ListID.IsLast(key))
                        {
                            _AutoAdvance = false;
                            return;
                        }
                        MarvinTask mt = new MarvinTask();
                        mt.AddDataset(getMinionID(), getNamespace(), "Next");
                        TASKMAN.AddPostponedTask(mt, _AutoAdvanceInterval);
                    }
                }
            });
            SetupTaskAction();
            return ApplyCSS();
        }
        return false;
    }

    private boolean setupImages()
    {
        _ImageView = new ImageView();
        _ImageView.setPreserveRatio(getPreserveRatio());
        _ImageView.setSmooth(true);
        _ImageView.setPickOnBounds(!GetClickThroughTransparentRegion());

        if (_CurrentKey == null)
        {
            LOGGER.severe("No Initial Image setup for Dynamic Image Widget.");
            return false;
        }
        else
        {
            if (_ImageFilenames.containsKey(_CurrentKey))
            {
                Image image = new Image(_ImageFilenames.get(_CurrentKey));
                _ImageView.setImage(image);
                _ListID.get(_CurrentKey); // just to keep next/prev alignment
//                if (0 == getWidth())
//                {
//                    setWidth(image.getWidth());
//                }
//                if (0 == getHeight())
//                {
//                    setHeight(image.getHeight());
//                }
            }
            else
            {
                LOGGER.severe("Initial key not valid for dynamic image widget: " + _CurrentKey);
                return false;
            }
        }
        ConfigureDimentions();
        return true;
    }

    @Override
    public boolean HandleWidgetSpecificSettings(FrameworkNode node)
    {
        String Id = null;
        String FileName = null;

        if (node.getNodeName().equalsIgnoreCase("Initial"))
        {
            Utility.ValidateAttributes(new String[]
            {
                "ID"
            }, node);
            if (node.hasAttribute("ID"))
            {
                _CurrentKey = node.getAttribute("ID").toLowerCase();
                return true;
            }
            else
            {
                LOGGER.severe("Dynamic Image Widget incorrectly defined Initial Image, no ID.");
                return false;
            }
        }
        if (node.getNodeName().equalsIgnoreCase("AutoAdvance"))
        {
            /*        
             <AutoAdvance Frequency='1000' Loop='False'/>
             */
            if (node.hasAttribute("Frequency"))
            {
                _AutoAdvanceInterval = node.getIntegerAttribute("Frequency", -1);
                if (_AutoAdvanceInterval < 100)
                {
                    LOGGER.severe("Frequency specified for DynamicImage is invalid: " + node.getAttribute("Frequency"));
                    return false;
                }

                if (node.hasAttribute("Loop"))
                {
                    _AutoLoopWithAdvance = node.getBooleanAttribute("Loop");
                }
                _AutoAdvance = true;
                return true;
            }
            return false;
        }

        if (node.getNodeName().equalsIgnoreCase("Image"))
        {
            Utility.ValidateAttributes(new String[]
            {
                "Source", "ID"
            }, node);
            if (node.hasAttribute("Source"))
            {
                FileName = node.getAttribute("Source");
            }
            else
            {
                LOGGER.severe("Dynamic Image Widget has no Source for Image");
                return false;
            }
            if (node.hasAttribute("ID"))
            {
                Id = node.getAttribute("ID");

                if (true == _ImageFilenames.containsKey(Id.toLowerCase()))
                {
                    LOGGER.severe("Dynamic Image Widget has repeated Image ID: " + Id);
                    return false;
                }
                Id = Id.toLowerCase();
            }
            else
            {
                LOGGER.severe("Dynamic Image Widget has no ID for Image");
                return false;
            }
            String fname = convertToFileOSSpecific(FileName);
            File file = new File(fname);
            if (file.exists())
            {
                String fn = "file:" + fname;
                //Image img = new Image(fn);
                _ImageFilenames.put(Id, fn);
                _ListID.add(Id);

            }
            else
            {
                LOGGER.severe("Dynamic Image Widget - missing Image file: " + FileName);
                return false;
            }
        }

        return true;
    }
}
