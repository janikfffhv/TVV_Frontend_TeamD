package at.fhv.tvv.frontendteamd;

import at.fhv.tvv.shared.dto.WarenkorbZeileDTO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import org.controlsfx.control.Notifications;

import java.io.IOException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.List;
import java.util.ResourceBundle;

public class WarenkorbController implements Initializable {

    private Stage stage;

    //WARENKORB-BUTTON (HEADER)
    @FXML
    private ImageView warenkorbBild;
    //BENACHRICHTIGUNG
    @FXML
    private ImageView benachrichtigungBild;

    //TICKET-TABELLE

    @FXML
    private TableView/*<TicketDto>*/ warenkorbTV;

    @FXML
    private TableColumn<String, WarenkorbZeileDTO> eventSpalte;
    @FXML
    private TableColumn<String, WarenkorbZeileDTO> terminSpalte;
    @FXML
    private TableColumn<Integer, WarenkorbZeileDTO> platzSpalte;
    @FXML
    private TableColumn<Float, WarenkorbZeileDTO> preisSpalte;

    //WARENKORB

    @FXML
    private Button ticketsLoeschenButton;

    @FXML
    private Button warenkorbLeerenButton;

    @FXML
    private Line line;

    @FXML
    private Label preisLabel;

    @FXML
    private Label preisTextLabel;

    @FXML
    private Button kassaButton;



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        float preisGesamt = 0;
        eventSpalte.setCellValueFactory(new PropertyValueFactory<>("eventName"));
        eventSpalte.setPrefWidth(367);
        terminSpalte.setCellValueFactory(new PropertyValueFactory<>("termin"));
        terminSpalte.setPrefWidth(367);
        platzSpalte.setCellValueFactory(new PropertyValueFactory<> ("platzId"));
        platzSpalte.setPrefWidth(367);
        preisSpalte.setCellValueFactory(new PropertyValueFactory<> ("preis"));
        preisSpalte.setPrefWidth(367);
        warenkorbTV.getColumns().clear();
        warenkorbTV.getItems().clear();
        warenkorbTV.getColumns().add(eventSpalte);
        warenkorbTV.getColumns().add(terminSpalte);
        warenkorbTV.getColumns().add(platzSpalte);
        warenkorbTV.getColumns().add(preisSpalte);

        try {
            List<WarenkorbZeileDTO> warenkorb = TVVApplication.getWarenkorb();
            for(WarenkorbZeileDTO zeile:warenkorb) {
                warenkorbTV.getItems().add(zeile);
                preisGesamt += zeile.getPreis();
            }
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }

        if(warenkorbTV.getItems().size() >= 1) {
            kassaButton.setVisible(true);
            preisTextLabel.setVisible(true);
            preisLabel.setVisible(true);
            preisLabel.setText(preisGesamt + " €");
            line.setVisible(true);
            warenkorbLeerenButton.setVisible(true);
            //ticketsLoeschenButton.setVisible(true);
        }

        //BENACHRICHTIGUNGEN-ICON
        if(TVVApplication.messages.size() > 0) { //WENN MINDESTENS EINE NACHRICHT IM POSTEINGANG LIEGT.
            //Benachrichtigungen-Icon ändern
            benachrichtigungBild.setImage(new Image(getClass().getResource("images/Neue_Benachrichtigungen.png").toString()));
        }

        //WARENKORB-ICON
        try {
            if(TVVApplication.getWarenkorb().size() > 0) { //WENN MINDESTENS EIN TICKET IM WARENKORB LIEGT.
                warenkorbBild.setImage(new Image(getClass().getResource("images/Gefuellter_Warenkorb.png").toString()));
            }
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }

    }

    //HEADER-FUNKTIONEN

    @FXML
    protected void logout(ActionEvent event) throws IOException {

        //TODO: Angemeldeten User ausloggen

        Parent root = FXMLLoader.load(getClass().getResource("/at/fhv/tvv/frontendteamd/fxml/login/TVV_Login.fxml"));
        stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        double breite = ((Node)event.getSource()).getScene().getWidth();
        double hoehe = ((Node)event.getSource()).getScene().getHeight();

        Scene scene = new Scene(root, breite, hoehe);
        stage.setScene(scene);

        stage.show();

    }

    @FXML
    protected void oeffneBenachrichtigungen(ActionEvent event) throws IOException {

        Parent root = FXMLLoader.load(getClass().getResource("/at/fhv/tvv/frontendteamd/fxml/benachrichtigungen/TVV_Benachrichtigungen.fxml"));
        stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        double breite = ((Node)event.getSource()).getScene().getWidth();
        double hoehe = ((Node)event.getSource()).getScene().getHeight();

        Scene scene = new Scene(root, breite, hoehe);
        stage.setScene(scene);

        stage.show();

    }

    @FXML
    protected void oeffneKundensuche(ActionEvent event) throws IOException {

        Parent root = FXMLLoader.load(getClass().getResource("/at/fhv/tvv/frontendteamd/fxml/kundensuche/TVV_Kundensuche.fxml"));
        stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        double breite = ((Node)event.getSource()).getScene().getWidth();
        double hoehe = ((Node)event.getSource()).getScene().getHeight();

        Scene scene = new Scene(root, breite, hoehe);
        stage.setScene(scene);

        stage.show();



    }

    @FXML
    protected void oeffneEventsuche(ActionEvent event) throws IOException {

        Parent root = FXMLLoader.load(getClass().getResource("/at/fhv/tvv/frontendteamd/fxml/eventsuche/TVV_Eventsuche.fxml"));
        stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        double breite = ((Node)event.getSource()).getScene().getWidth();
        double hoehe = ((Node)event.getSource()).getScene().getHeight();

        Scene scene = new Scene(root, breite, hoehe);
        stage.setScene(scene);

        stage.show();

    }

    //FUNKTIONEN

    @FXML
    protected void warenkorbLeeren() throws RemoteException {

        warenkorbTV.getItems().clear();
        TVVApplication.leeren();

        //WARENKORB-ICON AKTUALISIEREN
        warenkorbBild.setImage(new Image(getClass().getResource("images/Warenkorb.png").toString()));

        Notifications.create()
                .title("Geleert!")
                .text("Der Warenkorb wurde geleert!")
                .showConfirm();

    }

    @FXML
    protected void markierteTicketsLoeschen() {

        System.out.println("Ticket(s) aus Warenkorb loeschen..."); //TODO

    }

    @FXML
    protected void zurKassaGehen(ActionEvent event) throws IOException {

        Parent root = FXMLLoader.load(getClass().getResource("/at/fhv/tvv/frontendteamd/fxml/kaufvorgang/TVV_KaufvorgangDateneingabe.fxml"));
        stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        double breite = ((Node)event.getSource()).getScene().getWidth();
        double hoehe = ((Node)event.getSource()).getScene().getHeight();

        Scene scene = new Scene(root, breite, hoehe);
        stage.setScene(scene);

        stage.show();

    }

}
