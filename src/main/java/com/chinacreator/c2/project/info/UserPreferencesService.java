package com.chinacreator.c2.project.info;

import java.util.List;

import com.chinacreator.c2.dao.Dao;
import com.chinacreator.c2.dao.DaoFactory;

public class UserPreferencesService {

	/**
	 * 根据用户id和偏好名称查询偏好信息
	 * @param userId
	 * @param name
	 * @return
	 */
	public UserPreferences queryByPK(String userId, String name) {
		Dao<UserPreferences> dao = DaoFactory.create(UserPreferences.class);
		UserPreferences preference = new UserPreferences();
		preference.setInfoName(name);
		preference.setUserId(userId);
		List<UserPreferences> list = dao.select(preference);
		return list.size()>0?list.get(0):null;
	}

	/**
	 * 插入偏好信息
	 * @param dto
	 */
	public void create(UserPreferences dto) {
		Dao<UserPreferences> dao = DaoFactory.create(UserPreferences.class);
		dao.insert(dto);
	}

	/**
	 * 更新偏好信息
	 * @param dto
	 */
	public void update(UserPreferences dto) {
		Dao<UserPreferences> dao = DaoFactory.create(UserPreferences.class);
		dao.update(dto);
	}

	/**
	 * 查询某用户的所有偏好信息
	 * @param preference
	 * @return
	 */
	public List<UserPreferences> queryByUserPreferences(UserPreferences preference) {
		Dao<UserPreferences> dao = DaoFactory.create(UserPreferences.class);
		return dao.select(preference);
	}

}
