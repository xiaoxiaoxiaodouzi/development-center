developmentCenter.controller('teamTaskController',['$scope','$http','Messenger','debounce','$state',
	function($scope,$http,Messenger,debounce,$state){
		$scope.dateButtons = ['thisWeek','nextWeek','preWeek','thisMonth','preMonth','thisYear'];
		$scope.defaultButton = "thisWeek";
		$scope.search = {week:{name: "choose"},NotDone:true,Done:true,Closed:true};
		$scope.dateRange = {start:moment().startOf('week').toDate().getTime(),end:moment().endOf('week').toDate().getTime()};
		$scope.isTouchUser = false;
		
		$http.post("ws/dept/getCurrentUserDeptTree",{deepth:-1}).success(function(data){
			$scope.deptTree = data.result ;
			$scope.selectedDeptNode = $scope.deptTree[0] ;
			
			$scope.$watch("dateRange",function(v){
				if(angular.isUndefined(v)) return ;
				if(angular.isDefined(v.start))teamGroupUsers($scope.selectedDeptNode.id,v.start,v.end);
				$scope.search.week.st = v.start;
				$scope.search.week.et = v.end;
			},true);
		}) ;
		
		//部门用户，过滤白名单，过滤离职日期在选择日期之前的用户
		function teamGroupUsers(orgId,startDate,endDate){
			$http.post("ws/dept/getTeamGroupUsers",{orgId:orgId,startDate:startDate,endDate:endDate}).success(function(data){
				if(!$scope.isTouchUser){
					$scope.search.users = data.result;
				}
				$scope.deptUsers = data.result;
				$http.post("ws/workbench/teamGroupProjects",{search:{users:data.result}}).success(function(data){
					$scope.projectOptions = data.result;
				});
			});
		}
		
		$scope.selectUser=function(item){
			$scope.isTouchUser = true;
		}
		$scope.cleanUsers = function(){
			$scope.isTouchUser = true;
		}
		
		$scope.deptTreeOptions = {
				check:{
					enable : false ,
					chkStyle : "radio" ,
					chkboxType :  { "Y" : "s", "N" : "s" }
				},
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
			$scope.selectedDeptNode = treeNode;
			teamGroupUsers(treeNode.id,$scope.dateRange.start,$scope.dateRange.end);
		}
		
		$scope.$watch("search",function(v){
			debounceSearch(v);
		},true);
		var debounceSearch = debounce(function(searchCondition){
			$http.post("ws/workbench/teamTaskList",{search:searchCondition}).success(function(data){
				var teamTasksGroupByUser = [];
				angular.forEach($scope.search.users,function(user){
					var group = {userRealname:user.userRealname,userIsvalid:user.state,leaveDate:user.pasttime,tasks:[],consumed:0,estimate:0};
					angular.forEach(data.result,function(v){
						if(v.assignedTo == user.userName){
							group.tasks.push(v);
							group.consumed += v.consumed;
							group.estimate += v.estimate;
							if((v.record_date-v.work_date)/60/60/1000>48){
								v.isCollection=true;
							}else{
								v.isCollection=false;
							}
						}
					});
					group.tasks.length>0?teamTasksGroupByUser.unshift(group):teamTasksGroupByUser.push(group);
				});
				$scope.teamTaskList = teamTasksGroupByUser;
			});
		},400);
		
		$scope.$on("topCreateNewTask",function(data){
			debounceSearch($scope.search);
		});
		
		$scope.$on("taskDeleteBo",function(data){
			Messenger.success("任务删除成功");
			debounceSearch($scope.search);
		});
	}
]).controller("teamLogController",['Modal','$rootScope','$scope','$http','Messenger','debounce',function(Modal,$rootScope,$scope,$http,Messenger,debounce){
	$scope.dateButtons = ['thisWeek','preWeek','thisMonth','preMonth','thisYear'];
	$scope.defaultButton = "thisWeek";
	$scope.search = {task:true,lack:false,week:{name:"choose"},overtime:false,overdue:false};
	$scope.dateRange = {start:moment().startOf('week').toDate().getTime()};
	$scope.isTouchUser = false;
	
	function isRoleAssist() {
//		$scope.roleAssist = false;
		$http.post("ws/isRoleAssistant",{userName: $rootScope.subject.userName}).success(function(data) {
			$scope.roleAssist = data.result;
		});
	}
	
	isRoleAssist();
	
	initCurrentUserTree();
	
	function initCurrentUserTree() {
		$http.post("ws/dept/getCurrentUserDeptTree",{deepth:-1}).success(function(currentDept){
			var currentGroupDept = currentDept.result[0];
			$scope.selectedDeptNode = currentGroupDept;
			$http.post("ws/hasLogAdminJob",{}).success(function(adminJob){
				$scope.logAdmin = adminJob.result;
				if(adminJob.result){
					$http.post("ws/dept/getDeptTree",{deepth:-1}).success(function(allDept){
						$scope.deptTree = allDept.result ;
					});
				}else{
					$scope.deptTree = currentDept.result ;
				}
			});
			
			$scope.$watch("dateRange",function(v){
				if(angular.isUndefined(v)) return ;
				teamGroupUsers($scope.selectedDeptNode.id,v.start,v.end);
				$scope.search.week.st = v.start;
				$scope.search.week.et = v.end;
			},true);
		}) ;		
	}
	
	//部门用户，过滤白名单，过滤离职日期在选择日期之前的用户
	function teamGroupUsers(orgId,startDate,endDate){
		$http.post("ws/dept/getTeamGroupUsers",{orgId:orgId,startDate:startDate,endDate:endDate}).success(function(data){
			$scope.deptUsers = data.result;
			if(!$scope.isTouchUser){
				$scope.search.users = data.result;
			}
			$http.post("ws/workbench/teamGroupProjects",{search:{users:data.result}}).success(function(data){
				$scope.projectOptions = data.result;
			});
		});
	}
	
	$scope.initUserTree = function() {
		initCurrentUserTree();
	}
	
	$scope.selectUser=function(item){
		$scope.isTouchUser = true;
	}
	$scope.cleanUsers = function(){
		$scope.isTouchUser = true;
	}
	
	$scope.deptTreeOptions = {
			check:{
				enable : false ,
				chkStyle : "radio" ,
				chkboxType :  { "Y" : "s", "N" : "s" }
			},
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
		$scope.selectedDeptNode = treeNode;
		teamGroupUsers(treeNode.id,$scope.dateRange.start,$scope.dateRange.end);
	}
	
	var debounceSearch = debounce(function(searchCondition){
		$http.post("ws/workbench/teamLogList",{search:searchCondition}).success(function(data){
			var teamLogsGroupByUser = [];
			angular.forEach($scope.search.users,function(user){
				var group = {userRealname:user.userRealname,userIsvalid:user.state,leaveDate:user.pasttime,logs:[],lacklogs:[],lackEstimate:0,consumed:0,overestimate:0};
				angular.forEach(data.result,function(v){
					if(v.account == user.userName){
						if($scope.search.overtime){
							group.logs.push(v);
							if(v.flag){
								group.overestimate += v.overestimate;
							}
						}else if($scope.search.overdue){
							group.logs.push(v);
						}else{
							if(v.taskId){
								group.logs.push(v);
								group.consumed += v.consumed;
							}else{
								group.lacklogs.push(v);
								group.lackEstimate += v.lackEstimate;
							}
						}
					}
				});
				group.consumed = group.consumed>0?(parseInt(group.consumed)==group.consumed?group.consumed:group.consumed.toFixed(1)):0;
				group.lackEstimate = group.lackEstimate>0?(parseInt(group.lackEstimate)==group.lackEstimate?group.lackEstimate:group.lackEstimate.toFixed(1)):0;
				group.overestimate = group.overestimate>0?(parseInt(group.overestimate)==group.overestimate?group.overestimate:group.overestimate.toFixed(1)):0;
				if($scope.search.lack && !$scope.search.task){
					group.lacklogs.length>0?teamLogsGroupByUser.unshift(group):teamLogsGroupByUser.push(group);
				}else{
					group.logs.length>0?teamLogsGroupByUser.unshift(group):teamLogsGroupByUser.push(group);
				}
			});
			$scope.teamLogs = teamLogsGroupByUser;
		});
	},400);
	
	$scope.$watch("search.task",function(v){
		if(v){
			$('#projectDisabled').removeAttr('disabled');
			$scope.search.overtime = false;
			$scope.search.overdue = false;
			$('#overTitle').html("工时");
			$('#overTitle').css('width','85px');
		}
	});
	$scope.$watch("search.lack",function(v){
		if(v){
			$('#projectDisabled').removeAttr('disabled');
			$scope.search.overtime = false;
			$scope.search.overdue = false;
			$('#overTitle').html("工时");
			$('#overTitle').css('width','85px');
		}
	});
	$scope.$watch("search.overtime",function(v){//加班工时不能按项目过滤，无法界定加班时长
		if(v){
			$('#projectDisabled').attr('disabled',true);
			$scope.search.projects = [];
			$scope.search.lack = false;
			$scope.search.task = false;
			$scope.search.overdue = false;
			$('#overTitle').html("<span class='c-red'>加班工时</span>(总工时)");
			$('#overTitle').css('width','115px');
		}
	});
	$scope.$watch("search.overdue",function(v){
		if(v){
			$('#projectDisabled').removeAttr('disabled');
			$scope.search.lack = false;
			$scope.search.task = false;
			$scope.search.overtime = false;
		}
	})
	$scope.$watch("search",function(v){
		debounceSearch(v);
	},true);
	
	$scope.downLoadLackLog=function(){
		if(angular.isDefined($scope.search) && angular.isDefined($scope.search.week)){
			window.location.href= 'report/workLog/lack/' + $scope.search.week.st+"/"+$scope.search.week.et+"/"+JSON.stringify(angular.fromJson("[\""+$scope.selectedDeptNode.id+"\"]"));
		}else{
			Messenger.error("请选择补全查询条件");
		}
	}
	
	$scope.downLoadWorkLog = function(){
		var options = {
				title:'工作量日志导出',
				size:"middle",
				animation:true,
				onDismiss:function(params){
					
				}
		} ;
		Modal.open("home/workbench/teamGroup/worklogdownload.html",{},options) ;
	}
	
	$scope.downLoadOverLog = function(){
		var options = {
				title:'加班日志导出',
				size:"middle",
				animation:true,
				onDismiss:function(params){
					
				}
		} ;
		Modal.open("home/workbench/teamGroup/overlogdownload.html",{},options) ;
	}
	
	//逾期填写日志
	$scope.downLoadOverdue = function(){
		var options = {
				title:'逾期日志导出',
				size:"middle",
				animation:true,
				onDismiss:function(params){
					
				}
		} ;
		Modal.open("home/workbench/teamGroup/overduedownload.html",{},options) ;
	}

	$scope.downLoadProjectsLog = function(){
		var options = {
				title:'工作日志导出',
				size:"middle",
				animation:true,
				onDismiss:function(params){
					
				}
		} ;
		Modal.open("home/workbench/teamGroup/ProjectLogDownload.html",{},options) ;
	}

	
	
	$scope.openManageWhiteModal = function() {
		var url = "home/workbench/teamGroup/whiteListEdit.html";
		Modal.open(url,{},{
  			title:"名单管理",
  			animation:true,
  			size:'big'
  		});
	}
	
	//团队项目任务统计
	$scope.downLoadProjectTask = function(){
		var options = {
				title:'任务统计导出',
				size:"middle",
				animation:true,
				onDismiss:function(params){
					
				}
		} ;
		Modal.open("home/workbench/teamGroup/taskdownload.html",{},options) ;
	}
}])
/*
 * 白名单管理modal
 */
.controller("teamLogWhiteListCtrl",['$scope','$http','Messenger','debounce','Modal','$state','$rootScope',function($scope,$http,Messenger,debounce,Modal,$state,$rootScope){
	$http.post("ws/whiteName/getWhiteList",{}).success(function(data){
		$scope.users = data.result;
	});
	
	$http.post("ws/hasLogAdminJob",{}).success(function(adminJob){
		$scope.logAdmin = adminJob.result;
		$scope.whiteNameTitle = $scope.logAdmin?"全部白名单":"部门白名单";
		if($scope.logAdmin){
			$http.post("ws/whiteName/whiteNameManageTree",{}).success(function(data){
				$scope.deptMemberTree = data.result;
			});
		}
	});
	
	//下拉选择树设置
    $scope.deptMemberTreeOptions = {
        view: {
            selectedMulti: true
        },
        check:{
			enable : true ,
			chkStyle : "checkbox" ,
			chkboxType :  { "Y" : "", "N" : "" },
		},
        data: {
            simpleData: {
                enable: true,
				idKey: "id",
				pIdKey: "pid",
				rootPId: 0
            }
        },
        callback: {
            onCheck: zTreeEditWhiteList
        }
    }
    
	function zTreeEditWhiteList(event, treeId, treeNode){
    	if(treeNode.checked){
    		var name = {userName:treeNode.userName,userRealname:treeNode.name,orgId:treeNode.pid};
    		$http.post("ws/addWhiteName",{name:name}).success(function(data){
    			var orgName = treeNode.getParentNode().name;
    			$scope.users.push({userName:treeNode.userName,userRealname:treeNode.name,orgShowName:orgName});
    			Messenger.success("【"+treeNode.userName+"】成功加入白名单！");
    		});
    	}else{
    		$http.post("ws/deleteWhiteName",{name:treeNode.userName}).success(function(data){
    			angular.forEach($scope.users,function(v,i){
    				if(v.userName == treeNode.userName)$scope.users.splice(i,1);
    			});
    			Messenger.success("【"+treeNode.userName+"】成功从白名单剔除！");
    		});
    	}
	}
}])

.controller("projectLogController",['Modal','$stateParams','$scope','$http','Messenger','debounce','$state',function(Modal,$stateParams,$scope,$http,Messenger,debounce,$state){
	$scope.dateButtons = ['thisWeek','preWeek','thisMonth','preMonth','thisYear'];
	$scope.defaultButton = "thisWeek";
	$scope.search = {task:true,lack:false,week:{name:"choose"},users:[],projects:[{id:$stateParams.project}]};
	$scope.dateRange = {start:moment().startOf('week').toDate().getTime()};
	$scope.toBoardtask = function(){
		$state.go("project.boardtask",{moduleId:$stateParams.moduleId});	
	}

	$scope.toBoardbug = function(){
		$state.go("project.boardbug",{moduleId:$stateParams.moduleId});	
	}
	
	$scope.toBoardStory = function(){
		$state.go("project.boardstory",{moduleId:$stateParams.moduleId});	
	}
	
	$scope.$watch("dateRange",function(v){
		if(angular.isUndefined(v)) return ;
		//查询项目中的成员
		$http.post("ws/getProjectUsers",{projectId:$stateParams.project,startDate:v.start,endDate:v.end}).success(function(data){
			$scope.tempUsers = data.result.users;
			$scope.depts = data.result.depts;
			if(angular.isDefined($scope.dept)){
				$scope.selectDept($scope.dept);
			}else{
				$scope.search.users = data.result.users;
				$scope.projectUsers = data.result.users;
			}
		});
		$scope.search.week.st = v.start;
		$scope.search.week.et = v.end;
	},true);

	$scope.selectDept = function(item){
		$scope.dept = item;
		var users = [];
		for(var i=0;i<$scope.tempUsers.length;i++){
			var orgs = $scope.tempUsers[i].orgIds;
			for(var j=0;j<orgs.length;j++){
				if(orgs[j] == item.id){
					users.push($scope.tempUsers[i]);
					break;
				}
			}
		}
		$scope.projectUsers = users;
		$scope.search.users = users;
	}
	
	$scope.cleanDept = function(){
		$scope.projectUsers = angular.copy($scope.tempUsers);
		$scope.search.users = angular.copy($scope.tempUsers);
	}
	
	var debounceSearch = debounce(function(searchCondition){
		$http.post("ws/getProjectLog",{search:searchCondition}).success(function(data){
			var teamLogsGroupByUser = [];
			angular.forEach($scope.search.users,function(user){
				var group = {userRealname:user.userRealname,userIsvalid:user.state,leaveDate:user.pasttime,logs:[],lacklogs:[],lackEstimate:0,consumed:0,overestimate:0};
				angular.forEach(data.result,function(v){
					if(v.account == user.userName){
						if($scope.search.overtime){
							group.logs.push(v);
							if(v.flag){
								group.overestimate += v.overestimate;
							}
						}else{
							if(v.taskId){
								group.logs.push(v);
								group.consumed += v.consumed;
							}else{
								group.lacklogs.push(v);
								group.lackEstimate += v.lackEstimate;
							}
						}
					}
				});
				group.consumed = group.consumed>0?(parseInt(group.consumed)==group.consumed?group.consumed:group.consumed.toFixed(1)):0;
				group.lackEstimate = group.lackEstimate>0?(parseInt(group.lackEstimate)==group.lackEstimate?group.lackEstimate:group.lackEstimate.toFixed(1)):0;
				group.overestimate = group.overestimate>0?(parseInt(group.overestimate)==group.overestimate?group.overestimate:group.overestimate.toFixed(1)):0;
				if($scope.search.lack && !$scope.search.task){
					group.lacklogs.length>0?teamLogsGroupByUser.unshift(group):teamLogsGroupByUser.push(group);
				}else{
					group.logs.length>0?teamLogsGroupByUser.unshift(group):teamLogsGroupByUser.push(group);
				}
			});
			$scope.teamLogs = teamLogsGroupByUser;
		});
	},400);
	
	$scope.$watch("search",function(v){
		debounceSearch(v);
	},true);
	
	$scope.downLoadWorkLog = debounce(function(){
		if(angular.isDefined($scope.search) && angular.isDefined($scope.search.week)){
			var deptId = "0";
			if(angular.isDefined($scope.dept) && $scope.dept != null)deptId = $scope.dept.id;
			window.location.href= 'report/projectLog/' + $scope.search.week.st+"/"+$scope.search.week.et+"/"+$stateParams.project+"/"+deptId;
		}else{
			Messenger.error("请选择补全查询条件");
		}
	},500) ;
	
}]);
