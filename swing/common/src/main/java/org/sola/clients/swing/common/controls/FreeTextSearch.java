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
/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */
package org.sola.clients.swing.common.controls;

import com.ctc.wstx.util.StringUtil;
import java.awt.Color;
import java.awt.event.*;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import org.sola.clients.swing.common.LafManager;
import org.sola.common.StringUtility;

/**
 *
 * @author Elton Manoku
 */
public class FreeTextSearch extends JTextField {

    private JList list = null;
    private JScrollPane listScroll = null;
    private Integer listHeight = 100;
    private Object selectedElement = null;
    private Integer minimalSearchStringLength = 1;
    private String searchString = "";
    private boolean listIsHosted = false;
    private boolean hideListIfNotNeeded = true;
    private boolean refreshTextInSelection = false;
    private String defaultText;
    private boolean defaultTextIsSet = false;
    private Color originalTextColor = Color.BLACK;
    private Color defaultTextColor = Color.gray;
    public final static String ELEMENT_SELECTED = "elementSelected";
    
    public FreeTextSearch() {
        this.addKeyListener(
                new java.awt.event.KeyAdapter() {

                    @Override
                    public void keyReleased(java.awt.event.KeyEvent evt) {
                        processInput(evt);
                    }
                });

        this.addFocusListener(new FocusListener() {

            @Override
            public void focusGained(FocusEvent e) {
                removeDefaultText();
            }

            @Override
            public void focusLost(FocusEvent e) {
                setDefaultText();
            }
        });
        
        this.list = new JList(new DefaultListModel());
        this.list.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        LafManager.getInstance().setListProperties(list);
        this.list.addListSelectionListener(new javax.swing.event.ListSelectionListener() {

            @Override
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                listValueChanged(evt);
            }
        });
        this.list.addKeyListener(new java.awt.event.KeyAdapter() {

            @Override
            public void keyPressed(java.awt.event.KeyEvent evt) {
                onListKeyPressed(evt);
            }
        });
        this.list.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                onMouseClicked(e);
            }
        });
        setText("");
    }

    private void setDefaultText(){
        if(StringUtility.isEmpty(getText()) && getDefaultText() != null){
            defaultTextIsSet = true;
            originalTextColor = getForeground();
            setForeground(defaultTextColor);
            setText(getDefaultText());
        }
    }
    
    private void removeDefaultText(){
        if(defaultTextIsSet && StringUtility.empty(getText()).equals(getDefaultText())){
            defaultTextIsSet = false;
            setForeground(originalTextColor);
            setText("");
        }
    }

    /** Returns default text color. */
    public Color getDefaultTextColor() {
        return defaultTextColor;
    }

    /** Sets default text color. */
    public void setDefaultTextColor(Color defaultTextColor) {
        this.defaultTextColor = defaultTextColor;
    }

    /** Returns default text to display in the text field, when the field is empty. */
    public String getDefaultText() {
        return defaultText;
    }

    /** Sets default text to display in the text field, when the field is empty. */
    public void setDefaultText(String defaultText) {
        this.defaultText = defaultText;
        setDefaultText();
    }
    
    private void processInput(java.awt.event.KeyEvent evt) {
        JTextField tmp = (JTextField) evt.getSource();
        LafManager.getInstance().setTxtProperties(tmp);

        String tmpSearchString = tmp.getText();
        if (tmpSearchString.length() < this.minimalSearchStringLength
                && this.listScroll != null) {
            ((DefaultListModel) this.list.getModel()).clear();
            this.searchString = "";
            if (this.hideListIfNotNeeded) {
                this.listScroll.setVisible(false);
            }
            firePropertyChange(ELEMENT_SELECTED, false, true);
            return;
        }
        if (!this.listIsHosted) {
            this.listIsHosted = true;
            if (this.listScroll == null) {
                this.listScroll = new JScrollPane(list);
                this.listScroll.setSize(this.getSize().width, listHeight);

                this.getParent().add(this.listScroll);
                this.getParent().setComponentZOrder(this.listScroll, 0);

                this.listScroll.setLocation(
                        this.getLocation().x, this.getLocation().y + this.getSize().height + 3);
            } else {
                this.listScroll.setViewportView(this.list);
            }
        }
        if (evt.getKeyCode() == 40) {
            this.list.requestFocusInWindow();
            return;
        }

        if (!this.searchString.equals(tmpSearchString)) {
            DefaultListModel model = (DefaultListModel) this.list.getModel();
            this.onNewSearchString(tmpSearchString, model);
            this.listScroll.setVisible(true);
        }
        this.searchString = tmpSearchString;
    }

    private void listValueChanged(ListSelectionEvent e) {
        if (e.getValueIsAdjusting() == false) {

            if (list.getSelectedIndex() == -1) {
                this.selectedElement = null;

            } else {
                this.setSelectedElement(list.getSelectedValue());
            }
        }
    }

    private void onListKeyPressed(java.awt.event.KeyEvent evt) {
        //Enter is pressed
        if (evt.getKeyCode() == 10) {
            this.confirmSelection();
        }
    }

    private void onMouseClicked(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2) {
            this.confirmSelection();
        }
    }

    private void confirmSelection() {
        if (this.getSelectedElement() == null) {
            return;
        }
        if (this.hideListIfNotNeeded) {
            this.listScroll.setVisible(false);
        }
        this.onSelectionConfirmed();
    }

    public void onSelectionConfirmed() {
        System.out.println("Selected object:" + this.getSelectedElement());
    }

    public void onNewSearchString(String searchString, DefaultListModel listModel) {
        listModel.addElement(searchString);
    }

    public Object getSelectedElement() {
        return this.selectedElement;
    }

    public void setSelectedElement(Object element) {
        this.selectedElement = element;
        if (this.refreshTextInSelection) {
            this.setText(this.selectedElement.toString());
        }
        firePropertyChange(ELEMENT_SELECTED, false, true);
    }

    public Integer getListHeight() {
        return listHeight;
    }

    public void setListHeight(Integer listHeight) {
        this.listHeight = listHeight;
    }

    public Integer getMinimalSearchStringLength() {
        return minimalSearchStringLength;
    }

    public void setMinimalSearchStringLength(Integer minimalSearchStringLength) {
        this.minimalSearchStringLength = minimalSearchStringLength;
    }

    public JScrollPane getListScroll() {
        return listScroll;
    }

    public void setListScroll(JScrollPane listScroll) {
        this.listScroll = listScroll;
    }

    public boolean isHideListIfNotNeeded() {
        return hideListIfNotNeeded;
    }

    public void setHideListIfNotNeeded(boolean hideListIfNotNeeded) {
        this.hideListIfNotNeeded = hideListIfNotNeeded;
    }

    public boolean isRefreshTextInSelection() {
        return refreshTextInSelection;
    }

    public void setRefreshTextInSelection(boolean refreshTextInSelection) {
        this.refreshTextInSelection = refreshTextInSelection;
    }
}
