// General imports
import java.net.URI;
import java.util.ArrayList;

// Imports for exceptions
import java.io.IOException;
import java.net.URISyntaxException;
import javax.xml.parsers.ParserConfigurationException;
import com.microsoft.windowsazure.exception.ServiceException;
import org.xml.sax.SAXException;

// Imports for service management configuration
import com.microsoft.windowsazure.Configuration;
import com.microsoft.windowsazure.management.configuration.ManagementConfiguration;

// Service management imports for website creation
import com.microsoft.windowsazure.management.websites.*;
import com.microsoft.windowsazure.management.websites.models.*;

// Imports for authentication
import com.microsoft.windowsazure.core.utils.KeyStoreType;


public class WebCreator {

	  // Parameter definitions used for authentication.
	  private static String uri = "https://management.core.windows.net/";
	  private static String subscriptionId = "4025e99d-5de8-4da9-9555-3c31698595c3";
	  private static String keyStoreLocation = "c:\\certificates\\jamlcert1027.jks";
	  private static String keyStorePassword = "Poindexter";

	  // Define website parameter values.
	  private static String websiteName = "WebDemoWebsite0324";
	  private static String domainName = ".azurewebsites.net";
	  private static String webSpaceName = WebSpaceNames.WESTUSWEBSPACE;
	  private static String hostingPlanName = "WebDemoHostingPlan0324";

	  private static void createWebSite() throws Exception {

	      // Specify configuration settings for the service management client.
	      Configuration config = ManagementConfiguration.configure(
	          new URI(uri),
	          subscriptionId,
	          keyStoreLocation,  // Path to the JKS file
	          keyStorePassword,  // Password for the JKS file
	          KeyStoreType.jks   // Flag that you are using a JKS keystore
	          );

	      // Create the website management client to call Azure APIs
	      // and pass it the service management configuration object.
	      WebSiteManagementClient webSiteManagementClient = WebSiteManagementService.create(config);

	      // Set web hosting plan parameters.
          // This creates a web hosting plan for the webspace with the specified parameters.
	      WebHostingPlanCreateParameters hostingPlanParams = new WebHostingPlanCreateParameters();
	      hostingPlanParams.setName(hostingPlanName);
	      hostingPlanParams.setSKU(SkuOptions.Free);
	      webSiteManagementClient.getWebHostingPlansOperations().create(webSpaceName, hostingPlanParams);

	      // Set webspace parameters.
	      WebSiteCreateParameters.WebSpaceDetails webSpaceDetails = new WebSiteCreateParameters.WebSpaceDetails();
	      webSpaceDetails.setGeoRegion(GeoRegionNames.WESTUS);
	      webSpaceDetails.setPlan(WebSpacePlanNames.VIRTUALDEDICATEDPLAN);
	      webSpaceDetails.setName(webSpaceName);

	      // Set website parameters.
	      // The server farm name must be the same as the web hosting plan name.
	      WebSiteCreateParameters webSiteCreateParameters = new WebSiteCreateParameters();
	      webSiteCreateParameters.setName(websiteName);
	      webSiteCreateParameters.setServerFarm(hostingPlanName);
	      webSiteCreateParameters.setWebSpace(webSpaceDetails);

	      // Set site mode and compute mode.
	      WebSiteGetUsageMetricsResponse.UsageMetric usageMetric = new WebSiteGetUsageMetricsResponse.UsageMetric();
	      usageMetric.setSiteMode(WebSiteMode.Basic);
	      usageMetric.setComputeMode(WebSiteComputeMode.Shared);

          // Define the website object.
          // The full website name is the website name concatenated with the domain name.
		  ArrayList<String> fullWebsiteName = new ArrayList<String>();
	      fullWebsiteName.add(websiteName + domainName);
	      WebSite website = new WebSite();
	      website.setHostNames(fullWebsiteName);

	      // Create the website.
	      WebSiteCreateResponse webSiteCreateResponse = webSiteManagementClient.getWebSitesOperations().create(webSpaceName, webSiteCreateParameters);

	      // Output the HTTP status code of the response; 200 indicates the request succeeded; 4xx indicates failure.
	      System.out.println("----------");
	      System.out.println("Website created - HTTP response " + webSiteCreateResponse.getStatusCode() + "\n");

	      // Output name of the website this app created.
	      // getName retrieves the name of the web site associated with the website response object.
	      String websitename = webSiteCreateResponse.getWebSite().getName();
	      System.out.println("----------\n");
	      System.out.println("Name of website created: " + websitename + "\n");
	      System.out.println("----------\n");
	  }

	public static void main(String[] args)
		    throws IOException, URISyntaxException, ServiceException,
		    ParserConfigurationException, SAXException, Exception {

	    // Create website
		  createWebSite();

	}  // end of main()

}  // end of WebCreator class
