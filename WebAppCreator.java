// General imports
import java.net.URI;
import java.util.ArrayList;

// Imports for exceptions
import java.io.IOException;
import java.net.URISyntaxException;
import javax.xml.parsers.ParserConfigurationException;
import com.microsoft.windowsazure.exception.ServiceException;
import org.xml.sax.SAXException;

// Imports for Azure App Service management configuration
import com.microsoft.windowsazure.Configuration;
import com.microsoft.windowsazure.management.configuration.ManagementConfiguration;

// Service management imports for Azure Web Apps creation
import com.microsoft.windowsazure.management.websites.*;
import com.microsoft.windowsazure.management.websites.models.*;

// Imports for authentication
import com.microsoft.windowsazure.core.utils.KeyStoreType;


public class WebAppCreator {

    // Parameter definitions used for authentication.
    private static String uri = "https://management.core.windows.net/";
    private static String subscriptionId = "4025e99d-5de8-4da9-9555-3c31698595c3";
    private static String keyStoreLocation = "c:\\certificates\\jamlcert1027.jks";
    private static String keyStorePassword = "Poindexter";

    // Define Web Apps parameter values.
    private static String webAppName = "WebDemoWebApp0402";
    private static String domainName = ".azurewebsites.net";
    private static String webSpaceName = WebSpaceNames.WESTUSWEBSPACE;
    private static String hostingPlanName = "WebDemoAppServicePlan0402";

    private static void createWebApp() throws Exception {

        // Specify configuration settings for the App Service management client.
        Configuration config = ManagementConfiguration.configure(
            new URI(uri),
            subscriptionId,
            keyStoreLocation,  // Path to the JKS file
            keyStorePassword,  // Password for the JKS file
            KeyStoreType.jks   // Flag that you are using a JKS keystore
        );

        // Create the Web App management client to call Azure APIs
        // and pass it the App Service management configuration object.
        WebSiteManagementClient webAppManagementClient = WebSiteManagementService.create(config);

        // Create an Azure App Service plan for the Azure Web App with the specified parameters.
        WebHostingPlanCreateParameters hostingPlanParams = new WebHostingPlanCreateParameters();
        hostingPlanParams.setName(hostingPlanName);
        hostingPlanParams.setSKU(SkuOptions.Free);
        webAppManagementClient.getWebHostingPlansOperations().create(webSpaceName, hostingPlanParams);

        // Set webspace parameters.
        WebSiteCreateParameters.WebSpaceDetails webSpaceDetails = new WebSiteCreateParameters.WebSpaceDetails();
        webSpaceDetails.setGeoRegion(GeoRegionNames.WESTUS);
        webSpaceDetails.setPlan(WebSpacePlanNames.VIRTUALDEDICATEDPLAN);
        webSpaceDetails.setName(webSpaceName);

        // Set Web App parameters.
        // The server farm name must be the same as the Azure App Service plan name.
        WebSiteCreateParameters webAppCreateParameters = new WebSiteCreateParameters();
        webAppCreateParameters.setName(webAppName);
        webAppCreateParameters.setServerFarm(hostingPlanName);
        webAppCreateParameters.setWebSpace(webSpaceDetails);

        // Set usage metrics attributes.
        WebSiteGetUsageMetricsResponse.UsageMetric usageMetric = new WebSiteGetUsageMetricsResponse.UsageMetric();
        usageMetric.setSiteMode(WebSiteMode.Basic);
        usageMetric.setComputeMode(WebSiteComputeMode.Shared);

        // Define the Web App object.
        ArrayList<String> fullWebAppName = new ArrayList<String>();
        fullWebAppName.add(webAppName + domainName);
        WebSite webApp = new WebSite();
        webApp.setHostNames(fullWebAppName);

        // Create the Azure Web App.
        WebSiteCreateResponse webAppCreateResponse = webAppManagementClient.getWebSitesOperations().create(webSpaceName, webAppCreateParameters);

        // Output the HTTP status code of the response; 200 indicates the request succeeded; 4xx indicates failure.
        System.out.println("----------");
        System.out.println("Website created - HTTP response " + webAppCreateResponse.getStatusCode() + "\n");

        // Output the name of the Azure Web App that this application created.
        String shinyNewWebAppName = webAppCreateResponse.getWebSite().getName();
        System.out.println("----------\n");
        System.out.println("Name of Web App created: " + shinyNewWebAppName + "\n");
        System.out.println("----------\n");
    }

    public static void main(String[] args)
        throws IOException, URISyntaxException, ServiceException,
        ParserConfigurationException, SAXException, Exception {

        // Create Azure Web App
        createWebApp();

    }  // end of main()

}  // end of WebAppCreator class
