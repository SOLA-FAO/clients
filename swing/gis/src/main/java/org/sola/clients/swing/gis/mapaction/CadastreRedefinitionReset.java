/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sola.clients.swing.gis.mapaction;

import org.geotools.swing.mapaction.extended.ExtendedAction;
import org.sola.clients.swing.gis.ui.controlsbundle.ControlsBundleForCadastreRedefinition;
import org.sola.common.messaging.GisMessage;
import org.sola.common.messaging.MessageUtility;

/**
 * Cadastre redefinition reset command. It is used in the cadastre redefinition bundle to 
 * reset the process of redefinition.
 * 
 * @author Elton Manoku
 */
public final class CadastreRedefinitionReset extends ExtendedAction {

    public final static String MAPACTION_NAME = "cadastre-redefinition-reset";
    private ControlsBundleForCadastreRedefinition mapControl;

    public CadastreRedefinitionReset(ControlsBundleForCadastreRedefinition mapControl) {
        super(mapControl.getMap(), "cadastre-redefinition-reset",
                MessageUtility.getLocalizedMessage(
                GisMessage.CADASTRE_REDEFINITION_RESET_TOOLTIP).getMessage(),
                "resources/cadastre-redefinition-reset.png");
        this.mapControl = mapControl;
    }

    @Override
    public void onClick() {
        this.mapControl.reset();
        this.mapControl.refresh(false);
    }
}