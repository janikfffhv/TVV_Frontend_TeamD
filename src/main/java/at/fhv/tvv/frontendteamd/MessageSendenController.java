package at.fhv.tvv.frontendteamd;

import at.fhv.tvv.shared.ejb.MessageProducer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.time.LocalDate;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

public class MessageSendenController implements Initializable {

    private Stage stage;

    //WARENKORB
    @FXML
    private ImageView warenkorbBild;
    //BENACHRICHTIGUNG
    @FXML
    private ImageView benachrichtigungBild;

    //MESSAGE SENDEN
    @FXML
    private CheckBox kinoCheckbox;
    @FXML
    private CheckBox theaterCheckbox;
    @FXML
    private CheckBox konzertCheckbox;
    @FXML
    private CheckBox systemCheckbox;
    @FXML
    private TextField betreffTF;
    @FXML
    private DatePicker bisDatumDP;
    @FXML
    private TextArea inhaltTA;

    //VALIDIERUNG
    @FXML
    private Label betreffErrorLabel;
    @FXML
    private Label themaErrorLabel;
    @FXML
    private Label bisDatumErrorLabel;
    @FXML
    private Label inhaltErrorLabel;
    @FXML
    private Label successLabel;

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
    protected void messageSenden() throws MalformedURLException, NotBoundException, RemoteException, NamingException {

        if(validierung()) {

            //TODO: Nachricht auf gewählte Topics senden.
            System.out.println("Nachricht wird versendet...");
            MessageProducer messageProducer = (MessageProducer) ctx.lookup("ejb:/backend-1.0-SNAPSHOT/MessageProducerEJB!at.fhv.tvv.shared.ejb.MessageProducer");
            if(kinoCheckbox.isSelected()) {
                messageProducer.produce("KINO", betreffTF.getText(), inhaltTA.getText());
            }
            if(theaterCheckbox.isSelected()) {
                messageProducer.produce("THEATER", betreffTF.getText(), inhaltTA.getText());
            }
            if(konzertCheckbox.isSelected()) {
                messageProducer.produce("KONZERT", betreffTF.getText(), inhaltTA.getText());
            }

            successLabel.setVisible(true);
            kinoCheckbox.setSelected(false);
            theaterCheckbox.setSelected(false);
            konzertCheckbox.setSelected(false);
            betreffTF.setText("");
            bisDatumDP.setValue(null);
            inhaltTA.setText("");

        }

    }

    private boolean validierung() {

        boolean valid = true;
        successLabel.setVisible(false);
        betreffErrorLabel.setVisible(false);
        themaErrorLabel.setVisible(false);
        bisDatumErrorLabel.setVisible(false);
        inhaltErrorLabel.setVisible(false);

        LocalDate datumbis = bisDatumDP.getValue();

        if(!kinoCheckbox.isSelected() && !theaterCheckbox.isSelected() && !konzertCheckbox.isSelected() && !systemCheckbox.isSelected()) {

            themaErrorLabel.setVisible(true);
            valid = false;

        }

        if(!Pattern.matches("^.{3,}$", betreffTF.getText())) {

            betreffErrorLabel.setVisible(true);
            valid = false;

        }

        if(datumbis != null) { //Nur wenn datumbis angegeben wurde, soll dieses validiert werden!

            if(datumbis.isBefore(LocalDate.now()) || datumbis.equals(LocalDate.now())) {

                bisDatumErrorLabel.setVisible(true);
                valid = false;

            }

        }

        if(!Pattern.matches("^.{3,}$", inhaltTA.getText())) {

            inhaltErrorLabel.setVisible(true);
            valid = false;

        }

        return valid;

    }

    @FXML
    protected void resetDatumBis() {

        bisDatumErrorLabel.setVisible(false);
        bisDatumDP.setValue(null);

    }

}
