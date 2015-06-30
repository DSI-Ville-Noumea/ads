package nc.noumea.mairie.ads.service.impl;

import java.util.Date;

import nc.noumea.mairie.ads.service.IHelperService;

import org.springframework.stereotype.Service;

@Service
public class HelperService implements IHelperService {

	@Override
	public Date getCurrentDate() {
		return new Date();
	}

}
