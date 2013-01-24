package com.hntoplinks.controller;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.map.HashedMap;


import play.mvc.Controller;
import play.mvc.Http.Cookie;
import play.mvc.Http.Header;

public class HnController extends Controller{
	private static Map<String, UAgentInfo> agentMap = new HashMap<String, UAgentInfo>();
	
	protected static void render(String template, Object... args){
		String clientTemplate = template;
		Header userAgent = request.headers.get("user-agent");
		Header httpAccept = request.headers.get("accept");
		if(userAgent != null && httpAccept != null){
			String userAgentStr = userAgent.value();
			String httpAcceptStr = httpAccept.value();
			String headerStr = userAgentStr.concat(httpAcceptStr);
			UAgentInfo uAgentInfo  = agentMap.get(headerStr);
			if(uAgentInfo == null){
				uAgentInfo = new UAgentInfo(userAgentStr, httpAcceptStr);
				agentMap.put(headerStr, uAgentInfo);
			}
			
			if(uAgentInfo.detectSmartphone()){
				clientTemplate = templatName(template, ClientType.MOBILE);
				if(!templateExists(clientTemplate)){
					clientTemplate = template;
				}
			}
		}
		Controller.renderTemplate(clientTemplate, args);
	}
	
	private static String templatName(String baseName, ClientType type){
		String templateDirectory = baseName.substring(0, baseName.lastIndexOf("/"));
		String[] templateArr = baseName.substring(baseName.lastIndexOf("/")).split("\\.");
		return templateDirectory + templateArr[0] + "_" + type.toString().toLowerCase() + "." + templateArr[1];
	}
}
