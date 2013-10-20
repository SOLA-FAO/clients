package org.sola.clients.swing.common.controls;

import java.awt.event.ItemEvent;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.plaf.basic.BasicComboBoxEditor;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/**
 * Combobox component with list searching feature. 
 * When typing a text, first matching item will be found and selected in the list. 
 */
public class AutoComboBox extends JComboBox {

    private class AutoTextField extends JTextField {

        class AutoDocument extends PlainDocument {

            @Override
            public void replace(int i, int j, String s, AttributeSet attributeset) throws BadLocationException {
                super.remove(i, j);
                insertString(i, s, attributeset);
            }

            @Override
            public void insertString(int i, String s, AttributeSet attributeset) throws BadLocationException {
                if (s == null || "".equals(s)) {
                    return;
                }

                if (autoComboBox.isShowing() && (autoComboBox.hasFocus() || AutoTextField.this.hasFocus())) {
                    autoComboBox.setPopupVisible(true);
                }

                String s1 = getText(0, i);
                Object s2 = getMatch(s1 + s);
                int j = (i + s.length()) - 1;

                if (isStrict && s2 == null) {
                    s2 = getMatch(s1);
                    j--;
                } else if (!isStrict && s2 == null) {
                    super.insertString(i, s, attributeset);
                    return;
                }

                if (autoComboBox != null && s2 != null) {
                    autoComboBox.setSelectedValue(s2);
                }

                super.remove(0, getLength());
                super.insertString(0, s2.toString(), attributeset);
                setSelectionStart(j + 1);
                setSelectionEnd(getLength());
            }

            @Override
            public void remove(int i, int j) throws BadLocationException {
                int k = getSelectionStart();
                if (k > 0) {
                    k--;
                }

                if (autoComboBox.isShowing() && (autoComboBox.hasFocus() || AutoTextField.this.hasFocus())) {
                    autoComboBox.setPopupVisible(true);
                }
                
                Object s = getMatch(getText(0, k));

                if (!isStrict && s == null) {
                    super.remove(i, j);
                } else {
                    super.remove(0, getLength());
                    super.insertString(0, s.toString(), null);
                }

                if (autoComboBox != null && s != null) {
                    autoComboBox.setSelectedValue(s);
                }

                try {
                    setSelectionStart(k);
                    setSelectionEnd(getLength());
                } catch (Exception exception) {
                }
            }
        }

        AutoTextField(AutoComboBox b) {
            super();
            //setBorder(null);
            isCaseSensitive = false;
            isStrict = true;
            autoComboBox = null;
            autoComboBox = b;
            init();
        }

        private void init() {
            setDocument(new AutoTextField.AutoDocument());
            if (isStrict && autoComboBox.getModel().getSelectedItem() != null) {
                setText(autoComboBox.getModel().getSelectedItem().toString());
            }
        }

        private Object getMatch(String s) {
            for (int i = 0; i < autoComboBox.getModel().getSize(); i++) {
                String s1 = autoComboBox.getModel().getElementAt(i).toString();
                if (s1 != null) {
                    if (!isCaseSensitive && s1.toLowerCase().startsWith(s.toLowerCase())) {
                        return autoComboBox.getModel().getElementAt(i);
                    }
                    if (isCaseSensitive && s1.startsWith(s)) {
                        return autoComboBox.getModel().getElementAt(i);
                    }
                }
            }
            return null;
        }

        @Override
        public void replaceSelection(String s) {
            AutoTextField.AutoDocument _lb = (AutoTextField.AutoDocument) getDocument();
            if (_lb != null) {
                try {
                    int i = Math.min(getCaret().getDot(), getCaret().getMark());
                    int j = Math.max(getCaret().getDot(), getCaret().getMark());
                    _lb.replace(i, j - i, s, null);
                } catch (Exception exception) {
                }
            }
        }

        public boolean isCaseSensitive() {
            return isCaseSensitive;
        }

        public void setCaseSensitive(boolean flag) {
            isCaseSensitive = flag;
        }

        public boolean isStrict() {
            return isStrict;
        }

        public void setStrict(boolean flag) {
            isStrict = flag;
        }
        private boolean isCaseSensitive;
        private boolean isStrict;
        private AutoComboBox autoComboBox;
    }

    private class AutoTextFieldEditor extends BasicComboBoxEditor {

        private AutoTextField getAutoTextFieldEditor() {
            return (AutoTextField) editor;
        }

        AutoTextFieldEditor() {
            editor = new AutoTextField(AutoComboBox.this);
        }
    }

    /** Class constructor. */
    public AutoComboBox() {
        isFired = false;
        autoTextFieldEditor = new AutoTextFieldEditor();
        setEditable(true);
        setEditor(autoTextFieldEditor);
        setBorder(null);
    }

    /** Indicates whether list search is case sensitive or not. */
    public boolean isCaseSensitive() {
        return autoTextFieldEditor.getAutoTextFieldEditor().isCaseSensitive();
    }

    /** Sets list search case sensitivity. */
    public void setCaseSensitive(boolean flag) {
        autoTextFieldEditor.getAutoTextFieldEditor().setCaseSensitive(flag);
    }

    /** Indicates whether searched text must match at least one list item. */
    public boolean isStrict() {
        return autoTextFieldEditor.getAutoTextFieldEditor().isStrict();
    }

    /** 
     * Sets searched text to match at least one list item. 
     * If <code>false</code> is set, user can type any text.
     */
    public void setStrict(boolean flag) {
        autoTextFieldEditor.getAutoTextFieldEditor().setStrict(flag);
    }

    /** Sets data source for combobox. */
    public void setDataList(java.util.List list) {
        setModel(new DefaultComboBoxModel(list.toArray()));
    }

    /** Sets selected item in the combobox. */
    void setSelectedValue(Object obj) {
        if (!isFired) {
            isFired = true;
            setSelectedItem(obj);
            fireItemStateChanged(new ItemEvent(this, 701, selectedItemReminder, 1));
            isFired = false;
        }
    }

    @Override
    protected void fireActionEvent() {
        if (!isFired) {
            super.fireActionEvent();
        }
    }
    private AutoTextFieldEditor autoTextFieldEditor;
    private boolean isFired;
}
