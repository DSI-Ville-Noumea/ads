package nc.noumea.mairie.ads.service;

import nc.noumea.mairie.ads.domain.Noeud;
import nc.noumea.mairie.ads.domain.Revision;
import nc.noumea.mairie.ads.dto.DiffNoeudDto;
import nc.noumea.mairie.ads.dto.DiffRevisionDto;
import nc.noumea.mairie.ads.dto.ErrorMessageDto;
import nc.noumea.mairie.ads.dto.RevisionDto;
import nc.noumea.mairie.ads.repository.IRevisionRepository;
import nc.noumea.mairie.ads.repository.ITreeRepository;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.tuple.Pair;
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
import java.util.*;

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

		// Fetch the revision to re apply
		Revision revisionToRollbackTo = revisionRepository.getRevision(idRevision);

		// If it does not exists, stop here
		if (revisionToRollbackTo == null) {
			ErrorMessageDto msg = new ErrorMessageDto();
			msg.setMessage(String.format("La révision id [%s] donnée en paramètre n'existe pas.", idRevision));
			return Arrays.asList(msg);
		}

		// Can only rollback to previously deployed revision
		// (if this revision is to be in the future, we cannot rollback to it)
		if (revisionToRollbackTo.getDateEffet().after(helperService.getCurrentDate())) {
			ErrorMessageDto msg = new ErrorMessageDto();
			msg.setMessage(String.format("La révision id [%s] n'a pas encore été appliquée, elle ne peut donc pas être réappliquée.", idRevision));
			return Arrays.asList(msg);
		}

		// Force DateEffet to be today, dateDecret to be the date of the rollbacked revision, set default description
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

		if (rev == null) {
			return null;
		}

		return new RevisionDto(rev);
	}

	@Override
	public DiffRevisionDto getRevisionsDiff(Long idRevision, Long idRevision2) {

		Revision rev1 = revisionRepository.getRevision(idRevision);
		Revision rev2 = revisionRepository.getRevision(idRevision2);

		if (rev1 == null || rev2 == null) {
			throw new RevisionNotFoundException();
		}

		DiffRevisionDto dto = new DiffRevisionDto(rev1, rev2);

		List<Noeud> sourceNodes = treeRepository.getWholeTreeForRevision(rev1.getIdRevision());
		Map<Integer, Noeud> sourceMap = new HashMap<>();

		for (Noeud n : sourceNodes) {
			sourceMap.put(n.getIdService(), n);
		}

		List<Noeud> targetNodes = treeRepository.getWholeTreeForRevision(rev2.getIdRevision());

		for (Noeud n : targetNodes) {
			Noeud existingNode = sourceMap.get(n.getIdService());

			// If this node did not exists before
			if (existingNode == null) {
				dto.getAddedNodes().add(new DiffNoeudDto(n));
				continue;
			}

			// Else, if this node has been moved
			if (existingNode.getNoeudParent() != null || n.getNoeudParent() != null) {
				if (!existingNode.getNoeudParent().getIdService().equals(n.getNoeudParent().getIdService())) {
					dto.getMovedNodes().add(Pair.of(new DiffNoeudDto(existingNode), new DiffNoeudDto(n)));
				}
			}

			// If a property has been changed
			boolean equals = EqualsBuilder.reflectionEquals(existingNode, n, Arrays.asList("idNoeud", "revision", "noeudParent", "noeudsEnfants", "version", "siservInfo", "typeNoeud", "noeud"))
					&& EqualsBuilder.reflectionEquals(existingNode.getSiservInfo(), n.getSiservInfo(), Arrays.asList("idSiservInfo", "noeud", "version"))
					&& EqualsBuilder.reflectionEquals(existingNode.getTypeNoeud(), n.getTypeNoeud(), Arrays.asList("label"));

			if (!equals) {
				dto.getModifiedNodes().add(Pair.of(new DiffNoeudDto(existingNode), new DiffNoeudDto(n)));
			}

			sourceMap.remove(n.getIdService());
		}

		for (Noeud remainingNode : sourceMap.values()) {
			dto.getRemovedNodes().add(new DiffNoeudDto(remainingNode));
		}

		return dto;
	}
}
