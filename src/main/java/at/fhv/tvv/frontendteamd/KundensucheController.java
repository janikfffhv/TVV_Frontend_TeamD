package at.fhv.tvv.frontendteamd;

import at.fhv.tvv.frontendteamd.model.CustomerList;
import at.fhv.tvv.shared.dto.CustomerSearchDTO;
import at.fhv.tvv.shared.rmi.CustomerSearch;
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
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.rmi.Naming;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

public class KundensucheController implements Initializable {

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

    @FXML
    private TableColumn nameSpalte;

    @FXML
    private TableColumn geburtsdatumSpalte;

    @FXML
    private TableColumn adresseSpalte;



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

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
    protected void sucheKunden(ActionEvent event) throws IOException {

        //Methode aus Domain Layer aufrufen, welche die Event-Suche einleitet. //TODO: KUNDENSUCHE IMPLEMENTIEREN

        if(validierung()) {

            System.out.println("Suchen...");

            /**Parent root = FXMLLoader.load(getClass().getResource("/at/fhv/tvv/frontendteamd/fxml/kundeninfo/TVV_Kundeninfo.fxml")); //SPÄTER ENTFERNEN!
             stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
             double breite = ((Node)event.getSource()).getScene().getWidth();
             double hoehe = ((Node)event.getSource()).getScene().getHeight();

             Scene scene = new Scene(root, breite, hoehe);
             stage.setScene(scene);

             stage.show();**/

            //Methode aus Domain Layer aufrufen, welche die Event-Suche einleitet. //TODO: EVENTSUCHE IMPLEMENTIEREN
            System.out.println("Suchen...");
            try {
                List<CustomerSearchDTO> kunden;
                CustomerSearch customerSearch = (CustomerSearch) Naming.lookup("rmi://" + TVVApplication.getIp() + "/customerSearch");
                kunden = customerSearch.searchByString(suchbegriffTF.getText());
                TableColumn<Integer, CustomerList> idSpalte = new TableColumn<> ("Kunden-ID");
                idSpalte.setCellValueFactory(new PropertyValueFactory<>("id"));
                TableColumn<String, CustomerList> nameSpalte = new TableColumn<> ("Name");
                nameSpalte.setCellValueFactory(new PropertyValueFactory<>("name"));
                TableColumn<Integer, CustomerList> datumSpalte = new TableColumn<>("Geburtsdatum");
                datumSpalte.setCellValueFactory(new PropertyValueFactory<> ("geburtsdatum"));
                kundenTV.getColumns().clear();
                kundenTV.getItems().clear();
                kundenTV.getColumns().add(idSpalte);
                kundenTV.getColumns().add(nameSpalte);
                kundenTV.getColumns().add(datumSpalte);

                kundenTV.setOnMouseClicked((MouseEvent mouseEvent) -> {
                    if(mouseEvent.getClickCount() == 2) {
                        int eventIndex = kundenTV.getSelectionModel().getSelectedIndex();
                        CustomerList customerId = (CustomerList) kundenTV.getItems().get(eventIndex);
                        try {
                            System.out.println(customerId.getId());
                            FXMLLoader loader3 = new FXMLLoader(getClass().getResource("/at/fhv/tvv/frontendteamd/fxml/kundeninfo/TVV_Kundeninfo.fxml"));
                            Parent root = loader3.load();
                            stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
                            double breite = ((Node)event.getSource()).getScene().getWidth();
                            double hoehe = ((Node)event.getSource()).getScene().getHeight();

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
    protected void oeffneKundeninfo(ActionEvent event) throws IOException {

        Parent root = FXMLLoader.load(getClass().getResource("/at/fhv/tvv/frontendteamd/fxml/kundeninfo/TVV_Kundeninfo.fxml"));
        stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        double breite = ((Node)event.getSource()).getScene().getWidth();
        double hoehe = ((Node)event.getSource()).getScene().getHeight();

        Scene scene = new Scene(root, breite, hoehe);
        stage.setScene(scene);

        stage.show();

    }

    private boolean validierung() {

        boolean valid = true;

        suchbegriffErrorLabel.setVisible(false);

        String suchbegriff = suchbegriffTF.getText();

        if(!Pattern.matches("^[a-zA-Z0-9 äÄöÖüÜß-]{3,}$", suchbegriff)) {

            suchbegriffErrorLabel.setVisible(true);
            valid = false;

        }

        return valid;

    }

}
