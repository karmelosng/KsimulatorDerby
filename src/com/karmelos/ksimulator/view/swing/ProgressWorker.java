/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.karmelos.ksimulator.view.swing;

import com.karmelos.ksimulator.model.SimComponent;
import java.util.List;
import javax.swing.JList;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

/**
 *
 * @author jumoke
 */
public class ProgressWorker extends SwingWorker<Object, Object>{
   private JProgressBar jProgbar;
   private int sizeModule;
   private JList listing;
   
 public ProgressWorker(JProgressBar jprog,int Size,JList listed){
   jProgbar= jprog;
   listing=listed;
   sizeModule = Size;
   jProgbar.setVisible(true);
 }
    @Override
    protected Object doInBackground() throws Exception {
        //jProgbar.setVisible(true);
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
