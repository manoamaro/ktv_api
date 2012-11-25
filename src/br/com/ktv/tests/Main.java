package br.com.ktv.tests;

import br.com.ktv.linkedquery.LinkedDataQuery;

public class Main {

	public static void main(String[] args) {
		System.out.println(LinkedDataQuery.getInstance().navigateId("http://dbpedia.org/resource/Pulp_Fiction").toString());		
	}

}
