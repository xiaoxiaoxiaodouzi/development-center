package com.chinacreator.c2.project.log;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import com.chinacreator.c2.annotation.Column;
import com.chinacreator.c2.dao.Dao;
import com.chinacreator.c2.dao.DaoFactory;
import com.chinacreator.c2.dao.DaoService;
import com.chinacreator.c2.dao.reflection.EntityReflector;
import com.chinacreator.c2.dao.reflection.invoker.Invoker;
import com.chinacreator.c2.ioc.ApplicationContextManager;
import com.chinacreator.c2.project.task.Action;
import com.chinacreator.c2.project.task.History;
import com.chinacreator.c2.runtime.util.PrimitiveUtil;

/**
 * 操作日志工具类
 * 
 * @author tyf
 * 
 */
public class ActionLogUtil {

	private static Logger LOGGER = LoggerFactory.getLogger(ActionLogUtil.class);

	private static ActionLogUtil INSTANCE;

	public static ActionLogUtil getINSTANCE() {
		if (INSTANCE == null) {
			INSTANCE = ApplicationContextManager.getContext().getBean(
					ActionLogUtil.class);
		}
		return INSTANCE;
	}

	/**
	 * 比较新对象中不为空的属性,并将操作{@link Action}及比较详情{@link History}持久化<br>
	 * 适合属性(状态)变换场景(不比较空值)<br>
	 * 
	 * @param oldEntityObj 旧实体对象
	 * @param newEntityObj 新实体对象
	 * @param action 操作对象
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException 
	 */
	@Transactional(rollbackFor = Exception.class)
	public void Compare2Insert(Object oldEntityObj, Object newEntityObj, Action action) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		//TODO: 既然入参是Object类型,集合肯定也是要支持一下的
		//TODO: 新建线程执行日志处理
		List<History> list = new ArrayList<History>();
		// 若新对象某个属性不为空，则拿旧对象的该属性值作比对，不同则记录属性变更历史记录
		for(Invoker invoker : EntityReflector.forClass(oldEntityObj.getClass()).getColumnFieldInvokers()){
			//子对象不处理
			Column anno = (Column) invoker.getAnnotation() ;
			if(anno.association())
				continue ;
			if(invoker.getField().getName().equals("stageId"))
			  continue ;
			
			Object newVal = invoker.getObj(newEntityObj) ;
			Object oldVal = invoker.getObj(oldEntityObj) ;
			if (newVal != null) {
				History his = new History();
				// 若旧对象属性为空,则记录为null
				if (oldVal == null) {
					his.setOldVal(getNullValue(invoker.getType()));
				} else if (oldVal.toString().equals(newVal.toString())) {
					continue;
				} else {
					his.setOldVal(oldVal.toString());
				}
				his.setActionId(action.getId());
				his.setField(anno.id());
				his.setNewVal(newVal.toString());
				his.setDiff("");
				list.add(his);
			}
		}
		
		if (list.size() != 0){
			action.setHistory(list) ;
			new DaoService().insertAllCascade(action) ;
		}
	}

	private String getNullValue(Class<?> clazz) {
		/*if (!clazz.isPrimitive()) {
			if (clazz.isAssignableFrom(Date.class)) {
				return "0000-00-00";
			}
		}*/
		return "";
	}

	/**
	 * 比较新旧对象的所有属性(包括空值),并将操作{@link Action}及比较详情{@link History}持久化<br>
	 * 适合比较所有属性(包括空值)的场景<br>
	 * 
	 * @param oldObj
	 * @param newObj
	 * @param action
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException 
	 */
	@Transactional(rollbackFor = Exception.class)
	public void CompareAll2Insert(Object oldEntityObj, Object newEntityObj, Action action)throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		//TODO: 既然入参是Object类型,集合肯定也是要支持一下的
		
		List<History> list = new ArrayList<History>();
		
		for(Invoker invoker : EntityReflector.forClass(oldEntityObj.getClass()).getColumnFieldInvokers()){
			//子对象不处理
			Column anno = (Column) invoker.getAnnotation() ;
			if(anno.association())
				continue ;
			
			Object newVal = invoker.getObj(newEntityObj) ;
			Object oldVal = invoker.getObj(oldEntityObj) ;
			History his = new History();
			
			// 全为空时不记录
			if (isNull(oldVal) && isNull(newVal))
				continue;
			// 有一个为空
			if (oldVal == null || newVal == null) {
				his.setOldVal(oldVal != null ? oldVal.toString() : getNullValue(invoker.getType()));
				his.setNewVal(newVal != null ? newVal.toString() : getNullValue(invoker.getType()));
			} else if (oldVal.toString().equals(newVal.toString())) {
				// 全不为空,且相等
				continue;
			} else {
				// 全不为空,且不相等
				his.setOldVal(oldVal.toString());
				his.setNewVal(newVal.toString());
			}
			his.setActionId(action.getId());
			his.setField(anno.id());
			his.setDiff("");
			list.add(his);
		}
		if (list.size() != 0){
			action.setHistory(list) ;
			new DaoService().insertAllCascade(action) ;
		}
	}
	
	/**
	 * 比较新旧对象的所有属性(包括空值),并将操作{@link Action}及比较详情{@link History}持久化<br>
	 * 适合比较所有属性(包括空值)的场景<br>
	 * 
	 * @param oldObj
	 * @param newObj
	 * @param action
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException 
	 */
	public List<History> getCompareAll(Object oldEntityObj, Object newEntityObj, Action action) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		//TODO: 既然入参是Object类型,集合肯定也是要支持一下的
		
		List<History> list = new ArrayList<History>();
		
		for(Invoker invoker : EntityReflector.forClass(oldEntityObj.getClass()).getColumnFieldInvokers()){
			//子对象不处理
			Column anno = (Column) invoker.getAnnotation() ;
			if(anno.association())
				continue ;
			
			Object newVal = invoker.getObj(newEntityObj) ;
			Object oldVal = invoker.getObj(oldEntityObj) ;
			History his = new History();
			
			// 全为空时不记录
			if (isNull(oldVal) && isNull(newVal))
				continue;
			// 有一个为空
			if (oldVal == null || newVal == null) {
				his.setOldVal(oldVal != null ? oldVal.toString() : getNullValue(invoker.getType()));
				his.setNewVal(newVal != null ? newVal.toString() : getNullValue(invoker.getType()));
			} else if (oldVal.toString().equals(newVal.toString())) {
				// 全不为空,且相等
				continue;
			} else {
				// 全不为空,且不相等
				his.setOldVal(oldVal.toString());
				his.setNewVal(newVal.toString());
			}
			his.setActionId(action.getId());
			his.setField(anno.id());
			his.setDiff("");
			list.add(his);
		}
		return list;
	}

	private boolean isNull(Object val) {
		if (val instanceof String) {
			return StringUtils.isBlank((String) val) ;
		}
		return val==null;
	}

	/**
	 * 插入action
	 * 
	 * @param action
	 * @return
	 */
	public boolean insert(Action action) {
		try {
			Dao<Action> actionDao = DaoFactory.create(Action.class);
			actionDao.insert(action);
			return true;
		} catch (Exception e) {
			LOGGER.warn("Action录入失败...类型:"+action.getTargetType()+"," +
					"ID:"+action.getTargetId()+"" +
					",操作人"+action.getActor()+"," +
					"异常信息为:"+e.getMessage()+"");
			return false;
		}
	}

}
