package com.chinacreator.c2.web.filter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.filter.GenericFilterBean;

import com.chinacreator.c2.context.OperationContextHolder;
import com.chinacreator.c2.context.WebOperationContext;
import com.chinacreator.c2.dao.Dao;
import com.chinacreator.c2.dao.DaoFactory;
import com.chinacreator.c2.project.info.Member;
import com.chinacreator.c2.uop.OrgUserService;
import com.chinacreator.c2.uop.User;

public class ProjectUrlAuthenFilter extends GenericFilterBean {

	private static final String sfs_TEMPLATEURL_PROJECT = "/e/project/";

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
			throws IOException, ServletException {
		HttpServletRequest  httpReq  = (HttpServletRequest) request;  
        HttpServletResponse httpResp = (HttpServletResponse) response;  
        WebOperationContext context = (WebOperationContext)OperationContextHolder.getContext();
		User user = OrgUserService.getUserInfoById(context.getUser().getId());
		String currentUserName =user.getEmail().substring(0, user.getEmail().indexOf("@"));
		Map<String,Object> uu = OrgUserService.getUserByName(currentUserName);
		
		Pattern pattern = Pattern.compile("^[\\d]*$");  
		String url = httpReq.getRequestURI();
		if (null != url && !url.trim().equals("")) {
			int index = url.indexOf(sfs_TEMPLATEURL_PROJECT);
			if (index >= 0) {
				if (uu!=null && uu.get("user_id")!=null) {

					String project = url.substring(index + sfs_TEMPLATEURL_PROJECT.length());

					if (null != project && !project.trim().equals("")) {
						try {
						  if(pattern.matcher(project).matches()){//是否包含了项目id
						    int projectId = Integer.parseInt(project);
                            String userId = uu.get("user_id").toString();

                            Dao<Member> dao = DaoFactory.create(Member.class);
                            Map<String, Object> map = new HashMap<String, Object>();
                            map.put("projectId", projectId);
                            map.put("userId", userId);

                            int count = dao.getSession().selectOne(
                                    "com.chinacreator.c2.project.info.MemberMapper.existsByProjectAndUser", map);
                            if (count == 0) {
                                httpResp.addHeader("c2redirect", "/#/unauthorized");
                                return;
                            }
						  }
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
		filterChain.doFilter(request, response);
	}

}
