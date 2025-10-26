import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class RegistrationImpl extends UnicastRemoteObject implements RegistrationInterface {

    private final File storageFile;

    protected RegistrationImpl(File storageFile) throws RemoteException {
        super();
        this.storageFile = storageFile;
    }

    @Override
    public synchronized String registerUser(String name, String email, String password) throws RemoteException {
        // Basic validation
        if (name == null || name.trim().isEmpty()) return "Name cannot be empty.";
        if (email == null || email.trim().isEmpty()) return "Email cannot be empty.";

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        String line = String.format("%s | Name: %s | Email: %s | Password: %s%n", timestamp, name, email, password);

        try (FileWriter writer = new FileWriter(storageFile, true)) {
            writer.write(line);
        } catch (IOException e) {
            e.printStackTrace();
            return "Registration failed: server IO error.";
        }
        return "User registered successfully.";
    }
}
