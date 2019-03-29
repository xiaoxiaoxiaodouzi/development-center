services.factory('OAuth2Interceptor', ['$q', 'Messenger', '$timeout', '$injector', function($q, Messenger, $timeout, $injector) {
	 $.ajaxPrefilter(function(opts, originalOpts, jqXHR) {
	        jqXHR.setRequestHeader("Authorization", "Bearer " + c2.subject.accessToken);
	        opts._ac = "Bearer " + c2.subject.accessToken;
	        if (opts.refreshRequest) {
	            return;
	        }
	        //移除原始的错误回调，改由我们触发
	        opts._error = opts.error;
	        opts.error = $.noop();
	        if (originalOpts.error) {
	            originalOpts._error = originalOpts.error;
	            originalOpts.error = $.noop();
	        }

	        var deferred = $.Deferred();
	        jqXHR.done(deferred.resolve);
	        jqXHR.fail(function() {
	            var args = Array.prototype.slice.call(arguments);
	            if (jqXHR.status === 401) {
	                if (opts._ac === 'Bearer ' + c2.subject.accessToken) {
	                    $.ajax({
	                        url: 'oauth2/refresh_token',
	                        async: false,
	                        dataType: 'json',
	                        refreshRequest: true,
	                        success: function(data) {
	                            c2.subject = data;
	                        },
	                        error: function() {
	                            if (opts._error)
	                                deferred.fail(opts._error);
	                            deferred.rejectWith(jqXHR, args);
	                        }
	                    });
	                }
	                var newOpts = $.extend(true, {}, originalOpts, { refreshRequest: true });
	                $.ajax(newOpts).then(deferred.resolve, deferred.reject);
	            } else {
	                if (opts._error)
	                    deferred.fail(opts._error);
	                deferred.rejectWith(jqXHR, args);
	            }
	        });
	        return deferred.promise(jqXHR);
	    });
	    return {
	        request: function(config) {
	            config.headers.Authorization = 'Bearer ' + c2.subject.accessToken;
	            return config;
	        },
	        responseError: function(response) {
	            if (response.status === 401) {
	                var retry = angular.isDefined(c2.subject.accessToken); //at为空就没必要重试了
	                if (retry) {
	                    if (response.config.headers.Authorization === 'Bearer ' + c2.subject.accessToken) {
	                        //只有请求的access token与当前access token一致的情况下才刷refresh token
	                        //因为浏览器js是单线程执行，这样可以确保一次只有refresh token刷新的请求触发
	                        $.ajax({
	                            url: 'oauth2/refresh_token',
	                            async: false /*同步执行，非常重要*/ ,
	                            dataType: 'json',
	                            refreshRequest: true,
	                            success: function(data) {
	                                c2.subject = data;
	                            },
	                            error: function() {
	                                //重新请求token失败，清空at
	                                c2.subject.accessToken = undefined;
	                            }
	                        });
	                    }
	                    response.config.headers.Authorization = 'Bearer ' + c2.subject.accessToken;
	                    return $injector.get('$http')(response.config); /*重试*/
	                }
	            }
	            return $q.reject(response);
	        }
	    };
}]);