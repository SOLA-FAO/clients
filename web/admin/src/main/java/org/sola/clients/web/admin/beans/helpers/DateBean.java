package org.sola.clients.web.admin.beans.helpers;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import org.sola.common.DateUtility;

/**
 * Holds methods and fields to support dates operations
 */
@Named
@ApplicationScoped
public class DateBean {
    private String dateFormat = "dd/MM/yy";
    private String dateFormatForDisplay = "dd/MM/yyyy";
    private String timeFormat = "HH:mm";
    
    public DateBean(){
    }

    public String getDateFormatForDisplay() {
        return dateFormatForDisplay;
    }
    
    /** Returns date pattern for short format. */
    public String getDatePattern(){
        return dateFormat;
    }
    
    /** Returns short date and time string in short format */
    public String getShortDate(Date date){
        return DateUtility.formatDate(date, dateFormatForDisplay);
    }
    
    /** Localized short date */
    public String getShortLocalizedDate(Date date){
        return DateUtility.formatDate(date, dateFormatForDisplay);
    }
    
    /** Returns current date and time in short format. */
    public String getShortDateAndTime(){
        return DateUtility.formatDate(Calendar.getInstance().getTime(), dateFormatForDisplay + " " + timeFormat);
    }
    
    /** Returns date and time string in short format */
    public String getDateTime(Date date){
        return DateUtility.getDateTimeString(date, DateFormat.SHORT, DateFormat.SHORT);
    }
    
    /** Formats date using predefined date format. Formated date includes time */
    public String formatDate(Date date){
        return DateUtility.formatDate(date, dateFormatForDisplay + " " + timeFormat);
    }
}
