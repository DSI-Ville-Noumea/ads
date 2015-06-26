package nc.noumea.mairie.ads.service;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class TypeNoeudConflictException extends RuntimeException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7026588835538173407L;

	public TypeNoeudConflictException() {
		super();
	}

	public TypeNoeudConflictException(String message) {
		super(message);
	}
}
