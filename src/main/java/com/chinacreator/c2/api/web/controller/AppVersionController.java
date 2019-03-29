package com.chinacreator.c2.api.web.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.chinacreator.c2.app.APPVersion;
import com.chinacreator.c2.dao.DaoFactory;
import com.chinacreator.c2.web.controller.ResponseFactory;

@Controller	
@RequestMapping(value = "/app")
public class AppVersionController {
	
	private static ResponseFactory factory = new ResponseFactory();
  
    @RequestMapping(value = "/version",method = RequestMethod.GET)
    public Object doGetAppVersionInfo() {
    	List<APPVersion> versionInfo = DaoFactory.create(APPVersion.class).selectAll();
    	Map<String,Object> result = new HashMap<String,Object>() ;
    	if(versionInfo.size()>0){
    		APPVersion version = versionInfo.get(0);
    		result.put("version", version.getVersion()) ;
    		result.put("name", version.getName()) ;
    		Map<String,Object> ios = new HashMap<String,Object>() ;
    		ios.put("version", version.getIosVersion()) ;
    		ios.put("minVersion", version.getIosMinVersion()) ;
    		ios.put("build", version.getIosBuild()) ;
    		ios.put("scriptVersion", version.getIosScriptVersion()) ;
    		ios.put("url", version.getIosUrl()) ;
    		Map<String,Object> android = new HashMap<String,Object>() ;
    		android.put("version", version.getAndroidVersion()) ;
    		android.put("minVersion", version.getAndroidMinVersion()) ;
    		android.put("build", version.getAndroidBuild()) ;
    		android.put("scriptVersion", version.getAndroidScriptVersion()) ;
    		android.put("url", version.getAndroidUrl()) ;
    		result.put("ios", ios) ;
    		result.put("android", android) ;
    	}
    	
        return factory.createResponseBodyJSONObject(result) ; 
    }

}
