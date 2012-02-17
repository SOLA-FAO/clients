/**
 * ******************************************************************************************
 * Copyright (C) 2012 - Food and Agriculture Organization of the United Nations
 * (FAO). All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,this
 * list of conditions and the following disclaimer. 2. Redistributions in binary
 * form must reproduce the above copyright notice,this list of conditions and
 * the following disclaimer in the documentation and/or other materials provided
 * with the distribution. 3. Neither the name of FAO nor the names of its
 * contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT,STRICT LIABILITY,OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING
 * IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * *********************************************************************************************
 */
package org.sola.clients.swing.ui.security;

import java.awt.ComponentOrientation;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Locale;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import org.sola.clients.swing.common.LafManager;
import org.sola.clients.swing.common.config.ConfigurationManager;
import org.sola.clients.swing.common.controls.LanguageCombobox;
import org.sola.clients.swing.common.tasks.SolaTask;
import org.sola.clients.swing.common.tasks.TaskManager;
import org.sola.common.messaging.ClientMessage;
import org.sola.common.messaging.MessageUtility;

/**
 * Allows to authenticate users.
 */
public class LoginPanel extends javax.swing.JPanel {

    public static final String LOGIN_RESULT = "loginResult";
    private Class<?> mainClass;
    protected JRadioButton previousButton;

    /**
     * Create combobox with languages
     */
    private LanguageCombobox createLanguageCombobox() {
        if (mainClass != null) {
            return new LanguageCombobox(mainClass);
        } else {
            return new LanguageCombobox();
        }
    }

    /**
     * Default constructor.
     */
    public LoginPanel() {
        initComponents();
        pnlThems.setVisible(false);
        txtUsername.requestFocus();
    }

    /**
     * Creates new form LoginPanel.
     *
     * @param applicationMainClass
     */
    public LoginPanel(Class<?> mainClasss) {
        this.mainClass = mainClasss;
        initComponents();
        pnlThems.setVisible(false);
        txtUsername.requestFocus();

        // TODO: REMOVE IN RELEASE!!!
        txtUsername.setText("test");
        txtUserPassword.setText("test");
    }

    /**
     * Calls authentication procedure.
     */
    private void login() {
        SolaTask t = new SolaTask<Boolean, Object>() {

            private boolean result = false;

            @Override
            protected Boolean doTask() {
                setMessage(MessageUtility.getLocalizedMessage(
                        ClientMessage.GENERAL_LOADING_APPLICATION).getMessage());

                enablePanel(false);
                result = securityBean.authenticate(ConfigurationManager.getWSConfig());
                return result;
            }

            @Override
            protected void taskDone() {
                if (result) {
                    fireLoginEvent(true);
                } else {
                    enablePanel(true);
                }
            }

            @Override
            protected void taskFailed(Throwable e) {
                enablePanel(true);
            }
        };
        TaskManager.getInstance().runTask(t);
    }

    private void fireLoginEvent(boolean result) {
        firePropertyChange(LOGIN_RESULT, false, result);
    }

    /**
     * Enables or disables panel
     */
    public void enablePanel(boolean isEnabled) {
        btnLogin.setEnabled(isEnabled);
        txtUsername.setEnabled(isEnabled);
        txtUserPassword.setEnabled(isEnabled);
        languageCombobox.setEnabled(isEnabled);
        if (isEnabled) {
            txtUsername.requestFocusInWindow();
            txtUsername.selectAll();
        }
    }

    /**
     * Explicitely sets focus on user name text field.
     */
    public void setUserNameFocus() {
        txtUsername.requestFocus();
        this.getRootPane().setDefaultButton(btnLogin);
    }

    /**
     * Sets the backgroundpanel.
     */
    class LNFSetter implements ActionListener {

        String theLNFName;
        JRadioButton thisButton;
        String whichButton;

        LNFSetter(String lnfName, JRadioButton me, String theme) {
            theLNFName = lnfName;
            whichButton = theme;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                LafManager.getInstance().setProperties(whichButton);
                UIManager.setLookAndFeel(theLNFName);
                SwingUtilities.updateComponentTreeUI(mainPanel);
                Object psswdFont = "PasswordField.background";
                txtUsername.setBackground(UIManager.getColor(psswdFont));
                txtUserPassword.setBackground(UIManager.getColor(psswdFont));
                txtUsername.setBackground(UIManager.getColor(psswdFont));
                Object foreFont = "nimbusBase";
                jSeparator1.setForeground(UIManager.getColor(foreFont));
                jSeparator2.setForeground(UIManager.getColor(foreFont));
                jSeparator3.setForeground(UIManager.getColor(foreFont));

            } catch (Exception evt) {
                JOptionPane.showMessageDialog(null, java.util.ResourceBundle.getBundle("org/sola/clients/swing/ui/security/Bundle_en_US").getString("SETLOOKANDFEEL DIDN'T WORK: ") + evt, java.util.ResourceBundle.getBundle("org/sola/clients/swing/ui/security/Bundle_en_US").getString("UI FAILURE"),
                        JOptionPane.INFORMATION_MESSAGE);
                previousButton.setSelected(true); // reset the GUI to agree
            }
            previousButton = thisButton;
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        securityBean = new org.sola.clients.beans.security.SecurityBean();
        bg = new javax.swing.ButtonGroup();
        mainPanel = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        taskPanel1 = new org.sola.clients.swing.common.tasks.TaskPanel();
        pnlThems = new javax.swing.JPanel();
        bDefault = new javax.swing.JRadioButton();
        bMac = new javax.swing.JRadioButton();
        bMotif = new javax.swing.JRadioButton();
        bMSW = new javax.swing.JRadioButton();
        bJava = new javax.swing.JRadioButton();
        bAutumn = new javax.swing.JRadioButton();
        bNimbus = new javax.swing.JRadioButton();
        jPanel5 = new javax.swing.JPanel();
        txtUserPassword = new javax.swing.JPasswordField();
        labPassword = new javax.swing.JLabel();
        labUser = new javax.swing.JLabel();
        btnLogin = new javax.swing.JButton();
        txtUsername = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        languageCombobox = createLanguageCombobox();
        jPanel1 = new javax.swing.JPanel();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator3 = new javax.swing.JSeparator();
        jLabel2 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        labDescUp = new javax.swing.JLabel();
        labDescDown = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();

        setBackground(new java.awt.Color(255, 255, 255));

        mainPanel.setName("mainPanel"); // NOI18N
        mainPanel.setOpaque(false);

        jPanel2.setName("jPanel2"); // NOI18N
        jPanel2.setLayout(new javax.swing.BoxLayout(jPanel2, javax.swing.BoxLayout.LINE_AXIS));

        taskPanel1.setName("taskPanel1"); // NOI18N
        jPanel2.add(taskPanel1);

        pnlThems.setName("pnlThems"); // NOI18N
        pnlThems.setOpaque(false);
        pnlThems.setLayout(new java.awt.GridLayout(1, 7));

        bDefault.setBackground(new java.awt.Color(255, 255, 255));
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/sola/clients/swing/ui/security/Bundle"); // NOI18N
        bDefault.setText(bundle.getString("LoginPanel.bDefault.text")); // NOI18N
        bDefault.setName("bDefault"); // NOI18N
        String defaultLookAndFeel = UIManager.getSystemLookAndFeelClassName();
        bDefault.addActionListener(new LNFSetter(defaultLookAndFeel, bDefault, "default"));
        bg.add(bDefault);
        pnlThems.add(bDefault);

        bMac.setBackground(new java.awt.Color(255, 255, 255));
        bMac.setText(bundle.getString("LoginPanel.bMac.text")); // NOI18N
        bMac.setName("bMac"); // NOI18N
        bMac.addActionListener(new LNFSetter("com.sun.java.swing.plaf.mac.MacLookAndFeel", bMac, "default"));
        bg.add(bMac);
        pnlThems.add(bMac);

        bMotif.setBackground(new java.awt.Color(255, 255, 255));
        bMotif.setText(bundle.getString("LoginPanel.bMotif.text")); // NOI18N
        bMotif.setName("bMotif"); // NOI18N
        bMotif.addActionListener(new LNFSetter("com.sun.java.swing.plaf.motif.MotifLookAndFeel", bMotif, "default"));
        bg.add(bMotif);
        pnlThems.add(bMotif);

        bMSW.setBackground(new java.awt.Color(255, 255, 255));
        bMSW.setText(bundle.getString("LoginPanel.bMSW.text")); // NOI18N
        bMSW.setName("bMSW"); // NOI18N
        bMSW.addActionListener(new LNFSetter("com.sun.java.swing.plaf.windows.WindowsLookAndFeel", bMSW, "default"));
        bg.add(bMSW);
        pnlThems.add(bMSW);

        bJava.setBackground(new java.awt.Color(255, 255, 255));
        bJava.setText(bundle.getString("LoginPanel.bJava.text")); // NOI18N
        bJava.setName("bJava"); // NOI18N
        bJava.addActionListener(new LNFSetter("javax.swing.plaf.metal.MetalLookAndFeel", bJava, "default"));
        bg.add(bJava);
        pnlThems.add(bJava);

        bAutumn.setBackground(new java.awt.Color(255, 255, 255));
        bAutumn.setText(bundle.getString("LoginPanel.bAutumn.text")); // NOI18N
        bAutumn.setName("bAutumn"); // NOI18N
        bAutumn.addActionListener(new LNFSetter("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel", bAutumn, "autumn"));
        bg.add(bAutumn);
        pnlThems.add(bAutumn);

        bNimbus.setBackground(new java.awt.Color(255, 255, 255));
        bNimbus.setText(bundle.getString("LoginPanel.bNimbus.text")); // NOI18N
        bNimbus.setName("bNimbus"); // NOI18N
        bNimbus.addActionListener(new LNFSetter("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel", bNimbus,"green"));
        bg.add(bNimbus);
        pnlThems.add(bNimbus);

        jPanel5.setName("jPanel5"); // NOI18N
        jPanel5.setOpaque(false);

        txtUserPassword.setName("txtUserPassword"); // NOI18N

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, securityBean, org.jdesktop.beansbinding.ELProperty.create("${userPassword}"), txtUserPassword, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        txtUserPassword.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtUserPasswordKeyPressed(evt);
            }
        });

        labPassword.setText(bundle.getString("LoginPanel.labPassword.text")); // NOI18N
        labPassword.setName("labPassword"); // NOI18N

        labUser.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        labUser.setText(bundle.getString("LoginPanel.labUser.text")); // NOI18N
        labUser.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        labUser.setName("labUser"); // NOI18N

        btnLogin.setText(bundle.getString("LoginPanel.btnLogin.text")); // NOI18N
        btnLogin.setName("btnLogin"); // NOI18N
        btnLogin.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
        btnLogin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLoginActionPerformed(evt);
            }
        });

        txtUsername.setName("txtUsername"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, securityBean, org.jdesktop.beansbinding.ELProperty.create("${userName}"), txtUsername, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        txtUsername.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtUsernameKeyPressed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(61, 61, 61)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnLogin, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(labUser, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(labPassword, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtUserPassword)
                            .addComponent(txtUsername, javax.swing.GroupLayout.PREFERRED_SIZE, 329, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(62, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labUser, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtUsername, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtUserPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(btnLogin)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel3.setName("jPanel3"); // NOI18N
        jPanel3.setOpaque(false);

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/sola/caption_orange.png"))); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        languageCombobox.setName("languageCombobox"); // NOI18N
        languageCombobox.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(languageCombobox, javax.swing.GroupLayout.DEFAULT_SIZE, 155, Short.MAX_VALUE)
                .addGap(6, 6, 6))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(languageCombobox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel1.setName("jPanel1"); // NOI18N
        jPanel1.setOpaque(false);

        jSeparator1.setName("jSeparator1"); // NOI18N

        jSeparator3.setName("jSeparator3"); // NOI18N

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(0, 102, 51));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText(bundle.getString("LoginPanel.jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator1, javax.swing.GroupLayout.DEFAULT_SIZE, 535, Short.MAX_VALUE)
            .addComponent(jSeparator3, javax.swing.GroupLayout.DEFAULT_SIZE, 535, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 515, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 42, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel4.setName("jPanel4"); // NOI18N
        jPanel4.setOpaque(false);

        labDescUp.setBackground(new java.awt.Color(153, 153, 153));
        labDescUp.setFont(new java.awt.Font("Tahoma", 1, 10)); // NOI18N
        labDescUp.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labDescUp.setText(bundle.getString("LoginPanel.labDescUp.text")); // NOI18N
        labDescUp.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        labDescUp.setName("labDescUp"); // NOI18N

        labDescDown.setFont(new java.awt.Font("Tahoma", 1, 10));
        labDescDown.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labDescDown.setText(bundle.getString("LoginPanel.labDescDown.text")); // NOI18N
        labDescDown.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        labDescDown.setName("labDescDown"); // NOI18N

        jSeparator2.setName("jSeparator2"); // NOI18N

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(labDescDown, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 535, Short.MAX_VALUE)
            .addComponent(labDescUp, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 535, Short.MAX_VALUE)
            .addComponent(jSeparator2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 535, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(labDescUp)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labDescDown)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 563, Short.MAX_VALUE)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.LEADING, 0, 535, Short.MAX_VALUE)
                    .addComponent(pnlThems, javax.swing.GroupLayout.Alignment.LEADING, 0, 0, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18))
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlThems, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(13, 13, 13)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 10, Short.MAX_VALUE)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 556, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents

    private void btnLoginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLoginActionPerformed
        login();
    }//GEN-LAST:event_btnLoginActionPerformed

    private void txtUserPasswordKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtUserPasswordKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            //login();
        }
    }//GEN-LAST:event_txtUserPasswordKeyPressed

    private void txtUsernameKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtUsernameKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            evt.consume();
            txtUserPassword.requestFocus();
            txtUserPassword.selectAll();
        }
    }//GEN-LAST:event_txtUsernameKeyPressed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JRadioButton bAutumn;
    private javax.swing.JRadioButton bDefault;
    private javax.swing.JRadioButton bJava;
    private javax.swing.JRadioButton bMSW;
    private javax.swing.JRadioButton bMac;
    private javax.swing.JRadioButton bMotif;
    private javax.swing.JRadioButton bNimbus;
    private javax.swing.ButtonGroup bg;
    public javax.swing.JButton btnLogin;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JLabel labDescDown;
    private javax.swing.JLabel labDescUp;
    private javax.swing.JLabel labPassword;
    private javax.swing.JLabel labUser;
    private org.sola.clients.swing.common.controls.LanguageCombobox languageCombobox;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JPanel pnlThems;
    private org.sola.clients.beans.security.SecurityBean securityBean;
    private org.sola.clients.swing.common.tasks.TaskPanel taskPanel1;
    private javax.swing.JPasswordField txtUserPassword;
    private javax.swing.JTextField txtUsername;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
}
