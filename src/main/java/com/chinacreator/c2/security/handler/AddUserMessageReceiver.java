package com.chinacreator.c2.security.handler;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.chinacreator.c2.config.ConfigManager;
import com.chinacreator.c2.dao.Dao;
import com.chinacreator.c2.dao.DaoFactory;
import com.chinacreator.c2.uop.sync.message.org.OrgCategoryChangeMessage;
import com.chinacreator.c2.uop.sync.message.org.OrgCreateMessage;
import com.chinacreator.c2.uop.sync.message.org.OrgDeleteMessage;
import com.chinacreator.c2.uop.sync.message.org.OrgUpdateMessage;
import com.chinacreator.c2.uop.sync.message.user.UserCreateMessage;
import com.chinacreator.c2.uop.sync.message.user.UserDeleteMessage;
import com.chinacreator.c2.uop.sync.message.user.UserOrgChangeMessage;
import com.chinacreator.c2.uop.sync.message.user.UserUpdateMessage;
import com.chinacreator.c2.uop.sync.receiver.UopModifyMessageListener;

/**
 * 接收uop新增用户消息
 * @author 01526
 *
 */
public class AddUserMessageReceiver implements UopModifyMessageListener {

	@Override
	public void onOrgCategoryChanged(OrgCategoryChangeMessage arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onOrgCreated(OrgCreateMessage arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onOrgDeleted(OrgDeleteMessage arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onOrgUpdated(OrgUpdateMessage arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUserCreated(UserCreateMessage arg0) {

	}

	@Override
	public void onUserDeleted(UserDeleteMessage arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUserOrgChanged(UserOrgChangeMessage arg0) {
		if(ConfigManager.getInstance().getConfig("activeMQ_open").equals("true")){
			try {
				if(arg0.getCategory().equals(ConfigManager.getInstance().getConfig("c2_sso_categoryId"))&&arg0.getType().name().equals("ADD")){
					Map<String,Object> user = arg0.getUser();
					if(user.get("email")!=null && user.get("email").toString().contains("@chinacreator.com")){
					    //用户信息必须包含公司邮箱 
						String userName = user.get("email").toString().substring(0, user.get("email").toString().indexOf("@"));
						Map<String,Object> userInfo = existsByUserName(userName);
						if(userInfo==null){
							Map<String,Object> map = new HashMap<String,Object>();
							map.put("userName", userName);
							map.put("userId", user.get("id"));
							map.put("userRealName", user.get("name"));
							map.put("userPinyin", Pinyin4jUtil.getPinYin(user.get("name").toString()));
							map.put("remark3", Pinyin4jUtil.getPinYinHeadChar(user.get("name").toString()));
							map.put("userIsvalid", 2);
							if(user.get("sex").equals("2")){
								map.put("userSex", "F");
							}else if(user.get("sex").equals("1")){
								map.put("userSex", "M");
							}else{
								map.put("userSex", "-1");
							}
							map.put("userHometel",user.get("phone"));
							map.put("userWorktel", user.get("phone"));
							map.put("userMobiletel1", user.get("phone"));
							map.put("userMobiletel2", user.get("phone"));
							map.put("userEmail", user.get("email"));
							if(user.get("sex")!=null && user.get("sex").equals("2")){
								map.put("remark1", "assets/img/profile-pics/" + (new Random().nextInt(4) + 5) + ".jpg");
							}else{
								map.put("remark1", "assets/img/profile-pics/" + (new Random().nextInt(4) + 1) + ".jpg");
							}
							insertUser(map);
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onUserUpdated(UserUpdateMessage arg0) {
		// TODO Auto-generated method stub
		
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
