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
package org.sola.clients.swing.ui.security;

import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Locale;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import org.jdesktop.application.Application;
import org.jdesktop.application.Task;
import org.jdesktop.application.TaskEvent;
import org.jdesktop.application.TaskListener;
import org.sola.clients.swing.common.LafManager;
import org.sola.clients.swing.common.config.ConfigurationManager;
import org.sola.clients.swing.common.controls.LanguageCombobox;
import org.sola.clients.swing.common.tasks.TaskManager;
import org.sola.common.messaging.ClientMessage;
import org.sola.common.messaging.MessageUtility;
import org.sola.clients.swing.ui.security.LoginForm;
/**
 * Allows to authenticate users. 
 */
public class LoginPanel extends javax.swing.JPanel {
    
    public static final String LOGIN_RESULT = "LoginResult";
    private Application application;
    protected JRadioButton previousButton;
    
    private LoginForm loginform;
        
    
    Object foreFont =  LafManager.getInstance().getForeFont();
    Object labFont =  LafManager.getInstance().getLabFont();
    Object bgFont =    LafManager.getInstance().getBgFont();
    Object txtFont =   LafManager.getInstance().getTxtFont();
    Object txtAreaFont =   LafManager.getInstance().getTxtAreaFont();
    Object btnFont =   LafManager.getInstance().getBtnFont();
    Object tabFont =   LafManager.getInstance().getTabFont();
    Object cmbFont =   LafManager.getInstance().getCmbFont();
    Object btnBackground =   LafManager.getInstance().getBtnBackground();
    
    
    
    /** Create combobox with languages */
    private LanguageCombobox createLanguageCombobox() {
        if (application != null) {
            return new LanguageCombobox(application.getClass());
        } else {
            return new LanguageCombobox();
        }
    }
    
    
    /** Default constructor. */
    public LoginPanel() {
        initComponents();
        jPanel3.setVisible(false);
//             System.out.println("this.accessibleContext.toString()= "+this.accessibleContext.toString());
  
    }

    /** 
     * Creates new form LoginPanel. 
     * @param applicationMainClass 
     */
    public LoginPanel(Application application) {
        this.application = application;
        initComponents();
        jPanel3.setVisible(false);
        txtUsername.requestFocus();
        
        // TODO: REMOVE IN RELEASE!!!
        //txtUsername.setText("test");
       // txtUserPassword.setText("test");
//        System.out.println("this.accessibleContext.toString()= "+this.accessibleContext.toString());
    }

    /** Calls authentication procedure. */
    private void login() {
        Task t = new Task(application) {

            @Override
            protected Object doInBackground() throws Exception {
                boolean result = true;
                try {
                    setMessage(MessageUtility.getLocalizedMessage(
                            ClientMessage.GENERAL_LOADING_APPLICATION).getMessage());

                    enablePanel(false);
                    if (!securityBean.authenticate(ConfigurationManager.getWSConfig())) {
                        result = false;
                    }
                } catch (Exception ex) {
                    result = false;
                    throw ex;
                } finally {
                    if (!result) {
                        enablePanel(true);
                        txtUsername.requestFocusInWindow();
                        txtUsername.selectAll();
                    }
                }
                return result;
            }
        };

        t.addTaskListener(new TaskListener() {
            @Override
            public void doInBackground(TaskEvent te) {
            }

            @Override
            public void process(TaskEvent te) {
            }

            @Override
            public void succeeded(TaskEvent te) {
                if(te.getValue()!=null && Boolean.class.isAssignableFrom(te.getValue().getClass())){
                    fireLoginEvent((Boolean)te.getValue());
                }
            }

            @Override
            public void failed(TaskEvent te) {
            }

            @Override
            public void cancelled(TaskEvent te) {
            }

            @Override
            public void interrupted(TaskEvent te) {
            }

            @Override
            public void finished(TaskEvent te) {
            }
        });
        TaskManager.getInstance().runTask(t);
    }

    private void fireLoginEvent(boolean result) {
        firePropertyChange(LOGIN_RESULT, false, result);
    }

    /** Enables or disables panel*/
    public void enablePanel(boolean isEnabled) {
        btnLogin.setEnabled(isEnabled);
        txtUsername.setEnabled(isEnabled);
        txtUserPassword.setEnabled(isEnabled);
        languageCombobox.setEnabled(isEnabled);
    }

    /** Explicitely sets focus on user name text field.*/
    public void setUserNameFocus() {
        txtUsername.requestFocus();
        // TODO: REMOVE IN RELEASE!!!
        btnLogin.requestFocus();
    }
    
      /** Sets the backgroundpanel. */
 
     
   class LNFSetter implements ActionListener {
    String theLNFName;
    JRadioButton thisButton;
    String whichButton;
//    LoginForm loginform;
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
        Object foreFont = "nimbusBase";
        labWelcome.setForeground(UIManager.getColor(foreFont));
        jSeparator1.setForeground(UIManager.getColor(foreFont));
        jSeparator2.setForeground(UIManager.getColor(foreFont));
        jSeparator3.setForeground(UIManager.getColor(foreFont));
      
//        loginform.jPanel1.setBackground(UIManager.getColor(psswdFont));
      } catch (Exception evt) {
        JOptionPane.showMessageDialog(null, "setLookAndFeel didn't work: " + evt, "UI Failure",
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
        jLabel1 = new javax.swing.JLabel();
        languageCombobox = createLanguageCombobox();
        jSeparator3 = new javax.swing.JSeparator();
        labWelcome = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        labUser = new javax.swing.JLabel();
        txtUsername = new javax.swing.JTextField();
        labPassword = new javax.swing.JLabel();
        txtUserPassword = new javax.swing.JPasswordField();
        jSeparator2 = new javax.swing.JSeparator();
        jPanel1 = new javax.swing.JPanel();
        labDescUp = new javax.swing.JLabel();
        labDescDown = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        taskPanel1 = new org.sola.clients.swing.common.tasks.TaskPanel();
        btnLogin = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        bNimbus = new javax.swing.JRadioButton();
        bJava = new javax.swing.JRadioButton();
        bMSW = new javax.swing.JRadioButton();
        bMotif = new javax.swing.JRadioButton();
        bMac = new javax.swing.JRadioButton();
        bDefault = new javax.swing.JRadioButton();
        bAutumn = new javax.swing.JRadioButton();

        setBackground(new java.awt.Color(255, 255, 255));

        mainPanel.setBackground(new java.awt.Color(255, 255, 255));
        mainPanel.setName("mainPanel"); // NOI18N

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/sola/caption_orange.png"))); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        languageCombobox.setName("languageCombobox"); // NOI18N

        jSeparator3.setName("jSeparator3"); // NOI18N

        labWelcome.setFont(new java.awt.Font("Tahoma", 1, 18));
        labWelcome.setForeground(UIManager.getColor(foreFont));
        labWelcome.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/sola/clients/swing/ui/security/Bundle"); // NOI18N
        labWelcome.setText(bundle.getString("LoginPanel.labWelcome.text")); // NOI18N
        labWelcome.setName("labWelcome"); // NOI18N

        jSeparator1.setName("jSeparator1"); // NOI18N

        labUser.setFont(UIManager.getFont(labFont));
        labUser.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        labUser.setText(bundle.getString("LoginPanel.labUser.text")); // NOI18N
        labUser.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        labUser.setName("labUser"); // NOI18N

        txtUsername.setBackground(UIManager.getColor(bgFont));
        txtUsername.setFont((UIManager.getFont(txtFont)));
        txtUsername.setName("txtUsername"); // NOI18N

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, securityBean, org.jdesktop.beansbinding.ELProperty.create("${userName}"), txtUsername, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        txtUsername.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
        txtUsername.setHorizontalAlignment(JTextField.LEADING);
        txtUsername.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtUsernameKeyPressed(evt);
            }
        });

        labPassword.setFont(UIManager.getFont(labFont));
        labPassword.setText(bundle.getString("LoginPanel.labPassword.text")); // NOI18N
        labPassword.setName("labPassword"); // NOI18N

        txtUserPassword.setFont((UIManager.getFont(txtFont)));
        txtUserPassword.setName("txtUserPassword"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, securityBean, org.jdesktop.beansbinding.ELProperty.create("${userPassword}"), txtUserPassword, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        txtUserPassword.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtUserPasswordKeyPressed(evt);
            }
        });

        jSeparator2.setName("jSeparator2"); // NOI18N

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setName("jPanel1"); // NOI18N

        labDescUp.setBackground(new java.awt.Color(153, 153, 153));
        labDescUp.setFont(new java.awt.Font("Tahoma", 1, 10));
        labDescUp.setText(bundle.getString("LoginPanel.labDescUp.text")); // NOI18N
        labDescUp.setName("labDescUp"); // NOI18N

        labDescDown.setFont(new java.awt.Font("Tahoma", 1, 10));
        labDescDown.setText(bundle.getString("LoginPanel.labDescDown.text")); // NOI18N
        labDescDown.setName("labDescDown"); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labDescUp))
            .addComponent(labDescDown)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labDescUp)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(labDescDown))
        );

        jPanel2.setName("jPanel2"); // NOI18N

        taskPanel1.setName("taskPanel1"); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(taskPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 556, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(taskPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        btnLogin.setText(bundle.getString("LoginPanel.btnLogin.text")); // NOI18N
        btnLogin.setName("btnLogin"); // NOI18N
        btnLogin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLoginActionPerformed(evt);
            }
        });

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setName("jPanel3"); // NOI18N

        bNimbus.setBackground(new java.awt.Color(255, 255, 255));
        bNimbus.setText(bundle.getString("LoginPanel.bNimbus.text")); // NOI18N
        bNimbus.setName("bNimbus"); // NOI18N
        bNimbus.addActionListener(new LNFSetter("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel", bNimbus,"green"));
        bg.add(bNimbus);

        bJava.setBackground(new java.awt.Color(255, 255, 255));
        bJava.setText(bundle.getString("LoginPanel.bJava.text")); // NOI18N
        bJava.setName("bJava"); // NOI18N
        bJava.addActionListener(new LNFSetter("javax.swing.plaf.metal.MetalLookAndFeel", bJava, "default"));
        bg.add(bJava);

        bMSW.setBackground(new java.awt.Color(255, 255, 255));
        bMSW.setText(bundle.getString("LoginPanel.bMSW.text")); // NOI18N
        bMSW.setName("bMSW"); // NOI18N
        bMSW.addActionListener(new LNFSetter("com.sun.java.swing.plaf.windows.WindowsLookAndFeel", bMSW, "default"));
        bg.add(bMSW);

        bMotif.setBackground(new java.awt.Color(255, 255, 255));
        bMotif.setText(bundle.getString("LoginPanel.bMotif.text")); // NOI18N
        bMotif.setName("bMotif"); // NOI18N
        bMotif.addActionListener(new LNFSetter("com.sun.java.swing.plaf.motif.MotifLookAndFeel", bMotif, "default"));
        bg.add(bMotif);

        bMac.setBackground(new java.awt.Color(255, 255, 255));
        bMac.setText(bundle.getString("LoginPanel.bMac.text")); // NOI18N
        bMac.setName("bMac"); // NOI18N
        bMac.addActionListener(new LNFSetter("com.sun.java.swing.plaf.mac.MacLookAndFeel", bMac, "default"));
        bg.add(bMac);

        bDefault.setBackground(new java.awt.Color(255, 255, 255));
        bDefault.setText(bundle.getString("LoginPanel.bDefault.text")); // NOI18N
        bDefault.setName("bDefault"); // NOI18N
        String defaultLookAndFeel = UIManager.getSystemLookAndFeelClassName();
        bDefault.addActionListener(new LNFSetter(defaultLookAndFeel, bDefault, "default"));
        bg.add(bDefault);

        bAutumn.setBackground(new java.awt.Color(255, 255, 255));
        bAutumn.setText(bundle.getString("LoginPanel.bAutumn.text")); // NOI18N
        bAutumn.setName("bAutumn"); // NOI18N
        bAutumn.addActionListener(new LNFSetter("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel", bAutumn, "autumn"));
        bg.add(bAutumn);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(bNimbus)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(bAutumn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(bJava)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(bMSW)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(bMotif)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(bMac)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(bDefault, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(25, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(bNimbus)
                    .addComponent(bJava)
                    .addComponent(bMSW)
                    .addComponent(bMotif)
                    .addComponent(bMac)
                    .addComponent(bDefault)
                    .addComponent(bAutumn)))
        );

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addComponent(jLabel1)
                .addGap(41, 41, 41)
                .addComponent(languageCombobox, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(25, Short.MAX_VALUE))
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addGap(133, 133, 133)
                .addComponent(labWelcome, javax.swing.GroupLayout.PREFERRED_SIZE, 277, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(145, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainPanelLayout.createSequentialGroup()
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(btnLogin, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, mainPanelLayout.createSequentialGroup()
                        .addGap(59, 59, 59)
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(labUser, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(labPassword, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 65, Short.MAX_VALUE))
                        .addGap(16, 16, 16)
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(txtUserPassword)
                            .addComponent(txtUsername, javax.swing.GroupLayout.DEFAULT_SIZE, 334, Short.MAX_VALUE))))
                .addGap(844, 844, 844))
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addGap(39, 39, 39)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(839, Short.MAX_VALUE))
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jSeparator3, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 491, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(807, Short.MAX_VALUE))
            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(languageCombobox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labWelcome, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14, 14, 14)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labUser, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtUsername, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtUserPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(26, 26, 26)
                .addComponent(btnLogin)
                .addGap(18, 18, 18)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(38, 38, 38)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 555, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents

    private void btnLoginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLoginActionPerformed
        login();
    }//GEN-LAST:event_btnLoginActionPerformed

    private void txtUsernameKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtUsernameKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            txtUserPassword.requestFocus();
            txtUserPassword.selectAll();
        }
    }//GEN-LAST:event_txtUsernameKeyPressed

    private void txtUserPasswordKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtUserPasswordKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            login();
        }
    }//GEN-LAST:event_txtUserPasswordKeyPressed
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
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JLabel labDescDown;
    private javax.swing.JLabel labDescUp;
    private javax.swing.JLabel labPassword;
    private javax.swing.JLabel labUser;
    private javax.swing.JLabel labWelcome;
    private org.sola.clients.swing.common.controls.LanguageCombobox languageCombobox;
    private javax.swing.JPanel mainPanel;
    private org.sola.clients.beans.security.SecurityBean securityBean;
    private org.sola.clients.swing.common.tasks.TaskPanel taskPanel1;
    private javax.swing.JPasswordField txtUserPassword;
    private javax.swing.JTextField txtUsername;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
}
