package at.fhv.tvv.frontendteamd;

import at.fhv.tvv.frontendteamd.model.CustomerList;
import at.fhv.tvv.shared.dto.CustomerSearchDTO;
import at.fhv.tvv.shared.rmi.CustomerSearch;
import javafx.collections.FXCollections;
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

import java.io.IOException;
import java.net.URL;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

public class KaufvorgangDateneingabeController implements Initializable {

    private Stage stage;

    //BENACHRICHTIGUNG
    @FXML
    private ImageView benachrichtigungBild;

    //KAUFVORGANG - DATENEINGABE
    @FXML
    private TextField suchbegriffTF;
    @FXML
    private ChoiceBox<String> zahlungsmethodeCB;

    //VALIDIERUNG
    @FXML
    private Label suchbegriffErrorLabel;
    @FXML
    private Label kundenwahlErrorLabel;
    @FXML
    private Label zahlungsmethodeErrorLabel;


    //KUNDEN-TABELLE
    @FXML
    private TableView/*<KundenDto>*/ kundenTV;

    @FXML
    private TableColumn<String, CustomerList> nameSpalte;

    @FXML
    private TableColumn<String, CustomerList> geburtsdatumSpalte;

    @FXML
    private TableColumn<String, CustomerList> adresseSpalte;



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        zahlungsmethodeCB.setItems(FXCollections.observableArrayList("Kreditkarte", "PayPal", "GooglePay"));

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
    protected void zurueckZumWarenkorb(ActionEvent event) throws IOException {

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
    protected void sucheKunden(ActionEvent event) throws IOException {

        //Methode aus Domain Layer aufrufen, welche die Event-Suche einleitet.

        if(suchbegriffValidierung()) {
            try {
                List<CustomerSearchDTO> kunden;
                CustomerSearch customerSearch = (CustomerSearch) Naming.lookup("rmi://" + TVVApplication.getIp() + "/customerSearch");
                kunden = customerSearch.searchByString(suchbegriffTF.getText());
                nameSpalte.setCellValueFactory(new PropertyValueFactory<>("name"));
                nameSpalte.setPrefWidth(519);
                geburtsdatumSpalte.setCellValueFactory(new PropertyValueFactory<> ("geburtsdatum"));
                geburtsdatumSpalte.setPrefWidth(475);
                adresseSpalte.setCellValueFactory(new PropertyValueFactory<>("adresse"));
                adresseSpalte.setPrefWidth(430);
                kundenTV.getColumns().clear();
                kundenTV.getItems().clear();
                kundenTV.getColumns().add(nameSpalte);
                kundenTV.getColumns().add(geburtsdatumSpalte);
                kundenTV.getColumns().add(adresseSpalte);

                kundenTV.setOnMouseClicked((MouseEvent mouseEvent) -> {
                    if(mouseEvent.getClickCount() == 2) {
                        int eventIndex = kundenTV.getSelectionModel().getSelectedIndex();
                        CustomerList customerId = (CustomerList) kundenTV.getItems().get(eventIndex);
                            System.out.println(customerId.getId());
                        try {
                            TVVApplication.hinzufuegenKunde(customerId.getId());
                            Notifications.create()
                                    .title("Hinzugefügt!")
                                    .text("Kunde " + customerId.getName() + " wurde hinzugefügt!")
                                    .showConfirm();
                        } catch (RemoteException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });

                for(CustomerSearchDTO kunde : kunden) {
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
    protected void zurZusammenfassung(ActionEvent event) throws IOException {

        if(validierung()) {
            TVVApplication.hinzufuegenZahlungsmethode(zahlungsmethodeCB.getValue());
            Parent root = FXMLLoader.load(getClass().getResource("/at/fhv/tvv/frontendteamd/fxml/kaufvorgang/TVV_KaufvorgangZusammenfassung.fxml"));
            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            double breite = ((Node) event.getSource()).getScene().getWidth();
            double hoehe = ((Node) event.getSource()).getScene().getHeight();

            Scene scene = new Scene(root, breite, hoehe);
            stage.setScene(scene);

            stage.show();

        }

    }

    private boolean suchbegriffValidierung() {

        boolean valid = true;

        suchbegriffErrorLabel.setVisible(false);

        String suchbegriff = suchbegriffTF.getText();

        if(!Pattern.matches("^[a-zA-Z0-9 äÄöÖüÜß-]{3,}$", suchbegriff)) {

            suchbegriffErrorLabel.setVisible(true);
            valid = false;

        }

        return valid;

    }

    private boolean validierung() {

        boolean valid = true;

        kundenwahlErrorLabel.setVisible(false);
        zahlungsmethodeErrorLabel.setVisible(false);

        if(kundenTV.getSelectionModel().getSelectedItem() == null) {

            kundenwahlErrorLabel.setVisible(true);

            kundenTV.getSelectionModel().clearSelection();

            valid = false;

        }

        if(zahlungsmethodeCB.getValue() == null) {

            zahlungsmethodeErrorLabel.setVisible(true);

            valid = false;

        }

        return valid;

    }

}
