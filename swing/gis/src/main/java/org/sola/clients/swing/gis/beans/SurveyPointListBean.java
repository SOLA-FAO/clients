/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sola.clients.swing.gis.beans;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import org.jdesktop.observablecollections.ObservableList;
import org.jdesktop.observablecollections.ObservableListListener;
import org.sola.clients.beans.controls.SolaObservableList;

/**
 * The list bean is used to encapsulate the list initialization and managing of the survey point
 * lists.
 *
 * @author Elton Manoku
 */
public final class SurveyPointListBean extends AbstractListSpatialBean {

    private static String MEAN_PROPERTY = "mean";
    private static String STANDARD_DEVIATION_PROPERTY = "standardDeviation";
    private Double mean = 0.0;
    private Double standardDeviation = 0.0;
    private PropertyChangeListener beanPropertyChangeListener;

    public SurveyPointListBean() {
        super();
        initializeListBeanEvents();
    }

    @Override
    protected SolaObservableList initializeBeanList() {
        return new SolaObservableList<SurveyPointBean>();
    }

    @Override
    public SolaObservableList<SurveyPointBean> getBeanList() {
        return (SolaObservableList<SurveyPointBean>) super.getBeanList();
    }

    /**
     * It initializes the events on the list of the beans. The events are needed to calculate the
     * mean and standard deviations.
     */
    private void initializeListBeanEvents() {
        ObservableListListener listListener = new ObservableListListener() {

            @Override
            public void listElementsAdded(ObservableList ol, int i, int i1) {
                newBeansAdded(ol, i, i1);
            }

            @Override
            public void listElementsRemoved(ObservableList ol, int i, List list) {
                setMean();
                setStandardDeviation();
            }

            @Override
            public void listElementReplaced(ObservableList ol, int i, Object o) {
            }

            @Override
            public void listElementPropertyChanged(ObservableList ol, int i) {
            }
        };

        ((SolaObservableList) this.getBeanList()).addObservableListListener(listListener);

        this.beanPropertyChangeListener = new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent pce) {
                beanChanged((SpatialBean) pce.getSource(), pce.getPropertyName(),
                        pce.getNewValue(), pce.getOldValue());
            }
        };
    }

    private void newBeansAdded(ObservableList fromList, int startIndex, int total) {
        int endIndex = startIndex + total;
        for (int elementIndex = startIndex; elementIndex < endIndex; elementIndex++) {
            SpatialBean bean = (SpatialBean) fromList.get(elementIndex);
            bean.addPropertyChangeListener(this.beanPropertyChangeListener);
        }
        setMean();
        setStandardDeviation();
    }

    private void beanChanged(SpatialBean bean, String propertyName,
            Object newValue, Object oldValue) {
        if (propertyName.equals(SurveyPointBean.LINKED_FOR_FEATURE_PROPERTY)
                || propertyName.equals(SurveyPointBean.SHIFT_DISTANCE_PROPERTY)) {
            setMean();
            setStandardDeviation();
        }
    }

    /**
     * Gets the mean of the deviations of survey points from their original location. It is
     * calculated only for linked points
     *
     * @return
     */
    public Double getMean() {
        return this.mean;
    }

    /**
     * Sets the mean of all points. It fires the event of change.
     */
    private void setMean() {
        Double oldValue = this.mean;
        Double deltaX = 0.0, deltaY = 0.0;
        int totalPoints = 0;
        for (SurveyPointBean bean : this.getBeanList()) {
            if (!bean.isLinked()) {
                continue;
            }
            totalPoints++;
            deltaX += bean.getDeltaX();
            deltaY += bean.getDeltaY();
        }
        if (totalPoints > 0) {
            this.mean = Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2)) / totalPoints;
        } else {
            this.mean = 0.0;
        }
        propertySupport.firePropertyChange(MEAN_PROPERTY, oldValue, this.mean);
    }

    /**
     * Gets the standard deviation of survey points from their original location. It is calculated
     * only for linked points
     *
     * @return
     */
    public Double getStandardDeviation() {
        return standardDeviation;
    }

    /**
     * Sets the standard deviation. It fires the event of change
     */
    private void setStandardDeviation() {
        Double oldValue = this.standardDeviation;
        Double totalShift = 0.0;
        Integer totalPoints = 0;
        for (SurveyPointBean bean : this.getBeanList()) {
            if (!bean.isLinked()) {
                continue;
            }
            totalShift += Math.pow(bean.getShiftDistance() - this.getMean(), 2);
            totalPoints++;
        }
        if (totalPoints > 0) {
            this.standardDeviation = Math.sqrt(totalShift / totalPoints);
        } else {
            this.standardDeviation = 0.0;
        }
        propertySupport.firePropertyChange(
                STANDARD_DEVIATION_PROPERTY, oldValue, this.standardDeviation);
    }
}
