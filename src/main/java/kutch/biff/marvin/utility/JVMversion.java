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

/**
 * Lifted with pride from:
 * http://stackoverflow.com/questions/11079831/checking-the-version-of-jre-used-during-run-time
 *
 * @author Patrick Kutch
 */
public class JVMversion
{

    //specifies minimum major version. Examples: 5 (JRE 5), 6 (JRE 6), 7 (JRE 7) etc.

    public static final int MAJOR_VERSION = 8;

    //specifies minimum minor version. Examples: 12 (JRE 6u12), 23 (JRE 6u23), 2 (JRE 7u2) etc.
    public static final int BUILD_VERSION = 20;

    //checks if the version of the currently running JVM is bigger than
    //the minimum version required to run this program.
    //returns true if it's ok, false otherwise
    public static boolean isOKJVMVersion()
    {
        //get the JVM version
        String version = System.getProperty("java.version");

        //extract the major version from it
        int sys_major_version = Integer.parseInt(String.valueOf(version.charAt(2)));

        //if the major version is too low (unlikely !!), it's not good
        if (sys_major_version < MAJOR_VERSION)
        {
            return false;
        }
        else if (sys_major_version > MAJOR_VERSION)
        {
            return true;
        }
        else
        {
            //find the underline ( "_" ) in the version string
            int underlinepos = version.lastIndexOf("_");

            try
            {
                //everything after the underline is the minor version.
                //extract that
                int mv = Integer.parseInt(version.substring(underlinepos + 1));

                //if the minor version passes, wonderful
                return (mv >= BUILD_VERSION);

            }
            catch (NumberFormatException e)
            {
                //if it's not ok, then the version is probably not good
                return false;
            }
        }
    }

}
