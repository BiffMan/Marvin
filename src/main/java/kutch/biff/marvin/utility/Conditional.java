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

import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import kutch.biff.marvin.datamanager.DataManager;
import kutch.biff.marvin.logger.MyLogger;
import kutch.biff.marvin.task.TaskManager;

/**
 *
 * @author Patrick Kutch
 */
public class Conditional
{

    private final static Logger LOGGER = Logger.getLogger(MyLogger.class.getName());
    private final TaskManager TASKMAN = TaskManager.getTaskManager();

    public enum Type
    {

        EQ, NE, GT, GE, LT, LE, Invalid
    };
    private String _Value1_ID;
    private String _Value1_Namespace;
    private String _Value2_ID;
    private String _Value2_Namespace;
    private String _Value2;
    private Type _type;
    private String _If_Task;
    private String _Else_Task;
    private boolean _CaseSensitive;

    public Conditional(Conditional.Type type, String ID, String Namespace)
    {
        _type = type;
        _Value1_ID = ID;
        _Value1_Namespace = Namespace;
        _Value2_ID = null;
        _Value2_Namespace = null;
        _Value2 = null;
        _If_Task = null;
        _Else_Task = null;
    }

    public static Type GetType(String strType)
    {
        if (strType.equalsIgnoreCase("IF_EQ"))
        {
            return Type.EQ;
        }
        if (strType.equalsIgnoreCase("IF_NE"))
        {
            return Type.NE;
        }
        if (strType.equalsIgnoreCase("IF_GE"))
        {
            return Type.GE;
        }
        if (strType.equalsIgnoreCase("IF_GT"))
        {
            return Type.GT;
        }
        if (strType.equalsIgnoreCase("IF_LE"))
        {
            return Type.LE;
        }
        if (strType.equalsIgnoreCase("IF_LT"))
        {
            return Type.LT;
        }
        if (strType.equalsIgnoreCase("IF_EQ"))
        {
            return Type.EQ;
        }
        return Type.Invalid;
    }

    public void Enable()
    {
        DataManager.getDataManager().AddListener(_Value1_ID, _Value1_Namespace, new ChangeListener()
        {
            @Override
            @SuppressWarnings("unchecked")
            public void changed(ObservableValue o, Object oldVal, Object newVal)
            {
                Perform(newVal.toString());
            }
        });
    }

    private String GetValue2()
    {
        if (_Value2_ID != null && _Value2_Namespace != null)
        {
            return DataManager.getDataManager().GetValue(_Value2_ID, _Value2_Namespace);
        }
        return _Value2;
    }

    private void Perform(String Val1)
    {

        String Val2 = GetValue2();
        if (null == Val1 || null == Val2)
        {
            LOGGER.warning("Tried to perform Conditional, but data not yet available");
            return;
        }
        boolean result;
        try
        {
            result = PerformValue(Double.parseDouble(Val1), Double.parseDouble(Val2));
        }
        catch (NumberFormatException ex)
        {
            result = PerformString(Val1, Val2);
        }
        if (result)
        {
            TASKMAN.AddDeferredTask(_If_Task);
        }
        else if (_Else_Task != null)
        {
            TASKMAN.AddDeferredTask(_Else_Task);
        }
    }

    private boolean PerformString(String Val1, String Val2)
    {
        if (!isCaseSensitive())
        {
            Val1 = Val1.toLowerCase();
            Val2 = Val2.toLowerCase();
        }
        Val1 = Val1.trim();
        Val2 = Val2.trim();
        switch (_type)
        {
            case EQ:
                return Val1.equals(Val2);
            case NE:
                return !Val1.equals(Val2);

            case GT:
                return Val1.compareTo(Val2) > 0;

            case GE:
                return Val1.compareTo(Val2) >= 0;

            case LT:
                return Val1.compareTo(Val2) < 0;

            case LE:
                return Val1.compareTo(Val2) <= 0;
        }
        return false;
    }

    private boolean PerformValue(double Val1, double Val2)
    {
        switch (_type)
        {
            case EQ:
                return Val1 == Val2;

            case NE:
                return Val1 != Val2;

            case GT:
                return Val1 > Val2;

            case GE:
                return Val1 >= Val2;

            case LT:
                return Val1 < Val2;

            case LE:
                return Val1 <= Val2;
        }
        return false;
    }

    public String getValue1_Namespace()
    {
        return _Value1_Namespace;
    }

    public void setValue1_Namespace(String _Value1_Namespace)
    {
        this._Value1_Namespace = _Value1_Namespace;
    }

    public String getValue2_ID()
    {
        return _Value2_ID;
    }

    public void setValue2_ID(String _Value2_ID)
    {
        this._Value2_ID = _Value2_ID;
    }

    public String getValue2_Namespace()
    {
        return _Value2_Namespace;
    }

    public void setValue2_Namespace(String _Value2_Namespace)
    {
        this._Value2_Namespace = _Value2_Namespace;
    }

    public String getValue2()
    {
        return _Value2;
    }

    public void setValue2(String _Value2)
    {
        this._Value2 = _Value2;
    }

    public String getIf_Task()
    {
        return _If_Task;
    }

    public void setIf_Task(String _If_Task)
    {
        this._If_Task = _If_Task;
    }

    public String getElse_Task()
    {
        return _Else_Task;
    }

    public void setElse_Task(String _Else_Task)
    {
        this._Else_Task = _Else_Task;
    }

    public boolean isCaseSensitive()
    {
        return _CaseSensitive;
    }

    public void setCaseSensitive(boolean _CaseSensitive)
    {
        this._CaseSensitive = _CaseSensitive;
    }

}
