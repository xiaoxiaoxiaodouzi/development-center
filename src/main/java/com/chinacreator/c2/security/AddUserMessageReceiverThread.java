package com.chinacreator.c2.security;

import javax.servlet.ServletContext;

import com.chinacreator.c2.config.ConfigManager;
import com.chinacreator.c2.security.handler.AddUserMessageReceiver;
import com.chinacreator.c2.uop.sync.receiver.UopMQMessageReceiverServiceImpl;
import com.chinacreator.c2.web.init.ServerStartup;

/**
 * 接收用户新增消息线程
 * @author 01526
 *
 */
public class AddUserMessageReceiverThread implements ServerStartup {

	private UopMQMessageReceiverServiceImpl receiver;
	
	@Override
	public void close() {
		receiver.stop();
	}

	@Override
	public void startup(ServletContext arg0) {
		if( receiver !=null){
			receiver.stop();
		}
		
		String url = ConfigManager.getInstance().getConfig("activeMQ_url");
		String userName = ConfigManager.getInstance().getConfig("activeMQ_userName");
		String pwd = ConfigManager.getInstance().getConfig("activeMQ_pwd");
		
		receiver = new UopMQMessageReceiverServiceImpl(userName, pwd, url);
		receiver.addListener(new AddUserMessageReceiver());
		
		receiver.start();
	}

	
}
