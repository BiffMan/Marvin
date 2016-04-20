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

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import static kutch.biff.marvin.configuration.ConfigurationReader.OpenXMLFile;
import kutch.biff.marvin.logger.MyLogger;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;

/**
 * This class handles aliases.
 *
 * @author Patrick Kutch
 */
public class AliasMgr
{

    private final ArrayList<Map> _AliasList;
    private final static Logger LOGGER = Logger.getLogger(MyLogger.class.getName());
    private final static AliasMgr _Mgr = new AliasMgr();
    private final static String strCurrentRowAlias = "CurrentRowAlias";
    private final static String strNextRowAlias = "NextRowAlias";
    private final static String strCurrentColumnAlias = "CurrentColumnAlias";
    private final static String strNextColumnAlias = "NextColumnAlias";

    public static AliasMgr getAliasMgr()
    {
        return _Mgr;
    }

    private AliasMgr()
    {
        _AliasList = new ArrayList<>();
        PushAliasList(true);
        AddEnvironmentVars();
    }

    /**
     * Fetches the string associated with the alias if exists, else null
     *
     * @param strAlias
     * @return
     */
    public String GetAlias(String strAlias)
    {
        strAlias = strAlias.toUpperCase();
        for (Map map : _AliasList)
        {
            if (map.containsKey(strAlias))
            {
                String strRetVal = (String) map.get(strAlias);
                if (strAlias.equalsIgnoreCase(strNextRowAlias))
                {
                    int currVal = Integer.parseInt(GetAlias(strCurrentRowAlias));
                    strRetVal =  Integer.toString(currVal+1);
                    //System.out.println("NextRow: " + strRetVal);
                    //UpdateCurrentRow(currVal);
                }
                else if (strAlias.equalsIgnoreCase(strNextColumnAlias))
                {
                    int currVal = Integer.parseInt(GetAlias(strCurrentColumnAlias));
                    strRetVal =  Integer.toString(currVal+1);
                    //System.out.println("NextRow: " + strRetVal);
                    
//                    UpdateColumn(currVal);
                }
                return strRetVal;
            }
        }
        return null;
    }

    /**
     * Just checks to see if the given string is an Alias
     *
     * @param strAlias
     * @return
     */
    public boolean IsAliased(String strAlias)
    {
        strAlias = strAlias.toUpperCase();
        for (Map map : _AliasList)
        {
            if (map.containsKey(strAlias))
            {
                return true;
            }
        }
        return false;
    }

    /**
     *
     * @param Alias
     * @param Value
     */
    @SuppressWarnings({"unchecked", "unchecked"})
    public void AddAlias(String Alias, String Value)
    {
        Map map = _AliasList.get(0);
        if (map.containsKey(Alias.toUpperCase()))
        {
            LOGGER.config("Duplicate Alias detected for : " + Alias + ". Ignoring.");
            return;
        }
        map.put(Alias.toUpperCase(), Value);
    }
    
    public void UpdateCurrentColumn(int newValue)
    {
        UpdateAlias(strNextColumnAlias, Integer.toString(newValue + 1));
        UpdateAlias(strCurrentColumnAlias, Integer.toString(newValue));
    }

    public void UpdateCurrentRow(int newValue)
    {
        UpdateAlias(strNextRowAlias, Integer.toString(newValue + 1));
        UpdateAlias(strCurrentRowAlias, Integer.toString(newValue));
    }

    @SuppressWarnings("unchecked")
    private boolean UpdateAlias(String Alias, String newValue)
    {
        String strCheck = Alias.toUpperCase();

        for (Map map : _AliasList)
        {
            if (map.containsKey(strCheck))
            {
                map.replace(strCheck, newValue);
                return true;
            }
        }
        LOGGER.severe("Asked to updated alias: " + Alias + ". However it did not exist.");
        return false;
    }

    /**
     * Implemented as a kind of stack for scope reasons meaning that if an alias
     * is used in a file, it is valid for all nested files, but not outside of
     * that scope
     */
    public final void PushAliasList(boolean addRowColAliases)
    {
        _AliasList.add(0, new HashMap<>()); // put in position 0 
        if (addRowColAliases)
        {
            AddAlias(strCurrentRowAlias, "0");
            AddAlias(strNextRowAlias, "1");
            AddAlias(strCurrentColumnAlias, "0");
            AddAlias(strNextColumnAlias, "1");
        }
    }

    /**
     *
     */
    public void PopAliasList()
    {
        _AliasList.remove(0);
    }

    /**
     * Simple debug routine to dump top alias list
     */
    public void DumpTop()
    {
        Map map = _AliasList.get(0);
        String AliasStr = "Global Alias List:\n";

        if (map.isEmpty())
        {
            return;
        }
        //System.out.println("---- Alias List ----");
        for (Object objKey : map.keySet())
        {
            String key = (String) objKey;
            AliasStr += key + "-->" + map.get(objKey) + "\n";
        }
        LOGGER.info(AliasStr);
    }

    /**
     * Looks for <AliasList> aliases and processes them
     *
     * @param aliasNode <AliasList> node
     * @return true if successful, else false
     */
    public static boolean HandleAliasNode(FrameworkNode aliasNode)
    {
        if (aliasNode.hasAttribute("File"))
        {
            String filename = aliasNode.getAttribute("File");
            getAliasMgr().ReadExternalAliasFile(filename);
        }
        for (FrameworkNode nodeAlias : aliasNode.getChildNodes())
        {
            if (nodeAlias.getNodeName().equalsIgnoreCase("Alias"))
            {
                NamedNodeMap map = nodeAlias.GetNode().getAttributes();
                for (int iLoop = 0; iLoop < map.getLength(); iLoop++)
                {
                    FrameworkNode node = new FrameworkNode(map.item(iLoop));

                    String strAlias = node.getNodeName();
                    String strValue = node.getTextContent();
                    AliasMgr._Mgr.AddAlias(strAlias, strValue);
                }
            }
        }
        return true;
    }

    public static boolean ReadAliasFromExternalFile(String FileName)
    {
        Document doc = OpenXMLFile(FileName);
        return ReadAliasFromRootDocument(doc);

    }

    public static boolean ReadAliasFromRootDocument(Document doc)
    {
        if (null != doc)
        {
            NodeList aliasList = doc.getElementsByTagName("AliasList");

            if (aliasList.getLength() < 1)
            {
                return true;
            }
            for (int iLoop = 0; iLoop < aliasList.getLength(); iLoop++)
            {
                if (false == AliasMgr.HandleAliasNode(new FrameworkNode(aliasList.item(iLoop))))
                {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    private void AddEnvironmentVars()
    {
        Map<String, String> env = System.getenv();
        for (String envName : env.keySet())
        {
            AddAlias(envName, env.get(envName));
        }
    }

    public void AddAliasFromAttibuteList(FrameworkNode node, String KnownAttributes[])
    {
        if (node.hasAttributes())
        {
            NamedNodeMap attrs = node.GetNode().getAttributes();

            for (int oLoop = 0; oLoop < attrs.getLength(); oLoop++)
            {
                boolean found = false;
                Attr attribute = (Attr) attrs.item(oLoop);
                for (int iLoop = 0; iLoop < KnownAttributes.length; iLoop++) // compare to list of valid
                {
                    if (0 == KnownAttributes[iLoop].compareToIgnoreCase(attribute.getName())) // 1st check case independent just for fun
                    {
                        found = true;
                        break;
                    }
                }
                if (false == found)
                {
                    AddAlias(attribute.getName(), attribute.getTextContent());
                    LOGGER.info("Adding Alias for external file from attribute list : " + attribute.getName() + "-->" + attribute.getTextContent());
                }
            }
        }
    }

    private int ReadExternalAliasFile(String filename)
    {
        LOGGER.info("Reading external Alias File: " + filename);

        BufferedReader br;
        try
        {
            br = new BufferedReader(new FileReader(filename));
        }
        catch (FileNotFoundException ex)
        {
            Logger.getLogger(AliasMgr.class.getName()).log(Level.SEVERE, null, ex);
            return 0;
        }
        String line;
        try
        {
            while ((line = br.readLine()) != null)
            {
                if (line.trim() != null)
                {
                    StringTokenizer st = new StringTokenizer(line, "=#");
                    String strAlias, Value;
                    if (st.hasMoreElements())
                    {
                        strAlias = ((String) st.nextElement()).trim();

                    }
                    else
                    {
                        continue; // no more
                    }
                    if (st.hasMoreElements())
                    {
                        Value = ((String) st.nextElement()).trim();
                        if (Value.charAt(0) == '"' && Value.charAt(Value.length() - 1) == '"')
                        {
                            Value = Value.substring(1, Value.length() - 1);
                        }
                    }
                    else
                    {
                        if (line.charAt(0) != '#')
                        {
                            LOGGER.severe("Bad Alias in Alias File: " + line); // only be here if line is something like alias=
                        }
                        continue;
                    }
                    AddAlias(strAlias, Value);
                }
            }
        }
        catch (IOException ex)
        {
            Logger.getLogger(AliasMgr.class.getName()).log(Level.SEVERE, null, ex);
        }
        try
        {
            br.close();
        }
        catch (IOException ex)
        {
            Logger.getLogger(AliasMgr.class.getName()).log(Level.SEVERE, null, ex);
        }

        // process the line.
        return 0;
    }

    public int LoadAliasFile(String encodedFilename)
    {
        StringTokenizer tokens = new StringTokenizer(encodedFilename, "=");
        tokens.nextElement(); // eat up the -alaisfile=
        String filename = (String) tokens.nextElement(); // to get to the real filename!
        return ReadExternalAliasFile(filename);
    }
}
