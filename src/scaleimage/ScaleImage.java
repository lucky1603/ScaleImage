/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scaleimage;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author Sinisa Ristic
 */
public class ScaleImage {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)  {
        // TODO code application logic here
        String format = args[0];
        String dimension = args[1];
        int dimensionValue = Integer.parseInt(args[2]);
        
        String dir = System.getProperty("user.dir");
        String outputDirPath = dir + "\\male";
        File outputDir = new File(outputDirPath);
        outputDir.mkdir();
        
        File[] files = new File(dir).listFiles();
        for(File item : files) {
            if(!item.getName().toLowerCase().contains(format))
                continue;
            //System.out.println(item.getName());
            System.out.println(item.getAbsoluteFile());
            String path = item.getAbsoluteFile().toString();
            String outputPath = outputDirPath + "\\" + item.getName();
            
            try {
                BufferedImage bimg = ImageIO.read(new File(path));
                
                int width = bimg.getWidth();
                int height = bimg.getHeight();
                double ratio = (double) width / height;
                int targetWidth = width, targetHeight = height;
                                
                if(dimension.equals("width")) {
                    targetWidth = dimensionValue;
                    targetHeight = (int) (targetWidth / ratio);
                } else {
                    targetHeight = dimensionValue;
                    targetWidth = (int) (targetHeight * ratio);
                }
                
                BufferedImage outputImage = scale(bimg, targetWidth, targetHeight);
                File outputFile = new File(outputPath);
                ImageIO.write(outputImage, "jpg", outputFile);
                
            } catch (IOException ex) {
                Logger.getLogger(ScaleImage.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    }
    
    public static BufferedImage scale(BufferedImage img, int targetWidth, int targetHeight) {

        int type = (img.getTransparency() == Transparency.OPAQUE) ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
        BufferedImage ret = img;
        BufferedImage scratchImage = null;
        Graphics2D g2 = null;

        int w = img.getWidth();
        int h = img.getHeight();

        int prevW = w;
        int prevH = h;

        do {
            if (w > targetWidth) {
                w /= 2;
                w = (w < targetWidth) ? targetWidth : w;
            }

            if (h > targetHeight) {
                h /= 2;
                h = (h < targetHeight) ? targetHeight : h;
            }

            if (scratchImage == null) {
                scratchImage = new BufferedImage(w, h, type);
                g2 = scratchImage.createGraphics();
            }

            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.drawImage(ret, 0, 0, w, h, 0, 0, prevW, prevH, null);

            prevW = w;
            prevH = h;
            ret = scratchImage;
        } while (w != targetWidth || h != targetHeight);

        if (g2 != null) {
            g2.dispose();
        }

        if (targetWidth != ret.getWidth() || targetHeight != ret.getHeight()) {
            scratchImage = new BufferedImage(targetWidth, targetHeight, type);
            g2 = scratchImage.createGraphics();
            g2.drawImage(ret, 0, 0, null);
            g2.dispose();
            ret = scratchImage;
        }

        return ret;
    }
    
}
