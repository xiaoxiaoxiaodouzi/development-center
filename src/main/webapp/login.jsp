<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html class="login-content">
    <head>
        <meta charset="utf-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>开发中心</title>

        <!-- Vendor CSS -->
        <link href="assets/css/animate.min.css" rel="stylesheet">
        <link href="assets/css/material-design-iconic-font.min.css" rel="stylesheet">
        <link href="assets/css/loading-bar.css" rel="stylesheet">
      

        <!-- CSS -->
        <link href="assets/css/navigation.css"ssss rel="stylesheet" id="app-level">
        <link href="css/custom.css" rel="stylesheet">
        
    </head>

    <body class="login-content" id="l-login">

        <!-- Login -->
        <div class="login-center">
        	<img src="assets/img/profile-pics/l_2.png" alt="">
        </div>
        <div class="lc-block toggled" id="l-login" >
            <div class="input-group m-b-20">
                <span class="input-group-addon"><i class="md md-person"></i></span>
                <div class="fg-line">
                    <input id="username" type="text" class="form-control" placeholder="用户名">
                </div>
            </div>

            <div class="input-group m-b-20">
                <span class="input-group-addon"><i class="md md-vpn-key"></i></span>
                <div class="fg-line">
                    <input id="password" type="password" class="form-control" placeholder="密码">
                </div>
            </div>

            <div class="clearfix"></div>

            <!-- <div class="checkbox">
                <label>
                    <input type="checkbox" value="">
                    <i class="input-helper"></i>
                    	记住我
                </label>
            </div> -->
			
			<div id="showMes" class="error-warn" ></div>
			
            <a onclick="login()" id="login-button" class="btn btn-login btn-danger btn-float"><i class="md md-arrow-forward"></i></a>
        </div>
        <!-- Older IE warning message -->
        <!--[if lt IE 9]>
            <div class="ie-warning">
                <h1 class="c-white">警告!!</h1>
                <p>你使用的IE版本比较低，请升级至下列任意浏览器，以访问本网站.</p>
                <div class="iew-container">
                    <ul class="iew-download">
                        <li>
                            <a href="http://www.google.com/chrome/">
                                <img src="assets/img/browsers/chrome.png" alt="">
                                <div>Chrome</div>
                            </a>
                        </li>
                        <li>
                            <a href="https://www.mozilla.org/en-US/firefox/new/">
                                <img src="assets/img/browsers/firefox.png" alt="">
                                <div>Firefox</div>
                            </a>
                        </li>
                        <li>
                            <a href="http://www.opera.com">
                                <img src="assets/img/browsers/opera.png" alt="">
                                <div>Opera</div>
                            </a>
                        </li>
                        <li>
                            <a href="https://www.apple.com/safari/">
                                <img src="assets/img/browsers/safari.png" alt="">
                                <div>Safari</div>
                            </a>
                        </li>
                        <li>
                            <a href="http://windows.microsoft.com/en-us/internet-explorer/download-ie">
                                <img src="assets/img/browsers/ie.png" alt="">
                                <div>IE (New)</div>
                            </a>
                        </li>
                    </ul>
                </div>
                <p>Sorry for the inconvenience!</p>
            </div>
        <![endif]-->


      <!-- Core -->
      <script src="assets/js/jquery.min.js"></script>

      <script type="text/javascript">
		(function($) {
			$("#showMes").hide();
			$("#username").keyup(function(event) {
				if (event.keyCode == 13)$("#password").focus().select();
			});
			$("#password").keyup(function(event) {
				if (event.keyCode == 13)$("#login-button").click();
			});
			$("#username").focus();
		})(jQuery);
		function viewStatLogining() {
			$("#login-button").css({display : 'none'});
			$("#login-progress").css({display : 'inline-block'});
			$("#password").blur();
		}
		function viewStatLoginFinished() {
			$("#login-button").css({display : 'inline-block'});
			$("#login-progress").css({display : 'none'});
			$("#password").focus().select();
		}
		function login() {
			viewStatLogining();
			$("#showMes").hide();
			$.ajax({
				headers : {
					'Accept' : 'application/json',
					'Content-Type' : 'application/json'
				},
				'type' : 'POST',
				'url' : 'ws/login',
				'data' : JSON.stringify({username : $("#username").val(),	password : $("#password").val()}),
				'dataType' : 'json',
				'success' : function(data, type, request) {
					if (request.getResponseHeader('type') === 'error') {
						viewStatLoginFinished();
						$("#showMes").show();
			            $("#showMes").html("<i class='md md-error m-r-10'></i>您输入的用户名或者密码错误，登录失败");

					} else {
						var hrefStr=location.href;

						if(hrefStr.indexOf("backUrl")!=-1){
							var backUrl=hrefStr.substr(hrefStr.indexOf("backUrl")+8,hrefStr.length);
							location.href = "./"+backUrl;
						}else if(hrefStr.indexOf("login.jsp")!=-1||hrefStr.indexOf("login.html")!=-1){
							location.href = "./";
						}else{
							window.location.reload();
						}

					}
				},
				'error' : function(xmlhttprequest, errorinfo) {
					if (xmlhttprequest.getResponseHeader('type') === 'error') {
						viewStatLoginFinished();
						$("#showMes").show();
						$("#showMes").html("<i class='md md-error m-r-10'></i>您输入的用户名或者密码错误，登录失败");

					}
					viewStatLoginFinished();
				}
			});
		}
	</script>

    <!-- 提前缓存一些资源，提高首页加载速度 -->
    <!-- Angular -->
    <script src="assets/js/angular/angular.min.js"></script>
    <script src="assets/js/angular/angular-route.min.js"></script>
    <script src="assets/js/angular/angular-resource.min.js"></script>
    <script src="assets/js/angular/angular-animate.min.js"></script>

    <!-- Angular Modules -->
    <script src="assets/js/angular/ext/angular-ui-router.min.js"></script>
    <script src="assets/js/angular/ext/loading-bar.js"></script>

    <!-- Common Vendors -->
    <script src="assets/js/jquery.nicescroll.min.js"></script>
    <script src="assets/js/bootstrap-growl.min.js"></script>

    </body>
</html>
