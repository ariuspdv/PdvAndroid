package arius.pdv.core;

/**
 * Classe que estende de RuntimeException.
 * 
 */
public class ApplicationException extends RuntimeException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Instancia uma nova ApplicationException.
	 */
	public ApplicationException() {
		super();
	}

	/**
	 * Instancia uma nova ApplicationException passando a mensagem de erro, causa, habilita uma suppression e habilita a pilha de erro.
	 *
	 * @param message the message
	 * @param cause the cause
	 * @param enableSuppression the enable suppression
	 * @param writableStackTrace the writable stack trace
	 */
	public ApplicationException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	/**
	 * Instancia uma nova ApplicationException passando a mensagem de erro e causa.
	 *
	 * @param message the message
	 * @param cause the cause
	 */
	public ApplicationException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Instancia uma nova ApplicationException passando a mensagem de erro.
	 *
	 * @param message the message
	 */
	public ApplicationException(String message) {
		super(message);
	}

	/**
	 * Instancia uma nova ApplicationException passando o erro.
	 *
	 * @param cause the cause
	 */
	public ApplicationException(Throwable cause) {
		super(cause);
	}
}