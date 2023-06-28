package at.fhv.tvv.frontendteamd;

import at.fhv.tvv.shared.dto.MessageDTO;
import at.fhv.tvv.shared.dto.WarenkorbZeileDTO;
import at.fhv.tvv.shared.ejb.MessageConsumer;
import at.fhv.tvv.shared.ejb.TvvSession;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.controlsfx.control.Notifications;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TVVApplication extends Application {
    public static TvvSession tvvSession;
    public static String ip;
    public static List<MessageDTO> messages = new ArrayList<>();

    private static Properties props = new Properties();
    private static Context ctx = null;

    public static List<WarenkorbZeileDTO> getWarenkorb() throws RemoteException {
        return tvvSession.getWarenkorb();
    }

    public static boolean hinzufuegen(WarenkorbZeileDTO zeile) throws RemoteException {
        for (WarenkorbZeileDTO item : tvvSession.getWarenkorb()) {
            if (item.getPlatzId() == zeile.getPlatzId()) {
                return false;
            }
        }
        tvvSession.hinzufuegen(zeile);
        return true;
    }

    public static void loeschen(WarenkorbZeileDTO zeile) throws RemoteException {
        tvvSession.loeschen(zeile);
    }

    public static void connectSession(String ip) throws RemoteException {

        try {
            TVVApplication.setIp(ip);
            props.put(Context.INITIAL_CONTEXT_FACTORY, "org.wildfly.naming.client.WildFlyInitialContextFactory");
            props.put(Context.PROVIDER_URL, "http-remoting://" + TVVApplication.getIp() + ":8080");
            ctx = new InitialContext(props);
            tvvSession = (TvvSession) ctx.lookup("ejb:/backend-1.0-SNAPSHOT/TvvSessionImplEJB!at.fhv.tvv.shared.ejb.TvvSession?stateful");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void leeren() throws RemoteException {
        tvvSession.leeren();
    }

    public static String getIp() {

        System.out.println("IP: " + ip);
        return ip;
    }

    public static void setIp(String ipadd) {
        TVVApplication.ip = ipadd;
    }

    public static void hinzufuegenKunde(UUID uuid) throws RemoteException {
        tvvSession.hinzufuegenKunde(uuid);
    }

    public static void hinzufuegenZahlungsmethode(String s) throws RemoteException {
        tvvSession.hinzufuegenZahlungsMethode(s);
    }

    public static String getZahlungsmethode() throws RemoteException {
        return tvvSession.getZahlungsMethode();
    }

    public static UUID getKunde() throws RemoteException {
        return tvvSession.getKunde();
    }

    public static String getBenutzerName() throws RemoteException {
        return tvvSession.getBenutzerName();
    }

    public static void setBenutzerName(String s) throws RemoteException {
        tvvSession.setBenutzerName(s);
    }

    public static List<String> getRollen() throws RemoteException {
        return tvvSession.getRollen();
    }

    public static List<String> getTopics() throws RemoteException {
        return tvvSession.getTopics();
    }

    public static void setTopics(List<String> topics) throws RemoteException {
        tvvSession.setTopics(topics);
    }

    public static void setRoles(List<String> roles) throws RemoteException {
        tvvSession.setRollen(roles);
    }

    public static void showInfo() {
        Notifications.create()
                .title("Neue Nachricht!")
                .text("Sie haben eine neue Nachricht erhalten!")
                .showInformation();
    }

    public static void pollMessages() throws RemoteException, NamingException {
        MessageConsumer messageConsumer = (MessageConsumer) ctx.lookup("ejb:/backend-1.0-SNAPSHOT/MessageConsumerEJB!at.fhv.tvv.shared.ejb.MessageConsumer");
        messages = messageConsumer.getMessages(TVVApplication.getBenutzerName());
        ScheduledExecutorService scheduledExecutorService =
                Executors.newScheduledThreadPool(5);
        scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            public void run() {
                try {

                    MessageConsumer messageConsumer = (MessageConsumer) ctx.lookup("ejb:/backend-1.0-SNAPSHOT/MessageConsumerEJB!at.fhv.tvv.shared.ejb.MessageConsumer");
                    List<MessageDTO> messages2 = messageConsumer.getMessages(TVVApplication.getBenutzerName());
                    if (messages2.size() > messages.size() && !messages2.equals(messages)) {
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                TVVApplication.showInfo();
                            }
                        });
                        messages = messages2;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }, 5, 5, TimeUnit.SECONDS);
    }

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(TVVApplication.class.getResource("/at/fhv/tvv/frontendteamd/fxml/login/TVV_Login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1500, 700);

        Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/at/fhv/tvv/frontendteamd/images/Logo.png")));
        stage.getIcons().add(icon);
        stage.setTitle("Ticket Verkauf Vorarlberg - Hier werden Ticketträume wahr!");

        stage.setScene(scene);
        stage.show();

    }
}
