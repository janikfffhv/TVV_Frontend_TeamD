package at.fhv.tvv.frontendteamd;

import at.fhv.tvv.shared.dto.CustomerEventDTO;
import at.fhv.tvv.shared.dto.CustomerInfoDTO;
import at.fhv.tvv.shared.ejb.CustomerTickets;
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

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.IOException;
import java.net.URL;
import java.rmi.Naming;
import java.util.Properties;
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

    private static Properties props = new Properties();
    private static Context ctx = null;

    // TODO


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        props.put(Context.INITIAL_CONTEXT_FACTORY, "org.wildfly.naming.client.WildFlyInitialContextFactory");
        props.put(Context.PROVIDER_URL, "http-remoting://" + TVVApplication.getIp() + ":8080");
        try {
            ctx = new InitialContext(props);
        } catch (NamingException e) {
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
    public void sucheKunde(UUID kundenId) throws IOException {
        System.out.println("Geöffnet, ID:" + kundenId);
        try {
            CustomerInfoDTO kunde;
            CustomerTickets customerSearch = (CustomerTickets) ctx.lookup("ejb:/backend-1.0-SNAPSHOT/CustomerTicketsEJB!at.fhv.tvv.shared.ejb.CustomerTickets");
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

}
