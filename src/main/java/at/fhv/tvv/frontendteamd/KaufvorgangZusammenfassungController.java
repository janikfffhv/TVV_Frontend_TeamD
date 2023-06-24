package at.fhv.tvv.frontendteamd;

import at.fhv.tvv.shared.dto.CustomerSearchDTO;
import at.fhv.tvv.shared.dto.VerkaufDTO;
import at.fhv.tvv.shared.dto.WarenkorbZeileDTO;
import at.fhv.tvv.shared.ejb.CustomerSearch;
import at.fhv.tvv.shared.ejb.Verkauf;
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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.controlsfx.control.Notifications;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.time.Instant;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;

public class KaufvorgangZusammenfassungController implements Initializable {

    private Stage stage;

    //BENACHRICHTIGUNG
    @FXML
    private ImageView benachrichtigungBild;

    //KAUFVORGANG - Zusammenfassung
    @FXML
    private Label nameLabel;
    @FXML
    private Label geburtsdatumLabel;
    @FXML
    private Label strasseLabel;
    @FXML
    private Label ortLabel;
    @FXML
    private Label zahlungsmethodeLabel;
    @FXML
    private Label preisLabel;


    private float preisGesamt;


    //TICKET-TABELLE

    @FXML
    private TableView/*<TicketDto>*/ warenkorbTV;

    @FXML
    private TableColumn<String, WarenkorbZeileDTO> eventSpalte;
    @FXML
    private TableColumn<String, WarenkorbZeileDTO> terminSpalte;
    @FXML
    private TableColumn<String, WarenkorbZeileDTO> platzSpalte;
    @FXML
    private TableColumn<String, WarenkorbZeileDTO> preisSpalte;
    private static Properties props = new Properties();
    private static Context ctx = null;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        props.put(Context.INITIAL_CONTEXT_FACTORY, "org.wildfly.naming.client.WildFlyInitialContextFactory");
        props.put(Context.PROVIDER_URL, "http-remoting://" + TVVApplication.getIp() + ":8080");
        try {
            ctx = new InitialContext(props);
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
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
        preisGesamt = 0;
        try {
            List<WarenkorbZeileDTO> warenkorb = TVVApplication.getWarenkorb();
            for(WarenkorbZeileDTO zeile:warenkorb) {
                warenkorbTV.getItems().add(zeile);
                preisGesamt += zeile.getPreis();
            }
            zahlungsmethodeLabel.setText(TVVApplication.getZahlungsmethode());
            CustomerSearchDTO kunde;
            CustomerSearch customerSearch = (CustomerSearch) ctx.lookup("ejb:/backend-1.0-SNAPSHOT/CustomerSearchEJB!at.fhv.tvv.shared.ejb.CustomerSearch");
            kunde = customerSearch.searchById(TVVApplication.getKunde());
            nameLabel.setText(kunde.getVorname() + " " + kunde.getNachname());
            geburtsdatumLabel.setText(kunde.getGeburtsdatum());
            strasseLabel.setText(kunde.getStrasse() + " " + kunde.getHausnummer());
            ortLabel.setText(kunde.getPlz() + " " + kunde.getOrt());
            preisLabel.setText(preisGesamt + " €");

        } catch (RemoteException e) {
            throw new RuntimeException(e);
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }

        //BENACHRICHTIGUNGEN-ICON
        if(TVVApplication.messages.size() > 0) { //WENN MINDESTENS EINE NACHRICHT IM POSTEINGANG LIEGT.
            //Benachrichtigungen-Icon ändern
            benachrichtigungBild.setImage(new Image(getClass().getResource("images/Neue_Benachrichtigungen.png").toString()));
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
    protected void zurueck(ActionEvent event) throws IOException {

        Parent root = FXMLLoader.load(getClass().getResource("/at/fhv/tvv/frontendteamd/fxml/kaufvorgang/TVV_KaufvorgangDateneingabe.fxml"));
        stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        double breite = ((Node)event.getSource()).getScene().getWidth();
        double hoehe = ((Node)event.getSource()).getScene().getHeight();

        Scene scene = new Scene(root, breite, hoehe);
        stage.setScene(scene);

        stage.show();

    }

    //FUNKTIONEN


    @FXML
    protected void ticketBuchen(ActionEvent event) throws IOException, NotBoundException, NamingException {

        if(validierung()) {


            VerkaufDTO verkaufDTO = new VerkaufDTO(preisGesamt, TVVApplication.getKunde(), TVVApplication.getZahlungsmethode().toUpperCase(), TVVApplication.getWarenkorb(), String.valueOf(Instant.now().getEpochSecond()));
            Verkauf verkauf = (Verkauf) ctx.lookup("ejb:/backend-1.0-SNAPSHOT/VerkaufImplEJB!at.fhv.tvv.shared.ejb.Verkauf");
            if(verkauf.kaufe(verkaufDTO)) {
                Parent root = FXMLLoader.load(getClass().getResource("/at/fhv/tvv/frontendteamd/fxml/kaufvorgang/TVV_KaufvorgangEnde.fxml"));
                stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                double breite = ((Node) event.getSource()).getScene().getWidth();
                double hoehe = ((Node) event.getSource()).getScene().getHeight();

                Scene scene = new Scene(root, breite, hoehe);
                stage.setScene(scene);

                stage.show();
            } else {
                Notifications.create()
                        .title("Fehler!")
                        .text("Eines der Tickets wurde bereits gekauft! Bitte erneut versuchen!")
                        .showWarning();
            }

        }

    }


    private boolean validierung() {

        boolean valid = true;

        //TODO: Es soll überprüft werden, ob alle gewählten Tickets im Warenkorb noch verfügbar sind, wenn auf den Button "JETZT BUCHEN" geklickt wird!

        return valid;

    }

}
