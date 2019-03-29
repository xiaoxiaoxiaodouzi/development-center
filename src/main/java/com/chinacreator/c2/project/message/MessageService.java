package com.chinacreator.c2.project.message;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.time.DateUtils;

import com.chinacreator.c2.context.OperationContextHolder;
import com.chinacreator.c2.context.WebOperationContext;
import com.chinacreator.c2.dao.Dao;
import com.chinacreator.c2.dao.DaoFactory;
import com.chinacreator.c2.dao.mybatis.enhance.Page;
import com.chinacreator.c2.message.Constants;
import com.chinacreator.c2.message.MessageRequest;
import com.chinacreator.c2.message.MessageRequestBuilder;
import com.chinacreator.c2.message.MessageSender;
import com.chinacreator.c2.message.exception.InvalidMessageRequestException;
import com.chinacreator.c2.project.info.MembersMgt;
import com.chinacreator.c2.uop.OrgUserService;
import com.chinacreator.c2.uop.User;
import com.chinacreator.c2.web.controller.ResponseFactory;

public class MessageService {
	
	/**
	 * 分页获取消息列表
	 * @param pageIndex
	 * @param pageSize
	 * @param condition
	 * @return
	 */
	public Map<String,Object> getMessageListForPage(int pageIndex, int pageSize,Map<String, Object> condition) {
		
//		WebOperationContext context = (WebOperationContext)OperationContextHolder.getContext();
//		String currentUserId=context.getUser().getId();
//		
//		Dao<UserMessage> userMessageDao = DaoFactory.create(UserMessage.class);
//		
//		if(null==condition){
//			condition=new HashMap<String, Object>();
//		}
//		condition.put("msgTo",currentUserId);
//		//condition.put("category","pushSystem");
//		
////		Sortable sortable=new Sortable(new Order("arrival_time",Order.DIRECTION_DESC));
//		Pageable pageable=new Pageable(pageIndex,pageSize);
////		Conditions conditions=new Conditions();
////		conditions.append(new Rule("msgTo","eq",currentUserId));
////		conditions.setGroupOp("AND");
//		
//		RowBounds4Page rowBounds = new RowBounds4Page(pageable,null,null,true);
//		List<Map<String,Object>> userMessageList=userMessageDao.getSession().selectList("selectMessageList",condition,rowBounds);
//		
//		//解析格式
//		for (Map<String, Object> map : userMessageList) {
//			
//			String content=(String)map.get("content");
//			try{
//				
//				//验证格式
//				UserMessageVo messageVo=JSONObject.parseObject(content,UserMessageVo.class);
//				Assert.hasText(messageVo.getContent(),"content属性不能为空");
//				Assert.hasText(messageVo.getFromUserId(),"fromUserId属性不能为空");
//				Assert.hasText(messageVo.getFromUserRealname(),"fromUserRealname属性不能为空");
//				map.put("messageVo",messageVo);
//			}catch(Exception e){}
//			
//		}
//		Map<String,Object> result=new HashMap<String, Object>();
//		result.put("rows",userMessageList);
//		result.put("totalCount",rowBounds.getTotalSize());
//		return result;
//		
		Map<String,Object> result=new HashMap<String, Object>();
		result.put("rows",java.util.Collections.EMPTY_LIST);
		result.put("totalCount",0);
		return result;
	}
	
	
	/**
	 * 查询发送给当前用户的user信息
	 * @param msgFrom
	 * @param condition
	 * @return
	 */
	public List<Map<String,Object>> getAllMsgFromUserList(Map<String, Object> condition) {
		
//		WebOperationContext context = (WebOperationContext)OperationContextHolder.getContext();
//		String currentUserId=context.getUser().getId();
//		
//		if(null==condition){
//			condition=new HashMap<String, Object>();
//		}
//		condition.put("msgTo",currentUserId);
//		condition.put("category","pushSystem");
//		
//		Dao<UserMessage> userMessageDao = DaoFactory.create(UserMessage.class);
//		List<Map<String,Object>> msgFromUserList=userMessageDao.getSession().selectList("selectMsgFromUser",condition);
//		
//		Map<String,Map<String,Object>> repeatMap=new LinkedHashMap<String, Map<String,Object>>();
//		for (Map<String, Object> map : msgFromUserList) {
//			String userId=(String)map.get("user_id");
//			if("$system".equals(userId)){
//				map.put("user_name","系统");
//				map.put("user_realname","系统");
//				map.put("icon_url","assets/img/profile-pics/0.jpg");
//				map.put("user_id","$system");
//			}
//			repeatMap.put(userId,map);
//		}
//		
//		condition.put("msgFrom",currentUserId);
//		condition.put("category","pushSystem");
//		List<Map<String,Object>> msgFromToList=userMessageDao.getSession().selectList("selectMsgToUser",condition);
//		for (Map<String, Object> map : msgFromToList) {
//			String userId=(String)map.get("user_id");
//			if("$system".equals(userId)){
//				map.put("user_name","系统");
//				map.put("user_realname","系统");
//				map.put("icon_url","assets/img/profile-pics/0.jpg");
//				map.put("user_id","$system");
//			}
//			repeatMap.put(userId,map);
//		}
//		return new ArrayList<Map<String,Object>>(repeatMap.values());
		
		return java.util.Collections.EMPTY_LIST;
	}
	
	
	/**
	 * 查询发送给当前用户的user信息
	 * @param msgFrom
	 * @param condition
	 * @return
	 */
	public Map<String,Object>  getFromUserMsgDetail(String msgFromUser,Map<String, Object> condition) {
		
//		Map<String,Object> fromUserMsgDetail=new HashMap<String, Object>();
//		
//		WebOperationContext context = (WebOperationContext)OperationContextHolder.getContext();
//		String currentUserId=context.getUser().getId();
//		
//		if(null==condition){
//			condition=new HashMap<String, Object>();
//		}
//		condition.put("msgTo",currentUserId);
//		condition.put("msgFrom",msgFromUser);
//		condition.put("category","pushSystem");
//		
//		Dao<UserMessage> userMessageDao = DaoFactory.create(UserMessage.class);
//		List<Map<String,Object>> msgFromUserList=userMessageDao.getSession().selectList("selectBothMsgListByFromTo",condition);
//		for (Map<String, Object> map : msgFromUserList) {
//			
//			String content=(String)map.get("content");
//			String msgTo=(String)map.get("msg_to");
//			
//			try{
//				
//				//验证格式
//				UserMessageVo messageVo=JSONObject.parseObject(content,UserMessageVo.class);
//				Assert.hasText(messageVo.getContent(),"content属性不能为空");
//				Assert.hasText(messageVo.getFromUserId(),"fromUserId属性不能为空");
//				Assert.hasText(messageVo.getFromUserRealname(),"fromUserRealname属性不能为空");
//				map.put("messageVo",messageVo);
//			}catch(Exception e){}
//			
//			if("$system".equals(map.get("msg_from").toString())){
//				map.put("user_name","system");
//				map.put("user_realname","系统");
//				map.put("icon_url","assets/img/profile-pics/xt.jpg");
//			}
//			
//			//修改自己的消息已读
//			if(currentUserId.equals(msgTo)){
//				UserMessage um=new UserMessage();
//				um.setOpened(true);
//				um.setOpenTime(new Timestamp(System.currentTimeMillis()));
//				um.setId(map.get("um_id").toString());
//				userMessageDao.update(um);
//			}
//		}
//		
//		fromUserMsgDetail.put("rows",msgFromUserList);
//		fromUserMsgDetail.put("totalCount",msgFromUserList.size());
//		fromUserMsgDetail.put("msgFrom",this.getUserInfo(msgFromUser));
//		return fromUserMsgDetail;
		
		return java.util.Collections.EMPTY_MAP;
	}
	
	
	/**
	 * 修改消息已读
	 * @param msgId
	 */
	public void updateMsgOpened(String umId){
//		Dao<UserMessage> userMessageDao = DaoFactory.create(UserMessage.class);
//		UserMessage um=new UserMessage();
//		um.setId(umId);
//		um.setOpened(true);
//		userMessageDao.update(um);
	}
	
	
	/**
	 * 获取广播消息
	 * @param msgFromUser
	 * @param condition
	 * @return
	 */
	public Map<String,Object> getBroadcastMsgDetail(Map<String, Object> condition) {
		
//		WebOperationContext context = (WebOperationContext)OperationContextHolder.getContext();
//		String currentUserId=context.getUser().getId();
//		
//		Map<String,Object> fromUserMsgDetail=new HashMap<String, Object>();
//		
//		if(null==condition){
//			condition=new HashMap<String, Object>();
//		}
//		
//		condition.put("msgTo",currentUserId);//查询是否已读状态
//		condition.put("category","pushBroadcast");
//		
//		List<UserMessage> openedList=new ArrayList<UserMessage>();
//		Dao<UserMessage> userMessageDao = DaoFactory.create(UserMessage.class);
//		
//		List<Map<String,Object>> msgFromUserList=userMessageDao.getSession().selectList("selectBroadcastBothMsgListByFromTo",condition);
//		for (Map<String, Object> map : msgFromUserList) {
//			
//			String content=(String)map.get("content");
//			String id=(String)map.get("id_");
//			String msgFrom=(String)map.get("msg_from");
//			String opened=(String)map.get("opened");
//			
//			try{
//				//验证格式
//				UserMessageVo messageVo=JSONObject.parseObject(content,UserMessageVo.class);
//				Assert.hasText(messageVo.getContent(),"content属性不能为空");
//				Assert.hasText(messageVo.getFromUserId(),"fromUserId属性不能为空");
//				Assert.hasText(messageVo.getFromUserRealname(),"fromUserRealname属性不能为空");
//				//Assert.hasText(messageVo.getFromUserIcon(),"fromUserIcon属性不能为空");
//				map.put("messageVo",messageVo);
//			}catch(Exception e){}
//			
//			//标记此广播消息为已读
//			if(null==opened||"0".equals(opened)){
//				UserMessage searchUm=new UserMessage();
//				searchUm.setMessageId(id);
//				searchUm.setMsgTo(currentUserId);
//				searchUm.setOpened(true);
//				searchUm.setOpenTime(new Timestamp(System.currentTimeMillis()));
//				searchUm.setMsgFrom(msgFrom);
//				openedList.add(searchUm);
//			}
//		}
//		
//		userMessageDao.insertBatch(openedList);
//		
//		fromUserMsgDetail.put("rows",msgFromUserList);
//		fromUserMsgDetail.put("totalCount",msgFromUserList.size());
//
//		return fromUserMsgDetail;
		
		return java.util.Collections.EMPTY_MAP;

	}
	
	
	/**
	 * 推送广播消息
	 * @param msgContent   内容
	 * @param link         打开链接
	 * @param isPersistent 是否持久化
	 * @param expireSecond 过期秒数
	 * @return
	 */
	public Object pushBroadcast(String msgContent,String link,Boolean isPersistent,Integer expireSecond){
		
		try{
			
			if(null==isPersistent){
				isPersistent=true;
			}
			
			Date expireDate=null;
			if(null==expireSecond){
				expireDate=DateUtils.addDays(new Date(),Constants.MSG_EXPIRE_DAYS);
			}else{
				expireDate=DateUtils.addSeconds(new Date(),expireSecond);
			}
			
    		Map<String, Object> response=new HashMap<String, Object>();
    		String ownerName=((WebOperationContext)OperationContextHolder.getContext()).getUser().getName() ;
    		String msgfrom = new MembersMgt().getUserInfoByUsername(ownerName).get("userId") ;
    		
    		Map<String,Object> userDto = queryUserById(msgfrom);
    		
    		//组装消息对象
    		UserMessageVo umv=new UserMessageVo();
    		umv.setContent(msgContent);
    		umv.setFromUserIcon(userDto.get("remark1").toString());
    		umv.setFromUserId(msgfrom);
    		umv.setFromUserRealname(userDto.get("user_realname").toString());
    		umv.setLink(link);
    		
    		MessageRequest messageRequest=new MessageRequestBuilder().expire(expireDate)
    				.from(msgfrom)
    				.to(Constants.TO_ALL)
    				.persistent(isPersistent)
    				.category("pushBroadcast")
    				.content(umv)
    				.build();
    		MessageSender.getInstance().send(messageRequest);
    		
    		response.put("status",true);
    		response.put("text","消息发送成功");
    		return new ResponseFactory().createResponseBodyJSONObject(response);
    	}catch(Exception e){
    		throw new InvalidMessageRequestException("消息发送请求执行失败："+e.getMessage());
        }
		
	}
	
	
	/**
	 * 推送系统消息
	 * @param msgTo        接收者
	 * @param msgContent   内容
	 * @param link         打开链接
	 * @param isPersistent 是否持久化
	 * @param expireSecond 过期秒数
	 * @return
	 */
	public Object pushSystem(String msgTo,String msgContent,String msgTitle,String link,Boolean isPersistent,Integer expireSecond){
		
		try{
			
    		Map<String,Object> userDto = queryUserById(msgTo);
    		if(null==userDto||userDto.get("user_id")==null||userDto.get("user_id").equals("")){
    			throw new InvalidMessageRequestException("不存在此接收人，消息发送失败：");
    		}
			
			if(null==isPersistent){
				isPersistent=true;
			}
			
			Date expireDate=null;
			if(null==expireSecond){
				expireDate=DateUtils.addDays(new Date(),Constants.MSG_EXPIRE_DAYS);
			}else{
				expireDate=DateUtils.addSeconds(new Date(),expireSecond);
			}
			
    		Map<String, Object> response=new HashMap<String, Object>();
    		String ownerName=((WebOperationContext)OperationContextHolder.getContext()).getUser().getName() ;
    		String msgfrom = new MembersMgt().getUserInfoByUsername(ownerName).get("userId") ;
    		
    		Map<String,Object> userFromDto = queryUserById(msgfrom);
    		
    		//组装消息对象
    		UserMessageVo umv=new UserMessageVo();
    		umv.setContent(msgContent);
    		umv.setFromUserIcon(userFromDto.get("remark1").toString());
    		umv.setFromUserId(msgfrom);
    		umv.setFromUserRealname(userFromDto.get("user_realname").toString());
    		umv.setLink(link);
    		umv.setTitle(msgTitle);
    		MessageRequest messageRequest=new MessageRequestBuilder().expire(expireDate).title(msgTitle).from(msgfrom).to(msgTo).persistent(isPersistent).category("pushSystem").content(umv).build();
    		MessageSender.getInstance().send(messageRequest);
    		
    		response.put("status",true);
    		response.put("text","消息发送成功");
    		return new ResponseFactory().createResponseBodyJSONObject(response);
    	}catch(Exception e){
    		throw new InvalidMessageRequestException("消息发送请求执行失败："+e.getMessage());
        }
	}
	
	
	/**
	 * 推送系统消息
	 * @param msgTo        接收者
	 * @param msgContent   内容
	 * @param link         打开链接
	 * @param isPersistent 是否持久化
	 * @param expireSecond 过期秒数
	 * @return
	 */
	public Object pushBackground(String msgTo,String msgContent,String link,Boolean isPersistent,Integer expireSecond){
		
		try{
			
			//验证用户是否存在
    		Map<String,Object> userDto = queryUserById(msgTo);
    		if(null==userDto||userDto.get("user_id")==null||userDto.get("user_id").equals("")){
    			throw new InvalidMessageRequestException("不存在此接收人，消息发送失败：");
    		}
			
			if(null==isPersistent){
				isPersistent=true;
			}
			
			Date expireDate=null;
			if(null==expireSecond){
				expireDate=DateUtils.addDays(new Date(),Constants.MSG_EXPIRE_DAYS);
			}else{
				expireDate=DateUtils.addSeconds(new Date(),expireSecond);
			}
			
			Map<String, Object> response=new HashMap<String, Object>();
    		String msgfrom = "$system";

    		//组装消息对象
    		UserMessageVo umv=new UserMessageVo();
    		umv.setContent(msgContent);
    		umv.setFromUserIcon("assets/img/profile-pics/xt.jpg");
    		umv.setFromUserId(msgfrom);
    		umv.setFromUserRealname("系统");
    		umv.setLink(link);
    		MessageRequest messageRequest=new MessageRequestBuilder().expire(expireDate).from(msgfrom).to(msgTo).persistent(isPersistent).category("pushSystem").content(umv).build();
    		MessageSender.getInstance().send(messageRequest);
    		
    		response.put("status",true);
    		response.put("text","消息发送成功");
    		
    		return new ResponseFactory().createResponseBodyJSONObject(response);
    	}catch(Exception e){
    		throw new InvalidMessageRequestException("消息发送请求执行失败："+e.getMessage());
        }
	}
	

	/**
	 * 推送刷新任务
	 * @return
	 */
	public Object pushLoadTask(String msgTo){
		
		try{
			
    		Map<String, Object> response=new HashMap<String, Object>();
    		String ownerName=((WebOperationContext)OperationContextHolder.getContext()).getUser().getName() ;
    		String msgfrom = new MembersMgt().getUserInfoByUsername(ownerName).get("userId") ;
    		
    		MessageRequest messageRequest=new MessageRequestBuilder().from(msgfrom).to(msgTo).persistent(false).category("pushTask").content("").build();
    		MessageSender.getInstance().send(messageRequest);
    		
    		response.put("status",true);
    		response.put("text","任务发送成功");
    		return new ResponseFactory().createResponseBodyJSONObject(response);
    	}catch(Exception e){
    		throw new InvalidMessageRequestException("任务发送请求执行失败："+e.getMessage());
        }
	}
	
	
	/**
	 * 分页获取用户
	 * @param userName
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 */
	public Page<Map<String,Object>> getUserListByPage(String userName,int pageIndex,int pageSize){
		Page<Map<String,Object>> page = OrgUserService.queryByName(userName,pageIndex,pageSize);
		return page;
	}
	
	
	/**
	 * 获取用户信息
	 * @param userId
	 * @return
	 */
	public Map<String,Object> getUserInfo(String userId){
		
		Map<String,Object> userMap=new HashMap<String, Object>();
		
		if("$system".equals(userId)){
			userMap.put("user_id","$system");
			userMap.put("user_name","系统");
			userMap.put("user_realname","系统");
			return userMap;
		}
		
		Map<String,Object> userDTO = queryUserById(userId);
		if(null==userDTO||null==userDTO.get("user_id")){
			throw new RuntimeException("不存在此用户【"+userId+"】");
		}
		
		
		userMap.put("icon_url",userDTO.get("remark1"));
		userMap.put("user_id",userDTO.get("user_id"));
		userMap.put("user_name",userDTO.get("user_name"));
		userMap.put("user_realname",userDTO.get("user_realname"));
		return userMap;
	}

	//根据用户id查询用户信息
	private Map<String, Object> queryUserById(String userId) {
		Dao<Object> dao = DaoFactory.create(Object.class);
		return dao.getSession().selectOne("com.chinacreator.c2.project.info.MemberMapper.queryUserById", userId);
	}
	
}