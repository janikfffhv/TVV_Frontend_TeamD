package at.fhv.tvv.frontendteamd;

import at.fhv.tvv.shared.dto.MessageDTO;
import at.fhv.tvv.shared.ejb.MessageConsumer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.controlsfx.control.Notifications;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.IOException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.Properties;
import java.util.ResourceBundle;

public class MessagedetailsController implements Initializable {

    private Stage stage;

    private MessageDTO messageDTO;

    //WARENKORB
    @FXML
    private ImageView warenkorbBild;
    //BENACHRICHTIGUNG
    @FXML
    private ImageView benachrichtigungBild;

    //MESSAGEDETAILS
    @FXML
    private Label betreffLabel;
    @FXML
    private Label themaLabel;
    @FXML
    private Label autorLabel;
    @FXML
    private Label vonDatumLabel;
    @FXML
    private Label bisDatumTextLabel;
    @FXML
    private Label bisDatumLabel;
    @FXML
    private TextArea inhaltTA;

    private static Properties props = new Properties();
    private static Context ctx = null;



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        //TODO: Nachrichteninhalt aus MessageDto einfügen in die Labels + Textfeld!
        betreffLabel.setText("Beispielüberschrift");
        themaLabel.setText("Kino");
        autorLabel.setText("Beispielautor");
        vonDatumLabel.setText("18.04.2023");
        //if(MessageDto.getBisDatum != null) {
            bisDatumLabel.setText("31.05.2023");
            bisDatumTextLabel.setVisible(true);
            bisDatumLabel.setVisible(true);
        //}
        inhaltTA.setText("Dies ist die Geschichte von tausenden Kanälen, die in ein großes Meer aus Kabeln schwimmen - Alle waren begeistert...Hallo Welt!\n\nLorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Faucibus scelerisque eleifend donec pretium vulputate sapien nec sagittis. Sit amet mattis vulputate enim nulla. Dis parturient montes nascetur ridiculus. Ultrices gravida dictum fusce ut placerat orci. Ut diam quam nulla porttitor massa id neque aliquam vestibulum. Nunc aliquet bibendum enim facilisis gravida neque convallis a cras. Ultricies tristique nulla aliquet enim tortor at auctor urna nunc. Et netus et malesuada fames ac turpis egestas maecenas pharetra. Amet luctus venenatis lectus magna fringilla urna porttitor. Blandit volutpat maecenas volutpat blandit. Fermentum dui faucibus in ornare quam viverra. Semper feugiat nibh sed pulvinar proin gravida hendrerit lectus. Fames ac turpis egestas sed. Nulla facilisi nullam vehicula ipsum a arcu. Mauris ultrices eros in cursus turpis massa. Massa tincidunt dui ut ornare lectus. In mollis nunc sed id semper risus in hendrerit. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.");

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

    @FXML
    public void setMessage(MessageDTO message) throws IOException {
        betreffLabel.setText(message.getTitle());
        themaLabel.setText(message.getTopicName());
        inhaltTA.setText(message.getContent());
        this.messageDTO = message;
    }

    @FXML
    public void acknowledgeMessage(ActionEvent event) throws IOException, NamingException {
        MessageConsumer messageConsumer = (MessageConsumer) ctx.lookup("ejb:/backend-1.0-SNAPSHOT/MessageConsumerEJB!at.fhv.tvv.shared.ejb.MessageConsumer");
        if(messageConsumer.acknowledgeMessage(TVVApplication.getBenutzerName(), messageDTO.getId())) {

            //LISTE DER MESSAGES IN TVVApplication AKTUALISIEREN
            TVVApplication.messages = messageConsumer.getMessages(TVVApplication.getBenutzerName());

            Parent root = FXMLLoader.load(getClass().getResource("/at/fhv/tvv/frontendteamd/fxml/benachrichtigungen/TVV_Benachrichtigungen.fxml"));
            stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
            double breite = ((Node)event.getSource()).getScene().getWidth();
            double hoehe = ((Node)event.getSource()).getScene().getHeight();
            Scene scene = new Scene(root, breite, hoehe);
            stage.setScene(scene);
            stage.show();
            Notifications.create()
                    .title("Erfolg!")
                    .text("Nachricht zur Kenntnis genommen!")
                    .showWarning();
        } else {
            Notifications.create()
                    .title("Fehler!")
                    .text("Acknowledgement fehlgeschlagen!")
                    .showWarning();
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

}
