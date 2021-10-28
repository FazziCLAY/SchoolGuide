package ru.fazziclay.schoolguide.market.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import ru.fazziclay.schoolguide.SharedConstrains;
import ru.fazziclay.schoolguide.market.PacketParser;
import ru.fazziclay.schoolguide.market.PacketWriter;
import ru.fazziclay.schoolguide.market.packet.ClientboundHanshake;
import ru.fazziclay.schoolguide.market.packet.ServerboundHanshake;

public class MarketClient extends Thread {
    //MarketServerInfo marketServer;

    Socket socket;
    InputStream inputStream;
    OutputStream outputStream;

    PacketParser packetParser;
    PacketWriter packetWriter;

    List<OnErrorListener> onErrorListeners = new ArrayList<>();

    //public MarketClient(MarketServerInfo marketServer) {
    //    this.marketServer = marketServer;
    //}

    public void addOnErrorListener(OnErrorListener errorListener) {
        onErrorListeners.add(errorListener);
    }

    public void removeOnErrorListener(OnErrorListener errorListener) {
        onErrorListeners.remove(errorListener);
    }

    @Override
    public void run() {
        try {

       //     if (marketServer == null || marketServer.isTechnicalWork()) {
            //    throw new IOException("Technical works!");
          //  }
      //      socket = new Socket(marketServer.getAddress(), marketServer.getPort());
       //     inputStream = socket.getInputStream();
        //    outputStream = socket.getOutputStream();

       //     packetWriter = new PacketWriter(outputStream);

       //     packetParser = new PacketParser(inputStream);
        //    packetParser.setPacketParserInterface((packetId, packetData) -> {
                try {
       //             if (packetId == ClientboundHanshake.ID) {
       //                 ClientboundHanshake clientboundHanshake = (ClientboundHanshake) ClientboundHanshake.parse(packetData, ClientboundHanshake.class);

        //            }

                } catch (Exception e) {

                }
         //   });
///
          ///  ServerboundHanshake serverboundHanshake = new ServerboundHanshake(SharedConstrains.APPLICATION_VERSION_CODE, new UUID(0,0));
      ///  ll;;    packetWriter.write(ServerboundHanshake.ID, serverboundHanshake.write());

            while (!socket.isClosed()) packetParser.parse();

        } catch (Exception e) {
            for (OnErrorListener onErrorListener : onErrorListeners) onErrorListener.onError(e);
        }


    }

    public interface OnErrorListener {
        void onError(Exception e);
    }
}
