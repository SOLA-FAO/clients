/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sola.clients.swing.gis.beans;

/**
 * It represents a cadastre object during the process of redefinition of the cadastre process.
 * This bean holds the current and changed geometry.
 * 
 * @author Elton Manoku
 */
public class CadastreObjectTargetRedefinitionBean extends CadastreObjectTargetBean {
    
    private byte[] geomPolygon;
    private byte[] geomPolygonCurrent;

    public byte[] getGeomPolygon() {
        return geomPolygon;
    }

    public void setGeomPolygon(byte[] geomPolygon) {
        this.geomPolygon = geomPolygon.clone();
    }

    public byte[] getGeomPolygonCurrent() {
        return geomPolygonCurrent;
    }

    public void setGeomPolygonCurrent(byte[] geomPolygonCurrent) {
        this.geomPolygonCurrent = geomPolygonCurrent.clone();
    }
}
