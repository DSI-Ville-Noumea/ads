package nc.noumea.mairie.ws;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.noumea.mairie.ads.dto.ReturnMessageDto;
import nc.noumea.mairie.sirh.dto.FichePosteDto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.sun.jersey.api.client.ClientResponse;

@Service
public class SirhWSConsumer extends BaseWsConsumer implements ISirhWSConsumer {

	@Autowired
	@Qualifier("sirhWsBaseUrl")
	private String sirhWsBaseUrl;

	private static final String deleteFichesPosteDtoByIdEntiteUrl = "fichePostes/deleteFichePosteByIdEntite";
	private static final String dupliqueFichesPosteDtoByIdEntiteUrl = "fichePostes/dupliqueFichePosteByIdEntite";
	private static final String activeFichesPosteDtoByIdEntiteUrl = "fichePostes/activeFichesPosteByIdEntite";
	private static final String getListFichesPosteByIdEntiteUrl = "fichePostes/listFichePosteByIdEntite";

	@Override
	public ReturnMessageDto deleteFichesPosteByIdEntite(Integer idEntite, Integer idAgent) {

		String url = String.format(sirhWsBaseUrl + deleteFichesPosteDtoByIdEntiteUrl);

		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("idEntite", String.valueOf(idEntite));
		parameters.put("idAgent", String.valueOf(idAgent));

		ClientResponse res = createAndFireGetRequest(parameters, url);

		return readResponse(ReturnMessageDto.class, res, url);
	}

	@Override
	public ReturnMessageDto activeFichesPosteByIdEntite(Integer idEntite, Integer idAgent) {

		String url = String.format(sirhWsBaseUrl + activeFichesPosteDtoByIdEntiteUrl);

		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("idEntite", String.valueOf(idEntite));
		parameters.put("idAgent", String.valueOf(idAgent));

		ClientResponse res = createAndFireGetRequest(parameters, url);

		return readResponse(ReturnMessageDto.class, res, url);
	}

	@Override
	public List<FichePosteDto> getListFichesPosteByIdEntite(Integer idEntite, List<String> listStatutFichePoste) {

		String url = String.format(sirhWsBaseUrl + getListFichesPosteByIdEntiteUrl);

		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("idEntite", String.valueOf(idEntite));
		if (null != listStatutFichePoste && !listStatutFichePoste.isEmpty()) {
			String listStatut = "";
			for (String statutFDP : listStatutFichePoste) {
				listStatut += statutFDP + ",";
			}
			listStatut = listStatut.substring(0, listStatut.length() - 1);
			parameters.put("statutFDP", listStatut);
		}
		ClientResponse res = createAndFireGetRequest(parameters, url);

		return readResponseAsList(FichePosteDto.class, res, url);
	}

	@Override
	public ReturnMessageDto dupliqueFichesPosteByIdEntite(Integer idEntiteNew, Integer idEntiteOld, Integer idAgent) {

		String url = String.format(sirhWsBaseUrl + dupliqueFichesPosteDtoByIdEntiteUrl);

		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("idEntiteNew", String.valueOf(idEntiteNew));
		parameters.put("idEntiteOld", String.valueOf(idEntiteOld));
		parameters.put("idAgent", String.valueOf(idAgent));

		ClientResponse res = createAndFireGetRequest(parameters, url);

		return readResponse(ReturnMessageDto.class, res, url);
	}
}
