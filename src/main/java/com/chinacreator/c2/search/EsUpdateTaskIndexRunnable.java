package com.chinacreator.c2.search;

import java.util.Map;

import org.elasticsearch.client.Client;

import com.alibaba.fastjson.JSONObject;
import com.chinacreator.c2.dao.Dao;
import com.chinacreator.c2.dao.DaoFactory;

public class EsUpdateTaskIndexRunnable implements Runnable {
	private String taskId;
	public EsUpdateTaskIndexRunnable(String taskId) {
		this.taskId = taskId;
	}
	@Override
	public void run() {
		Client esClient = C2ElasticSearchService.getElasticsearchClient();
		Dao<Object> taskDao =DaoFactory.create(Object.class);
		Map<String,Object> projectTask = taskDao.getSession().selectOne("projectTask", taskId) ;
		
		String json = JSONObject.toJSONString(projectTask);
		esClient.prepareUpdate(C2ElasticSearchService.GLOBAL_SEARCH_INDEX, "task", taskId).setDoc(json).get();
	}

}
