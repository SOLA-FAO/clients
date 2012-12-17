/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sola.clients.swing.bulkoperations.beans;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;
import org.sola.clients.beans.source.SourceBean;

/**
 *
 * @author Elton Manoku
 */
public class SourceBulkMoveBean {

    private File baseFolder;
    private List<SourceBean> sourceList = null;
    private List<String> allowedExtensions = new ArrayList<String>();

    public SourceBulkMoveBean() {
        allowedExtensions.add(".tiff");
        allowedExtensions.add(".pdf");
        allowedExtensions.add(".jpg");
    }

    
    
    public File getBaseFolder() {
        return baseFolder;
    }

    public void setBaseFolder(File baseFolder) {
        this.baseFolder = baseFolder;
    }

    public List<String> getAllowedExtensions() {
        return allowedExtensions;
    }
    
    private FileFilter getSourceFileFilter(){
        return new FileFilter() {

            @Override
            public boolean accept(File pathname) {
                if (pathname.isDirectory()){
                    return false;
                }
                for(String allowedExtension: getAllowedExtensions()){
                    if (pathname.getName().toLowerCase().endsWith(allowedExtension)){
                        return true;
                    }
                }
                return false;
            }
        };
    }

    private List<SourceBean> getSources() {
        if (sourceList == null) {
            sourceList = new ArrayList<SourceBean>();
            for (File subFolderObj : getBaseFolder().listFiles()) {
                if (!subFolderObj.isDirectory()) {
                    continue;
                }
                treatSourceTypeFolder(subFolderObj);
            }
        }
        return sourceList;
    }

    private void treatSourceTypeFolder(File sourceTypeFolder) {
        String sourceType = sourceTypeFolder.getName();
        for (File sourceFile : sourceTypeFolder.listFiles(getSourceFileFilter())) {
            SourceBean sourceBean = new SourceBean();
            sourceBean.setTypeCode(sourceType);
            sourceBean.setReferenceNr(getReferenceNrFromFileName(sourceFile.getName()));
            sourceList.add(sourceBean);
        }
    }

    private String getReferenceNrFromFileName(String fileName){
         return fileName.split(".")[0];
    }
    
    public void sendToServer() {
        List<SourceBean> sourceBeanList = getSources();
    }
}
