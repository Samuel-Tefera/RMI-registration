import java.io.File;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

public class RegistrationServer {
    public static void main(String[] args) {
        try {
            if (args.length < 1) {
                System.out.println("Usage: java RegistrationServer <server-ip> [storage-file-path]");
                System.exit(1);
            }

            String serverIp = args[0];
            String storagePath = (args.length >= 2) ? args[1] : "users.txt";

            System.setProperty("java.rmi.server.hostname", serverIp);

            LocateRegistry.createRegistry(1099);
            System.out.println("RMI registry started on port 1099.");

            // Create file if not exists
            File storageFile = new File(storagePath);
            if (!storageFile.exists()) {
                storageFile.createNewFile();
                System.out.println("Created storage file: " + storageFile.getAbsolutePath());
            } else {
                System.out.println("Using storage file: " + storageFile.getAbsolutePath());
            }

            RegistrationImpl impl = new RegistrationImpl(storageFile);
            Naming.rebind("rmi://" + serverIp + "/RegistrationService", impl);

            System.out.println("Server ready at rmi://" + serverIp + "/RegistrationService");

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
