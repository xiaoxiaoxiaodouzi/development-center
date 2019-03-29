var services = angular.module('services', ['ngResource', 'ui.router']);
services
    .service('nicescrollService', function ($rootScope) {
        var ns = {};
        ns.niceScroll = function (selector, color, cursorWidth) {
            $(selector).niceScroll({
                cursorcolor: color,
                cursorborder: 0,
                cursorborderradius: 0,
                cursorwidth: cursorWidth,
                bouncescroll: true,
                mousescrollstep: 100,
                autohidemode: false
            });
        }
        return ns;
    })
    //让动画效果按队列执行，参与队列运算的动画必须是由queue接口触发
    //TODO 如果点快了有时动画无法按顺序触发，需检查逻辑
    .service('animateQueue', function () {
        var animateQueue = [];
        var locked = false;

        var handler = function () {
            if (animateQueue.length > 0) {
                var animate = animateQueue.pop();
                $('body').one($.support.transition.end, handler);
                animate();
            } else {
                locked = false;
            }
        };

        var fire = function () {
            if (locked) return;

            locked = true
            if (animateQueue.length > 0) {
                var animate = animateQueue.pop();
                $('body').one($.support.transition.end, handler);
                //执行第一次，剩下的动画交给动画结束时的处理function来触发
                animate();
            }
        }

        var syncAnimate = {
            push: function (doAnimate) {
                if (angular.isFunction(doAnimate)) {
                    animateQueue.push(doAnimate);
                    fire();
                } else {
                    console.error('动画队列中只支持放入function');
                }
            }
        };
        return syncAnimate;
    })

/**
 * 弹出窗
 * 方法：
 * open(url,params,options)
 * url:引用页面地址
 * params:传递到应用页面的参数对象(必须为对象),直接传递到引用页面的$scope中。比如params={a:"aa"},在引用页面直接$scope.a可以获取值。
 * options:Modal参数
 * {
     * 	title:String 标题
     * 	headerColor:String 标题栏背景色；颜色可参考：http://172.16.25.142/material/#/user-interface/colors
     * 	size:String 弹出框大小。"","small","big".默认为空
     *  animation:boolean，是否开启动画效果；默认true
     *  keyboard:boolean 是否可以通过按Esc键关闭对话框；关闭后触发onDismiss事件。默认true
     *  onClose:function(params) 通过Modal.close(params)方法关闭对话框后触发的事件。
     *  onDismiss:function(params) 通过Modal.dismiss(params)方法关闭对话框后触发的事件。
     * }
 *
 * close(params):关闭打开的弹出窗，触发onClose事件
 *
 * dismiss(params):关闭打开的弹出窗，触发onDismiss事件
 *
 * 注意，应用页面如果需要定义js，需要加入Controller。
 * 可以用<div ng-controller="XxxController">包裹模版元素，然后通过developmentCenter.registerCtrl注册一个Controller
 * developmentCenter.registerCtrl('XxxController',function($scope,Modal){
		$scope.XX = function(){alert("xx")}
		});

 可以在写到页面<script>里面，也可以写到js文件里面，然后引用到模版页面。
 */
    .service('Modal', ['$http', '$document', '$compile', '$rootScope', '$templateCache', '$timeout', '$sce', function ($http, $document, $compile, $rootScope, $templateCache, $timeout,$sce) {
        var modal = {
        	closeParams:null,
            dismissClose: true,
            open: function (url, params, options) {
                if ("modalDefault", $("#modalDefault").length > 0) {
                    console.error("模态窗口已存在!");
                    return;
                }
                var modal = this;

                var defaultOptions = {title: "", url: url, animation: true, keyboard: true, show: true,btnText:"确定",btnClass:"btn-primary"};
                var modalOptions = angular.extend(defaultOptions, options);
                modalOptions.backdrop = "static";
                if (angular.isDefined(modalOptions.headerColor))modalOptions.headerColor = "bgm-" + modalOptions.headerColor;

                modal.onClose = modalOptions.onClose;
                modal.onDismiss = modalOptions.onDismiss;

                var modalScope = $rootScope.$new(true);
                angular.extend(modalScope, params);
                modalScope.modalOptions = modalOptions;
                //关闭(X)事件
                modalScope.modalOptions.X = this.dismiss;
                //模态窗口作用域内默认有两个关闭的function
                modalScope.dismiss = this.dismiss;
                modalScope.close = this.close;

                var modalHtml = $templateCache.get('modal.html')
                var modalWindowEle = angular.element(modalHtml);
                angular.element($document.find("body")[0]).append(modalWindowEle);
                if (modalOptions.animation)$("#modalDefault").addClass("fade");
                $("#modalDefault").modal(modalOptions);
                $compile(modalWindowEle)(modalScope);

                //把.modal-footer元素拿出来放到.modal-body外面
                $timeout(function () {
                    if ($("#modalDefault").find(".modal-footer").length > 0) {
                        $("#modalDefault").find(".modal-content").append($("#modalDefault").find(".modal-footer"));
                    }
                }, 200);

                $("#modalDefault").on("hide.bs.modal", function (e) {
                    if (modal.dismissClose) {
                    	$timeout(function () {
                    		$("#modalDefault").remove();
                    		if (angular.isDefined(modal.onDismiss))modal.onDismiss(modal.closeParams);
                            modal.closeParams = null;
                        }, 400);
                    } else {
                    	$timeout(function () {
                            $("#modalDefault").remove();
                            if (angular.isDefined(modal.onClose))modal.onClose(modal.closeParams);
                            modal.closeParams = null;
                        }, 400);
                        modal.dismissClose = true;
                    }
                });
            },
            openConfirm:function(options){
            	var url = "template/confirm.html";
            	options.message = $sce.trustAsHtml(options.message);
            	modal.open(url,null,options);
            },
            delOpenConfirm:function(options){
            	var url = "template/delConfirm.html";
            	options.message = $sce.trustAsHtml(options.message);
            	modal.open(url,null,options);
            },
            close: function (data) {
                modal.dismissClose = false;
                modal.closeParams = data;
                $("#modalDefault").modal('hide');
            },
            dismiss: function (data) {
                $("#modalDefault").modal('hide');
                modal.closeParams = data;
            }
        }
        return modal;
    }]).service('FormContainerFactory', function () {
        var counter = 0;
        var FormContainer = function () {
            this.id = counter++;
            this.controls = {};
        };
        FormContainer.prototype = {
            $isFormContainer: true
        }
        this.create = function () {
            return new FormContainer;
        };
        this.findFormScope = function (scope) {
            var currScope = scope;
            while ((!currScope.form) || !currScope.form.$isFormContainer) {
                currScope = currScope.$parent;
                if (!currScope) {
                    return undefined;
                }
            }
            return currScope;
        },
            // 查找最近的FormContainer
            this.findForm = function (scope) {
                var formScope = this.findFormScope(scope)
                if (formScope) {
                    return formScope.form;
                }
                return undefined;
            };
    });

/**
 * 支持在弹出窗口显示的状态定义
 * 支持的参数基本与ui-router的标准state略有区别：
 * 1. 多一个options参数，即模态窗口的options，文档参考上面的Modal服务
 * 2. params参数与标准state的params含义稍有区别，这里的params表示传入到对话框内的初始参数，跟url无关
 * 3. 只支持tamplateUrl，不支持template和templateProvider
 * 4. 不支持abstract的state
 * 
 * 使用示例：
 * developmentCenter.config(function ($stateProvider, $urlRouterProvider,modalStateProvider){       
 *     modalStateProvider.state('home.workbench.myTasks.detail', {
 *          url: '/{taskId}',    //显示路径
 *          templateUrl: '', //对话框页面模板地址，即Modal.open的url参数
 *          params:{}, //对话框初始数据，即Modal.open的params参数
 *          modalOptions:{}//对话框控制参数，即Modal.open的options参数
 *     });
 * });
 */
services.provider('modalState', function($stateProvider) {
    var provider = this;
    this.$get = function() {
        return provider;
    }
    this.state = function(stateName, options) {
    	if(angular.isUndefined(options.modalOptions)){
    		options.modalOptions = {}
    	}
    	
		var outterOnClose = options.modalOptions.onClose;
		var outterOnDismiss = options.modalOptions.onDismiss;
		
        $stateProvider.state(stateName, {
            url: options.url,
            controller: options.controller,
            resolve: options.resolve,
            views:options.views,
            onEnter: function(Modal, $state) {
            	options.modalOptions.onClose = function(params){
        			if($state.$current.name === stateName) $state.go('^');
        			if(outterOnClose) outterOnClose(params);
        		}
            	options.modalOptions.onDismiss = function(params){
        			if($state.$current.name === stateName) $state.go('^');
        			if(outterOnDismiss) outterOnDismiss(params);
        		}
            	
                Modal.open(options.templateUrl, options.params, options.modalOptions);
            },
            onExit: function(Modal) {
                Modal.close();
            }
        });
        
        return this;
    };
});

/**
 * 树型数据的数据源，内部实际上只是保存了从后台获取数据的url
 *
 * @param $http
 * @param $resource
 * @param sn
 * @param pid
 * @constructor
 */
var TreeDataSource = function (url) {
    this.$url = url;
};

TreeDataSource.prototype = {
    $isAsync: true,
    $load: function () {
        // do nothing
    }
};

/**
 * 列表数据的数据源，内部实际上只是保存了从后台获取数据的url
 *
 * @param url
 */
var ListDataSource = function (url) {
    this.$url = url;
};

ListDataSource.prototype = {
    $isAsync: true,
    $load: function () {
        // do nothing
    }
}

/**
 * 调用后台WebService的数据源
 *
 * @param $http
 *            angular的$http服务
 * @param sn
 *            服务序号
 * @constructor
 */
var WebServiceDataSource = function ($http, sn, params) {
    this.$http = $http;
    this.$sn = sn;
    this.$initParams = params;
};

WebServiceDataSource.prototype = {
    $load: function (_params, errorCallback) {
        var _this = this;
        this.$initParams = angular.extend(this.$initParams, _params);
        var promise = this.$http.get("ws/" + this.$sn, {
            params: _this.$initParams
        }).success(function (data) {
            if (typeof _this.$filter == "function")_this.$filter(data);
            angular.forEach(data, function (value, key) {
                _this[key] = value;
            });
            if (angular.isDefined(_this.addFilter))addFilter(_this);
            return _this;
        }).error(function () {
            if (angular.isFunction(errorCallback)) {
                errorCallback();
            }
            return "没有数据";
        });
        return promise;
    },
    $reload: function () {
        return this.$load(this.$params);
    }
};

var DataSourceWrapper = function ($q, datasources) {
    this.datasources = datasources;
    this.$q = $q;
};

DataSourceWrapper.prototype = {
    loadAll: function ($params) {
        var promises = [];
        var _q = this.$q;
        angular.forEach(this.datasources, function (ds, id) {
            ds.$q = _q;//将$q服务注入到每一个数据源内
            if (angular.isFunction(ds.$load)) {
                //所有数据源的$load必须返回一个promise
                promises.push(ds.$load($params));
            }
        });
        return _q.all(promises);
    }
};

services.factory('ModelFactory', ['$resource', '$http', '$q',
    function ($resource, $http, $q) {
        return {
            wrap: function (datasources, $params) {
                return new DataSourceWrapper($q, datasources);
            },
            tree: function (url) {
                return new TreeDataSource(url);
            },
            /**
             * 初始化列表型数据源
             *
             * @param url
             *            contentProvider的访问路径
             * @returns {ListDataSource}
             */
            list: function (url) {
                return new ListDataSource(url);
            },//
            ws: function (sn, params) {
                return new WebServiceDataSource($http, sn, params);
            },
            /**
             * 创建实体数据源(实际上是一个实体对应的RESTFul客户端)
             *
             * @param sn
             *            实体编号
             * @param pk
             *            实体主键属性名
             * @param params
             *            实体初始化参数
             * @returns {*}
             */
            entity: function (sn, pk, params) {
                var resource = $resource('e/' + sn + '/:id', {
                    id: '@' + pk
                }, {
                    'get': {
                        method: 'GET',
                        transformResponse: function (data, headers) {
                        	if(data){
                        		var obj = angular.fromJson(data);
                        		if (typeof entity.$filter == "function")entity.$filter(obj);
                        		resource.prototype.$initialState = angular.copy(obj);
                        		return obj;
                        	}
                        }
                    },
                    'execute': {
                        method: 'POST',
                        //调用操作url：e/{sn}/op/{op}/{id}
                        url: 'e/' + sn + '/op' + '/:op' + '/:id'
                    }
                });
                //angular的Resource的内置变量
                var innerPropertyNames = ["$promise", "$resolved", "$q"];
                var countProperty = function (obj) {
                    //只计算非内置变量
                    var count = 0;
                    angular.forEach(obj, function (value, key) {
                        if ($.inArray(key, innerPropertyNames) == -1)
                            count++;
                    });
                    return count;
                };
                resource.prototype.$load = function () {
                    //只返回一个promise变量，不调用任何加载逻辑
                    return this.$promise;
                };
                resource.prototype.$dirty = function (dirty) {
                    var _this = this;
                    if (angular.isUndefined(_this.$initialState)) {
                        return false;
                    }
                    if (angular.isDefined(dirty)) {
                        _this.$initialState = undefined;
                        return false;
                    }
                    if (countProperty(_this) != countProperty(_this.$initialState)) {
                        return true;
                    }
                    var dirty = false;
                    angular.forEach(_this.$initialState, function (value, key) {
                        if (dirty)return;
                        if (!angular.equals(_this[key], value)) {
                            dirty = true;
                        }
                    });
                    return dirty;
                };
                resource.prototype.$reset = function () {
                    var _this = this;
                    if (angular.isDefined(_this.$initialState)) {
                        angular.forEach(_this, function (value, key) {
                            if (!angular.equals(value, _this.$initialState[key])) {
                                _this[key] = _this.$initialState[key];
                            }
                        });
                    }
                };
                var entity;
                if (angular.isDefined(params) && !isEmptyValue(params)) {
                    entity = resource.get(params);
                } else {
                    resource.prototype.$initialState = {};
                    //参数为空时，在初始化时不去加载数据，减少页面加载时的请求数
                    entity = new resource();

                }
                return entity;
            },
            /**
             * 对应后台bean，但是没有初始化获取数据的过程
             */
            bean: function () {
                return {
                    $load: function () {
                        return {};
                    }
                };
            }
        };
    }]);

services.factory('Messenger', function () {
    function notify(msg, type, icon) {
        return $.notify({
            icon: icon,
            message: msg,
            url: ''
        }, {
            element: 'body',
            type: type ? type : 'info',
            allow_dismiss: true,
            placement: {
                from: 'bottom',
                align: 'center'
            },
            offset: 20,
            spacing: 10,
            z_index: 2000,
            delay: 2500,
            timer: 1000,
            url_target: '_blank',
            mouse_over: false,
            animate: {
                enter: 'animated fadeInUp',
                exit: 'animated fadeOutDown'
            },
            icon_type: 'class',
            template: '<div data-notify="container" class="col-xs-11 col-sm-3 alert alert-{0}" role="alert" style="z-index:2000;">' +
        		'<button type="button" aria-hidden="true" class="close" data-notify="dismiss">×</button>' +
        		'<span data-notify="icon"></span> ' +
        		'<span data-notify="title">{1}</span> ' +
        		'<span data-notify="message">{2}</span>' +
        		'<div class="progress" data-notify="progressbar">' +
        			'<div class="progress-bar progress-bar-{0}" role="progressbar" aria-valuenow="0" aria-valuemin="0" aria-valuemax="100" style="width: 0%;"></div>' +
        		'</div>' +
        		'<a href="{3}" target="{4}" data-notify="url"></a>' +
        	'</div>'
        });
    }

    return {
        success: function (msg) {
            return notify(msg, 'success');
        },
        error: function (msg) {
            return notify(msg, 'danger');
        },
        post: function (msg) {
            if (typeof msg === "string") {
                return notify(msg);
            } else {
                var type = msg.type;
                if (type == 'error') {
                    type = "danger";
                }
                return notify(msg.message, type, msg.icon);
            }

        }
    };
});

services.provider('markdownConverter', function () {
    var opts = {};
    return {
        config: function (newOpts) {
            opts = newOpts;
        },
        $get: function () {
            var renderer = new marked.Renderer();
            renderer.table = function (header, body) {
                var table = '<table class="table table-striped table-bordered table-hover">' +
                    '<thead>' + header + '</thead>' + body + '</table>';
                return table;
            };
            renderer.tablerow = function (content) {
                return '<tr>' + content + '</tr>';
            };
            
            var options={
            		renderer: renderer,
            		langPrefix:'hljs ',
            		highlight: function (code,lang,callback) {
            			return hljs.highlightAuto(code,[lang]).value;
            		}
            };
            return {render:function(str){
            	var x=0,y=0,firstLevel;
            	options.renderer.heading = function (text, level) {
                	if(angular.isUndefined(firstLevel))firstLevel = level;
                	if(firstLevel==level)x+=1;
                	if(firstLevel+1==level)y+=1;
                	return "<h" + level + " id='"+x+"_"+y+"'>"+text+"</h"+level+">";
                }
            	return marked(str,options);
            }};
        }
    };
}).filter('markdown', ['$sce', 'markdownConverter','$ocLazyLoad', function ($sce, markdownConverter,$ocLazyLoad) {
	//确保有高亮样式
	$ocLazyLoad.load('css/markdown/xcode.css');
    return function (text) {
        if (text == null) text = '';
        var html = markdownConverter.render(text);
        return $sce.trustAsHtml(html);
    };
}]);
;

services.service('debounce', ['$timeout', function ($timeout) {
    return function (func, wait, immediate) {
        var timeout, args, context, result;

        function debounce() {
            /* jshint validthis:true */
            context = this;
            args = arguments;
            var later = function () {
                timeout = null;
                if (!immediate) {
                    result = func.apply(context, args);
                }
            };
            var callNow = immediate && !timeout;
            if (timeout) {
                $timeout.cancel(timeout);
            }
            timeout = $timeout(later, wait);
            if (callNow) {
                result = func.apply(context, args);
            }
            return result;
        }

        debounce.cancel = function () {
            $timeout.cancel(timeout);
            timeout = null;
        };
        return debounce;
    };
}]);

services.service('socketio', ['$ocLazyLoad', '$timeout', '$http','$rootScope', function ($ocLazyLoad, $timeout, $http, $rootScope) {
    var eventListeners = [];
    var service = {
        socket: undefined,
        on: function (scope , event, func) {
            eventListeners.push({event: event, func: func});
            if (!service.socket) {
                //连接建立后，会将监听列表中所有的内容都初始化
                return;
            }
            service.socket.on(event, func);
            
            if(scope){
            	scope.$on('$destroy',function(){
            		console.log("destroy",scope);
                	service.remove(event,func);
                });
            }
        },
        remove: function (event, func) {
            if (!service.socket) {
                console.error("socketio不存在");
                return;
            }
            service.socket.removeListener(event, func);
            for (var i = eventListeners.length - 1; i >= 0; i--) {
                if (eventListeners[i].event == event && eventListeners[i].func == func)eventListeners.splice(i,1);
            }
        },
        listeners: function () {
            return eventListeners;
        }
    };
    $ocLazyLoad.load('assets/js/socket.io-1.3.5.js').then(function () {
        /*console.log('开始获取推送服务器元数据...');
        $http.get('message/metadata').success(function(data){
          console.log('推送服务器信息获取成功，开始连接...');
          service.socket = io.connect(data.url, {
              'query':'credential='+data.credential,
              'reconnection delay': 5000,
              'force new connection': true
          });
          service.socket.on('connect', function (data) {
        	  $rootScope.socketId=service.socket.id;
              console.log('推送服务器连接成功，开始进行身份认证...');
              $http.put("message/authentication/"+service.socket.id).success(function(data){
                console.log('推送服务器身份认证成功！');
              });
          });
          for (var i = 0; i < eventListeners.length; i++) {
              service.socket.on(eventListeners[i].event, eventListeners[i].func);
          }
        });*/
    });
    return service;
}]);

// services.service('TimelineTempalteCache', ['$http','$parse',function ($http,$parse) {
//     var allTemplates = [];
//     var promise = $http.get('template/timeline.html').success(function(templates){
//         $(templates).each(function (index, ele) {
//             var transEle = $(ele);
//             if (transEle.is('timeline-template')) {
//                 var expr = transEle.attr('for');
//                 allTemplates.push({ expr: expr, match:$parse(expr), html: transEle.html().trim() });
//             }
// 		});
//     });
//     return {
//         promise:promise,
//         get:function($scope){
//             for (var i = allTemplates.length - 1; i >= 0; i--) {
// 				if (allTemplates[i].match($scope)) {
// 					return allTemplates[i].html;
// 				}
// 			}
//         }
//     }
// }]);

services.service('DynamicTempalteCache', ['$http','$parse',function ($http,$parse) {
    var referenceTemplates = {};
    var timelineTemplates = [];
    var listviewTemplate;
    var listviewRowTemplate = {};
    var promise = $http.get('template/dynamics.html').success(function(templates){
        $(templates).each(function (index, ele) {
            var template = $(ele);
            if (template.is('reference-template')) {
                var type = template.attr('type');
                referenceTemplates[type] = template.html().trim();
            }else if(template.is('timeline-template')){
                var expr = template.attr('for');
                timelineTemplates.push({ expr: expr, match:$parse(expr), html: template.html().trim() });
            }else if(template.is('listview-template')){
            	listviewTemplate = template.html();
            }else if(template.is("listview-row-template")){
            	var type = template.attr('type');
            	listviewRowTemplate[type] = template.html().trim();
            }
            	
		});
    });
    return {
        promise:promise,
        reference:function(type){
           return referenceTemplates[type];
        },
        timeline:function($scope){
             for (var i = timelineTemplates.length - 1; i >= 0; i--) {
				if (timelineTemplates[i].match($scope)) {
					return timelineTemplates[i].html;
				}
			}
        },
        listview:function(){
        	return listviewTemplate;
        },
        listviewRow:function(type){
        	return listviewRowTemplate[type];
        }
    }
}]);
