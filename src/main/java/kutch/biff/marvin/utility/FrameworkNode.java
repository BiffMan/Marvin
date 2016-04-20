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
package kutch.biff.marvin.utility;

import java.util.ArrayList;
import java.util.logging.Logger;
import kutch.biff.marvin.logger.MyLogger;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This is a wrapper class for the XML Node object.  It allows case non-sensitve attributes
 * and allows for alias's to be in the XML files
 * @author Patrick Kutch
 */
public class FrameworkNode 
{
    private final static Logger LOGGER = Logger.getLogger(MyLogger.class.getName());
    private static final AliasMgr aMGR = AliasMgr.getAliasMgr();
    private Node _node;
    private NamedNodeMap _attributes;
    public FrameworkNode(Node baseNode)
    {
        _node = baseNode;
        _attributes = baseNode.getAttributes();
    }
    
    public boolean hasAttributes()
    {
        return _node.hasAttributes();
    }
    public boolean hasAttribute(String elemStr)
    {
        return null != getAttribute(elemStr);
    }
    
    public boolean getBooleanAttribute(String elemStr)
    {
        if (hasAttribute(elemStr))
        {
            String str = getAttribute(elemStr);
            if (str.equalsIgnoreCase("True"))
            {
                return true;
            }
            if (str.equalsIgnoreCase("False"))
            {
                return false;
            }
            
            LOGGER.severe("Invalide boolean attribute for ["+elemStr+"] :" + str + ". Defaulting to false");
        }
        else
        {
            LOGGER.severe("Asked to read boolean attribute ["+elemStr+"] that does not exist. Defaulting to false");
        }
        return false;
    }
    
    public boolean getBooleanValue()
    {
        boolean retVal = false;
        String strBool = getTextContent();
        if ("True".equalsIgnoreCase(strBool))
        {
            retVal = true;
        }
        else if ("False".equalsIgnoreCase(strBool))
        {
        }
        else
        {
            LOGGER.severe("Asked to read boolean value for ["+getNodeName()+"] that does not exist. Defaulting to false");
        }
        
        return retVal;
    }
    public int getIntegerAttribute(String elemStr,int defaultValue)
    {
        if (hasAttribute(elemStr))
        {
            String str = getAttribute(elemStr);
            try
            {
                return Integer.parseInt(str);
            }
            catch (NumberFormatException ex)
            {
                LOGGER.severe("Invalid attribute : " + str);
            }
        }
        return defaultValue;
    }

    /**
     *
     * @param elemStr
     * @param defaultValue
     * @return
     */
    public double getDoubleAttribute(String elemStr,double defaultValue)
    {
        if (hasAttribute(elemStr))
        {
            String str = getAttribute(elemStr);
            try
            {
                return Double.parseDouble(str);
            }
            catch (NumberFormatException ex)
            {
                LOGGER.severe("Invalid attribute rate : " + str);
            }
        }
        return defaultValue;
    }
    
    public String getAttribute(String elemStr)
    {
        if (false == _node.hasAttributes())
        {
            return null;
        }
        if (null != _attributes.getNamedItem(elemStr))
        {
            return  HandleAlias(_attributes.getNamedItem(elemStr).getTextContent());
        }
        // now let's do a case non-sensitive check
        for (int iLoop = 0; iLoop < _attributes.getLength(); iLoop++)
        {
            if (elemStr.equalsIgnoreCase(_attributes.item(iLoop).getNodeName()))
            {
                return HandleAlias(_attributes.item(iLoop).getTextContent());
            }
        }
        return null;
    }
    
    public ArrayList<FrameworkNode> getChildNodes()
    {
        NodeList children = _node.getChildNodes();
        ArrayList<FrameworkNode> list = new ArrayList<>();
        for (int iLoop = 0; iLoop < children.getLength();iLoop++)
        {
            list.add(new FrameworkNode(children.item(iLoop)));
        }
        return list;
    }
    
    public FrameworkNode getChild(String nodeName)
    {
        NodeList children = _node.getChildNodes();
        
        for (int iLoop = 0; iLoop < children.getLength();iLoop++)
        {
            if (nodeName.equalsIgnoreCase(children.item(iLoop).getNodeName()))
            {
                return new FrameworkNode(children.item(iLoop));
            }
        }
        return null;
    }
    
    public boolean hasChild(String nodeName)
    {
        return null != getChild(nodeName);
    }
    
    public short getNodeType()
    {
        return _node.getNodeType();
    }
    
    public Node GetNode()
    {
        return _node;
    }
    
    public String getNodeName()
    {
        return _node.getNodeName();
    }
    public String getTextContent()
    {
        return HandleAlias(_node.getTextContent());
    }
    
    /***
     * Routine to see if there is an Alias embedded within the XML node string
     * Supports an alias within an alias.  Is a reentrant routine
     * @param strData the raw string
     * @return string with alias replacement
     */
    private String HandleAlias(String strData)
    {
        String retString="";
        if (false == strData.contains("$("))
        {
            return strData;
        }
        int OutterIndex = strData.indexOf("$(");
        int CloseParenIndex = strData.indexOf(")",OutterIndex);
        int NextStart = strData.indexOf("$(", OutterIndex+1);
        
        if (NextStart >= 0 && CloseParenIndex > 0)
        {
            if (strData.indexOf("$(", OutterIndex+1) < CloseParenIndex) // have an embedded Alias
            {
                retString = strData.substring(0, OutterIndex+2);
                String T = strData.substring(OutterIndex+2);
                retString += HandleAlias(T);
            }
            else 
            {
                String Alias = strData.substring(OutterIndex+2, CloseParenIndex);
                retString = strData.substring(0, OutterIndex);
                retString += AliasMgr.getAliasMgr().GetAlias(Alias);
                retString += strData.substring(CloseParenIndex+1);
            }
        }        
        else if (CloseParenIndex >0)
        {
                String Alias = strData.substring(OutterIndex+2, CloseParenIndex);
                retString = strData.substring(0, OutterIndex);
                retString += AliasMgr.getAliasMgr().GetAlias(Alias);
                retString += strData.substring(CloseParenIndex+1);
        }
        
        return HandleAlias(retString);
    }    
}
