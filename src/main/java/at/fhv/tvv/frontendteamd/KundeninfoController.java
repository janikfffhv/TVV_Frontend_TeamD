package at.fhv.tvv.frontendteamd;

import at.fhv.tvv.shared.dto.CustomerEventDTO;
import at.fhv.tvv.shared.dto.CustomerInfoDTO;
import at.fhv.tvv.shared.rmi.CustomerTickets;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.rmi.Naming;
import java.util.ResourceBundle;
import java.util.UUID;

public class KundeninfoController implements Initializable {

    private Stage stage;

    //WARENKORB
    @FXML
    private ImageView warenkorbBild;
    //BENACHRICHTIGUNG
    @FXML
    private ImageView benachrichtigungBild;


    //KUNDENINFO
    @FXML
    private Label nameLabel;
    @FXML
    private Label geburtsdatumLabel;
    @FXML
    private Label adresseLabel; //Straße + HausNr.
    @FXML
    private Label ortLabel; //PLZ + Ort

    //VALIDIERUNG
    @FXML
    private Label ticketsStorniertLabel; //SUCCESS
    @FXML
    private Label ticketStornierenErrorLabel;


    //"GEBUCHTE TICKETS"-TABELLE
    @FXML
    private TableView/*<TicketDto>*/ ticketsTV; //TODO TicketDto erstellen

    @FXML
    private TableColumn<String, CustomerEventDTO> ticketIDSpalte;
    @FXML
    private TableColumn<String, CustomerEventDTO> zeitpunktSpalte;
    @FXML
    private TableColumn<Float, CustomerEventDTO> preisSpalte;

    @FXML
    private TableColumn eventSpalte;

    @FXML
    private TableColumn<String, CustomerEventDTO> zahlungsmethodeSpalte;


    // TODO


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        System.out.println("Die Seite wurde geöffnet.");

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
    protected void oeffneEventsuche(ActionEvent event) throws IOException {

        Parent root = FXMLLoader.load(getClass().getResource("/at/fhv/tvv/frontendteamd/fxml/eventsuche/TVV_Eventsuche.fxml"));
        stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        double breite = ((Node)event.getSource()).getScene().getWidth();
        double hoehe = ((Node)event.getSource()).getScene().getHeight();

        Scene scene = new Scene(root, breite, hoehe);
        stage.setScene(scene);

        stage.show();

    }

    @FXML
    protected void oeffneWarenkorb(ActionEvent event) throws IOException {

        Parent root = FXMLLoader.load(getClass().getResource("/at/fhv/tvv/frontendteamd/fxml/warenkorb/TVV_Warenkorb.fxml"));
        stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        double breite = ((Node)event.getSource()).getScene().getWidth();
        double hoehe = ((Node)event.getSource()).getScene().getHeight();

        Scene scene = new Scene(root, breite, hoehe);
        stage.setScene(scene);

        stage.show();

    }

    //FUNKTIONEN

    @FXML
    protected void zurueckZurSuche(ActionEvent event) throws IOException {

        Parent root = FXMLLoader.load(getClass().getResource("/at/fhv/tvv/frontendteamd/fxml/kundensuche/TVV_Kundensuche.fxml"));
        stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        double breite = ((Node)event.getSource()).getScene().getWidth();
        double hoehe = ((Node)event.getSource()).getScene().getHeight();

        Scene scene = new Scene(root, breite, hoehe);
        stage.setScene(scene);

        stage.show();

    }

    @FXML
    protected void storniereTickets() {

        if(validiere()) {

            System.out.println("Ausgewaehlte Tickets stornieren..."); //TODO

            ticketsStorniertLabel.setVisible(true);

        }

    }

    @FXML
    public void sucheKunde(UUID kundenId) throws IOException {
        System.out.println("Geöffnet, ID:" + kundenId);
        try {
            CustomerInfoDTO kunde;
            CustomerTickets customerSearch = (CustomerTickets) Naming.lookup("rmi://" + TVVApplication.getIp() + "/customerTickets");
            kunde = customerSearch.searchById(kundenId);
            nameLabel.setText(kunde.getName());
            eventSpalte.setVisible(false);
            geburtsdatumLabel.setText(kunde.getGeburtsdatum());
            adresseLabel.setText(kunde.getAdresse());
            ortLabel.setText(kunde.getOrt());
            ticketIDSpalte.setCellValueFactory(new PropertyValueFactory<>("ticketID"));
            ticketIDSpalte.setPrefWidth(367);
            zeitpunktSpalte.setCellValueFactory(new PropertyValueFactory<>("verkaufsZeitpunkt"));
            zeitpunktSpalte.setPrefWidth(367);
            preisSpalte.setCellValueFactory(new PropertyValueFactory<>("gesamtPreis"));
            preisSpalte.setPrefWidth(367);
            zahlungsmethodeSpalte.setCellValueFactory(new PropertyValueFactory<>("zahlungsmethode"));
            zahlungsmethodeSpalte.setPrefWidth(367);
            ticketsTV.getItems().clear();
            for(CustomerEventDTO event:kunde.getTickets()) {
                ticketsTV.getItems().add(event);
            }




        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private boolean validiere() {

        boolean valid = true;

        ticketsStorniertLabel.setVisible(false);
        ticketStornierenErrorLabel.setVisible(false);

        if(ticketsTV.getSelectionModel().getSelectedItem() == null) {

            ticketStornierenErrorLabel.setVisible(true);

            valid = false;

        }

        return valid;

    }

}
