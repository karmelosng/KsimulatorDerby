/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.karmelos.ksimulator.view.swing;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.karmelos.ksimulator.controller.SimController;
import com.karmelos.ksimulator.model.SimComponent;
import java.awt.Color;
import java.awt.Transparency;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.SwingWorker;

/**
 *
 * @author jumoke
 */
public class PreLoadAsync extends SwingWorker<Object, Object> {
    private JFrame frame;
    private SimController simControl;
    private List<SimComponent> listed;
  public PreLoadAsync(JFrame jframe,SimController sControl,List<SimComponent> listed){
   frame=jframe;
   simControl=sControl;
   this.listed = listed;
  
  }
    @Override
    protected Object doInBackground() throws Exception {
//       Viewer n=new Viewer(false,listed);
//                       n.setToShow(false);
//                    LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
//                        cfg.title ="3D-VIEWER ";
//                        cfg.initialBackgroundColor = com.badlogic.gdx.graphics.Color.WHITE;
//                        cfg.width = 640;
//                        cfg.height = 480;
//                       cfg.forceExit = false;
//                      LwjglApplication load= new LwjglApplication(n, cfg);
//                           simControl.setModelTutorial(n);
//                           frame.toFront();
//                           
                           return " ";
    }
    
}
