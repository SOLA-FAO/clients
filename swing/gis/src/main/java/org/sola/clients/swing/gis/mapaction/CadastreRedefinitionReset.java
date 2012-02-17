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
 * Cadastre redefinition reset command. It is used in the cadastre redefinition bundle to 
 * reset the process of redefinition.
 * 
 * @author Elton Manoku
 */
public class CadastreRedefinitionReset extends ExtendedAction {

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
        try {
            this.mapControl.reset();
            this.mapControl.refresh(false);
        } catch (Exception ex) {
            org.sola.common.logging.LogUtility.log(
                    GisMessage.CADASTRE_REDEFINITION_RESET_ERROR, ex);
            Messaging.getInstance().show(GisMessage.CADASTRE_REDEFINITION_RESET_ERROR);
        }
    }
}