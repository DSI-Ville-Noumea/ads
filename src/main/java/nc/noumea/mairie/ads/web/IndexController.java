package nc.noumea.mairie.ads.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/app")
public class IndexController {

	private Logger logger = LoggerFactory.getLogger(IndexController.class);
	
	@RequestMapping(value = {"", "/", "index"}, method = RequestMethod.GET)
	public String index() {
		logger.debug("Loading application zul/index.zul");
		return "zul/index.zul";
	}
}
