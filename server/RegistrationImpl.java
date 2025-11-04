import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
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
        if (name == null || name.trim().isEmpty()) return "Name cannot be empty.";
        if (email == null || email.trim().isEmpty()) return "Email cannot be empty.";
        if (password == null) password = "";

        String normalizedEmail = email.trim().toLowerCase();

        try (BufferedReader reader = new BufferedReader(new FileReader(storageFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String marker = "Email: ";
                int idx = line.indexOf(marker);
                if (idx != -1) {
                    String existingEmail = line.substring(idx + marker.length()).split("\\s|,|\\|")[0].trim().toLowerCase();
                    if (existingEmail.equals(normalizedEmail) || line.toLowerCase().contains("email: " + normalizedEmail)) {
                        return "Email already registered.";
                    }
                } else {
                    if (line.toLowerCase().contains("email: " + normalizedEmail)) {
                        return "Email already registered.";
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "Registration failed: cannot read storage file.";
        }

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        String entryLine = String.format("%s | Name: %s | Email: %s | Password: %s", timestamp, name.trim(), email.trim(), password);

        try (FileWriter writer = new FileWriter(storageFile, true)) {
            writer.write(entryLine + System.lineSeparator());
        } catch (IOException e) {
            e.printStackTrace();
            return "Registration failed: server IO error.";
        }

        return "User registered successfully.";
    }

    @Override
    public synchronized String getAllUsers() throws RemoteException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(storageFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append(System.lineSeparator());
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "Error reading users.";
        }
        return sb.toString();
    }
}
