package com.chinacreator.c2.project.cache;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.chinacreator.c2.annotation.Entity;
import com.chinacreator.c2.dao.Dao;
import com.chinacreator.c2.dao.DaoFactory;
import com.chinacreator.c2.project.task.Task;
import com.google.common.collect.ImmutableMap;

/**
 * 	DB"字段-描述"缓存,用于action-history明细展示
 * 
 * @author tyf
 *
 */
public class DBCommentCache {

	private static Map<String,Map<String,String>> cache = new HashMap<String,Map<String,String>>();

	public static Map<String,String> get(String tableName){
		
		tableName = tableName.toLowerCase().trim() ;
		
		if(!cache.containsKey(tableName)){
			Map<String,String> condition = new HashMap<String,String>() ;
			condition.put("tabName", tableName) ;
			condition.put("schema", Task.class.getAnnotation(Entity.class).ds()) ;
			List<Map<String,String>> list = DaoFactory.create(Object.class).getSession().selectList("com.chinacreator.c2.project.task.TaskMapper.getColCommentByTableName", condition) ;
			
			ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();  
			for(Map<String,String> map : list){
				builder.put(map.get("col"), map.get("comment"));  
			}
			final ImmutableMap<String, String> immuMap = builder.build(); 
			if(immuMap.size()!=0){
				cache.put(tableName, immuMap) ;
			}
		}
		return cache.get(tableName) ;
	}
	
}
