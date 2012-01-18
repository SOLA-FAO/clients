/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sola.clients.swing.gis.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Elton Manoku
 */
public class CadastreObjectNodeBean implements Serializable {
    private String id;
    private byte[] geom;
    List<CadastreObjectBean> cadastreObjectList = new ArrayList<CadastreObjectBean>();
    
    public CadastreObjectNodeBean(){
    }

    public CadastreObjectNodeBean(String id){
        this.id = id;
    }

    public List<CadastreObjectBean> getCadastreObjectList() {
        return cadastreObjectList;
    }

    public void setCadastreObjectList(List<CadastreObjectBean> cadastreObjectList) {
        this.cadastreObjectList = cadastreObjectList;
    }

    public byte[] getGeom() {
        return geom;
    }

    public void setGeom(byte[] geom) {
        this.geom = geom;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object obj) {
        if (!obj.getClass().equals(this.getClass())){
            return false;
        }
        return this.getId().equals(((CadastreObjectNodeBean)obj).getId());
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 79 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }
    
     
}
