package com.chinacreator.c2.project.log;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.chinacreator.c2.annotation.Entity;
import com.chinacreator.c2.project.cache.DBCommentCache;
import com.chinacreator.c2.project.task.Action;
import com.chinacreator.c2.project.task.History;
import com.google.common.base.Preconditions;

/**
 *  操作历史记录解析器
 * 
 * @author tyf
 *
 */
public class HistoryRecordResolver {
	
	private Map<String,String> hisFieldMapper ;

	private Action action ;
	
	private String actionType ;
	
	private static Map<String,ActionTypes> actionTypeCache ;
	
	public HistoryRecordResolver(Action action,Class<?> entityClazz) {
		Preconditions.checkArgument(entityClazz!=null, "HistoryRecordResolver初始化失败.entityClazz不能为空!") ;
		Preconditions.checkArgument(entityClazz.getAnnotation(Entity.class)!=null, "HistoryCommentResolver初始化失败.entityClazz必须为实体类!") ;
		Preconditions.checkArgument(action!=null, "HistoryRecordResolver初始化失败.action不能为空!") ;
		if(actionTypeCache==null){
			actionTypeCache = new HashMap<String,ActionTypes>() ;
			for(ActionTypes type:ActionTypes.values()){
				actionTypeCache.put(type.getColVal(), type) ;
			}
		}
		this.action = action ;
		this.actionType = action.getType() ;
		this.hisFieldMapper = DBCommentCache.get(entityClazz.getAnnotation(Entity.class).table()) ;
	}
	
	/**
	 *  解析action.historys,将属性改变历史记录转换成文字记录
	 * 
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public Map<String,Object> resolve() {
		Map<String,Object> hisRecord = new HashMap<String,Object>();
		//若是今年则不显示年份
		if(action.getDate().getYear()==new Timestamp(System.currentTimeMillis()).getYear()){
			hisRecord.put("date", new SimpleDateFormat("M月d日 ah:mm").format(action.getDate()));
		}else{
			hisRecord.put("date", new SimpleDateFormat("yy年M月d日 ah:mm").format(action.getDate()));
		}
		hisRecord.put("actor", action.getActor());
		hisRecord.put("detail", getActionDesc());
		hisRecord.put("hisActionCom", action.getComment());
		
		if(action.getHistory().size()!=0){
			hisRecord.put("fieldChangeHisArr", this.translateColumnName(action.getHistory())) ;
			hisRecord.put("showFieldChangeHis", false) ;
		}
		if(actionTypeCache.containsKey(actionType))
			hisRecord.put("cssName", actionTypeCache.get(actionType).getCssName()) ;
		return hisRecord ;
	}
	

	private String getActionDesc() {
		List<String> args = new ArrayList<String>() ;
		String suffixComment = "" ;
		if(actionType.equals(ActionTypes.TASK_RECORD_ESTIMATE.getColVal()) 
				|| actionType.equals(ActionTypes.TASK_DELETE_ESTIMATE.getColVal()) ){
			suffixComment = ",计 "+action.getExtra()+" 小时" ;
			args.add(action.getExtra1()==null?"":action.getExtra1()) ;
		}else if(actionType.equals(ActionTypes.TASK_EDIT.getColVal()) ){ 
			//任务详情编辑是即时保存所以理论上不存在多个history,除编辑剩余工时外
			History history = action.getHistory().iterator().next() ;
			suffixComment = hisFieldMapper.get(history.getField()) +"为 \""+history.getNewVal()+"\"";
		}
		args.add(suffixComment) ;
		if(actionTypeCache.containsKey(actionType)){
			return String.format(actionTypeCache.get(actionType).getComment(), args.toArray()) ;
		}
		return actionType;
	}
	
	private Collection<History> translateColumnName(Collection<History> collection){
		for(Object his:collection){
			History history = (History) his ;
			if(hisFieldMapper.containsKey(history.getField())){
				history.setField(hisFieldMapper.get(history.getField())) ;
			}else{
				history.setField(history.getField()) ;
			}
		}
		return collection ;
	}

}
