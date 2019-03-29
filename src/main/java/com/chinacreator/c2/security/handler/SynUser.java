package com.chinacreator.c2.security.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.chinacreator.c2.dao.Dao;
import com.chinacreator.c2.dao.DaoFactory;
import com.chinacreator.c2.uop.OrgUserService;
import com.chinacreator.c2.uop.User;
import com.google.common.collect.Lists;

/**
 * 同步用户
 * 
 * @author 彭盛
 * 
 */
public class SynUser {

	/**
	 * 添加用户，判断用户是否存在，如果不存在则添加，存在忽略
	 * 
	 * @param userDTO
	 */
	public Map<String,Object> addUser(User user) {
		Map<String,Object> map = new HashMap<String,Object>();
		if(null != user && !existsByUserName(user.getEmail().substring(0, user.getEmail().indexOf("@")))){
			map.put("userName", user.getEmail().substring(0, user.getEmail().indexOf("@")));
			map.put("userRealName", user.getName());
			map.put("userId", user.getId());
			map.put("userPinyin", Pinyin4jUtil.getPinYin(user.getName()));
			map.put("remark3", Pinyin4jUtil.getPinYinHeadChar(user.getName()));
			map.put("userIsvalid", 2);
			map.put("state", 1);
			if(user.get("sex").equals("2")){
				map.put("userSex", "F");
			}else if(user.get("sex").equals("1")){
				map.put("userSex", "M");
			}else{
				map.put("userSex", "-1");
			}
			map.put("userHometel", user.get("phone"));
			map.put("userWorktel", user.get("phone"));
			map.put("userMobiletel1", user.get("phone"));
			map.put("userMobiletel2", user.get("phone"));
			map.put("userEmail", user.get("email"));
			if(user.getSex()!=null && user.getSex().equals("2")){
				map.put("remark1", "assets/img/profile-pics/" + (new Random().nextInt(4) + 5) + ".jpg");
			}else{
				map.put("remark1", "assets/img/profile-pics/" + (new Random().nextInt(4) + 1) + ".jpg");
			}
			insertUser(map);
		}
		return map;
	}

	public void addUserSys(String... userNames) {
		if (null != userNames && userNames.length > 0) {
			List<String> set = new ArrayList<String>();
			for (String name : userNames) {
				if (!existsByUserName(name)) {
					set.add(name);
				}
			}
			if (!set.isEmpty()) {
				List<User> users = OrgUserService.queryUserByNames(set);
				for (User userDTO : users) {
					addUser(userDTO);
				}
			}
		}
	}
	
	private void insertUser(Map<String, Object> map) {
		Dao<Object> dao = DaoFactory.create(Object.class);
		dao.getSession().insert("com.chinacreator.c2.project.info.MemberMapper.insertUser", map);
	}

	private boolean existsByUserName(String name) {
		Dao<Object> dao = DaoFactory.create(Object.class);
		Map<String,Object> user = dao.getSession().selectOne("com.chinacreator.c2.project.info.MemberMapper.existsByUserName", name);
		return user!=null?true:false;
	}

	public List<Map<String,Object>> addUserSys(List<User> users) {
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>(); 
	    int start = 0, end = 500;
	    while (start < users.size()) {
	      List<String> userNames = Lists.newArrayList();
	      for(int i=start;i<end;i++){
	        if(users.get(i).getEmail()!=null&&users.get(i).getEmail().indexOf("@")>-1){
	          userNames.add(users.get(i).getEmail().substring(0, users.get(i).getEmail().indexOf("@")));
	        }
	      }
	      if(userNames.size()>0){
	        Dao<Object> dao = DaoFactory.create(Object.class);
	        List<Map<String,Object>> userList = dao.getSession().selectList("com.chinacreator.c2.project.info.MemberMapper.getUserByNames", userNames);
	          Map<String,Object> userMap = new HashMap<String,Object>();
	          for(Map<String,Object> map:userList){
	            userMap.put(map.get("user_name").toString(),map);
	          }
	          for(int i=start;i<end;i++){
	            if(users.get(i).getEmail()!=null&&users.get(i).getEmail().indexOf("@")>-1
	                &&userMap.get(users.get(i).getEmail().substring(0, users.get(i).getEmail().indexOf("@")))==null){
	              addUser(users.get(i));
	            }
	          }
	      }
	      start = end;
	      end += 500;
	      if(end>=users.size()) end = users.size();
	    }
		return list;
	}
}
