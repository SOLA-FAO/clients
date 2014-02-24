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
package org.sola.clients.beans.digitalarchive;

import java.awt.Image;
import java.text.MessageFormat;
import java.util.Date;
import javax.swing.ImageIcon;
import org.sola.clients.beans.AbstractBindingBean;
import org.sola.clients.beans.converters.TypeConverters;
import org.sola.common.FileUtility;
import org.sola.common.NumberUtility;
import org.sola.services.boundary.wsclients.WSManager;
import org.sola.webservices.transferobjects.digitalarchive.FileInfoTO;

/**
 * Represents file object information, stored in the remote folder, excluding
 * binary content. Could be populated from the {@link FileInfoTO} object.<br />
 */
public class FileInfoBean extends AbstractBindingBean {

    public static final String FILE_SIZE_PROPERTY = "fileSize";
    public static final String MODIFICATION_DATE_PROPERTY = "modificationDate";
    public static final String NAME_PROPERTY = "name";
    private long fileSize;
    private Date modificationDate;
    private String name;
    private FileBinaryBean thumbnail;
    private ImageIcon thumbnailIcon;

    public FileInfoBean() {
        super();
    }

    /**
     * Lazy loads thumbnail for the file, represented by this bean.
     */
    public FileBinaryBean getThumbnail() {
        if (thumbnail == null && name != null) {
            String cacheFileName = this.getModificationDate().getTime() + "_"
                    + FileUtility.getFileNameWithoutExtension(getName()) + ".jpg";
            if (!FileUtility.isCached(cacheFileName)) {
                // Try to load thumbnail from the server
                FileInfoTO fileInfoTO = WSManager.getInstance().getDigitalArchive().getFileThumbnail(getName());
                thumbnail = TypeConverters.TransferObjectToBean(fileInfoTO, FileBinaryBean.class, null);
                thumbnail.setContent(FileUtility.getFileBinary(FileUtility.getCachePath() + fileInfoTO.getName()));
            } else {
                // The thumbnail is already cached on the client so load it for display
                thumbnail = TypeConverters.TransferObjectToBean(this, FileBinaryBean.class, null);
                thumbnail.setName(cacheFileName);
                thumbnail.setContent(FileUtility.getFileBinary(FileUtility.getCachePath() + cacheFileName));
            }
        }
        return thumbnail;
    }

    /**
     * Creates and returns thumbnail icon out of loaded thumbnail file.
     */
    public ImageIcon getThumbnailIcon(int width, int height) {
        if (getThumbnail() != null && (thumbnailIcon == null
                || thumbnailIcon.getIconWidth() != width || thumbnailIcon.getIconHeight() != height)) {
            thumbnailIcon = new ImageIcon(getThumbnail().getContent());

            // Scale the icon to fit the display area based on its longest dimension
            Double scaledHeight = new Double(height);
            Double scaledWidth = new Double(width);
            if (thumbnailIcon.getIconWidth() > thumbnailIcon.getIconHeight()) {
                // The width is the longest dimension so scale the height
                Double scaleFactor = scaledWidth / new Double(thumbnailIcon.getIconWidth());
                scaledHeight = scaleFactor * new Double(thumbnailIcon.getIconHeight());
            } else {
                // The height is the longest dimension so scale the width
                Double scaleFactor = scaledHeight / new Double(thumbnailIcon.getIconHeight());
                scaledWidth = scaleFactor * new Double(thumbnailIcon.getIconWidth());
            }
            thumbnailIcon = new ImageIcon(thumbnailIcon.getImage().getScaledInstance(
                    scaledWidth.intValue(), scaledHeight.intValue(), Image.SCALE_SMOOTH));
        }
        return thumbnailIcon;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long value) {
        long old = fileSize;
        fileSize = value;
        propertySupport.firePropertyChange(FILE_SIZE_PROPERTY, old, value);
    }

    /**
     * Formats file size in a convenient form, using KB, MB and GB.
     */
    public String getFormattedFileSize() {
        String result = "{0}";
        double tmpFileSize = fileSize;

        // Bytes
        if (fileSize > 0 && fileSize < 1024) {
            result = "{0} bytes";
        }

        // Kilobytes
        if (fileSize >= 1024 && fileSize < 1048576) {
            tmpFileSize = NumberUtility.roundDouble((double) fileSize / 1024, 2);
            result = "{0} KB";
        }

        // Megabytes
        if (fileSize >= 1048576 && fileSize < 1073741824) {
            result = "{0} MB";
            tmpFileSize = NumberUtility.roundDouble((double) fileSize / 1048576, 2);
        }

        // Gigabytes
        if (fileSize >= 1073741824) {
            result = "{0} GB";
            tmpFileSize = NumberUtility.roundDouble((double) fileSize / 1073741824, 2);
        }

        MessageFormat formatter = new MessageFormat(result);
        result = formatter.format(new Object[]{tmpFileSize});
        return result;
    }

    public Date getModificationDate() {
        return modificationDate;
    }

    public void setModificationDate(Date value) {
        Date old = modificationDate;
        modificationDate = value;
        propertySupport.firePropertyChange(MODIFICATION_DATE_PROPERTY, old, value);
    }

    public String getName() {
        return name;
    }

    public void setName(String value) {
        String old = name;
        name = value;
        propertySupport.firePropertyChange(NAME_PROPERTY, old, value);
    }
}
