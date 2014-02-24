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
package org.geotools.swing.extended.util;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Iterator;
import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.event.IIOReadProgressListener;
import javax.imageio.stream.ImageInputStream;
import org.geotools.swing.extended.exception.DirectImageNotValidFileException;

/**
 * Issue #285 Java Heap Error when loading an image for Digitizing
 * Provides image management functions including subsampling of large images
 * without loading them entirely from disk. Required to avoid Java Heap Memory
 * errors when trying to load images with significant pixel depth (e.g. larger
 * than 2000x2000 pixels. Base code sample provided here
 * http://stackoverflow.com/questions/3294388/make-a-bufferedimage-use-less-ram
 *
 * @author soladev
 */
public class ImageUtility {

    /**
     * Will ensure an image does not exceed the maxWidth or maxHeight pixel
     * depth by subsamping the image when it is read from disk. This avoids
     * excessive memory usage when loading an image with significant pixel depth
     * (e.g. greater than 2000x2000 pixels). The aspect ratio of the image is
     * maintained when subsampled.
     *
     * @param inputStream The input stream to retrieve the image from. See
     * ImageIO.createImageInputStream
     * @param maxWidth The max width of the image in pixels
     * @param maxHeight The max height of the image in pixels
     * @param progressListener Optional listener that can be used to track the
     * progress of the image while it is being loaded.
     * @return The subsampled image or the original image if it has a pixel
     * depth less than the specified maxWidth and maxHeight.
     * @throws IOException
     * @throws DirectImageNotValidFileException
     */
    public static BufferedImage subsampleImage(
            ImageInputStream inputStream,
            int maxWidth,
            int maxHeight,
            IIOReadProgressListener progressListener) throws IOException,
            DirectImageNotValidFileException {
        BufferedImage resampledImage;

        Iterator<ImageReader> readers = ImageIO.getImageReaders(inputStream);

        if (!readers.hasNext()) {
            // There is no image reader for the input stream
            throw new DirectImageNotValidFileException();
        }

        // Use the first/default image reader
        ImageReader reader = readers.next();

        ImageReadParam imageReaderParams = reader.getDefaultReadParam();
        reader.setInput(inputStream);
        // Determine subsampling parameters
        Dimension d1 = new Dimension(reader.getWidth(0), reader.getHeight(0));
        Dimension d2 = new Dimension(maxWidth, maxHeight);
        int subsampling = (int) scaleSubsamplingMaintainAspectRatio(d1, d2);
        // Set subsampling parameters
        imageReaderParams.setSourceSubsampling(subsampling, subsampling, 0, 0);

        reader.addIIOReadProgressListener(progressListener);
        resampledImage = reader.read(0, imageReaderParams);
        reader.removeAllIIOReadProgressListeners();

        return resampledImage;
    }

    /**
     * Calculates the subsampling ratio for the image based on the current image
     * dimensions and the target image dimensions.
     *
     * @param d1 Dimensions of the Image
     * @param d2 Target dimensions for the image after scaling.
     * @return
     */
    private static long scaleSubsamplingMaintainAspectRatio(Dimension d1, Dimension d2) {
        long subsampling = 1;

        if (d1.getWidth() > d2.getWidth()) {
            subsampling = Math.round(d1.getWidth() / d2.getWidth());
        } else if (d1.getHeight() > d2.getHeight()) {
            subsampling = Math.round(d1.getHeight() / d2.getHeight());
        }

        return subsampling;
    }
}