package sk.prototyping;

import com.sap.cloud.sdk.cloudplatform.connectivity.DefaultHttpClientFactory;
import com.sap.cloud.sdk.cloudplatform.connectivity.DefaultHttpDestination;
import com.sap.cloud.sdk.cloudplatform.connectivity.DestinationAccessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;


@WebServlet("/httppostsci")
public class HttpPostSCIExample {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(HttpGetExample.class);

    private static final String CATEGORY_PERSON = "1";
    private final DefaultHttpDestination destination = DestinationAccessor.getDestination("DEST_SCI").asHttp().decorate(DefaultHttpDestination::new);
    
	private final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_3) Version/7.0.3 Safari/7046A194A";
 
	public String postMessage() throws IOException {
        // Get Client from Destination Service
        final HttpClient httpClient = new DefaultHttpClientFactory().createHttpClient(destination);
        //HttpClient httpclient = HttpClientBuilder.create().build();
		final String parameters = "?name=tecbar&group=middleschool";
		HttpPost httppost = new HttpPost(parameters);
		httppost.setHeader(HTTP.USER_AGENT, USER_AGENT);
		httppost.setHeader(HTTP.CONTENT_TYPE, "text/xml; charset=UTF-8");
 
		final String msg = "<ns0:MCNS_0015_Customer_Request xmlns:ns0=\"http://schema.mcns.com/EPR/SAP/MCNS_0015_Customer\">\n" + 
				"    <I_KUNNR>string</I_KUNNR>\n" + 
				"    <I_NAME1>string</I_NAME1>\n" + 
				"</ns0:MCNS_0015_Customer_Request>";
		httppost.setEntity(new StringEntity(msg));
 
		HttpResponse httpResponse = httpclient.execute(httppost);
		HttpEntity entity = httpResponse.getEntity();
		
		return EntityUtils.toString(entity);
	}
 
	public static void main(String[] args) {
		try {
			HttpPostSCIExample instance = new HttpPostSCIExample();
			String response = instance.postMessage();
			System.out.println(response);
		} catch (IOException ex) {
			// process the exception
		}
	}
 
}