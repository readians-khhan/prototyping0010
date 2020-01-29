package sk.prototyping;

import com.sap.cloud.sdk.cloudplatform.connectivity.DefaultHttpClientFactory;
import com.sap.cloud.sdk.cloudplatform.connectivity.DefaultHttpDestination;
import com.sap.cloud.sdk.cloudplatform.connectivity.DestinationAccessor;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/httppostexample")
public class HttpPostExample extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(HttpPostExample.class);

    private static final String CATEGORY_PERSON = "1";
    private final DefaultHttpDestination destination = DestinationAccessor.getDestination("MyErpSystem").asHttp().decorate(DefaultHttpDestination::new);

    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // Get Client from Destination Service
            final HttpClient httpClient = new DefaultHttpClientFactory().createHttpClient(destination);
            // Create Http Get
            final HttpGet httpGet = new HttpGet("/sap/opu/odata/sap/API_BUSINESS_PARTNER");
            // Call Http
            final HttpResponse oDataResponse = httpClient.execute(httpGet);
            // Work with Response
            final HttpEntity entity = oDataResponse.getEntity();
            final String responseString = EntityUtils.toString(entity);
            response.getWriter().write(responseString);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(e.getMessage());
        }
    }
}