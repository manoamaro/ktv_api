package br.com.ktv.linkedquery.services;


import java.io.InputStream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.reasoner.ReasonerRegistry;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDFS;

public class DbPediaService extends ServiceInterface {
	
	private static OntModel dbPediaOntologyModel;
	private static Reasoner reasoner;
	private static DbPediaService instance;
	
	private DbPediaService() {
		super();
		InputStream in = FileManager.get().open(getClass().getResource("/br/com/ktv/ontologies/dbpedia_3.8.owl").getPath());
		dbPediaOntologyModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_RDFS_INF);
		dbPediaOntologyModel.read(in, "RDF/XML");
		dbPediaOntologyModel.setNsPrefix("dbpedia2","http://dbpedia.org/property/");
		dbPediaOntologyModel.setNsPrefix("dbpedia-owl","http://dbpedia.org/ontology/");
		reasoner = ReasonerRegistry.getRDFSReasoner().bindSchema(dbPediaOntologyModel);
		System.out.println("Reasoner registred");
	}
	
	private String processKeywords(String k) {
		return k.replace(" ", " and ").replace(";", " or ");
	}
	
	@Override
	public JSONArray searchByKeyword(String keyword) {
//		Model result = loadCache(this.readFileSparql(getClass().getResource("../queries/query2.sparql").getPath(), keyword));
		Model result;
		String sparql = this.readFileSparql(getClass().getResource("../queries/query1.sparql").getPath(), processKeywords(keyword));
		result = this.executeConstruct("http://dbpedia.org/sparql", sparql);
//		saveCache(result);
		OntModelSpec oms = new OntModelSpec(OntModelSpec.OWL_MEM_RDFS_INF);
		oms.setReasoner(reasoner);
		OntModel inf = (OntModel) ModelFactory.createOntologyModel(oms, result);
		OntClass work = inf.getOntClass(OWL.Thing.getURI());
		if (work == null) {
			return new JSONArray();
		}
		ExtendedIterator<OntResource> si = (ExtendedIterator<OntResource>) work.listInstances();
		JSONArray results = new JSONArray();
		while(si.hasNext()) {
			try {
				OntResource resource = si.next();
				JSONObject o = new JSONObject();
				o.put("id", resource.getURI());
				o.put("domain", resource.getRDFType().getLocalName());
				o.put("title", resource.getPropertyValue(RDFS.label).asLiteral().getString());
				o.put("abstract", resource.getPropertyValue(dbPediaOntologyModel.getProperty("http://dbpedia.org/ontology/", "abstract")).asLiteral().getString());
				results.put(o);
			} catch(Exception e) {
				
			}
		}
		return results;
    }

	@Override
	public JSONObject accessObject(String uri) {
		Model result = ModelFactory.createDefaultModel();
		result.read(uri);
		saveCache(result);
		OntModelSpec oms = new OntModelSpec(OntModelSpec.OWL_MEM_RDFS_INF);
		oms.setReasoner(reasoner);
		OntModel inf = (OntModel) ModelFactory.createOntologyModel(oms, result);
		StmtIterator si = inf.listStatements();
		JSONObject resultJson = new JSONObject();
		while(si.hasNext()) {
			try {
				Statement resource = si.next();
				Triple t = resource.asTriple();
				if(t.getObject().isLiteral()) {
					resultJson.put(t.getPredicate().getLocalName(), t.getObject().getLiteralValue());
				} else {
					resultJson.put(t.getPredicate().getLocalName(), t.getObject().getURI());
				}
			} catch(Exception e) {
				
			}
		}
		return resultJson;
	
	}

	public static DbPediaService getInstance() {
		if (instance == null)
			instance = new DbPediaService();
		return instance;
	}
}
