/*
-===` * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.karmelos.ksimulator.view.swing;

import com.karmelos.ksimulator.jdialogs.OkCancelOption;
import com.karmelos.ksimulator.jdialogs.OkOption;
import com.karmelos.ksimulator.model.SimComponent;
import com.karmelos.ksimulator.model.SimLocationPointId;
import com.karmelos.ksimulator.model.SimPoint;
import com.karmelos.ksimulator.model.SimState;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 *
 * @author MorpheuS
 */
public class SavePanel extends javax.swing.JPanel {

    private String popStringStatus;
    private String rwStringStatus;
    private String saveFullPath;
    private SimView wrk;
    private Date convertedDate;
    private Random temporaryStateID = new Random();
    /**
     * Creates new form SavePanel
     */
    public SavePanel() {
        try {
            Calendar cal = Calendar.getInstance();
            DateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String currtime = dateformat.format(cal.getTime());
            convertedDate = dateformat.parse(currtime);

        } catch (ParseException ex) {
            Logger.getLogger(SavePanel.class.getName()).log(Level.SEVERE, null, ex);
        }
        initComponents();
    }

    public SimView getWrk() {
        return wrk;
    }

    public void setWrk(SimView wrk) {
        this.wrk = wrk;
    }

    public String getSaveFullPath() {
        return saveFullPath;
    }

    public void setSaveFullPath(String saveFullPath) {
        this.saveFullPath = saveFullPath;
    }

    public String getPopStringStatus() {
        return popStringStatus;
    }

    public void setPopStringStatus(String popStringStatus) {
        this.popStringStatus = popStringStatus;
    }

    public String getRwStringStatus() {
        return rwStringStatus;
    }

    public void setRwStringStatus(String rwStringStatus) {
        this.rwStringStatus = rwStringStatus;
    }

    public JButton getContinueSaveButton() {
        return continueSaveButton;
    }

    public void setContinueSaveButton(JButton continueSaveButton) {
        this.continueSaveButton = continueSaveButton;
    }

    public JLabel getjLabel1() {
        return jLabel1;
    }

    public void setjLabel1(JLabel jLabel1) {
        this.jLabel1 = jLabel1;
    }

    public JLabel getjLabel2() {
        return jLabel2;
    }

    public void setjLabel2(JLabel jLabel2) {
        this.jLabel2 = jLabel2;
    }

    public JLabel getjLabel3() {
        return jLabel3;
    }

    public void setjLabel3(JLabel jLabel3) {
        this.jLabel3 = jLabel3;
    }

    public JComboBox getPopStatus() {
        return popStatus;
    }

    public void setPopStatus(JComboBox popStatus) {
        this.popStatus = popStatus;
    }

    public JComboBox getRwStatus() {
        return rwStatus;
    }

    public void setRwStatus(JComboBox rwStatus) {
        this.rwStatus = rwStatus;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        popStatus = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();
        rwStatus = new javax.swing.JComboBox();
        continueSaveButton = new javax.swing.JButton();

        setMaximumSize(new java.awt.Dimension(371, 236));

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 10)); // NOI18N
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/karmelos/ksimulator/2ndbaricon/save_results.png"))); // NOI18N
        jLabel1.setText("Select Save Properties");

        jLabel2.setText("Global Acess Settings:");

        popStatus.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Public", "Private" }));

        jLabel3.setText("Read or Write Access::");

        rwStatus.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Read Only", "Read and Write" }));

        continueSaveButton.setText("CONTINUE");
        continueSaveButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                continueSaveButtonMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(continueSaveButton)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                            .addGap(30, 30, 30)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 274, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createSequentialGroup()
                            .addGap(164, 164, 164)
                            .addComponent(jLabel2))
                        .addGroup(layout.createSequentialGroup()
                            .addGap(75, 75, 75)
                            .addComponent(popStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 229, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createSequentialGroup()
                            .addGap(160, 160, 160)
                            .addComponent(jLabel3))
                        .addGroup(layout.createSequentialGroup()
                            .addGap(75, 75, 75)
                            .addComponent(rwStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 229, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(67, 67, 67))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addComponent(jLabel1)
                .addGap(11, 11, 11)
                .addComponent(jLabel2)
                .addGap(6, 6, 6)
                .addComponent(popStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addComponent(jLabel3)
                .addGap(6, 6, 6)
                .addComponent(rwStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(29, 29, 29)
                .addComponent(continueSaveButton)
                .addContainerGap(60, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void continueSaveButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_continueSaveButtonMouseClicked

        boolean popStats = true;
        boolean rwStats = false;
        int pop = popStatus.getSelectedIndex();
        int rw = rwStatus.getSelectedIndex();
      
        if (pop == 1) {
            popStats = false;
        } else if (rw == 1) {
            rwStats = true;
        }
     
      int response = JOptionPane.showConfirmDialog(this, "Are You Sure of These Settings?", null, JOptionPane.OK_CANCEL_OPTION);
        
//        OkCancelOption ok_c = new OkCancelOption(null, "Are you sure");
//        ok_c.setLabel1("Are You Sure of These Settings?");
//      boolean response = ok_c.showDialog();
        wrk.repaint();

        SimState stateLocal = wrk.getController().getState();
         Map<SimComponent,SimPoint> tempMap = stateLocal.getPlacedComponents();
        
        if (response==JOptionPane.OK_OPTION) {
            
 
            // for fresh Save get all info first
            if (stateLocal != null && stateLocal.getId() == null &&
                    stateLocal.getPlacedComponents().size() > 0 &&wrk.getController().isFirstSave()) {
         
                // save State first temporary
                stateLocal.setGlobalAccessFlag(popStats);
                stateLocal.setReadWriteFlag(rwStats);
                stateLocal.setPlacedComponents(null);
                stateLocal.setSavedAt(convertedDate);
                 wrk.save();
                     
                  
               // create SimPoints and merge with that state id               
               SimState[] fetchSessions = wrk.getController().fetchSessions(false);              
               SimState lastSession;
               // get Single simstate for firsttimeuser               
               lastSession = fetchSessions[fetchSessions.length -1];             
                 Iterator<SimPoint> iterator = tempMap.values().iterator();
               Iterator<SimComponent> iteratorSim = tempMap.keySet().iterator();
                  while(iterator.hasNext()){
                      SimPoint s = iterator.next();
                    SimComponent next = iteratorSim.next();
                    SimLocationPointId simID = new SimLocationPointId();
                    simID.setComponentId(next.getId());
                    simID.setUserId(stateLocal.getSimUser().getUsername());
                    simID.setStateId(lastSession.getId());
                    
                    s.setSimPointLocId(simID);
                    getWrk().getController().saveObject(s);
                  }
                
                   stateLocal.setId(lastSession.getId());
                  stateLocal.setPlacedComponents(tempMap);
                    
                
              
             getWrk().getController().mergeObject(stateLocal);
             // change stuffs
              
                //exit save
                
                wrk.setOnSaveClear(true);
                wrk.getController().setDropOccured(false);
                wrk.getController().setFirstSave(false);
                 wrk.getController().afterFirstSaveReservoirCollector();
                wrk.repaint();


            }//end inner if
            
      else if ( stateLocal.getPlacedComponents().size() < 1) {
                // Means that Session Started but no selection of module type nad module has occured
//                JOptionPane.showMessageDialog(this, "You Cant Save this Empty Simulation! Select a module type First", null, JOptionPane.OK_CANCEL_OPTION);
//                 OkOption ok_cancel = new OkOption(null, "CAN'T SAVE"); 
      //  ok_c.setLabel1("You Cant Save this Empty Simulation! Select a module type First");
      //boolean resp = ok_c.showDialog();
        wrk.repaint();

            } 
//      else if(!getWrk().getController().isFirstSave()){
////            // save State first temporary
//               SimState newSimState= new SimState();
//               newSimState.setGlobalAccessFlag(stateLocal.getGlobalAccessFlag());
//                newSimState.setReadWriteFlag(stateLocal.getReadWriteFlag());
//                newSimState.setPlacedComponents(null);
//                newSimState.setAvailableComponents(stateLocal.getAvailableComponents());
//                newSimState.setSavedAt(convertedDate);
//                newSimState.setSimUser(stateLocal.getSimUser());
//                newSimState.setDescription("Dupicate of "+stateLocal.getId());
//             
//                 wrk.getController().saveObject(newSimState);
//           // Save SimPoints for them individually
//               SimState[] fetchSessions = wrk.getController().fetchSessions(false);              
//               SimState lastSession= fetchSessions[fetchSessions.length -1];
//               // get Single simstate for firsttimeuser  
//              
//               Iterator<SimPoint> iterator = tempMap.values().iterator();
//               Iterator<SimComponent> iteratorSim = tempMap.keySet().iterator();
//                  while(iterator.hasNext()){
//                      SimPoint s = iterator.next();
//                      // create Dummy SimPoint
//                     SimPoint sDummy = new SimPoint(s.getTopX(),s.getTopY());
//                    SimComponent next = iteratorSim.next();
//                    SimLocationPointId simID2 = new SimLocationPointId();
//                    simID2.setComponentId(next.getId());
//                    simID2.setUserId(lastSession.getSimUser().getUsername());
//                    simID2.setStateId(lastSession.getId());
//                    //initialize dummt SimPoints
//                    sDummy.setSimPointLocId(simID2);
//                    getWrk().getController().saveObject(sDummy);
//                  }
//                
//                
//                    
//                
//              
//             getWrk().getController().mergeObject(newSimState);
//         }
           

        }  // END OUTER IF HERE!!
        //Exit the Save Panel Anyways
        SwingUtilities.windowForComponent(this).setVisible(false);
    }//GEN-LAST:event_continueSaveButtonMouseClicked
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton continueSaveButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JComboBox popStatus;
    private javax.swing.JComboBox rwStatus;
    // End of variables declaration//GEN-END:variables
}
