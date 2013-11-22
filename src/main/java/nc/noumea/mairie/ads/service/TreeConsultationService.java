package nc.noumea.mairie.ads.service;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;

import nc.noumea.mairie.ads.domain.Noeud;
import nc.noumea.mairie.ads.domain.Revision;
import nc.noumea.mairie.ads.dto.NoeudDto;
import nc.noumea.mairie.ads.repository.IRevisionRepository;
import nc.noumea.mairie.ads.repository.ITreeRepository;

import org.apache.commons.lang3.StringEscapeUtils;
import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class TreeConsultationService implements ITreeConsultationService {

	@Autowired
	private ITreeRepository treeRepository;
	
	@Autowired
	private IRevisionRepository revisionRepository;
	
	@Override
	public NoeudDto getTreeOfLatestRevisionTree() {

		return new NoeudDto(getLatestRevisionRootNode());
	}

	@Override
	public byte[] exportTreeOfLatestRevisionToGraphMl() {
		
		Noeud rootNode = getLatestRevisionRootNode();
		
		DocumentFactory factory = DocumentFactory.getInstance();
		Element root = factory.createElement("graphml");
		Document document = factory.createDocument(root);
		document.setXMLEncoding("utf-8");

		root.addAttribute("xmlns", "http://graphml.graphdrawing.org/xmlns");
		root.addAttribute("xsi:schemaLocation", "http://graphml.graphdrawing.org/xmlns http://graphml.graphdrawing.org/xmlns/1.0/graphml.xsd");
		root.add(new Namespace("xsi","http://www.w3.org/2001/XMLSchema-instance"));
		root.add(new Namespace("y","http://www.yworks.com/xml/graphml"));
		
		root.addElement("key")
			.addAttribute("attr.name", "sigle")
			.addAttribute("attr.type", "string")
			.addAttribute("for", "node")
			.addAttribute("id", "d4");
		root.addElement("key")
			.addAttribute("attr.name", "label")
			.addAttribute("attr.type", "string")
			.addAttribute("for", "node")
			.addAttribute("id", "d5");
		
		Element graph = root.addElement("graph");
		graph.addAttribute("id", String.valueOf(rootNode.getRevision().getIdRevision()));
		graph.addAttribute("edgedefault", "undirected");
		
		buildGraphMlTree(graph, rootNode);
		
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
	 * Responsible for retrieving the latest revision of the tree and
	 * its root node only
	 * @return
	 */
	protected Noeud getLatestRevisionRootNode() {
		
		// Get latest revision
		Revision rev = revisionRepository.getLatestRevision();
		
		// Return root node associated with this revision
		return treeRepository.getWholeTreeForRevision(rev.getIdRevision()).get(0);
	}
	
	/**
	 * Recursive method to build graphml nodes and edges for the entire tree
	 * @param graph
	 * @param noeud
	 */
	protected void buildGraphMlTree(Element graph, Noeud noeud) {
		
		Element el = graph.addElement("node")
				.addAttribute("id", noeud.getIdService().toString());

		// Node rollover (for yed)
		el.addElement("data")
				.addAttribute("key", "d4")
				.setText(noeud.getSigle());
		
		// Node label (for yed)
		el.addElement("data")
				.addAttribute("key", "d5")
				.setText(noeud.getLabel());

		for (Noeud enfant : noeud.getNoeudsEnfants()) {
			buildGraphMlTree(graph, enfant);
			graph.addElement("edge")
					.addAttribute("source", noeud.getIdService().toString())
					.addAttribute("target", enfant.getIdService().toString());
		}
	}		
}
