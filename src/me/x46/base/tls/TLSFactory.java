package me.x46.base.tls;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.SecureRandom;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

//openssl pkcs12 -export -out certificate.p12 -inkey privkey.pem -in cert.pem -certfile chain.pem 
public class TLSFactory {

	private Socket socket;
	
	private boolean clientMode;
	
	private String keystoreFile;
	private String keystoreFilePassword;

	/**
	 * client mode true
	 *
	 * @param socket
	 * 
	 */
	public TLSFactory(Socket socket) {
		this.socket = socket;
		clientMode = true;
	}
	
	/**
	 * client mode false => server
	 * 
	 * @param socket
	 * @param keystoreFile
	 * @param keystoreFilePassword
	 *
	 * @throws NoCertificateException 
	 * 
	 */
	public TLSFactory(Socket socket, String keystoreFile, String keystoreFilePassword) throws NoCertificateException {
		this.socket = socket;
		
		this.keystoreFile = keystoreFile;
		this.keystoreFilePassword = keystoreFilePassword;
		
		if(keystoreFile == null || keystoreFilePassword == null) {
			throw new NoCertificateException();
		}
		
		clientMode = false;
	}

	public Socket startHandshake() {
		
		try {
			startTLS();
		} catch (IOException | GeneralSecurityException e) {
			e.printStackTrace();
		}
		
		return socket;
	}
	
	
	private void startTLS() throws IOException, GeneralSecurityException {
		
		SSLSocketFactory sf = null;
	
		if(clientMode) {
			sf = ((SSLSocketFactory) SSLSocketFactory.getDefault());
		}else {
			sf = sslContext(keystoreFile, keystoreFilePassword).getSocketFactory();
		}
		
		// Wrap 'socket' from above in a SSL socket
		InetSocketAddress remoteAddress =
		        (InetSocketAddress) socket.getRemoteSocketAddress();
		SSLSocket s = (SSLSocket) (sf.createSocket(
		        socket, remoteAddress.getHostName(), socket.getPort(), true));

		// we are a server
		s.setUseClientMode(clientMode);

		// allow all supported protocols and cipher suites
		s.setEnabledProtocols(s.getSupportedProtocols());
		s.setEnabledCipherSuites(s.getSupportedCipherSuites());

		// and go!		
		s.startHandshake();

		// continue communication on 'socket'
		socket = s;
	}
	
	
	private static SSLContext sslContext(String keystoreFile, String password)
			throws GeneralSecurityException, IOException {
		KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
		try (InputStream in = new FileInputStream(keystoreFile)) {
			keystore.load(in, password.toCharArray());
		}
		KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
		keyManagerFactory.init(keystore, password.toCharArray());

		TrustManagerFactory trustManagerFactory = TrustManagerFactory
				.getInstance(TrustManagerFactory.getDefaultAlgorithm());
		
		trustManagerFactory.init(keystore);

		
		SSLContext sslContext = SSLContext.getInstance("TLS");
		sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), new SecureRandom());

		return sslContext;
	}


}
