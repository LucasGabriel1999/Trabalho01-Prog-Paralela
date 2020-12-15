package com;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Date;

public class Main {
    public static void writeImage(BufferedImage image) throws IOException{
        Date date = new Date();
        String imagePath = "src/imagensResultantes/imagem-convertida-" + date.toString() + ".png";

        ImageIO.write(image, "PNG", new File(imagePath));
    }

    public static void main(String[] args) throws IOException {
        String path = "src/image.jpg";

        BufferedImage image = ImageIO.read(new File(path));

        int height = image.getHeight();
        int width = image.getWidth();


        Converter quadranteA = new Converter(image, height/2, height, 0, width/2 );
        Converter quadranteB = new Converter(image, height/2, height, width/2, width );
        Converter quadranteC = new Converter(image, 0, height/2, 0, width/2);
        Converter quadranteD = new Converter(image,0, height/2, width/2, width );

        quadranteA.run();
        quadranteB.run();
        quadranteC.run();
        quadranteD.run();

        writeImage(image);
    }
}
