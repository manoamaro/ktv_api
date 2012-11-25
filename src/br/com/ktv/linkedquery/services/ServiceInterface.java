package br.com.ktv.linkedquery.services;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ReadWrite;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.tdb.TDBFactory;
import com.hp.hpl.jena.util.FileManager;

public abstract class ServiceInterface {

	protected OntModel movieOntologyModel;
	
	protected ServiceInterface() {
		
		this.movieOntologyModel = ModelFactory.createOntologyModel();
//		InputStream in = FileManager.get().open(getClass().getResource("../../ontologies/movieontology.owl").getPath());;
//		this.movieOntologyModel.read(in, "RDF/XML");
	}
	
	protected String readFileSparql(String file, String... replaces) {
		StringBuffer contents = new StringBuffer();
		String replaced = "";
		try {
			BufferedReader in = new BufferedReader(new FileReader(file));
			String text;
			while((text = in.readLine()) != null)
				contents.append(text);
			in.close();
			replaced = contents.toString();
			for (int i = 0; i < replaces.length; i++)
				replaced = replaced.replaceAll("\\{"+i+"\\}", replaces[i]);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return replaced;
	}
	
	protected ResultSet executeQuery(String endpoint, String sparql) {
		Query query = QueryFactory.create(sparql);
		QueryExecution qe = QueryExecutionFactory.sparqlService(endpoint,query);
		ResultSet resultSet = qe.execSelect();
		return resultSet;
	}
	
	protected Model executeConstruct(String endpoint, String sparql) {
		Query query = QueryFactory.create(sparql);
		QueryExecution qe = QueryExecutionFactory.sparqlService(endpoint,query);
		Model resultModel = qe.execConstruct();
		return resultModel;
	}
	
	protected Model describeObject(String endpoint, String sparql) {
		Query query = QueryFactory.create(sparql);
		QueryExecution qe = QueryExecutionFactory.sparqlService(endpoint,query);
		Model resultModel = qe.execDescribe();
		return resultModel;
	}
	
	protected void saveCache(Model modelToSave) {
		String directory = "databases/dbpedia";
		File f = new File(directory);
		if (!f.exists()) {
			f.mkdirs();
		}
		Dataset dataset = TDBFactory.createDataset(directory);
		dataset.begin(ReadWrite.WRITE);
		try {
			Model model = dataset.getDefaultModel();
			model.add(modelToSave);
			dataset.commit();
		} finally {
			dataset.end();
		}
	}
	
	protected Model loadCache(String sparql) {
		Model result = null;
		String directory = "databases/dbpedia";
		File f = new File(directory);
		if (!f.exists()) {
			f.mkdirs();
		}
		Dataset dataset = TDBFactory.createDataset(directory);
		dataset.begin(ReadWrite.READ);
		try {
			QueryExecution qExec = QueryExecutionFactory.create(sparql, dataset);
			result = qExec.execConstruct();
		} finally {
			dataset.end();
		}
		return result;
	}
	
		
	public abstract JSONArray searchByKeyword(String keyword);
	public abstract JSONObject accessObject(String uri);
	
}
