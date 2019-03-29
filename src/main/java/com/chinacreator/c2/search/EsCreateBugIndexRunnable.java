package com.chinacreator.c2.search;

import java.util.Map;

import org.elasticsearch.client.Client;

import com.alibaba.fastjson.JSONObject;
import com.chinacreator.c2.dao.Dao;
import com.chinacreator.c2.dao.DaoFactory;

public class EsCreateBugIndexRunnable implements Runnable {
	private String bugId;
	public EsCreateBugIndexRunnable(String bugId) {
		this.bugId = bugId;
	}
	@Override
	public void run() {
		Client esClient = C2ElasticSearchService.getElasticsearchClient();
		Dao<Object> dao =DaoFactory.create(Object.class);
		Map<String,Object> map = dao.getSession().selectOne("bugIndex", bugId) ;
		
		String json = JSONObject.toJSONString(map);
		esClient.prepareIndex(C2ElasticSearchService.GLOBAL_SEARCH_INDEX, "bug", bugId)
				.setSource(json).execute().actionGet();
	}

}
