package com.chinacreator.c2.security.handler;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.chinacreator.asp.comp.sys.oauth2.common.Credential;
import com.chinacreator.asp.comp.sys.oauth2.common.spi.DefaultSpi;
import com.chinacreator.c2.dao.Dao;
import com.chinacreator.c2.dao.DaoFactory;
import com.chinacreator.c2.uop.OrgUserService;

public class OAuth2SSOFilterSpiImpl extends DefaultSpi {
	
	@Override
	public void onAuthenticateSuccessHandler(HttpServletRequest request,
			HttpServletResponse response, FilterChain chain, Credential credential){
		
		try {
			com.chinacreator.c2.uop.User uu = OrgUserService.getUserInfoById(credential.getUserInfo().getId());
			if(uu.getEmail()!=null && uu.getEmail().contains("@chinacreator.com")){
			    //用户信息必须包含公司邮箱 
				String userName = uu.getEmail().substring(0, uu.getEmail().indexOf("@"));
				Map<String,Object> userInfo = existsByUserName(userName);
				if(null != uu && userInfo==null){
					Map<String,Object> map = new HashMap<String,Object>();
					map.put("userName", userName);
					map.put("userId", uu.getId());
					map.put("userRealName", uu.getName());
					map.put("userPinyin", Pinyin4jUtil.getPinYin(uu.getName()));
					map.put("remark3", Pinyin4jUtil.getPinYinHeadChar(uu.getName()));
					map.put("userIsvalid", 2);
					if(uu.getSex().equals("2")){
						map.put("userSex", "F");
					}else if(uu.getSex().equals("1")){
						map.put("userSex", "M");
					}else{
						map.put("userSex", "-1");
					}
					map.put("userHometel",uu.getPhone());
					map.put("userWorktel", uu.getPhone());
					map.put("userMobiletel1", uu.getPhone());
					map.put("userMobiletel2", uu.getPhone());
					map.put("userEmail", uu.getEmail());
					if(uu.getSex()!=null && uu.getSex().equals("2")){
						map.put("remark1", "assets/img/profile-pics/" + (new Random().nextInt(4) + 5) + ".jpg");
					}else{
						map.put("remark1", "assets/img/profile-pics/" + (new Random().nextInt(4) + 1) + ".jpg");
					}
					insertUser(map);
				}
				
				//同步用户
				/*List<com.chinacreator.c2.uop.User> userDTOs = OrgUserService.queryUserByUserNames();
		        new SynUser().addUserSys(userDTOs);*/
		        
				super.onAuthenticateSuccessHandler(request, response, chain, credential) ;
			}else{
				throw new RuntimeException("请核实邮箱信息是否准确无误");
			}
		} catch (Exception e) {
			e.getStackTrace();
		}
		
	}

	private void insertUser(Map<String, Object> map) {
		Dao<Object> dao = DaoFactory.create(Object.class);
		dao.getSession().insert("com.chinacreator.c2.project.info.MemberMapper.insertUser", map);
	}

	private Map<String, Object> existsByUserName(String name) {
		Dao<Object> dao = DaoFactory.create(Object.class);
		Map<String,Object> user = dao.getSession().selectOne("com.chinacreator.c2.project.info.MemberMapper.existsByUserName", name);
		return user;
	}
}