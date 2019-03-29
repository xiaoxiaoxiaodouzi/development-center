package com.chinacreator.c2.project.message;

import org.springframework.util.Assert;

import com.alibaba.fastjson.JSONObject;
import com.chinacreator.c2.message.MessageValidator;
import com.chinacreator.c2.message.exception.MessageContentValidateException;

/**
 * 首页我的消息内容转换器
 * @author hushow
 *
 */
public class IndexMessageValidate implements MessageValidator {

	@Override
	public String getName() {
		return "pushSystem";
	}

	@Override
	public void validate(String content) throws MessageContentValidateException{
		
		try{
			
			UserMessageVo messageVo=JSONObject.parseObject(content,UserMessageVo.class);;
			
			//验证不能为空
			Assert.hasText(messageVo.getContent(),"content属性不能为空");
			Assert.hasText(messageVo.getFromUserId(),"fromUserId属性不能为空");
			Assert.hasText(messageVo.getFromUserRealname(),"fromUserRealname属性不能为空");
			//Assert.hasText(messageVo.getFromUserIcon(),"fromUserIcon属性不能为空");
		}catch(Exception e){
			e.printStackTrace();
			throw new MessageContentValidateException("IndexMsgFormat消息转换出现异常，请使用与UserMessageVo类实例或相符的json格式",e);
		}
	}

}
