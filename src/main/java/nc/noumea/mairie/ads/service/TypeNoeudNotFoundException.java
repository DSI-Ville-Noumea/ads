package nc.noumea.mairie.ads.service;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class TypeNoeudNotFoundException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 819814070279211070L;
}
