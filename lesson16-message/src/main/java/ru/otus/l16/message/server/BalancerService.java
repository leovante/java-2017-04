package ru.otus.l16.message.server;

import ru.otus.l16.message.ClientUtils;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class BalancerService implements Addressee {
    private static final Logger logger = Logger.getLogger(BalancerService.class.getName());

    private final SocketServer socketServer;
    private final List<Address> backendAddresses = new CopyOnWriteArrayList<>();
    private final Random random = new Random();

    public BalancerService(SocketServer socketServer) {
        this.socketServer = socketServer;
    }

    public void getCacheStat(Address from) {
        logger.info("getCacheStat" + " from " + from);
        while(backendAddresses.size() == 0) {
            try {
                TimeUnit.MILLISECONDS.sleep(100);
            }catch (Exception e) {
                logger.info("Error");
            }
        }

        Address backendAddress = backendAddresses.get(random.nextInt(backendAddresses.size()));
        logger.info("Backend wins: " + backendAddress);
        socketServer.sendMessage(backendAddress, new FrontRequestMessage(from, backendAddress));
    }

    public void addBackendAddress(Address address) {
        logger.info("Add back address");
        backendAddresses.add(address);
    }

    public void removeBackendAddress(Address address) {
        logger.info("Remove back address");
        backendAddresses.remove(address);
    }

    @Override
    public Address getAddress() {
        return ClientUtils.getBalancerAddress();
    }
}
