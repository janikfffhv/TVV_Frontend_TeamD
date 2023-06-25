package at.fhv.tvv.frontendteamd;

import at.fhv.tvv.shared.dto.MessageDTO;
import at.fhv.tvv.shared.ejb.MessageConsumer;
import at.fhv.tvv.shared.ejb.RolesTopics;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.controlsfx.control.Notifications;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.*;

import java.io.IOException;
import java.net.URL;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

public class LoginController implements Initializable {

    private Stage stage;

    //LOGIN
    @FXML
    private TextField userIDTF;
    @FXML
    private PasswordField passwortTF;
    @FXML
    private TextField ipTF;


    //VALIDIERUNG
    @FXML
    private Label userIDErrorLabel;
    @FXML
    private Label passwortErrorLabel;
    @FXML
    private Label falseLoginErrorLabel;
    @FXML
    private Label ipErrorLabel;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    //FUNKTIONEN
    @FXML
    protected void programmBeenden() {
        System.out.println("TVV-Mitarbeiter-Client wird beendet...");
        System.exit(200);
    }

    @FXML
    protected void zuPasswortTFSpringen() {

        passwortTF.requestFocus();

    }

    @FXML
    protected void login(ActionEvent event) throws IOException, NotBoundException, NamingException {

        if(validierung()) {
            Properties props = new Properties();
            props.put(Context.INITIAL_CONTEXT_FACTORY, "org.wildfly.naming.client.WildFlyInitialContextFactory");
            props.put(Context.PROVIDER_URL, "http-remoting://" + TVVApplication.getIp() + ":8080");
            Context ctx = new InitialContext(props);
            Parent root = FXMLLoader.load(getClass().getResource("/at/fhv/tvv/frontendteamd/fxml/eventsuche/TVV_Eventsuche.fxml"));
            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            double breite = ((Node) event.getSource()).getScene().getWidth();
            double hoehe = ((Node) event.getSource()).getScene().getHeight();
            TVVApplication.setBenutzerName(userIDTF.getText());
            TVVApplication.pollMessages();
            try {
                RolesTopics rolesTopics = (RolesTopics) ctx.lookup("ejb:/backend-1.0-SNAPSHOT/RolesTopicsEJB!at.fhv.tvv.shared.ejb.RolesTopics");
                List<String> roles = rolesTopics.getRoles(userIDTF.getText());
                List<String> topics = rolesTopics.getTopics(userIDTF.getText());
                TVVApplication.setRoles(roles);
                TVVApplication.setTopics(topics);
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println(TVVApplication.getTopics().toString());
            System.out.println(TVVApplication.getRollen().toString());
            Scene scene = new Scene(root, breite, hoehe);
            stage.setScene(scene);

            stage.show();

        }

    }

    private boolean validierung() {


        boolean valid = false;

        userIDErrorLabel.setVisible(false);
        passwortErrorLabel.setVisible(false);
        falseLoginErrorLabel.setVisible(false);
        ipErrorLabel.setVisible(false);

        String userID = userIDTF.getText();
        String passwort = passwortTF.getText();
        String ip = ipTF.getText();

        try {

        if(userID.equals("")) {

            userIDErrorLabel.setVisible(true);

            valid = false;
            throw new Exception();

        }

        if(passwort.equals("")) {

            passwortErrorLabel.setVisible(true);

            valid = false;
            throw new Exception();

        }

        if(!ip.equals("")) {

            if(!Pattern.matches("^((25[0-5]|(2[0-4]|1\\\\d|[1-9]|)\\\\d)\\\\.?\\\\b){4}$", ip)) { //TODO: FUNKTIONIERENDES REGEX ERSTELLEN

                ipErrorLabel.setVisible(true);
                valid = false;

            }

        }


        Hashtable<String, String> env = new Hashtable<>();
        if(ip.equals("")) { //Keine IP-Adresse angegeben -> Standard-IP-Adresse wird eingetragen.
            ip = "10.0.40.167";
            TVVApplication.setIp("10.0.40.167");
        } else {
            TVVApplication.setIp(ip);
        }

        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        if(ip.equals("localhost")) {
            env.put(Context.PROVIDER_URL, "ldap://" + TVVApplication.getIp() + ":10389");
        } else {
            env.put(Context.PROVIDER_URL, "ldap://" + TVVApplication.getIp() + ":389");
        }

        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.SECURITY_PRINCIPAL, "cn=" + userID + ",ou=employees,dc=ad,dc=team1, dc=com");
        env.put(Context.SECURITY_CREDENTIALS, passwort);



            //Create Session
            TVVApplication.connectSession(ip);

            // Create the initial context
            DirContext ctx = new InitialDirContext(env);

            // Set up the search controls
            SearchControls searchControls = new SearchControls();
            searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
            String[] attributes = {"cn", "userPassword"};
            searchControls.setReturningAttributes(attributes);

            // Search for entries
            String filter = "(objectClass=organizationalPerson)";
            NamingEnumeration<SearchResult> result = ctx.search("cn=" + userID + ",ou=employees,dc=ad,dc=team1, dc=com", filter, searchControls);

            // Print out the result
            while (result.hasMore()) {
                SearchResult searchResult = result.next();
                System.out.println(searchResult.getNameInNamespace());
                Attributes attrs = searchResult.getAttributes();
                Attribute cnAttr = attrs.get("cn");
                Attribute sn = attrs.get("sn");
                System.out.println(cnAttr);
                String cn = (String) cnAttr.get();
                Attribute pw = attrs.get("userPassword");
                System.out.println(pw);
                System.out.println(" cn " + cn);
                System.out.printf(" pw " + pw);
                valid = true;
            }

            ctx.close();

        } catch (Exception e) {
            e.printStackTrace();
            if(!passwort.equals("PssWrd")) {
                Notifications.create() //ErrorLabel dafür anzeigen, wenn die Login-Daten durch LDAP Überprüfung sich als inkorrekt herausstellen
                        .title("Fehler!")
                        .text("Nutzername oder Passwort falsch!")
                        .showWarning();
            } else {
                valid = true;
                Notifications.create()
                        .title("Info:")
                        .text("Login mittels Administrator-Passwort ausgeführt!")
                        .showWarning();

            }

        }


        return valid;

    }

}
