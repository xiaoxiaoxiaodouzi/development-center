package com.chinacreator.c2.search;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

import com.alibaba.fastjson.JSONObject;
import com.chinacreator.c2.config.ConfigManager;

public class C2ElasticSearchService {
	//Elasticsearch索引名称
	public static String GLOBAL_SEARCH_INDEX = "development-index";
	private static Client esClient;
	//es索引操作线程池。如果索引操作需要进行数据库查询操作，一律放到executor中执行。
	private static ExecutorService executor = Executors.newCachedThreadPool();
	
	//建立Elasticsearch连接
	public static Client getElasticsearchClient(){
		String elasticsearchIPs = ConfigManager.getInstance().getConfig("elasticsearch.ip");
		String clusterName = ConfigManager.getInstance().getConfig("elasticsearch.clusterName");
		String indexName = ConfigManager.getInstance().getConfig("elasticsearch.indexName");
		GLOBAL_SEARCH_INDEX = indexName;
		if(esClient==null){
//			String[] ips = elasticsearchIPs.split(";");
//			TransportAddress[] tas = new TransportAddress[ips.length];
//			for (int i = 0; i < ips.length; i++) {
//				tas[i] =new InetSocketTransportAddress(ips[i], 9300);
//			}
//			Settings settings = ImmutableSettings.settingsBuilder().put("cluster.name", clusterName).build();
//			esClient = new TransportClient(settings).addTransportAddresses(tas);
			
			String[] ips = elasticsearchIPs.split(";");
			InetSocketTransportAddress[] tas = new InetSocketTransportAddress[ips.length];
			for (int i = 0; i < ips.length; i++) {
				try {
					tas[i] =new InetSocketTransportAddress(InetAddress.getByName(ips[i]), 9300);
				} catch (UnknownHostException e) {
					e.printStackTrace();
				}
			}
			Settings settings = Settings.settingsBuilder().put("cluster.name", clusterName).build();
			esClient = TransportClient.builder().settings(settings).build().addTransportAddresses(tas);
		}
		return esClient;
	}
	//添加任务索引
	public static void taskIndex(String taskId){
		executor.execute(new EsCreateTaskIndexRunnable(taskId));
	}
	//更新任务索引
	public static void updateTaskIndex(String taskId){
		executor.execute(new EsUpdateTaskIndexRunnable(taskId));
	}
	//删除任务索引
	public static void deleteTaskIndex(String taskId){
		try {
			getElasticsearchClient().prepareDelete(GLOBAL_SEARCH_INDEX, "task", taskId).execute().actionGet();
		} catch (Exception e) {
			System.out.println("ES索引错误");
		}
	}
	
	//bug索引
	public static void bugIndex(String bugId){
		executor.execute(new EsCreateBugIndexRunnable(bugId));
	}
	public static void updateBugIndex(String bugId){
		executor.execute(new EsUpdateBugIndexRunnable(bugId));
	}
	public static void deleteBugIndex(String bugId){
		try {
			getElasticsearchClient().prepareDelete(GLOBAL_SEARCH_INDEX, "bug", bugId).execute().actionGet();
		} catch (Exception e) {
			System.out.println("ES索引错误");
		}
	}
	//局部更新bug索引
	public static void updateBugIndexLittle(String bugId,Map<String,Object> little){
		String json = JSONObject.toJSONString(little);
		try {
			getElasticsearchClient().prepareUpdate(GLOBAL_SEARCH_INDEX, "bug", bugId).setDoc(json).get();
		} catch (Exception e) {
			System.out.println("ES索引错误");
		}
	}
	
	//需求索引
	public static void storyIndex(String storyId){
		executor.execute(new EsCreateStoryIndexRunnable(storyId));
	}
	public static void updateStoryIndex(String storyId){
		executor.execute(new EsUpdateStoryIndexRunnable(storyId));
	}
	public static void updateStoryIndexLittle(String storyId,Map<String,Object> little){
		String json = JSONObject.toJSONString(little);
		try {
			getElasticsearchClient().prepareUpdate(GLOBAL_SEARCH_INDEX, "story", storyId).setDoc(json).get();
		} catch (Exception e) {
			System.out.println("ES索引错误");
		}
	}
	public static void deleteStoryIndex(String storyId){
		try {
			getElasticsearchClient().prepareDelete(GLOBAL_SEARCH_INDEX, "story", storyId).execute().actionGet();
		} catch (Exception e) {
			System.out.println("ES索引错误");
		}
	}
}
