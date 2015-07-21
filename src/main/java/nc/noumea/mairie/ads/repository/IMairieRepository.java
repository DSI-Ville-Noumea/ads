package nc.noumea.mairie.ads.repository;

import java.util.List;

import nc.noumea.mairie.domain.Siserv;
import nc.noumea.mairie.domain.SiservNw;

public interface IMairieRepository {

	List<String> getAllServiCodes();

	void persist(Object entity);

	List<SiservNw> getAllSiservNw();

	Siserv getSiservByCode(String codeAS400);

	SiservNw getSiservNwByCode(String codeAS400);

	List<Siserv> getSiservFromParentSigle(String parentSigle);
}
