package com.profile.api.fileStore.service;

import com.profile.api.common.exception.ImageProcessingException;
import com.profile.api.common.config.CentralizedLoggingFilter;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;

@Service
public class ImageProcessor {

    private static final Logger log = CentralizedLoggingFilter.getLogger(ImageProcessor.class);
    private static final String WEBP_MIME_TYPE = "image/webp";

    private static final long MAX_ORIGINAL_SIZE_BYTES = 10 * 1024 * 1024; // 10 MB
    private static final long TARGET_OUTPUT_SIZE_BYTES = 100 * 1024;       // 100 KB
    private static final int INITIAL_WEBP_QUALITY = 80;
    private static final int MINIMUM_WEBP_QUALITY = 40;
    private static final int MAX_DIMENSION_WIDTH = 4096;
    private static final int MAX_DIMENSION_HEIGHT = 4096;

    public ProcessedImage process(byte[] imageBytes, String originalFilename) {
        validateSize(imageBytes, originalFilename);
        BufferedImage decoded = decode(imageBytes, originalFilename);
        BufferedImage resized = resizeIfNecessary(decoded);
        return compressToWebp(resized, originalFilename);
    }

    private void validateSize(byte[] imageBytes, String filename) {
        if (imageBytes.length > MAX_ORIGINAL_SIZE_BYTES) {
            long maxKb = MAX_ORIGINAL_SIZE_BYTES / 1024;
            throw new ImageProcessingException(
                    "Image size exceeds maximum of " + maxKb + " KB: " + filename);
        }
    }

    private BufferedImage decode(byte[] imageBytes, String filename) {
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(imageBytes);
            BufferedImage image = ImageIO.read(bais);
            if (image == null) {
                throw new ImageProcessingException(
                        "Cannot decode image. File is not a valid image: " + filename);
            }
            return image;
        } catch (IOException e) {
            throw new ImageProcessingException(
                    "Failed to read image: " + filename, e);
        }
    }

    private BufferedImage resizeIfNecessary(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();

        if (width <= MAX_DIMENSION_WIDTH && height <= MAX_DIMENSION_HEIGHT) {
            return image;
        }

        double scaleW = (double) MAX_DIMENSION_WIDTH / width;
        double scaleH = (double) MAX_DIMENSION_HEIGHT / height;
        double scale = Math.min(scaleW, scaleH);

        int newWidth = Math.max((int) (width * scale), 1);
        int newHeight = Math.max((int) (height * scale), 1);

        log.info("Resizing image from {}x{} to {}x{}", width, height, newWidth, newHeight);

        return resizeTo(image, newWidth, newHeight);
    }

    private ProcessedImage compressToWebp(BufferedImage image, String filename) {
        int quality = INITIAL_WEBP_QUALITY;

        while (quality >= MINIMUM_WEBP_QUALITY) {
            byte[] webpBytes = encodeWebp(image, quality);
            log.info("WebP at quality {}: {} bytes", quality, webpBytes.length);

            if (webpBytes.length <= TARGET_OUTPUT_SIZE_BYTES) {
                return new ProcessedImage(webpBytes, WEBP_MIME_TYPE, image.getWidth(), image.getHeight());
            }
            quality -= 5;
        }

        return scaleDownAndRetry(image, filename);
    }

    private ProcessedImage scaleDownAndRetry(BufferedImage image, String filename) {
        int scaleSteps = 0;
        int maxScaleSteps = 5;
        BufferedImage current = image;

        while (scaleSteps < maxScaleSteps) {
            int newWidth = (int) (current.getWidth() * 0.75);
            int newHeight = (int) (current.getHeight() * 0.75);

            if (newWidth < 50 || newHeight < 50) {
                break;
            }

            current = resizeTo(current, newWidth, newHeight);

            int quality = INITIAL_WEBP_QUALITY;

            while (quality >= MINIMUM_WEBP_QUALITY) {
                byte[] webpBytes = encodeWebp(current, quality);
                log.info("Scale step {}, quality {}: {} bytes", scaleSteps + 1, quality, webpBytes.length);

                if (webpBytes.length <= TARGET_OUTPUT_SIZE_BYTES) {
                    return new ProcessedImage(webpBytes, WEBP_MIME_TYPE, current.getWidth(), current.getHeight());
                }
                quality -= 5;
            }

            scaleSteps++;
        }

        throw new ImageProcessingException(
                "Image cannot be reduced to " + (TARGET_OUTPUT_SIZE_BYTES / 1024)
                        + " KB without unacceptable quality loss: " + filename);
    }

    private BufferedImage resizeTo(BufferedImage source, int width, int height) {
        BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D g = resized.createGraphics();
        try {
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.drawImage(source, 0, 0, width, height, null);
        } finally {
            g.dispose();
        }
        return resized;
    }

    private byte[] encodeWebp(BufferedImage image, int quality) {
        Iterator<ImageWriter> writers = ImageIO.getImageWritersByMIMEType(WEBP_MIME_TYPE);
        if (!writers.hasNext()) {
            writers = ImageIO.getImageWritersByFormatName("webp");
        }
        if (!writers.hasNext()) {
            throw new ImageProcessingException("No WebP writer available. Check webp-imageio dependency.");
        }

        ImageWriter writer = writers.next();
        try {
            BufferedImage rgbImage = toRgb(image);

            ImageWriteParam param = writer.getDefaultWriteParam();
            param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);

            String[] types = param.getCompressionTypes();
            if (types != null && types.length > 0) {
                String selectedType = null;
                for (String type : types) {
                    if (type.equalsIgnoreCase("Lossy")) {
                        selectedType = type;
                        break;
                    }
                }
                if (selectedType == null) {
                    selectedType = types[0];
                }
                param.setCompressionType(selectedType);
            }
            param.setCompressionQuality(quality / 100.0f);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageOutputStream imageOutputStream = ImageIO.createImageOutputStream(baos);
            if (imageOutputStream == null) {
                throw new ImageProcessingException("Cannot create image output stream for WebP encoding");
            }

            try {
                writer.setOutput(imageOutputStream);
                writer.write(null, new IIOImage(rgbImage, null, null), param);
                imageOutputStream.flush();
            } finally {
                imageOutputStream.close();
            }

            byte[] result = baos.toByteArray();
            if (result.length == 0) {
                throw new ImageProcessingException("WebP encoder produced empty output at quality " + quality);
            }
            return result;
        } catch (IOException e) {
            throw new ImageProcessingException("Failed to encode WebP at quality " + quality, e);
        } finally {
            writer.dispose();
        }
    }

    private BufferedImage toRgb(BufferedImage source) {
        if (source.getType() == BufferedImage.TYPE_3BYTE_BGR) {
            return source;
        }
        BufferedImage rgb = new BufferedImage(source.getWidth(), source.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D g = rgb.createGraphics();
        try {
            g.drawImage(source, 0, 0, null);
        } finally {
            g.dispose();
        }
        return rgb;
    }

    public record ProcessedImage(byte[] data, String contentType, int width, int height) {}
}
