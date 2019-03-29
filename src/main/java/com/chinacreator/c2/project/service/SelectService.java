package com.chinacreator.c2.project.service;

import java.util.List;
import java.util.Map;

import com.chinacreator.c2.dao.Dao;
import com.chinacreator.c2.dao.DaoFactory;
import com.chinacreator.c2.dao.mybatis.enhance.Page;
import com.chinacreator.c2.dao.mybatis.enhance.Pageable;
import com.chinacreator.c2.dao.mybatis.enhance.RowBounds4Page;

public class SelectService {
	
	public List<Map<String,Object>> selectList(String statement,Map<String,Object> parameter){
		Dao<Object> baseDao = DaoFactory.create(Object.class);
		List<Map<String,Object>> list = baseDao.getSession().selectList(statement,parameter);
		return list;
	}
	
	public Object selectOne(String statement,Map<String,Object> parameter){
		Dao<Object> baseDao = DaoFactory.create(Object.class);
		Object one = baseDao.getSession().selectOne(statement,parameter);
		return one;
	}
	
	public Page<Object> selectPage(String statement,Map<String,Object> parameter,Pageable pageable){
		int page = pageable == null ? 1 : pageable.getPageIndex();
	    int size = pageable == null ? 10 : pageable.getPageSize();
	    RowBounds4Page rowBounds = new RowBounds4Page((page - 1) * size, size, null, null, true);
	    
	    Dao<Object> baseDao = DaoFactory.create(Object.class);
	    List<Object> list = baseDao.getSession().selectList(statement,parameter,rowBounds);
	    
	    Page<Object> pagingResult = new Page<Object>(page, size, rowBounds.getTotalSize(),list);
	    return pagingResult;
	}

}
