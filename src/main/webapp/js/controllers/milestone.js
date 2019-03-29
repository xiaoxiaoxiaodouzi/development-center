/*
 * 里程碑列表
 */
developmentCenter.controller('milestoneCtrl',['$scope','$http','$state','$stateParams','$timeout','Messenger','Modal','ModelFactory',
		  function($scope, $http, $state, $stateParams, $timeout, Messenger, Modal, ModelFactory) {
	$scope.initMilestone = function() {
		$scope.pagination = {
			pageIndex: 1,
			pageSize: 5
		};
		$scope.loadMilestoneListForPage(1, $scope.pagination.pageSize);
	}


	$scope.loadMilestoneListForPage = function(pageIndex, pageSize, condition) {

		if (pageIndex <= 0) pageIndex = 1;

		if (!$stateParams.project || $stateParams.project == "") {
			Messenger.post({
				'message': "没有产品参数，页面无法加载数据！",
				'type': 'error',
			});
			return;
		}

		if (undefined == condition || null == condition) {
			condition = {
				product: $stateParams.project
			}
		} else {
			condition.product = $stateParams.project
		}


		$http.post("ws/getMilestoneListForPage", {
			"pageIndex": pageIndex,
			"pageSize": pageSize,
			"condition": condition
		}).success(function(data) {

			$scope.milestoneList = data.result.rows;
			console.log($scope.milestoneList);
			$scope.pagination.pageIndex = pageIndex;
			$scope.pagination.pageSize = pageSize;
			$scope.pagination.totalRow = data.result.totalCount;
			$scope.pagination.totalPage = parseInt(($scope.pagination.totalRow + $scope.pagination.pageSize - 1) / $scope.pagination.pageSize)

		});
	}


	$scope.getPercent = function(close, open) {
		if (undefined == open) {
			open = 0;
		}

		if (undefined == close) {
			close = 0;
		}

		if ((close + open) == 0) {
			return 0;
		}
		return Math.round((close / (close + open)) * 1000) / 10;
	}

	$scope.create = function() {

		if (!$stateParams.project || $stateParams.project == "") {
			Messenger.post({
				'message': "没有产品参数，页面无法加载数据！",
				'type': 'error',
			});
			return;
		}

		Modal.open('project/milestone/add.html', {
			project: $stateParams.project
		}, {
			title: '新增里程碑',
			animation: true,
			onClose: function() {
				$scope.loadMilestoneListForPage($scope.pagination.pageIndex, $scope.pagination.pageSize);
			}
		});
	};

	$scope.delMilestone = function(milestone) {
		$http.post("ws/delMilestone", {
			"milestone": {
				"id": milestone.id
			}
		}).success(function(data, status, headers, config) {
			if (data.result == true) {
				$scope.loadMilestoneListForPage($scope.pagination.pageIndex, $scope.pagination.pageSize);
				Messenger.post({
					'message': "删除成功！",
					'type': 'success',
				});
			} else {
				Messenger.post({
					'message': "里程碑下有关联的任务或需求，不能删除！",
					'type': 'error',
				});
			}

		});
	};
	$scope.updateStatus = function(milestone, status) {
		var doUpdate=function(){
			var entityDataSource = ModelFactory.entity("c2_comunity_milestone", "id", {
				id: milestone.id
			});
			entityDataSource.$promise.then(function(entity) {
				entity.status = status;
				$http.post("ws/updateMilestone", {
					milestone: entity
				}).success(function(data) {
					$scope.loadMilestoneListForPage($scope.pagination.pageIndex, $scope.pagination.pageSize);
				});
			});
		};
		if (status == 2 && milestone.milestoneStoryReport.opened > 0) {
			Messenger.post({
				'message': "里程碑下有需求未关闭，不能关闭里程碑！",
				'type': 'error',
			});
			return;
			Modal.open("project/milestone/close-confirm.html",{},{
				onClose:doUpdate
			});
		}else{
			doUpdate();
		}
		
	};
	$scope.openDetail = function($event, milestoneId) {
		if ($($event.target).is('a')) {
			return;
		}
		$state.go("project.milestoneDetail", {
			milestoneId: milestoneId
		});
	}


	$scope.initMilestone();

	$http.post("ws/isProjectPermitedByBatch", {
		projectId: $stateParams.project,
		permExprs: ["milestone_cud", "advancedSet_menu"]
	}).success(function(data, status, headers, config) {
		$scope.milestoneCUD = data.result["milestone_cud"];
		$scope.advancedSetMenu = data.result["advancedSet_menu"];
	});
}]);

/*
 * 添加里程牌
 */
developmentCenter.controller('milestoneAddCtrl',['$scope','$element','$state','$stateParams','$timeout','$http','Messenger','Modal',
		 function($scope, $element, $state, $stateParams, $timeout, $http, Messenger, Modal) {
	
	var initMilestoneAdd=function(){

	}
	
	$scope.milestone={};
	
	$scope.submit = function() {
		
		if(!$scope.project||$scope.project==""){
			Messenger.post({
		                'message': "没有产品参数，添加失败！",
		                'type': 'error',
		            });
			return;
		}
		
	    if(angular.isUndefined($scope.milestone.title)){
	    	   Messenger.post({
	                'message': "标题不能为空！",
	                'type': 'error',
	            });
	            return;
	    }
	    
	    if(angular.isUndefined($scope.milestone.date)){
	    	   Messenger.post({
	                'message': "时间不能为空！",
	                'type': 'error',
	            });
	            return;
	    }
		
		$http.post("ws/addMilestone",{
			milestone:{
				title:$scope.milestone.title,
				enddate:$scope.milestone.date,
				description:$scope.milestone.desc,
				projectId:$scope.project
			}
		}).success(function(data){
		   Messenger.post({type:'success',message:'添加成功'});
		   Modal.close();   
		});
	};

	$scope.cancel = function() {
		Modal.close();
	};
	
	initMilestoneAdd();
}]);

/*
 * 里程碑详情
 */
developmentCenter.controller('milestoneDetailCtrl',['$scope','$http','$state','$stateParams','$timeout','Messenger','Modal','ModelFactory',
		 function($scope, $http, $state, $stateParams, $timeout, Messenger, Modal, ModelFactory){

	$scope.initMilestoneDetail = function() {
		$scope.milestone = {
			taskDoneNum: 0,
			taskUndoneNum: 0
		};
		$scope.story = {
			opened: 0,
			closed: 0
		};
		$scope.bug = {
			opened: 0,
			closed: 0
		};

		$scope.task = {
			page: {
				pageSize: 20,
				pageIndex: 1
			}
		};

		$scope.story = {
			page: {
				pageSize: 20,
				pageIndex: 1
			}
		}

		$scope.search = {
			Closed: false,
			Done: true,
			NotDone: true,
			week: {
				name: "allWeek"
			},
			milestone: {
				id: $stateParams.milestoneId
			},
			projects: [{
				id: $stateParams.project
			}]
		};

		$scope.milestone.isEditInfo = $stateParams.edit==1;

		$scope.loadMilestoneInfo();

		$scope.loadTaskListByPage($scope.task.page.pageIndex, $scope.task.page.pageSize, $scope.search);

		$scope.loadStoryListByPage($scope.task.page.pageIndex, $scope.task.page.pageSize);

		$scope.getMilestoneTaskReport();

		$scope.getMilestoneStoryReport();

		$scope.getMilestoneBugReport();

		$scope.loadActionHistory();
	}

	$scope.loadingTasks = true;

	/**
	 * 统计里程碑任务信息
	 **/
	$scope.getMilestoneTaskReport = function() {
		$http.post("ws/getMilestoneTaskReport", {
			search: {
				milestoneId: $stateParams.milestoneId,
				projectId: $stateParams.project
			}
		}).success(

		function(data) {
			$timeout(
					function() {
						$scope.loadingTasks = false;
			}, 100)

			if (data.result == undefined) {
				Messenger.post({
					'message': "获取数据出错！",
					'type': 'error',
				});
				return false;
			}

			if (data.result.done) {
				$scope.milestone.taskDoneNum = data.result.done;
			}

			if (data.result.undone) {
				$scope.milestone.taskUndoneNum = data.result.undone;
			}
		});
	}

	$scope.getMilestoneStoryReport = function() {
		$http.post("ws/getMilestoneStoryReport", {
			search: {
				milestoneId: $stateParams.milestoneId,
				projectId: $stateParams.project
			}
		}).success(function(data) {
			$scope.story.opened = data.result.opened;
			$scope.story.closed = data.result.closed;
		});
	}

	$scope.getMilestoneBugReport = function() {
		$http.post("ws/getMilestoneBugReport", {
			search: {
				milestoneId: $stateParams.milestoneId,
				projectId: $stateParams.project
			}
		}).success(function(data) {
			$scope.bug.opened = data.result.opened;
			$scope.bug.closed = data.result.closed;
		});
	}

	$scope.loadMilestoneInfo = function() {

		if (!$stateParams.milestoneId) {
			Messenger.post({
				'message': "参数不正确，无法加载数据！",
				'type': 'error',
			});
			return;
		}

		$http.post("ws/getMilestoneById", {
			"id": $stateParams.milestoneId
		}).success(

		function(data) {
			if (data.result == undefined) {
				Messenger.post({
					'message': "获取数据出错，无法编辑！",
					'type': 'error',
				});
				return false;
			}

			$scope.milestone.id = $stateParams.milestoneId;
			$scope.milestone.title = data.result.title;
			$scope.milestone.bakTitle = data.result.title;
			$scope.milestone.date = data.result.enddate;
			$scope.milestone.desc = data.result.description;
			$scope.milestone.projectId = data.result.projectId;
			$scope.milestone.status = data.result.status;
			$scope.milestoneBak = angular.copy($scope.milestone);
			
			if($scope.milestoneBak.status == '1'){
		    	$scope.$watch("milestone",function(newv){
		    		$scope.isChange=false;
		    		if($scope.milestoneBak.title != $scope.milestone.title || $scope.milestoneBak.date != $scope.milestone.date || $scope.milestoneBak.desc != $scope.milestone.desc){
		    			$scope.isChange=true;
		    		}
		    	},true);
			}
		});

		$scope.editInfo = function(isEdit) {
			$scope.milestone.isEditInfo = isEdit;
		}

		var saveMilestone = function() {
				$http.post("ws/updateMilestone", {
					milestone: {
						id: $scope.milestone.id,
						title: $scope.milestone.title,
						enddate: $scope.milestone.date,
						description: $scope.milestone.desc,
						projectId: $scope.project.id
					},
					comment: $scope.milestone.reason
				}).success(

				function(data) {
					$scope.milestone.bakTitle = $scope.milestone.title;
					Messenger.post({
						type: 'success',
						message: '保存成功'
					});
					$scope.initMilestoneDetail();
					$scope.editInfo(false);
				});
			};

		$scope.saveInfo = function() {
			saveMilestone();
		}

		$scope.cancelInfo = function() {
			$scope.editInfo(false);
			$scope.milestone.title = $scope.milestone.bakTitle;
		}
	}

	$scope.loadTaskListByPage = function(pageIndex, pageSize, searchParam) {
		$http.post("ws/taskList", {
			search: searchParam,
			pageable: {
				pageSize: pageSize,
				pageIndex: pageIndex
			}
		}).success(

		function(data) {
			angular.forEach(
			data.result.contents, function(task) {
				task.nomalAnimate = true;
				angular.forEach(
				task.labels, function(
				label) {
					label.backgroundStyle = {
						"background-color": label.color,
						"border-radius": "2px",
						"color": getTextColorByBackColor(label.color)
					};
				});
			});
			$scope.taskList = data.result.contents;
			$("#taskListDiv").css("height", "auto");
			$scope.task.page.total = data.result.total;
			if ($stateParams.init == "add") {
				$timeout(function() {
					$scope.openNewTask()
				}, 500);
			}
		});
	}

	$scope.pageList = function(pageIndex) {
		if (pageIndex <= 0) pageIndex = 1;
		$scope.loadTaskListByPage(pageIndex, $scope.task.page.pageSize, $scope.search);
	}

	$scope.loadStoryListByPage = function(pageIndex, pageSize) {
		var mileid = $stateParams.milestoneId;
		var projectId = $stateParams.project.id;
		$http.post("ws/getStoryList", {
			condition: {
				project: {
					id: projectId
				},
				milestone: {
					id: mileid
				}
			},
			pageable: {
				pageIndex: pageIndex,
				pageSize: pageSize
			}
		}).success(function(data) {
			$scope.sList = data.result.contents;
			$scope.story.page.total = data.result.total;
		});
	}
	
	$scope.refreshStoryList = function(pageIndex) {
		if (pageIndex <= 0) pageIndex = 1;
		$scope.loadStoryListByPage(pageIndex, $scope.story.page.pageSize);
	}

	$scope.$on("createNewStory",function(data){
		$scope.refreshStoryList(0);
	});
	
	$scope.openEstimateEditModal = function(task) {
		if(task.arcStatus=='0'){
			Messenger.error("项目【"+task.projectName+"】在erp库中不存在，不允许录入日志！");
			return ;
		}
		var statusStr = task.isDone ? "已完成" : "未完成";
		var options = {
			title: '<span class="est-header-name">任务: ' + task.name + '--</span><span class="est-header-status"> (状态 :' + statusStr + ')</span>',
			size: "big",
			animation: true,
			onDismiss: function(params) {
				if (params) {
					$scope.$emit("taskUpdateBo", {
						task: task
					});
					$http.post("message/$all/taskDetailUpdate", {
						id: task.id,
						to: task.userId,
						type: "update",
						socketId: $rootScope.socketId
					});
				}
			}
		};
		Modal.open("project/task/estimate.html", {
			taskId: task.id
		}, options);
	}
	$scope.showAllHistory = false;
	$scope.loadActionHistory = function() {
		var ws = ModelFactory.ws("getMileStoneHistory", {
			milestoneId: $stateParams.milestoneId,
			isAll: $scope.showAllHistory
		});
		ws.$load().then(

		function(data) {
			$(data.data.result).each(

			function(i, o) {
				try{
					o.attrVal = $.parseJSON(o.attrVal);
				}
				catch(e){
					
				}
			});
			$scope.milestoneActionHisArr = data.data.result;
		}).then(function() {
			$scope.initMilestoneDict();
		});
	}
	$scope.initMilestoneDict = function() {
		var ws = ModelFactory.ws("getMileStoneHistoryDict", {});
		ws.$load().then(

		function(data) {
			var milestoneHisDict = $scope.milestoneHisDict = data.data.result;
			var typeEnum = $scope.typeEnum = milestoneHisDict.MilestoneTargetType;
			var colEnum = $scope.colEnum = milestoneHisDict.MilestoneEntity;
			var statusEnum = $scope.statusEnum = milestoneHisDict.MilestoneStatus;

			$scope.displayVal = function(
			attr) {
				var r = undefined;
				if (attr.name == '状态') {
					r = statusEnum[attr.newVal];
				}
				return r;
			};
		});
	}
	$scope.changeHistoryShow = function() {
		$scope.showAllHistory = !($scope.showAllHistory);
		$scope.loadActionHistory();
	}
	$scope.delMilestone = function() {
		$http.post("ws/delMilestone", {
			"milestone": {
				"id": $stateParams.milestoneId
			}
		}).success(function(data, status, headers, config) {
			if (data.result == true) {
				Messenger.post({
					'message': "删除成功！",
					'type': 'success',
				});
				$state.go('project.milestones');
			} else {
				Messenger.post({
					'message': "里程碑下有关联的任务或需求，不能删除！",
					'type': 'error',
				});
			}

		});
	};


	$scope.initMilestoneDetail();
	
	//刷新，检测url当前的位置
	var currentPage = $state.current.url;
	if ('/stories' == currentPage) {
		$scope.addToMilestone = {status:0};		
	} else if ('/tasks' == currentPage) {
		$scope.addToMilestone = {status:1};		
	} else if ('/bugs' == currentPage) {
		$scope.addToMilestone = {status:2};		
	}
	
	$scope.changeList = function(statusParam) {
			$scope.addToMilestone = {status:statusParam};
	}
	
	//需求、任务、bug的添加事件
	$scope.addStuffToMilestone = function() {
		if (0 == $scope.addToMilestone.status) {
			modalTitle = '添加需求';			
			url = 'project/story/add.html';
		} else if (1 == $scope.addToMilestone.status) {
			modalTitle = '添加任务';		
			url = 'home/addTask.html';
		} else if (2 == $scope.addToMilestone.status) {
			modalTitle = '添加Bug';		
			url = 'project/bug/add.html';
		}

		Modal.open(url,{},{
				title:modalTitle,
	  			animation:true,
	  			size:"middle",
	  			onClose:function(params){
					if (0 == $scope.addToMilestone.status) {
						$scope.loadStoryListByPage($scope.story.page.pageIndex,$scope.story.page.pageSize) ;
					} else if (1 == $scope.addToMilestone.status) {
						$scope.loadTaskListByPage($scope.story.page.pageIndex, $scope.task.page.pageSize, $scope.search);
					} else if (2 == $scope.addToMilestone.status) {
						$state.go('project.milestoneDetail.bugList');
					}
				}
		});
	};
}]);

/*
 * 关闭里程碑
 */
developmentCenter.controller('closeMilestoneCtrl',['$scope','$http','$state','$stateParams','$timeout','Messenger','Modal','ModelFactory',
                                    		  function($scope, $http, $state, $stateParams, $timeout, Messenger, Modal, ModelFactory) {
	$scope.onClickClose=function(){
		Modal.close();
	};
}]);	