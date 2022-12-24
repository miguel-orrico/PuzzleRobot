package es.orricoquiles.puzzlerobot;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.image.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;

import java.io.*;
import java.util.Properties;

public class PuzzleRobotControlador {
    MiImagen imagenSinFondo;
    private int columnaImagenes =0;
    private int filaImagenes=0;
    public SplitPane panelDividido;
    public AnchorPane panelIzquierdo;
    public AnchorPane panelDerecho;
    public ImageView imagenDerecha;
    public AnchorPane listaPiezas;
    public ScrollPane scrollImagenes;
    @FXML
    ImageView imagenIzquierda;
    @FXML
    private Label welcomeText;

    @FXML
    public void initialize(){
        System.out.println("inicio");
        panelIzquierdo.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                System.out.println(observableValue + ", " + number + ", " + t1);
                imagenIzquierda.setFitWidth(t1.intValue());
            }
        });
        panelDerecho.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                System.out.println(observableValue + ", " + number + ", " + t1);
                imagenDerecha.setFitWidth(t1.intValue());
            }
        });

        Properties configuracion=new Properties();
        String nombreUltimaImagen;
        try {
            configuracion.load(new FileReader("configuracion.xml"));
            nombreUltimaImagen=configuracion.getOrDefault("ultimaImagen","SinImagen.png").toString();
            System.out.println(nombreUltimaImagen);

        } catch (IOException e) {
            nombreUltimaImagen="SinImagen.png";
            try {
                configuracion.setProperty("ultimaImagen","SinImagen.png");
                configuracion.store(new FileWriter("configuracion.xml"),"");
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
        FileInputStream inputstream = null;
        try {
            inputstream = new FileInputStream(nombreUltimaImagen);
            Image image = new Image(inputstream);
            imagenIzquierda.setFitWidth(panelIzquierdo.getWidth());
            imagenIzquierda.setImage(image);
            imagenSinFondo=new MiImagen(imagenIzquierda.getImage());

            imagenSinFondo.fondoANegro(imagenDerecha);
            imagenDerecha.setImage(imagenSinFondo.getImagenDeVectores());


        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }


    }

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }

    @FXML
    protected void cargarImagenClick(){
        welcomeText.setText("Cargando imagen");
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        String fichero=fileChooser.showOpenDialog(welcomeText.getScene().getWindow()).getPath();
        FileInputStream inputstream = null;

        Properties configuracion=new Properties();

        try {
            configuracion.load(new FileReader("configuracion.xml"));
            inputstream = new FileInputStream(fichero);
            Image image = new Image(inputstream);
            System.out.println(panelIzquierdo.getWidth());
            imagenIzquierda.setFitWidth(panelIzquierdo.getWidth());
            imagenIzquierda.setImage(image);
            configuracion.setProperty("ultimaImagen",fichero);
            configuracion.store(new FileWriter("configuracion.xml"),"");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    public void detectarFondoClick(ActionEvent actionEvent) {
        imagenSinFondo=new MiImagen(imagenIzquierda.getImage());

            imagenSinFondo.fondoANegro(imagenDerecha);
            imagenDerecha.setImage(imagenSinFondo.getImagenDeVectores());


    }

    public void detectarPiezasClick(ActionEvent actionEvent) {
        int tamanyo=100;
        if(imagenSinFondo==null) {
            System.out.println("Sin imagen");
            return;
        }
        System.out.println("RR");
        imagenSinFondo.sacaPieza();
        for (Pieza p: imagenSinFondo.listaPiezas) {
            System.out.println("EE");
            ImageView visor=new ImageView();

            listaPiezas.getChildren().add(visor);


            visor.setImage(p.getImagen());
            visor.setFitWidth(p.ancho/2);
            visor.setFitHeight(p.alto/2);

            visor.relocate(columnaImagenes++*tamanyo,filaImagenes*tamanyo);
            if(columnaImagenes *tamanyo>(listaPiezas.getWidth()-tamanyo)){
                filaImagenes++;
                columnaImagenes=0;
            }
        }
    }
}