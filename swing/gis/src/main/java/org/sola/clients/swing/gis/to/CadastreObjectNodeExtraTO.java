/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sola.clients.swing.gis.to;

import java.util.List;
import org.sola.common.MappingManager;
import org.sola.webservices.transferobjects.cadastre.CadastreObjectNodeTO;
import org.sola.webservices.transferobjects.cadastre.CadastreObjectTO;

/**
 * It extends the TO which is generated from the web service with the set method
 * which disappears during the auto generation from the representation of the TO object in 
 * the server. This method is needed in order to use the generic mapper to translate from 
 * data bean to TO and viceversa.
 * 
 * @author Elton Manoku
 */
public class CadastreObjectNodeExtraTO extends CadastreObjectNodeTO{

    /**
     * Constructor which gets as input the original TO
     * 
     * @param cadastreObjectNodeTO 
     */
    public CadastreObjectNodeExtraTO(CadastreObjectNodeTO cadastreObjectNodeTO){
        MappingManager.getMapper().map(cadastreObjectNodeTO, this);
    }
    
    public void setCadastreObjectList(List<CadastreObjectTO> cadastreObjectList) {
        this.cadastreObjectList = cadastreObjectList;
    }
}
