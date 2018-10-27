import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Scanner;

public class Server 
{
	public static String SERVER_IP = "127.0.0.1";
	public static final int SERVER_PORT = 13579;
	
	private ServerSocket serverSocket = null;
	private Socket client = null;
	
	private PublicKey publicKey = null;
	private PrivateKey privateKey = null;
	
	private PublicKey clientPublicKey = null;
	
	ObjectOutputStream sender = null;
    ObjectInputStream receiver = null;

	public Server()
	{
		Scanner sc = new Scanner(System.in);
        if (initialize())
        {
		    try 
		    {
		    	sender.writeObject(RSAEncrypt.incode(sc.nextLine(), clientPublicKey));
		    	
		    	byte[] receivedData = (byte[])receiver.readObject();
		    	
		    	System.out.println(RSAEncrypt.decode(receivedData, privateKey));
		    }
		    catch (Exception e) 
		    {
		        e.printStackTrace();
		    }
        }
        
        sc.close();
	}
	
	private boolean initialize()
	{
		try
		{
			KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
	        keyPairGenerator.initialize(RSAEncrypt.RSA_ENCRYPT_BYTE_SIZE);
	        KeyPair keyPair = keyPairGenerator.genKeyPair();
	        
	        publicKey = keyPair.getPublic();
	        privateKey = keyPair.getPrivate();

			serverSocket = new ServerSocket(SERVER_PORT);
	        System.out.println("서버 포트열기 성공.");

	        client = serverSocket.accept();
	        System.out.println("클라이언트 접속됨.");
	        
	        sender = new ObjectOutputStream(client.getOutputStream());
	        receiver = new ObjectInputStream(client.getInputStream());
	        System.out.println("스트림 수립 성공.");
	        
	        sender.writeObject(publicKey);
	        sender.flush();
	        
	        clientPublicKey = (PublicKey)receiver.readObject();
            System.out.println("공개키 교환 성공.");
            
            return true;
		}
		catch(NoSuchAlgorithmException e)
		{
			System.out.println("키체인 생성중 오류");
			e.printStackTrace();
			
			return false;
		}
		catch (ClassNotFoundException e) 
		{
			System.out.println("수신된 데이터 변환중 오류");
			e.printStackTrace();
			
			return false;
		}
		catch (IOException e) 
		{
			System.out.println("클라이언트 접속중 오류");
			e.printStackTrace();
			
			return false;
		}
		catch (Exception e) 
		{
			System.out.println("수신된 데이터 변환중 오류");
			e.printStackTrace();
			
			return false;
		}
	}
	
    public static void main(String[] args) 
    {
    	new Server();
    }
}