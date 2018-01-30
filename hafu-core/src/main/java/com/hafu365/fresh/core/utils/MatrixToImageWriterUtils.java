package com.hafu365.fresh.core.utils;

import com.google.zxing.common.BitMatrix;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Zxing方式 生成二维码
 * 二维码的生成需要借助MatrixToImageWriter类，该类是由Google提供的，可以将该类直接拷贝到源码中使用
 * Created by zhaihuilin on 2017/8/22  9:01.
 */
public class MatrixToImageWriterUtils {


    private static final int BLACK = 0xFF000000;
    private static final int WHITE = 0xFFFFFFFF;


    private MatrixToImageWriterUtils(){}

    public  static BufferedImage toBufferedImage(BitMatrix bitMatrix){
         int width=bitMatrix.getWidth();
         int height=bitMatrix.getHeight();
         BufferedImage image=new BufferedImage(width,height,BufferedImage.TYPE_INT_BGR);
         for (int x=0;x<width;x++){
              for (int y=0;y<height;y++){
                  image.setRGB(x,y,bitMatrix.get(x,y)?BLACK :WHITE);
              }
         }

         return image;
    }

    public static  void writeTofile(BitMatrix bitMatrix, String format, File file) throws IOException{

        BufferedImage image=toBufferedImage(bitMatrix);
        boolean flag=ImageIO.write(image,format,file);
        if (!flag){
            throw new IOException("Could not write an image of format " + format + " to " + file);
        }
    }

    public static void writeToStream(BitMatrix bitMatrix, String format, OutputStream stream) throws  IOException{
         BufferedImage image= toBufferedImage(bitMatrix);
         boolean flag=ImageIO.write(image,format,stream);
         if ( !flag){
             throw new IOException("Could not write an image of format " + format);
         }
    }















}
