package sk.prototyping;

import com.sap.cloud.sdk.cloudplatform.connectivity.DefaultHttpClientFactory;
import com.sap.cloud.sdk.cloudplatform.connectivity.DefaultHttpDestination;
import com.sap.cloud.sdk.cloudplatform.connectivity.DestinationAccessor;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.cloudfoundry.identity.client.UaaContext;
import org.cloudfoundry.identity.client.UaaContextFactory;
import org.cloudfoundry.identity.client.token.GrantType;
import org.cloudfoundry.identity.client.token.TokenRequest;
import org.cloudfoundry.identity.uaa.oauth.token.CompositeAccessToken;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;

@WebServlet("/prototypingcustomerwas")
public class PrototypingCustomerWAS extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(PrototypingCustomerWAS.class);

    private static final String CATEGORY_PERSON = "1";
    private final DefaultHttpDestination destination = DestinationAccessor.getDestination("DEST_SCI").asHttp().decorate(DefaultHttpDestination::new);

    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // System Variable - Destination Service
            JSONObject jsonObj = new JSONObject(System.getenv("VCAP_SERVICES"));
            JSONArray jsonArrSci = jsonObj.getJSONArray("it-rt");
            JSONObject credentialsSci = jsonArrSci.getJSONObject(0).getJSONObject("credentials");
            JSONObject oauthSci = credentialsSci.getJSONObject("oauth");
            String clientid = oauthSci.getString("clientid");
            String clientsecret = oauthSci.getString("clientsecret");
            URI oauthUrl = new URI(oauthSci.getString("tokenurl"));

            // JWT Token
            UaaContextFactory factory = UaaContextFactory.factory(oauthUrl);
            TokenRequest tokenRequest = factory.tokenRequest();
            tokenRequest.setGrantType(GrantType.CLIENT_CREDENTIALS);
            tokenRequest.setClientId(clientid);
            tokenRequest.setClientSecret(clientsecret);
            UaaContext xsUaaContext = factory.authenticate(tokenRequest);
            CompositeAccessToken jwtToken = xsUaaContext.getToken();

            // Method : POST /http/skcmcns/Customer
            final HttpClient httpClient = new DefaultHttpClientFactory().createHttpClient(destination);
            final String sciPath = "/http/WS/Customer";
            final HttpPost httpPost = new HttpPost(sciPath);
            final String requestString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><ns0:MCNS_0015_Customer_Request xmlns:ns0=\"http://schema.mcns.com/EPR/SAP/MCNS_0015_Customer\">\n" +
                    "    <I_KUNNR>string</I_KUNNR>\n" +
                    "    <I_NAME1>string</I_NAME1>\n" +
                    "</ns0:MCNS_0015_Customer_Request>";
            httpPost.setEntity(new StringEntity(requestString));
            httpPost.addHeader("authorization", "BEARER " + jwtToken);
            final HttpResponse oDataResponse = httpClient.execute(httpPost);
            String responseString = EntityUtils.toString(oDataResponse.getEntity());

            response.getWriter().write(responseString);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(e.getMessage());
        }
    }
}