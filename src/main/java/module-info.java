module at.fhv.tvv.frontendteamd {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.rmi;
    requires beta.jboss.ejb.api_3_2;
    requires java.naming;
    requires tvv.shared;


    requires org.controlsfx.controls;

    opens at.fhv.tvv.frontendteamd to javafx.fxml;
    opens at.fhv.tvv.frontendteamd.model to javafx.base, javafx.fxml;
    exports at.fhv.tvv.frontendteamd;
}