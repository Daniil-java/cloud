package Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import static GeneralClasses.Sender.getFile;
import static GeneralClasses.Sender.sendFile;


public class Handler implements Runnable{

    private static final int SIZE = 256;

    private Path serverDir;
    private DataInputStream is;
    private DataOutputStream os;
    private final byte[] buf;

    public Handler(Socket socket) throws IOException {
        is = new DataInputStream(socket.getInputStream());
        os = new DataOutputStream(socket.getOutputStream());
        serverDir = Paths.get("data");
        buf = new byte[SIZE];
        sendServerFiles();
    }

    public void sendServerFiles() throws IOException {
        List<String> files = Files.list(serverDir)
                .map(p -> p.getFileName().toString())
                .collect(Collectors.toList());
        os.writeUTF("#list#");
        os.writeInt(files.size());
        for (String file : files) {
            os.writeUTF(file);
        }
        os.flush();
    }

    @Override
    public void run() {
        try {
            while (true) {
                String command = is.readUTF();
                System.out.println("received: " + command);
                if (command.equals("#file#")) {
                    getFile(is, serverDir, SIZE, buf);
                    sendServerFiles();
                } else if (command.equals("#get_file#")) {
                    String fileName = is.readUTF();
                    sendFile(fileName, os, serverDir);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}