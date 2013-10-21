package org.sola.clients.swing.common.controls;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.font.TextAttribute;
import java.text.AttributedString;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.ComboBoxUI;
import javax.swing.plaf.basic.BasicComboBoxEditor;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.synth.SynthComboBoxUI;
import org.sola.clients.swing.common.tasks.SolaTask;
import org.sola.clients.swing.common.tasks.TaskManager;
import org.sola.common.messaging.ClientMessage;
import org.sola.common.messaging.MessageUtility;

/**
 * Combobox component with list searching feature. When typing a text, first
 * matching item will be found and selected in the list.
 */
public class TextSearch extends JComboBox {

    private class AutoTextField extends JTextField {

        AutoTextField(TextSearch b) {
            textSearch = b;
            init();
        }

        private void init() {
            this.addKeyListener(
                    new java.awt.event.KeyAdapter() {

                        @Override
                        public void keyReleased(java.awt.event.KeyEvent evt) {
                            if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
                                setPopupVisible(false);
                                return;
                            }

                            if (!evt.isActionKey()
                                    && evt.getKeyCode() != KeyEvent.VK_SHIFT
                                    && evt.getKeyCode() != KeyEvent.VK_CONTROL
                                    && evt.getKeyCode() != KeyEvent.VK_ALT) {
                                processInput(evt);
                            }
                        }
                    });
        }

        private void processInput(final java.awt.event.KeyEvent evt) {
            if (!searchText.equals(getText())) {
                searchText = getText();
                
                //TODO: skip search if item can be found in the current model

                if (searchText.length() >= getMinSearchStringLength()) {
                    isFiredBySearch = true;
                    removeAllItems();
                    setText(searchText);

                    // Run search as a task
                    if (runAsTask) {
                        // Check if a search is currently running and if so, cancel it
                        if (searchTask != null && TaskManager.getInstance().isTaskRunning(searchTask.getId())) {
                            TaskManager.getInstance().removeTask(searchTask);
                        }

                        searchTask = new SolaTask<Void, Void>() {

                            @Override
                            public Void doTask() {
                                // Set message
                                if (getProgessMessage() != null && !getProgessMessage().equals("")) {
                                    setMessage(getProgessMessage());
                                } else {
                                    // Set default message
                                    setMessage(MessageUtility.getLocalizedMessage(
                                            ClientMessage.PROGRESS_MSG_MAP_SEARCHING,
                                            new String[]{""}).getMessage());
                                }

                                try {
                                    // Allow a small delay so that the thread can be cancelled
                                    // before executing the search if the user is still typing. 
                                    Thread.sleep(500);
                                    search(searchText);
                                    if (!selectFirstMatch(searchText, !isAutoSelect(), evt)) {
                                        setText(searchText);
                                        setCaretToTheEnd();
                                    }
                                } catch (InterruptedException ex) {
                                }
                                return null;
                            }

                            @Override
                            public void taskDone() {
                                if (isAutoSelect()) {
                                    selectText();
                                } else {
                                    setCaretToTheEnd();
                                }
                                searchText = getText();
                                textSearch.setPopupVisible(getModel().getSize() > 0);
                            }
                        };
                        TaskManager.getInstance().runTask(searchTask);

                    } else {
                        // Run as a simple function
                        search(searchText);
                        textSearch.setPopupVisible(getModel().getSize() > 0);

                        if (!selectFirstMatch(searchText, !isAutoSelect(), evt)) {
                            setText(searchText);
                        } else {
                            selectText();
                            searchText = getText();
                        }
                    }
                } else {
                    removeAllItems();
                    isFiredBySearch = false;
                    setText(searchText);
                }
            }
        }

        private void setCaretToTheEnd(){
            setCaretPosition(getText().length());
        }
        
        private boolean selectFirstMatch(String searchText, boolean isStrict, java.awt.event.KeyEvent evt) {
            if (evt.getKeyCode() == KeyEvent.VK_DELETE || evt.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                // In case of delete auto select only if full match
                isStrict = true;
            }

            Object o = getMatch(searchText, isStrict);
            isFiredBySearch = false;
            if (o != null) {
                setSelectedItem(o);
                return true;
            } else {
                setSelectedItem(null);
            }
            return false;
        }

        private void selectText() {
            try {
                setCaretPosition(searchText.length());
                setSelectionStart(searchText.length());
                setSelectionEnd(getText().length());
            } catch (Exception exception) {
            }
        }

        private Object getMatch(String s, boolean isStrict) {
            for (int i = 0; i < textSearch.getModel().getSize(); i++) {
                String s1 = textSearch.getModel().getElementAt(i).toString();
                if (s1 != null) {
                    if ((!isStrict && s1.toLowerCase().startsWith(s.toLowerCase()))
                            || (isStrict && s1.equalsIgnoreCase(s))) {
                        return textSearch.getModel().getElementAt(i);
                    }
                }
            }
            return null;
        }

        @Override
        public void paint(Graphics g) {
            super.paint(g);
            Graphics2D g2 = (Graphics2D) g;
            String stringToDraw = getWatermarkText();
            int fontHeight = getFontMetrics(getFont()).getHeight();
            int y = (this.getHeight() / 2) + (fontHeight / 2) - getFontMetrics(getFont()).getDescent();

            if (getText() != null && !getText().equals("")) {
                stringToDraw = "";
            }

            if (!stringToDraw.equals("")) {
                AttributedString as = new AttributedString(stringToDraw);
                as.addAttribute(TextAttribute.FONT, this.getFont());
                as.addAttribute(TextAttribute.FOREGROUND, watermarkColor);
                g2.drawString(as.getIterator(), getInsets().left, y);
            }
        }
        private String searchText = "";
        private TextSearch textSearch;
        private SolaTask searchTask = null;
    }

    private class AutoTextFieldEditor extends BasicComboBoxEditor {

        AutoTextFieldEditor() {
            editor = new AutoTextField(TextSearch.this);
        }
    }

    /**
     * Class constructor.
     */
    public TextSearch() {
        isFired = false;
        autoTextFieldEditor = new TextSearch.AutoTextFieldEditor();
        setEditable(true);
        setEditor(autoTextFieldEditor);
        init();
    }

    private void init() {
        ComboBoxUI cui = (ComboBoxUI) UIManager.getUI(this);

        if (cui.getClass().isAssignableFrom(SynthComboBoxUI.class)) {
            this.setUI(
                    new SynthComboBoxUI() {

                        @Override
                        protected JButton createArrowButton() {
                            return getInvisibleButton();
                        }

                        @Override
                        public void configureArrowButton() {
                        }
                    });
        } else {
            this.setUI(new BasicComboBoxUI() {

                @Override
                protected JButton createArrowButton() {
                    return getInvisibleButton();
                }

                @Override
                public void configureArrowButton() {
                }
            });
        }
        setBorder(null);
        addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if(!isFiredBySearch){
                    firePropertyChange(ITEM_SELECTED, lastSelectedItem, getSelectedObject());
                    lastSelectedItem = getSelectedObject();
                }
            }
        });
    }

    private JButton getInvisibleButton() {
        JButton button = new JButton() {

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(0, 0);
            }

            @Override
            public int getWidth() {
                return 0;
            }

            @Override
            public void setBounds(int x, int y, int width, int height) {
            }

            @Override
            public Insets getInsets() {
                return new Insets(0, 0, 0, 0);
            }

            @Override
            public void setBorder(Border border) {
                super.setBorder(null);
            }
        };
        button.setBorder(null);
        return button;
    }

    /**
     * Sets data source for combobox.
     */
    public void setDataList(java.util.List list) {
        setModel(new DefaultComboBoxModel(list.toArray()));
    }

    /**
     * Returns minimum length of the search string to initiate searching.
     */
    public int getMinSearchStringLength() {
        return minSearchStringLength;
    }

    /**
     * Set minimum length of the search string to initiate searching.
     */
    public void setMinSearchStringLength(int minSearchStringLength) {
        if (minSearchStringLength < 1) {
            minSearchStringLength = 1;
        }
        this.minSearchStringLength = minSearchStringLength;
    }

    /**
     * Searches for entered text and populate combobox items.
     */
    public void search(String searchText) {
    }

    /**
     * Returns selected object, comparing search text with model items.
     */
    public Object getSelectedObject() {
        if (getSelectedItem() == null || getSelectedItem().toString().equals("")) {
            return null;
        }

        String text = getSelectedItem().toString();

        for (int i = 0; i < getModel().getSize(); i++) {
            String s1 = getModel().getElementAt(i).toString();
            if (s1 != null) {
                if (s1.equalsIgnoreCase(text)) {
                    return getModel().getElementAt(i);
                }
            }
        }
        return null;
    }

    /**
     * Returns progress message to show when search runs as a SolaTask
     */
    public String getProgessMessage() {
        return progessMessage;
    }

    /**
     * Sets progress message to show when search runs as a SolaTask
     */
    public void setProgessMessage(String progessMessage) {
        this.progessMessage = progessMessage;
    }

    /**
     * Returns true is search function should be run as a SolaTask
     */
    public boolean isRunAsTask() {
        return runAsTask;
    }

    /**
     * Sets flag whether to run search function as a SolaTask
     */
    public void setRunAsTask(boolean runAsTask) {
        this.runAsTask = runAsTask;
    }

    /**
     * Returns flag indicating whether to select first matching item in the list
     * or not.
     */
    public boolean isAutoSelect() {
        return autoSelect;
    }

    /**
     * Sets flag indicating whether to select first matching item in the list or
     * not.
     */
    public void setAutoSelect(boolean autoSelect) {
        this.autoSelect = autoSelect;
    }

    /**
     * Returns watermark text.
     */
    public String getWatermarkText() {
        return watermarkText;
    }

    /**
     * Sets watermark text.
     */
    public void setWatermarkText(String watermark) {
        if (watermark == null) {
            this.watermarkText = "";
        } else {
            this.watermarkText = watermark;
        }
        autoTextFieldEditor.getEditorComponent().repaint();
    }

    /**
     * Returns watermark text color.
     */
    public Color getWatermarkColor() {
        return watermarkColor;
    }

    /**
     * Sets watermark text color.
     */
    public void setWatermarkColor(Color watermarkColor) {
        this.watermarkColor = watermarkColor;
        autoTextFieldEditor.getEditorComponent().repaint();
    }

    @Override
    public void setPopupVisible(boolean v) {
        if (getModel().getSize() > 0) {
            super.setPopupVisible(v);
        }
    }
    
    @Override
    protected void fireActionEvent() {
        if (!isFired) {
            super.fireActionEvent();
        }
    }
    
    public static final String ITEM_SELECTED = "ItemSelected";
    private TextSearch.AutoTextFieldEditor autoTextFieldEditor;
    private boolean isFired;
    private Object lastSelectedItem;
    private boolean isFiredBySearch = false;
    private int minSearchStringLength = 1;
    private boolean runAsTask = true;
    private String progessMessage;
    private boolean autoSelect = true;
    private String watermarkText = "";
    private Color watermarkColor = new java.awt.Color(100, 100, 100);
}

