/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sola.clients.swing.bulkoperations;

import java.awt.Component;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Common control that is used to open files given a filter.
 * 
 * @author Elton Manoku
 */
public class FileBrowser extends JFileChooser {
    
    public FileBrowser(String ... extensions){
        super();
        this.setAcceptAllFileFilterUsed(false);
        this.setFileFilter(getFilenameFilter(extensions));
    }
    
    public static File showFileChooser(
            Component parentComponent, String ... extensions){
        FileBrowser fileBrowser = new FileBrowser(extensions);
        if(fileBrowser.showOpenDialog(parentComponent) ==  APPROVE_OPTION){
            return fileBrowser.getSelectedFile();
        };
        return null;
    }
    
    private FileFilter getFilenameFilter(String ... extensions) {
        String filterDescription = "";
        for(String ext:extensions){
            if (!filterDescription.equals("")){
                filterDescription += ", ";
            }
            filterDescription += "*." + ext;
        }
        FileFilter fileFilter = new FileNameExtensionFilter(
                filterDescription, extensions);
        return fileFilter;
    }
    
}
