package Server.Model;

import javafx.application.Platform;
import javafx.scene.control.TextArea;

import java.io.IOException;
import java.io.OutputStream;

public class CustomOutputStream extends OutputStream {
    private TextArea output;

    public CustomOutputStream(TextArea textArea) {
        this.output = textArea;
    }

    @Override
    public void write(final int i) throws IOException {
        Platform.runLater(new Runnable() {
            public void run() {
                output.appendText(String.valueOf((char) i));
            }
        });
    }
}