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
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
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
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;

public class BenachrichtigungenController implements Initializable {

    private static Properties props = new Properties();
    private static Context ctx = null;
    private Stage stage;
    //WARENKORB
    @FXML
    private ImageView warenkorbBild;
    //BENACHRICHTIGUNG
    @FXML
    private ImageView benachrichtigungBild;
    //BENACHRICHTIGUNGEN
    @FXML
    private Button messageSendenButton;
    //NACHRICHTEN-TABELLE
    @FXML
    private TableView/*<MessageDto>*/ nachrichtenTV; //TODO: MessageDto erstellen
    @FXML
    private TableColumn<String, MessageDTO> themaSpalte;
    @FXML
    private TableColumn<String, MessageDTO> betreffSpalte;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


        messageSendenButton.setVisible(true);
        props.put(Context.INITIAL_CONTEXT_FACTORY, "org.wildfly.naming.client.WildFlyInitialContextFactory");
        props.put(Context.PROVIDER_URL, "http-remoting://" + TVVApplication.getIp() + ":8080");
        try {
            ctx = new InitialContext(props);
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }

        try {
            //Messages einholen.
            List<MessageDTO> messages;
            MessageConsumer messageConsumer = (MessageConsumer) ctx.lookup("ejb:/backend-1.0-SNAPSHOT/MessageConsumerEJB!at.fhv.tvv.shared.ejb.MessageConsumer");
            messages = messageConsumer.getMessages(TVVApplication.getBenutzerName());

            //Tabelle für Messages generieren.
            TableColumn<String, MessageDTO> themaSpalte = new TableColumn<>("Thema");
            themaSpalte.setCellValueFactory(new PropertyValueFactory<>("topicName"));
            themaSpalte.setPrefWidth(400);
            themaSpalte.setResizable(false);

            TableColumn<String, MessageDTO> betreffSpalte = new TableColumn<>("Betreff");
            betreffSpalte.setCellValueFactory(new PropertyValueFactory<>("title"));
            betreffSpalte.setPrefWidth(500);
            betreffSpalte.setResizable(false);

            nachrichtenTV.getColumns().clear();
            nachrichtenTV.getItems().clear();
            nachrichtenTV.getColumns().add(themaSpalte);
            nachrichtenTV.getColumns().add(betreffSpalte);


            nachrichtenTV.setOnMouseClicked((MouseEvent mouseEvent) -> {
                if (mouseEvent.getClickCount() == 2) {
                    int nachrichtenIndex = nachrichtenTV.getSelectionModel().getSelectedIndex();
                    MessageDTO message = (MessageDTO) nachrichtenTV.getItems().get(nachrichtenIndex);
                    try {
                        FXMLLoader loader2 = new FXMLLoader(getClass().getResource("/at/fhv/tvv/frontendteamd/fxml/benachrichtigungen/TVV_Messagedetails.fxml"));
                        Parent root = loader2.load();
                        stage = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
                        double breite = ((Node) mouseEvent.getSource()).getScene().getWidth();
                        double hoehe = ((Node) mouseEvent.getSource()).getScene().getHeight();

                        Scene scene = new Scene(root, breite, hoehe);
                        stage.setScene(scene);
                        MessagedetailsController messagedetailsController = loader2.getController();
                        messagedetailsController.setMessage(message);
                        stage.show();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });

            for (MessageDTO message1 : messages) {
                nachrichtenTV.getItems().add(message1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        //BENACHRICHTIGUNGEN-ICON
        if (TVVApplication.messages.size() > 0) { //WENN MINDESTENS EINE NACHRICHT IM POSTEINGANG LIEGT.
            //Benachrichtigungen-Icon ändern
            benachrichtigungBild.setImage(new Image(getClass().getResource("images/Neue_Benachrichtigungen.png").toString()));
        }

        //WARENKORB-ICON
        try {
            if (TVVApplication.getWarenkorb().size() > 0) { //WENN MINDESTENS EIN TICKET IM WARENKORB LIEGT.
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
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        double breite = ((Node) event.getSource()).getScene().getWidth();
        double hoehe = ((Node) event.getSource()).getScene().getHeight();

        Scene scene = new Scene(root, breite, hoehe);
        stage.setScene(scene);

        stage.show();

    }

    @FXML
    protected void oeffneKundensuche(ActionEvent event) throws IOException {

        Parent root = FXMLLoader.load(getClass().getResource("/at/fhv/tvv/frontendteamd/fxml/kundensuche/TVV_Kundensuche.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        double breite = ((Node) event.getSource()).getScene().getWidth();
        double hoehe = ((Node) event.getSource()).getScene().getHeight();

        Scene scene = new Scene(root, breite, hoehe);
        stage.setScene(scene);

        stage.show();

    }

    @FXML
    protected void oeffneEventsuche(ActionEvent event) throws IOException {

        Parent root = FXMLLoader.load(getClass().getResource("/at/fhv/tvv/frontendteamd/fxml/eventsuche/TVV_Eventsuche.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        double breite = ((Node) event.getSource()).getScene().getWidth();
        double hoehe = ((Node) event.getSource()).getScene().getHeight();

        Scene scene = new Scene(root, breite, hoehe);
        stage.setScene(scene);

        stage.show();

    }

    @FXML
    protected void oeffneWarenkorb(ActionEvent event) throws IOException {

        Parent root = FXMLLoader.load(getClass().getResource("/at/fhv/tvv/frontendteamd/fxml/warenkorb/TVV_Warenkorb.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        double breite = ((Node) event.getSource()).getScene().getWidth();
        double hoehe = ((Node) event.getSource()).getScene().getHeight();

        Scene scene = new Scene(root, breite, hoehe);
        stage.setScene(scene);

        stage.show();

    }

    //FUNKTIONEN

    @FXML
    protected void oeffneAboEinstellungen(ActionEvent event) throws IOException {

        Parent root = FXMLLoader.load(getClass().getResource("/at/fhv/tvv/frontendteamd/fxml/benachrichtigungen/TVV_AboEinstellungen.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        double breite = ((Node) event.getSource()).getScene().getWidth();
        double hoehe = ((Node) event.getSource()).getScene().getHeight();

        Scene scene = new Scene(root, breite, hoehe);
        stage.setScene(scene);

        stage.show();

    }

    @FXML
    protected void oeffneMessageSenden(ActionEvent event) throws IOException {
        List<String> roles = TVVApplication.getRollen();
        if (!roles.contains("Operator")) {
            Notifications.create()
                    .title("Fehler!")
                    .text("Sie besitzen als Mitarbeiter-User keine Berechtigung um Messages zu versenden!")
                    .showWarning();
        } else {

            Parent root = FXMLLoader.load(getClass().getResource("/at/fhv/tvv/frontendteamd/fxml/benachrichtigungen/TVV_MessageSenden.fxml"));
            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            double breite = ((Node) event.getSource()).getScene().getWidth();
            double hoehe = ((Node) event.getSource()).getScene().getHeight();

            Scene scene = new Scene(root, breite, hoehe);
            stage.setScene(scene);

            stage.show();
        }

    }

}
