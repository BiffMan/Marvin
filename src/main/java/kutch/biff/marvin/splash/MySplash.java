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
package kutch.biff.marvin.splash;

import java.net.URL;
import java.util.logging.Logger;
import javafx.animation.AnimationTimer;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D; 
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import kutch.biff.marvin.configuration.Configuration;
import kutch.biff.marvin.logger.MyLogger;
import kutch.biff.marvin.version.Version;

/**
 *
 * @author Patrick Kutch
 */
public class MySplash
{

    private static MySplash _Splash;
    private final static Logger LOGGER = Logger.getLogger(MyLogger.class.getName());
    private Pane splashLayout;
    private ProgressBar loadProgress;
    private Label progressText;
    private static int SPLASH_WIDTH = 676;
    private static final int SPLASH_HEIGHT = 227;
    private final boolean _Show;
    Stage _Stage;
    AnimationTimer timer;
    double startTimerTime;

    static public MySplash getSplash()
    {
        return _Splash;
    }

    public MySplash(boolean show)
    {
        _Splash = this;
        _Show = show;
    }

    
    public void init()
    {
        if (false == _Show)
        {
            return;
        }

        URL resource = MySplash.class.getResource("Logo.png");

        Image splashImg = new Image(resource.toString());

        ImageView splash = new ImageView(splashImg);

        SPLASH_WIDTH = (int) splashImg.getWidth();

        loadProgress = new ProgressBar();
        loadProgress.setPrefWidth(SPLASH_WIDTH);
        
        progressText = new Label(Version.getVersion());
        progressText.setAlignment(Pos.CENTER);
        
        progressText.setStyle("-fx-content-display:center");
        
        splashLayout = new VBox();
        ((VBox)(splashLayout)).setAlignment(Pos.CENTER);

        splashLayout.getChildren().addAll(splash, loadProgress, progressText);
        
        splashLayout.setStyle("-fx-padding: 5; -fx-background-color: darkgray; -fx-border-width:5; -fx-border-color: darkslategray;");
        splashLayout.setEffect(new DropShadow());
    }

    public void start(Stage parentStage)
    {
        if (false == _Show)
        {
            return;
        }
        double TimerInterval = 5000;
        parentStage.setIconified(true);

        _Stage = new Stage();
        _Stage.setTitle("About Marvin");
        _Stage.initStyle(StageStyle.UNDECORATED);
        _Stage.toFront();

        showSplash(_Stage);

        startTimerTime = 0;
        timer = new AnimationTimer() // can't update the Widgets outside of GUI thread, so this is a little worker to do so
        {
            @Override
            public void handle(long now)
            {
                if (0 == startTimerTime)
                {
                    startTimerTime = System.currentTimeMillis();
                }
                if (System.currentTimeMillis() > startTimerTime + TimerInterval)
                {
                    parentStage.setIconified(false);
                    _Stage.close();
                    timer.stop();

                    if (true == Configuration.getConfig().getKioskMode())
                    {
                        parentStage.setResizable(false);
                    }
                }
            }
        };

        timer.start();
    }

    private void showSplash(Stage initStage)
    {
        Scene splashScene = new Scene(splashLayout);
        initStage.initStyle(StageStyle.UNDECORATED);
       
        final Rectangle2D bounds = Screen.getPrimary().getBounds();
        initStage.setScene(splashScene);
        initStage.setX(bounds.getMinX() + bounds.getWidth() / 2 - SPLASH_WIDTH / 2);
        initStage.setY(bounds.getMinY() + bounds.getHeight() / 2 - SPLASH_HEIGHT / 2);
        initStage.show();
    }
//
//    public int CalculateLoadItems(String filename)
//    {
//        Node doc = ConfigurationReader.OpenXMLFileQuietly(filename);
//        WidgetCount = DetermineNumberOfNodes(doc);
//        return WidgetCount;
//    }
//
//    private int DetermineNumberOfNodes(Node topNode)
//    {
//        if (null == topNode)
//        {
//            return 0;
//        }
//        NodeList Children = topNode.getChildNodes();
//        int iCount = 0;
//
//        for (int iLoop = 0; iLoop < Children.getLength(); iLoop++)
//        {
//            Node node = Children.item(iLoop);
//
//            if (node.getNodeName().equalsIgnoreCase("Widget")) // root
//            {
//                if (false == isFlip(node))
//                {
//                    iCount++;
//                }
//                else
//                {
//                    iCount += DetermineNumberOfNodes(node);
//                }
//            }
//            else if (node.getNodeName().equalsIgnoreCase("Grid") || node.getNodeName().equalsIgnoreCase("Tab"))
//            {
//                Element elem = (Element) node;
//                if (elem.hasAttribute("File"))
//                {
//                    iCount += CalculateLoadItems(elem.getAttribute("File"));
//                }
//                iCount += DetermineNumberOfNodes(node);
//            }
//            else if (node.hasChildNodes())
//            {
//                iCount += DetermineNumberOfNodes(node);
//            }
//        }
//        return iCount;
//    }

//    private boolean isFlip(Node topNode)
//    {
//        NodeList Children = topNode.getChildNodes();
//        for (int iLoop = 0; iLoop < Children.getLength(); iLoop++)
//        {
//            Node node = Children.item(iLoop);
//            if (node.getNodeName().equalsIgnoreCase("Front")) // root
//            {
//                return true;
//            }
//        }
//        return false;
//    }
}
