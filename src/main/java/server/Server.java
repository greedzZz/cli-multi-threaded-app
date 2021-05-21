package server;

import common.Serializer;
import common.commands.Command;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import server.utility.CommandExecutor;
import server.utility.DataBaseManager;
import server.utility.UserValidator;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private SocketAddress address;
    private DatagramChannel channel;
    private final int PORT = 8725;
    private final Serializer serializer;
    private final Logger logger;
    private final ExecutorService service;

    public Server() {
        logger = LogManager.getLogger();
        this.address = new InetSocketAddress(PORT);
        this.serializer = new Serializer();
        service = Executors.newFixedThreadPool(10);
        logger.info("Server start.");

    }

    public static void main(String[] args) {
        Server server = new Server();
        server.run();
    }

    public void openChannel() throws IOException {
        this.channel = DatagramChannel.open();
        channel.bind(address);
        logger.info("The channel is open and bound to an address.");
    }

    public Command readRequest() throws IOException, ClassNotFoundException {
        byte[] bytes = new byte[1000000];
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        address = channel.receive(buffer);
        logger.info("The server received a new request.");
        return (Command) serializer.deserialize(bytes);
    }

    public String executeCommand(Command command, CollectionManager cm) {
        logger.info("The server is trying to execute a client's request.");
        return command.execute(cm);
    }

    public void sendAnswer(String str) {
        try {
            ByteBuffer byteBuffer = ByteBuffer.wrap(serializer.serialize(str));
            channel.send(byteBuffer, address);
            logger.info("The server sent an answer to client.");
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    public void run() {
        try {
            CollectionManager collectionManager = new CollectionManager();
            DataBaseManager dataBaseManager = new DataBaseManager();
            dataBaseManager.fillCollection(collectionManager);
            logger.info("The collection is created based on the contents of the database.");
            openChannel();
            UserValidator userValidator = new UserValidator(dataBaseManager.getConnection());
            while (true) {
                new Thread(new CommandExecutor(service.submit(this::readRequest).get(),
                        this, collectionManager, userValidator)).start();
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }
}
