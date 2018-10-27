import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;

public class Client 
{
	private Socket server = null;
	
	private PublicKey publicKey = null;
	private PrivateKey privateKey = null;
	
	private PublicKey serverPublicKey = null;
	
	ObjectOutputStream sender = null;
    ObjectInputStream receiver = null;
    
	public Client()
	{
		if(initialize())
		{
			try
			{
				byte[] receivedData = (byte[])receiver.readObject();
		    	String incomeData = RSAEncrypt.decode(receivedData, privateKey); 
				
		    	System.out.println(incomeData);
		    	
		    	sender.writeObject(RSAEncrypt.incode(incomeData, serverPublicKey));
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}		
	}
	
	public boolean initialize()
	{
		try
		{
			KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
	        keyPairGenerator.initialize(RSAEncrypt.RSA_ENCRYPT_BYTE_SIZE);
	        KeyPair keyPair = keyPairGenerator.genKeyPair();
	        
	        publicKey = keyPair.getPublic();
	        privateKey = keyPair.getPrivate();
	        
	        System.out.println("키체인 생성 성공");
	        
	        server = new Socket(Server.SERVER_IP, Server.SERVER_PORT);
            System.out.println("서버 연결 성공");
            
            sender = new ObjectOutputStream(server.getOutputStream());
            receiver = new ObjectInputStream(server.getInputStream());
            System.out.println("스트림 수립 성공");
	        
            serverPublicKey = (PublicKey)receiver.readObject();
            sender.writeObject(publicKey);
            sender.flush();
            
            System.out.println("공개키 교환 성공");
            
            return true;
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			
			return false;
		}
	}
	
    public static void main(String[] args) 
    {
    	new Client();
    }
}