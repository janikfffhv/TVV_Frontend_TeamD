package at.fhv.tvv.frontendteamd;

import at.fhv.tvv.shared.dto.EventDescriptionDTO;
import at.fhv.tvv.shared.dto.PlatzDTO;
import at.fhv.tvv.shared.dto.WarenkorbZeileDTO;
import at.fhv.tvv.shared.ejb.EventSearch;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.controlsfx.control.Notifications;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.IOException;
import java.net.URL;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.*;

public class EventinfoController implements Initializable {

    private Stage stage;

    //WARENKORB
    @FXML
    private ImageView warenkorbBild;
    //BENACHRICHTIGUNG
    @FXML
    private ImageView benachrichtigungBild;

    //EVENTINFOS
    @FXML
    private Label eventserieLabel;
    @FXML
    private Label veranstalterLabel;
    @FXML
    private TextArea beschreibungTA;
    @FXML
    private Label datumLabel;
    @FXML
    private Label eventortLabel;
    @FXML
    private Label eventnameLabel;

    @FXML
    private Label strasseLabel;
    @FXML
    private Label plzortLabel;
    @FXML
    private Label kategorieLabel;
    @FXML
    private ChoiceBox platzartCB;

    //VALIDIERUNG
    @FXML
    private Label platzArtLabel;

    @FXML
    private TableView eventTicketTV;
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
    public void sucheKunde(int kundenId) throws IOException {
        System.out.println("Geöffnet, ID:" + kundenId);
        try {
            EventDescriptionDTO event;
            EventSearch eventSearch = (EventSearch) ctx.lookup("ejb:/backend-1.0-SNAPSHOT/EventSearchEJB!at.fhv.tvv.shared.ejb.EventSearch");
            event = eventSearch.searchById(kundenId);
            System.out.println(event.getBeschreibung());
            eventserieLabel.setText(event.getVeranstaltungsserie());
            veranstalterLabel.setText(event.getVeranstalter());
            beschreibungTA.setText(event.getBeschreibung());
            Date date = new Date(event.getDatum()*1000L);
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy hh:mm");
            datumLabel.setText(sdf.format(date));
            eventortLabel.setText(event.getGebaeude() + ", " + event.getRaum());
            strasseLabel.setText(event.getStrasse() + " " + event.getHausnummer());
            plzortLabel.setText(event.getPlz() + " " + event.getOrt());
            kategorieLabel.setText(event.getKategorie());
            List<PlatzDTO> katSitzplatz = new ArrayList<>();
            List<PlatzDTO> katStehplatz = new ArrayList<>();
            List<PlatzDTO> katVip = new ArrayList<>();
            List<PlatzDTO> katUndefined = new ArrayList<>();
            for(PlatzDTO platz:event.getPlaetze()) {
                switch (platz.getKategorie()) {
                    case "PLATZ":
                        katUndefined.add(platz);
                        break;
                    case "SITZPLATZ":
                        katSitzplatz.add(platz);
                        break;
                    case "STEHPLATZ":
                        katStehplatz.add(platz);
                        break;
                    case "VIP":
                        katVip.add(platz);
                        break;
                }
            }
            if(!katUndefined.isEmpty()) {
                platzartCB.setVisible(false);
                platzArtLabel.setVisible(false);
            } else if(!katSitzplatz.isEmpty()){
                platzartCB.getItems().add("Sitzplatz");
            }
            if(!katStehplatz.isEmpty()) {
                platzartCB.getItems().add("Stehplatz");
            }
            if(!katVip.isEmpty()) {
                platzartCB.getItems().add("VIP");
            }
            eventnameLabel.setText(event.getName());

            TableColumn<Integer, PlatzDTO> reiheSpalte = new TableColumn<> ("Reihe");
            reiheSpalte.setCellValueFactory(new PropertyValueFactory<>("reihe"));
            reiheSpalte.setPrefWidth(367);
            TableColumn<String, PlatzDTO> platzSpalte = new TableColumn<> ("Platz");
            platzSpalte.setCellValueFactory(new PropertyValueFactory<>("nummer"));
            platzSpalte.setPrefWidth(367);
            TableColumn<Integer, PlatzDTO> preisSpalte = new TableColumn<>("Preis");
            preisSpalte.setCellValueFactory(new PropertyValueFactory<> ("preis"));
            preisSpalte.setPrefWidth(367);
            TableColumn<Integer, PlatzDTO> addSpalte = new TableColumn<> ("Hinzufügen");
            addSpalte.setText("Hinzufügen");
            addSpalte.setPrefWidth(367);
            eventTicketTV.getColumns().clear();
            eventTicketTV.getItems().clear();
            eventTicketTV.getColumns().add(reiheSpalte);
            eventTicketTV.getColumns().add(platzSpalte);
            eventTicketTV.getColumns().add(preisSpalte);
            eventTicketTV.getColumns().add(addSpalte);

            eventTicketTV.setOnMouseClicked((MouseEvent mouseEvent) -> {
                if(mouseEvent.getClickCount() == 2) {

                    /*try {
                        if(tvvSession.getKunde().getRolle() == "Operator") { //TODO: Kontrollieren, ob angemeldeter User die Rolle Operator besitzt!



                        } else {*/ //User ist kein Operator und besitzt somit die Berechtigung, Tickets in den Warenkorb zu legen und somit später zu kaufen.
                    try {
                        List<String> roles = TVVApplication.getRollen();
                        if(!roles.contains("Mitarbeiter")) {
                            Notifications.create()
                                    .title("Fehler!")
                                    .text("Sie besitzen als Operator-User keine Warenkorb-Berechtigungen!")
                                    .showWarning();
                        } else {
                            int ticketIndex = eventTicketTV.getSelectionModel().getSelectedIndex();
                            PlatzDTO platzId = (PlatzDTO) eventTicketTV.getItems().get(ticketIndex);
                            System.out.println(platzId.getPlatzId());
                            WarenkorbZeileDTO warenkorbZeile = new WarenkorbZeileDTO(platzId.getPlatzId(), platzId.getKategorie(), event.getEventId(), event.getName(), platzId.getPreis(), sdf.format(date));
                            try {

                                if (TVVApplication.hinzufuegen(warenkorbZeile)) {
                                    Notifications.create()
                                            .title("Hinzugefügt!")
                                            .text("Ticket Nummer " + platzId.getPlatzId() + " wurde zum Warenkorb hinzugefügt!")
                                            .showConfirm();
                                } else {
                                    Notifications.create()
                                            .title("Fehler!")
                                            .text("Ticket Nummer " + platzId.getPlatzId() + " befindet sich bereits im Warenkorb!")
                                            .showWarning();
                                }
                                System.out.println("Hinzugefügt: " + TVVApplication.getWarenkorb().get(0).getPlatzId());

                            } catch (RemoteException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    } catch (RemoteException e) {
                        throw new RuntimeException(e);
                    }

                    /**FXMLLoader loader2 = new FXMLLoader(getClass().getResource("/at/fhv/tvv/frontendteamd/fxml/eventinfo/TVV_Eventinfo.fxml"));
                     Parent root = loader2.load();
                     stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
                     double breite = ((Node)event.getSource()).getScene().getWidth();
                     double hoehe = ((Node)event.getSource()).getScene().getHeight();

                     Scene scene = new Scene(root, breite, hoehe);
                     stage.setScene(scene);
                     EventinfoController eventinfoController = loader2.getController();
                     eventinfoController.sucheKunde(eventId.getEventId());
                     stage.show();**/

                    /*} catch (RemoteException e) {
                        throw new RuntimeException(e);
                    }*///TODO ENDE: Kontrollieren, ob angemeldeter User die Rolle Operator besitzt!
                }
            });

            platzartCB.setOnAction(event1 -> {
                System.out.println("CHANGING!");
                System.out.println(platzartCB.getValue().toString());
                switch(platzartCB.getValue().toString()) {
                    case "Sitzplatz":
                        eventTicketTV.getItems().clear();
                        for(PlatzDTO ticket : katSitzplatz) {
                            eventTicketTV.getItems().add(ticket);
                        }
                        break;
                    case "Stehplatz":
                        eventTicketTV.getItems().clear();
                        for(PlatzDTO ticket : katStehplatz) {
                            eventTicketTV.getItems().add(ticket);
                        }
                        break;
                    case "VIP":
                        eventTicketTV.getItems().clear();
                        for(PlatzDTO ticket : katVip) {
                            eventTicketTV.getItems().add(ticket);
                        }
                }
            });



        } catch (Exception e) {
            e.printStackTrace();
        }
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
/*
    @FXML
    protected void zuWarenkorbHinzufuegen() {

        if(validiere()) {

            //Methode aus Domain Layer aufrufen, welche Warenkorb Funktionalität ausführt //TODO
            System.out.println("Ticket zu Warenkorb hinzufuegen...");
            warenkorbErfolgreichLabel.setVisible(true);

        }

    }
*/
    @FXML
    protected void zurueckZurSuche(ActionEvent event) throws IOException {

        Parent root = FXMLLoader.load(getClass().getResource("/at/fhv/tvv/frontendteamd/fxml/eventsuche/TVV_Eventsuche.fxml"));
        stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        double breite = ((Node)event.getSource()).getScene().getWidth();
        double hoehe = ((Node)event.getSource()).getScene().getHeight();

        Scene scene = new Scene(root, breite, hoehe);
        stage.setScene(scene);

        stage.show();

    }
/*
    private boolean validiere() {

        boolean valid = true;

        platzwahlErrorLabel.setVisible(false);
        warenkorbErfolgreichLabel.setVisible(false);

        if(platzCB.getValue() == null /* && EventDto.getPlatzwahl() == true) {

            platzwahlErrorLabel.setVisible(true);

            valid = false;

        }

        return valid;

    }
*/
}
