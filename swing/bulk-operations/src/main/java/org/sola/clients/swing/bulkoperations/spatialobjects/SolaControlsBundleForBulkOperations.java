/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sola.clients.swing.bulkoperations.spatialobjects;

import org.geotools.swing.extended.exception.InitializeLayerException;
import org.sola.clients.swing.gis.Messaging;
import org.sola.clients.swing.gis.data.PojoDataAccess;
import org.sola.clients.swing.gis.layer.CadastreChangeNewCadastreObjectLayer;
import org.sola.clients.swing.gis.layer.CadastreChangeNewSurveyPointLayer;
import org.sola.clients.swing.gis.mapaction.CadastreChangeNewCadastreObjectListFormShow;
import org.sola.clients.swing.gis.mapaction.CadastreChangePointSurveyListFormShow;
import org.sola.clients.swing.gis.tool.CadastreChangeNewCadastreObjectTool;
import org.sola.clients.swing.gis.ui.controlsbundle.SolaControlsBundle;
import org.sola.common.messaging.GisMessage;

/**
 *
 * @author Elton Manoku
 */
public class SolaControlsBundleForBulkOperations extends SolaControlsBundle{
    
    private CadastreChangeNewCadastreObjectLayer newCadastreObjectLayer = null;
    private CadastreChangeNewSurveyPointLayer newPointsLayer = null;
    private CadastreChangeNewCadastreObjectTool newCadastreObjectTool;

    public SolaControlsBundleForBulkOperations(){
        super();
        this.Setup(PojoDataAccess.getInstance());
    }

    @Override
    public void Setup(PojoDataAccess pojoDataAccess) {
        super.Setup(pojoDataAccess);
        try {
            
            //Adding layers
            this.addLayers();

            //Adding tools and commands
            this.addToolsAndCommands();

        } catch (InitializeLayerException ex) {
            Messaging.getInstance().show(GisMessage.CADASTRE_CHANGE_ERROR_SETUP);
            org.sola.common.logging.LogUtility.log(GisMessage.CADASTRE_CHANGE_ERROR_SETUP, ex);
        }
    }

    protected void addLayers() throws InitializeLayerException {

        this.newCadastreObjectLayer = new CadastreChangeNewCadastreObjectLayer("");
        this.getMap().addLayer(newCadastreObjectLayer);

        this.newPointsLayer = new CadastreChangeNewSurveyPointLayer(this.newCadastreObjectLayer);
        this.getMap().addLayer(newPointsLayer);
    }

    protected void addToolsAndCommands() {
        this.getMap().addMapAction(
                new CadastreChangePointSurveyListFormShow(
                this.getMap(), this.newPointsLayer.getHostForm()),
                this.getToolbar(),
                true);

        this.newCadastreObjectTool =
                new CadastreChangeNewCadastreObjectTool(this.newCadastreObjectLayer);
        this.newCadastreObjectTool.getTargetSnappingLayers().add(newPointsLayer);
        this.getMap().addTool(newCadastreObjectTool, this.getToolbar(), true);

        this.getMap().addMapAction(new CadastreChangeNewCadastreObjectListFormShow(
                this.getMap(), this.newCadastreObjectLayer.getHostForm()),
                this.getToolbar(),
                true);
    }

    public CadastreChangeNewCadastreObjectLayer getNewCadastreObjectLayer() {
        return newCadastreObjectLayer;
    }

    public CadastreChangeNewSurveyPointLayer getNewPointsLayer() {
        return newPointsLayer;
    }
    
    
}
