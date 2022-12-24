package es.orricoquiles.puzzlerobot;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.WritableImage;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MiImagen {
    Image imagen;
    byte[] buffer;
    int tamanyo;
    Vector4D[] vectores;

    List<Pieza> listaPiezas=new ArrayList<>();
    public MiImagen(Image imagen) {
        this.imagen=imagen;
        this.tamanyo=(int)imagen.getWidth()*(int)imagen.getHeight();
        buffer=new byte[tamanyo*4];
        var pixelReader=imagen.getPixelReader();
        int width = (int)imagen.getWidth();
        int height = (int)imagen.getHeight();
        pixelReader.getPixels(
                0,
                0,
                width,
                height,
                PixelFormat.getByteBgraInstance(),
                buffer,
                0,
                width * 4
        );
        vectores=aVectorial();
    }

    public Image getImagen(){
        int width = (int)imagen.getWidth();
        int height = (int)imagen.getHeight();
        WritableImage nueva = new WritableImage(width, height);
        nueva.getPixelWriter().setPixels(0, 0, width, height,
                PixelFormat.getByteBgraInstance(),
                buffer, 0, width * 4);
        return nueva;
    }
    public Image getImagenDeVectores(){
        int width = (int)imagen.getWidth();
        int height = (int)imagen.getHeight();
        WritableImage nueva = new WritableImage(width, height);
        nueva.getPixelWriter().setPixels(0, 0, width, height,
                PixelFormat.getByteBgraInstance(),
                this.deVectorial(), 0, width * 4);
        return nueva;
    }

    public void ponNegro(){
        for (int i = 0; i < 60000; i+=4) {
            buffer[i]=0;
            buffer[i+1]=0;
            buffer[i+2]=0;
            buffer[i+3]=(byte)255;
        }
    }
    public Vector4D[] aVectorial(){
        Vector4D[] salida=new Vector4D[tamanyo];
        int indiceSalida=0;
        for (int i = 0; i < buffer.length; i+=4) {
            salida[indiceSalida++]=new Vector4D(buffer[i],buffer[i+1],buffer[i+2] );
        }
        return salida;
    }

    public byte[] deVectorial(){
        byte[] salida=new byte[tamanyo*4];
        int indiceSalida=0;
        for (int i = 0; i < vectores.length; i++) {
            salida[indiceSalida++]=(byte)vectores[i].x;
            salida[indiceSalida++]=(byte)vectores[i].y;
            salida[indiceSalida++]=(byte)vectores[i].z;
            salida[indiceSalida++]=(byte)vectores[i].alfa;

        }
        return salida;
    }
    public void fondoANegro(ImageView imagenDerecha){
        Vector4D semilla=vectores[deCoordenadasAIndice(0,0)];
    /*
        for (int j = 0; j < vectores.length; j++) {
            Vector4D e = vectores[j];
            //System.out.println(e.distancia(semilla));
            if (e.distancia(semilla) < 90) {
                e.alfa = 0;
            }
            //System.out.println(j + " J");
        }*/
        //quitaFondoRecursivo(0,0,semilla,0);
        quitaFondoIterativo(0,0,semilla.copy(),imagenDerecha);
    }

    private void quitaFondoIterativo(int x, int y, Vector4D semilla, ImageView imagenDerecha) {
        boolean[][] visitados=new boolean[(int)imagen.getWidth()][(int)imagen.getWidth()];
        List<Punto> puntos=new LinkedList<>();
        puntos.add(new Punto(x,y));
        visitados[x][y]=true;
        while(!puntos.isEmpty()){
            //System.out.println(puntos.size());


            Punto puntoActual=puntos.remove(0);

            visitados[puntoActual.x][puntoActual.y]=true;
            Vector4D vectorActual=vectores[deCoordenadasAIndice(puntoActual.getX(), puntoActual.getY())];
            if(vectorActual.distancia(semilla)<120) {
                //System.out.println(vectorActual);
//                vectorActual.x=(byte)0;
//                vectorActual.y=(byte)0;
//                vectorActual.z=(byte)0;
                vectorActual.alfa=(byte)0;
                //System.out.println(vectorActual);
                int xpunto= puntoActual.getX();
                int ypunto= puntoActual.getY();
                if(!(xpunto+1== imagen.getWidth())) {
                    if (!visitados[xpunto + 1][ypunto]) {
                        visitados[xpunto + 1][ypunto]=true;
                        puntos.add(new Punto(xpunto + 1, ypunto));
                    }
                }
                if(!(xpunto-1<0)) {
                    if (!visitados[xpunto - 1][ypunto]) {
                        visitados[xpunto - 1][ypunto]=true;
                        puntos.add(new Punto(xpunto - 1, ypunto));
                    }
                }
                if(!(ypunto+1== imagen.getHeight())) {
                    if (!visitados[xpunto][ypunto+1]) {
                        visitados[xpunto][ypunto+1]=true;
                        puntos.add(new Punto(xpunto, ypunto + 1));
                    }
                }
                if(!(ypunto-1<0)) {
                    if (!visitados[xpunto][ypunto-1]) {
                        visitados[xpunto][ypunto-1]=true;
                        puntos.add(new Punto(xpunto, ypunto - 1));
                    }
                }
            }
        }
    }


    public int deCoordenadasAIndice(int x, int y){
        return x+y*(int)imagen.getWidth();
    }

    public List<Pieza> sacaPieza() {
        int x=0;
        int y=0;
        Pieza nuevaPieza=new Pieza();
        Vector4D actual=vectores[deCoordenadasAIndice(x,y)];
        int grandes=0;
        while(y< imagen.getHeight()) {
            //System.out.println(actual);
            while (actual.alfa == 0) {
                //System.out.println("e");
                x++;
                if (x == imagen.getWidth()) {
                    x = 0;
                    y++;
                }
                if (y == imagen.getHeight()) break;
                actual = vectores[deCoordenadasAIndice(x, y)];
            }
            obtenAdjacentes(x, y, nuevaPieza);
            //System.out.println("Nueva pieza " + x + ", " + y + " de tamaño " + nuevaPieza.pixeles.size());
            if (nuevaPieza.pixeles.size() > 200) {
                grandes++;
                listaPiezas.add(nuevaPieza);
            }
            actual.alfa = 0;
            nuevaPieza = new Pieza();
        }
        System.out.println(grandes + " piezas grandes");
        return listaPiezas;
    }

    private void obtenAdjacentes(int x, int y,Pieza nuevaPieza) {
        Vector4D semilla=vectores[deCoordenadasAIndice(0,0)];
        boolean[][] visitados=new boolean[(int)imagen.getWidth()][(int)imagen.getWidth()];
        List<Punto> puntos=new LinkedList<>();
        puntos.add(new Punto(x,y));
        visitados[x][y]=true;
        while(!puntos.isEmpty()){
            //System.out.println(puntos.size());


            Punto puntoActual=puntos.remove(0);


            visitados[puntoActual.x][puntoActual.y]=true;
            if(puntoValido(puntoActual.getX(), puntoActual.getY())) {
                Vector4D vectorActual = vectores[deCoordenadasAIndice(puntoActual.getX(), puntoActual.getY())];

                if (vectorActual.alfa != 0) {

                    nuevaPieza.anyadePixel(puntoActual.getX(), puntoActual.getY(), vectorActual.x, vectorActual.y, vectorActual.z, vectorActual.alfa);
                    vectorActual.alfa = 0;
                    int xpunto = puntoActual.getX();
                    int ypunto = puntoActual.getY();
                    if (!(xpunto + 1 == imagen.getWidth())) {
                        if (!visitados[xpunto + 1][ypunto]) {
                            visitados[xpunto + 1][ypunto] = true;
                            puntos.add(new Punto(xpunto + 1, ypunto));
                        }
                    }
                    if (!(xpunto - 1 < 0)) {
                        if (!visitados[xpunto - 1][ypunto]) {
                            visitados[xpunto - 1][ypunto] = true;
                            puntos.add(new Punto(xpunto - 1, ypunto));
                        }
                    }
                    if (!(ypunto + 1 == imagen.getHeight())) {
                        if (!visitados[xpunto][ypunto + 1]) {
                            visitados[xpunto][ypunto + 1] = true;
                            puntos.add(new Punto(xpunto, ypunto + 1));
                        }
                    }
                    if (!(ypunto - 1 < 0)) {
                        if (!visitados[xpunto][ypunto - 1]) {
                            visitados[xpunto][ypunto - 1] = true;
                            puntos.add(new Punto(xpunto, ypunto - 1));
                        }
                    }
                }
            }
        }
    }

    boolean puntoValido(int x,int y){
        if(x<0) return false;
        if(y<0) return false;
        if(x>=imagen.getWidth()) return false;
        if(y>= imagen.getHeight()) return false;
        return true;
    }

    class Vector4D{
        byte x;
        byte y;
        byte z;
        byte alfa;
        public Vector4D(byte r, byte g, byte b) {
            this.x=r;
            this.y=g;
            this.z=b;
            alfa=(byte)255;
        }
        Vector4D copy(){
            return new Vector4D(this.x,this.y,this.z);
        }

        double distancia(Vector4D otro){
            double radicando=(this.x- otro.x)*(this.x- otro.x)+(this.y- otro.y)*(this.y- otro.y)+(this.z- otro.z)*(this.z- otro.z);
            return Math.sqrt(radicando);
        }

        @Override
        public String toString() {
            return "Vector4D{" +
                    "x=" + (byte)x +
                    ", y=" + y +
                    ", z=" + z +
                    ", alfa=" + alfa +
                    '}';
        }
    }
    class Punto{
        int x;
        int y;

        public Punto(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }
    }




}
