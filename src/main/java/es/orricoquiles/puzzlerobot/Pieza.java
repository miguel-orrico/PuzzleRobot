package es.orricoquiles.puzzlerobot;

import javafx.scene.image.Image;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.WritableImage;

import java.util.ArrayList;
import java.util.List;

public class Pieza {
    List<Pixel> pixeles=new ArrayList<>();
    int ancho;
    int alto;

    byte[] pixelesRGB;

    public Image getImagenDeVectores() {
        return null;
    }

    public void anyadePixel(int x, int y, byte r, byte g, byte b, byte alfa){
        pixeles.add(new Pixel(x,y,r,g,b,alfa));
    }

    public Image getImagen(){
        normaliza();
        int width = ancho;
        int height = alto;
        byte[] pixeles=new byte[4*width*height];
        for (int i = 0; i < pixeles.length; i++) {
            pixeles[i]=(byte)255;
        }
        WritableImage nueva = new WritableImage(width, height);
        nueva.getPixelWriter().setPixels(0, 0, width, height,
                PixelFormat.getByteBgraInstance(),
                pixelesRGB, 0, width * 4);
        return nueva;
    }

    private void normaliza() {
        int minimaX=pixeles.get(0).x;
        int maximaX=pixeles.get(0).x;
        int minimaY=pixeles.get(0).y;
        int maximaY=pixeles.get(0).y;
        for (Pixel p:pixeles
             ) {
            //System.out.println(p);
            if(p.x<minimaX) minimaX=p.x;
            if(p.x>maximaX) maximaX=p.x;
            if(p.y<minimaY) minimaY=p.y;
            if(p.y>maximaY) maximaY=p.y;
        }

        this.ancho=maximaX-minimaX+1;
        this.alto=maximaY-minimaY+1;
        pixelesRGB=new byte[ancho*alto*4];
        for (int i = 0; i < pixelesRGB.length; i+=4) {
            pixelesRGB[i]=0;
            pixelesRGB[i+1]=0;
            pixelesRGB[i+2]=0;
            pixelesRGB[i+3]=0;
        }
        for (Pixel p :pixeles
             ) {
            int indice=((p.x-minimaX)+(p.y-minimaY)*ancho)*4;
            System.out.println(indice);
            pixelesRGB[indice]=p.r;
            pixelesRGB[indice+1]=p.g;
            pixelesRGB[indice+2]=p.b;
            pixelesRGB[indice+3]=(byte)255;
        }

        System.out.println("Tamaño " + pixeles.size());
        System.out.println("Ancho " + ancho + " Alto " + alto);
    }



    class Pixel{
        int x;
        int y;
        byte r;
        byte g;
        byte b;
        byte alfa;

        public Pixel(int x, int y, byte r, byte g, byte b, byte alfa) {
            this.x = x;
            this.y = y;
            this.r = r;
            this.g = g;
            this.b = b;
            this.alfa = alfa;
        }


        @Override
        public String toString() {
            return "Pixel{" +
                    "x=" + x +
                    ", y=" + y +
                    ", r=" + r +
                    ", g=" + g +
                    ", b=" + b +
                    ", alfa=" + alfa +
                    '}';
        }
    }
}
