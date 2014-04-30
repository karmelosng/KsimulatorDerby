/*                                                                                                  
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.karmelos.ksimulator.view.swing;

/**
 *
 * @author niyid
 */

import java.awt.HeadlessException;
import javax.swing.JFrame;
import javax.swing.UIManager;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.tests.utils.GdxTest;
import com.karmelos.ksimulator.model.SimComponent;
import java.util.List;
import java.util.Map;

public class ObjectTestLoader extends JFrame {
    
    // empty 
    public ObjectTestLoader () throws HeadlessException {
    }

   
	/**
	 * Runs the {@link GdxTest} with the given name.
	 * 
	 * @param testName the name of a test class
	 * @return {@code true} if the test was found and run, {@code false} otherwise
	 */
	public static boolean runTest () {
		GdxTest test = new ObjectTest();
		if (test == null) {
			return false;
		}
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 640;
		config.height = 480;
		config.title = "3D Object Viewer";
		config.forceExit = false;
		new LwjglApplication(test, config);
		return true;
	}

	/**
	 * Runs a libgdx test.
	 * 
	 * If no arguments are provided on the command line, shows a list of tests to choose from.
	 * If an argument is present, the test with that name will immediately be run.
	 * 
	 * @param argv command line arguments
	 */
	public static void main (String[] argv) throws Exception {
		if (argv.length > 0) {
			if (runTest()) {
				return;
				// Otherwise, fall back to showing the list
			}
		}
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		new ObjectTestLoader();
                runTest();
	}
}
