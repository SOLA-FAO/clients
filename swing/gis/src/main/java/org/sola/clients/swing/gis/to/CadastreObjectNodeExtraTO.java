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
 *
 * @author Elton Manoku
 */
public class CadastreObjectNodeExtraTO extends CadastreObjectNodeTO{

    public CadastreObjectNodeExtraTO(CadastreObjectNodeTO cadastreObjectNodeTO){
        MappingManager.getMapper().map(cadastreObjectNodeTO, this);
    }
    
    public void setCadastreObjectList(List<CadastreObjectTO> cadastreObjectList) {
        this.cadastreObjectList = cadastreObjectList;
    }
}
