package nc.noumea.mairie.ads.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(value = "/")
public class IndexController {

	private final Logger logger = LoggerFactory.getLogger(IndexController.class);

	/**
	 * <strong>Service : </strong>Point d'entrée de l'application web ADS (gestion-services).<br/>
	 * <strong>Description : </strong>Ce controller redirige vers l'application web de l'arbre des services.<br/>
	 */
	@RequestMapping(value = "index", method = RequestMethod.GET)
	public String index() {
		logger.debug("Loading application zul/index.zul");
		return "zul/index.zul";
	}
}
