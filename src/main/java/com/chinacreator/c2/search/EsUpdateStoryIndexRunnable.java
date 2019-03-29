package com.chinacreator.c2.search;

import java.util.Map;

import org.elasticsearch.client.Client;

import com.alibaba.fastjson.JSONObject;
import com.chinacreator.c2.dao.Dao;
import com.chinacreator.c2.dao.DaoFactory;

public class EsUpdateStoryIndexRunnable implements Runnable {
	private String storyId;
	public EsUpdateStoryIndexRunnable(String storyId) {
		this.storyId = storyId;
	}
	@Override
	public void run() {
		Client esClient = C2ElasticSearchService.getElasticsearchClient();
		Dao<Object> taskDao =DaoFactory.create(Object.class);
		Map<String,Object> projectTask = taskDao.getSession().selectOne("storyIndex", storyId) ;
		
		String json = JSONObject.toJSONString(projectTask);
		esClient.prepareUpdate(C2ElasticSearchService.GLOBAL_SEARCH_INDEX, "story", storyId).setDoc(json).get();
	}

}
