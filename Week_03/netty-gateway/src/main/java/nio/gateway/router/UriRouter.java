package nio.gateway.router;

import org.apache.http.client.HttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Random;


public class UriRouter implements HttpEndpointRouter {
    private static final Logger LOG = LoggerFactory.getLogger(UriRouter.class);
    static Random random = new Random();
    @Override
    public String route(List<String> endpoints) {
        return this.fullRandom(endpoints);
    }

    private String fullRandom(List<String> endpoints) {
        int number = random.nextInt(endpoints.size());
        return endpoints.get(number);
    }


}
