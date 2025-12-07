package com.medical.dme.distributed.socket;

import com.medical.dme.common.model.MedicalRecord;
import com.medical.dme.common.xml.MedicalRecordXMLProcessor;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.nio.file.Files;
import java.nio.file.Paths;

@Component
public class MedicalSocketServer {

    @Value("${socket.server.port:8888}")
    private int port;

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private Channel channel;

    @PostConstruct
    public void start() throws Exception {
        bossGroup = new NioEventLoopGroup(1);
        workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ChannelPipeline p = ch.pipeline();
                            p.addLast(new StringDecoder(CharsetUtil.UTF_8));
                            p.addLast(new StringEncoder(CharsetUtil.UTF_8));
                            p.addLast(new MedicalSocketHandler());
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            channel = b.bind(port).sync().channel();
            System.out.println("Socket Server started on port " + port);

        } catch (Exception e) {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
            throw e;
        }
    }

    @PreDestroy
    public void stop() {
        if (channel != null) {
            channel.close();
        }
        if (workerGroup != null) {
            workerGroup.shutdownGracefully();
        }
        if (bossGroup != null) {
            bossGroup.shutdownGracefully();
        }
        System.out.println("Socket Server stopped");
    }

    private class MedicalSocketHandler extends SimpleChannelInboundHandler<String> {

        private final MedicalRecordXMLProcessor xmlProcessor =
                new MedicalRecordXMLProcessor();

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, String msg) {
            System.out.println("Socket request received: " + msg);

            String response;
            try {
                if (msg.startsWith("GET_RECORD:")) {
                    String patientId = msg.substring(11);
                    response = handleGetRecord(patientId);
                } else if (msg.startsWith("SEND_RECORD:")) {
                    String xmlContent = msg.substring(12);
                    response = handleReceiveRecord(xmlContent);
                } else {
                    response = "ERROR: Unknown command";
                }
            } catch (Exception e) {
                response = "ERROR: " + e.getMessage();
            }

            ctx.writeAndFlush(response);
        }

        private String handleGetRecord(String patientId) throws Exception {
            // Simuler la récupération d'un dossier
            String xmlFilePath = "records/" + patientId + ".xml";

            if (Files.exists(Paths.get(xmlFilePath))) {
                MedicalRecord record = xmlProcessor.unmarshalFromXML(xmlFilePath);
                return "RECORD:" + toXML(record);
            } else {
                return "ERROR: Record not found";
            }
        }

        private String handleReceiveRecord(String xmlContent) throws Exception {
            // Sauvegarder le dossier reçu
            MedicalRecord record = xmlProcessor.unmarshalFromXML(
                    new java.io.ByteArrayInputStream(xmlContent.getBytes())
            );

            String outputPath = "records/received_" +
                    record.getPatient().getPatientId() + ".xml";
            xmlProcessor.marshalToXML(record, outputPath);

            return "SUCCESS: Record saved as " + outputPath;
        }

        private String toXML(MedicalRecord record) throws Exception {
            java.io.StringWriter writer = new java.io.StringWriter();

            jakarta.xml.bind.JAXBContext context =
                    jakarta.xml.bind.JAXBContext.newInstance(MedicalRecord.class);
            jakarta.xml.bind.Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(jakarta.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, false);

            marshaller.marshal(record, writer);
            return writer.toString();
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            cause.printStackTrace();
            ctx.close();
        }
    }
}