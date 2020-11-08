package nio.gateway.filter;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HeadFilter implements HttpRequestFilter {
    private static Logger logger = LoggerFactory.getLogger(HeadFilter.class);
    @Override
    public void filter(FullHttpRequest fullRequest, ChannelHandlerContext ctx) {
        fullRequest.headers().set("nio","clei");
        logger.info("add nio info to request header");
    }
}
