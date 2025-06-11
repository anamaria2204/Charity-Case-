package Problema_7.network.utils;

import Problema_7.network.jsonprotocol.VolunteerJsonWorker;
import Problema_7.services.IServices;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.Socket;

public class JsonConcurentServer extends AbsConcurrentServer{
    private IServices ServerV;
    private static Logger logger = LogManager.getLogger(JsonConcurentServer.class);
    public JsonConcurentServer(int port, IServices ServerV) {
        super(port);
        this.ServerV = ServerV;
        logger.info("JsonConcurrentServer");
    }

    @Override
    protected Thread createWorker(Socket client) {
        VolunteerJsonWorker worker=new VolunteerJsonWorker(ServerV, client);

        Thread tw=new Thread(worker);
        return tw;
    }
}
