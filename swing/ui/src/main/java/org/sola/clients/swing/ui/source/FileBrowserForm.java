/**
 * ******************************************************************************************
 * Copyright (C) 2011 - Food and Agriculture Organization of the United Nations (FAO).
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
package org.sola.clients.swing.ui.source;

import org.sola.clients.swing.ui.ImagePreview;
import java.awt.ComponentOrientation;
import java.awt.Desktop;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.ResourceBundle;
import javax.swing.ImageIcon;
import org.sola.clients.beans.digitalarchive.DocumentBean;
import org.sola.clients.beans.digitalarchive.FileBinaryBean;
import org.sola.clients.beans.digitalarchive.FileInfoListBean;
import org.sola.clients.swing.ui.renderers.FileNameCellRenderer;
import org.sola.services.boundary.wsclients.WSManager;
import org.sola.common.messaging.ClientMessage;
import org.sola.common.messaging.MessageUtility;
import java.util.Locale;
import javax.swing.SwingUtilities;

/**
 * This form provides browsing of local and remote folders for the scanned images.
 * Could be used to attach digital copies of documents.<br />
 * The following bean is used to bind server side files list on the form -
 * {@link FileInfoListBean}.
 */
public class FileBrowserForm extends javax.swing.JDialog {

    /** File browser action upon file attachment event.*/
    public enum AttachAction {
        CLOSE_WINDOW, SHOW_MESSAGE
    }
    
    /**
     * Property name, used to rise property change event upon attached document id change. 
     * This event is rised on attach button click.
     */
    public static final String ATTACHED_DOCUMENT = "AttachedDocumentId";
    private ResourceBundle formBundle = ResourceBundle.getBundle("org/sola/clients/swing/ui/source/Bundle");
    private AttachAction attachAction = AttachAction.CLOSE_WINDOW;

    public FileBrowserForm(java.awt.Frame parent, boolean modal, AttachAction attachAction) {
        super(parent, modal);
        initComponents();

        this.attachAction = attachAction;
        serverFiles.loadServerFileInfoList();
        serverFiles.addPropertyChangeListener(serverFilesListener());
        localFileChooser.setControlButtonsAreShown(false);
        localFileChooser.setAccessory(new ImagePreview(localFileChooser, 225, 300));
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        serverFiles = new org.sola.clients.beans.digitalarchive.FileInfoListBean();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        localFileChooser = new javax.swing.JFileChooser();
        btnOpenLocal = new javax.swing.JButton();
        btnAttachLocal = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tbServerFiles = new javax.swing.JTable();
        lblServerPreview = new javax.swing.JLabel();
        btnRefreshServerList = new javax.swing.JButton();
        btnOpenServerFile = new javax.swing.JButton();
        btnAttachFromServer = new javax.swing.JButton();
        btnDeleteServerFile = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(null);
        setMinimumSize(new java.awt.Dimension(706, 432));
        setName("Form"); // NOI18N
        setResizable(false);

        jTabbedPane1.setName("jTabbedPane1"); // NOI18N
        jTabbedPane1.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));

        jPanel1.setName("jPanel1"); // NOI18N
        jPanel1.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));

        localFileChooser.setName("localFileChooser"); // NOI18N
        localFileChooser.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
        localFileChooser.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                localFileChooserMouseClicked(evt);
            }
        });

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/sola/clients/swing/ui/source/Bundle"); // NOI18N
        btnOpenLocal.setText(bundle.getString("FileBrowserForm.btnOpenLocal.text")); // NOI18N
        btnOpenLocal.setFocusable(false);
        btnOpenLocal.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnOpenLocal.setName("btnOpenLocal"); // NOI18N
        btnOpenLocal.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnOpenLocal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOpenLocalActionPerformed(evt);
            }
        });

        btnAttachLocal.setText(bundle.getString("FileBrowserForm.btnAttachLocal.text")); // NOI18N
        btnAttachLocal.setFocusable(false);
        btnAttachLocal.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnAttachLocal.setName("btnAttachLocal"); // NOI18N
        btnAttachLocal.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnAttachLocal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAttachLocalActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(480, Short.MAX_VALUE)
                .addComponent(btnOpenLocal, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnAttachLocal, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addComponent(localFileChooser, javax.swing.GroupLayout.DEFAULT_SIZE, 681, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(localFileChooser, javax.swing.GroupLayout.PREFERRED_SIZE, 342, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnOpenLocal)
                    .addComponent(btnAttachLocal))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab(bundle.getString("FileBrowserForm.jPanel1.TabConstraints.tabTitle"), jPanel1); // NOI18N

        jPanel2.setName("jPanel2"); // NOI18N
        jPanel2.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));

        jScrollPane1.setBackground(new java.awt.Color(255, 255, 255));
        jScrollPane1.setName("jScrollPane1"); // NOI18N
        jScrollPane1.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));

        tbServerFiles.setAutoCreateRowSorter(true);
        tbServerFiles.setName("tbServerFiles"); // NOI18N
        tbServerFiles.setSelectionBackground(new java.awt.Color(185, 227, 185));
        tbServerFiles.setSelectionForeground(new java.awt.Color(0, 102, 51));
        tbServerFiles.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));

        org.jdesktop.beansbinding.ELProperty eLProperty = org.jdesktop.beansbinding.ELProperty.create("${fileInfoList}");
        org.jdesktop.swingbinding.JTableBinding jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, serverFiles, eLProperty, tbServerFiles);
        org.jdesktop.swingbinding.JTableBinding.ColumnBinding columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${name}"));
        columnBinding.setColumnName("Name");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${modificationDate}"));
        columnBinding.setColumnName("Modification Date");
        columnBinding.setColumnClass(java.util.Date.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${formattedFileSize}"));
        columnBinding.setColumnName("Formatted File Size");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        bindingGroup.addBinding(jTableBinding);
        jTableBinding.bind();org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, serverFiles, org.jdesktop.beansbinding.ELProperty.create("${selectedFileInfoBean}"), tbServerFiles, org.jdesktop.beansbinding.BeanProperty.create("selectedElement"));
        bindingGroup.addBinding(binding);

        tbServerFiles.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbServerFilesMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tbServerFiles);
        tbServerFiles.getColumnModel().getColumn(0).setHeaderValue(bundle.getString("FileBrowserForm.tbServerFiles.columnModel.title0")); // NOI18N
        tbServerFiles.getColumnModel().getColumn(0).setCellRenderer(new FileNameCellRenderer());
        tbServerFiles.getColumnModel().getColumn(1).setHeaderValue(bundle.getString("FileBrowserForm.tbServerFiles.columnModel.title1")); // NOI18N
        tbServerFiles.getColumnModel().getColumn(2).setHeaderValue(bundle.getString("FileBrowserForm.tbServerFiles.columnModel.title2")); // NOI18N

        lblServerPreview.setBackground(new java.awt.Color(255, 255, 255));
        lblServerPreview.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblServerPreview.setText(bundle.getString("FileBrowserForm.lblServerPreview.text")); // NOI18N
        lblServerPreview.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 153, 153), 1, true));
        lblServerPreview.setName("lblServerPreview"); // NOI18N
        lblServerPreview.setOpaque(true);

        btnRefreshServerList.setText(bundle.getString("FileBrowserForm.btnRefreshServerList.text")); // NOI18N
        btnRefreshServerList.setFocusable(false);
        btnRefreshServerList.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnRefreshServerList.setName("btnRefreshServerList"); // NOI18N
        btnRefreshServerList.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnRefreshServerList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshServerListActionPerformed(evt);
            }
        });

        btnOpenServerFile.setText(bundle.getString("FileBrowserForm.btnOpenServerFile.text")); // NOI18N
        btnOpenServerFile.setFocusable(false);
        btnOpenServerFile.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnOpenServerFile.setName("btnOpenServerFile"); // NOI18N
        btnOpenServerFile.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnOpenServerFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOpenServerFileActionPerformed(evt);
            }
        });

        btnAttachFromServer.setText(bundle.getString("FileBrowserForm.btnAttachFromServer.text")); // NOI18N
        btnAttachFromServer.setFocusable(false);
        btnAttachFromServer.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnAttachFromServer.setName("btnAttachFromServer"); // NOI18N
        btnAttachFromServer.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnAttachFromServer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAttachFromServerActionPerformed(evt);
            }
        });

        btnDeleteServerFile.setText(bundle.getString("FileBrowserForm.btnDeleteServerFile.text")); // NOI18N
        btnDeleteServerFile.setFocusable(false);
        btnDeleteServerFile.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnDeleteServerFile.setName("btnDeleteServerFile"); // NOI18N
        btnDeleteServerFile.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnDeleteServerFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteServerFileActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 419, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(btnAttachFromServer, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnRefreshServerList, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnOpenServerFile, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnDeleteServerFile, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(17, 17, 17)
                .addComponent(lblServerPreview, javax.swing.GroupLayout.PREFERRED_SIZE, 225, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnAttachFromServer, btnDeleteServerFile, btnOpenServerFile, btnRefreshServerList});

        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(lblServerPreview, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 322, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 322, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnOpenServerFile)
                    .addComponent(btnRefreshServerList)
                    .addComponent(btnAttachFromServer)
                    .addComponent(btnDeleteServerFile))
                .addGap(15, 15, 15))
        );

        jTabbedPane1.addTab(bundle.getString("FileBrowserForm.jPanel2.TabConstraints.tabTitle"), jPanel2); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 686, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 410, Short.MAX_VALUE)
                .addContainerGap())
        );

        bindingGroup.bind();

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnRefreshServerListActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshServerListActionPerformed
        serverFiles.loadServerFileInfoList();
    }//GEN-LAST:event_btnRefreshServerListActionPerformed

    private void btnOpenServerFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOpenServerFileActionPerformed
        if (serverFiles.getSelectedFileInfoBean() == null) {
            MessageUtility.displayMessage(ClientMessage.ARCHIVE_SELECT_FILE_TO_OPEN);
        } else {
            FileBinaryBean.openFile(serverFiles.getSelectedFileInfoBean().getName());
        }
    }//GEN-LAST:event_btnOpenServerFileActionPerformed

    /**
     * Property change listener for the {@link FileInfoListBean} to trap selected 
     * file change in the list of scanned files in the remote folder.
     */
    private PropertyChangeListener serverFilesListener() {
        PropertyChangeListener listener = new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent e) {
                if (e.getPropertyName().equals(FileInfoListBean.SELECTED_FILE_INFO_BEAN_PROPERTY)) {
                    // Bind thumbnail
                    if (serverFiles.getSelectedFileInfoBean() != null) {

                        lblServerPreview.setIcon(null);
                        lblServerPreview.setText(formBundle.getString("FileBrowser.LoadingThumbnailMsg"));
                        lblServerPreview.repaint();

                        Runnable loadingThumbnail = new Runnable() {

                            @Override
                            public void run() {
                                ImageIcon thumbnail = serverFiles.getSelectedFileInfoBean().getThumbnailIcon();
                                if (thumbnail != null) {
                                    lblServerPreview.setIcon(thumbnail);
                                    lblServerPreview.setText(null);
                                } else {
                                    lblServerPreview.setIcon(null);
                                    lblServerPreview.setText(formBundle.getString("FileBrowser.FormatForThumbnailNotSupportedMsg"));
                                }
                            }
                        };
                        SwingUtilities.invokeLater(loadingThumbnail);

                    } else {
                        lblServerPreview.setIcon(null);
                        lblServerPreview.setText(formBundle.getString("FileBrowser.ThumbnailPreviewCaption"));
                    }
                }
            }
        };
        return listener;
    }

    private void tbServerFilesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbServerFilesMouseClicked
        if (evt.getClickCount() == 2 && serverFiles.getSelectedFileInfoBean() != null) {
            FileBinaryBean.openFile(serverFiles.getSelectedFileInfoBean().getName());
        }
    }//GEN-LAST:event_tbServerFilesMouseClicked

    /** 
     * Uploads selected file into the digital archive from remote folder, 
     * gets {@link DocumentBean} of uploaded file and calls method 
     * {@link #fireAttachEvent(DocumentBean)} to rise attachment event.
     */
    private void btnAttachFromServerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAttachFromServerActionPerformed
        if (serverFiles.getSelectedFileInfoBean() == null) {
            MessageUtility.displayMessage(ClientMessage.ARCHIVE_SELECT_FILE_TO_ATTACH);
        } else {
            fireAttachEvent(DocumentBean.createDocumentFromServerFile(
                    serverFiles.getSelectedFileInfoBean().getName()));
        }
    }//GEN-LAST:event_btnAttachFromServerActionPerformed

    /** 
     * Checks uploaded document bean, rises {@link #ATTACHED_DOCUMENT} property 
     * change event. Closes the window or displays the message, depending on 
     * the {@link AttachAction} value, passed to the form constructor.
     */
    private void fireAttachEvent(DocumentBean documentBean) {
        if (documentBean == null) {
            MessageUtility.displayMessage(ClientMessage.ARCHIVE_FAILED_TO_ATTACH_FILE,
                    new Object[]{serverFiles.getSelectedFileInfoBean().getName()});
        } else {
            this.firePropertyChange(ATTACHED_DOCUMENT, null, documentBean);
            
            if(attachAction == AttachAction.CLOSE_WINDOW){
                this.dispose();
            }
            
            if(attachAction == AttachAction.SHOW_MESSAGE){
                MessageUtility.displayMessage(ClientMessage.ARCHIVE_FILE_ADDED,
                    new Object[]{documentBean.getNr()});
            }
        }
    }

    /** Deletes selected file from the remote folder with scanned images. */
    private void btnDeleteServerFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteServerFileActionPerformed
        if (serverFiles.getSelectedFileInfoBean() != null) {
            if (MessageUtility.displayMessage(ClientMessage.ARCHIVE_CONFIRM_FILE_DELETION)
                    == MessageUtility.BUTTON_ONE) {
                if (WSManager.getInstance().getDigitalArchive().deleteFile(
                        serverFiles.getSelectedFileInfoBean().getName())) {

                    MessageUtility.displayMessage(ClientMessage.ARCHIVE_FILE_DELETED);
                    serverFiles.loadServerFileInfoList();
                }else{
                    MessageUtility.displayMessage(ClientMessage.ARCHIVE_FAILED_DELETE_FILE);
                }

            }
        }
    }//GEN-LAST:event_btnDeleteServerFileActionPerformed

    /** Opens selected file from the local drive, by double click. */
    private void localFileChooserMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_localFileChooserMouseClicked
        if (evt.getClickCount() == 2) {
            try {
                Desktop dt = Desktop.getDesktop();
                dt.open(localFileChooser.getSelectedFile());
            } catch (IOException ex) {
                MessageUtility.displayMessage(ClientMessage.ERR_FAILED_OPEN_FILE,
                        new Object[]{localFileChooser.getSelectedFile().getName()});
            }
        }
    }//GEN-LAST:event_localFileChooserMouseClicked

    /** Opens selected file from the local drive, by click on the open button. */
    private void btnOpenLocalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOpenLocalActionPerformed
        try {
            Desktop dt = Desktop.getDesktop();
            dt.open(localFileChooser.getSelectedFile());
        } catch (IOException ex) {
            MessageUtility.displayMessage(ClientMessage.ERR_FAILED_OPEN_FILE,
                    new Object[]{localFileChooser.getSelectedFile().getName()});
        }
    }//GEN-LAST:event_btnOpenLocalActionPerformed

    /** 
     * Uploads selected file into the digital archive from local drive, 
     * gets {@link DocumentBean} of uploaded file and calls method 
     * {@link #fireAttachEvent(DocumentBean)} to rise attachment event.
     */
    private void btnAttachLocalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAttachLocalActionPerformed
        File selectedFile = localFileChooser.getSelectedFile();

        if (selectedFile != null) {
            fireAttachEvent(DocumentBean.createDocumentFromLocalFile(
                    localFileChooser.getSelectedFile()));
        }

    }//GEN-LAST:event_btnAttachLocalActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAttachFromServer;
    private javax.swing.JButton btnAttachLocal;
    private javax.swing.JButton btnDeleteServerFile;
    private javax.swing.JButton btnOpenLocal;
    private javax.swing.JButton btnOpenServerFile;
    private javax.swing.JButton btnRefreshServerList;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel lblServerPreview;
    private javax.swing.JFileChooser localFileChooser;
    private org.sola.clients.beans.digitalarchive.FileInfoListBean serverFiles;
    private javax.swing.JTable tbServerFiles;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
}
