package com.chinacreator.c2.project.info;

import com.chinacreator.c2.dao.Dao;
import com.chinacreator.c2.dao.DaoFactory;

public class JobService {

	/**
	 * 根据岗位id查询岗位信息
	 * @param jobId
	 * @return
	 */
	public JobDTO queryByPK(String jobId) {
		Dao<JobDTO> dao = DaoFactory.create(JobDTO.class);
		return dao.selectByID(jobId);
	}

}
