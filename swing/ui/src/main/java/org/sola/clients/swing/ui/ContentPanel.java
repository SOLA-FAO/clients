package org.sola.clients.swing.ui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JPanel;

/**
 * Basic class for the panel forms, used to display application logic.
 */
public class ContentPanel extends JPanel {

    private MainContentPanel mainContentPanel;
    private HeaderPanel headerPanel;
    private PropertyChangeListener headerPanelListener;
    private boolean closeOnHide = false;

    public ContentPanel() {
        super();
        headerPanelListener = new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                handleHeaderPanelPropertyChanges(evt);
            }
        };
    }

    public HeaderPanel getHeaderPanel() {
        return headerPanel;
    }

    public void setHeaderPanel(HeaderPanel headerPanel) {
        if (this.headerPanel != null) {
            this.headerPanel.removePropertyChangeListener(headerPanelListener);
        }
        this.headerPanel = headerPanel;
        try {
            this.headerPanel.addPropertyChangeListener(headerPanelListener);
        } catch (Exception e) {
        }
    }

    public MainContentPanel getMainContentPanel() {
        return mainContentPanel;
    }

    public void setMainContentPanel(MainContentPanel mainContentPanel) {
        this.mainContentPanel = mainContentPanel;
    }

    public boolean isCloseOnHide() {
        return closeOnHide;
    }

    public void setCloseOnHide(boolean closeOnHide) {
        this.closeOnHide = closeOnHide;
    }

    // METHODS
    /**
     * Listens to the {@link HeaderPanel} property changes to trap control
     * buttons click.
     */
    private void handleHeaderPanelPropertyChanges(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(HeaderPanel.CLOSE_BUTTON_CLICKED)) {
            if (panelClosing()) {
                close();
            }
        }
    }

    /**
     * Close the panel from the {@link MainContentPanel}
     */
    public void close() {
        if (getMainContentPanel() != null) {
            getMainContentPanel().closePanel(this);
        } else {
            firePropertyChange(HeaderPanel.CLOSE_BUTTON_CLICKED, false, true);
        }
    }

    /**
     * This method is called each time when panel is added to the main content
     * panel.
     */
    protected void panelAdded() {
    }

    /**
     * This method is called each time when panel is going to be closed. If true
     * is returned, panel will be closed, if false, close procedure will be
     * interrupted.
     */
    protected boolean panelClosing() {
        return true;
    }

    /**
     * This method is called each time when panel is shown on the main content
     * panel.
     */
    protected void panelShown() {
    }
}
