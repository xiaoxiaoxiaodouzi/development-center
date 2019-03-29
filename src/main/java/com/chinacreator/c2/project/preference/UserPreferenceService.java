package com.chinacreator.c2.project.preference;

import java.util.HashMap;
import java.util.Map;

import com.chinacreator.c2.context.OperationContextHolder;
import com.chinacreator.c2.context.WebOperationContext;
import com.chinacreator.c2.project.info.UserPreferences;
import com.chinacreator.c2.project.info.UserPreferencesService;
import com.chinacreator.c2.uop.OrgUserService;
import com.chinacreator.c2.uop.User;

public class UserPreferenceService {
	
	private static UserPreferencesService preferenceService = new UserPreferencesService();
	
	public Map<String,UserPreferences> getUserPreferences(String... infoNames){
		WebOperationContext context = (WebOperationContext)OperationContextHolder.getContext();
		User user = OrgUserService.getUserInfoById(context.getUser().getId());
		String currentUserName =user.getEmail().substring(0, user.getEmail().indexOf("@"));
		Map<String,Object> uu = OrgUserService.getUserByName(currentUserName);
		Map<String,UserPreferences> result = new HashMap<String,UserPreferences>() ;
		for (String name : infoNames) {
			result.put(name, preferenceService.queryByPK(uu.get("user_id").toString(), name)) ;
		}
		return result ;
	}
	
	public void saveUserPreference(UserPreferences dto){
		WebOperationContext context = (WebOperationContext)OperationContextHolder.getContext();
		User user = OrgUserService.getUserInfoById(context.getUser().getId());
		String currentUserName =user.getEmail().substring(0, user.getEmail().indexOf("@"));
		Map<String,Object> uu = OrgUserService.getUserByName(currentUserName);
		dto.setUserId(uu.get("user_id").toString()) ;
		if(preferenceService.queryByPK(dto.getUserId(), dto.getInfoName()) == null){
			preferenceService.create(dto) ;
		}else{
			preferenceService.update(dto) ;
		}
		
	}
}
