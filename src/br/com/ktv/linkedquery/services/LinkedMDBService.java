package br.com.ktv.linkedquery.services;


public class LinkedMDBService {

	
	public void searchByKeyword(String keyword) {
//		OntModel resultModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_RULE_INF, null);
//		String sparql = this.readFileSparql(getClass().getResource("../queries/query2.sparql").getPath(), keyword);
//		ResultSet rs = this.executeQuery("http://data.linkedmdb.org/sparql", sparql);
//		for(; rs.hasNext(); ) {
//			QuerySolution qs = rs.nextSolution();
//			OntClass movieClass = movieOntologyModel.getOntClass("http://www.movieontology.org/2009/11/09/Movie");
//			Individual movie = resultModel.createIndividual(qs.getResource("movie").getURI(), movieClass);
//			OntProperty title = movieOntologyModel.getOntProperty("http://www.movieontology.org/2009/10/01/movieontology.owl#title");
//			movie.addProperty(title, qs.getLiteral("title").getString());
//		}
//		resultModel.write(System.out);
	}

	public String accessObject(String uri) {
		return null;
	}


}
