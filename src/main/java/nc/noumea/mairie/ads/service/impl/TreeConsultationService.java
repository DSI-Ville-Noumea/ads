package nc.noumea.mairie.ads.service.impl;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import nc.noumea.mairie.ads.domain.Entite;
import nc.noumea.mairie.ads.domain.EntiteHisto;
import nc.noumea.mairie.ads.domain.StatutEntiteEnum;
import nc.noumea.mairie.ads.dto.EntiteDto;
import nc.noumea.mairie.ads.dto.EntiteHistoDto;
import nc.noumea.mairie.ads.dto.NoContentException;
import nc.noumea.mairie.ads.dto.ReferenceDto;
import nc.noumea.mairie.ads.repository.IMairieRepository;
import nc.noumea.mairie.ads.repository.ITreeRepository;
import nc.noumea.mairie.ads.service.IReferenceDataService;
import nc.noumea.mairie.ads.service.ITreeConsultationService;
import nc.noumea.mairie.domain.Siserv;
import nc.noumea.mairie.domain.SiservNw;

import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TreeConsultationService implements ITreeConsultationService {

	@Autowired
	private ITreeRepository			treeRepository;

	@Autowired
	private IReferenceDataService	referenceDataService;

	@Autowired
	private IMairieRepository		sirhRepository;

	protected final String			LABEL_DIRECTION	= "AFFICHAGE SIRH DE TYPE DIRECTION";

	@Override
	@Transactional
	public EntiteDto getWholeTree() {

		EntiteDto tree = new EntiteDto(getRootEntity(), true);

		// on recherche les directions des entites
		constructDirection(tree, null);

		return tree;
	}

	/**
	 * Dans Organigramme, pour les filtres de recherches, nous avons besoin d'afficher les directions + les entites pour faciliter la recherche utilisateur cf
	 * #17795
	 * 
	 * @param entite
	 * @param entiteDirection
	 */
	protected void constructDirection(EntiteDto entite, EntiteDto entiteDirection) {

		if (null != entite.getEnfants()) {
			for (EntiteDto enfant : entite.getEnfants()) {
				if (null != enfant && null != enfant.getTypeEntite() && enfant.getTypeEntite().getLabel().toUpperCase().equals(LABEL_DIRECTION.toUpperCase())) {
					entiteDirection = new EntiteDto(enfant);
				}
				if (null != entiteDirection) {
					entiteDirection.getEnfants().clear();
					enfant.setEntiteDirection(entiteDirection);
				}
				constructDirection(enfant, entiteDirection);
				if (null != enfant && null != enfant.getTypeEntite() && enfant.getTypeEntite().getLabel().toUpperCase().equals(LABEL_DIRECTION.toUpperCase())) {
					entiteDirection = null;
				}
			}
		}
	}

	/**
	 * Responsible for retrieving the latest revision of the tree and its root Entity only
	 *
	 * @return
	 */
	protected Entite getRootEntity() {

		// Return root Entity associated with this revision
		return treeRepository.getWholeTree().get(0);
	}

	@Override
	@Transactional(readOnly = true)
	public EntiteDto getEntityByIdEntite(int idEntite) {

		Entite result = treeRepository.getEntiteFromIdEntite(idEntite);

		if (result == null)
			throw new NoContentException();

		return new EntiteDto().mapEntite(result, getDirectionOfEntity(result));
	}

	@Override
	@Transactional(readOnly = true)
	public EntiteDto getEntityByCodeService(String codeServi) {

		Entite result = treeRepository.getEntiteFromCodeServi(codeServi);

		if (result == null)
			throw new NoContentException();

		return new EntiteDto().mapEntite(result, getDirectionOfEntity(result));
	}

	@Override
	@Transactional(readOnly = true)
	public EntiteDto getEntityByIdEntiteWithChildren(int idEntite) {

		Entite result = treeRepository.getEntiteFromIdEntite(idEntite);

		if (result == null)
			throw new NoContentException();

		return new EntiteDto(result, true);
	}

	@Override
	@Transactional(readOnly = true)
	public EntiteDto getEntityByCodeServiceWithChildren(String codeServi) {

		Entite result = treeRepository.getEntiteFromCodeServi(codeServi);

		if (result == null)
			throw new NoContentException();

		return new EntiteDto(result, true);
	}

	@Override
	@Transactional(readOnly = true)
	public EntiteDto getEntityBySigle(String sigle) {

		Entite result = treeRepository.getEntiteActiveFromSigle(sigle);

		if (result == null)
			throw new NoContentException();

		return new EntiteDto().mapEntite(result, getDirectionOfEntity(result));
	}

	@Override
	public byte[] exportWholeTreeToGraphMl() {

		Entite rootEntity = treeRepository.getWholeTree().get(0);

		DocumentFactory factory = DocumentFactory.getInstance();
		Element root = factory.createElement("graphml");
		Document document = factory.createDocument(root);
		document.setXMLEncoding("utf-8");

		root.addAttribute("xmlns", "http://graphml.graphdrawing.org/xmlns");
		root.addAttribute("xsi:schemaLocation", "http://graphml.graphdrawing.org/xmlns http://graphml.graphdrawing.org/xmlns/1.0/graphml.xsd");
		root.add(new Namespace("xsi", "http://www.w3.org/2001/XMLSchema-instance"));
		root.add(new Namespace("y", "http://www.yworks.com/xml/graphml"));

		root.addElement("key").addAttribute("attr.name", "sigle").addAttribute("attr.type", "string").addAttribute("for", "node").addAttribute("id", "d4");
		root.addElement("key").addAttribute("attr.name", "label").addAttribute("attr.type", "string").addAttribute("for", "node").addAttribute("id", "d5");

		Element graph = root.addElement("graph");
		graph.addAttribute("id", String.valueOf(rootEntity.getIdEntite()));
		graph.addAttribute("edgedefault", "undirected");

		buildGraphMlTree(graph, rootEntity);

		ByteArrayOutputStream os_writer = new ByteArrayOutputStream();
		try {
			BufferedWriter wtr = new BufferedWriter(new OutputStreamWriter(os_writer, "UTF-8"));
			document.write(wtr);
			wtr.flush();
			wtr.close();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return os_writer.toByteArray();
	}

	/**
	 * Recursive method to build graphml nodes and edges for the entire tree
	 *
	 * @param graph Element
	 * @param entite Entite
	 */
	protected void buildGraphMlTree(Element graph, Entite entite) {

		Element el = graph.addElement("node").addAttribute("id", entite.getIdEntite().toString());

		// Node rollover (for yed)
		el.addElement("data").addAttribute("key", "d4").setText(entite.getSigle());

		// Node label (for yed)
		el.addElement("data").addAttribute("key", "d5").setText(entite.getLabel());

		for (Entite enfant : entite.getEntitesEnfants()) {
			buildGraphMlTree(graph, enfant);
			graph.addElement("edge").addAttribute("source", entite.getIdEntite().toString()).addAttribute("target", enfant.getIdEntite().toString());
		}
	}

	@Override
	@Transactional(readOnly = true)
	public EntiteDto getParentOfEntiteByTypeEntite(Integer idEntite, Integer idTypeEntite) {

		Entite result = treeRepository.getEntiteFromIdEntite(idEntite);

		if (result == null)
			throw new NoContentException();

		Entite entiteParent = treeRepository.getParentEntityWithIdEntityChildAndIdTypeEntity(idEntite, idTypeEntite);
		if (entiteParent == null || entiteParent.getIdEntite() == null)
			throw new NoContentException();

		return new EntiteDto().mapEntite(entiteParent, null);
	}

	@Override
	@Transactional(readOnly = true)
	public EntiteDto getEntiteByCodeServiceSISERV(String codeAS400) {
		Siserv service = sirhRepository.getSiservByCode(codeAS400);
		if (service == null || service.getServi() == null) {
			throw new NoContentException();
		}
		return new EntiteDto(service);
	}

	@Override
	@Transactional(readOnly = true)
	public List<EntiteHistoDto> getHistoEntityByIdEntite(Integer idEntite) {

		List<EntiteHisto> listHisto = treeRepository.getListEntiteHistoByIdEntite(idEntite);

		List<EntiteHistoDto> result = new ArrayList<EntiteHistoDto>();
		if (null != listHisto) {
			for (EntiteHisto histo : listHisto) {
				EntiteDto entiteParent = null;
				EntiteDto entiteRemplacee = null;
				if (histo.getIdEntiteRemplacee() != null) {
					Entite entiteRemp = treeRepository.getEntiteFromIdEntite(histo.getIdEntiteRemplacee());
					entiteRemplacee = new EntiteDto(entiteRemp, false);
				}
				if (histo.getIdEntiteParent() != null) {
					Entite entitePare = treeRepository.getEntiteFromIdEntite(histo.getIdEntiteParent());
					if (entitePare != null) {
						entiteParent = new EntiteDto(entitePare, false);
					}
				}
				EntiteHistoDto dto = new EntiteHistoDto(histo, entiteParent, entiteRemplacee, null);
				result.add(dto);
			}
		}
		return result;
	}

	@Override
	@Transactional(readOnly = true)
	public List<EntiteHistoDto> getHistoEntityByCodeService(String codeService) {

		Entite entite = treeRepository.getEntiteFromCodeServi(codeService);

		if (null == entite) {
			throw new NoContentException();
		}

		List<EntiteHisto> listHisto = treeRepository.getListEntiteHistoByIdEntite(entite.getIdEntite());

		List<EntiteHistoDto> result = new ArrayList<EntiteHistoDto>();
		if (null != listHisto) {
			for (EntiteHisto histo : listHisto) {
				EntiteDto entiteParent = null;
				EntiteDto entiteRemplacee = null;
				if (histo.getIdEntiteRemplacee() != null) {
					Entite entiteRemp = treeRepository.getEntiteFromIdEntite(histo.getIdEntiteRemplacee());
					entiteRemplacee = new EntiteDto(entiteRemp, false);
				}
				if (histo.getIdEntiteParent() != null) {
					Entite entitePare = treeRepository.getEntiteFromIdEntite(histo.getIdEntiteParent());
					entiteParent = new EntiteDto(entitePare, false);
				}
				EntiteHistoDto dto = new EntiteHistoDto(histo, entiteParent, entiteRemplacee, null);
				result.add(dto);
			}
		}
		return result;
	}

	@Override
	@Transactional(readOnly = true)
	public List<EntiteDto> getListEntityByStatut(Integer idStatut) {

		List<EntiteDto> result = new ArrayList<EntiteDto>();
		StatutEntiteEnum statut = StatutEntiteEnum.getStatutEntiteEnum(idStatut);
		if (statut != null) {
			List<Entite> res = treeRepository.getListEntityByStatut(statut);

			for (Entite entity : res) {
				result.add(new EntiteDto(entity, false));
			}
		}
		return result;
	}

	@Override
	@Transactional(readOnly = true)
	public EntiteDto getEntiteSiservByIdEntite(Integer idEntite) {
		Entite entite = treeRepository.getEntiteFromIdEntite(idEntite);
		if (entite == null || entite.getIdEntite() == null || entite.getSiservInfo().getCodeServi() == null)
			throw new NoContentException();

		SiservNw siServNw = sirhRepository.getSiservNwByCode(entite.getSiservInfo().getCodeServi());
		Siserv siServ = siServNw.getSiServ();
		if (siServ == null || siServ.getServi() == null) {
			throw new NoContentException();
		}
		return new EntiteDto(siServ);
	}

	@Override
	public Entite getDirectionOfEntity(Entite entite) {

		List<ReferenceDto> listeType = referenceDataService.getReferenceDataListTypeEntite();
		ReferenceDto type = null;
		for (ReferenceDto r : listeType) {
			if (r.getLabel().toUpperCase().equals(LABEL_DIRECTION.toUpperCase())) {
				type = r;
				break;
			}
		}

		if (null == type)
			return null;

		Entite entiteParent = treeRepository.getParentEntityWithIdEntityChildAndIdTypeEntity(entite.getIdEntite(), type.getId());

		if (entiteParent == null || entiteParent.getIdEntite() == null)
			return null;

		return entiteParent;
	}
}
