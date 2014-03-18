package com.karmelos.ksimulator.view.swing;                                                                    
         

import com.karmelos.ksimulator.model.SimComponent;
import com.sun.j3d.loaders.IncorrectFormatException;
import com.sun.j3d.loaders.ParsingErrorException;
import com.sun.j3d.loaders.Scene;
import com.sun.j3d.loaders.objectfile.ObjectFile;
import com.sun.j3d.utils.behaviors.vp.OrbitBehavior;
import com.sun.j3d.utils.universe.PlatformGeometry;
import com.sun.j3d.utils.universe.SimpleUniverse;
import com.sun.j3d.utils.universe.ViewingPlatform;
import java.awt.GraphicsConfiguration;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.media.j3d.Alpha;
import javax.media.j3d.AmbientLight;
import javax.media.j3d.Background;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.Group;
import javax.media.j3d.RotationInterpolator;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3f;


public class ViewFor3d extends javax.swing.JFrame {

    private boolean spin = false;
    private boolean noTriangulate = false;
    private boolean noStripify = false;
    private double creaseAngle = 60.0;
    private URL filename = null;    
    private List<SimComponent> placed= null;
    private SimpleUniverse univ = null;
    private BranchGroup scene = null;
    private double scaleRatio=0.00;
    private Map<SimComponent,BranchGroup> mapScenery;
    TransformGroup objScale;
    TransformGroup objTrans;
    BranchGroup objRoot;
    
    
    
    public BranchGroup createSceneGraph() throws FileNotFoundException {
   // Create the root of the branch graph
     objRoot = new BranchGroup();
    objRoot.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
    objRoot.setCapability(BranchGroup.ALLOW_DETACH);
    
      TransformGroup objScale = new TransformGroup();
        Transform3D t3d = new Transform3D();
        t3d.setScale(getScaleRatio());
        objScale.setTransform(t3d);
        objRoot.addChild(objScale);
    objTrans = new TransformGroup();
  objTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
  objTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);  
  objTrans.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);
  objScale.addChild(objTrans);

  
  ObjectFile f = new ObjectFile(0, 
    (float)(creaseAngle * Math.PI / 180.0));
  // load all 3d
        
  for(int i=0;i<placed.size();i++){ 
     BranchGroup bgTemp =mapScenery.get(placed.get(i)); 
        bgTemp.setCapability(BranchGroup.ALLOW_DETACH);
     
     bgTemp.detach();
     
     objTrans.addChild(bgTemp.cloneTree());         
  }
  

  BoundingSphere bounds = new BoundingSphere(new Point3d(0.0,0.0,0.0), 100.0);

        if (spin) {
    Transform3D yAxis = new Transform3D();
    Alpha rotationAlpha = new Alpha(-1, Alpha.INCREASING_ENABLE,
            0, 0,
            4000, 0, 0,
            0, 0, 0);

    RotationInterpolator rotator =
        new RotationInterpolator(rotationAlpha, objTrans, yAxis,
               0.0f, (float) Math.PI*2.0f);
    rotator.setSchedulingBounds(bounds);
    objTrans.addChild(rotator);
  } 

        // Set up the background
        Color3f bgColor = new Color3f(0.5f, 0.5f, 0.002f);
        Background bgNode = new Background(bgColor);
        bgNode.setApplicationBounds(bounds);
        objRoot.addChild(bgNode);

  return objRoot;
    }
 
    private Canvas3D createUniverse() {
  // Get the preferred graphics configuration for the default screen
  GraphicsConfiguration config =
      SimpleUniverse.getPreferredConfiguration();

  // Create a Canvas3D using the preferred configuration
  Canvas3D canvas3d = new Canvas3D(config);

  // Create simple universe with view branch
  univ = new SimpleUniverse(canvas3d);
        BoundingSphere bounds = new BoundingSphere(new Point3d(0.0,0.0,0.0), 100.0);

  // add mouse behaviors to the ViewingPlatform
  ViewingPlatform viewingPlatform = univ.getViewingPlatform();

  PlatformGeometry pg = new PlatformGeometry();

  // Set up the ambient light
  Color3f ambientColor = new Color3f(0.5f, 0.5f, 0.002f);
  AmbientLight ambientLightNode = new AmbientLight(ambientColor);
  ambientLightNode.setInfluencingBounds(bounds);
  pg.addChild(ambientLightNode);

  // Set up the directional lights
  Color3f light1Color = new Color3f(1.0f, 1.0f, 0.9f);
  Vector3f light1Direction  = new Vector3f(1.0f, 1.0f, 1.0f);
  Color3f light2Color = new Color3f(1.0f, 1.0f, 1.0f);
  Vector3f light2Direction  = new Vector3f(-1.0f, -1.0f, -1.0f);

  DirectionalLight light1
      = new DirectionalLight(light1Color, light1Direction);
  light1.setInfluencingBounds(bounds);
  pg.addChild(light1);

  DirectionalLight light2
      = new DirectionalLight(light2Color, light2Direction);
  light2.setInfluencingBounds(bounds);
  pg.addChild(light2);

  viewingPlatform.setPlatformGeometry( pg );
      
  // This will move the ViewPlatform back a bit so the
  // objects in the scene can be viewed.
  viewingPlatform.setNominalViewingTransform();

  if (!spin) {
            OrbitBehavior orbit = new OrbitBehavior(canvas3d,
                OrbitBehavior.REVERSE_ALL);
            orbit.setSchedulingBounds(bounds);
            viewingPlatform.setViewPlatformBehavior(orbit);      
  }               
   // Ensure at least 5 msec per frame (i.e., < 200Hz)
  univ.getViewer().getView().setMinimumFrameCycleTime(5);

  return canvas3d;
    }

   
    // constructor 
    public ViewFor3d(List<SimComponent> listedComponents,Map<SimComponent,BranchGroup> mapScene) {
    
         placed= listedComponents;
        mapScenery=mapScene;
         setScaleRatio(placed.get(0).getModule().getScaleRatio());
       
         this.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                //placed.clear();
               // mapScenery.clear();
            setVisible(false);
            }
   });
        
  // Initialize the GUI components
  initComponents();
  
  // Create Canvas3D and SimpleUniverse; add canvas to drawing panel
  Canvas3D c = createUniverse();
  drawingPanel.add(c, java.awt.BorderLayout.CENTER);
 
  // Create the content branch and add it to the universe
        try {
            scene = createSceneGraph();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ViewFor3d.class.getName()).log(Level.SEVERE, null, ex);
        }
  univ.addBranchGraph(scene);
    }

    // ----------------------------------------------------------------
   
    public double getScaleRatio() {
        return scaleRatio;
        
    }

    public void setScaleRatio(double scaleRatio) {
        this.scaleRatio = scaleRatio;
    }
  
    private void initComponents() {
        drawingPanel = new javax.swing.JPanel();
        
       setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("3DView");
        drawingPanel.setLayout(new java.awt.BorderLayout());

        drawingPanel.setPreferredSize(new java.awt.Dimension(500, 500));
        getContentPane().add(drawingPanel, java.awt.BorderLayout.CENTER);
       
        pack();
        
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel drawingPanel;
    // End of variables declaration//GEN-END:variables
 
}

