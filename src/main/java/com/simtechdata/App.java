package com.simtechdata;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ResourceList;
import io.github.classgraph.ScanResult;
import javafx.application.Application;
import javafx.stage.Stage;

import javax.swing.*;
import java.awt.*;
import java.net.URL;


/**
 * JavaFX App
 */
public class App extends Application {

    private static final String dockIconBase = "Icons/logo.png";
    private static final JFrame jFrame       = new JFrame();

    @Override
    public void start(Stage stage) {
        setTaskbarDockIcon();
        new MainForm();
    }

    private void setTaskbarDockIcon() {
        ScanResult   scanResult = new ClassGraph().enableAllInfo().scan();
        ResourceList resources  = scanResult.getAllResources();
        URL          url        = null;
        for (URL u : resources.getURLs()) {
            if (u.toString().contains(dockIconBase)) {
                url = u;
                break;
            }
        }
        final Toolkit defaultToolkit = Toolkit.getDefaultToolkit();
        final Image image = defaultToolkit.getImage(url);
        final Taskbar taskbar = Taskbar.getTaskbar();

        try {
            //set icon for MacOS (and other systems which do support this method)
            taskbar.setIconImage(image);
        }
        catch (final UnsupportedOperationException e) {
            System.out.println("The os does not support: 'taskbar.setIconImage'");
        }
        catch (final SecurityException e) {
            System.out.println("There was a security exception for: 'taskbar.setIconImage'");
        }
        jFrame.setUndecorated(true);
        //set icon for Windows os (and other systems which do support this method)
        jFrame.setIconImage(image);
        //adding something to the window so it does show up
        jFrame.getContentPane().add(new JLabel("Wifi Repeater"));
        //some default JFrame things
        jFrame.setDefaultCloseOperation(jFrame.EXIT_ON_CLOSE);
        jFrame.pack();
        jFrame.setVisible(true);
        jFrame.setSize(new Dimension(26, 26));
    }

    public static void main(String[] args) {
        launch();
    }
}
