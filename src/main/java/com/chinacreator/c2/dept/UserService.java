package com.chinacreator.c2.dept;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.chinacreator.c2.dao.Dao;
import com.chinacreator.c2.dao.DaoFactory;
import com.chinacreator.c2.uop.OrgUserService;
import com.chinacreator.c2.uop.User;
import com.chinacreator.c2.workbench.WhiteName;

public class UserService {
	
	private static Dao<WhiteName> whiteNameDao = DaoFactory.create(WhiteName.class); 
	
	/**
	 *  获取机构下除白名单之外的所有用户信息
	 * 
	 * @param orgId 机构id
	 * @return
	 */
	public List<Map<String,Object>> getOrgUsersFilterByWhiteList(String orgId){
		List<Map<String,Object>> users = new ArrayList<Map<String,Object>>();
		List<User> allUser = OrgUserService.queryUserByOrg(orgId);
		if(allUser.size()==0) return users;
		List<String> userName = new ArrayList<String>();
		for(User u:allUser){
			userName.add(u.getEmail().substring(0, u.getEmail().indexOf("@")));
		}
		List<String> whiteList = whiteNameDao.getSession().selectList("com.chinacreator.c2.workbench.WhiteNameMapper.selectWhiteNameListInUsers", userName);
		
		Iterator<User> it = allUser.iterator();
		while(it.hasNext()){
			User uu = it.next();
			String name = uu.getEmail().substring(0, uu.getEmail().indexOf("@"));
			if(whiteList.contains(name)){
				it.remove() ;
			}else{
				Map<String,Object> map = new HashMap<String,Object>();
				map = uu;
				map.put("userName", name);
				map.put("userId", uu.getId());
				map.put("userRealname", uu.getName());
				users.add(map);
			}
		}
		
		return users;
	}

}
