import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RegistrationInterface extends Remote {
    String registerUser(String name, String email, String password) throws RemoteException;
}
