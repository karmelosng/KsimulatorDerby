 package com.karmelos.ksimulator.controller;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.karmelos.ksimulator.exception.SimException;
import com.karmelos.ksimulator.jdialogs.OkOption;
import com.karmelos.ksimulator.model.Settings;
import java.util.List;
import com.karmelos.ksimulator.model.SimComponent;
import com.karmelos.ksimulator.model.SimModule;
import com.karmelos.ksimulator.model.SimModuleType;
import com.karmelos.ksimulator.model.SimPoint;
import com.karmelos.ksimulator.model.SimState;
import com.karmelos.ksimulator.model.SimUser;
import com.karmelos.ksimulator.model.SimStateNull;

import com.karmelos.ksimulator.view.swing.SimView;
import java.awt.Point;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.swing.JLabel;

// Minor Change in Code: Creates Two Entity Manangers ,One to retrieve
// components and the other for session saving or retrieval
// Ping Server prior to saving to check Server Status, if it fails save to local hence save session to server
public class SimController {
    //Entity Manager for local System

    @PersistenceContext(unitName = "SimulatorPU")
    private EntityManager entityManager;
    private EntityManagerFactory entityManagerfactory;
    //Entity Manager for Server Access
    private EntityManager entityManagerServer;
    private EntityManagerFactory entityManagerfactoryServer;
    public static boolean serverAvailablity = false;
    private SimState state;
    private SimStateNull dummyState;
    private SimUser simUser;
    private boolean dropOcurred = false;
    private boolean isEmptyPlacedComponent = true;
    private boolean isOpenDirective = false;
    private boolean gridPainter = true;
    private boolean clearAction = false;
    private boolean LoginClear =false;
    private  ResultSet settingsProperties;
    private List<JLabel> listofAttachedThemes;
    private Map<String,String> mapOfThemes= new HashMap<String, String>();
    // url format
    private static String baseUrl =  "jdbc:mysql://localhost:3306/ksimulator?zeroDateTimeBehavior=convertToNull";
    private static String baseUrl2 = "jdbc:derby://localhost:1527/ksim;";
    
    
    private boolean firstSave=true;
    private List<SimComponent> immediatePlacedComponentBeforeSave= new LinkedList<SimComponent>();
    private AssetManager assetCopy;
    private Map<SimComponent, ModelInstance>  preLoadedModel= new HashMap<SimComponent,ModelInstance>();
    public SimController() {
        // if starttSession is true. means a session has started, else dunmmystate
        dummyState = new SimStateNull();
        Map LocalProperty = new HashMap();
        LocalProperty.put("hibernate.show_sql", true);
        LocalProperty.put("hibernate.connection.url", baseUrl2.replaceAll("localhost", "127.0.0.1"));
        LocalProperty.put("hibernate.connection.username", "root");
        LocalProperty.put("hibernate.connection.password", "root");
        try{
        entityManagerfactory = Persistence.createEntityManagerFactory("Access", LocalProperty); }
        catch(Exception ex){
            OkOption ok = new OkOption(null, "Ksimulator");
            ok.setLabel1("Cannot find database for this application");
            ok.showDialog();
            System.exit(1);
        }
        entityManager = entityManagerfactory.createEntityManager();
        instantiateServerEntityManager();

    }

    public Map<SimComponent, ModelInstance> getPreLoadedModel() {
        return preLoadedModel;
    }

    public void setPreLoadedModel(Map<SimComponent, ModelInstance> preLoadedModel) {
  
        this.preLoadedModel = preLoadedModel;
    }
    
    public AssetManager getAssetCopy() {
        return assetCopy;
    }

    public void setAssetCopy(AssetManager assetCopy) {
        this.assetCopy = assetCopy;
    }

    public boolean isLoginClear() {
        return LoginClear;
    }

    public void setLoginClear(boolean LoginClear) {
        this.LoginClear = LoginClear;
    }
    
    private void instantiateServerEntityManager() {

        if (serverAvailablity) {
            
            String hostonly = retrieveServerAddress().split("//")[1];
            Map serverProperty = new HashMap();
            serverProperty.put("hibernate.show_sql", true);
            serverProperty.put("hibernate.connection.url", baseUrl.replaceAll("host", hostonly).replaceAll("simulatorserver","testserver"));
            entityManagerfactoryServer = Persistence.createEntityManagerFactory("AccessNetwork", serverProperty);
            entityManagerServer = entityManagerfactoryServer.createEntityManager();
            synchronizeServerLocal();
        }

    }
    public void deleteObject(Object simObject){
    // delete any Entity Object passed to it
        if (serverAvailablity) {
            try {
                entityManagerServer.getTransaction().begin();
                entityManagerServer.remove(simObject);

                entityManagerServer.flush();
            
            } finally {
            }

        } else {
            try {

                entityManager.getTransaction().begin();
                entityManager.remove(entityManager.merge(simObject));

                entityManager.flush();
                entityManager.getTransaction().commit();

            } finally {
            }


        }

    }
     // Start: This are a set of Utility methods that helps second or third time Saving of States  
    public boolean checkPlacedComponentsLag(){
    // TRUE for positive?addition and FALSE for negative?remove 
     int previous =immediatePlacedComponentBeforeSave.size();
     int present = state.getPlacedComponents().size();
     boolean flag;
      flag = present>previous?true:false;
    
    return flag;
    
    }
    public int getLengthOfComponentsPerModule(Long moduleid){
    
    Query q = entityManager.createQuery("select o from SimComponent o WHERE o.module.id = :moduleid");
        //convert list to SimState[] and return
        q.setParameter("moduleid", moduleid);       
        List<SimUser> result = q.getResultList();
       
     return result.size();
    }
    public boolean checkSameSimPoints(){
        // This checks if the placed components are completed  by chcking if the cordinates are same
        int sameCounter=1;
        boolean completed = false;
        Map<SimComponent, SimPoint> placedComponents = state.getPlacedComponents();
        
       Iterator<SimPoint> it= placedComponents.values().iterator();
       Iterator<SimComponent> sc = placedComponents.keySet().iterator();
       SimPoint firstPoint =it.next();
       SimComponent firstComponent= sc.next();
      int normalLength = getLengthOfComponentsPerModule(firstComponent.getModule().getId());
        while(it.hasNext()){
            SimPoint next = it.next();
        if(firstPoint.getTopX()==next.getTopX() && firstPoint.getTopY()== next.getTopY()){        
        sameCounter+=1;        
        }
        }
        
        if(sameCounter==normalLength){completed=true;}
    return completed;
    }
    public List<SimComponent> comparePlacedComponents(){
        // This utility method does the job of comparing immediate past and placed components
     boolean status = checkPlacedComponentsLag();
    List<SimComponent> newPlacedComponentList = new LinkedList<SimComponent>();
    Map<SimComponent, SimPoint> placedComponents = state.getPlacedComponents();
          Iterator<SimComponent> iterator = placedComponents.keySet().iterator();
          while(iterator.hasNext()){
          newPlacedComponentList.add(iterator.next());
          }
          // compare #immediatePlacedComponentBeforeSave and #newPlacedComponentList
          // this retu
     newPlacedComponentList.removeAll(immediatePlacedComponentBeforeSave);
    return newPlacedComponentList;
    }
    public List<SimComponent> afterFirstSaveReservoirCollector(){
          // this method keeps a reservoir of last simulation after save, b
          //efore a new Component is added and is called after every single merge
         immediatePlacedComponentBeforeSave = new LinkedList<SimComponent>();
          Map<SimComponent, SimPoint> placedComponents = state.getPlacedComponents();
          Iterator<SimComponent> iterator = placedComponents.keySet().iterator();
          while(iterator.hasNext()){
          immediatePlacedComponentBeforeSave.add(iterator.next());
          }

    return immediatePlacedComponentBeforeSave;
    }
 //END: This are a set of Utility methods that helps second or third time Saving of States

    public List<SimComponent> getImmediatePlacedComponentBeforeSave() {
        return immediatePlacedComponentBeforeSave;
    }

    public void setImmediatePlacedComponentBeforeSave(List<SimComponent> immediatePlacedComponentBeforeSave) {
        this.immediatePlacedComponentBeforeSave = immediatePlacedComponentBeforeSave;
    }
    
    
    public boolean isFirstSave() {
        return firstSave;
    }

    public void setFirstSave(boolean firstSave) {
        this.firstSave = firstSave;
    }
   // fetch ALl SImUsers
     public SimUser[] fetchSimUser() {
        try {
            Query q = entityManager.createQuery("select o from SimUser o");

            //convert list to SimModule[] and return
            List<SimModule> list = q.getResultList();
            SimUser[] listArray = new SimUser[list.size()];
            listArray = list.toArray(listArray);
            return listArray;
        } finally {
        }
    }
   // change StateUser/SimUser
     public boolean changeUser(SimUser simUser){
       if(state==null){
           
            Object[] tempObject = new Object[2];  
        tempObject[0] = "changeUserNoState";
        tempObject[1] = simUser;
        dummyState.setChanged();
        dummyState.notifyObservers(tempObject);
        
       }
       else{
       Object[] tempObject = new Object[2];  
        tempObject[0] = "changeUser";
        tempObject[1] = simUser;
        state.setChanged();
        state.notifyObservers(tempObject);
     
       }
     return true;
     }
     // This handles LogOut
     
     public boolean logOut(SimView s){
     
         String homeUser =System.getProperty("user.name");if(state==null){
         
        s.getLoggedLabel().setText(homeUser);
        s.setPresentSimUser(null);
       }
       else{
       Object[] tempObject = new Object[2];  
        tempObject[0] = "LogOut";
        tempObject[1] = homeUser;
        state.setChanged();
        state.notifyObservers(tempObject);
     
       }
     return true;
     
     
     
     }
     
    public static boolean isServerAvailablity() {
        return serverAvailablity;
    }

    public static void setServerAvailablity(boolean serverAvailablity) {
        SimController.serverAvailablity = serverAvailablity;
    }

    public List<JLabel> getListofAttachedThemes() {
        return listofAttachedThemes;
    }

    public void setListofAttachedThemes(List<JLabel> listofAttachedThemes) {
        this.listofAttachedThemes = listofAttachedThemes;
    }

    public SimComponent getDraggedComponent(Map<SimComponent, SimPoint> placedMap, SimPoint simPt) {
        // get a List of SimComponents
        List<SimComponent> vecSim = new ArrayList<SimComponent>();
        int positionOfComponent = 0;
        SimComponent drag = null;
        // iterate over the keys
        for (SimComponent s : placedMap.keySet()) {
            vecSim.add(s);
        }
        // iterate their points
        for (int i = 0; i < placedMap.size(); i++) {
            SimPoint temporary = placedMap.get(vecSim.get(i));
            if (checkAreaOfDrag(simPt, temporary)) {
                drag = vecSim.get(i);

            }

        }

        return drag;
    }
    
    public Point retrieveCursorOffset(SimPoint draggedImagePoint, SimPoint mousePoint) {
        Point tempPoint = new Point();
        int xOffset = (int) (mousePoint.getTopX() - draggedImagePoint.getTopX());
        int yOffset = (int) (mousePoint.getTopY() - draggedImagePoint.getTopY());

        tempPoint.x = xOffset;
        tempPoint.y = yOffset;
        return tempPoint;
    }

    public List<SimComponent> getDraggedComponents(Map<SimComponent, SimPoint> placedMap, SimPoint simPt) {
        // get a List of SimComponents
        List<SimComponent> vecSim = new ArrayList<SimComponent>();

        List<SimComponent> multidrag = new ArrayList<SimComponent>();
        // iterate over the keys
        for (SimComponent s : placedMap.keySet()) {
            vecSim.add(s);
        }
        // iterate their points
        for (int i = 0; i < placedMap.size(); i++) {
            SimPoint temporary = placedMap.get(vecSim.get(i));
            if (checkAreaOfDrag(simPt, temporary)) {
                multidrag.add(vecSim.get(i));

            }

        }

        return multidrag;
    }
    //check if multiDrag

    public boolean isMultiDrag(SimPoint t) {
        Map<SimComponent, SimPoint> placedMap = state.getPlacedComponents();
        int componentCount = 0;

        //  check if its within the component
        Iterator<SimPoint> iterator = placedMap.values().iterator();
        while (iterator.hasNext()) {
            SimPoint simpoint = iterator.next();

            if (checkAreaOfDrag(t, simpoint)) {

                componentCount += 1;
            }
        }

        if (componentCount > 1) {
            return true;
        } else {
            return false;
        }

    }

    public SimComponent getComponentToBeSucceeded(Map<SimComponent, SimPoint> placedMap, SimPoint sp, SimComponent dragged) {
        // get a List of SimComponents
        SimComponent compSucceeded = null;
        List<SimComponent> vecSim = new ArrayList<SimComponent>();
        // iterate over the keys
        for (SimComponent s : placedMap.keySet()) {
            vecSim.add(s);
        }
        // iterate their points
        for (int i = 0; i < placedMap.size(); i++) {
            SimPoint temporary = placedMap.get(vecSim.get(i));
            //updateStatustext("down"+ temporary.getTopX()+":"+temporary.getTopY()+",up:"+ sp.getTopX()+":"+sp.getTopY());
            if (checkAreaOfDrag(sp, temporary)) {

                if (!vecSim.get(i).equals(dragged)) {
                    compSucceeded = vecSim.get(i);
                }

            }

        }

        return compSucceeded;
    }

    public boolean canSucceed(SimComponent succedable, SimComponent draggable) {


        Set<SimComponent> successors = succedable.getSuccessors();

        boolean isSuccesor = successors.contains(draggable);


        return isSuccesor;
    }

    public boolean IsEmptyPlacedComponent() {
        return isEmptyPlacedComponent;
    }

    public void setEmptyPlacedComponent(boolean isEmptyPlacedComponent) {
        this.isEmptyPlacedComponent = isEmptyPlacedComponent;
    }

    public boolean isClearAction() {
        return clearAction;
    }

    public void setClearAction(boolean clearAction) {
        this.clearAction = clearAction;
    }

    public boolean checkAreaOfDrag(SimPoint click, SimPoint placedComp) {
        double maxPlacedX = placedComp.getTopX() + 260;
        double maxPlacedY = placedComp.getTopY() + 220;
        if (click.getTopX() <= maxPlacedX && click.getTopX() >= placedComp.getTopX() && click.getTopY() >= placedComp.getTopY() && click.getTopY() <= maxPlacedY) {
            return true;
        } else {
            return false;
        }


    }

    public SimUser login(String userName, String password) {
        SimUser loggedOnuser = null;


        Query q = entityManager.createQuery("select o from SimUser o WHERE o.username = :uname AND  password = :pword");
        //convert list to SimState[] and return
        q.setParameter("uname", userName);
        q.setParameter("pword", passwordHasher(password));
        List<SimUser> result = q.getResultList();
        if (result.size() == 1) {
            loggedOnuser = (SimUser) result.get(0);
        }
        return loggedOnuser;

    }

    public void startSimulation(Long moduleId, String description) {
        state = new SimState();
        state.setUsedComponents(Collections.EMPTY_MAP);
    }

    public void placeComponent(SimComponent simComponent, SimPoint simPoint) {
        Object[] tempObject = new Object[2];
        state.getPlacedComponents().put(simComponent, simPoint);
        state.getAvailableComponents().remove(simComponent);
        tempObject[0] = "placeComponent";
        tempObject[1] = simComponent;
        state.setChanged();
        state.notifyObservers(tempObject);

    }

    public void addComponent(SimComponent component) throws SimException {
    }

    public String getAllComponentsWithinArea(SimPoint mousePoint) {
        List<SimComponent> simComps = new ArrayList<SimComponent>();
        Iterator<SimComponent> iteratorPlacedkeys = getState().getPlacedComponents().keySet().iterator();
        Iterator<SimPoint> iteratorPlacedPoints = getState().getPlacedComponents().values().iterator();
        while (iteratorPlacedkeys.hasNext()) {
            SimPoint pointForPlaced = iteratorPlacedPoints.next();
            SimComponent simComp = iteratorPlacedkeys.next();

            if (checkAreaOfDrag(mousePoint, pointForPlaced)) {
                simComps.add(simComp);
            }

        }
        return StringifyList(simComps);
    }

    private String StringifyList(List<SimComponent> simComps) {
       // to use when it 
        Iterator<SimComponent> iterator = simComps.iterator();

        String strList = "";
        while (iterator.hasNext()) {
            SimComponent compIterator = iterator.next();
            if (compIterator.equals(simComps.get(simComps.size()))) {
                strList += compIterator.getComponentName();
            } else {
                strList += compIterator.getComponentName() + "_";
            }


        }

        return strList;
    }

    public int calculateFreshDropOverlapPercentage(SimComponent overlappableComponent, SimPoint pointOfDrop) {
        // get all placed Components and check if point of drop is within Location
        Map<SimComponent, SimPoint> placedComponents = state.getPlacedComponents();
        Iterator<SimComponent> iterator = placedComponents.keySet().iterator();
        Object[] resultObject = null;
        while (iterator.hasNext()) {
            SimComponent temp = iterator.next();
            // mathematics to check
            double minX = placedComponents.get(temp).getTopX();
            double minY = placedComponents.get(temp).getTopY();
            resultObject = compareLocation(minX, pointOfDrop);
            // check if it overlaps at all
            Object overlapCheck = resultObject[0];



        }


        return 1;
    }

    public double calculateDraggedOverlapPercentage(SimComponent baseComponent, int topComponent) {
        Map<SimComponent, SimPoint> placedComponents = state.getPlacedComponents();
        double overlap = 0;
        SimPoint baseSimPoint = new SimPoint(state.getPlacedComponents().get(baseComponent).getTopX(),
                state.getPlacedComponents().get(baseComponent).getTopY());


        Object[] result = isHorizontalOverlap(baseSimPoint.getTopX(), topComponent);
        boolean directive = Boolean.getBoolean(result[0].toString());

        if (directive) {
            // get the percentage
            overlap = Double.parseDouble(result[2].toString());

        }

        return overlap;
    }

    public SimPoint getBaseSimPoint(SimComponent baseComponent) {

        return state.getPlacedComponents().get(baseComponent);

    }

    public Object[] compareLocation(double placedCompMinX, SimPoint dropPoint) {

        return isHorizontalOverlap(placedCompMinX, dropPoint.getTopX());
    }

    public void updateStatustext(String Text) {

        Object[] results = new Object[2];

        results[0] = "updateStatus";
        results[1] = Text;
        if (state != null) {
            state.setChanged();
            state.notifyObservers(results);

        }



    }

    public Object[] isHorizontalOverlap(double placedComponentX, double dropPointX) {
        boolean isHOverlap = false;
        double componentMaxX = placedComponentX + 260;
        double ifLeftOverlapMaxX = dropPointX + 220;
        double percentOverlap = 0.0;
        Object[] results = new Object[3];
        String sideOverlap = null;
        if ((dropPointX > placedComponentX) && (dropPointX < componentMaxX)) {

            isHOverlap = true;
            sideOverlap = "right";
            percentOverlap = percentCalculator("right", dropPointX, componentMaxX);
        } else if ((ifLeftOverlapMaxX >= placedComponentX) && (ifLeftOverlapMaxX < componentMaxX)) {
            isHOverlap = true;
            sideOverlap = "left";
            percentOverlap = percentCalculator("left", placedComponentX, ifLeftOverlapMaxX);
        }
        results[0] = isHOverlap;
        results[1] = sideOverlap;
        results[2] = percentOverlap;

        return results;
    }

    public double percentCalculator(String side, double Min, double Max) {
        double diff = Max - Min;
        double percent = 0.0;
        if (side.equals("right")) {
            percent = (diff / Max) * 100;
        } else {
            percent = (diff / Min) * 100;
        }

        return percent;
    }

    public boolean checkCompleteSimulation() {
        boolean completed = false;
        if (state.getAvailableComponents().isEmpty()) {

            completed = true;

        }

        return completed;
    }

    public SimComponent undo() {
        return null;
    }

    public SimComponent redo() {
        return null;
    }

    public void open(SimState state) {
        this.state = state;
    }

    public void save() {
        //TODO Persist state to database, saves to server/network or Local
      if(serverAvailablity){
          SimState temporary = new SimState();
          
          temporary.setDescription("vule");//state.getDescription()
          temporary.setSimUser(state.getSimUser());
          
            
                  try {
            entityManagerServer.getTransaction().begin();
            //
            if (temporary.getId() == null) {
                entityManagerServer.persist(temporary);
            } else {
                entityManagerServer.merge(temporary);
            }
            entityManagerServer.flush();
            entityManagerServer.getTransaction().commit();
        } finally {
            // state = null;
        }
      
      } // end IF here
      else{
           try {
            entityManager.getTransaction().begin();
            //
            if (state.getId() == null) {
                System.out.println("PERSISTING");
                entityManager.persist(state);
            } else {
                System.out.println("MERGING");
                entityManager.merge(state);
            }
            entityManager.flush();
            entityManager.getTransaction().commit();
        } finally {
            // state = null;
        }
      
      
      }
   

    }

    public void saveObject(Object SimObject) {
      if(serverAvailablity){
       try {
            entityManagerServer.getTransaction().begin();
            entityManagerServer.persist(SimObject);

            entityManagerServer.flush();
            entityManagerServer.getTransaction().commit();
        } finally {
        }
     
      }
      else{
           try {
            entityManager.getTransaction().begin();
            entityManager.persist(SimObject);

            entityManager.flush();
            entityManager.getTransaction().commit();
            
        } finally {
        }
      
      
      
      
      }
       

    }

   
    
    
    public void mergeObject(Object SimObject) {
      if(serverAvailablity){
      try {
            entityManagerServer.getTransaction().begin();
            entityManagerServer.merge(SimObject);
            entityManagerServer.flush();
            entityManagerServer.getTransaction().commit();
        } finally {
        }// end if
      }
      else{
      try {
            entityManager.getTransaction().begin();
            entityManager.merge(SimObject);
            entityManager.flush();
            entityManager.getTransaction().commit();
            
        } finally {
         setFirstSave(false);
        // afterFirstSaveReservoirCollector();
        }// end if
      
   
      }
  }
  
    public void exit() {
        //TODO Clean SimState and open connections and exit
        state = null;
        
       // entityManagerServer.close();
        entityManager.close();
    }

    public SimModuleType[] fetchModuleTypes() {
        try {
            Query q = entityManager.createQuery("select o from SimModuleType o");

            //convert list to SimModule[] and return
            List<SimModule> list = q.getResultList();
            SimModuleType[] listArray = new SimModuleType[list.size()];
            listArray = list.toArray(listArray);
            return listArray;
        } finally {
        }
    }

    //fetchModule with a moduleID
    public SimModule fetchModule(Long moduleId) {
        try {
            return entityManager.find(SimModule.class, moduleId);
        } finally {
        }
    }

    //fetchSimModules
    public SimModule[] fetchModules(Long moduleTypeId) {


        try {
            Query q = entityManager.createQuery("select o from SimModule o where o.moduleType.id = :mtid ");
            q.setParameter("mtid", moduleTypeId);
            //convert list to SimModule[] and return
            List<SimModule> list = q.getResultList();
            SimModule[] listArray = new SimModule[list.size()];
            listArray = list.toArray(listArray);
            return listArray;
        } finally {
        }
    }
    // or SimState

    public SimState[] fetchSessions(boolean recent) {
        //TODO If recent is true, fetch recent 5 ordered descending by SimState.savedAt for logged in user
        //TODO If recent is false, fetch all SimState for logged in user
        Query q = null;
        if (recent) {
            q = entityManager.createQuery("select o from SimState o where o.simUser.username = :uname order by o.savedAt desc");
            q.setParameter("uname", getSimUser().getUsername());
            q.setFirstResult(0);
            q.setMaxResults(5);

        } else { 
            q = entityManager.createQuery("select o from SimState o where o.simUser.username = :uname");        
           q.setParameter("uname", state.getSimUser().getUsername());
        }
        try {

            //convert list to SimState[] and return
            List<SimState> list = q.getResultList();
            SimState[] listArray = new SimState[list.size()];
            listArray = list.toArray(listArray);
            return listArray;
        } finally {
        }
    }

    public SimState getState() {
        return state;
    }

    public SimUser getLoggedInUser(String name) {
        simUser = null;

        Query q = entityManager.createQuery("select o from SimUser o where o.username = :uname");
        //convert list to SimState[] and return
        q.setParameter("uname", name);
        List<SimUser> result = q.getResultList();
        if (result.size() == 1) {
            simUser = (SimUser) result.get(0);

        }

        return simUser;
    }

    public void setSimUser(SimUser simUser) {
        this.simUser = simUser;
    }

    public SimUser getSimUser() {
        return simUser;
    }

    public void setDropOccured(boolean ifitoccurred) {
        dropOcurred = ifitoccurred;
    }

    public boolean getDropOccured() {
        return dropOcurred;
    }

    public void closeState() {
        state = null;
    }

    public boolean IsOpenDirective() {
        return isOpenDirective;
    }

    public void setOpenDirective(boolean isOpenDirective) {
        this.isOpenDirective = isOpenDirective;
    }

    public boolean isGridPainter() {
        return gridPainter;
    }

    public void setGridPainter(boolean gridPainter) {
        this.gridPainter = gridPainter;
    }

    public void returnClearedComponent() {
        Object[] retList = new Object[2];
        Iterator<SimComponent> iterator = getState().getPlacedComponents().keySet().iterator();
        List<SimComponent> avList = getState().getAvailableComponents();

        while (iterator.hasNext()) {
            SimComponent componentCleared = iterator.next();
            avList.add(componentCleared);
            retList[0] = "clearAction";
            retList[1] = componentCleared;
            state.setChanged();
            state.notifyObservers(retList);
        }

    }

    public void setState(SimState state) {
        this.state = state;
    }
   
    public  String retrieveServerAddress() {
        String serverAdd="";
         List<Settings> list;
     try {
            Query q = entityManager.createQuery("select o from Settings o where o.keyword = :mtid ");
            q.setParameter("mtid", "serveraddress");
            //convert list to SimModule[] and return
            list = q.getResultList();
        } finally {
        }
     return list.get(0).getSettingvalues();
    }
    
    public  Map retrieveThemeSettings() throws SQLException {
        Iterator<Settings> iterater;
       try {
            Query q = entityManager.createQuery("select o from Settings o where o.themeUsable = :mtid ");
            q.setParameter("mtid", true);
            //convert list to SimModule[] and return
            List<Settings> list = q.getResultList();
             iterater =list.iterator();
        } finally {
        }

        return convertResultSetToMap(iterater);
    }
    public  String getPresentTheme(){
     String themeName="";
     Iterator<Settings> setup;
      try { 
             Query q = entityManager.createQuery("select o from Settings o where o.keyword = :mtid ");
            q.setParameter("mtid", "currenttheme");
            //convert list to SimModule[] and return
            setup = q.getResultList().iterator();
        } finally {
        }
  return setup.next().getSettingvalues();
    }
    // utility method 
    public Map<String,String> convertResultSetToMap(Iterator<Settings> convertee) throws SQLException{
       Map<String,String> converted= new HashMap<String, String>();
        while(convertee.hasNext()){
            Settings setup = convertee.next();
       converted.put(setup.getKeyword(),setup.getSettingvalues());
       
       }
      mapOfThemes=converted;  
   
    return converted;
    }
    
    // static method to retrieve ThemeName Alone
    
    
     public void setCurrentTheme(String current){
     // update currentTheme
       Query q = entityManager.createQuery("select o from Settings o where o.keyword = :mtid ");
            q.setParameter("mtid","currenttheme"); 
           Settings setup = (Settings) q.getResultList().get(0);
          setup.setSettingvalues(current);
          mergeObject(setup);
               }
     
     public List<SimComponent> retrieveAllSimComponent(){
     
     List<SimComponent> allSimComponents = null;

        Query q = entityManager.createQuery("select o from SimComponent o ");
        //convert list to SimState[] and return
        
       allSimComponents =  q.getResultList();
       

        return allSimComponents;
   
     }
     
     
    public Map<String, String> getMapOfThemes() {
        return mapOfThemes;
    }

    public void setMapOfThemes(Map<String, String> mapOfThemes) {
        this.mapOfThemes = mapOfThemes;
    }
    
    public static boolean pingServer(String url) {

        boolean availability = false;
        HttpURLConnection connection = null;
        try {
            URL u = new URL(url);
            connection = (HttpURLConnection) u.openConnection();
            connection.setRequestMethod("HEAD");
            int code = connection.getResponseCode();
            if (code == 200) {

                availability = true;
            } else {
                availability = false;
            }
            //  200 is success.
        } catch (Exception e) {
            // Handles all exception
            availability = false;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        serverAvailablity = availability;
         
        return availability;
    }
    
    public void synchronizeServerLocal(){
    // checks if theres little changes  or theres an additional module 
    // checks if a new moduletype, then MODULE!!
     syncModuleType();
      //syncModule();
    
    
    
    }
    // THese are Network Synchronisation methods , Optional if there's network
    void syncModuleType(){ 
        boolean alreadySynchronized= false;
      
        
        try {
            Query qLocal = entityManager.createQuery("select o from SimModuleType o");
            Query qServer = entityManagerServer.createQuery("select o from SimModuleType o");
            //convert list to SimModule[] and return
            List<SimModuleType> listL = qLocal.getResultList();
            List<SimModuleType> listS = qServer.getResultList();
            if(listL.size() == listS.size()){alreadySynchronized=true;}
            
            // if sync should occur 
            if(!alreadySynchronized){
                // get and saveObject
              
            SimModuleType[] listArray = new SimModuleType[listL.size()];
                  listArray = listL.toArray(listArray);
                 
               // SimModuleType[] toArray = (SimModuleType[])listL.toArray();
                SimModuleType lastMTypeLocal = listArray[listArray.length-1]; 
                // getIndex of lastMT and get remainder on server
                 int positionOnServer =-1;
                 for(int i=0;i<listS.size();i++){
                 if(lastMTypeLocal.getDescription().equals(listS.get(i).getDescription())){
                 positionOnServer=i+1;
                 }
                 }
                
                
                for(int i=positionOnServer;i<listS.size();i++){
                // save new Moduleype to localDb
                    SimModuleType sType = new SimModuleType();
                    sType.setDescription(listS.get(i).getDescription());
                    sType.setTypeName(listS.get(i).getTypeName());
                    sType.setModules(null);
                    syncSaveSimObject(sType);
                }
            
            }
            
        } finally {
        }
       
      
    }
    void syncModule(){
     boolean alreadySynchronized= false;
      
        
        try {
            Query qLocal = entityManager.createQuery("select o from SimModule o");
            Query qServer = entityManagerServer.createQuery("select o from SimModule o");
            //convert list to SimModule[] and return
            List<SimModule> listL = qLocal.getResultList();
            List<SimModule> listS = qServer.getResultList();
            if(listL.size() == listS.size()){alreadySynchronized=true;}
            
            // if sync should occur 
            if(!alreadySynchronized){
                // get and saveObject
              
            SimModule[] listArray = new SimModule[listL.size()];
                  listArray = listL.toArray(listArray);
                 
               // SimModuleType[] toArray = (SimModuleType[])listL.toArray();
                SimModule  lastMTypeLocal = listArray[listArray.length-1]; 
                // getIndex of lastMT and get remainder on server
                 int positionOnServer =-1;
                 for(int i=0;i<listS.size();i++){
                 if(lastMTypeLocal.getDescription().equals(listS.get(i).getDescription())){
                 positionOnServer=i+1;
                 }
                 }
                
                
                for(int i=positionOnServer;i<listS.size();i++){
                // save new Moduleype to localDb
                    SimModule sModule = new SimModule();
                    sModule.setDescription(listS.get(i).getDescription());
                    sModule.setModelName(listS.get(i).getModelName());
                    sModule.setModuleType(listS.get(i).getModuleType());
                    sModule.setComponents(null);
                    sModule.setVersionName(listS.get(i).getVersionName());
                    syncSaveSimObject(sModule);
                }
            
            }
            
        } finally {
        }
       
      
    }
   public String testrret(){return "weyrey";}
    void syncSimComponent(){
    boolean alreadySynchronized= false;
      
        
        try {
            Query qLocal = entityManager.createQuery("select o from SimComponent o");
            Query qServer = entityManagerServer.createQuery("select o from SimComponent o");
            //convert list to SimModule[] and return
            List<SimComponent> listL = qLocal.getResultList();
            List<SimComponent> listS = qServer.getResultList();
            if(listL.size() == listS.size()){alreadySynchronized=true;}
            
            // if sync should occur 
            if(!alreadySynchronized){
                // get and saveObject
              
            SimComponent[] listArray = new SimComponent[listL.size()];
                  listArray = listL.toArray(listArray);
                 
               // SimModuleType[] toArray = (SimModuleType[])listL.toArray();
                SimComponent  lastMTypeLocal = listArray[listArray.length-1]; 
                // getIndex of lastMT and get remainder on server
                 int positionOnServer =-1;
                 for(int i=0;i<listS.size();i++){
                 if(lastMTypeLocal.getDescription().equals(listS.get(i).getDescription())){
                 positionOnServer=i+1;
                 }
                 }
                
                
                for(int i=positionOnServer;i<listS.size();i++){
                // save new Moduleype to localDb
                    SimComponent sComponent = new SimComponent();
                    sComponent.setComponentName(listS.get(i).getComponentName());
                    sComponent.setDescription(listS.get(i).getDescription());
                    sComponent.setOverlayOrder(0);
                    sComponent.setRawIconImage(listS.get(i).getRawIconImage());
                    sComponent.setRawSolidImage(listS.get(i).getRawSolidImage());
                    sComponent.setRawWireframeImage(listS.get(i).getRawWireframeImage());
                    sComponent.setModule(listS.get(i).getModule());
                    sComponent.setRawDescriptionimage(listS.get(i).getRawDescriptionimage());                    
                    syncSaveSimObject(sComponent);
                }
            
            }
            
        } finally {
        }
       
      
    }
    void syncSuccessorRules(){
    
    }
    void syncSaveSimObject(Object simObject){
        try{
         entityManager.getTransaction().begin();
            entityManager.persist(simObject);
            entityManager.flush();
            entityManager.getTransaction().commit();
        
        
        }
        finally{}
    
    
    
    }
      public static String passwordHasher(String passWord){
         // this uses an md5
            byte[] bytes = passWord.getBytes(Charset.forName("UTF-8"));
        final char[] HEX_CHARS = "0123456789ABCDEF".toCharArray();
       MessageDigest md = null;
       try {
           md = MessageDigest.getInstance("SHA-1");
       }
       catch(NoSuchAlgorithmException e) {
       }
       byte[] buf = md.digest(bytes);
       char[] chars = new char[2 * buf.length];
       for (int i = 0; i < buf.length; ++i) {
           chars[2 * i] = HEX_CHARS[(buf[i] & 0xF0) >>> 4];
           chars[2 * i + 1] = HEX_CHARS[buf[i] & 0x0F];
       }
       return new String(chars);

   }

    public SimStateNull getDummyState() {
        return dummyState;
    }

    public void setDummyState(SimStateNull dummyState) {
        this.dummyState = dummyState;
    }
    
} //End Of Class Sim Controller

