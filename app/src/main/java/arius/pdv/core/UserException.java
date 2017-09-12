package arius.pdv.core;

/**
 * Classe de erro de usuário que estende de RuntimeException.
 */
public class UserException extends RuntimeException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Instancia um novo UserException.
	 */
	public UserException() {
		super();
	}

	/**
	 * Instancia um novo UserException passando a mensagem de erro, erro, habilita supression e habilita a exibição da pilha de erros.
	 *
	 * @param message the message
	 * @param cause the cause
	 * @param enableSuppression the enable suppression
	 * @param writableStackTrace the writable stack trace
	 */
	public UserException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	/**
	 * Instancia um novo UserException passando a mensagem de erro, erro.
	 *
	 * @param message the message
	 * @param cause the cause
	 */
	public UserException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Instancia um novo UserException passando a mensagem de erro.
	 *
	 * @param message the message
	 */
	public UserException(String message) {
		super(message);
	}

	/**
	 * Instancia um novo UserException passando o erro.
	 *
	 * @param cause the cause
	 */
	public UserException(Throwable cause) {
		super(cause);
	}
}