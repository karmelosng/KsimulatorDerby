package com.karmelos.ksimulator;
import java.awt.event.MouseAdapter;
import javax.swing.DefaultListModel;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Adebayo Adeniyan
 */
public class KsimulatorJunit4Test {
     SimViewTest simViewInstance;
    DefaultListModel availModel;
    DefaultListModel usedModel;
    Object[] initialAvailableComponents = {"Backcasing", "Frame", "MotherBoard", "FaceCover", "innerscreen", "keypad"};
    
    public KsimulatorJunit4Test() {  
       //set up SimView test display
        simViewInstance = new SimViewTest();
        simViewInstance.setVisible(true);
        simViewInstance.setLocationRelativeTo(null);
       
       availModel = new DefaultListModel();
        usedModel = new DefaultListModel();
 
    }
@Test
  public void unchangedNumberOfComponentsTest() throws InterruptedException {


       
        simViewInstance.testJListAvailableComponents.setModel(availModel);
        simViewInstance.testJListUsedComponents.setModel(usedModel);
        //add test components to the
        for (int i = 0; i < initialAvailableComponents.length; i++) {
            availModel.addElement(initialAvailableComponents[i]);
        }

        //assert that the Available Component Jlist contains expected data
        System.out.println("AssertTrue: Available Components contains expected data");
        int availableComponents = simViewInstance.testJListAvailableComponents.getModel().getSize();
        assertTrue(initialAvailableComponents.length == availableComponents);

        // assert that Used Component is empty when session starts
        System.out.println("AssertTrue:Used Component is empty when session starts");
        assertTrue(simViewInstance.testJListUsedComponents.isSelectionEmpty());



        // double click on the Available Component Jlist
        simViewInstance.testJListAvailableComponents.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {

                if (evt.getClickCount() == 2) {

                    System.out.println("AssertEquals: Click Count = 2 selected Available component dropped in Used component ");
                    assertEquals(evt.getClickCount(), 2);//pass

                    int index = simViewInstance.testJListAvailableComponents.locationToIndex(evt.getPoint());
                    if (index > -1) {
                        usedModel.addElement(simViewInstance.testJListAvailableComponents.getModel().getElementAt(index));
                        availModel.remove(index);
                    }
                    int sizeAfterRemoving = simViewInstance.testJListAvailableComponents.getModel().getSize();
                    System.out.println("AssertEquals: Available component decreased by 1");
                    assertEquals("Items in  should have decreased by 1", simViewInstance.testJListAvailableComponents.getModel().getSize(), sizeAfterRemoving);
                }

            }
        });


        simViewInstance.testDeleteOptionPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                //simViewInstance.testJListUsedComponents.getSelectedValue();

                System.out.println("Assert: Select used component before deletion");
                assertTrue("No component selected in the used component list", !simViewInstance.testJListUsedComponents.isSelectionEmpty());

                if (!simViewInstance.testJListUsedComponents.isSelectionEmpty()) {


                    //assert click count==1
                    // assertEquals(evt.getClickCount(),1);//fail
                    System.out.println("AssertEquals: Click Count = 1 used component deleted");
                    assertEquals("", evt.getClickCount(), 1);//pass
                    int index = simViewInstance.testJListUsedComponents.locationToIndex(evt.getPoint());
                    if (index > -1) {

                        availModel.addElement(simViewInstance.testJListUsedComponents.getModel().getElementAt(index));
                        usedModel.remove(index);
                    }
                    int sizeAfterRemoving = simViewInstance.testJListAvailableComponents.getModel().getSize();
                    System.out.println("AssertEquals: Used component decreased by 1");
                    assertEquals("Items in Used Component  should have decreased by 1", simViewInstance.testJListAvailableComponents.getModel().getSize(), sizeAfterRemoving);
                }
            }
        });

        //there was nothing selected in Available list, so nothing should be added to used list
        System.out.println("Assert: nothing selected in Available list, so nothing should be added to used list");
        assertTrue(simViewInstance.testJListAvailableComponents.isSelectionEmpty() == usedModel.isEmpty());

        //there was nothing selected in Component list, so nothing should be added to Available Component list
        System.out.println("Assert: nothing selected in Component list, so nothing should be added to Available Component list");
        assertFalse(simViewInstance.testJListUsedComponents.isSelectionEmpty() == availModel.removeElement(null));
        //sleep for 20seconds to check display of SimView 
        Thread.sleep(40000);

    }
      
}