package com.chinacreator.c2.project.label;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.Assert;

import com.chinacreator.c2.dao.Dao;
import com.chinacreator.c2.dao.DaoFactory;
import com.chinacreator.c2.dao.mybatis.enhance.RowBounds4Page;
import com.chinacreator.c2.project.issue.IssueLabel;
import com.chinacreator.c2.project.story.StoryLabel;

public class LabelService {
	
	/**
	 * 添加
	 * @param label
	 * @return
	 */
	public Label addLabel(Label label){
		
		Assert.notNull(label.getProject(), "参数不正确");
		Assert.hasText(label.getName(), "参数不正确");
		Assert.hasText(label.getColor(),"参数不正确");
		
		Dao dao = DaoFactory.create(Label.class);

		dao.insert(label);
		return label;
	}
	
	
	/**
	 * 修改
	 * @param label
	 * @return
	 */
	public boolean deleteLabel(Label label){
		
		Assert.notNull(label.getId(), "参数不正确");
		
		//删除需求标签关联
		Dao<StoryLabel> storyLabelDao = DaoFactory.create(StoryLabel.class);
		StoryLabel sl=new StoryLabel();
		sl.setLabelId(label.getId());
		storyLabelDao.getSession().delete("com.chinacreator.c2.project.story.StoryLabelMapper.deleteByLabelId",sl);
		
		
		//删除任务与标签关联
		Dao<LabelTask> labelTaskDao = DaoFactory.create(LabelTask.class);
		LabelTask lt=new LabelTask();
		lt.setLabelId(label.getId());
		labelTaskDao.getSession().delete("com.chinacreator.c2.project.label.LabelTaskMapper.deleteByLabelId",lt);
		
		//删除label本身
		Dao labeDao = DaoFactory.create(Label.class);
		labeDao.delete(label);
		return true;
	}
	
	
	/**
	 * 删除
	 * @param label
	 * @return
	 */
	public boolean updateLabel(Label label){
		
		Assert.notNull(label.getId(), "参数不正确");
		
		Dao dao = DaoFactory.create(Label.class);
		
		if(StringUtils.isEmpty(label.getName())){
			throw new RuntimeException("参数不正确");
		}
		
		if(StringUtils.isEmpty(label.getColor())){
			throw new RuntimeException("参数不正确");
		}
	
		Label dbLabel=(Label)dao.selectByID(label.getId());
		dbLabel.setName(label.getName());
		dbLabel.setColor(label.getColor());
		dao.update(dbLabel);
		return true;
	}
	

	
	/**
	 * 标签查询服务
	 * @param condition
	 * @return
	 */
	public List<Object> searchLabelList(Map<String, Object> condition) {
		
		Label label=new Label();
		if(null!=condition&&null!=condition.get("name")){
			label.setName((String)condition.get("name"));
		}
		
		Dao dao = DaoFactory.create(Label.class);
		return dao.select(label);
		
	}
	
	/**
	 * 分页获取问题列表
	 * 
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 */
	public Map<String, Object> getLabelListForPage(int pageIndex, int pageSize,
			Map<String, Object> condition) {
		
		if(pageIndex<=0) pageIndex=1;

		Map<String, Object> reData = new HashMap<String, Object>();
		
		if(null==condition.get("product")||"".equals(condition.get("product").toString())){
			reData.put("rows",new ArrayList());
			reData.put("totalCount",0);
			return reData;
		}
		
		int product=Integer.parseInt(condition.get("product").toString());
		
		Dao dao = DaoFactory.create(Label.class);
		
		Label label=new Label();
		label.setName(null==condition?null:(String)condition.get("name"));
		label.setProject(product);
		
		RowBounds4Page rowBounds = new RowBounds4Page((pageIndex - 1)
				* pageSize, pageSize, null);
		List<Object> reList = dao
				.getSession()
				.selectList(
						"com.chinacreator.c2.project.label.LabelMapper.selectByPage",
						label,rowBounds);
		
		reData.put("rows", reList);
		
		int totalCount=dao.count(label);
		reData.put("totalCount",totalCount);
		
		return reData;
	}


	public Object getLabelsByProductId(int product) {
		Dao dao = DaoFactory.create(Label.class);
		Label label=new Label();
		label.setProject(product);
		return dao.select(label);
	}
	
	public int importLabel(List<Label> labels){
		Dao<Label> dao = DaoFactory.create(Label.class);
		int i = dao.insertBatch(labels);
		return i;
	}
	
	public int sortLabels(List<Label> labels){
		Dao<Label> dao = DaoFactory.create(Label.class);
		int i = dao.updateBatch(labels);
		return i;
	}
	
}
