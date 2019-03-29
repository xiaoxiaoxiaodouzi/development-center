package com.chinacreator.c2.web.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.chinacreator.c2.res.SerialNumberRegister;

/**
 * 负责操作上下文初始化和清理工作的拦截器，操作上下文初始化完成后会包含当前操作用户的信息和操作的输入参数 User: Vurt Date: 13-9-13
 * Time: 下午3:28
 */
public class BeautyURIInterceptor extends HandlerInterceptorAdapter {
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		String uri=request.getRequestURI();
		if(uri.startsWith("/")){
			uri=uri.substring(1);
		}
		if(SerialNumberRegister.getFormID(uri)!=null){
			request.getRequestDispatcher("/f/"+uri).forward(request, response);
			return false;
		}else{
			return true;
		}
	}
	
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		super.postHandle(request, response, handler, modelAndView);
	}

	@Override
	public void afterCompletion(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
	}
}
