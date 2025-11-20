package util;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;

public class ImageProcessor {
    
    private static final String ASCII_CHARS = "@%#*+=-:. ";
    private static final int MAX_WIDTH = 100;
    
    public static String convertToAscii(String imagePath) throws IOException {
        BufferedImage image = ImageIO.read(new File(imagePath));
        
        // Resize image
        int width = Math.min(image.getWidth(), MAX_WIDTH);
        int height = (image.getHeight() * width) / image.getWidth() / 2; // Adjust for char ratio
        
        BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = resized.createGraphics();
        g.drawImage(image, 0, 0, width, height, null);
        g.dispose();
        
        // Convert to ASCII
        StringBuilder ascii = new StringBuilder();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = resized.getRGB(x, y);
                int r = (rgb >> 16) & 0xFF;
                int g1 = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;
                int gray = (int) (0.299 * r + 0.587 * g1 + 0.114 * b);
                
                int charIndex = (gray * (ASCII_CHARS.length() - 1)) / 255;
                ascii.append(ASCII_CHARS.charAt(charIndex));
            }
            ascii.append("\n");
        }
        
        return ascii.toString();
    }
    
    public static void saveAsciiToFile(String ascii, String filePath) throws IOException {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(ascii);
        }
    }
}