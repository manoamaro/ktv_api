package br.com.ktv.linkedquery;

import br.com.ktv.linkedquery.services.DbPediaService;
import br.com.ktv.linkedquery.services.ServiceInterface;

public class LinkedDataQuery {
	
	private ServiceInterface services[] = {DbPediaService.getInstance()};
	private static LinkedDataQuery instance = null;
	
	private LinkedDataQuery(){
		super();
	}
	
	public String queryByKeyword(String keyword) {
		String result = "";
		for (ServiceInterface si : services) {
			result += si.searchByKeyword(keyword).toString();
		}
		return result;
	}
	
	public String navigateId(String id) {
		String result = "";
		for (ServiceInterface si : services) {
			result += si.accessObject(id).toString();
		}
		return result;
	}
	
	public static LinkedDataQuery getInstance() {
		if(instance == null)
			instance = new LinkedDataQuery();
		return instance;
	}
	
}
