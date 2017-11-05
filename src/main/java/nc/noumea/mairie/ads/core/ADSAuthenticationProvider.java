package nc.noumea.mairie.ads.core;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;

/**
 * Implémentation de AuthenticationProvider pour l'authentification à
 * l'application
 * 
 */
public class ADSAuthenticationProvider implements AuthenticationProvider {

	private static final Logger		LOGGER			= LoggerFactory.getLogger(ADSAuthenticationProvider.class);

	/** Message par défaut */
	private static final String		DEFAULT_MESSAGE	= "Connection à l'application Arbre des services impossible";

	/** Authentication provider */
	private AuthenticationProvider	provider;

	/** Message d'erreur si échec d'authentification par le provider */
	private String					messageProvider	= DEFAULT_MESSAGE;

	/** Message d'erreur si échec d'authentification à l'application */
	private String					messageADSConf	= DEFAULT_MESSAGE;

	/**
	 * Override la méthode authenticate
	 * 
	 * @param authentication
	 *            Authentication
	 * @throws AuthenticationException
	 *             Exception d'authentification
	 */
	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {

		Authentication authenticationResult = null;

		if (provider != null)
			try {
				authenticationResult = provider.authenticate(authentication);
			} catch (BadCredentialsException e) {
				LOGGER.error("Error lors de l'authentifation", e);
				throw new BadCredentialsException(messageProvider);
			}

		if (authenticationResult.getAuthorities().isEmpty()) {
			// MAUVAIS Group AD pour l'authentification
			throw new BadCredentialsException(messageADSConf);
		}

		String username = authentication.getName();
		String password = (String) authentication.getCredentials();
		List<GrantedAuthority> roles = new ArrayList<>();

		return (provider == null) ? new UsernamePasswordAuthenticationToken(username, password, roles) : authenticationResult;
	}

	/**
	 * Override de la méthode supports
	 * 
	 * @param authentication
	 *            Authentication
	 */
	@Override
	public boolean supports(Class<?> authentication) {
		return (provider == null) ? true : provider.supports(authentication);
	}

	/**
	 * get Provider
	 * 
	 * @return provider
	 */
	public AuthenticationProvider getProvider() {
		return provider;
	}

	/**
	 * set Provider
	 * 
	 * @param provider
	 *            Provider to set
	 */
	public void setProvider(AuthenticationProvider provider) {
		this.provider = provider;
	}

	/**
	 * get MessageProvider
	 * 
	 * @return message
	 */
	public String getMessageProvider() {
		return messageProvider;
	}

	/**
	 * set MessageProvider
	 * 
	 * @param messageProvider
	 *            message to set
	 */
	public void setMessageProvider(String messageProvider) {
		this.messageProvider = messageProvider;
	}

	public String getMessageADSConf() {
		return messageADSConf;
	}

	public void setMessageADSConf(String messageADSConf) {
		this.messageADSConf = messageADSConf;
	}

}
