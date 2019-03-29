/* global angular */
var developmentCenter = angular.module('developmentCenter', ['services','directives','angular-loading-bar','oc.lazyLoad','ngAnimate','ngSanitize','ui.sortable'])

developmentCenter.run(function($templateCache,$http) {
	$templateCache.put('modal.html',
	'<div class="modal" id="modalDefault" tabindex="-1" role="dialog" aria-hidden="false">'+
	'    <div class="modal-dialog" ng-class="{\'modal-sm\':modalOptions.size==\'small\',\'modal-lg\':modalOptions.size==\'big\'}">'+
	'        <div class="modal-content">'+
	'            <div class="modal-header {{modalOptions.headerColor}}">'+
	'            	<button type="button" class="close" ng-click="modalOptions.X()"><span aria-hidden="true">&times;</span></button>'+
	'                <h4 class="modal-title" ng-bind-html="modalOptions.title"></h4>'+
	'            </div>'+
	'            <div class="modal-body" ng-style="modalOptions.bodyStyle" ng-include="modalOptions.url"></div>'+
	'        </div>'+
	'    </div>'+
	'</div>');
	$templateCache.put('listviewTemplate',
	'<div class="listview lv-bordered lv-lg">'+
	'	<div class="lv-body">'+
    '	<div class="lv-item media"></div>'+
	'	</div>'+
	'</div>');
}).config(['$httpProvider','$controllerProvider',function($httpProvider,$controllerProvider){
	developmentCenter.registerCtrl = $controllerProvider.register;
	if (!$httpProvider.defaults.headers.get) {
	    $httpProvider.defaults.headers.get = {};
	}
	$httpProvider.defaults.headers.get['Pragma'] = 'no-cache';
	$httpProvider.defaults.headers.get['Cache-Control'] = 'no-cache';
    $httpProvider.defaults.headers.get['If-Modified-Since'] = 'Mon, 26 Jul 1997 05:00:00 GMT';

    $httpProvider.interceptors.push('ErrorResponseInterceptor');
    
    //if(c2.subject && c2.subject.accessToken){
		//$httpProvider.interceptors.push('OAuth2Interceptor');
    //}
}]);


developmentCenter.filter('howLongTime',['$filter',function($filter){
	return function(time,fmt){
		if(angular.isNumber(time)){
			var curTime = new Date().getTime();			
			var basicTime = 4*3600*1000;
			var diffTime = curTime - time;
			if(diffTime>0 && basicTime>diffTime){
				var hour = parseInt(diffTime/(60*60*1000));
				var minute = parseInt(diffTime/(60*1000))-hour*60;
				var result = "";
				if(hour>0){
					result += hour+"小时";
				}
				if(hour==0 && minute==0){
					result = "刚刚";
				}else if(hour>0 && minute==0){
					result += "之前";
				}else{
					result += minute+"分钟之前";
				}
				return result;
			}			
			return $filter("date")(time,fmt);
		}else{
			return $filter("date")(time,fmt);
		}
	};
}]);


developmentCenter.filter('fmtTextColor',['$filter',function($filter){
	return function(color){
		
		var colorObj=undefined;

		//解析r、g、b
		var r,g,b;
		if(color.indexOf("#")!=-1&&color.length==4){
			r=parseInt(color.substr(1,1),16);
			g=parseInt(color.substr(2,1),16);
			b=parseInt(color.substr(3,1),16);
		}else if(color.indexOf("#")!=-1&&color.length==7){
			r=parseInt(color.substr(1,2),16);
			g=parseInt(color.substr(3,2),16);
			b=parseInt(color.substr(5,2),16);
		}

		if(isNaN(r)||isNaN(g)||isNaN(b)){
			return "#100F0F";
		}else{
			colorObj={r:r,g:g,b:b};
		}

		//计算
		var grayLevel=colorObj.r*0.299+colorObj.g*0.587+colorObj.b*0.114;
		if (grayLevel>= 192){
			return "#100F0F"
		}
		return "#fff";
	};
}]);


var AjaxPrefilter = function(Messenger) {
	var _Messenger = Messenger;
	this.filter = function(options, originalOptions, jqXHR) {
		var success = options.success;
		var error = options.error;
		options.success = function(data, textStatus, jqXHR) {
			/**
			 * 转向到了登陆页，登陆提示
			 */
			if("c2login"==jqXHR.getResponseHeader("nologin")){
				var loginUrl=response.headers().loginurl;
				if(undefined==loginUrl||""==loginUrl){
					loginUrl="/login.jsp";
				}
				var loginHash=window.location.hash;
				if(loginUrl.substring(0,1)=="/"){
					window.location.href="."+loginUrl+"?backUrl="+loginHash;
				}else{
					window.location.href=loginUrl+"?backUrl="+loginHash;
				}
				return error(data, textStatus, jqXHR);
			}

			if ("true" == jqXHR.getResponseHeader("isshowmessage")) {
				_Messenger.post({
					'message' : decodeURIComponent(jqXHR.getResponseHeader("message")./*将编码后的加号处理成空格*/replace(/\+/g, '%20')),
					'type' : jqXHR.getResponseHeader("type"),
					'id' : jqXHR.status
				});
			}
			if ("error" == jqXHR.getResponseHeader("type")) {
				if (typeof (error) === "function")
					return error(data, textStatus, jqXHR);
			}
			// override success handling
			if (typeof (success) === "function")
				return success(data, textStatus, jqXHR);
		};

		options.error = function(jqXHR, textStatus, errorThrown) {

			/**
			 * 转向到了登陆页，登陆提示
			 */
			if("c2login"==jqXHR.getResponseHeader("nologin")){
				var loginUrl=response.headers().loginurl;
				if(undefined==loginUrl||""==loginUrl){
					loginUrl="/login.jsp";
				}
				var loginHash=window.location.hash;
				if(loginUrl.substring(0,1)=="/"){
					window.location.href="."+loginUrl+"?backUrl="+loginHash;
				}else{
					window.location.href=loginUrl+"?backUrl="+loginHash;
				}
				return error(data, textStatus, jqXHR);
			}

			if ("true" == jqXHR.getResponseHeader("isshowmessage")) {
				_Messenger.post({
					'message' : decodeURIComponent(jqXHR.getResponseHeader("message")./*将编码后的加号处理成空格*/replace(/\+/g, '%20')),
					'type' : jqXHR.getResponseHeader("type"),
					'id' : jqXHR.status
				});
			}
			// override error handling
			if (typeof (error) === "function")
				return error(jqXHR, textStatus, errorThrown);
		};
	};
};

// 定义响应拦截服务，所有的响应错误都在这里处理，包括响应状态码为200的业务错误
services.factory('ErrorResponseInterceptor', [ '$q', 'Messenger',
		function($q, Messenger) {
			// 还有一些请求是通过jquery的ajax发送的，要通过这种形式拦截
			var ajaxFilter = new AjaxPrefilter(Messenger);
			$.ajaxPrefilter(ajaxFilter.filter);
			return {
				// 处理正常响应，要处理响应头中标识为错误的情况(后台业务错误)
				'response' : function(response) {
					/**
					 * 转向到了登陆页，登陆提示
					 */
					if("c2login"==response.headers().nologin){
						var loginUrl=response.headers().loginurl;
						if(undefined==loginUrl||""==loginUrl){
							loginUrl="/login.jsp";
						}
						var loginHash=window.location.hash;
						if(loginUrl.substring(0,1)=="/"){
							window.location.href="."+loginUrl+"?backUrl="+loginHash;
						}else{
							window.location.href=loginUrl+"?backUrl="+loginHash;
						}
						return $q.reject(response);
					}

					if ("true" == response.headers().isshowmessage) {
						Messenger.post({
							'message' : decodeURIComponent(response.headers().message./*将编码后的加号处理成空格*/replace(/\+/g, '%20')),
							'type' : response.headers().type
						});
					}
					
					if(angular.isDefined(response.headers().c2redirect)){
						var redirectUrl=response.headers().c2redirect;
						
						if(redirectUrl.substring(0,1)=="/"){
							window.location.href="."+redirectUrl;
						}else{
							window.location.href=redirectUrl;
						}
						
						return $q.reject(response);
					}					
					
					if ("error" == response.headers().type) {
						return $q.reject(response);
					} else {
						return response;
					}
				},
				// 处理错误响应，既HTTP的状态码非2XX的响应
				'responseError' : function(response) {
					/**
					 * 转向到了登陆页，登陆提示
					 */
					if("c2login"==response.headers().nologin){
						var loginUrl=response.headers().loginurl;
						if(undefined==loginUrl||""==loginUrl){
							loginUrl="/login.jsp";
						}
						var loginHash=window.location.hash;
						if(loginUrl.substring(0,1)=="/"){
							window.location.href="."+loginUrl+"?backUrl="+loginHash;
						}else{
							window.location.href=loginUrl+"?backUrl="+loginHash;
						}
						return $q.reject(response);
					}

					var message = '服务器错误! 错误代码：' + response.status;

					if ("true" == response.headers().isshowmessage) {
						message = decodeURIComponent(response.headers().message./*将编码后的加号处理成空格*/replace(/\+/g, '%20'));
					} else {
						if ('404' == response.status) {
							message = '请求地址:' + response.config.url + '无效!';
						}
					}
					Messenger.post({
						'message' : message,
						'type' : 'error'
					});
					return $q.reject(response);
				}

			};
		} ]);
