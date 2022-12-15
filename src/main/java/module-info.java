module es.orricoquiles.puzzlerobot {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;

    opens es.orricoquiles.puzzlerobot to javafx.fxml;
    exports es.orricoquiles.puzzlerobot;
}