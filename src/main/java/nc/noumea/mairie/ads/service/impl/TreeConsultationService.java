package nc.noumea.mairie.ads.service.impl;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import nc.noumea.mairie.ads.domain.Entite;
import nc.noumea.mairie.ads.domain.EntiteHisto;
import nc.noumea.mairie.ads.dto.EntiteDto;
import nc.noumea.mairie.ads.dto.EntiteHistoDto;
import nc.noumea.mairie.ads.repository.IMairieRepository;
import nc.noumea.mairie.ads.repository.ITreeRepository;
import nc.noumea.mairie.ads.service.ITreeConsultationService;
import nc.noumea.mairie.domain.Siserv;

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
	private ITreeRepository treeRepository;

	@Autowired
	private IMairieRepository sirhRepository;

	@Override
	public EntiteDto getWholeTree() {

		return new EntiteDto(getRootEntity(), true);
	}

	/**
	 * Responsible for retrieving the latest revision of the tree and its root
	 * Entity only
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
			return null;

		return new EntiteDto().mapEntite(result);
	}

	@Override
	@Transactional(readOnly = true)
	public EntiteDto getEntityByCodeService(String codeServi) {

		Entite result = treeRepository.getEntiteFromCodeServi(codeServi);

		if (result == null)
			return null;

		return new EntiteDto().mapEntite(result);
	}

	@Override
	@Transactional(readOnly = true)
	public EntiteDto getEntityByIdEntiteWithChildren(int idEntite) {

		Entite result = treeRepository.getEntiteFromIdEntite(idEntite);

		if (result == null)
			return null;

		return new EntiteDto(result, true);
	}

	@Override
	@Transactional(readOnly = true)
	public EntiteDto getEntityByCodeServiceWithChildren(String codeServi) {

		Entite result = treeRepository.getEntiteFromCodeServi(codeServi);

		if (result == null)
			return null;

		return new EntiteDto(result, true);
	}

	@Override
	@Transactional(readOnly = true)
	public EntiteDto getEntityBySigle(String sigle) {

		Entite result = treeRepository.getEntiteActiveFromSigle(sigle);

		if (result == null)
			return null;

		return new EntiteDto().mapEntite(result);
	}

	@Override
	public byte[] exportWholeTreeToGraphMl() {

		Entite rootEntity = treeRepository.getWholeTree().get(0);

		DocumentFactory factory = DocumentFactory.getInstance();
		Element root = factory.createElement("graphml");
		Document document = factory.createDocument(root);
		document.setXMLEncoding("utf-8");

		root.addAttribute("xmlns", "http://graphml.graphdrawing.org/xmlns");
		root.addAttribute("xsi:schemaLocation",
				"http://graphml.graphdrawing.org/xmlns http://graphml.graphdrawing.org/xmlns/1.0/graphml.xsd");
		root.add(new Namespace("xsi", "http://www.w3.org/2001/XMLSchema-instance"));
		root.add(new Namespace("y", "http://www.yworks.com/xml/graphml"));

		root.addElement("key").addAttribute("attr.name", "sigle").addAttribute("attr.type", "string")
				.addAttribute("for", "node").addAttribute("id", "d4");
		root.addElement("key").addAttribute("attr.name", "label").addAttribute("attr.type", "string")
				.addAttribute("for", "node").addAttribute("id", "d5");

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
	 * @param graph
	 *            Element
	 * @param entite
	 *            Entite
	 */
	protected void buildGraphMlTree(Element graph, Entite entite) {

		Element el = graph.addElement("node").addAttribute("id", entite.getIdEntite().toString());

		// Node rollover (for yed)
		el.addElement("data").addAttribute("key", "d4").setText(entite.getSigle());

		// Node label (for yed)
		el.addElement("data").addAttribute("key", "d5").setText(entite.getLabel());

		for (Entite enfant : entite.getEntitesEnfants()) {
			buildGraphMlTree(graph, enfant);
			graph.addElement("edge").addAttribute("source", entite.getIdEntite().toString())
					.addAttribute("target", enfant.getIdEntite().toString());
		}
	}

	@Override
	@Transactional(readOnly = true)
	public EntiteDto getParentOfEntiteByTypeEntite(Integer idEntite, Integer idTypeEntite) {

		Entite result = treeRepository.getEntiteFromIdEntite(idEntite);

		if (result == null)
			return null;

		Entite entiteParent = treeRepository.getParentEntityWithIdEntityChildAndIdTypeEntity(idEntite, idTypeEntite);
		if (entiteParent == null || entiteParent.getIdEntite() == null)
			return null;

		return new EntiteDto().mapEntite(entiteParent);
	}

	@Override
	@Transactional(readOnly = true)
	public EntiteDto getEntiteByCodeServiceSISERV(String codeAS400) {
		Siserv service = sirhRepository.getSiservByCode(codeAS400);
		if (service == null || service.getServi() == null) {
			return null;
		}
		return new EntiteDto(service);
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<EntiteHistoDto> getHistoEntityByIdEntite(Integer idEntite) {
		
		List<EntiteHisto> listHisto = treeRepository.getListEntiteHistoByIdEntite(idEntite);
		
		List<EntiteHistoDto> result = new ArrayList<EntiteHistoDto>();
		if (null != listHisto) {
			for(EntiteHisto histo : listHisto) {
				EntiteHistoDto dto = new EntiteHistoDto(histo);
				result.add(dto);
			}
		}
		return result;
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<EntiteHistoDto> getHistoEntityByCodeService(String codeService) {

		Entite entite = treeRepository.getEntiteFromCodeServi(codeService);
		
		if(null == entite) {
			return null;
		}
		
		List<EntiteHisto> listHisto = treeRepository.getListEntiteHistoByIdEntite(entite.getIdEntite());
		
		List<EntiteHistoDto> result = new ArrayList<EntiteHistoDto>();
		if (null != listHisto) {
			for(EntiteHisto histo : listHisto) {
				EntiteHistoDto dto = new EntiteHistoDto(histo);
				result.add(dto);
			}
		}
		return result;
	}
}
