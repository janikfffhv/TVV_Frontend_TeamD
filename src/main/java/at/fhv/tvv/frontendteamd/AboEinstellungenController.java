package at.fhv.tvv.frontendteamd;

import at.fhv.tvv.shared.ejb.RolesTopics;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.IOException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;

public class AboEinstellungenController implements Initializable {

    private Stage stage;

    //WARENKORB
    @FXML
    private ImageView warenkorbBild;
    //BENACHRICHTIGUNG
    @FXML
    private ImageView benachrichtigungBild;

    //ABONNIERTE THEMEN
    @FXML
    private Label themenLabel;
    @FXML
    private CheckBox kinoCheckbox;
    @FXML
    private CheckBox theaterCheckbox;
    @FXML
    private CheckBox konzertCheckbox;

    private boolean kinoBereitsAbonniert = false;
    private boolean theaterBereitsAbonniert = false;
    private boolean konzertBereitsAbonniert = false;

    //VALIDIERUNG
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
        //TODO: Kontrollieren, welche Themen der User bereits abonniert hat. Die Checkboxen der bereits abonnierten Themen
        //TODO: sollen bei Seitenaufruf sofort ausgewählt sein (z.B. kinoCheckbox.setSelected(true)).
        //TODO: Die dazugehörigen boolean-Variablen sollen dann auf true gesetzt werden (z.B. kinoBereitsAbonniert = true).
        try {
            List<String> topics = TVVApplication.getTopics();
            if(topics.contains("KINO")) {
                kinoCheckbox.setSelected(true); //Test - später löschen!
                kinoBereitsAbonniert = true; //Test - später löschen!
            }
            if(topics.contains("THEATER")) {
                theaterCheckbox.setSelected(true); //Test - später löschen!
                theaterBereitsAbonniert = true; //Test - später löschen!
            }
            if(topics.contains("KONZERT")) {
                konzertCheckbox.setSelected(true); //Test - später löschen!
                konzertBereitsAbonniert = true; //Test - später löschen!
            }
        } catch (RemoteException e) {
            throw new RuntimeException(e);
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

        //Angemeldeten User ausloggen -> Warenkorb leeren
        TVVApplication.leeren();

        Parent root = FXMLLoader.load(getClass().getResource("/at/fhv/tvv/frontendteamd/fxml/login/TVV_Login.fxml"));
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
    protected void zurueck(ActionEvent event) throws IOException {

        Parent root = FXMLLoader.load(getClass().getResource("/at/fhv/tvv/frontendteamd/fxml/benachrichtigungen/TVV_Benachrichtigungen.fxml"));
        stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        double breite = ((Node)event.getSource()).getScene().getWidth();
        double hoehe = ((Node)event.getSource()).getScene().getHeight();

        Scene scene = new Scene(root, breite, hoehe);
        stage.setScene(scene);

        stage.show();

    }

    @FXML
    protected void themenSpeichern(ActionEvent event) throws RemoteException {

        if(validierung()) {
            List<String> topics = new ArrayList<>();

            //KINO
            if(kinoCheckbox.isSelected()) {

                kinoBereitsAbonniert = true;
                topics.add("KINO");

            }

            if(!kinoCheckbox.isSelected()) {

                kinoBereitsAbonniert = false;

            }

            //THEATER
            if(theaterCheckbox.isSelected()) {

                theaterBereitsAbonniert = true;
                topics.add("THEATER");

            }

            if(!theaterCheckbox.isSelected()) {

                theaterBereitsAbonniert = false;

            }

            //KONZERT
            if(konzertCheckbox.isSelected()) {

                konzertBereitsAbonniert = true;
                topics.add("KONZERT");

            }

            if(!konzertCheckbox.isSelected()) {

                konzertBereitsAbonniert = false;

            }

            //SYSTEM IST BEI JEDEM USER IMMER FIX ABONNIERT!
            String username = TVVApplication.getBenutzerName();
            try {
                RolesTopics rolesTopics = (RolesTopics) ctx.lookup("ejb:/backend-1.0-SNAPSHOT/RolesTopicsEJB!at.fhv.tvv.shared.ejb.RolesTopics");
                rolesTopics.setTopics(topics, username);
                TVVApplication.setTopics(topics);
                zurueck(event);


            } catch (Exception e) {
                e.printStackTrace();
            }

            themenLabel.setVisible(true);

        }

    }

    private boolean validierung() {

        boolean valid = true;

        themenLabel.setVisible(false);

        if(!kinoCheckbox.isSelected() && !theaterCheckbox.isSelected() && !konzertCheckbox.isSelected()) {

            valid = false;

        }

        return valid;

    }

    private boolean abonniereThema(String thema) {

        System.out.println("Thema " + thema + " abonnieren...");
        //TODO: User bei gewähltem Topic abonnieren.

        return true; //Thema wurde abonniert.

    }

    private boolean deabonniereThema(String thema) {

        System.out.println("Thema " + thema + " deabonnieren...");
        //TODO: User von gewähltem Topic deabonnieren.

        return false; //Thema wurde deabonniert.

    }

}
