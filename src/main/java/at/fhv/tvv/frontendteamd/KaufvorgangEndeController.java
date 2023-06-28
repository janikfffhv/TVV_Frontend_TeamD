package at.fhv.tvv.frontendteamd;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ResourceBundle;

public class KaufvorgangEndeController implements Initializable {

    private Stage stage;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        //WARENKORB NACH KAUF LEEREN
        try {
            TVVApplication.leeren();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }

    }


    //FUNKTIONEN

    @FXML
    protected void zurEventsuche(ActionEvent event) throws IOException {

        Parent root = FXMLLoader.load(getClass().getResource("/at/fhv/tvv/frontendteamd/fxml/eventsuche/TVV_Eventsuche.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        double breite = ((Node) event.getSource()).getScene().getWidth();
        double hoehe = ((Node) event.getSource()).getScene().getHeight();

        Scene scene = new Scene(root, breite, hoehe);
        stage.setScene(scene);

        stage.show();

    }

}
