package nc.noumea.mairie.ads.service;

import java.util.Date;

import org.springframework.stereotype.Service;

@Service
public class HelperService implements IHelperService {

	@Override
	public Date getCurrentDate() {
		return new Date();
	}

}
