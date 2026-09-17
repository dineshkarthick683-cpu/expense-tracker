package com.example.demo;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.server.AppShellSettings;
import com.vaadin.flow.server.PWA;

//java -jar target/demo-0.0.1-SNAPSHOT.jar -Dvaadin.productionMode=true

//# Build the app with production profile
//mvn clean install -Pproduction
//
//# Run the app with production profile
//mvn spring-boot:run -Pproduction


@PWA(
        name = "Finance App",
        shortName = "Finance",
        iconPath = "money.png"   // matches src/main/resources/static/money.png
)
public class AppShell implements AppShellConfigurator {
    @Override
    public void configurePage(AppShellSettings settings) {
        settings.setViewport("width=device-width, initial-scale=1");
    }
}
