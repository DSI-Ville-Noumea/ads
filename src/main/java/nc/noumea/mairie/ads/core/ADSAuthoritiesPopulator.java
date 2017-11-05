package nc.noumea.mairie.ads.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.ldap.userdetails.LdapAuthoritiesPopulator;
import org.springframework.stereotype.Service;

/**
 * Overriden Authorities Populator for Ldap authentication with spring security
 * This class loads roles from the database i/o using LDAP groups
 */
@Service
public class ADSAuthoritiesPopulator implements LdapAuthoritiesPopulator {

	@Autowired
	@Qualifier("groupeADAuthentification")
	private String groupeADAuthentification;

	@Override
	public Collection<? extends GrantedAuthority> getGrantedAuthorities(DirContextOperations dirContextOperations, String login) {

		List<GrantedAuthority> roles = new ArrayList<>();

		String[] t2 = dirContextOperations.getStringAttributes("memberOf");
		for (String b : t2) {
			if (b.contains(groupeADAuthentification)) {
				roles.add(new SimpleGrantedAuthority("USER"));
				break;
			}
		}

		return roles;
	}

}
