package at.fhv.tvv.frontendteamd;

import at.fhv.tvv.frontendteamd.model.CustomerList;
import at.fhv.tvv.shared.dto.CustomerSearchDTO;
import at.fhv.tvv.shared.ejb.CustomerSearch;
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
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

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
import java.util.regex.Pattern;

public class KundensucheController implements Initializable {

    private static Properties props = new Properties();
    private static Context ctx = null;
    private Stage stage;
    //WARENKORB
    @FXML
    private ImageView warenkorbBild;
    //BENACHRICHTIGUNG
    @FXML
    private ImageView benachrichtigungBild;
    //KUNDENSUCHE
    @FXML
    private TextField suchbegriffTF;
    //VALIDIERUNG
    @FXML
    private Label suchbegriffErrorLabel;
    //KUNDEN-TABELLE
    @FXML
    private TableView/*<KundenDto>*/ kundenTV; //TODO: KundenDto erstellen

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
    protected void oeffneBenachrichtigungen(ActionEvent event) throws IOException {

        Parent root = FXMLLoader.load(getClass().getResource("/at/fhv/tvv/frontendteamd/fxml/benachrichtigungen/TVV_Benachrichtigungen.fxml"));
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
    protected void sucheKunden(ActionEvent event) throws IOException {

        //Methode aus Domain Layer aufrufen, welche die Event-Suche einleitet. //TODO: KUNDENSUCHE IMPLEMENTIEREN

        if (validierung()) {


            //Methode aus Domain Layer aufrufen, welche die Event-Suche einleitet.
            try {
                List<CustomerSearchDTO> kunden;
                CustomerSearch customerSearch = (CustomerSearch) ctx.lookup("ejb:/backend-1.0-SNAPSHOT/CustomerSearchEJB!at.fhv.tvv.shared.ejb.CustomerSearch");
                kunden = customerSearch.searchByString(suchbegriffTF.getText());

                TableColumn<String, CustomerList> nameSpalte = new TableColumn<>("NAME");
                nameSpalte.setCellValueFactory(new PropertyValueFactory<>("name"));
                nameSpalte.setPrefWidth(350);
                nameSpalte.setResizable(false);

                TableColumn<Integer, CustomerList> datumSpalte = new TableColumn<>("GEBURTSDATUM");
                datumSpalte.setCellValueFactory(new PropertyValueFactory<>("geburtsdatum"));
                datumSpalte.setPrefWidth(200);
                datumSpalte.setResizable(false);

                TableColumn<Integer, CustomerList> adresseSpalte = new TableColumn<>("ADRESSE");
                adresseSpalte.setCellValueFactory(new PropertyValueFactory<>("adresse"));
                adresseSpalte.setPrefWidth(350);
                adresseSpalte.setResizable(false);

                kundenTV.getColumns().clear();
                kundenTV.getItems().clear();
                kundenTV.getColumns().add(nameSpalte);
                kundenTV.getColumns().add(datumSpalte);
                kundenTV.getColumns().add(adresseSpalte);

                kundenTV.setOnMouseClicked((MouseEvent mouseEvent) -> {
                    if (mouseEvent.getClickCount() == 2) {
                        int eventIndex = kundenTV.getSelectionModel().getSelectedIndex();
                        CustomerList customerId = (CustomerList) kundenTV.getItems().get(eventIndex);
                        try {
                            System.out.println(customerId.getId());
                            FXMLLoader loader3 = new FXMLLoader(getClass().getResource("/at/fhv/tvv/frontendteamd/fxml/kundeninfo/TVV_Kundeninfo.fxml"));
                            Parent root = loader3.load();
                            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                            double breite = ((Node) event.getSource()).getScene().getWidth();
                            double hoehe = ((Node) event.getSource()).getScene().getHeight();

                            Scene scene = new Scene(root, breite, hoehe);
                            stage.setScene(scene);
                            System.out.println("Gegebener Kunde: " + customerId.getId().toString());
                            KundeninfoController kundenInfoController = loader3.getController();
                            kundenInfoController.sucheKunde(customerId.getId());
                            stage.show();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });

                for (CustomerSearchDTO kunde : kunden) {
                    String name = kunde.getVorname() + " " + kunde.getNachname();
                    String adresse = kunde.getHausnummer() + " " + kunde.getStrasse() + ", " + kunde.getPlz() + " " + kunde.getOrt();
                    CustomerList kunde2 = new CustomerList(kunde.getCustomerId(), name, kunde.getGeburtsdatum(), adresse);
                    kundenTV.getItems().add(kunde2);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    @FXML
    protected void oeffneKundeninfo(ActionEvent event) throws IOException {

        Parent root = FXMLLoader.load(getClass().getResource("/at/fhv/tvv/frontendteamd/fxml/kundeninfo/TVV_Kundeninfo.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        double breite = ((Node) event.getSource()).getScene().getWidth();
        double hoehe = ((Node) event.getSource()).getScene().getHeight();

        Scene scene = new Scene(root, breite, hoehe);
        stage.setScene(scene);

        stage.show();

    }

    private boolean validierung() {

        boolean valid = true;

        suchbegriffErrorLabel.setVisible(false);

        String suchbegriff = suchbegriffTF.getText();

        if (!Pattern.matches("^[a-zA-Z0-9 äÄöÖüÜß-]{3,}$", suchbegriff)) {

            suchbegriffErrorLabel.setVisible(true);
            valid = false;

        }

        return valid;

    }

}
