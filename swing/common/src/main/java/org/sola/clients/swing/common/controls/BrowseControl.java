/**
 * ******************************************************************************************
 * Copyright (C) 2014 - Food and Agriculture Organization of the United Nations (FAO).
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
package org.sola.clients.swing.common.controls;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.font.TextAttribute;
import java.awt.geom.Rectangle2D;
import java.text.AttributedString;
import javax.swing.*;
import org.sola.clients.swing.common.LafManager;

/**
 * Browse control has view of text field with browse button to pickup values.
 * Once there is a value, it could be removed by clicking delete button, or
 * opened by clicking on the text in form of hyperlink.
 */
public class BrowseControl extends JTextField {

    private Color color;
    private String text;
    private Rectangle textBox;
    private Rectangle deleteButtonBox;
    private Rectangle browseBtnBox;
    private final int leftInset = 3;
    private final int btnGap = 5;
    private final int browseBtnWidth = 23;
    private final int browseBtnGap = 2;
    private Icon deleteButtonIcon;
    private Color browseBtnOffColor;
    private Color browseBtnOnColor;
    private Color browseBtnClickColor;
    private Color browseBtnCurrentColor;
    private boolean displayDeleteButton;
    private boolean displayBrowseButton;
    private boolean underline;
    private String trailer;
    private String toolTip;
    private String deleteButtonTooltip;
    private String browseButtonTooltip;
    private boolean enabled;

    /**
     * Class constructor. Initializes default values.
     */
    public BrowseControl() {
        super();
        this.setBackground(UIManager.getColor(LafManager.getInstance().getTxtFieldBg()));
        this.setBorder(javax.swing.BorderFactory.createLineBorder(UIManager.getColor(LafManager.getInstance().getBtnDarkShadow())));
        this.setPreferredSize(new Dimension(185, 23));

        deleteButtonIcon = new ImageIcon(BrowseControl.class.getResource("/org/sola/clients/swing/common/controls/resources/delete_icon.gif"));
        textBox = new Rectangle();
        deleteButtonBox = new Rectangle();
        browseBtnBox = new Rectangle();
        browseBtnOffColor = UIManager.getColor(LafManager.getInstance().getBtnBackground());
        browseBtnOnColor = new Color(150, 150, 150);
        browseBtnClickColor = UIManager.getColor(LafManager.getInstance().getBtnShadow());


        browseBtnCurrentColor = browseBtnOffColor;
        color = new java.awt.Color(0, 153, 255);
        text = "";
        displayBrowseButton = true;
        displayDeleteButton = true;
        underline = true;
        trailer = " ...";
        deleteButtonTooltip = "Remove";
        browseButtonTooltip = "Browse...";
        enabled = true;

        this.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {

            @Override
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                mouseMovedHandler(evt);
            }
        });

        this.addMouseListener(new MouseListener() {

            @Override
            public void mouseClicked(MouseEvent e) {
                mouseClickedHandler(e);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                mousePressedHandler(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
                mouseExitedHandler(e);
            }
        });
    }

    /**
     * Handles mouse pressed event.
     */
    private void mousePressedHandler(MouseEvent e) {
        if (browseBtnCurrentColor != browseBtnClickColor && browseBtnBox.contains(e.getPoint())) {
            browseBtnCurrentColor = browseBtnClickColor;
            drawBrowseButton((Graphics2D) this.getGraphics());
        }
    }

    /**
     * Handles mouse exit out of control.
     */
    private void mouseExitedHandler(MouseEvent e) {
        // Draw default settings
        if (browseBtnCurrentColor != browseBtnOffColor) {
            browseBtnCurrentColor = browseBtnOffColor;
            drawBrowseButton((Graphics2D) this.getGraphics());
        }
        this.setToolTipText(null);
    }

    /**
     * Handles mouse clicks on the control.
     */
    private void mouseClickedHandler(MouseEvent e) {
        // Define the click type
        if (textBox.contains(e.getPoint())) {
            fireTextClickEvent(e);
        } else if (deleteButtonBox.contains(e.getPoint())) {
            fireDeleteButtonClickEvent(e);
        } else if (browseBtnBox.contains(e.getPoint())) {
            fireBrowseButtonClickEvent(e);
        } else {
            fireControlClickEvent(e);
        }
    }

    /**
     * Handles mouse moves.
     */
    private void mouseMovedHandler(java.awt.event.MouseEvent evt) {
        if (browseBtnBox.contains(evt.getPoint())) {
            if (underline) {
                setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
            }
            if (enabled) {
                this.setToolTipText(browseButtonTooltip);
                if (browseBtnCurrentColor != browseBtnOnColor) {
                    browseBtnCurrentColor = browseBtnOnColor;
                    drawBrowseButton((Graphics2D) this.getGraphics());
                }
            }
        } else if (deleteButtonBox.contains(evt.getPoint())) {
            if (underline) {
                setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
            }
            if (enabled) {
                this.setToolTipText(deleteButtonTooltip);
            }
        } else {
            if (browseBtnCurrentColor != browseBtnOffColor) {
                browseBtnCurrentColor = browseBtnOffColor;
                drawBrowseButton((Graphics2D) this.getGraphics());
            }
            if (textBox.contains(evt.getPoint())) {
                if (underline) {
                    this.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
                }
                this.setToolTipText(toolTip);
            } else {
                if (underline) {
                    setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
                }
                this.setToolTipText(null);
            }
        }
    }

    @Override
    /**
     * Drawing logic of the control.
     */
    public void paint(Graphics g) {
        super.paint(g);

        // Clear boxes
        textBox = new Rectangle();
        deleteButtonBox = new Rectangle();
        browseBtnBox = new Rectangle();

        Graphics2D g2 = (Graphics2D) g;

        if (text != null && text.length() > 1) {
            // Draw string
            String displayText = text;
            int fontHeight = getFontMetrics(getFont()).getHeight();
            int stringWidth = getFontMetrics(getFont()).stringWidth(displayText);
            int y = (this.getHeight() / 2) + (fontHeight / 2) - getFontMetrics(getFont()).getDescent();
            int x = leftInset;

            if (displayDeleteButton) {
                x = x + btnGap + deleteButtonIcon.getIconWidth();
            }

            // Calculate text box size
            int boxWidth = x + stringWidth;
            int accessableWidth = this.getWidth();
            if (displayBrowseButton) {
                accessableWidth = accessableWidth - browseBtnGap - browseBtnWidth;
            }

            toolTip = null;

            if (boxWidth > accessableWidth) {
                accessableWidth = accessableWidth - x - getFontMetrics(getFont()).stringWidth(trailer);
                int growingWidth = 0;
                int i = 0;

                for (i = 0; i < text.length(); i++) {
                    growingWidth += getFontMetrics(getFont()).charWidth(text.charAt(i));
                    if (growingWidth > accessableWidth) {
                        break;
                    }
                }

                if (i > 0) {
                    displayText = displayText.substring(0, i - 1);
                    displayText = displayText + trailer;
                    stringWidth = getFontMetrics(getFont()).stringWidth(displayText);
                    toolTip = text;
                }
            }

            textBox = new Rectangle(x, y - fontHeight, stringWidth, fontHeight + 1);

            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            AttributedString as = new AttributedString(displayText);
            as.addAttribute(TextAttribute.FONT, this.getFont());
            as.addAttribute(TextAttribute.FOREGROUND, color);
            if (underline) {
                as.addAttribute(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
            }
            g2.drawString(as.getIterator(), x, y);

            drawDeleteButton(g2);
        }
        drawBrowseButton(g2);
    }

    /**
     * Draws browse button.
     */
    private void drawBrowseButton(Graphics2D g) {
        if (!displayBrowseButton) {
            return;
        }

        int x = this.getWidth() - browseBtnWidth;

        Rectangle2D btnRect = new Rectangle2D.Double(x, 1, browseBtnWidth - 1, this.getHeight() - 2);
        Rectangle2D btnLeftGapRect = new Rectangle2D.Double(x - browseBtnGap, 1, browseBtnGap, this.getHeight() - 2);
        browseBtnBox = new Rectangle(btnLeftGapRect.getBounds().x,
                btnLeftGapRect.getBounds().y,
                btnRect.getBounds().width + btnLeftGapRect.getBounds().width,
                btnRect.getBounds().height);

        Painter btnPainter;

        if (browseBtnCurrentColor == browseBtnOnColor) {
            btnPainter = (Painter) UIManager.get("Button[Default+MouseOver].backgroundPainter");
        } else if (browseBtnCurrentColor == browseBtnClickColor) {
            btnPainter = (Painter) UIManager.get("Button[Default+Focused+Pressed].backgroundPainter");
        } else {
            btnPainter = (Painter) UIManager.get("Button[Default].backgroundPainter");
        }

        if (btnPainter != null) {
            btnPainter.paint((Graphics2D) g.create(x, 0, browseBtnWidth, this.getHeight()), null, browseBtnWidth, this.getHeight());
        } else {
            // Draw rectangle
            g.setPaint(browseBtnCurrentColor);

            g.fill(btnRect);

            // Draw vertical line as a left border of button
            g.setPaint(Color.LIGHT_GRAY);
            g.drawLine(btnRect.getBounds().x, btnRect.getBounds().y, btnRect.getBounds().x,
                    btnRect.getBounds().y + btnRect.getBounds().height);
        }

        // Draw label on the button
        Font labelFont = new Font("Tahoma", Font.PLAIN, 11);
        String labelText = "...";
        int textY = (btnRect.getBounds().height / 2) + (getFontMetrics(labelFont).getHeight() / 2) - getFontMetrics(getFont()).getDescent();
        int textX = btnRect.getBounds().x + (btnRect.getBounds().width / 2 - getFontMetrics(labelFont).stringWidth(labelText) / 2);

        g.setPaint(UIManager.getColor(LafManager.getInstance().getBtnForeground()));
        AttributedString as = new AttributedString(labelText);
        as.addAttribute(TextAttribute.FONT, labelFont);
        g.drawString(as.getIterator(), textX, textY);
    }

    /**
     * Draws delete button.
     */
    private void drawDeleteButton(Graphics2D g) {
        if (!displayDeleteButton) {
            return;
        }

        int y1 = (this.getHeight() / 2) - (deleteButtonIcon.getIconHeight() / 2);
        deleteButtonBox = new Rectangle(leftInset, y1, deleteButtonIcon.getIconWidth(), deleteButtonIcon.getIconHeight());
        g.drawImage(((ImageIcon) deleteButtonIcon).getImage(), leftInset, y1, null);
    }

    /**
     * Returns displayed string.
     */
    @Override
    public String getText() {
        return text;
    }

    /**
     * Sets displayed string.
     */
    @Override
    public void setText(String text) {
        this.text = text;
        this.repaint();
    }

    /**
     * Returns text color.
     */
    public Color getColor() {
        return color;
    }

    /**
     * Sets text color.
     */
    public void setColor(Color color) {
        this.color = color;
        this.repaint();
    }

    /**
     * Returns true if browse button is displayed and false if not.
     */
    public boolean isDisplayBrowseButton() {
        return displayBrowseButton;
    }

    /**
     * Sets browse button visibility.
     */
    public void setDisplayBrowseButton(boolean displayBrowseButton) {
        this.displayBrowseButton = displayBrowseButton;
        this.repaint();
    }

    /**
     * Returns true if delete button is displayed and false if not.
     */
    public boolean isDisplayDeleteButton() {
        return displayDeleteButton;
    }

    /**
     * Sets delete button visibility.
     */
    public void setDisplayDeleteButton(boolean displayDeleteButton) {
        this.displayDeleteButton = displayDeleteButton;
        this.repaint();
    }

    /**
     * Returns true if underline attribute is on and false if it is off.
     */
    public boolean isUnderline() {
        return underline;
    }

    /**
     * Sets underline attribute for the displayed string.
     */
    public void setUnderline(boolean underline) {
        this.underline = underline;
        this.repaint();
    }

    /**
     * Returns icon of delete button.
     */
    public Icon getDeleteButtonIcon() {
        return deleteButtonIcon;
    }

    /**
     * Sets icon for delete button.
     */
    public void setDeleteButtonIcon(Icon icon) {
        this.deleteButtonIcon = icon;
    }

    /**
     * Returns browse button tooltip.
     */
    public String getBrowseButtonTooltip() {
        return browseButtonTooltip;
    }

    /**
     * Sets browse button tooltip.
     */
    public void setBrowseButtonTooltip(String browseButtonTooltip) {
        this.browseButtonTooltip = browseButtonTooltip;
    }

    /**
     * Returns delete button tooltip.
     */
    public String getDeleteButtonTooltip() {
        return deleteButtonTooltip;
    }

    /**
     * Sets delete button tooltip.
     */
    public void setDeleteButtonTooltip(String deleteButtonTooltip) {
        this.deleteButtonTooltip = deleteButtonTooltip;
    }

    @Override
    public void setEditable(boolean editable) {
        super.setEditable(false);
    }

    public boolean getEditable() {
        return false;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean getEnabled() {
        return enabled;
    }

    // Registers event listeners
    public void addBrowseControlEventListener(BrowseControlListener listener) {
        listenerList.add(BrowseControlListener.class, listener);
    }

    // Unregisters event listeners
    public void removeBrowseControlListener(BrowseControlListener listener) {
        listenerList.remove(BrowseControlListener.class, listener);
    }

    // Fires delete button click event
    private void fireDeleteButtonClickEvent(MouseEvent evt) {
        if (enabled) {
            Object[] listeners = listenerList.getListenerList();
            for (int i = 0; i < listeners.length; i += 2) {
                if (listeners[i] == BrowseControlListener.class) {
                    ((BrowseControlListener) listeners[i + 1]).deleteButtonClicked(evt);
                }
            }
        }
    }

    // Fires browse button click event
    private void fireBrowseButtonClickEvent(MouseEvent evt) {
        if (enabled) {
            Object[] listeners = listenerList.getListenerList();
            for (int i = 0; i < listeners.length; i += 2) {
                if (listeners[i] == BrowseControlListener.class) {
                    ((BrowseControlListener) listeners[i + 1]).browseButtonClicked(evt);
                }
            }
        }
    }

    // Fires control click event
    private void fireControlClickEvent(MouseEvent evt) {
        Object[] listeners = listenerList.getListenerList();
        for (int i = 0; i < listeners.length; i += 2) {
            if (listeners[i] == BrowseControlListener.class) {
                ((BrowseControlListener) listeners[i + 1]).controlClicked(evt);
            }
        }
    }

    // Fires text value click event
    private void fireTextClickEvent(MouseEvent evt) {
        Object[] listeners = listenerList.getListenerList();
        for (int i = 0; i < listeners.length; i += 2) {
            if (listeners[i] == BrowseControlListener.class) {
                ((BrowseControlListener) listeners[i + 1]).textClicked(evt);
            }
        }
    }
}
