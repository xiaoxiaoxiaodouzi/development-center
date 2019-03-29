package com.chinacreator.c2.project.service;

import java.util.HashMap;
import java.util.Map;

import com.chinacreator.asp.comp.sys.oauth2.common.CredentialStore;
import com.chinacreator.c2.uop.OrgUserService;
import com.chinacreator.c2.uop.User;

public class LocalUserService {
	
	public Map<String,Object> queryByUserName(String name){
		return OrgUserService.getUserByName(name);
	}
	
	public Map<String,Object> getUserInfo(){
		Map<String,Object> map = new HashMap<String,Object>();
		String userId = CredentialStore.getCurrCredential().getUserInfo().getId();
		User user = OrgUserService.getUserInfoById(userId);
		map = queryByUserName(user.getEmail().substring(0, user.getEmail().indexOf("@")));
		map.put("userId", map.get("user_id"));
		map.put("userRealname", map.get("user_realname"));
		map.put("userName", map.get("user_name"));
		if(user.getSex().equals("0")){
			map.put("userSex", "-1");
		}else if(user.getSex().equals("1")){
			map.put("userSex", "M");
		}else{
			map.put("userSex", "F");
		}
		map.put("userMobiletel1", user.getPhone());
		map.put("userEmail", user.getEmail());
		return map;
	}
}
