/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sola.clients.swing.gis.mapaction;

import org.geotools.swing.extended.util.Messaging;
import org.geotools.swing.mapaction.extended.ExtendedAction;
import org.sola.clients.swing.gis.ui.controlsbundle.ControlsBundleForCadastreRedefinition;
import org.sola.common.messaging.GisMessage;
import org.sola.common.messaging.MessageUtility;

/**
 *
 * @author Elton Manoku
 */
public class CadastreRedefinitionReset extends ExtendedAction {

    ControlsBundleForCadastreRedefinition mapControl;

    public CadastreRedefinitionReset(ControlsBundleForCadastreRedefinition mapControl) {
        super(mapControl.getMap(), "location-remove",
                MessageUtility.getLocalizedMessage(
                GisMessage.CADASTRE_TOOLTIP_REMOVE_LOCATION).getMessage(),
                "resources/application-location-remove.png");
        this.mapControl = mapControl;
    }

    @Override
    public void onClick() {
        try {
            this.mapControl.reset();
        } catch (Exception ex) {
            Messaging.getInstance().show("Reseted error");
        }
    }
}