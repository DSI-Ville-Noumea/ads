package nc.noumea.mairie.ads.service;

import nc.noumea.mairie.ads.domain.Noeud;
import nc.noumea.mairie.ads.domain.Revision;
import nc.noumea.mairie.ads.dto.ErrorMessageDto;
import nc.noumea.mairie.ads.dto.RevisionDto;
import nc.noumea.mairie.ads.repository.IRevisionRepository;
import nc.noumea.mairie.ads.repository.ITreeRepository;
import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
public class RevisionService implements IRevisionService {

	@Autowired
	private IRevisionRepository revisionRepository;

	@Autowired
	private ITreeRepository treeRepository;

	@Autowired
	private ICreateTreeService createTreeService;

	@Autowired
	private IHelperService helperService;

	@Override
	public List<RevisionDto> getRevisionsByDateEffetDesc() {

		List<RevisionDto> revisions = new ArrayList<>();

		for (Revision rev : revisionRepository.getAllRevisionsByDateEffetDesc()) {
			revisions.add(new RevisionDto(rev));
		}

		// The latest revision is the only one the user 
		// will be able to edit
		revisions.get(0).setCanEdit(true);

		return revisions;
	}

	@Override
	public Revision getLatestyRevisionForDate(Date date) {
		return revisionRepository.getLatestRevisionForDate(date);
	}

	@Override
	public void updateRevisionToExported(Revision revision) {
		revisionRepository.updateRevisionToExported(revision);
	}

	@Override
	public byte[] exportRevisionToGraphMl(long idRevision) {

		Noeud rootNode = treeRepository.getWholeTreeForRevision(idRevision).get(0);

		DocumentFactory factory = DocumentFactory.getInstance();
		Element root = factory.createElement("graphml");
		Document document = factory.createDocument(root);
		document.setXMLEncoding("utf-8");

		root.addAttribute("xmlns", "http://graphml.graphdrawing.org/xmlns");
		root.addAttribute(
				"xsi:schemaLocation",
				"http://graphml.graphdrawing.org/xmlns http://graphml.graphdrawing.org/xmlns/1.0/graphml.xsd");
		root.add(new Namespace("xsi",
				"http://www.w3.org/2001/XMLSchema-instance"));
		root.add(new Namespace("y", "http://www.yworks.com/xml/graphml"));

		root.addElement("key").addAttribute("attr.name", "sigle")
				.addAttribute("attr.type", "string")
				.addAttribute("for", "node").addAttribute("id", "d4");
		root.addElement("key").addAttribute("attr.name", "label")
				.addAttribute("attr.type", "string")
				.addAttribute("for", "node").addAttribute("id", "d5");

		Element graph = root.addElement("graph");
		graph.addAttribute("id",
				String.valueOf(rootNode.getRevision().getIdRevision()));
		graph.addAttribute("edgedefault", "undirected");

		buildGraphMlTree(graph, rootNode);

		ByteArrayOutputStream os_writer = new ByteArrayOutputStream();
		try {
			BufferedWriter wtr = new BufferedWriter(new OutputStreamWriter(
					os_writer, "UTF-8"));
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
	 * @param noeud
	 */
	protected void buildGraphMlTree(Element graph, Noeud noeud) {

		Element el = graph.addElement("node").addAttribute("id",
				noeud.getIdService().toString());

		// Node rollover (for yed)
		el.addElement("data").addAttribute("key", "d4")
				.setText(noeud.getSigle());

		// Node label (for yed)
		el.addElement("data").addAttribute("key", "d5")
				.setText(noeud.getLabel());

		for (Noeud enfant : noeud.getNoeudsEnfants()) {
			buildGraphMlTree(graph, enfant);
			graph.addElement("edge")
					.addAttribute("source", noeud.getIdService().toString())
					.addAttribute("target", enfant.getIdService().toString());
		}
	}

	@Override
	@Transactional(value = "adsTransactionManager")
	public List<ErrorMessageDto> rollbackToPreviousRevision(RevisionDto revisionDto, Long idRevision) {

		// Do the verifications on whether this creation is authorized
		Revision revisionToRollbackTo = revisionRepository.getRevision(idRevision);

		// If there are no nodes, that means this revision does not exists
		if (revisionToRollbackTo == null) {
			ErrorMessageDto msg = new ErrorMessageDto();
			msg.setMessage(String.format("La révision id [%s] donnée en paramètre n'existe pas.", idRevision));
			return Arrays.asList(msg);
		}

		// Can only rollback to previously deployed revision
		if (revisionToRollbackTo.getDateEffet().after(helperService.getCurrentDate())) {
			ErrorMessageDto msg = new ErrorMessageDto();
			msg.setMessage(String.format("La révision id [%s] n'a pas encore été appliquée, elle ne peut donc pas être réappliquée.", idRevision));
			return Arrays.asList(msg);
		}

		// Force DateEffet, description and to be today
		revisionDto.setDateEffet(helperService.getCurrentDate());
		revisionDto.setDescription(String.format("Rollback à la révision id [%s].", idRevision));
		revisionDto.setDateDecret(revisionToRollbackTo.getDateDecret());

		List<Noeud> nodes = treeRepository.getWholeTreeForRevision(idRevision);

		// At last, proceed with the creation (will go through only if the data respecs the data consistency for creation)
		return createTreeService.createTreeFromRevisionAndNoeuds(revisionDto, nodes.get(0), true);
	}

	@Override
	public RevisionDto getRevisionById(Long idRevision) {

		Revision rev = revisionRepository.getRevision(idRevision);

		if (rev == null)
			return null;

		return new RevisionDto(rev);
	}
}
