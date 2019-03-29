/*兼容ie，ie8和9在没有打开开发者工具的情况下，console对象都是undefined*/
(function() {
    var method;
    var noop = function () {};
    var methods = [
        'assert', 'clear', 'count', 'debug', 'dir', 'dirxml', 'error',
        'exception', 'group', 'groupCollapsed', 'groupEnd', 'info', 'log',
        'markTimeline', 'profile', 'profileEnd', 'table', 'time', 'timeEnd',
        'timeline', 'timelineEnd', 'timeStamp', 'trace', 'warn'
    ];
    var length = methods.length;
    var console = (window.console = window.console || {});

    while (length--) {
        method = methods[length];
        if (!console[method]) {
            console[method] = noop;
        }
    }
}());

/**
 * 判断js对象是否为空值，以下情况被视为空值：
 * 1. undefined 和 null
 * 2. {}
 * 3. "" 或者 ''
 * 4. []
 */
function isEmptyValue(value) {
    var type;
    if(!value||value == null) { /* 等同于 value === undefined || value === null*/
        return true;
    }
    type = Object.prototype.toString.call(value).slice(8, -1);
    switch(type) {
        case 'String':
            return $.trim(value).length==0;
        case 'Array':
            return !value.length;
        case 'Object':
            return $.isEmptyObject(value); /* 普通对象使用 for...in 判断，有 key 即为 false*/
        default:
            return false; /* 其他对象均视作非空*/
    }
};

/*为ie下添加indexOf方法,为ui-tab中兼容*/
if(!Array.indexOf){
	   Array.prototype.indexOf = function(Object){
	     for(var i = 0;i<this.length;i++){
	        if(this[i] == Object){
	           return i;
	         }
	     }
	     return -1;
	   }
}

/*String方法扩展替换｛num｝。例如："abc{0}".format("Good");*/
String.prototype.format = function(){
    var args = arguments;
    return this.replace(/\{(\d+)\}/g,
        function(m,i){
            return args[i];
        });
};

/*获取页面请求参数*/
function getRequestParams() {
	   var url = location.search;
	   var params = new Object();
	   if (url.indexOf("?") != -1) {
	      var str = url.substr(1);
	      strs = str.split("&");
	      for(var i = 0; i < strs.length; i ++) {
	    	  params[strs[i].split("=")[0]]=unescape(strs[i].split("=")[1]);
	      }
	   }
	   return params;
}


/*根据背景色获取文字颜色*/
function getTextColorByBackColor(color){

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

/*
* 如果一个元素在newArray和oldArray都存在，那么将newArray中的对象用oldArray中的那个替换
* 判断元素相同是使用传入的关键属性的值对比来判断的.
* 一般用在替换ng-repeat的数据时使用，可以确保列表中数据未变的元素在列表中不会被重新生成
*/
function replaceWithOldElements(newArray, oldArray, keyProperty){
  if(!oldArray) return newArray;
  /*创建老数组索引，避免多次遍历*/
  var lookup={};
  for (var i = 0, len = oldArray.length; i < len; i++) {
      lookup[oldArray[i][keyProperty]] = oldArray[i];
  }

  for(var i=0;i<newArray.length;i++){
    var existElement = lookup[newArray[i][keyProperty]];
    if(existElement){
      /* 
      * 将新元素数据写入到老的对象内部，然后用老数组中的改元素替代新数组中的
      * ng-repeat目前的版本确认是否是原对象还是靠的obj1===obj2
      * 所以这边可以以这样的方式保证已存在的元素不会被重新创建，同时也可以确保新值会被正确显示
      */
      angular.copy(newArray[i], existElement);
      newArray[i] = existElement;
    }
  }
  return newArray;
}

var c2={
	$loadSubject:function(){
		$.ajax({url: 'ws/getUserInfo',dataType: 'json',success: function(data){
			 if (data) {
				c2.subject = data;
			}else if(data && data.accessToken){//oauth2 client
				c2.subject = data;
			}
		},error:function(response){
		  if(response.status==401){
		      var loginUrl = response.getResponseHeader("loginurl");
		      
		      var backUrl = window.location.hash;
			  var localBaseIndexOf = loginUrl.indexOf("redirect_uri=");
			  if(localBaseIndexOf!=-1){
				var localBase = loginUrl.substring(localBaseIndexOf+13,loginUrl.indexOf("oauth2-login"));
				backUrl = window.location.href.replace(localBase,"");
			  }
			  window.location.href=loginUrl+"?backUrl="+encodeURIComponent(backUrl);
		  }
	   }});//页面加载时先同步加载当前用户的信息(包括token)
	}
}

$.ajaxSetup({async : false});
c2.$loadSubject();
$.ajaxSetup({async : true,cache: true});

