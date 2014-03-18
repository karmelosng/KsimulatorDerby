/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.karmelos.ksimulator.model;

import java.util.Observable;

/**
 *
 * @author Dare Fatimehin
 */
// Dummy Class to handle unstarted State issues, i.e  Changing SimUser when state isnt started
public class SimStateNull extends Observable{
    @Override
    public void setChanged() {
        super.setChanged();
    }
}
