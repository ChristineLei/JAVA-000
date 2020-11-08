package nio.gateway.outbound.netty4;


import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.nio.charset.Charset;

import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class NettyHttpClientOutboundHandler  extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx)
            throws Exception {
        String sendMessage = "Hello netty";

        ByteBuf messageBuffer = Unpooled.buffer();
        messageBuffer.writeBytes(sendMessage.getBytes());
        ctx.write(messageBuffer);
        ctx.flush();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf readMessage = (ByteBuf) msg;
        System.out.println("NettyClientHandler channelRead : " + readMessage.toString(Charset.defaultCharset()));
        ctx.writeAndFlush("Acknowledged");
    }
}
