/**
 * ******************************************************************************************
 * Copyright (C) 2015 - Food and Agriculture Organization of the United Nations (FAO).
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 *    1. Redistributions of source code must retain the above copyright notice,this list
 *       of conditions and the following disclaimer.
 *    2. Redistributions in binary form must reproduce the above copyright notice,this list
 *       of conditions and the following disclaimer in the documentation and/or other
 *       materials provided with the distribution.
 *    3. Neither the name of FAO nor the names of its contributors may be used to endorse or
 *       promote products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,PROCUREMENT
 * OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,STRICT LIABILITY,OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * *********************************************************************************************
 */
package org.sola.clients.swing.gis;

import java.awt.Component;
import java.util.HashMap;
import java.util.Map;
import org.junit.Ignore;
import org.junit.Test;
import org.sola.clients.beans.administrative.BaUnitBean;
import org.sola.clients.beans.application.ApplicationBean;
import org.sola.clients.beans.converters.TypeConverters;
import org.sola.clients.swing.gis.ui.controlsbundle.ControlsBundleForApplicationLocation;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.WKBWriter;
import com.vividsolutions.jts.io.WKTReader;
import java.awt.Dimension;
import javax.swing.JDialog;
import org.geotools.swing.extended.util.GeometryUtility;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.sola.clients.beans.security.SecurityBean;
import org.sola.clients.swing.gis.beans.CadastreObjectBean;
import org.sola.clients.swing.gis.beans.TransactionCadastreChangeBean;
import org.sola.clients.swing.gis.ui.controlsbundle.ControlsBundleForBaUnit;
import org.sola.clients.swing.gis.ui.controlsbundle.ControlsBundleForCadastreChange;
import org.sola.clients.swing.gis.data.PojoDataAccess;
import org.sola.clients.swing.gis.imagegenerator.MapImageGeneratorForSelectedParcel;
import org.sola.clients.swing.gis.imagegenerator.MapImageInformation;
import org.sola.clients.swing.gis.mapaction.TestAction;
import org.sola.clients.swing.gis.ui.controlsbundle.*;
import org.sola.services.boundary.wsclients.WSManager;
import org.sola.webservices.search.MapDefinitionTO;
import org.sola.webservices.transferobjects.administrative.BaUnitTO;
import org.sola.webservices.transferobjects.casemanagement.ApplicationTO;

/**
 * Unit test for simple App.
 */
public class Development {

    /**
     * Test the controls bundle for setting the location of an application
     */
    @Ignore
    @Test
    public void testUIControlsBundleViewer() throws Exception {
        System.out.println("Test ControlsBundle for Viewer");
        System.err.println("Language from user.property:" + System.getProperty("user.language"));
        SecurityBean.authenticate("test", "test".toCharArray(), this.getWSConfig());
        ControlsBundleViewer ctrl = new ControlsBundleViewer();
        this.displayControlsBundleForm(ctrl);
    }

    @Ignore
    @Test
    public void testUIControlsBundleForSpatialUnitGroupManagement() throws Exception {
        System.out.println("Test ControlsBundle for Spatial unit group");
        SecurityBean.authenticate("test", "test".toCharArray(), this.getWSConfig());
        ControlsBundleForSpatialUnitGroupEditor ctrl = 
                new ControlsBundleForSpatialUnitGroupEditor();
        this.displayControlsBundleForm(ctrl);
    }

    /**
     * Test the controls bundle for setting the location of an application
     */
    @Ignore
    @Test
    public void testUIControlsBundleForPublicDisplay() throws Exception {
        System.out.println("Test ControlsBundle for public display");
        SecurityBean.authenticate("test", "test".toCharArray(), this.getWSConfig());
        ControlsBundleForPublicDisplay ctrl = new ControlsBundleForPublicDisplay(null, null);
        this.displayControlsBundleForm(ctrl);
    }


    /**
     * Test the controls bundle for setting the location of an application
     */
    @Ignore
    @Test
    public void testUIControlsBundleForBaUnit() throws Exception {
        System.out.println("Test ControlsBundle for setting cadastre objects");
        SecurityBean.authenticate("test", "test".toCharArray(), this.getWSConfig());
        BaUnitTO baUnitTO =WSManager.getInstance().getAdministrative().getBaUnitById("3068323");
        BaUnitBean baUnitBean = new BaUnitBean();
        TypeConverters.TransferObjectToBean(baUnitTO, BaUnitBean.class, baUnitBean);

        ControlsBundleForBaUnit ctrl = new ControlsBundleForBaUnit();
        ctrl.setCadastreObjects(baUnitBean.getCadastreObjectList());
        this.displayControlsBundleForm(ctrl);
    }

    /**
     * Test the controls bundle for setting the location of an application
     */
    @Ignore
    @Test
    public void testUIControlsBundleForApplicationLocation() throws Exception {
        System.out.println("Test ControlsBundle for setting Application Location");
        SecurityBean.authenticate("test", "test".toCharArray(), this.getWSConfig());
        MapDefinitionTO mapDef = PojoDataAccess.getInstance().getMapDefinition();
        WKTReader wktReader = new WKTReader();
        WKBWriter wkbWriter = new WKBWriter();
        double x = mapDef.getWest() + (mapDef.getEast() - mapDef.getWest()) / 2;
        double y = mapDef.getSouth() + (mapDef.getNorth() - mapDef.getSouth()) / 2;
        Geometry geom = wktReader.read(
                String.format("MULTIPOINT(%s %s)", x, y));

        geom.setSRID(mapDef.getCrsList().get(0).getSrid());
        byte[] result = wkbWriter.write(geom);

        ControlsBundleForApplicationLocation ctrl = new ControlsBundleForApplicationLocation();
        ctrl.setApplicationLocation(result);
        this.displayControlsBundleForm(ctrl);
    }

    private void displayControlsBundleForm(Component ctrl) {
        JDialog controlContainer = new JDialog();
        //controlContainer.setAlwaysOnTop(true);
        controlContainer.setModal(true);
        ctrl.setPreferredSize(new Dimension(600, 600));
        controlContainer.getContentPane().add(ctrl);
        controlContainer.pack();
        controlContainer.setVisible(true);
    }

    /**
     * Test the controls bundle for cadastre change
     */
    @Ignore
    @Test
    public void testUIControlsBundleForCadastreChange() throws Exception {
        System.out.println("Test ControlsBundle for cadastre change");
        CadastreObjectBean bean = new CadastreObjectBean();
        bean.setNameFirstpart("test");
        bean.setNameLastpart("fund");
        String[] fromFieldsOnly = new String[2];
        fromFieldsOnly[0] = "nameFirstpart";
        fromFieldsOnly[1] = "nameLastpart";
        Map result = bean.getValues(fromFieldsOnly);
        
        HashMap<String, Object> values = new HashMap<String, Object>();
        values.put("nameFirstpart", "test2");
        values.put("nameLastpart", "test2-last");
        values.put("officialArea", 2.0);
        //bean.setValues(values);

        WKTReader wktReader = new WKTReader();
        Geometry geom = wktReader.read("POINT(1782978 5926627)");
        byte[] geomAsBytes = GeometryUtility.getWkbFromGeometry((Geometry)geom.clone());
        Geometry geom2 = GeometryUtility.getGeometryFromWkb(geomAsBytes.clone());
        
        SecurityBean.authenticate("test", "test".toCharArray(), this.getWSConfig());
        
        ControlsBundleForCadastreChange ctrl = new ControlsBundleForCadastreChange(
                this.getApplicationBean("3001"), "4000", "3078053", "parcel");
        
        //ctrl.setReadOnly(true);

        this.displayControlsBundleForm(ctrl);
    }

    /**
     * Test the controls bundle for cadastre change
     */
    @Ignore
    @Test
    public void testUIControlsBundleForCadastreChangeById() throws Exception {
        System.out.println("Test ControlsBundle for cadastre change");
        
        SecurityBean.authenticate("test", "test".toCharArray(), this.getWSConfig());
                    String transactionId = "0e9625c4-c723-4ec3-bc5c-138b47c3f56c";

        TransactionCadastreChangeBean trns = 
                PojoDataAccess.getInstance().getTransactionCadastreChangeById(transactionId);
        ControlsBundleForCadastreChange ctrl = new ControlsBundleForCadastreChange(
                trns, "parcel", "AAAA");
        
        //ctrl.setReadOnly(true);

        this.displayControlsBundleForm(ctrl);
    }

    @Ignore
    @Test
    public void testUIControlsBundleForCadastreRedefinition() throws Exception {
        System.out.println("Test ControlsBundle for cadastre redefinition");

        SecurityBean.authenticate("test", "test".toCharArray(), this.getWSConfig());

        ControlsBundleForCadastreRedefinition ctrl =
                new ControlsBundleForCadastreRedefinition(
                this.getApplicationBean("3001"), "4011", "3068323", "parcel");
        this.displayControlsBundleForm(ctrl);
    }

    @Ignore
    @Test
    public void testUIControlsBundleForSpatialUnitManagement() throws Exception {
        System.out.println("Test ControlsBundle for Spatial unit");
        SecurityBean.authenticate("test", "test".toCharArray(), this.getWSConfig());
        ControlsBundleForSpatialUnitEditor ctrl = 
                new ControlsBundleForSpatialUnitEditor();
        this.displayControlsBundleForm(ctrl);
    }
    
    @Ignore
    @Test
    public void TestThroughAction() throws Exception {
        System.out.println("Test through action");
        SecurityBean.authenticate("test", "test".toCharArray(), this.getWSConfig());
        ControlsBundleViewer ctrl = new ControlsBundleViewer();
        ctrl.getMap().addMapAction(new TestAction(ctrl.getMap()), ctrl.getToolbar(), true);
        this.displayControlsBundleForm(ctrl);
    }

    @Test
    public void TestTitlePlan() throws Exception {
        System.out.println("Test title plan");
        SecurityBean.authenticate("test", "test".toCharArray(), this.getWSConfig());
        MapImageGeneratorForSelectedParcel gen = 
                new MapImageGeneratorForSelectedParcel(400, 500, 200, 40);
        MapImageInformation info = gen.getMapAndScalebarImage("5050170");
        System.out.println(info);
        info = gen.getMapAndScalebarImage("4821936");
        System.out.println(info);
    }
    
    //    //@Ignore
//    @Test
//    public void testImportPanel() throws Exception {
//        System.out.println("Test import panel");
//
//       // SecurityBean.authenticate("test", "test".toCharArray(), this.getWSConfig());
//
//        ImportSpatialPanel ctrl = new ImportSpatialPanel();
//        this.displayControlsBundleForm(ctrl);
//    }

    private HashMap<String, String> getWSConfig() {
        HashMap<String, String> wsConfig = new HashMap<String, String>();
        wsConfig.put("SOLA_WS_CASE_MANAGEMENT_SERVICE_URL", "http://localhost:8080/sola/webservices/casemanagement-service?wsdl");
        wsConfig.put("SOLA_WS_REFERENCE_DATA_SERVICE_URL", "http://localhost:8080/sola/webservices/referencedata-service?wsdl");
        wsConfig.put("SOLA_WS_ADMIN_SERVICE_URL", "http://localhost:8080/sola/webservices/admin-service?wsdl");
        wsConfig.put("SOLA_WS_CADASTRE_SERVICE_URL", "http://localhost:8080/sola/webservices/cadastre-service?wsdl");
        wsConfig.put("SOLA_WS_SEARCH_SERVICE_URL", "http://localhost:8080/sola/webservices/search-service?wsdl");
        wsConfig.put("SOLA_WS_DIGITAL_ARCHIVE_URL", "http://localhost:8080/sola/webservices/digitalarchive-service?wsdl");
        wsConfig.put("SOLA_WS_SPATIAL_SERVICE_URL", "http://localhost:8080/sola/webservices/spatial-service?wsdl");
        wsConfig.put("SOLA_WS_ADMINISTRATIVE_SERVICE_URL", "http://localhost:8080/sola/webservices/administrative-service?wsdl");
        return wsConfig;
    }
    
    private ApplicationBean getApplicationBean(String applicationId){
        ApplicationTO appTO =
                PojoDataAccess.getInstance().getWSManager().getCaseManagementService().getApplication(
                applicationId);
        
        return TypeConverters.TransferObjectToBean(appTO, ApplicationBean.class, null);        
    }
}
