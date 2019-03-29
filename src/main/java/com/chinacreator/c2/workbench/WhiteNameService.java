package com.chinacreator.c2.workbench;

import com.chinacreator.c2.dao.Dao;
import com.chinacreator.c2.dao.DaoFactory;

public class WhiteNameService {

	public void addWhiteName(WhiteName name){
		Dao<WhiteName> dao = DaoFactory.create(WhiteName.class);
		dao.insert(name);
	}
	
	public void deleteWhiteName(String name){
		Dao<WhiteName> dao = DaoFactory.create(WhiteName.class);
		dao.delete(name);
	}
}
