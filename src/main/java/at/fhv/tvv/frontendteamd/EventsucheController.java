package at.fhv.tvv.frontendteamd;

import at.fhv.tvv.frontendteamd.model.EventList;
import at.fhv.tvv.shared.dto.EventSearchDTO;
import at.fhv.tvv.shared.ejb.EventSearch;
import at.fhv.tvv.shared.ejb.TvvSession;
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

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.IOException;
import java.net.URL;
import java.rmi.Naming;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

public class EventsucheController implements Initializable {

    private Stage stage;

    //WARENKORB
    @FXML
    private ImageView warenkorbBild;
    //BENACHRICHTIGUNG
    @FXML
    private ImageView benachrichtigungBild;


    //EVENT-SUCHE-FORMULAR
    @FXML
    private TextField suchbegriffTF;

    @FXML
    private ChoiceBox<String> kategorieCB;

    @FXML
    private DatePicker datumvonDP;

    @FXML
    private DatePicker datumbisDP;

    //VALIDIERUNG
    @FXML
    private Label suchbegriffErrorLabel;
    @FXML
    private Label kategorieErrorLabel;
    @FXML
    private Label datumErrorLabel;


    //EVENT-SUCHE-TABELLE
    @FXML
    private TableView/*<EventDto>*/ eventsTV;

    @FXML
    private TableColumn eventSpalte;
    @FXML
    private TableColumn terminSpalte;
    @FXML
    private TableColumn ortSpalte;
    @FXML
    private TableColumn ticketSpalte;


    // TODO

    private static Properties props = new Properties();
    private static Context ctx = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        datumvonDP.setValue(LocalDate.now());
        datumbisDP.setValue(LocalDate.now().plusDays(1));

        //kategorieCB.setItems(FXCollections.observableArrayList("Kino", "Theater", "Konzert"));
        kategorieCB.getItems().add("Kino");
        kategorieCB.getItems().add("Theater");
        kategorieCB.getItems().add("Konzert");

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
    protected void sucheEventBegriff(ActionEvent event) throws IOException {

        //Methode aus Domain Layer aufrufen, welche die Event-Suche einleitet. //TODO: EVENTSUCHE IMPLEMENTIEREN


        if(validierung("Begriff")) {

            System.out.println("Suchen...");
            try {
                List<EventSearchDTO> events;
                EventSearch eventSearch = (EventSearch) ctx.lookup("ejb:/backend-1.0-SNAPSHOT/EventSearchEJB!at.fhv.tvv.shared.ejb.EventSearch");
                /**if(datumbisDP.getValue() != null) {
                    LocalDate d1 = datumvonDP.getValue();
                    LocalDate d2 = datumbisDP.getValue();
                    int search1 = (int) d1.toEpochSecond(LocalTime.MIN, ZoneOffset.UTC);
                    int search2 = (int) d2.toEpochSecond(LocalTime.MIN, ZoneOffset.UTC);
                    events = eventSearch.searchByDate(search1, search2);
                } else {**/
                    events = eventSearch.searchByString(suchbegriffTF.getText());
                /**}**/
                TableColumn<Integer, EventSearchDTO> idSpalte = new TableColumn<> ("Event-ID");
                idSpalte.setCellValueFactory(new PropertyValueFactory<>("eventId"));
                TableColumn<String, EventSearchDTO> eventSpalte = new TableColumn<> ("Event");
                eventSpalte.setCellValueFactory(new PropertyValueFactory<>("name"));
                TableColumn<Integer, EventSearchDTO> terminSpalte = new TableColumn<>("Termin");
                terminSpalte.setCellValueFactory(new PropertyValueFactory<> ("datum"));
                TableColumn<String, EventSearchDTO> ortSpalte = new TableColumn<> ("Veranstaltungsort");
                ortSpalte.setCellValueFactory(new PropertyValueFactory<> ("ort"));
                TableColumn<Integer, EventSearchDTO> ticketSpalte = new TableColumn<> ("Ticket-Verfügbarkeit");
                ticketSpalte.setCellValueFactory(new PropertyValueFactory<> ("plaetzeVerfuegbar"));
                eventsTV.getColumns().clear();
                eventsTV.getItems().clear();
                eventsTV.getColumns().add(idSpalte);
                eventsTV.getColumns().add(eventSpalte);
                eventsTV.getColumns().add(terminSpalte);
                eventsTV.getColumns().add(ortSpalte);
                eventsTV.getColumns().add(ticketSpalte);

                eventsTV.setOnMouseClicked((MouseEvent mouseEvent) -> {
                    if(mouseEvent.getClickCount() == 2) {
                        int eventIndex = eventsTV.getSelectionModel().getSelectedIndex();
                        EventList eventId = (EventList) eventsTV.getItems().get(eventIndex);
                        try {
                            System.out.println(eventId.getEventId());
                            FXMLLoader loader2 = new FXMLLoader(getClass().getResource("/at/fhv/tvv/frontendteamd/fxml/eventinfo/TVV_Eventinfo.fxml"));
                            Parent root = loader2.load();
                            stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
                            double breite = ((Node)event.getSource()).getScene().getWidth();
                            double hoehe = ((Node)event.getSource()).getScene().getHeight();

                            Scene scene = new Scene(root, breite, hoehe);
                            stage.setScene(scene);
                            EventinfoController eventinfoController = loader2.getController();
                            eventinfoController.sucheKunde(eventId.getEventId());
                            stage.show();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });

                for(EventSearchDTO event1 : events) {
                    EventList event2 = new EventList(event1.getEventId(), event1.getName(), event1.getVeranstaltungsserie(), event1.getDatum(), event1.getOrt(), event1.getPlaetzeVerfuegbar());
                    eventsTV.getItems().add(event2);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }


        }

    }

    @FXML
    protected void sucheEventKat(ActionEvent event) throws IOException {

        if(validierung("Kategorie")) {

                System.out.println("Suchen...");
                try {
                    List<EventSearchDTO> events;
                    EventSearch eventSearch = (EventSearch) ctx.lookup("ejb:/backend-1.0-SNAPSHOT/EventSearchEJB!at.fhv.tvv.shared.ejb.EventSearch");
                    events = eventSearch.searchByCategory(kategorieCB.getValue().toUpperCase());
                    TableColumn<Integer, EventSearchDTO> idSpalte = new TableColumn<> ("Event-ID");
                    idSpalte.setCellValueFactory(new PropertyValueFactory<>("eventId"));
                    TableColumn<String, EventSearchDTO> eventSpalte = new TableColumn<> ("Event");
                    eventSpalte.setCellValueFactory(new PropertyValueFactory<>("name"));
                    TableColumn<Integer, EventSearchDTO> terminSpalte = new TableColumn<>("Termin");
                    terminSpalte.setCellValueFactory(new PropertyValueFactory<> ("datum"));
                    TableColumn<String, EventSearchDTO> ortSpalte = new TableColumn<> ("Veranstaltungsort");
                    ortSpalte.setCellValueFactory(new PropertyValueFactory<> ("ort"));
                    TableColumn<Integer, EventSearchDTO> ticketSpalte = new TableColumn<> ("Ticket-Verfügbarkeit");
                    ticketSpalte.setCellValueFactory(new PropertyValueFactory<> ("plaetzeVerfuegbar"));
                    eventsTV.getColumns().clear();
                    eventsTV.getItems().clear();
                    eventsTV.getColumns().add(idSpalte);
                    eventsTV.getColumns().add(eventSpalte);
                    eventsTV.getColumns().add(terminSpalte);
                    eventsTV.getColumns().add(ortSpalte);
                    eventsTV.getColumns().add(ticketSpalte);

                    eventsTV.setOnMouseClicked((MouseEvent mouseEvent) -> {
                        if(mouseEvent.getClickCount() == 2) {

                            int eventIndex = eventsTV.getSelectionModel().getSelectedIndex();
                            EventList eventId = (EventList) eventsTV.getItems().get(eventIndex);
                            try {
                                System.out.println(eventId.getEventId());
                                FXMLLoader loader2 = new FXMLLoader(getClass().getResource("/at/fhv/tvv/frontendteamd/fxml/eventinfo/TVV_Eventinfo.fxml"));
                                Parent root = loader2.load();
                                stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
                                double breite = ((Node)event.getSource()).getScene().getWidth();
                                double hoehe = ((Node)event.getSource()).getScene().getHeight();

                                Scene scene = new Scene(root, breite, hoehe);
                                stage.setScene(scene);
                                EventinfoController eventinfoController = loader2.getController();
                                eventinfoController.sucheKunde(eventId.getEventId());
                                stage.show();
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    });

                    for(EventSearchDTO event1 : events) {
                        EventList event2 = new EventList(event1.getEventId(), event1.getName(), event1.getVeranstaltungsserie(), event1.getDatum(), event1.getOrt(), event1.getPlaetzeVerfuegbar());
                        eventsTV.getItems().add(event2);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }


            }

    }

    @FXML
    protected void sucheEventDatum(ActionEvent event) throws IOException {

        //Methode aus Domain Layer aufrufen, welche die Event-Suche einleitet.


        if(validierung("Datum")) {

            System.out.println("Suchen...");
            try {
                List<EventSearchDTO> events;
                EventSearch eventSearch = (EventSearch) ctx.lookup("ejb:/backend-1.0-SNAPSHOT/EventSearchEJB!at.fhv.tvv.shared.ejb.EventSearch");
                 LocalDate d1 = datumvonDP.getValue();
                 LocalDate d2 = datumbisDP.getValue();
                 int search1 = (int) d1.toEpochSecond(LocalTime.MIN, ZoneOffset.UTC);
                 int search2 = (int) d2.toEpochSecond(LocalTime.MIN, ZoneOffset.UTC);
                 events = eventSearch.searchByDate(search1, search2);
                TableColumn<Integer, EventSearchDTO> idSpalte = new TableColumn<> ("Event-ID");
                idSpalte.setCellValueFactory(new PropertyValueFactory<>("eventId"));
                TableColumn<String, EventSearchDTO> eventSpalte = new TableColumn<> ("Event");
                eventSpalte.setCellValueFactory(new PropertyValueFactory<>("name"));
                TableColumn<Integer, EventSearchDTO> terminSpalte = new TableColumn<>("Termin");
                terminSpalte.setCellValueFactory(new PropertyValueFactory<> ("datum"));
                TableColumn<String, EventSearchDTO> ortSpalte = new TableColumn<> ("Veranstaltungsort");
                ortSpalte.setCellValueFactory(new PropertyValueFactory<> ("ort"));
                TableColumn<Integer, EventSearchDTO> ticketSpalte = new TableColumn<> ("Ticket-Verfügbarkeit");
                ticketSpalte.setCellValueFactory(new PropertyValueFactory<> ("plaetzeVerfuegbar"));
                eventsTV.getColumns().clear();
                eventsTV.getItems().clear();
                eventsTV.getColumns().add(idSpalte);
                eventsTV.getColumns().add(eventSpalte);
                eventsTV.getColumns().add(terminSpalte);
                eventsTV.getColumns().add(ortSpalte);
                eventsTV.getColumns().add(ticketSpalte);

                eventsTV.setOnMouseClicked((MouseEvent mouseEvent) -> {
                    if(mouseEvent.getClickCount() == 2) {
                        int eventIndex = eventsTV.getSelectionModel().getSelectedIndex();
                        EventList eventId = (EventList) eventsTV.getItems().get(eventIndex);
                        try {
                            System.out.println(eventId.getEventId());
                            FXMLLoader loader2 = new FXMLLoader(getClass().getResource("/at/fhv/tvv/frontendteamd/fxml/eventinfo/TVV_Eventinfo.fxml"));
                            Parent root = loader2.load();
                            stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
                            double breite = ((Node)event.getSource()).getScene().getWidth();
                            double hoehe = ((Node)event.getSource()).getScene().getHeight();

                            Scene scene = new Scene(root, breite, hoehe);
                            stage.setScene(scene);
                            EventinfoController eventinfoController = loader2.getController();
                            eventinfoController.sucheKunde(eventId.getEventId());
                            stage.show();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });

                for(EventSearchDTO event1 : events) {
                    EventList event2 = new EventList(event1.getEventId(), event1.getName(), event1.getVeranstaltungsserie(), event1.getDatum(), event1.getOrt(), event1.getPlaetzeVerfuegbar());
                    eventsTV.getItems().add(event2);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    @FXML
    protected void oeffneEventinfo(ActionEvent event) throws IOException {

        /*
        //Methode aus Domain Layer aufrufen, welche die Event-Suche einleitet. //TODO: EVENTSUCHE IMPLEMENTIEREN

        Parent root = FXMLLoader.load(getClass().getResource("/at/fhv/tvv/frontendteamd/fxml/eventinfo/TVV_Eventinfo.fxml"));
        stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        double breite = ((Node)event.getSource()).getScene().getWidth();
        double hoehe = ((Node)event.getSource()).getScene().getHeight();

        Scene scene = new Scene(root, breite, hoehe);
        stage.setScene(scene);

        stage.show();

         */

    }

    @FXML
    protected void resetEingaben() {

        eventsTV.getItems().clear();

        //Eingabefelder auf Standardwert setzen
        suchbegriffTF.setText("");
        kategorieCB.setValue("");
        datumvonDP.setValue(LocalDate.now());
        datumbisDP.setValue(LocalDate.now().plusDays(1));

        //Error-Labels unsichtbar machen
        suchbegriffErrorLabel.setVisible(false);
        datumErrorLabel.setVisible(false);

    }

    private boolean validierung(String suchTyp) {

        boolean valid = true;

        suchbegriffErrorLabel.setVisible(false);
        kategorieErrorLabel.setVisible(false);
        datumErrorLabel.setVisible(false);

        switch (suchTyp) {

            case "Begriff":
                String suchbegriff = suchbegriffTF.getText();

                if(!Pattern.matches("^[a-zA-Z0-9 äÄöÖüÜß-]{3,}$", suchbegriff)) {

                    suchbegriffErrorLabel.setVisible(true);
                    valid = false;

                }
                break;

            case "Kategorie":

                if(kategorieCB.getValue() == null) {

                    kategorieErrorLabel.setVisible(true);
                    valid = false;

                }
                break;

            case "Datum":
                LocalDate datumvon = datumvonDP.getValue();
                LocalDate datumbis = datumbisDP.getValue();

                if(/*datumvon.isBefore(LocalDate.now()) ||*/ datumvon.isAfter(datumbis)) {

                    datumErrorLabel.setVisible(true);
                    valid = false;

                }

                /*
                if(datumbis.isBefore(LocalDate.now()) || datumbis.isBefore(datumvon)) { //Falls man keine Events in der Vergangenheit suchen darf

                    datumErrorLabel.setVisible(true);
                    valid = false;

                }
                */

                break;
        }

        return valid;
    }

}
