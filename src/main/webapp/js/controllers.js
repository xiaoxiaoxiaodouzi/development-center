/* global $ */
developmentCenter
	/*首页全局控制器*/
    .controller('indexController', ["$rootScope", "$timeout", "$state", "$http", "socketio", function($rootScope, $timeout, $state, $http, socketio){
        $rootScope.$state=$state;
        this.$state = $state;

        if( /Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(navigator.userAgent) ) {
           angular.element('html').addClass('ismobile');
        }

        this.sidebarToggle = {
            left: false
        }

        this.layoutType = localStorage.getItem('ma-layout-status');
       
        this.sidebarStat = function(event) {
            if (!angular.element(event.target).parent().hasClass('active')) {
                this.sidebarToggle.left = false;
            }
        }

//        $http.post("ws/getUserInfo",{}).success(function(data){
//			$rootScope.subject = data;
//        });
        $rootScope.subject = c2.subject;
        
        this.logout = function(){
        	$http.post("ws/logout",{}).success(function(data){
        		if (angular.isUndefined(data) || angular.isUndefined(data.result)) {
    					window.location.reload();
    				}else{
    					window.location.href = data.result;
    				}
        	});
        }
    }])
	/*顶栏控制器，内部有全局消息推送的注册，还有全局创建菜单，消息和任务列表*/
    .controller('headerCtrl', ["$scope", "$attrs", "$timeout","$http","socketio","$rootScope","Modal","$state","$stateParams","$location","$sce",function($scope, $attrs, $timeout,$http,socketio,$rootScope,Modal,$state,$stateParams,$location,$sce){	
    	/*显示当前项目
    	$scope.currentProjectId = $state.params.project;
    	切换项目事件
    	$scope.itemClick=function(item){
    		定义详情与列表状态关联，如果在详情状态切换项目，跳转到关联的列表状态
    		var stateRelation = {
				"project.milestoneDetail":"project.milestones",
				"project.weeklyReport":"project.weeklyReports",
				"project.storyComment":"project.story",
				"project.doc":"project.docs",
				"project.milestoneDetail.storyList":"project.milestones",
				"project.milestoneDetail.changeList":"project.milestones",
				"project.milestoneDetail.taskList":"project.milestones",
				"project.milestoneDetail.bugList":"project.milestones",
				"project.instanceTree.instance":"project.plan",
				"project.instanceTree.list":"project.plan",
				"project.case.detail":"project.case",
				"project.case.addCase":"project.case"
    		};
    		var goStateName = stateRelation[$state.current.name]?stateRelation[$state.current.name]:$state.current.name;
    		$state.go(goStateName,{project:item.id});
    	}*/
    	$http.post("ws/project/myprojects",{}).success(function(data){
    		$scope.myProjects = data.result;
    		$scope.projectIds=new Array()
    		angular.forEach(data.result,function(v,n){
    			$scope.projectIds[n] = v.id;
    		});
    		$scope.totalSearchProjectItems = angular.copy(data.result);
    		$scope.totalSearchProjectItems.unshift({id:"0",name:"所有项目"});
    	});
    	
    	$scope.systemDesktopNotify=function(title,content,icon,userId,userRealname,category){
    		if (window.Notification){
    			if(Notification.permission==='granted'){
    				  var notification = new Notification("来自["+userRealname+"]通知",
						{
							icon : icon,
							body : content
						}
    				  );
    				  
    				  notification.onclick = function(){
    					  window.focus();
    					  if(category=="pushBroadcast"){
    						  $state.go("home.messages.chat",{'user_id':'$broadcast'});
    					  }else{
    						  $state.go("home.messages.chat",{'user_id':userId});
    					  }
    				  }
    				  setTimeout(notification.close.bind(notification),10000);
    			}else {
    				window.Notification.requestPermission();
    			};
    		}
    	}
    	
      	$scope.hasSideBar = angular.isDefined($attrs.hasSideBar);
      	
      	$scope.checkStatus=false;
      	
      	/*根据是否有sidebar来确定使用哪种布局*/
      	if($scope.hasSideBar){
      		$scope.mactrl.layoutType='1';
      	}else{
      		$scope.mactrl.layoutType='0';
      	}

      	$rootScope.loadMessage=function(){
            $http.post("ws/getMessageListForPage",{pageIndex:1,pageSize:4,condition:{opened:false}}).success(function(data){
            	$rootScope.messageResult=data.result;
            });
      	}
      	
      	$rootScope.loadTask=function(){
	    	/*$http.post("ws/getIndexTaskList",{search:{},pageable:{pageSize:5,pageIndex:1}}).success(function(data){
	    		$rootScope.taskMessageList = data.result;
	    	});*/
      	}
      	
      	$scope.addTask=function(){
      		Modal.open("home/addTask.html",{},{onClose:function(task){
                $rootScope.$broadcast("topCreateNewTask",task);
      		}});
      	}
    	$scope.addStory=function(){
      		Modal.open("project/addStory.html",{
      			title:'',
      			animation:true,
      			size:"middle",onClose:function(data){
                    $rootScope.$broadcast("createNewStory",data);
          		}});
      	}
    	$scope.addBug=function(){
      		Modal.open("project/bug/add.html",{
      			title:'',
      			animation:true,
      			size:"middle",onClose:function(data){
                    $rootScope.$broadcast("createNewBug",data);
          		}});
      	}
      	
      	//$scope.addStory=function(){
      		//Modal.open("project/select.html",{},{
      			//title:'请选择项目',
      			//animation:true,
      			//size:"big",
      			//onClose:function(params){
      				//$state.go("project.story",{"init":"add","project":params.id});
      			//}
      		//});
      	//}
      	
      	$scope.addDoc=function(){
      		Modal.open("project/select.html",{},{
      			title:'请选择项目',
      			animation:true,
      			size:"big",
      			onClose:function(params){
      				$state.go("project.docs",{"init":"add","project":params.id});
      			}
      		});
      	}
      	
      	$scope.taskDone=function(messageTask){
			var options = {
    				title:' '+messageTask.name+' ('+moment(messageTask.estStartDate).format('YYYY-MM-DD')+' - '+moment(messageTask.deadline).format('YYYY-MM-DD')+')',
    				size:"big",
    				animation:true,
    				onClose:function(params){
    					messageTask.isDone=false;
    					$rootScope.loadTask();
    				},
    				onDismiss:function(params){
    					messageTask.isDone=false;
    				}
    		};
			
    		Modal.open("project/task/estimate.html",{taskId:messageTask.id},options);
      	}
      	
      	var hctrl = this;
        /* Top Search*/
      	$scope.globalSearchCondition = {type:"all",project:"0"};
      	$scope.totalSearchTypes = [{id:"all",name:"全部",iconClass:"md-apps"},
      	                           {id:"task",name:"任务",iconClass:"md-format-list-bulleted"},
      	                           {id:"bug",name:"BUG",iconClass:"md-bug-report"},
      	                           {id:"story",name:"需求",iconClass:"md-content-paste"}];
      	
      	//ESC退出搜索
      	var searchKeyup = function(event){
      		if(event.keyCode==27)hctrl.closeSearch();
      	}
        this.openSearch = function(){
            angular.element('#header').addClass('search-toggled');
            $("#globalSearchInput").focus();
            $http.post("ws/search/initElasticsearch",{});
            $(document).bind("keyup",searchKeyup);
        }
        this.closeSearch = function(){
            angular.element('#header').removeClass('search-toggled');
            $(document).unbind("keyup",searchKeyup);
        }
        $scope.changeSearch = function(paramName,value,name){
        	$scope.globalSearchCondition[paramName] = value;
        	if(name)$scope.globalSearchCondition[paramName+"Name"] = name.replace("<em>","").replace("</em>","");
        }
        $scope.searchClean = function(paramName){
        	delete $scope.globalSearchCondition[paramName];
        	delete $scope.globalSearchCondition[paramName+"Name"];
        }
        //搜索建议
        $scope.$watch("hctrl.globalSearch",function(nv){
        	if(angular.isUndefined(nv)) return ;
        	$http.post("ws/search/suggestWords",{search:nv,page:1,size:10}).success(function(data){
        		var suggestWords = data.result;
        		var swList = [];
        		angular.forEach(suggestWords,function(v,n){
        			swList.push({value:v});;
        		});
        		hctrl.suggests = swList;
        		$("#search-dropdown").controller("c2Dropdown").toggle(hctrl.suggests.length>0);
        	});
        });
        var firstLoad = true;
        $scope.$watch("globalSearchCondition",function(nv,ov){
        	if(firstLoad){
        		firstLoad = false;
        		return ;
        	}
        	globalSearch();
        },true);
        //全局搜索
        function globalSearch(pageIndex){
        	var searchCondition = angular.copy($scope.globalSearchCondition);
        	//如果所有项目，传递当前用户所有项目id数组
        	if(searchCondition.project==0)searchCondition.projectIds = $scope.projectIds;
        	else searchCondition.projectIds = [searchCondition.project];
        	
        	var search = angular.isDefined(hctrl.globalSearch)?hctrl.globalSearch:"";
        	$http.post("ws/search/globalSearch",{search:search,searchCondition:searchCondition,page:pageIndex}).success(function(data){
        		angular.forEach(data.searchHits.hits,function(searchData){
        			angular.forEach(searchData.source.labels,function(label){
        				label.backgroundStyle = {"cursor":"pointer","background-color":label.color,"border-radius":"2px","color":getTextColorByBackColor(label.color)};
        			});
        		});
        		$scope.searchData = data;
        	});
        }
        $scope.searchPaging = function(pageIndex){
        	globalSearch(pageIndex);
        }
        this.searchEnter = function(){
        	$("#search-dropdown").controller("c2Dropdown").toggle(false);
        	globalSearch();
        }
        this.suggestClick = function(searchString){
        	hctrl.globalSearch = searchString;
        	globalSearch();
        }
        //搜索结果侧滑页面
        $scope.searchDetail = function(searchType,hit){
        	var url = "";
        	var activeId = hit.type+hit.id;
        	switch (searchType) {
			case "task":
				$scope.globalSearchParams = {taskId:hit.source.id};
    			url = "project/task/detail.html";
				break;
			case "bug":
				$scope.globalSearchParams = {projectId:hit.source.projectId,bugNo:hit.source.bugNo}; 
				url = "project/bug/detail.html";
				break;
			case "story":
				$scope.globalSearchParams = {projectId:hit.source.projectId,storyId:hit.source.id}; 
				url = "project/story/detail.html";
				break;

			default:
				break;
			}
        	if($scope.detailUrl==""){
        		$scope.activeId = activeId;
        		$scope.detailUrl = url;
        	}else{
        		$timeout(function(){
        			$scope.activeId = activeId;
        			$scope.detailUrl = url;
        		},300);
        	}
        }
        
        $scope.fomatBugStatus=function(status){
			switch(status){ 
				case 0: 
					return "Closed";
				case 1: 
					return "New";
				case 2: 
					return "Open";
				case 3: 
					return "Reopen";
				case 4: 
					return "FixLater";
				case 5: 
					return "Rejected";
				case 6: 
					return "Fixed";													
			}
		}

        /*Fullscreen View*/
        this.fullScreen = function(){
            function launchIntoFullscreen(element) {
                if(element.requestFullscreen) {
                    element.requestFullscreen();
                } else if(element.mozRequestFullScreen) {
                    element.mozRequestFullScreen();
                } else if(element.webkitRequestFullscreen) {
                    element.webkitRequestFullscreen();
                } else if(element.msRequestFullscreen) {
                    element.msRequestFullscreen();
                }
            }

            function exitFullscreen() {
                if(document.exitFullscreen) {
                    document.exitFullscreen();
                } else if(document.mozCancelFullScreen) {
                    document.mozCancelFullScreen();
                } else if(document.webkitExitFullscreen) {
                    document.webkitExitFullscreen();
                }
            }

            if (exitFullscreen()) {
                launchIntoFullscreen(document.documentElement);
            }
            else {
                launchIntoFullscreen(document.documentElement);
            }
        }

        $rootScope.loadMessage();

        socketio.on($scope,'pushSystem',function(data){
        	$rootScope.loadMessage();
        	var message=JSON.parse(data);
        	
        	if(message.fromUserId!=$stateParams.user_id){
        		$scope.systemDesktopNotify("消息通知",message.content,message.fromUserIcon,message.fromUserId,message.fromUserRealname);
        	}
        });
        
        $rootScope.loadTask();
        
        socketio.on($scope,'pushTask',function(data){
        	$rootScope.loadTask();
        });
        
        $scope.appendMsg=function(message){
        	$rootScope.messageResult.rows.unshift(message);
        	$rootScope.messageResult.totalCount++;
		    if( $rootScope.$$phase != '$apply' &&  $rootScope.$$phase != '$digest') {
			    $rootScope.$apply();
		    }
        }
        
        
        var onPushBroadcast=function(data){
        	var message=JSON.parse(data);
        	
        	$rootScope.loadMessage();
        	if('$broadcast'!=$stateParams.user_id){
        		$scope.systemDesktopNotify("通知",message.content,'assets/img/profile-pics/tz.jpg',message.fromUserId,message.fromUserRealname,"pushBroadcast");
        	}
        }
        socketio.on($scope,'pushBroadcast',onPushBroadcast);
        
        $scope.openMsg=function(message){
        	if(message.messageVo.link){
        		$location.path(message.messageVo.link);
                $http.post('ws/message/updateMsgOpened',{umId:message.um_id}).success(function(){
                	$rootScope.loadMessage();
                });
        	}else{
        		$state.go("home.messages.chat",{user_id:message.msg_from});
        	}
        }
    }])
    /*"我的项目"的控制器*/
    .controller('myprojectsController', ['$scope','$http','Modal','Messenger','ModelFactory','debounce',function($scope,$http,Modal,Messenger,ModelFactory,debounce){
        var $model=$scope.$model={};
        var $params=$scope.$params={};
        
        $scope.loaded = false;
        $scope.visible = false;
        $scope.flag = "显示";
        init();
        
        function init(){
        	var allDs=ModelFactory.wrap([ModelFactory.ws('project/getAllMyProjects',{}),
    		                             ModelFactory.ws("getCurrentUserPreferences",{infoNames:["starProject","projectViewMode"]})]);
    		
    		allDs.loadAll().then(function(){
    				$scope.myProjects = allDs.datasources[0].result ;
    				$scope.starProjects = new Array();
    				if($scope.myProjects.length>0){
    					var projectids=[];
                        for(var i=0;i<$scope.myProjects.length;i++){
                          projectids.push($scope.myProjects[i].id);
                        }
                        
                        $http.post('ws/project/statistics',{projectIds:projectids}).success(function(data){
                        	var statistics = {} ;
                        	angular.forEach(data.result,function(value){
                        		statistics[value.projectId] = value ;
                        	}) ;
                            $params.statistics=statistics;
                            $scope.loaded = true;
                          });
                        
                        var preferences = allDs.datasources[1].result ;
                        if(preferences.starProject && preferences.starProject.infoContent){
	                   		 var starProjectIds = preferences.starProject.infoContent.split(",");
	                		 angular.forEach($scope.myProjects,function(p){
	                			 angular.forEach(starProjectIds,function(pid){
	                    			 if(p.id+"" == pid){
	                    				 p.star = true;
	                    				 $scope.starProjects.push(p);
	                    			 }
	                    		 }); 
	                		 });
                        }
                		 
                		 if(!preferences.projectViewMode || !preferences.projectViewMode.infoContent){
                			 $scope.projectViewMode = 'simple' ;
                		 }else{
                			 $scope.projectViewMode = preferences.projectViewMode.infoContent ;
                		 }
    				}else{
    					 $scope.projectViewMode = 'simple' ;
    				}
    			});
    		
        }
        
        $scope.switchViewMode=function(){
        	if(angular.equals($scope.projectViewMode,'simple')){
        		$scope.projectViewMode = 'default' ;
        		saveViewModePreference('projectViewMode','default') ;
        	}else if(angular.equals($scope.projectViewMode,'default')){
        		$scope.projectViewMode = 'simple' ;
        		saveViewModePreference('projectViewMode','simple') ;
        	}
        }
        
        var saveViewModePreference = debounce(function(infoName,content){
    		$http.post("ws/saveCurrentUserPreference",{dto:{infoName:infoName,infoContent:content}});
    	},1000);
        
        $scope.projectStar = function(clickProject){
        	clickProject.star = !clickProject.star;
        	var newPids = new Array();
        	angular.forEach($scope.myProjects,function(p,i){
        		if(p.star)newPids.push(p.id);
        	});
        	$http.post("ws/saveCurrentUserPreference",{dto:{infoName:"starProject",infoContent:newPids.join(",")}}).success(function(data){
        		if(clickProject.star)$scope.starProjects.push(clickProject);
        		else{
        			angular.forEach($scope.starProjects,function(sp,x){
            			if(sp == clickProject)$scope.starProjects.splice(x,1);
            		});
        		}
            });
        }
        $scope.getProjectStatistic = function(id,property){
          return $params.statistics[""+id][property];
        }
        $scope.viewTip = function(project){
        	var userId = $scope.subject.userId;
        	var projectid = project.id;
        	var projectcreator;
        	//查询当前用户是否是创建者
        	$http.post("ws/getProjectCreator",{projectId:projectid})
        	.success(function(data,status,headers,config){
        		projectCreator = data.result;
        		if(!(projectCreator.user_id==userId)){
            		Messenger.post({message:"项目【"+project.name+"】已归档, 归档人【"
            			+projectCreator.user_realname+"】!",hideAfter:5,showCloseButton:true});
            	}else{
            		Modal.open('project/settings/active-project.html',{project:project,creator:projectCreator.user_realname},{
                        title:'重新激活项目',                
                        onClose:function(){   
                        	init();
                        }
                    })
            	}        	
        	});
        }
        $scope.viewToggle = function(){
        	$scope.visible = !$scope.visible;
        	if($scope.flag == "隐藏")
        		$scope.flag = "显示";
        	else if($scope.flag == "显示")
        		$scope.flag = "隐藏";
        }
        $scope.createproject=function(){
            Modal.open('project/create.html',{},{
            	size:'big',
                onClose:function(){
                    Messenger.success('创建完成');
                    init() ;
                }
            });
        }
    }])
	/*创建项目对话框的控制器*/
    .controller('createProjectController',['$scope','ModelFactory','Modal','$http','$timeout','$rootScope','Messenger',function($scope,ModelFactory,Modal,$http,$timeout,$rootScope,Messenger){
    	$scope.fromErp=true;
        var $model=$scope.$model={};
        $model.project=ModelFactory.entity('project');
       
        $scope.projectOwner = {id:$rootScope.subject.userId,userRealname:$rootScope.subject.userRealname};
        $http.post("ws/getAllMembers",{}).success(function(data){
    		$scope.allUsers = data.result;
    	});
        $scope.focusInput = function(){
        	$("#projectOwnerInput").focus();
        }
        
        $scope.fromErpClick = function(){
        	if(!$scope.fromErp){
        		$model.project.code = "";
        		$model.project.contractNo = "";
        		$model.project.name = "";
        	}
        }
        
        $scope.projectNextStep = function(){
        	var selectedErpProject;
        	angular.forEach($scope.erpProjects,function(p){
        		if(p.active)selectedErpProject = p;
        	});
        	if(selectedErpProject){
        		$model.project.code = selectedErpProject.pcode;
        		$model.project.contractNo = selectedErpProject.pcode;//合同编号
        		$model.project.name = selectedErpProject.pname;
        		$scope.fromErp = false;
        		$scope.stepCreatProject = true;
        		$timeout(function(){
        			$("#projectCodeInput").focus();
        			$("#projectContractInput").focus();
            		$("#projectNameInput").focus();
            		$("#projectOwnerInput").focus();
        		},0);
        	}else{
        		Messenger.post("请先选择项目。");
        	}
        }
        $scope.saveProject=function(){
    		$model.project.owner = $scope.projectOwner.id;
            $http.post("ws/project/getProjectOwnerIfExist",{code:$model.project.code}).success(function(data){
            	if(data.result){
                    Messenger.error('项目【'+$model.project.name+'】已被【'+data.result+'】导入,请联系相关负责人加入项目成员即可!') ;
                }else{
                    $model.project.$save(function(){
                        Modal.close();
                    });
                }
            });
        };
        
        $scope.$watch('search.code',function(search){
        	$http.post("ws/erp/projects",{code:search,pageable:{pageSize:10,pageIndex:1}}).success(function(data){
            	$scope.erpProjects = data.result.contents;
            	$scope.total= data.result.total;
            });
        });
        
        $scope.search={};
        
        $scope.selectProject = function(ep){
        	angular.forEach($scope.erpProjects,function(p){
        		p.active = ep==p;
        	});
        }
        $scope.checkboxClick = function(event){
        	event.stopPropagation();
        }
    }]).controller("SupportListController",["$scope","$state","$http","Messenger",function($scope,$state,$http,Messenger){
    	$scope.search = {New:true,Accept:true,Done:true,Closed:false};
    	
    	var getList = function(index){
    		$http.post("ws/support/supportList",{search:$scope.search,pageable:{pageSize:10,pageIndex:index}}).success(function(data){
        		$scope.page = {total:data.result.total};
        		$scope.supportList = data.result.contents;
        	});
    	}
    	$scope.$watch("search",function(v,o){
    		if(!(v.New||v.Accept||v.Done||v.Closed)){
    			Messenger.post("请最少选择一种状态的支持单！");
    			v.New = o.New;
    			v.Accept = o.Accept;
    			v.Done = o.Done;
    			v.Closed = o.Closed;
    		}else getList(1);
    	},true);
    	
    	$http.post("ws/chinacreatorDepartment",{}).success(function(allDept){
    		$scope.supportOrgs = angular.copy(allDept.result);
    		$scope.workOrgs = angular.copy(allDept.result);
		});
    	
    	$scope.deptTreeOptions = {
				data: {
					simpleData: {
						enable: true,
						idKey: "id",
						pIdKey: "pId",
						rootPId: 0
					}
				},
				callback: {
					onClick: zTreeOnClick
				}
		} ;
		
		function zTreeOnClick(event, treeId, treeNode){
			$scope.search.orgId = treeNode.id;
			$scope.org = treeNode;
			getList(1);
		}
		
		$scope.cleanSupportOrg = function(){
			$scope.search.orgId = undefined;
			$scope.org = undefined;
		}
		
		$scope.workTreeOptions = {
				data: {
					simpleData: {
						enable: true,
						idKey: "id",
						pIdKey: "pId",
						rootPId: 0
					}
				},
				callback: {
					onClick: workOnClick
				}
		} ;
		
		function workOnClick(event, treeId, treeNode){
			$scope.search.doOrgId = treeNode.id;
			$scope.worg = treeNode;
			getList(1);
		}
		
		$scope.cleanWorkOrg = function(){
			$scope.search.doOrgId = undefined;
			$scope.worg = undefined;
		}
		
    	$scope.supportStateName = function(state){
    		var name;
    		switch (state) {
			case 0:
				name = "New";
				break;
			case 1:
				name = "Accept";
				break;
			case 2:
				name = "Done";
				break;
			case 3:
				name = "Closed";
				break;

			default:
				break;
			}
    		return name;
    	}
    	
    	$scope.addClick = function(){
    		$state.go("home.addSupport");
    	};
    	
    }]).controller("SupportInfoController",["$scope","$rootScope","$state","$http","$timeout","Messenger",function($scope,$rootScope,$state,$http,$timeout,Messenger){
    	$scope.check = {title:true,orgId:true,projectId:true,doOrgId:true,manageUser:true,checkUser:true,askStartTime:true,askEndTime:true,
    			doUser:true,planStartTime:true,planEndTime:true,
    			success:true,actualStartTime:true,actualEndTime:true,actualWorkload:true,accountWorkload:true};
    	$scope.disabled = {user:true,orgId:true,projectId:true,doOrgId:true,manageUser:true,checkUser:true,askStartTime:true,askEndTime:true,description:true,
    			doUser:true,planStartTime:true,planEndTime:true,
    			success:true,actualStartTime:true,actualEndTime:true,actualWorkload:true,accountWorkload:true,info:true};
    	
    	$http.post("ws/support/supportInfo",{id:$state.params.supportId}).success(function(data){
    		$scope.support = data.result;
    		
    		$scope.role = "normal";
        	if($rootScope.subject.userName == $scope.support.user)$scope.role = "user";
        	if($rootScope.subject.userName == $scope.support.manageUser)$scope.role = "manager";
        	if($rootScope.subject.userName == $scope.support.doUser)$scope.role = "manager";
        	if($rootScope.subject.userName == $scope.support.checkUser)$scope.role = "checker";
        	$scope.checkRole = function(role){
        		var check = false;
        		switch (role) {
				case "user":
					check = $rootScope.subject.userName == $scope.support.user;
					break;
				case "manager":
					check = ($rootScope.subject.userName == $scope.support.manageUser || $rootScope.subject.userName == $scope.support.doUser);
					break;
				case "checker":
					check = $rootScope.subject.userName == $scope.support.checkUser;
					break;

				default:
					break;
				}
        		return check;
        	}
    		
    		if($scope.support.state==0){
    			if($scope.checkRole("user")){
    				angular.extend($scope.disabled,{orgId:false,projectId:false,doOrgId:false,manageUser:false,checkUser:false,askStartTime:false,
    					askEndTime:false,description:false});
    			}
    			if($scope.checkRole("manager")){
    				angular.extend($scope.disabled,{doUser:false,planStartTime:false,planEndTime:false});
    				$scope.needCheck = ["doUser","planStartTime","planEndTime"];
    			}
    		}else{
    			if($scope.checkRole("manager")){
    				angular.extend($scope.disabled,{success:false,actualStartTime:false,actualEndTime:false,actualWorkload:false,accountWorkload:false,info:false});
    				$scope.needCheck = ["success","actualStartTime","actualEndTime","actualWorkload","accountWorkload"];
    			}
    		}
    		$http.post("ws/getMembersByOrgId",{orgId:$scope.support.orgId}).success(function(data){
        		$scope.checkUsers = angular.copy(data.result);//验收负责人
        	});
    	});
    	/*$http.post("ws/getAllMembers",{}).success(function(data){
    		$scope.applyUsers = angular.copy(data.result);
    		$scope.manageUsers = angular.copy(data.result);
    		$scope.checkUsers = angular.copy(data.result);
    		$scope.doUsers = angular.copy(data.result);
    	});*/
    	$http.post("ws/getMembersByWorgOrg",{}).success(function(data){
    		$scope.manageUsers = angular.copy(data.result);//执行负责人
    		$scope.doUsers = angular.copy(data.result);//分配给
    	});
    	$http.post("ws/chinacreatorDepartment",{}).success(function(data){
    		$scope.supportOrgs = angular.copy(data.result);
    		for(var i=0;i<data.result.length;i++){
    			if(data.result[i].id == 'I9eQ139sQxOOnSVfALzPjg'){//基础软件研发中心id
    				$scope.worgName = data.result[i].name;
    			}
    		}
    	});
    	$http.post("e/project/op/select",{}).success(function(data){
    		$scope.supportProjects = data.result;
    	});
    	
    	$scope.ok = function(state,isSubmit){
    		var pass = true;
    		if(isSubmit){
    			angular.forEach($scope.needCheck,function(k){
    				$scope.check[k] = !!$scope.support[k];
    				if(!$scope.check[k])pass = false;
    			});
    		}
    		if(!pass){
    			$scope.passMessageShow = true;
    			$timeout(function(){
    				$scope.passMessageShow = false;
    			},3000);
    		}else{
    			$scope.support.state = state;
    			$http.post("e/support/"+$scope.support.id,$scope.support).success(function(data){
    				Messenger.success("操作成功");
    				if(isSubmit)$state.go("home.support");
    			});
    		}
    	};
    	$scope.goBack = function(){
    		$state.go("home.support");
    	};
    	$scope.deleteSupport = function(id){
    		$http.delete("e/support/"+id).success(function(data){
    			if(data){
    				Messenger.success("操作成功");
    				$state.go("home.support");
    			}
    		});
    	};
    	$scope.selectProject = function(item){
    		$scope.support.projectCode = item.code;
    	}
    }]).controller("SupportAddController",["$scope","$rootScope","$state","$http","$timeout","Messenger",function($scope,$rootScope,$state,$http,$timeout,Messenger){
    	
    	$http.post("ws/chinacreatorDepartment",{}).success(function(allDept){
    		$scope.supportOrgs = angular.copy(allDept.result);
    		$http.post("ws/findUserBelongDepartment",{userId:$rootScope.subject.userId}).success(function(data){
        		$scope.support.orgId = data.result;
        		$scope.org = $scope.supportOrgs.find(o=>o.id==data.result);
        		$http.post("ws/getMembersByOrgId",{orgId:$scope.support.orgId}).success(function(data){
            		$scope.checkUsers = angular.copy(data.result);//验收负责人
            	});
        	});
		});
    	
    	$scope.deptTreeOptions = {
				data: {
					simpleData: {
						enable: true,
						idKey: "id",
						pIdKey: "pId",
						rootPId: 0
					}
				},
				callback: {
					onClick: zTreeOnClick
				}
		} ;
		
		function zTreeOnClick(event, treeId, treeNode){
			$scope.org = treeNode;
			$scope.orgClick(treeNode);
		}
		
    	$http.post("e/project/op/select",{}).success(function(data){
    		$scope.supportProjects = data.result;
    	});
    	$http.post("ws/getMembersByWorgOrg",{}).success(function(data){
    		$scope.manageUsers = angular.copy(data.result);//执行负责人
    	});
    	$scope.support = {doOrgId:'I9eQ139sQxOOnSVfALzPjg',description:"任务描述..."};
    	
    	$scope.orgClick=function(org){
    		$scope.support.orgId = org.id;
    		$scope.cuser=undefined;
    		$http.post("ws/getMembersByOrgId",{orgId:$scope.support.orgId}).success(function(data){
        		$scope.checkUsers = angular.copy(data.result);//验收负责人
        	});
    	}
    	$scope.check = {title:true,orgId:true,projectId:true,doOrgId:true,manageUser:true,/*checkUser:true,askStartTime:true,askEndTime:true*/};
    	$scope.submit = function(){
    		var pass = true;
    		angular.forEach($scope.check,function(v,k){
    			$scope.check[k] = !!$scope.support[k];
    			if(!$scope.check[k])pass = false;
    		});
    		if(!pass){
    			$scope.passMessageShow = true;
    			$timeout(function(){
    				$scope.passMessageShow = false;
    			},3000);
    		}else{
    			$http.post("ws/support/createSupportNote",{support:$scope.support}).success(function(data){
    				if(data.result.id){
    					Messenger.success("支持单创建成功");
    					$state.go("home.support");
    				}else{
    					Messenger.success("支持单创建成功");
    				}
    			});
    		}
    	}
    	$scope.goBack = function(){
    		$state.go("home.support");
    	}
    }]);