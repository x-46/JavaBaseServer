package me.x46.base.tls;

@SuppressWarnings("serial")
public class NoCertificateException extends RuntimeException {
	
	public NoCertificateException() {

	}

	public NoCertificateException(String message) {
		super(message);
	}
}
