package de.alexanderwodarz.code.cloudflare;

import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.client.urlconnection.HTTPSProperties;
import de.alexanderwodarz.code.rest.ClientThread;
import lombok.Getter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;
import java.util.HashMap;

@Getter
public class Request {

    private String response;
    private JSONObject responseObject;
    private JSONArray responseArray;

    public Request(CloudFlare cf, String path, ClientThread.RequestMethod method, String body) {
        String url = "https://api.cloudflare.com/client/v4" + path;
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + cf.getBearer());

        ClientConfig config = new DefaultClientConfig();
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }};
            SSLContext ctx = SSLContext.getInstance("SSL");
            ctx.init(null, trustAllCerts, null);
            config.getProperties().put(HTTPSProperties.PROPERTY_HTTPS_PROPERTIES, new HTTPSProperties((hostname, session) -> true, ctx));
        } catch (Exception ignored) {
        }
        ClientThread thread = new ClientThread(url, method, config);
        thread.setHeaders(headers);
        if (body != null && body.length() > 0)
            thread.setBody(new JSONObject(body));
        thread.run();
        while (thread.isAlive()) {
        }
        response = thread.getResponse();
        try {
            responseObject = new JSONObject(thread.getResponse());
        } catch (JSONException e) {
            if (e.getMessage().equals("A JSONObject text must begin with '{' at 1 [character 2 line 1]")) {
                responseArray = new JSONArray(thread.getResponse());
            } else {
                e.printStackTrace();
            }
        }
    }

}
