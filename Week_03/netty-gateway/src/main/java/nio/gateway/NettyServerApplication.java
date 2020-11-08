package nio.gateway;

import nio.gateway.inbound.HttpInboundServer;

import java.util.ArrayList;
import java.util.List;

public class NettyServerApplication {

    public final static String GATEWAY_NAME = "NIOGateway";
    public final static String GATEWAY_VERSION = "1.0.0";

    public static void main(String[] args) {
        String proxyServer1 = System.getProperty("proxyServer","http://localhost:8081");
        String proxyServer2 = System.getProperty("proxyServer","http://localhost:8082");
        String proxyServer3 = System.getProperty("proxyServer","http://localhost:8083");
        String proxyPort = System.getProperty("proxyPort","8888");

        //  http://localhost:8888/api/hello  ==> gateway API
        //  http://localhost:8088/api/hello  ==> backend service

        int port = Integer.parseInt(proxyPort);
        List<String> proxyServerList = new ArrayList<>();
        proxyServerList.add(proxyServer1);
        proxyServerList.add(proxyServer2);
        proxyServerList.add(proxyServer3);
        System.out.println(GATEWAY_NAME + " " + GATEWAY_VERSION +" starting...");
        HttpInboundServer server = new HttpInboundServer(port, proxyServerList);
        System.out.println(GATEWAY_NAME + " " + GATEWAY_VERSION +" started at http://localhost:" + port + " for server:" + proxyServerList);
        try {
            server.run();
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
