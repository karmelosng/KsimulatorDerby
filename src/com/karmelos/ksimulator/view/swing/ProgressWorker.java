/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.karmelos.ksimulator.view.swing;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.karmelos.ksimulator.model.SimComponent;
import java.util.List;
import java.util.Map;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

/**
 *
 * @author jumoke
 */
public class ProgressWorker extends SwingWorker<Map<SimComponent,ModelInstance>, Object>{
   private JProgressBar jProgbar;
   private int sizeModule;
   private JList listing;
   private List<SimComponent> listedComp;
   
 public ProgressWorker(JProgressBar jprog,List<SimComponent> listAvailable,JList listed){
   jProgbar= jprog;
   listing=listed;
   listedComp = listAvailable;
   sizeModule = listAvailable.size();
   jProgbar.setVisible(true);
  
 }
    @Override
    protected Map<SimComponent,ModelInstance> doInBackground() throws Exception {
       
//       ThreeDummyLoader tD = new ThreeDummyLoader(listedComp);
//        tD.loadAllScenes();
//       Map<SimComponent, ModelInstance> modelInstances = tD.getModelInstances();
       jProgbar.setMinimum(0);
       jProgbar.setMaximum(sizeModule);
       for(int i=0;i<sizeModule;i++){
       
       jProgbar.setValue(i);
       jProgbar.setString(i+" Scenes Loaded");
        Thread.sleep(2000);
       }
      return null;
    }

    @Override
    protected void done() {
         
        listing.setEnabled(true);
        jProgbar.setVisible(false);
    }
    
}
