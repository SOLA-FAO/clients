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
package org.sola.clients.swing.ui.source;

import java.awt.ComponentOrientation;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;
import org.sola.clients.beans.digitalarchive.DocumentBean;
import org.sola.clients.beans.digitalarchive.FileBinaryBean;
import org.sola.clients.beans.digitalarchive.FileInfoListBean;
import org.sola.clients.swing.common.tasks.SolaTask;
import org.sola.clients.swing.common.tasks.TaskManager;
import org.sola.clients.swing.ui.ImagePreview;
import org.sola.clients.swing.ui.renderers.DateTimeRenderer;
import org.sola.clients.swing.ui.renderers.FileNameCellRenderer;
import org.sola.common.FileUtility;
import org.sola.common.WindowUtility;
import org.sola.common.messaging.ClientMessage;
import org.sola.common.messaging.MessageUtility;
import org.sola.services.boundary.wsclients.WSManager;

/**
 * This form provides browsing of local and remote folders for the scanned
 * images. Could be used to attach digital copies of documents.<br /> The
 * following bean is used to bind server side files list on the form -
 * {@link FileInfoListBean}.
 */
public class FileBrowserForm extends javax.swing.JDialog {

    private SolaTask generateThumbTask = null;

    // Custom file filter to do a quick search in the current local directory
    private class FilesFilter extends FileFilter {

        private String filterString = "";

        //Filters files with a given filter and allows all directories.
        @Override
        public boolean accept(File f) {
            if (f.isDirectory() || getFilterString() == null || getFilterString().equals("")) {
                return true;
            }
            return FileUtility.getFileNameWithoutExtension(f.getName())
                    .toLowerCase().contains(getFilterString().toLowerCase());
        }

        //The description of this filter
        @Override
        public String getDescription() {
            return "Files filter";
        }

        public String getFilterString() {
            return filterString;
        }

        public void setFilterString(String filterString) {
            if (filterString == null) {
                this.filterString = "";
            } else {
                this.filterString = filterString;
            }
        }
    }
    private FilesFilter userFilter;
    private static String USER_PREFERRED_DIR = "userPreferredDir";

    /**
     * File browser action upon file attachment event.
     */
    public enum AttachAction {

        CLOSE_WINDOW, SHOW_MESSAGE
    }
    /**
     * Property name, used to rise property change event upon attached document
     * id change. This event is raised on attach button click.
     */
    public static final String ATTACHED_DOCUMENT = "AttachedDocumentId";
    private ResourceBundle formBundle = ResourceBundle.getBundle("org/sola/clients/swing/ui/source/Bundle");
    private AttachAction attachAction = AttachAction.CLOSE_WINDOW;

    public FileBrowserForm(java.awt.Frame parent, boolean modal, AttachAction attachAction) {
        super(parent, modal);
        this.attachAction = attachAction;
        initComponents();
        postInit();
    }

    private void postInit() {
        userFilter = new FilesFilter();
        serverFiles.loadServerFileInfoList();
        serverFiles.addPropertyChangeListener(serverFilesListener());

        localFileChooser.setAcceptAllFileFilterUsed(false);
        localFileChooser.setFileFilter(userFilter);
        localFileChooser.setControlButtonsAreShown(false);
        localFileChooser.setAccessory(new ImagePreview(localFileChooser));

        if (WindowUtility.hasUserPreferences()) {
            // Check if the user has a preferred location to upload files from
            Preferences prefs = WindowUtility.getUserPreferences();
            String preferredDir = prefs.get(USER_PREFERRED_DIR, null);
            if (preferredDir != null) {
                localFileChooser.setCurrentDirectory(new File(preferredDir));
            }
        }
		
		WindowUtility.addEscapeListener(this, false);
        this.setIconImage(WindowUtility.getTitleBarImage());
        customizeRemoteFileButtons();
    }

    /**
     * Property change listener for the {@link FileInfoListBean} to trap
     * selected file change in the list of scanned files in the remote folder.
     */
    private PropertyChangeListener serverFilesListener() {
        PropertyChangeListener listener = new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent e) {
                if (e.getPropertyName().equals(FileInfoListBean.SELECTED_FILE_INFO_BEAN_PROPERTY)) {
                    customizeRemoteFileButtons();
                    loadServerPreview();
                }
            }
        };
        return listener;
    }

    /**
     * Loads a thumbnail of the selected file from the Network Scan Folder
     */
    private void loadServerPreview() {
        // Bind thumbnail
        if (serverFiles.getSelectedFileInfoBean() != null) {

            // Check if a thumbnail is currently being generated and if so, cancel it
            if (generateThumbTask != null && TaskManager.getInstance().isTaskRunning(generateThumbTask.getId())) {
                TaskManager.getInstance().removeTask(generateThumbTask);
            }

            final ImageIcon thumbnail[] = {null};

            lblServerPreview.setIcon(null);
            lblServerPreview.setText(formBundle.getString("FileBrowser.LoadingThumbnailMsg"));
            lblServerPreview.repaint();

            generateThumbTask = new SolaTask<Void, Void>() {
                @Override
                public Void doTask() {
                    setMessage(MessageUtility.getLocalizedMessageText(ClientMessage.PROGRESS_MSG_GENERATE_THUMBNAIL));
                    try {
                        // Allow a small delay on the background thread so that the thread can be cancelled
                        // before executing the generate if the user is still resizing the form. 
                        Thread.sleep(200);
                        thumbnail[0] = serverFiles.getSelectedFileInfoBean().getThumbnailIcon(
                                lblServerPreview.getWidth(), lblServerPreview.getHeight());
                    } catch (InterruptedException ex) {
                    }
                    return null;
                }

                @Override
                public void taskDone() {
                    // Display the thumbnail
                    if (thumbnail[0] != null) {
                        lblServerPreview.setIcon(thumbnail[0]);
                        lblServerPreview.setText(null);
                    } else {
                        lblServerPreview.setIcon(null);
                        lblServerPreview.setText(formBundle.getString("FileBrowser.FormatForThumbnailNotSupportedMsg"));
                    }

                }
            };
            TaskManager.getInstance().runTask(generateThumbTask);

        } else {
            lblServerPreview.setIcon(null);
            lblServerPreview.setText(formBundle.getString("FileBrowser.ThumbnailPreviewCaption"));
        }
    }

    private void customizeRemoteFileButtons() {
        boolean enabled = serverFiles.getSelectedFileInfoBean() != null;
        btnAttachFromServer.setEnabled(enabled);
        btnDeleteServerFile.setEnabled(enabled);
        btnOpenServerFile.setEnabled(enabled);
        menuRemoteAttach.setEnabled(btnAttachFromServer.isEnabled());
        menuRemoteDelete.setEnabled(btnDeleteServerFile.isEnabled());
        menuRemoteOpen.setEnabled(btnOpenServerFile.isEnabled());
    }

    private void refreshRemoteFiles() {
        SolaTask t = new SolaTask<Void, Void>() {
            @Override
            public Void doTask() {
                setMessage(MessageUtility.getLocalizedMessageText(ClientMessage.PROGRESS_MSG_DOCUMENT_GETTING_LIST));
                serverFiles.loadServerFileInfoList();
                return null;
            }
        };
        TaskManager.getInstance().runTask(t);
    }

    private void openRemoteFile() {
        if (serverFiles.getSelectedFileInfoBean() != null) {
            SolaTask t = new SolaTask<Void, Void>() {
                @Override
                public Void doTask() {
                    setMessage(MessageUtility.getLocalizedMessageText(ClientMessage.PROGRESS_MSG_DOCUMENT_OPENING));
                    FileBinaryBean.openFile(serverFiles.getSelectedFileInfoBean().getName());
                    return null;
                }
            };
            TaskManager.getInstance().runTask(t);
        }
    }

    /**
     * Opens a file from the local file system using
     * {@linkplain FileUtility#openFile(java.io.File)}
     */
    private void openLocalFile() {
        SolaTask t = new SolaTask<Void, Void>() {
            @Override
            public Void doTask() {
                setMessage(MessageUtility.getLocalizedMessageText(ClientMessage.PROGRESS_MSG_DOCUMENT_OPENING));
                FileUtility.openFile(localFileChooser.getSelectedFile());
                return null;
            }
        };
        TaskManager.getInstance().runTask(t);
    }

    /**
     * Uploads selected file into the digital archive from remote folder, gets
     * {@link DocumentBean} of uploaded file and calls method
     * {@link #fireAttachEvent(DocumentBean)} to rise attachment event.
     */
    private void attachRemoteFile() {
        if (serverFiles.getSelectedFileInfoBean() != null) {
            fireAttachEvent(DocumentBean.createDocumentFromServerFile(
                    serverFiles.getSelectedFileInfoBean().getName()));
        }
    }

    /**
     * Checks uploaded document bean, rises {@link #ATTACHED_DOCUMENT} property
     * change event. Closes the window or displays the message, depending on the
     * {@link AttachAction} value, passed to the form constructor.
     */
    private void fireAttachEvent(DocumentBean documentBean) {
        if (documentBean == null) {
            MessageUtility.displayMessage(ClientMessage.ARCHIVE_FAILED_TO_ATTACH_FILE,
                    new Object[]{serverFiles.getSelectedFileInfoBean().getName()});
        } else {
            this.firePropertyChange(ATTACHED_DOCUMENT, null, documentBean);

            if (attachAction == AttachAction.CLOSE_WINDOW) {
                this.dispose();
            }

            if (attachAction == AttachAction.SHOW_MESSAGE) {
                MessageUtility.displayMessage(ClientMessage.ARCHIVE_FILE_ADDED,
                        new Object[]{documentBean.getNr()});
            }
        }
    }

    /**
     * Deletes selected file from the remote folder with scanned images.
     */
    private void deleteRemoteFile() {
        if (serverFiles.getSelectedFileInfoBean() != null) {
            if (MessageUtility.displayMessage(ClientMessage.ARCHIVE_CONFIRM_FILE_DELETION)
                    == MessageUtility.BUTTON_ONE) {
                if (WSManager.getInstance().getDigitalArchive().deleteFile(
                        serverFiles.getSelectedFileInfoBean().getName())) {

                    MessageUtility.displayMessage(ClientMessage.ARCHIVE_FILE_DELETED);
                    serverFiles.loadServerFileInfoList();
                } else {
                    MessageUtility.displayMessage(ClientMessage.ARCHIVE_FAILED_DELETE_FILE);
                }

            }
        }
    }

    private void attachLocalFile() {
        File selectedFile = localFileChooser.getSelectedFile();
        if (selectedFile != null) {
            SolaTask<Void, Void> task = new SolaTask<Void, Void>() {
                DocumentBean document = null;

                @Override
                protected Void doTask() {
                    setMessage(MessageUtility.getLocalizedMessageText(ClientMessage.SOURCE_LOAD_DOC_ON_SERVER));
                    document = DocumentBean.createDocumentFromLocalFile(
                            localFileChooser.getSelectedFile());
                    return null;
                }

                @Override
                protected void taskDone() {
                    if (WindowUtility.hasUserPreferences()) {
                        Preferences prefs = WindowUtility.getUserPreferences();
                        prefs.put(USER_PREFERRED_DIR, localFileChooser.getSelectedFile().getAbsolutePath());
                    }
                    fireAttachEvent(document);
                }
            };
            TaskManager.getInstance().runTask(task);
        }
    }

    private void filterFiles() {
        userFilter.setFilterString(txtFilter.getText());
        localFileChooser.getUI().rescanCurrentDirectory(localFileChooser);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        serverFiles = new org.sola.clients.beans.digitalarchive.FileInfoListBean();
        popupRemoteFiles = new javax.swing.JPopupMenu();
        menuRemoteRefresh = new javax.swing.JMenuItem();
        menuRemoteOpen = new javax.swing.JMenuItem();
        menuRemoteDelete = new javax.swing.JMenuItem();
        menuRemoteAttach = new javax.swing.JMenuItem();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        localFileChooser = new javax.swing.JFileChooser();
        jToolBar2 = new javax.swing.JToolBar();
        btnOpenLocal = new javax.swing.JButton();
        btnAttachLocal = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        jLabel1 = new javax.swing.JLabel();
        txtFilter = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tbServerFiles = new javax.swing.JTable();
        lblServerPreview = new javax.swing.JLabel();
        jToolBar1 = new javax.swing.JToolBar();
        btnRefreshServerList = new javax.swing.JButton();
        btnOpenServerFile = new javax.swing.JButton();
        btnDeleteServerFile = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        btnAttachFromServer = new javax.swing.JButton();
        taskPanel1 = new org.sola.clients.swing.common.tasks.TaskPanel();

        popupRemoteFiles.setName("popupRemoteFiles"); // NOI18N

        menuRemoteRefresh.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/refresh.png"))); // NOI18N
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/sola/clients/swing/ui/source/Bundle"); // NOI18N
        menuRemoteRefresh.setText(bundle.getString("FileBrowserForm.menuRemoteRefresh.text")); // NOI18N
        menuRemoteRefresh.setName("menuRemoteRefresh"); // NOI18N
        menuRemoteRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuRemoteRefreshActionPerformed(evt);
            }
        });
        popupRemoteFiles.add(menuRemoteRefresh);

        menuRemoteOpen.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/folder-open-document.png"))); // NOI18N
        menuRemoteOpen.setText(bundle.getString("FileBrowserForm.menuRemoteOpen.text")); // NOI18N
        menuRemoteOpen.setName("menuRemoteOpen"); // NOI18N
        menuRemoteOpen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuRemoteOpenActionPerformed(evt);
            }
        });
        popupRemoteFiles.add(menuRemoteOpen);

        menuRemoteDelete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/remove.png"))); // NOI18N
        menuRemoteDelete.setText(bundle.getString("FileBrowserForm.menuRemoteDelete.text")); // NOI18N
        menuRemoteDelete.setName("menuRemoteDelete"); // NOI18N
        menuRemoteDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuRemoteDeleteActionPerformed(evt);
            }
        });
        popupRemoteFiles.add(menuRemoteDelete);

        menuRemoteAttach.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/attachment.png"))); // NOI18N
        menuRemoteAttach.setText(bundle.getString("FileBrowserForm.menuRemoteAttach.text")); // NOI18N
        menuRemoteAttach.setName("menuRemoteAttach"); // NOI18N
        menuRemoteAttach.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuRemoteAttachActionPerformed(evt);
            }
        });
        popupRemoteFiles.add(menuRemoteAttach);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(bundle.getString("FileBrowserForm.title_1")); // NOI18N
        setMinimumSize(new java.awt.Dimension(706, 432));
        setName("Form"); // NOI18N
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                formComponentResized(evt);
            }
        });

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

        jToolBar2.setFloatable(false);
        jToolBar2.setRollover(true);
        jToolBar2.setName("jToolBar2"); // NOI18N
        jToolBar2.setPreferredSize(new java.awt.Dimension(213, 28));

        btnOpenLocal.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/folder-open-document.png"))); // NOI18N
        btnOpenLocal.setText(bundle.getString("FileBrowserForm.btnOpenLocal.text")); // NOI18N
        btnOpenLocal.setFocusable(false);
        btnOpenLocal.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnOpenLocal.setName("btnOpenLocal"); // NOI18N
        btnOpenLocal.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnOpenLocal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOpenLocalActionPerformed(evt);
            }
        });
        jToolBar2.add(btnOpenLocal);

        btnAttachLocal.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/attachment.png"))); // NOI18N
        btnAttachLocal.setText(bundle.getString("FileBrowserForm.btnAttachLocal.text")); // NOI18N
        btnAttachLocal.setFocusable(false);
        btnAttachLocal.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnAttachLocal.setName("btnAttachLocal"); // NOI18N
        btnAttachLocal.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnAttachLocal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAttachLocalActionPerformed(evt);
            }
        });
        jToolBar2.add(btnAttachLocal);

        jSeparator2.setName(bundle.getString("FileBrowserForm.jSeparator2.name")); // NOI18N
        jToolBar2.add(jSeparator2);

        jLabel1.setText(bundle.getString("FileBrowserForm.jLabel1.text")); // NOI18N
        jLabel1.setName(bundle.getString("FileBrowserForm.jLabel1.name")); // NOI18N
        jToolBar2.add(jLabel1);

        txtFilter.setText(bundle.getString("FileBrowserForm.txtFilter.text")); // NOI18N
        txtFilter.setMaximumSize(new java.awt.Dimension(170, 2147483647));
        txtFilter.setMinimumSize(new java.awt.Dimension(170, 20));
        txtFilter.setName(bundle.getString("FileBrowserForm.txtFilter.name")); // NOI18N
        txtFilter.setPreferredSize(new java.awt.Dimension(170, 30));
        txtFilter.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtFilterKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtFilterKeyReleased(evt);
            }
        });
        jToolBar2.add(txtFilter);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar2, javax.swing.GroupLayout.DEFAULT_SIZE, 939, Short.MAX_VALUE)
            .addComponent(localFileChooser, javax.swing.GroupLayout.DEFAULT_SIZE, 939, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(9, 9, 9)
                .addComponent(jToolBar2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(localFileChooser, javax.swing.GroupLayout.DEFAULT_SIZE, 519, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab(bundle.getString("FileBrowserForm.jPanel1.TabConstraints.tabTitle"), jPanel1); // NOI18N

        jPanel2.setName("jPanel2"); // NOI18N
        jPanel2.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));

        jScrollPane1.setBackground(new java.awt.Color(255, 255, 255));
        jScrollPane1.setName("jScrollPane1"); // NOI18N
        jScrollPane1.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));

        tbServerFiles.setAutoCreateRowSorter(true);
        tbServerFiles.setComponentPopupMenu(popupRemoteFiles);
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
        tbServerFiles.getColumnModel().getColumn(1).setCellRenderer(new DateTimeRenderer());
        tbServerFiles.getColumnModel().getColumn(2).setHeaderValue(bundle.getString("FileBrowserForm.tbServerFiles.columnModel.title2")); // NOI18N

        lblServerPreview.setBackground(new java.awt.Color(255, 255, 255));
        lblServerPreview.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblServerPreview.setText(bundle.getString("FileBrowserForm.lblServerPreview.text")); // NOI18N
        lblServerPreview.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 153, 153), 1, true));
        lblServerPreview.setName("lblServerPreview"); // NOI18N
        lblServerPreview.setOpaque(true);

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);
        jToolBar1.setName("jToolBar1"); // NOI18N

        btnRefreshServerList.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/refresh.png"))); // NOI18N
        btnRefreshServerList.setText(bundle.getString("FileBrowserForm.btnRefreshServerList.text")); // NOI18N
        btnRefreshServerList.setFocusable(false);
        btnRefreshServerList.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnRefreshServerList.setName("btnRefreshServerList"); // NOI18N
        btnRefreshServerList.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnRefreshServerList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshServerListActionPerformed(evt);
            }
        });
        jToolBar1.add(btnRefreshServerList);

        btnOpenServerFile.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/folder-open-document.png"))); // NOI18N
        btnOpenServerFile.setText(bundle.getString("FileBrowserForm.btnOpenServerFile.text")); // NOI18N
        btnOpenServerFile.setFocusable(false);
        btnOpenServerFile.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnOpenServerFile.setName("btnOpenServerFile"); // NOI18N
        btnOpenServerFile.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnOpenServerFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOpenServerFileActionPerformed(evt);
            }
        });
        jToolBar1.add(btnOpenServerFile);

        btnDeleteServerFile.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/remove.png"))); // NOI18N
        btnDeleteServerFile.setText(bundle.getString("FileBrowserForm.btnDeleteServerFile.text")); // NOI18N
        btnDeleteServerFile.setFocusable(false);
        btnDeleteServerFile.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnDeleteServerFile.setName("btnDeleteServerFile"); // NOI18N
        btnDeleteServerFile.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnDeleteServerFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteServerFileActionPerformed(evt);
            }
        });
        jToolBar1.add(btnDeleteServerFile);

        jSeparator1.setName("jSeparator1"); // NOI18N
        jToolBar1.add(jSeparator1);

        btnAttachFromServer.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/attachment.png"))); // NOI18N
        btnAttachFromServer.setText(bundle.getString("FileBrowserForm.btnAttachFromServer.text")); // NOI18N
        btnAttachFromServer.setFocusable(false);
        btnAttachFromServer.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnAttachFromServer.setName("btnAttachFromServer"); // NOI18N
        btnAttachFromServer.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnAttachFromServer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAttachFromServerActionPerformed(evt);
            }
        });
        jToolBar1.add(btnAttachFromServer);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 547, Short.MAX_VALUE)
                    .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, 547, Short.MAX_VALUE))
                .addGap(17, 17, 17)
                .addComponent(lblServerPreview, javax.swing.GroupLayout.DEFAULT_SIZE, 355, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblServerPreview, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 509, Short.MAX_VALUE)))
                .addContainerGap())
        );

        jTabbedPane1.addTab(bundle.getString("FileBrowserForm.jPanel2.TabConstraints.tabTitle"), jPanel2); // NOI18N

        taskPanel1.setName(bundle.getString("FileBrowserForm.taskPanel1.name")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1)
                .addContainerGap())
            .addComponent(taskPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1)
                .addGap(10, 10, 10)
                .addComponent(taskPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        bindingGroup.bind();

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnRefreshServerListActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshServerListActionPerformed
        refreshRemoteFiles();
    }//GEN-LAST:event_btnRefreshServerListActionPerformed

    private void btnOpenServerFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOpenServerFileActionPerformed
        openRemoteFile();
    }//GEN-LAST:event_btnOpenServerFileActionPerformed

    private void tbServerFilesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbServerFilesMouseClicked
        if (evt.getClickCount() == 2 && evt.getButton() == MouseEvent.BUTTON1) {
            openRemoteFile();
        }
    }//GEN-LAST:event_tbServerFilesMouseClicked

    private void btnAttachFromServerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAttachFromServerActionPerformed
        attachRemoteFile();
    }//GEN-LAST:event_btnAttachFromServerActionPerformed

    private void btnDeleteServerFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteServerFileActionPerformed
        deleteRemoteFile();
    }//GEN-LAST:event_btnDeleteServerFileActionPerformed

    /**
     * Opens selected file from the local drive, by double click.
     */
    private void localFileChooserMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_localFileChooserMouseClicked
        if (evt.getClickCount() == 2) {
            openLocalFile();
        }
    }//GEN-LAST:event_localFileChooserMouseClicked

    /**
     * Opens selected file from the local drive, by click on the open button.
     */
    private void btnOpenLocalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOpenLocalActionPerformed
        openLocalFile();
    }//GEN-LAST:event_btnOpenLocalActionPerformed

    /**
     * Uploads selected file into the digital archive from local drive, gets
     * {@link DocumentBean} of uploaded file and calls method
     * {@link #fireAttachEvent(DocumentBean)} to rise attachment event.
     */
    private void btnAttachLocalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAttachLocalActionPerformed
        attachLocalFile();
    }//GEN-LAST:event_btnAttachLocalActionPerformed

    private void menuRemoteRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuRemoteRefreshActionPerformed
        refreshRemoteFiles();
    }//GEN-LAST:event_menuRemoteRefreshActionPerformed

    private void menuRemoteOpenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuRemoteOpenActionPerformed
        openRemoteFile();
    }//GEN-LAST:event_menuRemoteOpenActionPerformed

    private void menuRemoteDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuRemoteDeleteActionPerformed
        deleteRemoteFile();
    }//GEN-LAST:event_menuRemoteDeleteActionPerformed

    private void menuRemoteAttachActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuRemoteAttachActionPerformed
        attachRemoteFile();
    }//GEN-LAST:event_menuRemoteAttachActionPerformed

    private void formComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentResized
        loadServerPreview();
    }//GEN-LAST:event_formComponentResized

    private void txtFilterKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtFilterKeyPressed
    }//GEN-LAST:event_txtFilterKeyPressed

    private void txtFilterKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtFilterKeyReleased
        filterFiles();
    }//GEN-LAST:event_txtFilterKeyReleased
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAttachFromServer;
    private javax.swing.JButton btnAttachLocal;
    private javax.swing.JButton btnDeleteServerFile;
    private javax.swing.JButton btnOpenLocal;
    private javax.swing.JButton btnOpenServerFile;
    private javax.swing.JButton btnRefreshServerList;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JToolBar jToolBar2;
    private javax.swing.JLabel lblServerPreview;
    private javax.swing.JFileChooser localFileChooser;
    private javax.swing.JMenuItem menuRemoteAttach;
    private javax.swing.JMenuItem menuRemoteDelete;
    private javax.swing.JMenuItem menuRemoteOpen;
    private javax.swing.JMenuItem menuRemoteRefresh;
    private javax.swing.JPopupMenu popupRemoteFiles;
    private org.sola.clients.beans.digitalarchive.FileInfoListBean serverFiles;
    private org.sola.clients.swing.common.tasks.TaskPanel taskPanel1;
    private javax.swing.JTable tbServerFiles;
    private javax.swing.JTextField txtFilter;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
}
