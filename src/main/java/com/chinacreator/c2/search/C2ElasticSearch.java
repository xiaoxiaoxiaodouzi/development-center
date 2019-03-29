package com.chinacreator.c2.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.suggest.SuggestRequestBuilder;
import org.elasticsearch.action.suggest.SuggestResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.highlight.HighlightField;
import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.search.suggest.Suggest.Suggestion;
import org.elasticsearch.search.suggest.Suggest.Suggestion.Entry;
import org.elasticsearch.search.suggest.Suggest.Suggestion.Entry.Option;
import org.elasticsearch.search.suggest.completion.CompletionSuggestionBuilder;

public class C2ElasticSearch {
	public void initElasticClient(){
		C2ElasticSearchService.getElasticsearchClient();
	}
	//全局索引
	public Map<String,Object> simpleSearch(String search,Map<String,Object> searchCondition,Integer page){
		Client esClient = C2ElasticSearchService.getElasticsearchClient();
		if(page == null)page = 1;
		int size = 20;
		int from = (page-1)*size;
		
		String searchType = searchCondition.get("type").toString();
		Object searchModule = searchCondition.get("module");
		Object searchMilestone = searchCondition.get("milestone");
		Object searchLabel = searchCondition.get("labelName");
		
		//根据条件添加过滤
		BoolQueryBuilder filterBuilder = QueryBuilders.boolQuery();
		List<String> projectIds = (List<String>) searchCondition.get("projectIds");
		QueryBuilder projectQueryBuilder = QueryBuilders.termsQuery("projectId", projectIds.toArray());
		filterBuilder.must(projectQueryBuilder);
		if(searchModule!=null){
			QueryBuilder moduleFilter =QueryBuilders.termQuery("moduleId", searchModule);
			filterBuilder.must(moduleFilter);
		}
		if(searchMilestone!=null){
			QueryBuilder milestoneFilter =QueryBuilders.termQuery("milestoneId", searchMilestone);
			filterBuilder.must(milestoneFilter);
		}
		if(searchLabel!=null){
			QueryBuilder labelFilter =QueryBuilders.termQuery("labels.name", searchLabel);
			filterBuilder.must(labelFilter);
		}
		
		//如果查询字段为空，查询所有
		QueryBuilder qb = search.equals("")?QueryBuilders.matchAllQuery():QueryBuilders.simpleQueryStringQuery(search);
		
		//查询请求
		SearchRequestBuilder searchRequest = esClient.prepareSearch(C2ElasticSearchService.GLOBAL_SEARCH_INDEX)
				.setQuery(qb)
				.setPostFilter(filterBuilder)
				.setFrom(from).setSize(size)
				.addHighlightedField("description", 20, 6)
				.addHighlightedField("spec", 20, 6)
				.setHighlighterRequireFieldMatch(false);
		if(!searchType.equals("all"))searchRequest.setTypes(searchType);
		//添加高亮字段
		String[] hightlightField = {"name","title","projectName","moduleName","milestoneTitle","userRealname","openedByName","createByName","assignedToName"};
		for (String fieldName : hightlightField) {
			searchRequest.addHighlightedField(fieldName);
		}
//		System.out.println(searchRequest);
		
		//执行检索
		long startTime = System.currentTimeMillis();
		SearchResponse sr = searchRequest.execute().actionGet();
		long spendTime = System.currentTimeMillis()-startTime;
		
		//获取查询结果
		SearchHits sh = sr.getHits();
		for (SearchHit searchHit : sh) {
			Map<String,Object> source = searchHit.getSource();
			for (String fieldName : hightlightField) {
				sourceHightlight(searchHit, source, fieldName);
			}
			HighlightField descriptionhf = searchHit.getHighlightFields().get("description");
			if(descriptionhf!=null){
				Text[] htext = descriptionhf.getFragments();
				String hightlightString="";
				for (Text text : htext) {
					hightlightString += text.toString()+" ... &nbsp;&nbsp;&nbsp; ";  
				}
				source.put("descriptionHL", hightlightString.replace("\n",""));
			}
			HighlightField spechf = searchHit.getHighlightFields().get("spec");
			if(spechf!=null){
				Text[] htext = spechf.getFragments();
				String hightlightString="";
				for (Text text : htext) {
					hightlightString += text.toString()+" ... &nbsp;&nbsp;&nbsp;";  
				}
				source.put("specHL", hightlightString.replace("\n",""));
			}
		}
//		esClient.close();
		
		Map<String,Object> searchResult = new HashMap<String, Object>();
		searchResult.put("searchHits", sh);
		searchResult.put("spendTime", spendTime);
		return searchResult;
	}
	
	//查询建议
	public List<String> searchSuggest(String search){
		Client esClient = C2ElasticSearchService.getElasticsearchClient();
 		SuggestRequestBuilder suggestRequestBuilder =esClient.prepareSuggest(C2ElasticSearchService.GLOBAL_SEARCH_INDEX);
		
		String[] suggestField = {"assignedTo","milestoneTitle","moduleName","projectName","userRealname"};
		for (String suggestName : suggestField) {
			CompletionSuggestionBuilder suggestion = new CompletionSuggestionBuilder(suggestName); 
			suggestion.text(search);
			suggestion.field(suggestName+"Suggest");
			suggestRequestBuilder.addSuggestion(suggestion);
		}
		
		SuggestResponse suggestResponse = suggestRequestBuilder.execute().actionGet();
	    
		List<String> items = new ArrayList<String>();
		for (String suggestName : suggestField) {
			Suggestion<? extends Entry<? extends Option>> suggestion = suggestResponse.getSuggest().getSuggestion(suggestName);
			if(suggestion!=null){
				Iterator<? extends Option> iterator = suggestion.iterator().next().getOptions().iterator();
				while (iterator.hasNext()) {
					Suggest.Suggestion.Entry.Option next = iterator.next();
					items.add(next.getText().string());
				}
			}
		}
	    
//	    client.close();
		return items;
	}
	
	private void sourceHightlight(SearchHit searchHit,Map<String,Object> source,String fieldName){
		HighlightField namehf = searchHit.getHighlightFields().get(fieldName);
		if(namehf!=null){
			Text[] htext = namehf.getFragments();
			source.put(fieldName, htext[0].toString());
		}
	}
	
}
