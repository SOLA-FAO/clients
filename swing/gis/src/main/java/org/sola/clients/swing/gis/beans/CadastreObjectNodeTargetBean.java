/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sola.clients.swing.gis.beans;

import java.io.Serializable;

/**
 * A data bean which represents a node target during the Redefine cadastre process.
 * 
 * @author Elton Manoku
 */
public class CadastreObjectNodeTargetBean implements Serializable {
    private String nodeId;
    private byte[] geom;

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public byte[] getGeom() {
        return geom;
    }

    public void setGeom(byte[] geom) {
        this.geom = geom.clone();
    }
}
