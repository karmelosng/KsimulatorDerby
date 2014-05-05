package com.karmelos.ksimulator.view.swing;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.utils.Array;
import com.karmelos.ksimulator.model.SimComponent;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
 
public class Viewer implements ApplicationListener {
   ModelBatch modelBatch;
   Environment environment;
   PerspectiveCamera camera;
   AssetManager assets;
   CameraInputController cameraController; 
   int screenWidth;
   int screenHeight;
   List<SimComponent> listing;
    List<SimComponent> placed;
   Array<ModelInstance> instances = new Array<ModelInstance>();
   Map<SimComponent,ModelInstance> modelStash=new HashMap<SimComponent, ModelInstance>();
    private boolean toShow =true;
   
   public Viewer(boolean show,List<SimComponent> listed){
      
       assets = new AssetManager();
       listing = listed;
      toShow = show;
   }

    public List<SimComponent> getPlaced() {
        return placed;
    }

    public void setPlaced(List<SimComponent> placed) {
        this.placed = placed;
    }
   

   @Override
   public void create() {
       
      // Get screen dimensions
     // screenWidth = Gdx.graphics.getWidth();
     // screenHeight = Gdx.graphics.getHeight();
 
      // Create ModelBatch that will render all models using a camera
      modelBatch = new ModelBatch();
 
      // Create a camera and point it to our model
      camera = new PerspectiveCamera(75, screenWidth, screenHeight);
      camera.position.set(0,0, 1000);
      camera.lookAt(0, 0, 0);
       camera.near = 500f;
      camera.far =2000f;
      camera.update();
 
      // Create the generic camera input controller to make the app interactive
      cameraController = new CameraInputController(camera);
      Gdx.input.setInputProcessor(cameraController);
 
      /// Create an asset manager that lets us dispose all assets at once
        //loadAllScenes(listing); 
       if(toShow){
//                for(int i=1;i<4;i++){
//                  assets.load("data/obj_"+i+".g3db",Model.class);
//
//                 }  assets.finishLoading();
//              // Create an instance of our crate model and put it in an array
//               for(int i=1;i<4;i++){
//                  Model model = assets.get("data/obj_"+i+".g3db", Model.class);
//              ModelInstance inst = new ModelInstance(model);
//              instances.add(inst);
//                 }
       }
       else{
       // just load ModelInstances
           for(int i=0;i<listing.size();i++){
          assets.load("data/obj_"+listing.get(i).getId()+".g3db",Model.class);        
         }  assets.finishLoading();
      // Create an instance of our crate model and put it in an array
       for(int i=0;i<listing.size();i++){
          Model model = assets.get("data/obj_"+listing.get(i).getId()+".g3db", Model.class);
      ModelInstance inst = new ModelInstance(model);
         modelStash.put(listing.get(i), inst);
         }
       }
     
 
      // Set up environment with simple lighting
      environment = new Environment();
      environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
      environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -0.8f, 0.3f, -1f));
   }
 
   @Override
   public void dispose() {
      // Release all resources
     modelBatch.dispose();
      instances.clear();
      assets.dispose();
   }
 
   @Override
   public void render() {
      if(toShow){
      // Respond to user events and update the camera
      cameraController.update();
 
      // Clear the viewport
     // Gdx.gl.glViewport(0, 0, screenWidth, screenHeight);
     Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
 
      // Draw all model instances using the camera

      modelBatch.begin(camera);
      modelBatch.render(convertMapToList(modelStash, getPlaced()), environment);
      modelBatch.end();
      }
      else{
      System.out.print("sfasdfdsf");
      }
   }
   public Array<ModelInstance> convertMapToList(Map<SimComponent,ModelInstance> mI,List<SimComponent> list){
     Array<ModelInstance> instances = new Array<ModelInstance>();
       Iterator<SimComponent> iterator = list.iterator();
       while(iterator.hasNext()){
        SimComponent sC = iterator.next();
         ModelInstance get = mI.get(sC);
         instances.add(get);
       }
   return instances;
   }
   @Override
   public void resize(int width, int height) {
      // Update screen dimensions
      screenWidth = width;
      screenHeight = height;
 
      // Update viewport size and refresh camera matrices
      camera.viewportWidth = width;
      camera.viewportHeight = height;
      camera.update(true);
   }

    public boolean isToShow() {
        return toShow;
    }

    public void setToShow(boolean toShow) {
        this.toShow = toShow;
    }
 
   @Override
   public void pause() {
   }
 
   @Override
   public void resume() {
   }

}
