package com.karmelos.ksimulator.view.swing;

/**
 *DemoVideoPanel:pop up box with a video information on how to use the application
 * 
 * @author MorpheuS
 */
public class DemoVideoPanel extends javax.swing.JPanel {

    /**
     * Creates new form DemoVideoPanel
     */
    public DemoVideoPanel() {
       
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        demoIcon = new javax.swing.JLabel();

        setBorder(javax.swing.BorderFactory.createTitledBorder("VideoTips"));

        demoIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/karmelos/ksimulator/2ndbaricon/photo_camera.png"))); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(47, 47, 47)
                .addComponent(demoIcon)
                .addContainerGap(227, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(48, 48, 48)
                .addComponent(demoIcon, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(151, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel demoIcon;
    // End of variables declaration//GEN-END:variables
}
