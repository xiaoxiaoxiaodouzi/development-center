developmentCenter.controller('TaskListController',
	function ($scope, Modal, $http, $stateParams, $timeout, debounce, $state, $rootScope, $ocLazyLoad, socketio, Messenger, $templateCache, $compile) {
		$scope.bugsTaskButton = false;
		var currentUser = { id: $rootScope.subject.userId, name: $rootScope.subject.userRealname, icon: $rootScope.subject.remark1, userName: $rootScope.subject.userName };
		var affixOffset, taskSortWay;
		// 时间
		var thisWeek = { value: "本周完成", estStartDate: moment().isoWeekday(1).toDate().getTime(), deadline: moment().isoWeekday(5).toDate().getTime(), time: moment().isoWeekday(1).format("YYYY/MM/DD") + "—" + moment().isoWeekday(5).format("YYYY/MM/DD") };
		var nextWeek = { value: "下周完成", estStartDate: moment().add(7, 'd').isoWeekday(1).toDate().getTime(), deadline: moment().add(7, 'd').isoWeekday(5).toDate().getTime(), time: moment().add(7, 'd').isoWeekday(1).format("YYYY/MM/DD") + "—" + moment().add(7, 'd').isoWeekday(5).format("YYYY/MM/DD") };
		var unknow = { value: "暂不安排", estStartDate: null, deadline: null, time: "暂不安排" };
		$scope.taskWeek = thisWeek;
		$scope.taskWeeks = [thisWeek, nextWeek, unknow];
		//项目任务OR我的任务
		if ($stateParams.project) {
			//			affixOffset = 187;
			affixOffset = 121;
			$scope.isProjectTasks = true;
			$scope.search = { projects: [{ id: $stateParams.project }], labels: [], users: [], orderWay: "project_task_order", planId: $stateParams.planId };
			taskSortWay = { projectId: $stateParams.project, url: "ws/task/projectTaskOrder" };
			$http.get("e/project/" + $stateParams.project).success(function (data) {
				$scope.currentProject = data;
			});
			//项目统计信息
			$http.post("ws/simpleSelectOne", { statement: "totalProjectTask", parameter: { projectId: $stateParams.project, planId: $stateParams.planId } }).success(function (data) {
				$scope.totalProjectTask = data.result;
			});
			$http.post("ws/simpleSelectOne", { statement: "doneProjectTask", parameter: { projectId: $stateParams.project, planId: $stateParams.planId } }).success(function (data) {
				$scope.doneProjectTask = data.result;
			});
			$http.post("ws/simpleSelectOne", { statement: "sumConsumedProjectTask", parameter: { projectId: $stateParams.project, planId: $stateParams.planId } }).success(function (data) {
				$scope.sumConsumedProjectTask = data.result;
			});

			//条件过滤
			//里程碑
			$http.post("e/c2_comunity_milestone/op/selectMilestone", { projectId: $stateParams.project }).success(function (data) {
				$scope.projectMilestones = data.result;
			});
			/*$http.post("ws/milestone/selectAllUnclosedMilestone",{milestone:{projectId:$stateParams.project}}).success(function(data){
					$scope.projectMilestones = data.result;
				});*/
			//项目成员
			$http.post("ws/getMembers", { projectId: $stateParams.project }).success(function (data) {
				var taskMembers = [];
				angular.forEach(data.result, function (v, n) {
					taskMembers.push({ id: v.userDTO.user_id, name: v.userDTO.user_realname, icon: v.userDTO.remark1, userName: v.userDTO.user_name });
				});
				$scope.projectUsers = taskMembers;
				$scope.bugUsers = angular.copy(taskMembers);
				$scope.changeBugMan = function (item, bug) {
					$http.post("ws/bug/changeBugMan", { bugId: bug.bugId, man: item.userName, changeMan: $rootScope.subject.userName, manRealname: item.name });
				}
			});
			//标签
			$http.post("e/label/op/selectLabel", { project: $stateParams.project }).success(function (data) {
				angular.forEach(data.result, function (v, n) {
					v.backgroundStyle = { "background-color": v.color, "color": getTextColorByBackColor(v.color) };
				});
				$scope.projectLabels = angular.copy(data.result);
			});

		} else {
			affixOffset = 121;
			$scope.isProjectTasks = false;
			$scope.search = { users: [{ userName: currentUser.userName }], projects: [], orderWay: "my_task_order" };
			taskSortWay = { username: currentUser.userName, url: "ws/task/myTaskOrder" };
			//我的任务统计信息
			$http.post("ws/simpleSelectOne", { statement: "totalUserTask", parameter: { userName: currentUser.userName } }).success(function (data) {
				$scope.totalProjectTask = data.result;
			});
			$http.post("ws/simpleSelectOne", { statement: "doneUserTask", parameter: { userName: currentUser.userName } }).success(function (data) {
				$scope.doneProjectTask = data.result;
			});
			$http.post("ws/simpleSelectOne", { statement: "sumConsumedUserTask", parameter: { userName: currentUser.userName } }).success(function (data) {
				$scope.sumConsumedProjectTask = data.result;
			});
			//项目
			$http.post("ws/project/myprojects", {}).success(function (data) {
				$scope.projects = data.result;
				$scope.taskProjects = angular.copy($scope.projects);
			});
		}

		$scope.tabList = [
			{
				title: '需求管理',
				url: 'project.projectPlan.story',
			}, {
				title: $scope.isProjectTasks ? '任务管理' : '我的任务',
				url: 'project.projectPlan.tasks',
			}, {
				title: 'BUG管理',
				url: 'project.projectPlan.bugs',
			},
		]

		$scope.tab = {
			title: $scope.isProjectTasks ? '任务管理' : '我的任务',
			url: 'project.projectPlan.tasks',
		};
		$scope.tabClick = function (item) {
			$state.go(item.url)
		}

		$scope.search.bug = false;
		$scope.$watch("searchCreatorCondition", function (v) {
			$scope.search.creator = v ? $rootScope.subject.userName : null;
		});

		//项目归档状态
		$http.post("ws/getProjectInfo", { projectId: $stateParams.project }).success(function (data) {
			$scope.arcStatus = data.result.arcStatus;
		});

		$http.post("ws/hasLogAdminJob", {}).success(function (adminJob) {
			$scope.logAdmin = adminJob.result;
		});
		//		$scope.roleAssist = false;
		$http.post("ws/isRoleAssistant", { userName: $rootScope.subject.userName }).success(function (data) {
			$scope.roleAssist = data.result;
		});


		//任务列表
		$scope.firstLoad = true;
		$scope.currentIndex = 1;
		$scope.task = { estimate: 8 };
		$scope.page = { pageSize: 10 };
		$scope.pageList = function (index) {
			$scope.currentIndex = index;
			tasksSearch($scope.search, index);
			$timeout(function () { $scope.scrollToTop(); }, 300);
		}




		//排序配置
		$scope.sortableOptions = {
			revert: 200, cursor: "-webkit-grabbing", cancel: "a,input,[ng-click]",
			placeholder: "sortable-placeholder", items: ".lv-item:not(.ui-sort-disabled)",
			update: function (event, ui) {
				var sortTop = ui.position.top - ui.originalPosition.top;
				$timeout(function () {
					taskSortWay.direction = sortTop > 0 ? "down" : "up";
					taskSortOpration($scope.originalOrderTaskList, $scope.taskList);
					//					console.log(taskSortWay.moveTask.name+taskSortWay.direction+"移动到"+taskSortWay.targetTask.name);
					$scope.originalOrderTaskList = angular.copy($scope.taskList);
					$http.post(taskSortWay.url, taskSortWay).success(function () {
						Messenger.success("操作成功！");
					}).error(function () {
						Messenger.success("操作失败！");
					});
				});
			},
			change: function (event, ui) { }
		};

		$scope.downLoadWorkLog = function () {
			if (!angular.isDefined($scope.search.week.et)) {
				$scope.search.week.et = moment().format('X');
			}
			if (angular.isDefined($scope.search) && angular.isDefined($scope.search.week)) {
				var str = JSON.stringify($scope.search)
				window.location.href = 'report/projectsLog/' + $scope.search.week.st + "/" + $scope.search.week.et + "/" + $scope.currentProject.id + "?search=" + str;
			} else {
				Messenger.error("请选择补全查询条件");
			}
		};

		//任务排序找到移动任务和目标任务
		function taskSortOpration(originalTasks, afterOrderTasks, direction) {
			var diffBreak = true;
			angular.forEach(originalTasks, function (ot, i) {
				if (diffBreak && ot.id != afterOrderTasks[i].id) {
					if (taskSortWay.direction == "down") {
						taskSortWay.moveTaskId = ot.id;
						angular.forEach(afterOrderTasks, function (aot, j) {
							if (taskSortWay.moveTaskId == aot.id) taskSortWay.targetTaskId = originalTasks[j].id;
						});
					}
					if (taskSortWay.direction == "up") {
						taskSortWay.targetTaskId = ot.id;
						taskSortWay.moveTaskId = afterOrderTasks[i].id;
					}
					diffBreak = false;
				}
			});
		}

		function getTaskList(searchParam, pageIndex) {
			if ($scope.projectPlan) {//项目计划内的任务列表
				searchParam.week.st = $scope.projectPlan.startDate;
				searchParam.week.et = $scope.projectPlan.endDate;
			}
			$http.post("ws/taskList", { search: searchParam, pageable: { pageSize: $scope.page.pageSize, pageIndex: pageIndex } }).success(function (data) {
				angular.forEach(data.result.contents, function (task) {
					task.nomalAnimate = true;
					angular.forEach(task.labels, function (label) {
						label.backgroundStyle = { "background-color": label.color, "border-radius": "2px", "color": getTextColorByBackColor(label.color) };
					});
				});
				//    		$scope.taskList = replaceWithOldElements(data.result.contents,$scope.taskList,"id");
				$scope.taskList = data.result.contents;
				$scope.originalOrderTaskList = angular.copy($scope.taskList);
				$("#taskListDiv").css("height", "auto");
				$scope.page.total = data.result.total;
			});
		}
		var tasksSearch = function (v, index) {
			//第一次加载进入页面直接加载内容，不做任何延迟处理
			if ($scope.firstLoad) {
				getTaskList(v, index);
				$scope.firstLoad = false;
				return;
			}
			$("#taskListDiv").css("height", $("#taskListDiv").height());
			if (v.NotDone || v.Done || v.Closed) {
				$scope.taskList = null;
				$timeout(function () {
					getTaskList(v, index);
				}, 300);
			} else {
				$scope.taskList = [];
				$timeout(function () {
					$("#taskListDiv").css("height", "auto");
				}, 300);
			}
		}
		var tasksSearchDebounce = debounce(function (v, index) {
			tasksSearch();
		}, 600);

		$scope.debounce = { notLoad: true, comShow: true };
		$scope.search.NotDone = true;
		$scope.search.Done = false;
		$scope.search.Closed = false;
		$scope.search.week = { name: "all" };

		$scope.searchTime = 1;
		$scope.dateButtons = ['thisWeek', 'nextWeek', 'preWeek', 'thisMonth', 'preMonth', 'thisYear'];
		$scope.defaultButton = "thisWeek";
		$scope.dateRange = { show: "时间段", start: moment().startOf('week').toDate().getTime() };

		$scope.$watch("dateRange", function (v) {
			if (angular.isUndefined(v)) return;
			$scope.search.week.st = v.start;
			$scope.search.week.et = v.end;
		}, true);
		$scope.$watch("search", function (v, ov) {
			if ($scope.scrollToTop) $timeout(function () { $scope.scrollToTop(); }, 300);
			tasksSearch(v, 1);
			if ($("#pageEle").controller("c2Pagination")) $("#pageEle").controller("c2Pagination").goPage(1);
			//			getBugs(v);
		}, true);

		var debounceSearchFn = debounce(function (ds) {
			angular.extend($scope.search, angular.copy(ds));
		}, 600);
		$scope.$watch("debounce", function (v) {
			if (v.notLoad) {
				delete v.notLoad;
				return;
			}
			debounceSearchFn(v);
		}, true);


		$scope.searchGroup = function (t) {
			if (t == 1) {
				$scope.search.week = { name: "all" };
			} else if (t == 2) {
				$scope.search.week = { name: "no" };
			} else if (t == 3) {
				//				if($scope.chooseTimeName=="时间段")$scope.chooseTimeName=$scope.dateChooseButtons[0].name;
				//				var st = $("#datetimepickerFrom").data("DateTimePicker").date().toDate().getTime();
				//				var et = $("#datetimepickerTo").data("DateTimePicker").date().toDate().getTime();
				//				$scope.search.week = {name:"choose",st:st,et:et};
				//				searchGroupDateChoose();
			} else if (t == 4) {
				$scope.search.week = { name: "choose" };
				var drdCtrl = $("#rangeDate").controller("c2DateRangeDropdown");
				if ($scope.dateRange.show == "时间段") drdCtrl.setRangeDate("thisWeek");
				else {
					var dateRange = drdCtrl.dateRange;
					$scope.search.week.st = dateRange.start;
					$scope.search.week.ed = dateRange.end;
				}
			}
		}

		//搜索栏固定
		$timeout(function () {
			$("#searchBar").innerWidth($("#searchBarPosition").width());
		}, 0);
		window.onresize = function () {
			$("#searchBar").innerWidth($("#searchBarPosition").width());
		}
		$("#searchBar").affix({
			offset: {
				top: affixOffset
			}
		});

		$ocLazyLoad.load(['assets/js/jquery.scrollTo.min.js']).then(function () {
			$scope.scrollToTop = function () {
				//如果滚动页面超过查询条位置，则滚动到查询条位置
				var toPosition = affixOffset + 2;
				if (window.pageYOffset > toPosition) $(window).scrollTo(toPosition + "px", 300);
			}
		});

		//bug列表
		$scope.bugsParams = { statement: "addToTaskBugs", parameter: $scope.search };
		$scope.bugs = [];
		$scope.bugsSuccess = function (data) {
			$scope.bugs = $scope.bugs.concat(data.result.contents);
			bugCountInfo();
		}

		function bugCountInfo() {
			$http.post("ws/simpleSelectOne", { statement: "taskBugsCount", parameter: { status: 1, projects: $scope.search.projects, users: $scope.search.users } }).success(function (data) {
				$scope.newBugCount = data.result.count;
			});
			$http.post("ws/simpleSelectOne", { statement: "taskBugsCount", parameter: { status: 3, projects: $scope.search.projects, users: $scope.search.users } }).success(function (data) {
				$scope.reopenBugCount = data.result.count;
			});
			$http.post("ws/simpleSelectOne", { statement: "taskBugsCount", parameter: { status: 4, projects: $scope.search.projects, users: $scope.search.users } }).success(function (data) {
				$scope.delayBugCount = data.result.count;
			});
		}

		$scope.acceptBug = function (bug) {
			//			if(!$scope.isProjectTasks)addTaskInit(bug.bugProject);
			//弹窗新建bug任务之后，移除bug并重新计算bug数量
			Modal.open("home/addTask.html", { source: "bug", bug: bug }, {
				title: '新建任务', onClose: function (task) {
					if (task.bugId) {
						var bugData = $("#bugListview").controller("c2Listview").getListViewData();
						var bugIndex = -1;
						angular.forEach(bugData, function (tbug, i) {
							if (tbug.bugId == task.bugId) bugIndex = i;
						});
						if (bugIndex >= 0) bugData.splice(bugIndex, 1);
						bugCountInfo();
					}
					$scope.pageList(1);
				}
			});
		}

		$scope.delayBug = function (bug) {
			Modal.openConfirm({
				title: "BugDelay",
				message: "点击推迟后将更改BUG为<div class='bug-block bug-FixLater m-5'>FixLater</div>状态<br>" +
					"你可以随时<div class='bug-block bgm-green m-5'><i class='md md-play-arrow'></i> 开始</div>" +
					"<div class='bug-block bgm-red m-5'><i class='md md-stop'></i> 拒绝</div>或者" +
					"<div class='bug-block bgm-orange m-5'><i class='md md-panorama-fisheye'></i> 解决</div>该BUG!",
				btnText: "推迟",
				btnClass: "btn-block bgm-lightblue simple-shadow",
				onClose: function () {
					$http.post("ws/bug/changeBugStatus", { bugId: bug.bugId, status: 4 }).success(function () {
						bug.bugStatus = 4;
						bugCountInfo();
					});
				}
			});
		}
		$scope.rejectBug = function (bug) {
			Modal.open("project/task/bugReject.html", { bugId: bug.bugId }, {
				title: "BugReject",
				onClose: function () {
					$("#bugListview").controller("c2Listview").refresh();
				}
			});
		}
		$scope.fixedBug = function (bug) {
			Modal.openConfirm({
				title: "BugFixed",
				message: "点击解决后将不会生成任何关联的任务，也不会有工时信息！",
				btnText: "解决",
				btnClass: "btn-block bgm-lightblue simple-shadow",
				onClose: function () {
					$http.post("ws/bug/changeBugStatus", { bugId: bug.bugId, status: 6 }).success(function () {
						$("#bugListview").controller("c2Listview").refresh();
					});
				}
			});
		}
		$scope.selectedBug = function () {
			$scope.bugsTaskButton = false;
			angular.forEach($scope.bugs, function (b) {
				if (b.selected) $scope.bugsTaskButton = true;
			});
		}

		$scope.openNewTask = function () {
			Modal.open("home/addTask.html", { source: "taskListNewTask", projectPlan: $scope.projectPlan }, {
				onClose: function (data) {
					$scope.pageList(1);
				}
			});
		}

		$scope.closeTask = function (task) {
			$http.post("ws/changeTaskClosed", { task: { id: task.id, closed: true, isConfirm: task.isConfirm } }).success(function () {
				task.closed = true;
				deleteTaskListSingle(task.id);
			});
		}

		$scope.openEstimateEditModal = function (task) {
			if (!task.bugId && task.isConfirm == '0') {
				Messenger.error("任务需确认后才可录工时，请联系项目其他成员！");
				return;
			}
			if (task.arcStatus == '0') {
				Messenger.error("项目【" + task.projectName + "】在erp库中不存在，不允许录入日志！");
				return;
			}
			var statusStr = task.isDone ? "已完成" : "未完成";
			var options = {
				title: '<span class="est-header-name">任务: ' + task.name + '--</span><span class="est-header-status"> (状态 :' + statusStr + ')</span>',
				size: "big",
				animation: true,
				onDismiss: function (params) {
					if (params.isDone) {

						$scope.$emit("taskUpdateBo", { task: task });
						$http.post("message/$all/taskDetailUpdate", { id: task.id, to: task.userId, type: "updateAdd", p: "done", socketId: $rootScope.socketId });
					} else if (params.isChange) {
						$scope.$emit("taskUpdateBo", { task: task });
						$http.post("message/$all/taskDetailUpdate", { id: task.id, to: task.userId, type: "update", socketId: $rootScope.socketId });
					}
				}
			};
			Modal.open("project/task/estimate.html", { taskId: task.id, assignedToRealname: task.userRealname }, options);
		}

		$scope.taskDone = function (task) {
			if (task.isDone == true) {
				task.isDone = false;
				$scope.openEstimateEditModal(task);
			} else {
				if (task.closed || task.bugId) {
					task.isDone = true;
					var msg = task.closed ? "已关闭的任务不能继续开始！" : "BUG关联任务完成后不能开启！";
					Messenger.post(msg);
					return;
				}
				$http.post("ws/changeTaskStatus", { task: { id: task.id, isDone: task.isDone, isConfirm: task.isConfirm } });
				$scope.emit("taskStatusChange");
				$http.post("message/$all/taskDetailUpdate", { id: task.id, to: task.userId, type: "updateAdd", p: "done", socketId: $rootScope.socketId });
			}
		}

		//    	$scope.taskClose = true;
		if ($scope.isProjectTasks) {
			$http.post("ws/isProjectPermitedByBatch", { projectId: $stateParams.project, permExprs: ["advancedSet_menu", "task_close"] }).success(function (data, status, headers, config) {
				$scope.advancedSetMenu = data.result["advancedSet_menu"];
				$scope.taskClose = data.result["task_close"];
			});

			//	    	$http.post("ws/isProjectTaskCreator",{projectId:$stateParams.project}).success(function(data,status,headers,config){
			//	    		$scope.cuTask = data.result;	    		
			//	    	});
		}

		//检查任务是否在当前列表存在
		function checkTaskExistCurrentPage(taskId) {
			var selectIndex = -1;
			angular.forEach($scope.taskList, function (t, i) {
				if (t.id == taskId) {
					selectIndex = i;
				}
			});
			return selectIndex;
		}
		function checkTaskDateCondition(latestTask) {
			var checkDate = false;
			var latestTaskEstStartDate = moment(latestTask.estStartDate);
			if ($scope.search.week.name == "allWeek") checkDate = true;
			if ($scope.search.week.name == "thisWeek" && latestTaskEstStartDate.isBefore(moment().isoWeekday(5))) checkDate = true;
			if ($scope.search.week.name == "nextWeek" && latestTaskEstStartDate.isBefore(moment().add(7, 'd').isoWeekday(5))) checkDate = true;
			if ($scope.search.week.name == "otherWeek" && latestTaskEstStartDate == null) checkDate = true;
			return checkDate;
		}
		function updateTaskAnimate(latestTask, selectIndex) {
			$scope.taskList[selectIndex].nomalAnimate = false;
			$scope.taskList[selectIndex].isUpdateTask = true;
			latestTask.isUpdateTask = true;
			$timeout(function () {
				$scope.taskList[selectIndex] = latestTask;
				$timeout(function () {
					//如果任务完成或者我的任务列表任务变更为其他人的任务，更新后移除
					if ((latestTask.isDone && !$scope.search.Done) || (!$scope.isProjectTasks && latestTask.assignedTo != $rootScope.subject.userName)) {
						deleteTaskAnimate(selectIndex);
					} else {
						latestTask.isUpdateTask = false;
						latestTask.nomalAnimate = true;
					}
				}, 400);
			}, 100);
		}
		function deleteTaskAnimate(selectIndex) {
			$scope.taskList[selectIndex].nomalAnimate = false;
			$scope.taskList[selectIndex].removeAnimate = true;
			$timeout(function () {
				$scope.taskList.splice(selectIndex, 1);
			}, 400);
		}
		function addTaskAnimate(latestTask) {
			latestTask.isNewTask = true;
			$scope.taskList.unshift(latestTask);
			$timeout(function () {
				latestTask.isNewTask = false;
				latestTask.nomalAnimate = true;
			}, 1100);
		}
		//更新列表单个任务
		function updateTaskListSingle(taskId) {
			var selectIndex = checkTaskExistCurrentPage(taskId);
			if (selectIndex != -1) {
				dealProjectTask(taskId, function (latestTask) {
					updateTaskAnimate(latestTask, selectIndex);
				});
			}
		}
		function deleteTaskListSingle(taskId) {
			var selectIndex = checkTaskExistCurrentPage(taskId);
			if (selectIndex != -1 && !$scope.search.Closed) {
				deleteTaskAnimate(selectIndex);
			}
		}
		function addTaskListSingle(latestTask) {
			var checkDate = checkTaskDateCondition(latestTask);
			//项目任务
			if ($scope.isProjectTasks) {
				if (checkDate && latestTask.projectId == $stateParams.project) addTaskAnimate(latestTask);
			} else {//我的任务
				if (checkDate && latestTask.assignedTo == $rootScope.subject.userName) addTaskAnimate(latestTask);
			}
		}
		//获取需要更新的任务
		function dealProjectTask(taskId, callback) {
			$http.post("ws/projectTask", { id: taskId }).success(function (latestTask) {
				if (angular.isUndefined(latestTask.result)) return;
				angular.forEach(latestTask.result.labels, function (label) {
					label.backgroundStyle = { "background-color": label.color, "border-radius": "2px", "color": getTextColorByBackColor(label.color) };
				});
				callback(latestTask.result);
			});
		}

		//接受任务消息推送，如果能够在当前页找到该任务，则更新，如果不能找到，则判断是否为当前用户需要添加的任务
		socketio.on($scope, 'taskDetailUpdate', function (data) {
			//			console.log('监听到任务推送');
			var messageData = angular.fromJson(data);
			if ($rootScope.socketId == messageData.socketId) {
				//			console.log('判断为当前页面推送，不做处理');
				return;
			}
			//更新了任务
			if (messageData.type == "update") {
				updateTaskListSingle(messageData.id);
			}
			//新增了任务
			if (messageData.type == "add") {
				dealProjectTask(messageData.id, function (latestTask) {
					addTaskListSingle(latestTask);
				});
			}
			if (messageData.type == "delete") {
				$scope.$apply(function () {
					deleteTaskListSingle(messageData.id);
				});
			}

			if (messageData.type == "updateAdd") {
				var selectIndex = checkTaskExistCurrentPage(messageData.id);
				if (selectIndex >= 0) {
					updateTaskListSingle(messageData.id);
					return;
				}
				dealProjectTask(messageData.id, function (latestTask) {
					if (messageData.p == "user") {
						if (!$scope.isProjectTasks && latestTask.assignedTo == $rootScope.subject.userName) addTaskAnimate(latestTask);
					} else if (messageData.p == "done") {
						if ($scope.isProjectTasks && latestTask.projectId == $stateParams.project) {
							if (latestTask.isDone) {
								if ($scope.search.Done) addTaskListSingle(latestTask);
							} else {
								if ($scope.search.NotDone) addTaskListSingle(latestTask);
							}
						}
						if (!$scope.isProjectTasks && latestTask.assignedTo == $rootScope.subject.userName) {
							if (latestTask.isDone) {
								if ($scope.search.Done) addTaskListSingle(latestTask);
							} else {
								if ($scope.search.NotDone) addTaskListSingle(latestTask);
							}
						}
					}
				});
			}
		});

		$scope.$on("taskUpdateBo", function (event, msg) {
			updateTaskListSingle(msg.task.id);
		});
		$scope.$on("taskDeleteBo", function (event, msg) {
			deleteTaskListSingle(msg.task.id);
		});
		$scope.$on("taskUpdateAddBo", function (event, msg) {
			var selectIndex = checkTaskExistCurrentPage(msg.task.id);
			dealProjectTask(msg.id, function (latestTask) {
				if (selectIndex != -1) {
					updateTaskAnimate(latestTask, selectIndex);
					return;
				} else {
					addTaskListSingle(latestTask);
				}
			});
		});
		$scope.$on("taskAddBo", function (event, msg) {
			dealProjectTask(msg.id, function (latestTask) {
				if (msg.type == "done") {
					if (latestTask.isDone) {
						if ($scope.search.Done) addTaskListSingle(latestTask);
						else updateTaskListSingle(msg.id);
					} else {
						if ($scope.search.NotDone) addTaskListSingle(latestTask);
						else updateTaskListSingle(msg.id);
					}
				} else if (msg.type = "user") {
					addTaskListSingle(latestTask);
				}
			});
		});

		$scope.$on("flushBugList", function (event, msg) {
			$("#bugListview").controller("c2Listview").refresh();
		});

		$scope.$on("topCreateNewTask", function (event, data) {
			if ($scope.isProjectTasks) {
				if (data.projectId == $stateParams.project) $scope.pageList(1);
			} else {
				if (data.userId == currentUser.id) $scope.pageList(1);
			}
		});

		//		$scope.$on("changeBugState",function(event,msg){
		//			getBugs($scope.search);
		//		});

		//		$scope.$on("aside.closed",function(event,state){
		//		});
		$http.get("ProjectPlan/all/project", {
			params: {
				projectId: $stateParams.project,
			}
		}).success(function (data) {
			var rootNode = {
				id: 0,
				name: $scope.project.name,
			}
			data.push(rootNode)
			$scope.projectPlans = data;
		})

		//plan	
		$scope.treePlanOptions = {
			view: {
				selectedMulti: false
			},
			data: {
				simpleData: {
					enable: true,
					pIdKey: "parent"
				}
			},
			callback: {
				onClick: function (event, treeId, treeNode, clickFlag) {
					$scope.$apply(function () {
						$scope.projectPlan = treeNode;
						if (treeNode.id == 0) {
							$scope.search.planId = null;
						} else {
							$scope.search.planId = treeNode.id;
						}
					});
					// $("#newModuleDropdown").controller("c2-dropdown").toggle();
				}
			}
		};
		$scope.taskDetail = function (taskId) {
			Modal.open('project/task/PlanTaskDetail.html', { taskId: taskId }, {
				onClose: function (data) {
					tasksSearch($scope.search, 1);
				}
			})
		}
	});


developmentCenter
	.controller('taskDetailCtrl', ['$compile', 'markdownConverter', 'debounce', 'Modal', 'ModelFactory', '$state', '$scope', '$http', '$timeout', '$stateParams', '$rootScope', 'Messenger',
		function ($compile, markdownConverter, debounce, Modal, ModelFactory, $state, $scope, $http, $timeout, $stateParams, $rootScope, Messenger) {
			var $params = $scope.$params = {};
			//临时Label数组
			$scope.tempTaskLabels = [];
			$scope.hideUEditor = true;
			//默认值显示部分历史记录
			$scope.showAllHistory = false;
			//label改变状态
			$scope.labelChange = false;
			//全局检索页面链接无$stateParams参数，需要到父scope获取
			var taskId = $scope.taskId ? $scope.taskId : angular.isDefined($stateParams.taskId) ? $stateParams.taskId : $scope.globalSearchParams.taskId;
			//刷新页面后状态参数丢失
			if (angular.isUndefined($stateParams.task) || angular.isUndefined($stateParams.task.id)) {
				$http.post("ws/projectTask", { id: taskId }).success(function (data) {

					$scope.detailTask = data.result;
					if ($scope.detailTask.taskType == 1) $scope.taskType = "普通任务";
					if ($scope.detailTask.taskType == 2) $scope.taskType = "售前任务";
					if ($scope.detailTask.taskType == 3) $scope.taskType = "运维任务";
					$scope.isTaskDone = $scope.detailTask.isDone;
					$scope.defaultTask = angular.copy($scope.detailTask);
					//			initEstStartDateDataTimePicker() ;
					initDS();

				});
			} else {
				$scope.detailTask = $stateParams.task;
				$scope.isTaskDone = $scope.detailTask.isDone;
				$scope.defaultTask = angular.copy($scope.detailTask);
				initEstStartDateDataTimePicker();
				initDS();
			}

			//数据源加载
			function initDS() {
				$scope.projectPlan = $scope.detailTask.projectPlan;
				var allDs = ModelFactory.wrap([
					ModelFactory.ws("getLabels", { taskId: $scope.detailTask.id, projectId: $scope.detailTask.projectId }),
					//ModelFactory.ws("getDoingProjects",{}),
					ModelFactory.ws("getMembers", { projectId: $scope.detailTask.projectId }),
					ModelFactory.ws("task/history", { taskId: $scope.detailTask.id, isAll: $scope.showAllHistory }),
					ModelFactory.ws("isProjectPermitedByBatch", { projectId: $scope.detailTask.projectId, permExprs: ["advancedSet_menu"] })
				]);

				allDs.loadAll().then(function () {

					$scope.taskDel = allDs.datasources[3].result["advancedSet_menu"];//项目负责人权限

					if (angular.isDefined($scope.detailTask.description) && !angular.equals($scope.detailTask.description, "")) {
						$('#markdowntext').replaceWith('<div id="markdowntext" ng-bind-html="detailTask.description | markdown"></div>');
						$compile($('#markdowntext'))($scope);
					} else {
						$('#markdowntext').replaceWith('<div id="markdowntext">添加描述</div>');
					}
					$params.description = $scope.detailTask.description;

					//标签
					angular.forEach(allDs.datasources[0].result.AllLabels, function (v, n) {
						v.backgroundStyle = { "background-color": v.color, "border-radius": "2px", "color": getTextColorByBackColor(v.color) };
					});
					angular.forEach(allDs.datasources[0].result.currentLabels, function (v, n) {
						v.backgroundStyle = { "background-color": v.color, "border-radius": "2px", "color": getTextColorByBackColor(v.color) };
					});
					$scope.tempTaskLabels = allDs.datasources[0].result.currentLabels;
					$scope.taskLabels = allDs.datasources[0].result.currentLabels;
					$scope.allLabels = allDs.datasources[0].result.AllLabels;

					$scope.project = { id: $scope.detailTask.projectId, name: $scope.detailTask.projectName };
					//项目下拉框
					/*var projects =[] ;
					$.each(allDs.datasources[1].result,function(index,ele){
						projects.push({id:ele.id,name:ele.name}) ;
						if(ele.id == $scope.detailTask.projectId){
							$scope.project = {id:ele.id,name:ele.name,owner:ele.owner} ;
						}
					}) ;
					$scope.projects = projects ;*/

					//初始化成员下拉
					var taskMembers = [];
					angular.forEach(allDs.datasources[1].result, function (v, n) {
						if (angular.equals(v.userDTO.user_name, $scope.detailTask.assignedTo)) {
							$scope.taskUser = { id: v.userDTO.user_id, name: v.userDTO.user_realname, icon: v.userDTO.remark1, userName: v.userDTO.user_name };
						}
						taskMembers.push({ id: v.userDTO.user_id, name: v.userDTO.user_realname, icon: v.userDTO.remark1, userName: v.userDTO.user_name });
					});
					$scope.taskUsers = taskMembers;
					if (angular.isUndefined($scope.taskUser)) {
						$http.post("ws/user/getUserByUsername", { name: $scope.detailTask.assignedTo }).success(function (data) {
							var u = data.result;
							$scope.taskUser = { id: u.userId, name: u.userRealname, icon: u.remark1, userName: u.userName };
						});
					}

					//里程碑
					$http.post("ws/milestone/selectAllUnclosedMilestone", { milestone: { "projectId": $scope.detailTask.projectId } }).success(function (data) {
						$.each(data.result, function (index, ele) {
							if (angular.equals($scope.detailTask.milestoneId, ele.id)) {
								$scope.taskMilestone = ele;
							}
						})
						$scope.milestones = data.result;
						$scope.taskActionHisArr = allDs.datasources[2].result;
					});

					//需求
					$http.post("ws/getAllStory", { projectId: $scope.detailTask.projectId }).success(function (data) {
						if (data.result.length > 0) {
							$.each(data.result, function (index, ele) {
								if (angular.equals($scope.detailTask.storyId, ele.id)) {
									$scope.taskStory = ele;
								}
							})
							$scope.storys = data.result;
						} else {
							$scope.storys = [];
						}
					})

					//获取项目关于确认任务的配置
					$http.post("ws/getProjectPreference", { projectId: $scope.detailTask.projectId, preferNames: ["projectTaskConfirmState"] })
						.success(function (data, status, headers, config) {
							if (!angular.isUndefined(data.result)
								&& !angular.isUndefined(data.result.projectTaskConfirmState)
								&& !angular.isUndefined(data.result.projectTaskConfirmState.preferContent)) {
								$scope.taskConfirm = "true" == data.result.projectTaskConfirmState.preferContent;
							} else {
								$scope.taskConfirm = false;
							}
						});

					//模块
					$http.post("e/module/op/selectModule", { projectId: { id: $scope.detailTask.projectId } }).success(function (data) {
						$.each(data.result, function (index, ele) {
							if (angular.equals($scope.detailTask.moduleId, ele.id)) {
								$scope.taskModule = ele;
							}
						});
						$scope.moduleNodes = data.result;
					});
					if ($scope.project) {
						//计划
						$http.get("ProjectPlan/all/project", {
							params: {
								projectId: $scope.project.id,
							}
						}).success(function (data) {
							var rootNode = { id: 0, name: $scope.project.name };
							data.push(rootNode);
							$scope.projectPlans = data;
							$timeout(function () {
								var ztree = $("#taskDetailProjectPlan").controller("c2-dropdown").getTree();
								ztree.expandNode(ztree.getNodes()[0], true, false, true);
							}, 400);
						})
					}

					$scope.selectPrioritys = { title: 3 };
					$scope.allPriorityList = [{ title: 1 }, { title: 2 }, { title: 3 }];

				});
			}

			//确认与取消确认任务
			$scope.confirmTask = function () {
				var confirm = true;
				if ($scope.detailTask.isConfirm == '1') {
					confirm = false;
				}
				if (confirm) {//除自己外的其他人才有权限确认任务
					if ($scope.detailTask.assignedTo == $rootScope.subject.userName) {
						Messenger.error("您不能确认自己的任务，请联系项目其他成员!");
						return;
					} else {
						Modal.delOpenConfirm({
							title: "任务确认",
							message: "任务确认后任务详情信息将无法修改，是否确认【" + $scope.detailTask.name + "】任务？",
							btnText: "确认",
							btnClass: "bgm-lightblue simple-shadow",
							qxText: "取消",
							qxClass: "bgm-lightblue simple-shadow",
							onClose: function () {
								$http.post("ws/changeTaskConfirmed", {
									task: {
										id: $scope.detailTask.id, isConfirm: '1', confirmUser: $rootScope.subject.userName,
										projectId: $scope.detailTask.projectId, milestoneId: $scope.detailTask.milestoneId
									}
								}).success(function () {
									$scope.detailTask.isConfirm = '1';
									//refreshHistory() ;
									$scope.defaultTask = angular.copy($scope.detailTask);
									$scope.$emit("taskUpdateBo", { task: $scope.detailTask });
									Messenger.post("任务【" + $scope.detailTask.name + "】确认成功!");
									$state.go('^');
									$('aside#detail-panel').removeClass('toggled');
								});
							}
						});
					}
				} else {
					if ($scope.taskDel || $scope.detailTask.confirmUser == $rootScope.subject.userName) {//负责人或者确认人
						if ($scope.detailTask.assignedTo == $rootScope.subject.userName) {
							Messenger.error("您不能修改自己的已确认任务，请联系确认者或项目负责人!");
							return;
						} else {
							$http.post("ws/changeTaskConfirmed", { task: { id: $scope.detailTask.id, isConfirm: '0' } }).success(function () {
								$scope.detailTask.isConfirm = '0';
								$scope.defaultTask = angular.copy($scope.detailTask);
								//refreshHistory() ;
								$scope.$emit("taskUpdateBo", { task: $scope.detailTask });
								Messenger.post("任务【" + $scope.detailTask.name + "】修改为待确认状态!");
								$state.go('^');
								$('aside#detail-panel').removeClass('toggled');
							});
						}
					} else {
						Messenger.error("您无权限修改此状态!");
						return;
					}
				}
			}

			//判断是否为任务负责人for delTask
			$scope.isTaskCreator = function () {
				if (angular.isUndefined($scope.detailTask)) return;
				if (angular.equals($rootScope.subject.userName, $scope.detailTask.creator)) {
					return true;
				} else
					return false;
			}

			$scope.projectMenuClick = function (project) {
				$scope.detailTask.projectId = project.id;
				$scope.project = project;
			}

			var saveTaskLabels = debounce(function () {
				var labelTask = [];
				$.each($scope.tempTaskLabels, function (index, ele) {
					labelTask.push({ taskId: $scope.detailTask.id, labelId: ele.id });
				});
				$http.post("ws/updateTaskLabels", { taskId: $scope.detailTask.id, labels: labelTask }).success(function (data) {
					$scope.taskLabels = $scope.tempTaskLabels;
					$scope.labelChange = true;
					$scope.$emit("taskUpdateBo", { task: $scope.detailTask });
					$http.post("message/$all/taskDetailUpdate", { id: $scope.detailTask.id, to: $scope.detailTask.userId, type: "update", socketId: $rootScope.socketId });
				});
			}, 1000);

			var innerPropertyNames = ["$promise", "$resolved", "$q"];
			function countProperty(obj) {
				//只计算非内置变量
				var count = 0;
				angular.forEach(obj, function (value, key) {
					if ($.inArray(key, innerPropertyNames) == -1)
						count++;
				});
				return count;
			};

			function dirty() {
				var dirty = false;
				if (countProperty($scope.detailTask) != countProperty($scope.defaultTask)) {
					return true;
				}
				angular.forEach($scope.defaultTask, function (value, key) {
					if (!angular.equals($scope.detailTask[key], value)) {
						dirty = true;
					}
				});
				return dirty;
			}

			$scope.labelMenuClick = function () {
				saveTaskLabels();
			}
			//时间选择器--截止日期
			$scope.showDeadline = function () {
				$('#deadline').focus();
			}
			$scope.mileStoneMenuClick = function (mileStone) {
				$scope.taskMilestone = mileStone;
				$scope.detailTask.milestoneId = mileStone.id;
				$scope.taskStory = undefined;
				$scope.detailTask.storyId = undefined;
				//需求
				$http.post("ws/getAllStory", { projectId: $scope.detailTask.projectId, milestoneId: $scope.detailTask.milestoneId }).success(function (data) {
					if (data.result.length > 0) {
						$.each(data.result, function (index, ele) {
							if (angular.equals($scope.detailTask.storyId, ele.id)) {
								$scope.taskStory = ele;
							}
						})
						$scope.storys = data.result;
					} else {
						$scope.storys = [];
					}
				})
			}

			//story
			$scope.storyMenuClick = function (story) {
				//选完需求自动将需求的模块选中
				if (story.module) {
					$scope.taskModule = story.module;
				}
				$scope.taskStory = story;
				$scope.detailTask.storyId = story.id;
			}

			$scope.userMenuClick = function (user) {
				user.userRealname = user.name;
				$scope.detailTask.assignedTo = user.userName;
				$scope.detailTask.userId = user.id;
				$scope.detailTask.userRealname = user.name;
				$scope.taskUser = user;
				$scope.detailTask.assigned = user
			}

			$scope.blurIfEnterKeyWasPressed = function ($event) {
				var keyCode = $event.which || $event.keyCode;
				if (keyCode === 13) {
					$('#' + $event.target.id).blur();
				}
			}

			$scope.treeOptions = {
				view: {
					selectedMulti: false
				},
				data: {
					simpleData: {
						enable: true,
						pIdKey: "parent"
					}
				},
				callback: {
					onClick: function (event, treeId, treeNode, clickFlag) {
						if ($scope.detailTask.isConfirm == '1') {
							if ($scope.taskDel && $scope.detailTask.assignedTo == $rootScope.subject.userName) {
								Messenger.error("您不能修改自己的已确认任务，请联系确认者或项目负责人!");
								return;
							} else if ($scope.detailTask.confirmUser != $rootScope.subject.userName) {
								Messenger.error("任务已确认，无法修改！");
								return;
							} else {
								$scope.$apply(function () {
									$scope.taskModule = treeNode;
									$scope.detailTask.moduleId = treeNode.id;
								});
								$("#detailModuleDropdown").controller("c2-dropdown").toggle();
							}
						} else {
							$scope.$apply(function () {
								$scope.taskModule = treeNode;
								$scope.detailTask.moduleId = treeNode.id;
							});
							$("#detailModuleDropdown").controller("c2-dropdown").toggle();
						}
					}
				}
			};

			//plan	
			$scope.treePlanOptions = {
				view: {
					selectedMulti: false
				},
				data: {
					simpleData: {
						enable: true,
						pIdKey: "parent"
					}
				},
				callback: {
					onClick: function (event, treeId, treeNode, clickFlag) {
						$scope.$apply(function () {
							$scope.projectPlan = treeNode;
							$scope.detailTask.projectPlan = treeNode;
						});
						// $("#newModuleDropdown").controller("c2-dropdown").toggle();
					}
				}
			};

			$scope.changeHistoryShow = function () {
				$scope.showAllHistory = !($scope.showAllHistory);
				refreshHistory();
			}

			function changeTaskStatus(task, taskChangeMessage) {
				if (!task.isDone && (task.closed || task.bugId)) {
					$scope.detailTask.isDone = true;
					var msg = task.closed ? "已关闭的任务不能继续开始！" : "BUG关联任务完成后不能开启！";
					Messenger.post(msg);
					return;
				}
				$http.post("ws/changeTaskStatus", { task: { id: task.id, isDone: task.isDone } }).success(
					function (data) {
						refreshHistory();
						taskChangeMessage();
						//如果是bug任务，完成后要更新bug状态
						if (task.isDone && task.bugId) $http.post("ws/bug/changeBugStatus", { bugId: task.bugId, status: 6 });
					}
				);
			}

			$scope.deleteTask = function () {
				if (angular.isDefined($scope.detailTask.bugNo) && !angular.equals($scope.detailTask.bugNo, "")) {
					var msg = "当前任务【" + $scope.detailTask.name + "】已经关联bug，无法删除！";
					Messenger.post(msg);
				} else {
					$http.post("ws/deleteTask", { task: { id: $scope.detailTask.id } }).success(function () {
						Messenger.post("任务【" + $scope.detailTask.name + "】删除成功!");
						$scope.$emit("taskDeleteBo", { task: $scope.detailTask });
						$('#detail-panel').controller('stateAsideContainer').close();//收起侧滑框
						$http.post("message/$all/taskDetailUpdate", { id: $scope.detailTask.id, type: "delete", socketId: $rootScope.socketId });
					});
				}

			}

			function refreshHistory() {
				$http.post("ws/task/history", { taskId: $scope.detailTask.id, isAll: $scope.showAllHistory }).success(
					function (data) {
						$scope.taskActionHisArr = data.result;
					}
				);
			}

			function saveTask(task, taskChangeMessage) {
				var url = "ws/update/task";
				if (task.deadline == null || angular.equals(task.milestoneId, null)) {
					url = "ws/update/taskWithNull";
				}
				//		else if(task.deadline==null || angular.isUndefined(task.deadline)){
				//				task.deadline = addBusinessDays(moment(task.estStartDate),Math.ceil(Number(task.estimate)/8-1)).valueOf() ;
				//				$scope.detailTask.deadline = task.deadline ;
				//				$scope.defaultTask = angular.copy($scope.detailTask) ;
				//		}else if(task.estStartDate==null || angular.isUndefined(task.estStartDate)){
				//				task.estStartDate = subtractBusinessDays(moment(task.deadline),Math.ceil(Number(task.estimate)/8-1)).valueOf() ;
				//				$scope.detailTask.estStartDate = task.estStartDate ;
				//				$scope.defaultTask = angular.copy($scope.detailTask) ;
				//		}
				$http.post(url, { task: task }).success(
					function (data) {
						$scope.detailTask.stageId = data.result.stageId;
						$scope.labelChange = true;
						$scope.$emit("projectPlantaskUpdate", { task: task });
						refreshHistory();
						taskChangeMessage();
					}
				);
			}

			function subtractBusinessDays(date, daysToSubtract) {
				var cnt = 0;
				var tmpDate = moment(date);
				while (cnt < daysToSubtract) {
					tmpDate = tmpDate.subtract('days', 1);
					if (tmpDate.weekday() != 5 && tmpDate.weekday() != 6) {
						cnt = cnt + 1;
					}
				}
				return tmpDate;
			}

			function addBusinessDays(date, daysToAdd) {
				var cnt = 0;
				var tmpDate = moment(date);
				while (cnt < daysToAdd) {
					tmpDate = tmpDate.add('days', 1);
					if (tmpDate.weekday() != 5 && tmpDate.weekday() != 6) {
						cnt = cnt + 1;
					}
				}
				return tmpDate;
			}

			$scope.showUEditor = function () {
				$scope.hideUEditor = true;
			}

			function startEditing($event) {
				if ($($event.target).is('a') || $($event.target).is('img')) {
					return;
				}
				$scope.hideUEditor = false;
			}

			var mousedownPostion = undefined;

			$scope.ueMousedown = function ($event) {
				mousedownPostion = { x: $event.clientX, y: $event.clientY };
			}

			$scope.ueMouseup = function ($event) {
				if (mousedownPostion) {
					var moved = Math.abs($event.clientX - mousedownPostion.x) + Math.abs($event.clientY - mousedownPostion.y);
					if (moved < 10) {
						startEditing($event);
					}
					mousedownPostion = undefined;
				}
			}

			$scope.ueditorBlur = function () {
				if (!angular.equals($scope.detailTask.description, $scope.$params.description)) {
					var description = $scope.$params.description;
					var task = angular.copy($scope.detailTask);
					task.description = description;
					$scope.$emit("taskUpdateBo", { task: task });
					$http.post("ws/updateTaskWithOutRegisAction", { task: { id: $scope.detailTask.id, description: description, isConfirm: $scope.detailTask.isConfirm } }).success(function (data) {
						$scope.hideUEditor = true;
						$scope.detailTask.description = description;
						if (angular.equals(description, "")) {
							$('#markdowntext').replaceWith('<div id="markdowntext">添加描述</div>');
						} else {
							$('#markdowntext').replaceWith('<div id="markdowntext" ng-bind-html="detailTask.description | markdown"></div>');
							$compile($('#markdowntext'))($scope);
						}


					});
				} else {
					$scope.hideUEditor = true;
				}
			}

			/*$scope.$watch('taskMilestone',function(newVal,oldVal){
				if(angular.isDefined(oldVal) && newVal==null){
					$scope.detailTask.milestoneId = null ;
				}
			}) ;*/

			$scope.HistoryExchange = function (actionHis) {
				//里程碑历史记录中没有记录里程碑的名称..
				if (actionHis.detail.indexOf("里程碑") != -1) {
					var id = -1;
					$.each(actionHis.fieldChangeHisArr, function (index, ele) {
						if (angular.equals(ele.field, '里程碑')) {
							id = Number(ele.newVal);
						}
					});
					var title = '""';
					$.each($scope.milestones, function (index, ele) {
						if (angular.equals(ele.id, id)) {
							title = ele.title;
						}
					});
					actionHis.detail = "更新 里程碑为 " + title;
				}
			}

			$scope.$watch('detailTask', function (newValue, oldValue) {
				if (angular.isDefined(oldValue) && dirty() && newValue.isConfirm == oldValue.isConfirm) {
					if (!angular.equals(newValue.description, oldValue.description) || $scope.labelChange) {
						$scope.labelChange = false;//恢复状态
						return;
					}
					if (oldValue.isConfirm == '1') {
						if (($scope.taskDel && oldValue.assignedTo == $rootScope.subject.userName) ||
							(oldValue.confirmUser != $rootScope.subject.userName)) {//负责人不能改自己已确认状态的任务,非确认者不能修改
							$scope.detailTask = angular.copy($scope.defaultTask);
							$scope.isTaskDone = $scope.detailTask.isDone;
							$scope.taskUser = { icon: $scope.detailTask.userIcon, id: $scope.detailTask.userId, name: $scope.detailTask.userRealname, userName: $scope.detailTask.creator };
							if (!$scope.detailTask.milestoneId) {
								$scope.taskMilestone = undefined;
							} else {
								for (var i = 0; i < $scope.milestones.length; i++) {
									if ($scope.detailTask.milestoneId == $scope.milestones[i].id) {
										$scope.taskMilestone = $scope.milestones[i];
										break;
									}
								}
							}
							if (!$scope.detailTask.storyId) {
								$scope.taskStory = undefined;
							} else {
								for (var i = 0; i < $scope.storys.length; i++) {
									if ($scope.detailTask.storyId == $scope.storys[i].id) {
										$scope.taskStory = $scope.storys[i];
										break;
									}
								}
							}
							if ($scope.taskDel && oldValue.assignedTo == $rootScope.subject.userName) {
								Messenger.error("您不能修改自己的已确认任务，请联系确认者或项目负责人!");
								return;
							}
							if (oldValue.confirmUser != $rootScope.subject.userName) {
								Messenger.error("任务已确认，无法修改！");
								return;
							}
						}
					}
					var taskChangeMessage = function () {
						//发送消息到“我的任务框”
						$http.post("message/" + newValue.userId + "/pushTask", {});
						//如果更改了分配人，需要多发一条消息到“另一个任务框”
						if (newValue.assignedTo != oldValue.assignedTo) {
							$scope.$emit("taskUpdateAddBo", { task: newValue, type: "user" });
							$http.post("message/$all/taskDetailUpdate", { id: $scope.detailTask.id, type: "updateAdd", p: "user", socketId: $rootScope.socketId });

							//如果是bug任务，要通知到bug
							if ($scope.detailTask.bugId) $http.post("ws/bug/changeBugMan", { bugId: $scope.detailTask.bugId, man: newValue.assignedTo, changeMan: $rootScope.subject.userName, manRealname: newValue.userRealname });

							$http.post("message/" + oldValue.userId + "/pushTask", {});
							//发送系统消息,如果发送给自己，则不需要发送
							/* if (oldValue.userId != $rootScope.subject.userId) {
								$http.post("ws/pushSystem", {
									msgTo: oldValue.userId,
									msgContent: "[" + $rootScope.subject.userRealname + "]将你的任务[" + oldValue.name + "]分给了" + newValue.userRealname,
									isPersistent: true
								});
							}
							if (newValue.userId != $rootScope.subject.userId) {
								$http.post("ws/pushSystem", {
									msgTo: newValue.userId,
									msgContent: "[" + $rootScope.subject.userRealname + "]将任务[" + $scope.detailTask.name + "]分配给了你:",
									link: "tasks/" + $scope.detailTask.id,
									isPersistent: true
								});
							} */
						} else if (newValue.isDone != oldValue.isDone) {
							$scope.$emit("taskUpdateBo", { task: newValue });
							$http.post("message/$all/taskDetailUpdate", { id: $scope.detailTask.id, type: "updateAdd", p: "done", socketId: $rootScope.socketId });
							$('#detail-panel').controller('stateAsideContainer').close();//收起侧滑框
						} else {
							$scope.$emit("taskUpdateBo", { task: newValue });
							$http.post("message/$all/taskDetailUpdate", { id: $scope.detailTask.id, type: "update", socketId: $rootScope.socketId });
							/* if (newValue.userId != $rootScope.subject.userId) {
								$http.post("ws/pushSystem", {
									msgTo: newValue.userId,
									msgContent: "[" + $rootScope.subject.userRealname + "]修改了你的任务[" + oldValue.name + "]",
									link: "tasks/" + $scope.detailTask.id,
									isPersistent: true
								});
							} */
						}
					}
					//直接编辑newValue会导致对task的监听被触发
					var newVal = angular.copy(newValue);
					if (!angular.equals(newValue.estimate, oldValue.estimate)) {
						newVal.left = newValue.estimate - newValue.consumed;
					}
					$scope.defaultTask = angular.copy($scope.detailTask);
					if (!angular.equals(newValue.isDone, oldValue.isDone)) {
						changeTaskStatus(newVal, taskChangeMessage);
					} else {
						saveTask(newVal, taskChangeMessage);
					}
				}
			}, true);

			$scope.taskDoneClick = function (isTaskDone) {
				if (isTaskDone == false) {
					$scope.detailTask.isDone = !$scope.detailTask.isDone;
				} else {
					$scope.isTaskDone = !$scope.isTaskDone;
					if ($scope.detailTask.arcStatus == '0') {
						Messenger.error("项目【" + $scope.detailTask.projectName + "】在erp库中不存在，不允许创建任务！")
						return;
					}
					if ($scope.detailTask.isConfirm == '0') {
						Messenger.error("任务需确认后才可录工时，请联系项目其他成员确认此任务！");
						return;
					}
					var options = {
						title: '<span class="est-header-name">任务: ' + $scope.detailTask.name + '--</span><span class="est-header-status"> (状态 :未完成)</span>',
						size: "big",
						animation: true,
						onDismiss: function (params) {
							if (params.isDone) {
								$scope.isTaskDone = !$scope.isTaskDone;
								$scope.detailTask.isDone = $scope.isTaskDone;
								$scope.$emit("taskUpdateBo", { task: $scope.detailTask });
								$scope.$emit("projectPlantaskUpdate", { task: newValue });
								$http.post("message/$all/taskDetailUpdate", { id: $scope.detailTask.id, to: $scope.detailTask.userId, type: "updateAdd", p: "done", socketId: $rootScope.socketId });
								$('#detail-panel').controller('stateAsideContainer').close();//收起侧滑框
							} else if (params.isChange) {
								$scope.$emit("taskUpdateBo", { task: $scope.detailTask });
								$scope.$emit("projectPlantaskUpdate", { task: $scope.detailTask });
								$http.post("message/$all/taskDetailUpdate", { id: $scope.detailTask.id, to: $scope.detailTask.userId, type: "update", socketId: $rootScope.socketId });
								refreshHistory();
							}
						}
					};
					Modal.open("project/task/estimate.html", { taskId: $scope.detailTask.id, assignedToRealname: $scope.detailTask.userRealname }, options);
				}
			}

			$scope.createNewTask = function () {

			}

		}]);

developmentCenter
	.controller('taskEstimateCtrl', ['socketio', 'debounce', 'Modal', 'nicescrollService', 'ModelFactory', '$timeout', '$compile', 'Messenger', '$scope', '$http', '$rootScope',
		function (socketio, debounce, Modal, nicescrollService, ModelFactory, $timeout, $compile, Messenger, $scope, $http, $rootScope) {
			Modal.closeParams = { isChange: false };

			$scope.estimates = [];

			var $params = $scope.$params = {};

			$scope.taskID = $scope.$parent.$parent.taskId;

			$scope.assignedToRealname = $scope.$parent.$parent.assignedToRealname;

			dsInit();

			//数据源加载
			function dsInit() {
				var allDs = ModelFactory.wrap([ModelFactory.entity("task", "id", { id: $scope.taskID }),
				ModelFactory.ws("getTaskPreference", { infoName: "showTips" })
				]);

				allDs.loadAll().then(function () {
					$scope.task = allDs.datasources[0];
					if (angular.isUndefined($scope.startDate)) {
						$scope.startDate = moment($scope.task.estStartDate).format('YYYY-MM-DD');
					}
					$params.estStartDateStr = angular.isDefined($scope.task.estStartDate) ? moment($scope.task.estStartDate).format('YYYY-MM-DD') : undefined;
					$params.deadlineStr = angular.isDefined($scope.task.deadline) ? moment($scope.task.deadline).format('YYYY-MM-DD') : undefined;

					getEstimateEvents();
					if (angular.isDefined(allDs.datasources[1].result.showTips)) {
						$scope.showTips = Boolean(allDs.datasources[1].result.infoContent);
					} else {
						$scope.showTips = true;
					}
				});
			}

			socketio.on($scope, 'taskDetailUpdate', function (data) {
				var task = angular.fromJson(data);
				if (angular.equals(task.id, $scope.taskID)) {
					var allDs = ModelFactory.wrap([ModelFactory.entity("task", "id", { id: $scope.taskID })]);
					allDs.loadAll().then(function () {
						task = allDs.datasources[0];
						if (!angular.equals(task.name, $scope.task.name)) {
							$('.est-header-name').text('任务: ' + task.name + '--');
						} else if (!angular.equals(task.isDone, $scope.task.isDone)) {
							var status = task.isDone ? "已完成" : "未完成";
							$('.est-header-status').text('(状态 :' + status + ')');
						}
						$scope.task = task;
						$params.estStartDateStr = angular.isDefined($scope.task.estStartDate) ? moment($scope.task.estStartDate).format('YYYY-MM-DD') : undefined;
						$params.deadlineStr = angular.isDefined($scope.task.deadline) ? moment($scope.task.deadline).format('YYYY-MM-DD') : undefined;
					});
				}
			});

			$scope.removeTips = function () {
				$http.post("ws/setTaskPreference", { preference: { infoName: "showTips", infoContent: "false" } }).success(function (data) {
					$scope.showTips = false;
				});
			}

			$scope.estimateViewRender = function (params) {
				$scope.templateUrl = "project/task/estimate-log.html";
			}

			$scope.estimateEventAfterRender = function (params) {

				var event = params.event;
				var element = params.element;

				event.id = moment(event.start.format('YYYY-MM-DD')).valueOf();
				element.attr("id", event.id);

				let overTime = Number(event.overTime) ? Number(event.overTime) : 0;
				let usualTime = Number(event.usualTime) ? Number(event.usualTime) : 0;
				//计算超时宽度
				let overWidth = 0;
				if ((overTime + usualTime) > 0) {
					overWidth = overTime * 100 / (overTime + usualTime)
				} else {
					overWidth = 100;
				}
				let marginLeft = 35;
				if (Number(event.title) > 9) {
					marginLeft = 30;
				}
				if (event.current) {
					//若当天有其他任务的工时记录
					if (!angular.equals(parseFloat(event.title), parseFloat(event.currentEvent.title))) {
						let curWidth = 0;
						//判断当前时间是否是超时
						if ((event.currentEvent.record - moment(event.currentEvent.start.format('YYYY-MM-DD')).valueOf()) / 60 / 60 / 1000 < 48) {
							if (event.currentTime > 0) {
								//计算当前时间的宽度
								curWidth = Number(event.currentTime) * 100 / (overTime + usualTime + Number(event.currentTime));
							}
						}
						element.before('<span id="progress-title-' + event.id + '" class="est-event-progress-title" style="margin-left:' + marginLeft + 'px">' + event.title + '</span>');
						element.find('.fc-content').replaceWith('<div class="progress-bar" style="background-color:#8CB5D8;width: ' + (100 - overWidth - curWidth) + '%;border-top-left-radius:3px;border-bottom-left-radius: 3px;"></div>' +
							'<div class="progress-bar" style="background-color:#FF9800;width: ' + (overWidth) + '%;border-top-right-radius:3px;border-bottom-right-radius: 3px;"></div>' +
							'<div class="progress-bar" style="background-color:#1B87E4;width: ' + (curWidth) + '%;border-top-right-radius:3px;border-bottom-right-radius: 3px;"></div>');
						element.addClass('est-event-progress');
						$('#progress-title-' + event.id).on("click", function (event) {
							this.nextElementSibling.click();
						});
					} else if (overTime === 0 && parseFloat(event.title) !== 0) {
						element.addClass('est-event-current');
					} else if (parseFloat(event.title) === 0) {
						element.addClass('est-event-progress');
					} else {
						element.addClass('est-event-overtime');
					}

					/* 	//判断是否超过24小时
						if(time>48){
							//若当天有其他任务的工时记录
							if(!angular.equals(event.title,event.currentEvent.title)){
								var other = Number(event.title)-Number(event.currentEvent.title) ;
								var cur = Number(event.currentEvent.title) ;
								var width  = other*(100/(cur+other)) ;
								var marginLeft = 35;
								if(Number(event.title)>9){
									marginLeft = 30 ;
								}
								element.before('<span id="progress-title-'+event.id+'" class="est-event-progress-title" style="margin-left:'+marginLeft+'px">'+event.title+'</span>') ;
								element.find('.fc-content').replaceWith('<div class="progress-bar" style="background-color:#8CB5D8;width: '+width+'%;border-top-left-radius:3px;border-bottom-left-radius: 3px;"></div>'+
								'<div class="progress-bar" style="background-color:#FF9800;width: '+(100-width)+'%;border-top-right-radius:3px;border-bottom-right-radius: 3px;"></div>') ;
								element.addClass('est-event-progress') ;
								$('#progress-title-'+event.id).on("click",function(event){
									this.nextElementSibling.click() ;
								}) ;
							}else{
								element.addClass('est-event-overtime')
							}
						}else{
							//若当天有其他任务的工时记录
								if(!angular.equals(event.title,event.currentEvent.title)){
									var other = Number(event.title)-Number(event.currentEvent.title) ;
									var cur = Number(event.currentEvent.title) ;
									var width  = other*(100/(cur+other)) ;
									var marginLeft = 35;
									if(Number(event.title)>9){
										marginLeft = 30 ;
									}
									element.before('<span id="progress-title-'+event.id+'" class="est-event-progress-title" style="margin-left:'+marginLeft+'px">'+event.title+'</span>') ;
									element.find('.fc-content').replaceWith('<div class="progress-bar" style="background-color:#8CB5D8;width: '+width+'%;border-top-left-radius:3px;border-bottom-left-radius: 3px;"></div>'+
									'<div class="progress-bar" style="background-color:#1B87E4;width: '+(100-width)+'%;border-top-right-radius:3px;border-bottom-right-radius: 3px;"></div>') ;
									element.addClass('est-event-progress') ;
									$('#progress-title-'+event.id).on("click",function(event){
										this.nextElementSibling.click() ;
									}) ;
								}else{
									element.addClass('est-event-current') ;
								}
						} */
				} else {
					element.before('<span id="progress-title-' + event.id + '" class="est-event-progress-title" style="margin-left:' + marginLeft + 'px">' + event.title + '</span>');
					element.find('.fc-content').replaceWith('<div class="progress-bar" style="background-color:#8CB5D8;width: ' + (100 - overWidth) + '%;border-top-left-radius:3px;border-bottom-left-radius: 3px;"></div>' +
						'<div class="progress-bar" style="background-color:#FF9800;width: ' + (overWidth) + '%;border-top-right-radius:3px;border-bottom-right-radius: 3px;"></div>');
					element.addClass('est-event-progress');
					$('#progress-title-' + event.id).on("click", function (event) {
						this.nextElementSibling.click();
					});
				}
			}

			$scope.estimateDayClick = function (params) {
				$scope.templateUrl = "project/task/estimate-log.html";
				//此处的date为时间
				var date = params.event.format('YYYY-MM-DD');
				var id = moment(date).valueOf();
				if ($('#' + id).length == 0) {
					if (moment(date).diff(moment(), 'days') == 0) {
						$('#calendar').fullCalendar('addEventSource', [{ id: id, className: "est-event-current-null", "start": date, "title": 0 }]);
					} else {
						$('#calendar').fullCalendar('addEventSource', [{ id: id, className: "est-event-null", "start": date, "title": 0 }]);
					}
				}
				var element = $('#' + id);
				//若返回null表示该日期已有当前任务工时记录
				var event = getEventByStart(date);
				if (!angular.equals(event, null) && event.current == true) {
					event.id = id;
					$scope.estimateEventClick({ event: event });
				} else {
					if (angular.isUndefined($scope.estimates[id])) {
						$scope.estimates[id] = {
							taskId: $scope.task.id,
							workDate: id,
							recordDate: moment().valueOf(),
							consumed: getConsumed(event)
						};
					}
					showPopOver(id, '录入工时');
				}
			}

			function getEventByStart(date) {
				var resultEvent = null;
				$.each($scope.estimateEvents, function (index, ele) {
					if (angular.equals(date, ele.start)) {
						resultEvent = ele;
					}
				});
				return resultEvent;
			}

			$scope.estimateEventClick = function (params) {
				var event = params.event;
				var id = event.id;
				var element = $('#' + id);

				if (angular.equals(params.event.className[0], 'est-event-null')) {
					initPop(element, id, '录入工时');
				} else if (event.current == true) {
					$scope.estimates[id] = {
						id: event.currentEvent.id,
						taskId: $scope.task.id,
						workDate: id,
						recordDate: moment().valueOf(),
						consumed: Number(event.currentEvent.title),
						description: event.currentEvent.description
					}

					showPopOver(id, '修改工时');

				} else {
					var consumed = 7.5;
					if (event.title <= 7.5) {
						consumed = 7.5 - event.title;
					}
					if (angular.isUndefined($scope.estimates[id])) {
						$scope.estimates[id] = {
							taskId: $scope.task.id,
							workDate: id,
							recordDate: moment().valueOf(),
							consumed: getConsumed(event)
						};
					}
					showPopOver(id, '录入工时');
				}

			}

			function getConsumed(event) {
				var consumed = $scope.task.left > 7.5 ? 7.5 : $scope.task.left;
				return consumed;
			}

			$scope.showEstimateEvent = function (event) {
				var id = moment(event.start).valueOf();
				var element = $('#' + id);
				if (element.length == 0) {
					$scope.startDate = event.start;
					getEstimateEvents(function () { $timeout(function () { $scope.showEstimateEvent(event) }, 200); });
				} else {
					$scope.estimates[id] = {
						id: event.id,
						taskId: $scope.task.id,
						workDate: id,
						recordDate: moment().valueOf(),
						consumed: Number(event.title),
						description: event.description
					}
					showPopOver(id, '修改工时');
				}
			}

			function hidePopOver(id) {
				var element = $('#' + id);
				element.popover("hide");
				$scope.otherEstimateInfos = [];
			}

			function showPopOver(id, title) {

				var element = $('#' + id);
				if (angular.isDefined($scope.currentEventId)) {
					if (angular.equals($scope.currentEventId, id)) {
						$scope.togglePopOver($scope.currentEventId);
					} else {
						hidePopOver($scope.currentEventId);
						$http.post("ws/getEstimatesInfo", { estimate: { workDate: moment(id).format('YYYY-MM-DD'), account: $scope.task.assignedTo, taskId: $scope.taskID } }).success(function (data) {
							$scope.otherEstimateInfos = data.result;
							initPop(element, id, title);
						});
					}
				} else {
					$http.post("ws/getEstimatesInfo", { estimate: { workDate: moment(id).format('YYYY-MM-DD'), account: $scope.task.assignedTo, taskId: $scope.taskID } }).success(function (data) {
						$scope.otherEstimateInfos = data.result;
						initPop(element, id, title);
					});
				}
			}

			$scope.changeTaskCalendar = function (taskId, workDate) {
				$scope.taskChangeAnimateFlag = true;
				$scope.taskID = taskId;
				var allDs = ModelFactory.wrap([ModelFactory.entity("task", "id", { id: $scope.taskID })
				]);

				allDs.loadAll().then(function () {
					Modal.closeParams.isChange = false;
					$scope.task = allDs.datasources[0];
					$scope.startDate = moment($scope.task.estStartDate).format('YYYY-MM-DD');
					$params.estStartDateStr = angular.isDefined($scope.task.estStartDate) ? moment($scope.task.estStartDate).format('YYYY-MM-DD') : undefined;
					$params.deadlineStr = angular.isDefined($scope.task.deadline) ? moment($scope.task.deadline).format('YYYY-MM-DD') : undefined;

					getEstimateEvents(function () {
						$scope.estimateDayClick({ event: moment(workDate) });
						var status = $scope.task.isDone ? "已完成" : "未完成";
						$('.modal-title').replaceWith('<h4 class="modal-title"> <span class="est-header-name">任务: ' + $scope.task.name + '--</span><span class="est-header-status"> (状态 :' + status + ')</span></h4>');
						$scope.taskChangeAnimateFlag = false;
					});

				});
			}

			function initPop(element, index, title) {

				var funcName = 'saveEstimate';

				if (angular.equals(title, '修改工时')) {
					funcName = 'updateEstimate';
				}

				$scope.currentEventId = index;

				var otherEstimateInfosHtml = "";
				if ($scope.otherEstimateInfos.length > 0) {
					otherEstimateInfosHtml = '<h3 class="est-popover-title"><span style="font-family: 微软雅黑;">其他任务工时信息 (' + moment(index).format("YYYY-MM-DD") + ')</span></h3>' +
						'<div class="p-10" style="border-bottom: 1px solid #eee;font-family: 微软雅黑;">' +
						'<div class="row p-l-10 est-popover-list" ng-repeat="estimateInfo in otherEstimateInfos" ng-click="changeTaskCalendar(estimateInfo.taskId,estimateInfo.workDate)">' +
						'<div class="col-sm-9 est-popover-list-info" >{{estimateInfo.taskName}}</div>' +
						'<div class="col-sm-1">{{estimateInfo.consumed}}h</div>' +
						'</div>' +
						'</div>';
				}

				var templateHtml = $compile(otherEstimateInfosHtml +
					'<h3 class="est-popover-title">' +
					'<div style="width: 20%; float: left;"><span style="font-family: 微软雅黑;font-weight: 600;">' + title + '</span>' +
					'</div>' +
					'<div style="white-space: nowrap; overflow: hidden; text-overflow: ellipsis;">' +
					'<span style="font-family: 微软雅黑;">当前任务: ' + $scope.task.name + '</span>' +
					'</div></h3>' +
					'<div id="est-popover-template_' + index + '" style="margin:10px 0px 10px 5px;">' +
					'<div class="row" style="width: 480px; padding-left: 20px;">' +
					'<div class="col-sm-3" style="padding: 0">' +
					'<div class="input-group">' +
					'<span class="input-group-addon" style="padding-right: 0px;"><i ' +
					'class="md md-access-time"></i></span>' +
					'<div class="fg-line">' +
					'<input id="estimate_' + index + '" type="number" class="form-control" ng-model="estimates[' + index + '].consumed" placeholder="工时(h)" ng-trim="true" min="0"  required="required" >' +
					'</div>' +
					'<span class="input-group-addon last"><i ' +
					'class="md md-description"></i></span>' +
					'</div>' +
					'</div>' +
					'<div class="col-sm-8" style="padding: 0">' +
					'<div class="input-group">' +
					'<div class="fg-line">' +
					'<input  type="text" class="form-control" ng-model="estimates[' + index + '].description" placeholder="工作内容(备注)">' +
					'</div>' +
					'<span class="input-group-addon last"><i class=""></i></span>' +
					'</div>' +
					'</div>' +
					'<div class="col-sm-1" style="padding: 0;">' +
					'<div class="input-group" style="margin-top: 10px;">' +
					'<a href="javascript:void(0)" class="input-group-addon" ng-click="' + funcName + '(' + index + ')">' +
					'<i class="md md-check c-green"></i>' +
					'</a> <a href="javascript:void(0)" class="input-group-addon" ng-click="togglePopOver(' + index + ')">' +
					'<i class="md md-close c-red"></i>' +
					'</a>' +
					'</div></div></div></div>')($scope);

				element.popover({
					html: true,
					content: function () {
						return templateHtml;
					},
					placement: "top",
					animation: true,
					trigger: "manual"
				});
				$timeout(function () {
					element.popover("toggle");
					$("#est-popover-template_" + index).keyup(function (event) {
						if (event.which === 13) {
							$scope[funcName](index);
						}
					});
				}, 100);


			}

			$scope.togglePopOver = function (id) {
				var element = $('#' + id);
				element.popover("toggle");
			}

			function getEstimateEvents(func) {
				var now = moment($scope.startDate);

				var startMonth = now.get('month') - 1;
				var startYear = now.get('year');
				if (startMonth == -1) {
					startYear = startYear - 1;
					startMonth = 12;
				} else {
					startMonth = startMonth + 1;
				}
				var start = startYear + '-' + startMonth + '-' + now.get('date');
				var endMonth = now.get('month') + 1;
				var endYear = now.get('year');
				if (endMonth == 12) {
					endYear = startYear + 1;
					endMonth = 1;
				} else {
					endMonth = endMonth + 1;
				}
				var end = endYear + '-' + endMonth + '-' + now.get('date');
				$http.post("ws/getCalenderEstimateEvents", { conditions: { "start": start, "end": end, "assignedTo": $scope.task.assignedTo, taskId: $scope.task.id } }).success(
					function (data) {
						$scope.estimateList = data.result.estimateList;
						let ary = [];
						for (let i in data.result.estimateAry) {
							let query = Object.assign({}, data.result.estimateAry[i][0])
							//设置初始超时时间
							query.overTime = 0;
							//设置初始其他时间
							query.usualTime = 0;
							//设置初始当前时间
							query.currentTime = 0;
							//设置标题
							query.title = 0;
							data.result.estimateAry[i].forEach(item => {
								//遍历每一条数据，判断每条数据的录入时间是否是补录的,然后分别存入到query中
								if ((item.record - moment(item.start.format('YYYY-MM-DD')).valueOf()) / 60 / 60 / 1000 > 48) {
									query.overTime += parseFloat(item.title)
								} else {
									query.usualTime += parseFloat(item.title)
								}
								if (item.current) {
									query.currentTime = parseFloat(item.currentEvent.title);
								}
								query.title += parseFloat(item.title);
							})

							query.title = query.title > 0 ? (parseInt(query.title) === query.title ? query.title : query.title.toFixed(1)) : 0;

							ary.push(query);
						}
						/* 
											for(var i=0;i<data.result.estimateEvents.length;i++){
						
												
												var title = parseFloat(data.result.estimateEvents[i].title);
												title = title>0?(parseInt(title)==title?title:title.toFixed(1)):0;
												
												data.result.estimateEvents[i].title = title;
												if(((data.result.estimateEvents[i].record-moment(data.result.estimateEvents[i].start.format('YYYY-MM-DD')).valueOf())/60/60/1000)>24){
													data.result.estimateEvents[i].className='est-event-overtime'
												}
						
						
											} */
						$scope.estimateEvents = ary;
						$scope.estimates = [];
						$scope.currentEventId = null;
						if (angular.isDefined(func)) {
							$timeout(func, 500);
						}
					}
				);
			}

			function checkTaskStatus() {
				if ($scope.task.isDone) {
					Messenger.error("任务已完成,请启用任务!");
					return false;
				}
				return true;
			}

			$scope.saveEstimate = function (index) {

				if (checkTaskStatus() && angular.isDefined($scope.estimates[index]) && checkEstimate(index)) {
					var sum = 0;
					$http.post("ws/getEstimateByWorkDate", { estimate: { workDate: moment(index).format('YYYY-MM-DD'), account: $scope.task.assignedTo } }).success(function (data) {
						sum = sum + data.result;
						sum = sum + $scope.estimates[index].consumed;
						if (sum > 24) {
							Messenger.post('对不起，您【' + moment(index).format('YYYY-MM-DD') + '】录入工时已超过24小时，请重新确认工作量!');
						} else {
							$('#' + index).popover('destroy');
							$scope.currentEventId = null;
							saveEstimate(index);
						}
					});
				}
			}

			var saveEstimate = debounce(function (index) {//工时录入出现一位小数以上时直接进位
				$scope.estimates[index].consumed = $scope.estimates[index].consumed.toFixed(1);
				$http.post("ws/saveEstimate", { estimate: $scope.estimates[index] }).success(function (data) {
					Modal.closeParams.isChange = true;
					dsInit();
				});
			}, 150);

			$scope.changeTaskStatus = function () {
				var isDone = !$scope.task.isDone;

				if (!isDone && ($scope.task.closed || $scope.task.bugId)) {
					var msg = $scope.task.closed ? "已关闭的任务不能继续开始！" : "BUG关联任务完成后不能开启！";
					Messenger.post(msg);
					return;
				}
				$http.post("ws/changeTaskStatus", { task: { id: $scope.task.id, isDone: isDone } }).success(
					function (data) {
						if (isDone) {
							Modal.closeParams.isDone = true;
							Modal.dismiss(Modal.closeParams);
							//如果是bug任务，完成后要更新bug状态
							if ($scope.task.bugId) $http.post("ws/bug/changeBugStatus", { bugId: $scope.task.bugId, status: 6 });
						} else {
							$scope.task.isDone = isDone;
							$('.est-header-status').text("(状态 :未完成)");
							$http.post("message/$all/taskDetailUpdate", { id: $scope.task.id, to: $scope.task.userId, type: "updateAdd", p: "done", socketId: $rootScope.socketId });
						}
					}
				);
			}

			$scope.cancel = function () {
				Modal.dismiss(Modal.closeParams);
			}

			$scope.estimateEventAfterAllRender = function (params) {
				if ($('.est-prev-button').length == 0) {
					var prevButtonHtml = $compile('<a class="fc-prev-button fc-button fc-state-default fc-corner-left fc-corner-right est-prev-button" ng-click="prevClick()">' +
						'<span class="fc-icon fc-icon-left-single-arrow">' +
						'</span></a>'
					)($scope);
					$('.fc-prev-button').replaceWith(prevButtonHtml);

					var nextButtonHtml = $compile('<a class="fc-next-button fc-button fc-state-default fc-corner-left fc-corner-right est-next-button" ng-click="nextClick()">' +
						'<span class="fc-icon fc-icon-right-single-arrow">' +
						'</span></a>'
					)($scope);
					$('.fc-next-button').replaceWith(nextButtonHtml);
				}
			}

			$scope.prevClick = function () {
				var now = moment($scope.startDate);
				var month = now.get('month') - 1;
				var year = now.get('year');
				if (month == -1) {
					month = 11;
					now.year(year - 1);
				}
				now.month(month);
				now.date(1);
				$scope.startDate = now.format('YYYY-MM-DD');
				getEstimateEvents(false);
			}

			$scope.nextClick = function () {
				var now = moment($scope.startDate);
				var month = now.get('month') + 1;
				var year = now.get('year');
				if (month == 12) {
					month = 0;
					now.year(year + 1);
				}
				now.month(month);
				now.date(1);
				$scope.startDate = now.format('YYYY-MM-DD');
				getEstimateEvents(false);
			}

			$scope.removeEstimateEvent = function (event) {
				if (checkTaskStatus()) {
					$scope.estimates[moment(event.start).valueOf()] = undefined;
					$.each($scope.estimateList, function (index, ele) {
						if (angular.equals(event.start, ele.start)) {
							setTimeout(function () {
								$scope.$apply(
									function () {
										delete $scope.estimateList[ele.start];
									}
								);
							}, 0);
							$http.post("ws/deleteEstimate", { estimate: { id: event.id, taskId: $scope.task.id, consumed: Number(event.title), workDate: moment(event.start) } }).success(function (data) {
								Modal.closeParams.isChange = true;
								dsInit();
							});
						}
					});

				}
			}

			var updateEstimate = debounce(function (index) {//工时录入出现一位小数以上直接进位
				$scope.estimates[index].consumed = $scope.estimates[index].consumed.toFixed(1);
				$http.post("ws/updateEstimate", { estimate: $scope.estimates[index] }).success(function (data) {
					Modal.closeParams.isChange = true;
					dsInit();
				});
			}, 150);

			$scope.updateEstimate = function (index) {
				if (checkTaskStatus() && angular.isDefined($scope.estimates[index])) {
					var sum = 0;
					$http.post("ws/getEstimateByWorkDate", { estimate: { workDate: moment(index).format('YYYY-MM-DD'), account: $scope.task.assignedTo, taskId: $scope.taskID } }).success(function (data) {
						sum = sum + data.result;
						sum = sum + $scope.estimates[index].consumed;
						if (sum > 24) {
							Messenger.post('对不起，您【' + moment(index).format('YYYY-MM-DD') + '】录入工时已超过24小时，请重新确认工作量!');
						} else {
							$('#' + index).popover('destroy');
							$scope.currentEventId = null;
							updateEstimate(index);
						}
					});
				}
			}

			function checkEstimate(index) {
				if (angular.isUndefined($scope.estimates[index].consumed)
					|| angular.equals($scope.estimates[index].consumed, 0)) {
					Messenger.error("工时数不能为空!");
					return false;
				}
				return true;
			}


		}]);






developmentCenter
	.controller('taskAddCtrl', ['$stateParams', '$compile', 'markdownConverter', 'Modal', 'ModelFactory', '$state', '$scope', '$http', '$rootScope', 'Messenger', '$timeout',
		function ($stateParams, $compile, markdownConverter, Modal, ModelFactory, $state, $scope, $http, $rootScope, Messenger, $timeout) {
			var $params = $scope.$params = {};

			//临时Label数组
			$scope.tempTaskLabels = [];
			$scope.hideUEditor = true;
			$scope.filterOnly = false;
			$scope.newTask = { taskType: 1, estimate: 7.5, deadline: moment().day(5).valueOf() };
			$scope.newTask.description = "";

			if ($scope.$parent.$parent.source == "bug") {
				var bug = $scope.$parent.$parent.bug;
				$scope.newTask.name = "修复"

				$scope.taskUser = { id: bug.userId, name: bug.userRealname, icon: bug.userIcon, userName: bug.userName };
				$scope.newTask.name = "修复BUG:" + bug.bugTitle;
				$scope.newTask.bugId = bug.bugId;
				$scope.newTask.bugNo = bug.bugNo;

				$scope.taskTypeDisabled = true;
				$scope.taskProjectDisabled = true;
				$scope.taskContinueButtonHide = true;
				$scope.project = { id: bug.bugProject, name: bug.bugProjectName };
				//			if(bug.bugStatus==4)$scope.taskWeek = unknow;
			} else if ($scope.$parent.$parent.source == "story") {
				var story = $scope.$parent.$parent.story;
				$scope.newTask.storyId = story.id;
				$scope.newTask.description = "[任务来源【需求：" + story.title + "】](#/project/" + story.project.id + "/story/" + story.id + ") ";
				$scope.description = "[任务来源【需求：" + story.title + "】](#/project/" + story.project.id + "/story/" + story.id + ") ";
				//        	$scope.hideUEditor = false;
				//需求添加任务应默认显示模块、里程碑、标签信息
				loadStoryAndComments();
				$scope.taskMilestone = story.milestone;
				$scope.taskModule = story.module;
				$scope.taskStory = story;
			}

			function loadStoryAndComments() {
				$http.post("ws/getStoryAndComments", {
					"storyId": $scope.newTask.storyId
				}).success(function (data) {
					if (data.result.storyLabel)
						$scope.tempTaskLabels = data.result.storyLabel;
				});
			};

			$scope.taskTypes = [{ name: "普通任务", id: 1 }, { name: "售前任务", id: 2 }, { name: "运维任务", id: 3 }, { name: "支持任务", id: 4 }];
			$scope.taskType = { name: "普通任务", id: 1 };

			var taskProjects = [];
			var projectUserOptions = [];
			var allProjects;
			var allUsers;
			initDS();

			//数据源加载
			function initDS() {
				var currentUrl = $rootScope.$state.current.templateUrl;
				setCurrentUser();

				$http.post("ws/task/getMyDoingProjects", { userId: $rootScope.subject.userId })
					.success(function (data) {
						$.each(data.result, function (index, ele) {
							//若从项目详情页加载，且该项目的项目id存在与用户参与的项目中，则默认选择当前项目
							if ($state.includes("project") && $stateParams.project == ele.id) {
								$scope.newTask.projectId = $rootScope.$state.params.project;
								$scope.project = { id: ele.id, name: ele.name, owner: ele.owner };
								$scope.taskProjectDisabled = true;//项目不允许切换
								afterSelectProject();
							} else if ($scope.project && $scope.project.id == ele.id) {
								$scope.newTask.projectId = $scope.project.id;
								$scope.project = { id: ele.id, name: ele.name, owner: ele.owner };
								$scope.taskProjectDisabled = true;//项目不允许切换
								afterSelectProject();
							}
							taskProjects.push({ id: ele.id, name: ele.name, owner: ele.owner });
						})

						$scope.projects = taskProjects;
						/*if($scope.project){
							$http.get("ProjectPlan/all/project", {
								params: {
									projectId: $scope.project.id
								}
							}).success(function (data) {
								data.forEach(function (i) {
									if (i.id == $scope.planId) {
										$scope.taskprojectPlan = i;
										$scope.projectPlan = i;
									}
								})
								var rootNode = {
									id: 0,
									name: $scope.project.name,
								}
								data.push(rootNode)
								$scope.projectPlans = data;
								$timeout(function(){
									var ztree = $("#projectPlan").controller("c2-dropdown").getTree();
									ztree.expandNode(ztree.getNodes()[0], true, false, true);
								},400);
							})
						}*/

					});

				$http.post("e/project/op/select", { arcStatus: 1 }).success(function (data) {
					allProjects = data.result;
				});
			}
			function setCurrentUser() {
				$scope.taskUser = {
					user_id: $rootScope.subject.user_id,
					user_name: $rootScope.subject.user_name,
					user_realname: $rootScope.subject.user_realname,
					remark1: $rootScope.subject.remark1
				};

				$scope.newTask.assignedTo = $scope.taskUser.user_name;
				$scope.newTask.userId = $scope.taskUser.user_id;
				$scope.newTask.userRealname = $scope.taskUser.user_realname;
			}
			function cleanCurrentUser() {
				$scope.taskUser = null;
				$scope.newTask.assignedTo = null;
				$scope.newTask.userId = null;
				$scope.newTask.userRealname = null;
			}

			$scope.typeClick = function (taskType) {
				if ($scope.newTask.taskType == taskType.id) return;
				if ((($scope.newTask.taskType == 2 || $scope.newTask.taskType == 4) && taskType.id == 3) ||
					(($scope.newTask.taskType == 3 || $scope.newTask.taskType == 4) && taskType.id == 2) ||
					(($scope.newTask.taskType == 3 || $scope.newTask.taskType == 2) && taskType.id == 4)) {
					$scope.newTask.taskType = taskType.id;
					return;
				}
				$scope.newTask.taskType = taskType.id;
				setCurrentUser();

				if (taskType.id == 4) {//支持单类型
					$http.post("ws/getSupportList", {}).success(function (data) {
						$scope.supports = data.result;
					})
					//查询申请部门以及用户信息
					$http.post("ws/chinacreatorDepartment", {}).success(function (allDept) {
						$scope.supportOrgs = angular.copy(allDept.result);
						$http.post("ws/findUserBelongDepartment", { userId: $rootScope.subject.userId }).success(function (data) {
							$scope.org = $scope.supportOrgs.find(o => o.id == data.result);
						});
					});
					$http.post("ws/getMembersByWorgOrg", {}).success(function (data) {
						$scope.manageUsers = angular.copy(data.result);//执行负责人
					});
				}
				if (taskType.id == 1) {
					$scope.projects = taskProjects;

					var projectExist = false;
					if ($scope.project) {
						angular.forEach($scope.projects, function (p) {
							if (p.id == $scope.project.id) {
								projectExist = true;
								$scope.taskUsers = projectUserOptions;
								$scope.filterOnly = false;
								$("#taskUserDropdown").find(".dropdown-search").find("input").attr("placeholder", "项目成员搜索...");
								//        					cleanCurrentUser();
							}
						});
					}
					if (!projectExist) {
						$scope.project = null;
						$scope.newTask.projectId = null;
						refreshContent();
					}
				} else {
					$scope.projects = allProjects;

					if (angular.isUndefined(allUsers)) {
						$http.get("ws/dept/getUsersByDeptId").success(function (data) {
							allUsers = data.result;
							$scope.taskUsers = allUsers;
							$scope.filterOnly = true;
						});
					} else {
						$scope.taskUsers = allUsers;
						$scope.filterOnly = true;
					}

					$("#taskUserDropdown").find(".dropdown-search").find("input").attr("placeholder", "全员搜索...");
				}

			}

			$scope.showHideSupport = function () {
				$scope.taskDown = !$scope.taskDown;
				$("#taskDiv").slideToggle(400);
				$scope.newSupport = {};
			}

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
			};

			function zTreeOnClick(event, treeId, treeNode) {
				$scope.org = treeNode;
			}

			$scope.supportClick = function (support) {
				$scope.taskSupport = support;
			}

			//新增支持单
			$scope.creatSupport = function () {
				var check = true;
				$scope.addSupportButtonDisabled = true;
				if (angular.isUndefined($scope.newSupport.title)) {
					$scope.suppportTitleRed = true;
					check = false;
				}
				if (angular.isUndefined($scope.newSupport.manageUser)) {
					$scope.suppportUserRed = true;
					check = false;
				}
				if (angular.isUndefined($scope.project)) {
					$scope.taskProjectRed = true;
					check = false;
				}
				if (!check) {
					$scope.addSupportButtonDisabled = false;
					return;
				} else {
					$scope.newSupport.orgId = $scope.org.id;
					$scope.newSupport.projectId = $scope.project.id;
					$http.post("ws/support/createSupportNote", { support: $scope.newSupport }).success(function (data) {
						$scope.taskSupport = data.result;
						$scope.addSupportButtonDisabled = false;
					})
				}
			}

			$scope.projectClick = function (project) {
				if ($scope.newTask.projectId == project.id) return;
				refreshContent();
				$scope.newTask.projectId = project.id;
				$scope.project = { id: project.id, name: project.name, owner: project.owner };
				$scope.taskProjectRed = false;
				afterSelectProject();
			}

			$scope.cleanModule = function () {
				$scope.taskModule = null;
			}

			function refreshContent() {
				//刷新
				$scope.taskModule = null;
				$scope.taskStory = null;
				//        	$scope.taskUsers = [];
				$scope.taskMilestone = null;
				//          $scope.tempTaskLabels = [];
			}

			function afterSelectProject() {
				//普通任务才需要查机构用户
				if ($scope.taskType.id == 1) {
					//初始化成员下拉 - 默认当前用户userId
					//若新选择项目也有已选择的成员，不变
					if (angular.isUndefined($scope.taskUser) || $scope.taskUser == null) setCurrentUser();
					$http.post("ws/task/getMembersForAdd", { projectId: $scope.newTask.projectId })
						.success(function (data) {
							var userExistInProject = false;
							projectUserOptions = [];
							angular.forEach(data.result, function (u) {
								projectUserOptions.push(u);
								if (angular.equals(u.user_id, $scope.newTask.userId)) {
									$scope.taskUser = u;
									userExistInProject = true;
								}
							});
							if (!userExistInProject) setCurrentUser();

							$scope.taskUsers = projectUserOptions;
							//            		$scope.filterOnly = false;
						});
				} else {
					$http.post("ws/task/getMembersForAdd", { projectId: $scope.newTask.projectId }).success(function (data) {
						projectUserOptions = data.result;
					});
				}

				//模块
				/*$http.post("e/module/op/selectModule", { projectId: { id: $scope.newTask.projectId } })
					.success(function (data) {
						$scope.moduleNodes = data.result;
					});*/
				//需求
				$http.post("ws/getAllStory", { projectId: $scope.newTask.projectId, milestoneId: $scope.newTask.milestoneId }).success(function (data) {
					if (data.result.length > 0) {
						$scope.storys = data.result;
					} else {
						$scope.storys = [];
					}
				})
				//里程碑
				$http.post("ws/milestone/selectAllUnclosedMilestone", { milestone: { "projectId": $scope.newTask.projectId } })
					.success(function (data) {
						if (data.result.length > 0) {
							$scope.milestones = data.result;
						} else {
							$scope.milestones = [];
						}
					});
				//项目计划
				$http.get("ProjectPlan/all/project", {
					params: {
						projectId: $scope.project.id,
					}
				}).success(function (data) {
					data.forEach(function (i) {
						if (i.id == $scope.planId) {
							$scope.taskprojectPlan = i;
							$scope.projectPlan = i;
						}
					})
					var rootNode = {
						id: 0,
						name: $scope.project.name,
					}
					data.push(rootNode)
					$scope.projectPlans = data;
					$timeout(function () {
						var ztree = $("#newTaskProjectPlan").controller("c2-dropdown").getTree();
						ztree.expandNode(ztree.getNodes()[0], true, false, true);
					}, 400);

				})
				//项目关于任务确认的设置
				$http.post("ws/getProjectPreference", { projectId: $scope.newTask.projectId, preferNames: ["projectTaskConfirmState"] })
					.success(function (data, status, headers, config) {
						if (!angular.isUndefined(data.result)
							&& !angular.isUndefined(data.result.projectTaskConfirmState)
							&& !angular.isUndefined(data.result.projectTaskConfirmState.preferContent)) {
							$scope.taskConfirm = "true" == data.result.projectTaskConfirmState.preferContent;
						} else {
							$scope.taskConfirm = false;
						}
					});
				/*$http.post("e/c2_comunity_milestone/op/selectMilestone", {projectId: $scope.newTask.projectId})
						.success(function (data) {
								if (data.result.length > 0) {
										$scope.milestones = data.result;
								} else {
										$scope.milestones = [];
								}
						});*/

				//标签
				$http.post("ws/getAllLabels", { projectId: $scope.newTask.projectId })
					.success(function (data) {
						$.each(data.result.AllLabels, function (index, val) {
							val.backgroundStyle = {
								"background-color": val.color,
								"border-radius": "2px",
								"color": getTextColorByBackColor(val.color)
							};
						});
						$scope.allLabels = data.result.AllLabels;
					});
			}

			//module	
			$scope.treeOptions = {
				view: {
					selectedMulti: false
				},
				data: {
					simpleData: {
						enable: true,
						pIdKey: "parent"
					}
				},
				callback: {
					onClick: function (event, treeId, treeNode, clickFlag) {
						$scope.$apply(function () {
							$scope.taskModule = treeNode;
							$scope.newTask.moduleId = treeNode.id;
						});
						// $("#newModuleDropdown").controller("c2-dropdown").toggle();
					}
				}
			};
			//plan	
			$scope.treePlanOptions = {
				view: {
					selectedMulti: false
				},
				data: {
					simpleData: {
						enable: true,
						pIdKey: "parent"
					}
				},
				callback: {
					onClick: function (event, treeId, treeNode, clickFlag) {
						$scope.$apply(function () {
							$scope.projectPlan = treeNode;
							$scope.newTask.projectPlan = treeNode;
						});
						// $("#newModuleDropdown").controller("c2-dropdown").toggle();
					}
				}
			};

			//user
			$scope.userMenuClick = function (user) {
				if (!user.remark1) {//调用远程用户，没有用户图标处理
					$http.post("ws/user/getUserByUsername", { name: user.userName }).success(function (data) {
						user.remark1 = data.result.remark1 || "assets/img/profile-pics/1.jpg";
						$scope.newTask.userId = data.result.userId;
					});
				}

				$scope.newTask.assignedTo = user.user_name;
				$scope.newTask.userRealname = user.user_realname;
				$scope.taskUser = user;
			}

			//mileStone
			$scope.mileStoneMenuClick = function (mileStone) {
				$scope.taskMilestone = mileStone;
				$scope.newTask.milestoneId = mileStone.id;//切换里程碑后需求重新变化
				$scope.taskStory = undefined;
				$scope.newTask.storyId = undefined;
				$http.post("ws/getAllStory", { projectId: $scope.newTask.projectId, milestoneId: $scope.newTask.milestoneId }).success(function (data) {
					if (data.result.length > 0) {
						$scope.storys = data.result;
					} else {
						$scope.storys = [];
					}
				})
			}

			//story
			$scope.storyMenuClick = function (story) {
				$scope.taskStory = story;
				$scope.newTask.storyId = story.id;
			}

			var mouseDownPosition = undefined;

			$scope.ueMouseDown = function ($event) {
				mouseDownPosition = { x: $event.clientX, y: $event.clientY };
			}

			$scope.ueMouseUp = function ($event) {
				if (mouseDownPosition) {
					var moved = Math.abs($event.clientX - mouseDownPosition.x) + Math.abs($event.clientY - mouseDownPosition.y);
					if (moved < 10) {
						startEditing($event);
					}
					mouseDownPosition = undefined;
				}
			}

			function startEditing($event) {
				if ($($event.target).is('a') || $($event.target).is('img')) {
					return;
				}
				$scope.hideUEditor = false;
			}

			$scope.saveDesc = function () {
				$scope.newTask.description = $scope.description;
				$scope.hideUEditor = true;
			}
			$scope.cancelDesc = function () {
				$scope.hideUEditor = true;
				$scope.description = $scope.newTask.description;
			}

			//时间选择器--截止日期
			$scope.showDeadline = function () {
				$('#deadline').focus();
			}

			//保存新建的任务
			function saveTask(task) {
				var url = "ws/creatTask";
				$http.post(url, { task: task, stageName: task.stageName, taskConfirm: $scope.taskConfirm }).success(function (data) {
					Messenger.post("任务【" + $scope.newTask.name + "】创建成功!");
					//执行保存label的方法
					var labelTask = [];
					$.each($scope.tempTaskLabels, function (index, ele) {
						labelTask.push({
							taskId: data.result.id,
							labelId: ele.id
						});
					});
					$http.post("ws/updateTaskLabels", { taskId: data.result.id, labels: labelTask });

					if (task.bugId) {
						//修改bug状态为打开
						$http.post("ws/bug/changeBugStatus", { bugId: task.bugId, status: 2 }).success(function () {
							//修改bug负责人为任务分配的人
							$http.post("ws/bug/changeBugMan", { bugId: task.bugId, man: task.assignedTo, changeMan: $rootScope.subject.userName, manRealname: task.userRealname });
						});
					}
					//			$http.post("message/"+$scope.newTask.userId+"/pushTask",{});

					/* 		//发送通知消息
							if ($rootScope.subject.userName != $scope.newTask.assignedTo) {
								$http.post("ws/pushSystem", {
									msgTo: $scope.newTask.userId,
									msgContent: "[" + $rootScope.subject.userRealname + "]将任务[" + $scope.newTask.name + "]分配给了你:",
									link: "tasks/" + $scope.newTask.id,
									isPersistent: true
								});
							} */

					//关闭新建任务弹窗时，每个弹窗事件的回调各自处理页面刷新事件。
					Modal.close();
					//发送广播，刷新团队任务列表
					$rootScope.$broadcast("topCreateNewTask", { task: data.result, stageName: task.stageName });
				});
			}

			$scope.createNewTask = function () {
				var taskCheck = true;
				if (angular.isUndefined($scope.project) || $scope.project == null) {
					$scope.taskProjectRed = true;
					taskCheck = false;
				}
				if (angular.isUndefined($scope.newTask.name) || $scope.newTask.name == '') {
					$scope.taskNameRed = true;
					taskCheck = false;
				}
				if (angular.isUndefined($scope.taskUser)) {
					$scope.taskUserRed = true;
					taskCheck = false;
				}
				if (angular.isUndefined($scope.projectPlan)) {
					$scope.taskPlanRed = true;
					taskCheck = false;
				}
				$scope.newTask.milestoneId = $scope.taskMilestone ? $scope.taskMilestone.id : null;
				$scope.newTask.storyId = $scope.taskStory ? $scope.taskStory.id : null;
				$scope.newTask.moduleId = $scope.taskStory ? ($scope.taskStory.module ? $scope.taskStory.module.id : null) : ($scope.moduleId ? $scope.moduleId : null);
				$scope.newTask.projectId = $scope.project ? $scope.project.id : null;
				$scope.newTask.stageId = $scope.$parent.$parent.stage ? $scope.$parent.$parent.stage.id : null;
				$scope.newTask.stageName = $scope.$parent.$parent.stage ? $scope.$parent.$parent.stage.name : null;
				$scope.newTask.projectPlan = $scope.projectPlan ? $scope.projectPlan : null;
				$scope.newTask.supportId = $scope.taskSupport ? $scope.taskSupport.id : null;

				if (taskCheck) {
					//将按钮失效几秒钟避免重复提交数据
					timer(5);
					$scope.newTask.left = $scope.newTask.estimate;
					//    			console.log("task",$scope.newTask);
					saveTask($scope.newTask);
				} else {
					$("#addTaskErrorMsg").addClass("see");
					$timeout(function () {
						$("#addTaskErrorMsg").removeClass("see");
					}, 2000);
				}

			}

			function timer(time) {
				var btn = $(".btn-save-task");
				btn.attr("disabled", true);  //按钮禁止点击
				var hander = setInterval(function () {
					if (time <= 0) {
						clearInterval(hander); //清除倒计时
						btn.attr("disabled", false);
						return false;
					} else {
						time--;
					}
				}, 1000);
			}

			function continueTask(task) {
				var url = "ws/creatTask";
				$http.post(url, { task: task, stageName: task.stageName, taskConfirm: $scope.taskConfirm }).success(function (data) {
					Messenger.post("任务【" + $scope.newTask.name + "】创建成功!");

					$scope.newTask.name = "";
					$scope.newTask.description = "";

					//执行保存label的方法
					var labelTask = [];
					$.each($scope.tempTaskLabels, function (index, ele) {
						labelTask.push({
							taskId: data.result.id,
							labelId: ele.id
						});
					});
					$http.post("ws/updateTaskLabels", { taskId: data.result.id, labels: labelTask });
					//			$http.post("message/"+$scope.newTask.userId+"/pushTask",{});

					//发送通知消息
					/* 	if ($rootScope.subject.userName != $scope.newTask.assignedTo) {
							$http.post("ws/pushSystem", {
								msgTo: $scope.newTask.userId,
								msgContent: "[" + $rootScope.subject.userRealname + "]将任务[" + $scope.newTask.name + "]分配给了你:",
								link: "tasks/" + $scope.newTask.id,
								isPersistent: true
							});
						} */

					//发送广播，刷新团队任务列表
					$rootScope.$broadcast("topCreateNewTask", { task: data.result, stageName: task.stageName });
				});

			}


			$scope.continueNewTask = function () {
				var taskCheck = true;
				if (angular.isUndefined($scope.project) || $scope.project == null) {
					$scope.taskProjectRed = true;
					taskCheck = false;
				}
				if (angular.isUndefined($scope.newTask.name) || $scope.newTask.name == '') {
					$scope.taskNameRed = true;
					taskCheck = false;
				}
				if (angular.isUndefined($scope.taskUser)) {
					$scope.taskUserRed = true;
					taskCheck = false;
				}

				if (angular.isUndefined($scope.projectPlan)) {
					$scope.taskPlanRed = true;
					taskCheck = false;
				}
				$scope.newTask.milestoneId = $scope.taskMilestone ? $scope.taskMilestone.id : null;
				$scope.newTask.storyId = $scope.taskStory ? $scope.taskStory.id : null;
				$scope.newTask.moduleId = $scope.taskStory ? ($scope.taskStory.module ? $scope.taskStory.module.id : null) : null;
				$scope.newTask.stageId = $scope.$parent.$parent.stage ? $scope.$parent.$parent.stage.id : null;
				$scope.newTask.stageName = $scope.$parent.$parent.stage ? $scope.$parent.$parent.stage.name : null;
				$scope.newTask.projectPlan = $scope.projectPlan ? $scope.projectPlan : null;
				$scope.newTask.supportId = $scope.taskSupport ? $scope.taskSupport.id : null;

				if (taskCheck) {
					$scope.newTask.left = $scope.newTask.estimate;
					//    			console.log("task",$scope.newTask);
					continueTask($scope.newTask);
				} else {
					$("#addTaskErrorMsg").addClass("see");
					$timeout(function () {
						$("#addTaskErrorMsg").removeClass("see");
					}, 2000);
				}

			}

		}]);

developmentCenter
	.controller('addStoryCtrl', ['$stateParams', '$compile', 'markdownConverter', 'Modal', 'ModelFactory', '$state', '$scope', '$http', '$rootScope', 'Messenger', '$timeout',
		function ($stateParams, $compile, markdownConverter, Modal, ModelFactory, $state, $scope, $http, $rootScope, Messenger, $timeout) {
			var $params = $scope.$params = {};
			//    var projectId = $scope.$parent.$parent.projectId;
			$scope.story = { project: { id: $stateParams.project } };
			$scope.taskModule = $scope.module ? $scope.module : undefined;
			$scope.story.module = $scope.module ? $scope.module : undefined;
			$scope.story.selectPrioritys = { id: 3, title: '低' };
			$scope.story.selectCompletes = { id: 0, title: '0%' };
			$scope.story.selectType = { id: 1, title: '功能' };
			$scope.story.type = 1;
			$scope.story.pri = 3;
			$scope.allSelectType = [{ id: 1, title: '功能' }, { id: 2, title: '用户体验' }];
			$scope.allPriorityList = [{ id: 1, title: '高' }, { id: 2, title: '中' }, { id: 3, title: '低' }];
			$scope.allCompletes = [{ id: 0, title: '0%' }, { id: 1, title: '25%' }, { id: 2, title: '50%' }, { id: 3, title: '75%' }, { id: 4, title: '100%' }];
			$scope.allLabelList = [];
			$scope.projectUsers = [];
			$scope.story.selectLabels = [];
			$scope.story.spec = "";
			$scope.story.openedDate = moment().format('YYYY-MM-DD');
			$scope.addType = 'story';
			$scope.moduleId ? $scope.moduleId : $stateParams.moduleId ? $stateParams.moduleId : undefined;

			$scope.hideMainStory = true;
			$scope.hideStoryFunction = true;
			$scope.hideStoryEffect = true;
			$scope.story.mainStory = "";
			$scope.story.storyFunction = "";
			$scope.story.storyEffect = "";
			//$scope.assignTo = {userId:$rootScope.subject.userId,userRealname:$rootScope.subject.userRealname,icon:$rootScope.subject.remark1,userName:$rootScope.subject.userName};
			//$scope.story.assignedTo = $scope.assignTo.userName;


			$scope.hideUEditor = true;
			$scope.filterOnly = false;


			var taskProjects = [];
			var projectUserOptions = [];
			var allProjects;
			var allUsers;

			initDS();


			//数据源加载
			function initDS() {
				$scope.story.selectPrioritys = { id: 3, title: '低' };
				var currentUrl = $rootScope.$state.current.templateUrl;
				$http.post("ws/task/getMyDoingProjects", { userId: $rootScope.subject.userId })
					.success(function (data) {
						$.each(data.result, function (index, ele) {
							//若从项目详情页加载，且该项目的项目id存在与用户参与的项目中，则默认选择当前项目
							if ($state.includes("project") && $stateParams.project == ele.id) {
								$scope.projectId = $rootScope.$state.params.project;
								$scope.project = { id: ele.id, name: ele.name, owner: ele.owner };
								afterSelectProject();
							}
							taskProjects.push({ id: ele.id, name: ele.name, owner: ele.owner });
						})

						$scope.projects = taskProjects;
					});

				$http.post("e/project/op/select", { arcStatus: 1 }).success(function (data) {
					allProjects = data.result;
				});
			}


			$scope.projectClick = function (project) {
				if ($scope.projectId == project.id) return;
				refreshContent();
				$scope.projectId = project.id;
				$scope.project = { id: project.id, name: project.name, owner: project.owner };
				$scope.taskProjectRed = false;
				afterSelectProject();
				$scope.story.project = { id: project.id };
			}
			$scope.cleanMilstone = function () {
				$scope.story.milestone = undefined;
			}

			$scope.cleanModule = function () {
				$scope.story.module = undefined;
			}

			function refreshContent() {
				//刷新
				$scope.taskModule = null;
				//        	$scope.taskUsers = [];
				$scope.taskMilestone = null;
				//          $scope.tempTaskLabels = [];
			}

			function afterSelectProject() {
				$scope.projectUsers = [];
				//项目成员
				$http.post("ws/getMembers", { projectId: $scope.projectId }).success(function (data) {
					angular.forEach(data.result, function (v, n) {
	    			/*if(v.userDTO.user_id==$rootScope.subject.userId) {
	    				$scope.assignTo = {userId:v.userDTO.user_id,userRealname:v.userDTO.user_realname,icon:v.userDTO.remark1,userName:v.userDTO.user_name};
					}*/
						$scope.projectUsers.push({ userId: v.userDTO.user_id, userRealname: v.userDTO.user_realname, icon: v.userDTO.remark1, userName: v.userDTO.user_name });
					});
				});
				//模块
				$http.post("e/module/op/selectModule", { projectId: { id: $scope.projectId } })
					.success(function (data) {
						var modList = [{ id: 0, name: "所有模块", order: 1, parent: undefined, projectId: {} }];
						for (var i = 0; i < data.result.length; i++) {
							modList.push(data.result[i]);
							/*if ($scope.moduleId && $scope.moduleId == data.result[i].id) {
								$scope.story.module = data.result[i];
							}*/
						}
						$scope.moduleNodes = modList;
						if (!$scope.module) {
							$scope.taskModule = modList[0];
							$scope.story.module = modList[0];
						}
					});
				//里程碑
				$http.post("ws/milestone/selectAllUnclosedMilestone", { milestone: { "projectId": $scope.projectId } })
					.success(function (data) {
						if (data.result.length > 0) {
							$scope.milestones = data.result;
						} else {
							$scope.milestones = [];
						}
						/*if ($state.includes("project.milestoneDetail")) {
							$.each(data.result, function (index, val) {
								if (val.id == $stateParams.milestoneId) {
									$scope.taskMilestone = val;
									$scope.story.milestone = val;
								}
							})
						}*/
					});
				/*$http.post("e/c2_comunity_milestone/op/selectMilestone", {projectId: $scope.projectId})
						.success(function (data) {
								if (data.result.length > 0) {
										$scope.milestones = data.result;
								} else {
										$scope.milestones = [];
								}
								if($state.includes("project.milestoneDetail")) {
				$.each(data.result,function (index, val){
					if(val.id == $stateParams.milestoneId) {
						$scope.taskMilestone=val;
						$scope.story.milestone=val;
							}
				})
			}
						});*/

				//标签
				$http.post("ws/getAllLabels", { projectId: $scope.projectId })
					.success(function (data) {
						$.each(data.result.AllLabels, function (index, val) {
							val.backgroundStyle = {
								"background-color": val.color,
								"border-radius": "2px",
								"color": getTextColorByBackColor(val.color)
							};
						});
						$scope.allLabels = data.result.AllLabels;
					});
			}

			//module	
			$scope.treeOptions = {
				view: {
					selectedMulti: false,
					addHoverDom: addHoverDom,
					removeHoverDom: removeHoverDom
				},
				data: {
					simpleData: {
						enable: true,
						pIdKey: "parent"
					}
				},
				callback: {
					onClick: function (event, treeId, treeNode, clickFlag) {
						$scope.$apply(function () {
							if (treeNode.id != '0') {
								$scope.story.module = treeNode;
								$scope.taskModule = treeNode;
							}
						});
						// $("#newModuleDropdown").controller("c2-dropdown").toggle();
					},
					onRename: function caseRename(event, treeId, treeNode, isCancel) {
						$http.post("ws/module/updateModuleName", { id: treeNode.id, name: treeNode.name });

					}
				}
			};

			//增加子模块模块
			function addHoverDom(treeId, treeNode) {
				var sObj = $("#" + treeNode.tId + "_span");
				if (treeNode.editNameFlag || $("#addBtn_" + treeNode.tId).length > 0) return;
				var addStr = "<span class='button add' id='addBtn_" + treeNode.tId
					+ "' title='新增模块' onfocus='this.blur();'></span>";
				sObj.after(addStr);
				var btn = $("#addBtn_" + treeNode.tId);
				if (btn) btn.bind("click", function () {
					$http.post("ws/module/addModule", { parent: treeNode.id, projectId: $scope.projectId }).success(function (data) {
						var newNode = { id: data.result.id, parent: treeNode.tid, name: "" };
						newNode = $.fn.zTree.getZTreeObj('newModuleDropdownTree').addNodes(treeNode, newNode);
						$.fn.zTree.getZTreeObj('newModuleDropdownTree').editName(newNode[0]);
					})
					return false;
				});
			};

			//新增按钮样式移除
			function removeHoverDom(treeId, treeNode) {
				$("#addBtn_" + treeNode.tId).unbind().remove();
			};

			$scope.prioritysClick = function (Prioritys) {
				$scope.story.selectPrioritys = Prioritys;
				$scope.story.pri = $scope.story.selectPrioritys.id;
			}

			$scope.completesClick = function (selectCompletes) {
				$scope.story.selectCompletes = selectCompletes
				$scope.story.completes = selectCompletes.id;
			}

			//点击需求类型选择
			$scope.selectTypeClick = function (selectType) {
				$scope.story.selectType = selectType;
				$scope.story.type = $scope.story.selectType.id;
			}

			$scope.saveDesc = function (type) {
				if (type == 'mainStory') {
					$scope.hideMainStory = true;
					$scope.story.mainStory = $scope.mainStory;
				} else if (type == 'storyFunction') {
					$scope.hideStoryFunction = true;
					$scope.story.storyFunction = $scope.storyFunction;
				} else if (type == 'storyEffect') {
					$scope.hideStoryEffect = true;
					$scope.story.storyEffect = $scope.storyEffect;
				}
			}

			$scope.cancelDesc = function (type) {
				if (type == 'mainStory') {
					$scope.hideMainStory = true;
					$scope.mainStory = $scope.story.mainStory;
				} else if (type == 'storyFunction') {
					$scope.hideStoryFunction = true;
					$scope.storyFunction = $scope.story.storyFunction;
				} else if (type == 'storyEffect') {
					$scope.hideStoryEffect = true;
					$scope.storyEffect = $scope.story.storyEffect;
				}
			}

			//mileStone
			$scope.mileStoneMenuClick = function (mileStone) {
				//$scope.taskMilestone = mileStone;
				$scope.story.milestone = mileStone;
			}

			//负责人
			$scope.userClick = function (user) {
				$scope.story.assignedTo = user.userName;
			}

			//提出人
			$scope.proposerClick = function (user) {
				$scope.story.proposer = user.userName;
			}

			var mouseDownPosition = undefined;

			$scope.ueMouseDown = function ($event) {
				mouseDownPosition = { x: $event.clientX, y: $event.clientY };
			}

			$scope.ueMouseUp = function ($event) {
				if (mouseDownPosition) {
					var moved = Math.abs($event.clientX - mouseDownPosition.x) + Math.abs($event.clientY - mouseDownPosition.y);
					if (moved < 10) {
						startEditing($event);
					}
					mouseDownPosition = undefined;
				}
			}




			//保存新建的需求
			function saveStory(story) {
				var url = "ws/addStory";
				//            if (task.deadline == null || angular.isUndefined(task.deadline) //截止日期为空或者起始日期大于截止日期时-重设截止日期
				//                || moment(moment(task.estStartDate).format('YYYY-MM-DD')).diff(moment(moment(task.deadline).format('YYYY-MM-DD'))) > 0) {
				//                task.deadline = addBusinessDays(moment(task.estStartDate), Math.ceil(Number(task.estimate) / 8 - 1)).valueOf();
				//                $scope.newTask.deadline = task.deadline;
				//            } else if (task.estStartDate == null || angular.isUndefined(task.estStartDate)) {
				//                task.estStartDate = subtractBusinessDays(moment(task.deadline), Math.ceil(Number(task.estimate) / 8 - 1)).valueOf();
				//                $scope.newTask.estStartDate = task.estStartDate;
				//            }
				$http.post(url, {
					story: $scope.story,
					labelList: $scope.story.selectLabels
				}).success(function (data) {

					$http.post("message/" + $rootScope.subject.userId + "/pushStory", {});

					//发送广播，刷新团队任务列表
					$rootScope.$broadcast("createNewStory", data.result);
					Modal.close(data.result);
				});
				Messenger.post("需求【" + $scope.story.title + "】创建成功!");

			}

			$scope.createNewStory = function () {
				var taskCheck = true;
				/*$scope.story.mainStory = $scope.mainStory;
				$scope.story.storyEffect = $scope.storyEffect;
				$scope.story.storyFunction = $scope.storyFunction;*/
				if (angular.isUndefined($scope.project) || $scope.project == null) {
					$scope.taskProjectRed = true;
					taskCheck = false;
				}
				if (angular.isUndefined($scope.assignTo) || $scope.assignTo == null) {
					$scope.formCheck = true;
					taskCheck = false;
				}
				if (angular.isUndefined($scope.proposer) || $scope.proposer == null) {
					$scope.formCheck = true;
					taskCheck = false;
				}
				if (angular.isUndefined($scope.story.module) || $scope.story.module.name == '') {
					$scope.formCheck = true;
					taskCheck = false;
				}

				if (angular.isUndefined($scope.story.title) || $scope.story.title == '') {
					$scope.taskNameRed = true;
					taskCheck = false
				}
				/*if (angular.isUndefined($scope.story.storyEffect) || $scope.story.storyEffect == '') {
					$scope.formCheck = true;
					taskCheck = false;
				}*/
				if (angular.isUndefined($scope.story.spec) || $scope.story.spec == "") {
					$scope.formCheck = true;
					taskCheck = false;
				}

				$scope.story.completes = $scope.story.selectCompletes.id;

				//   $scope.story.module = $scope.taskModule?$scope.taskModule.id:null;
				//    $scope.story.milestone = $scope.taskMilestone?$scope.taskMilestone.id:null;
				if (taskCheck) {
					//按钮失效几秒防止重复提交
					timer(5);
					$scope.story.left = $scope.story.estimate;
					//    			console.log("task",$scope.newTask);
					saveStory($scope.story);
				} else {
					$("#addTaskErrorMsg").addClass("see");
					$timeout(function () {
						$("#addTaskErrorMsg").removeClass("see");
					}, 2000);
				}

			}

			function timer(time) {
				var btn = $(".btn-save-task");
				btn.attr("disabled", true);  //按钮禁止点击
				var hander = setInterval(function () {
					if (time <= 0) {
						clearInterval(hander); //清除倒计时
						btn.attr("disabled", false);
						return false;
					} else {
						time--;
					}
				}, 1000);
			}

			//继续新建需求
			function continueStory(story) {
				var url = "ws/addStory";
				//            if (task.deadline == null || angular.isUndefined(task.deadline) //截止日期为空或者起始日期大于截止日期时-重设截止日期
				//                || moment(moment(task.estStartDate).format('YYYY-MM-DD')).diff(moment(moment(task.deadline).format('YYYY-MM-DD'))) > 0) {
				//                task.deadline = addBusinessDays(moment(task.estStartDate), Math.ceil(Number(task.estimate) / 8 - 1)).valueOf();
				//                $scope.newTask.deadline = task.deadline;
				//            } else if (task.estStartDate == null || angular.isUndefined(task.estStartDate)) {
				//                task.estStartDate = subtractBusinessDays(moment(task.deadline), Math.ceil(Number(task.estimate) / 8 - 1)).valueOf();
				//                $scope.newTask.estStartDate = task.estStartDate;
				//            }
				$http.post(url, {
					story: $scope.story,
					labelList: $scope.story.selectLabels
				}).success(function (data) {
					Messenger.post("需求【" + $scope.story.title + "】创建成功!");
					$scope.story.title = "";
					$scope.story.spec = "";
					$http.post("message/" + $rootScope.subject.userId + "/pushStory", {});
					//发送广播，刷新团队任务列表
					$rootScope.$broadcast("createNewStory", data.result);
				});


			}

			$scope.continueStory = function () {
				var taskCheck = true;
				/*$scope.story.mainStory = $scope.mainStory;
				$scope.story.storyEffect = $scope.storyEffect;
				$scope.story.storyFunction = $scope.storyFunction;*/
				if (angular.isUndefined($scope.project) || $scope.project == null) {
					$scope.taskProjectRed = true;
					taskCheck = false;
				}
				if (angular.isUndefined($scope.story.title) || $scope.story.title == '') {
					$scope.taskNameRed = true;
					taskCheck = false;
				}

				if (angular.isUndefined($scope.story.module) || $scope.story.module.name == '') {
					$scope.formCheck = true;
					taskCheck = false;
				}
				/*if (angular.isUndefined($scope.story.storyEffect) || $scope.story.storyEffect == '') {
					$scope.formCheck = true;
					taskCheck = false;
				}*/

				if (angular.isUndefined($scope.story.spec) || $scope.story.spec == "") {
					$scope.formCheck = true;
					taskCheck = false;
				}

				//   $scope.story.module = $scope.taskModule?$scope.taskModule.id:null;
				//    $scope.story.milestone = $scope.taskMilestone?$scope.taskMilestone.id:null;
				if (taskCheck) {
					$scope.story.left = $scope.story.estimate;
					//    			console.log("task",$scope.newTask);
					continueStory($scope.story);
				} else {
					$("#addTaskErrorMsg").addClass("see");
					$timeout(function () {
						$("#addTaskErrorMsg").removeClass("see");
					}, 2000);
				}

			}


		}]);


developmentCenter
	.controller('addStoryPlans', ['$stateParams', '$compile', 'markdownConverter', 'Modal', 'ModelFactory', '$state', '$scope', '$http', '$rootScope', 'Messenger', '$timeout',
		function ($stateParams, $compile, markdownConverter, Modal, ModelFactory, $state, $scope, $http, $rootScope, Messenger, $timeout) {
			var $params = $scope.$params = {};
			//    var projectId = $scope.$parent.$parent.projectId;
			$scope.module = {};
			$scope.story = { project: { id: $stateParams.project } };
			$scope.taskMilestone = $scope.milestone ? $scope.milestone : undefined;
			$scope.story.milestone = $scope.milestone ? $scope.milestone : undefined;
			$scope.story.selectPrioritys = { id: 3, title: '低' };
			$scope.story.selectCompletes = { id: 0, title: '0%' };
			$scope.story.selectType = { id: 1, title: '功能' };
			$scope.story.type = 1;
			$scope.story.pri = 3;
			$scope.allSelectType = [{ id: 1, title: '功能' }, { id: 2, title: '用户体验' }];
			$scope.allPriorityList = [{ id: 1, title: '高' }, { id: 2, title: '中' }, { id: 3, title: '低' }];
			$scope.allCompletes = [{ id: 0, title: '0%' }, { id: 1, title: '25%' }, { id: 2, title: '50%' }, { id: 3, title: '75%' }, { id: 4, title: '100%' }];
			$scope.allLabelList = [];
			$scope.projectUsers = [];
			$scope.story.selectLabels = [];
			$scope.story.spec = "";
			$scope.story.openedDate = moment().format('YYYY-MM-DD');
			$scope.addType = $scope.addType ? $scope.addType : 'story';
			$scope.moduleId = $scope.moduleId ? $scope.moduleId : ($stateParams.moduleId ? $stateParams.moduleId : 0);
			$scope.module.parent = $scope.moduleId;

			$scope.hideMainStory = true;
			$scope.hideStoryFunction = true;
			$scope.hideStoryEffect = true;
			$scope.story.mainStory = "";
			$scope.story.storyFunction = "";
			$scope.story.storyEffect = "";
			$scope.hideUEditor = true;
			$scope.filterOnly = false;

			$scope.module.parentModule = {name:'所有模块'};
			var taskProjects = [];
			var projectUserOptions = [];
			var allProjects;
			var allUsers;

			initDS();


			//数据源加载
			function initDS() {
				$scope.story.selectPrioritys = { id: 3, title: '低' };
				var currentUrl = $rootScope.$state.current.templateUrl;
				$http.post("ws/task/getMyDoingProjects", { userId: $rootScope.subject.userId })
					.success(function (data) {
						$.each(data.result, function (index, ele) {
							//若从项目详情页加载，且该项目的项目id存在与用户参与的项目中，则默认选择当前项目
							if ($state.includes("project") && $stateParams.project == ele.id) {
								$scope.projectId = $rootScope.$state.params.project;
								$scope.project = { id: ele.id, name: ele.name, owner: ele.owner };
								afterSelectProject();
							}
							taskProjects.push({ id: ele.id, name: ele.name, owner: ele.owner });
						})

						$scope.projects = taskProjects;
					});

				$http.post("e/project/op/select", { arcStatus: 1 }).success(function (data) {
					allProjects = data.result;
				});
			}


			$scope.projectClick = function (project) {
				if ($scope.projectId == project.id) return;
				refreshContent();
				$scope.projectId = project.id;
				$scope.project = { id: project.id, name: project.name, owner: project.owner };
				$scope.taskProjectRed = false;
				afterSelectProject();
				$scope.story.project = { id: project.id };
			}
			$scope.cleanMilstone = function () {
				$scope.story.milestone = undefined;
			}

			$scope.cleanModule = function () {
				$scope.story.module = undefined;
			}

			function refreshContent() {
				//刷新
				$scope.taskModule = null;
				//        	$scope.taskUsers = [];
				$scope.taskMilestone = null;
				//          $scope.tempTaskLabels = [];
			}

			function afterSelectProject() {
				//项目成员
				$http.post("ws/getMembers", { projectId: $scope.projectId }).success(function (data) {
					angular.forEach(data.result, function (v, n) {
						if (v.userDTO.user_id == $rootScope.subject.userId) {
							$scope.proposer = { userId: v.userDTO.user_id, userRealname: v.userDTO.user_realname, icon: v.userDTO.remark1, userName: v.userDTO.user_name };
						}
						$scope.projectUsers.push({ userId: v.userDTO.user_id, userRealname: v.userDTO.user_realname, icon: v.userDTO.remark1, userName: v.userDTO.user_name });
					});
				});
				//模块
				$http.post("e/module/op/selectModule", { projectId: { id: $scope.projectId } })
					.success(function (data) {
						var modList = [{ id: 0, name: "所有模块", order: 1, parent: undefined, projectId: {} }];
						for (var i = 0; i < data.result.length; i++) {
							modList.push(data.result[i]);
							if ($scope.moduleId && $scope.moduleId == data.result[i].id) {
								$scope.story.module = data.result[i];
								$scope.module.parentModule = data.result[i]
							}
						}
						$scope.moduleNodes = modList;
					});
				//里程碑
				$http.post("ws/milestone/selectAllUnclosedMilestone", { milestone: { "projectId": $scope.projectId } })
					.success(function (data) {
						if (data.result.length > 0) {
							$scope.milestones = data.result;
						} else {
							$scope.milestones = [];
						}
						if ($state.includes("project.milestoneDetail")) {
							$.each(data.result, function (index, val) {
								if (val.id == $stateParams.milestoneId) {
									$scope.taskMilestone = val;
									$scope.story.milestone = val;
								}
							})
						}
					});

				//标签
				$http.post("ws/getAllLabels", { projectId: $scope.projectId })
					.success(function (data) {
						$.each(data.result.AllLabels, function (index, val) {
							val.backgroundStyle = {
								"background-color": val.color,
								"border-radius": "2px",
								"color": getTextColorByBackColor(val.color)
							};
						});
						$scope.allLabels = data.result.AllLabels;
					});
			}

			//module	
			$scope.treeOptions = {
				view: {
					selectedMulti: false,
					removeHoverDom: removeHoverDom
				},
				data: {
					simpleData: {
						enable: true,
						pIdKey: "parent"
					}
				},
				callback: {
					onClick: function (event, treeId, treeNode, clickFlag) {
						$scope.$apply(function () {
							if (treeNode.id != '0') {
								$scope.story.module = treeNode;
							}
							$scope.module.parentModule = treeNode;
						});
						// $("#newModuleDropdown").controller("c2-dropdown").toggle();
					},
					onRename: function caseRename(event, treeId, treeNode, isCancel) {
						$http.post("ws/module/updateModuleName", { id: treeNode.id, name: treeNode.name });

					}
				}
			};

			//增加子模块模块
			function addHoverDom(treeId, treeNode) {
				var sObj = $("#" + treeNode.tId + "_span");
				if (treeNode.editNameFlag || $("#addBtn_" + treeNode.tId).length > 0) return;
				var addStr = "<span class='button add' id='addBtn_" + treeNode.tId
					+ "' title='新增模块' onfocus='this.blur();'></span>";
				sObj.after(addStr);
				var btn = $("#addBtn_" + treeNode.tId);
				if (btn) btn.bind("click", function () {
					$http.post("ws/module/addModule", { parent: treeNode.id, projectId: $scope.projectId }).success(function (data) {
						var newNode = { id: data.result.id, parent: treeNode.tid, name: "" };
						newNode = $.fn.zTree.getZTreeObj('newModuleDropdownTree').addNodes(treeNode, newNode);
						$.fn.zTree.getZTreeObj('newModuleDropdownTree').editName(newNode[0]);
					})
					return false;
				});
			};

			//新增按钮样式移除
			function removeHoverDom(treeId, treeNode) {
				$("#addBtn_" + treeNode.tId).unbind().remove();
			};

			$scope.prioritysClick = function (Prioritys) {
				$scope.story.selectPrioritys = Prioritys;
				$scope.story.pri = $scope.story.selectPrioritys.id;
			}

			$scope.completesClick = function (selectCompletes) {
				$scope.story.selectCompletes = selectCompletes
				$scope.story.completes = selectCompletes.id;
			}

			//点击需求类型选择
			$scope.selectTypeClick = function (selectType) {
				$scope.story.selectType = selectType;
				$scope.story.type = $scope.story.selectType.id;
			}

			//选择添加类型
			$scope.selectAddType = function (type) {
				//var addStyle={"border-bottom-width":"3px","border-bottom-style":"solid","border-bottom-color":"#03a9f4"}
				if (type == "addStory") {
					if (!$('#addStory').hasClass('add-border')) {
						$('#addStory').addClass('add-border');
					}
					$('#addModule').removeClass('add-border');
					$scope.addType = 'story';
				} else if (type == "addModule") {
					if (!$('#addModule').hasClass('add-border')) {
						$('#addModule').addClass('add-border');
					}
					$('#addStory').removeClass('add-border');
					$scope.addType = 'module';
				}
			}

			$scope.saveDesc = function (type) {
				if (type == 'mainStory') {
					$scope.hideMainStory = true;
					$scope.story.mainStory = $scope.mainStory;
				} else if (type == 'storyFunction') {
					$scope.hideStoryFunction = true;
					$scope.story.storyFunction = $scope.storyFunction;
				} else if (type == 'storyEffect') {
					$scope.hideStoryEffect = true;
					$scope.story.storyEffect = $scope.storyEffect;
				}
			}

			$scope.cancelDesc = function (type) {
				if (type == 'mainStory') {
					$scope.hideMainStory = true;
					$scope.mainStory = $scope.story.mainStory;
				} else if (type == 'storyFunction') {
					$scope.hideStoryFunction = true;
					$scope.storyFunction = $scope.story.storyFunction;
				} else if (type == 'storyEffect') {
					$scope.hideStoryEffect = true;
					$scope.storyEffect = $scope.story.storyEffect;
				}
			}

			//mileStone
			$scope.mileStoneMenuClick = function (mileStone) {
				$scope.taskMilestone = mileStone;
				$scope.story.milestone = mileStone;
			}

			//负责人
			$scope.userModuleClick = function (user) {
				$scope.module.assignedTo = user.userName;
				$scope.module.assignTo = user;
			}

			//负责人
			$scope.userClick = function (user) {
				$scope.story.assignedTo = user.userName;
			}

			//提出人
			$scope.proposerClick = function (user) {
				$scope.story.proposer = user.userName;
			}

			var mouseDownPosition = undefined;

			$scope.ueMouseDown = function ($event) {
				mouseDownPosition = { x: $event.clientX, y: $event.clientY };
			}

			$scope.ueMouseUp = function ($event) {
				if (mouseDownPosition) {
					var moved = Math.abs($event.clientX - mouseDownPosition.x) + Math.abs($event.clientY - mouseDownPosition.y);
					if (moved < 10) {
						startEditing($event);
					}
					mouseDownPosition = undefined;
				}
			}

			//保存新建的需求
			function saveStory(story) {
				var url = "ws/addStory";
				//            if (task.deadline == null || angular.isUndefined(task.deadline) //截止日期为空或者起始日期大于截止日期时-重设截止日期
				//                || moment(moment(task.estStartDate).format('YYYY-MM-DD')).diff(moment(moment(task.deadline).format('YYYY-MM-DD'))) > 0) {
				//                task.deadline = addBusinessDays(moment(task.estStartDate), Math.ceil(Number(task.estimate) / 8 - 1)).valueOf();
				//                $scope.newTask.deadline = task.deadline;
				//            } else if (task.estStartDate == null || angular.isUndefined(task.estStartDate)) {
				//                task.estStartDate = subtractBusinessDays(moment(task.deadline), Math.ceil(Number(task.estimate) / 8 - 1)).valueOf();
				//                $scope.newTask.estStartDate = task.estStartDate;
				//            }
				$scope.story.proposer = $scope.proposer ? $scope.proposer.userName : $scope.story.proposer
				$http.post(url, {
					story: $scope.story,
					labelList: $scope.story.selectLabels
				}).success(function (data) {

					$http.post("message/" + $rootScope.subject.userId + "/pushStory", {});

					//发送广播，刷新团队任务列表
					$rootScope.$broadcast("createNewStory", data.result);
					if (data.result.assignedTo) data.result.assignedTo.userRealname = data.result.assignedTo.user_realname;
					if (data.result.proposer) data.result.proposer.userRealname = data.result.proposer.user_realname;
					Modal.close(data.result);
				});
				Messenger.post("需求【" + $scope.story.title + "】创建成功!");
			}

			$scope.createNewModule = function () {
				if (angular.isUndefined($scope.module.name)) {
					$scope.moduleNameRed = true;
				} else if (angular.isUndefined($scope.module.assignTo)) {
					$scope.moduleNameRed = true;
				} else {
					$scope.module.projectId = $scope.project;
					$scope.module.completes = 0;
					$scope.module.parent = $scope.module.parentModule.id ? $scope.module.parentModule.id : $scope.moduleId;
					//按钮失效几秒防止重复提交
					timer(5);
					$http.post("ws/saveModule", {
						module: $scope.module,
					}).success(function (data) {
						Messenger.post("模块【" + $scope.module.name + "】创建成功!");
						data.result.assigned = $scope.module.assignTo;
						Modal.close(data.result);
					});
				}
			}

			$scope.continueModule = function () {
				if (angular.isUndefined($scope.module.name)) {
					$scope.moduleNameRed = true;
				} else if (angular.isUndefined($scope.module.assignTo)) {
					$scope.moduleNameRed = true;
				} else {
					$scope.module.projectId = $scope.project;
					$scope.module.completes = 0;
					$scope.module.parent = $scope.module.parentModule.id ? $scope.module.parentModule.id : $scope.moduleId;
					//按钮失效几秒防止重复提交
					timer(5);
					$http.post("ws/saveModule", {
						module: $scope.module,
					}).success(function (data) {
						Messenger.post("模块【" + $scope.module.name + "】创建成功!");
						data.result.assigned = $scope.module.assignTo;
						$scope.continueModules(data.result);
						$scope.module.name = "";
					});
				}
			}

			$scope.createNewStory = function () {
				var taskCheck = true;
				if ($scope.addType == 'story') {
					if (angular.isUndefined($scope.project) || $scope.project == null) {
						$scope.taskProjectRed = true;
						taskCheck = false;
					}
					if (angular.isUndefined($scope.assignTo) || $scope.assignTo == null) {
						$scope.formCheck = true;
						taskCheck = false;
					}
					if (angular.isUndefined($scope.proposer) || $scope.proposer == null) {
						$scope.formCheck = true;
						taskCheck = false;
					}
					if (angular.isUndefined($scope.story.module) || $scope.story.module.name == '') {
						$scope.formCheck = true;
						taskCheck = false;
					}

					if (angular.isUndefined($scope.story.title) || $scope.story.title == '') {
						$scope.taskNameRed = true;
						taskCheck = false;
					}
					if (angular.isUndefined($scope.story.spec) || $scope.story.spec == "") {
						$scope.formCheck = true;
						taskCheck = false;
					}
					$scope.story.completes = $scope.story.selectCompletes.id;
				}

				//   $scope.story.module = $scope.taskModule?$scope.taskModule.id:null;
				//    $scope.story.milestone = $scope.taskMilestone?$scope.taskMilestone.id:null;
				if (taskCheck) {
					//按钮失效几秒防止重复提交
					timer(5);
					$scope.story.left = $scope.story.estimate;
					//    			console.log("task",$scope.newTask);
					saveStory($scope.story);
				} else {
					$("#addTaskErrorMsg").addClass("see");
					$timeout(function () {
						$("#addTaskErrorMsg").removeClass("see");
					}, 2000);
				}

			}

			function timer(time) {
				var btn = $(".btn-save-task");
				btn.attr("disabled", true);  //按钮禁止点击
				var hander = setInterval(function () {
					if (time <= 0) {
						clearInterval(hander); //清除倒计时
						btn.attr("disabled", false);
						return false;
					} else {
						time--;
					}
				}, 1000);
			}

			//继续新建需求
			function continueStory(story) {
				var url = "ws/addStory";
				//            if (task.deadline == null || angular.isUndefined(task.deadline) //截止日期为空或者起始日期大于截止日期时-重设截止日期
				//                || moment(moment(task.estStartDate).format('YYYY-MM-DD')).diff(moment(moment(task.deadline).format('YYYY-MM-DD'))) > 0) {
				//                task.deadline = addBusinessDays(moment(task.estStartDate), Math.ceil(Number(task.estimate) / 8 - 1)).valueOf();
				//                $scope.newTask.deadline = task.deadline;
				//            } else if (task.estStartDate == null || angular.isUndefined(task.estStartDate)) {
				//                task.estStartDate = subtractBusinessDays(moment(task.deadline), Math.ceil(Number(task.estimate) / 8 - 1)).valueOf();
				//                $scope.newTask.estStartDate = task.estStartDate;
				//            }
				$scope.story.proposer = $scope.proposer ? $scope.proposer.userName : $scope.story.proposer
				$http.post(url, {
					story: $scope.story,
					labelList: $scope.story.selectLabels
				}).success(function (data) {
					Messenger.post("需求【" + $scope.story.title + "】创建成功!");
					$scope.story.title = "";
					$scope.story.spec = "";
					$http.post("message/" + $rootScope.subject.userId + "/pushStory", {});
					//发送广播，刷新团队任务列表
					if (data.result.assignedTo) data.result.assignedTo.userRealname = data.result.assignedTo.user_realname;
					if (data.result.proposer) data.result.proposer.userRealname = data.result.proposer.user_realname;
					$scope.continueStorys(data.result);
					$rootScope.$broadcast("createNewStory", data.result);
				});
			}



			$scope.continueStory = function () {
				var taskCheck = true;
				/*$scope.story.mainStory = $scope.mainStory;
				$scope.story.storyEffect = $scope.storyEffect;
				$scope.story.storyFunction = $scope.storyFunction;*/
				if (angular.isUndefined($scope.project) || $scope.project == null) {
					$scope.taskProjectRed = true;
					taskCheck = false;
				}
				if (angular.isUndefined($scope.story.title) || $scope.story.title == '') {
					$scope.taskNameRed = true;
					taskCheck = false;
				}

				if (angular.isUndefined($scope.story.module) || $scope.story.module.name == '') {
					$scope.formCheck = true;
					taskCheck = false;
				}
				/*if (angular.isUndefined($scope.story.storyEffect) || $scope.story.storyEffect == '') {
					$scope.formCheck = true;
					taskCheck = false;
				}*/

				if (angular.isUndefined($scope.story.spec) || $scope.story.spec == "") {
					$scope.formCheck = true;
					taskCheck = false;
				}

				$scope.story.completes = $scope.story.selectCompletes.id;

				//   $scope.story.module = $scope.taskModule?$scope.taskModule.id:null;
				//    $scope.story.milestone = $scope.taskMilestone?$scope.taskMilestone.id:null;
				if (taskCheck) {
					$scope.story.left = $scope.story.estimate;
					//    			console.log("task",$scope.newTask);
					continueStory($scope.story);
				} else {
					$("#addTaskErrorMsg").addClass("see");
					$timeout(function () {
						$("#addTaskErrorMsg").removeClass("see");
					}, 2000);
				}
			}
		}]);


developmentCenter.controller('editModuleCtrl',
	function ($stateParams, $rootScope, $compile, markdownConverter, Modal, $state, debounce, $scope, $http, Messenger, $timeout) {
		$scope.subject = $rootScope.subject;
		$scope.edit = false;
		if (!$stateParams.module) {
			$http.post("ws/getModuleById", { projectId: $stateParams.project, moduleId: $stateParams.moduleId }).success(function (data) {
				if (data) {
					$scope.module = data
				} else {
					Messenger.post("模块不存在或被删除！")
				}
			})
		} else {
			$scope.module = $stateParams.module;
		}

		$scope.dateButtons = ['thisWeek', 'preWeek', 'thisMonth', 'preMonth', 'thisYear'];
		$scope.defaultButton = "thisWeek";
		$scope.search = { moduleId: $stateParams.moduleId, task: true, lack: false, week: { name: "choose" }, users: [], projects: [{ id: $stateParams.project }] };
		$scope.dateRange = { start: moment().startOf('week').toDate().getTime() };
		$scope.toBoardtask = function () {
			$state.go("project.boardtask", { moduleId: $stateParams.moduleId });
		}

		$scope.toBoardbug = function () {
			$state.go("project.boardbug", { moduleId: $stateParams.moduleId });
		}

		$scope.toBoardStory = function () {
			$state.go("project.boardstory", { moduleId: $stateParams.moduleId });
		}

		$scope.$watch("dateRange", function (v) {
			if (angular.isUndefined(v)) return;
			//查询项目中的成员
			$http.post("ws/getProjectUsers", { projectId: $stateParams.project, startDate: v.start, endDate: v.end }).success(function (data) {
				$scope.tempUsers = data.result.users;
				$scope.depts = data.result.depts;
				if (angular.isDefined($scope.dept)) {
					$scope.selectDept($scope.dept);
				} else {
					$scope.search.users = data.result.users;
					$scope.projectUsers = data.result.users;
				}
			});
			$scope.search.week.st = v.start;
			$scope.search.week.et = v.end;
		}, true);

		$scope.selectDept = function (item) {
			$scope.dept = item;
			var users = [];
			for (var i = 0; i < $scope.tempUsers.length; i++) {
				var orgs = $scope.tempUsers[i].orgIds;
				for (var j = 0; j < orgs.length; j++) {
					if (orgs[j] == item.id) {
						users.push($scope.tempUsers[i]);
						break;
					}
				}
			}
			$scope.projectUsers = users;
			$scope.search.users = users;
		}

		$scope.cleanDept = function () {
			$scope.projectUsers = angular.copy($scope.tempUsers);
			$scope.search.users = angular.copy($scope.tempUsers);
		}

		var debounceSearch = debounce(function (searchCondition) {
			$http.post("ws/getProjectLog", { search: searchCondition }).success(function (data) {
				var teamLogsGroupByUser = [];
				var total = 0;
				angular.forEach($scope.search.users, function (user) {
					var group = { userRealname: user.userRealname, userIsvalid: user.state, leaveDate: user.pasttime, logs: [], lacklogs: [], lackEstimate: 0, consumed: 0, overestimate: 0 };
					angular.forEach(data.result, function (v) {
						if (v.account == user.userName) {
							if ($scope.search.overtime) {
								group.logs.push(v);
								if (v.flag) {
									group.overestimate += v.overestimate;
								}
							} else {
								if (v.taskId) {
									group.logs.push(v);
									group.consumed += v.consumed;
								} else {
									group.lacklogs.push(v);
									group.lackEstimate += v.lackEstimate;
								}
							}
						}
					});
					group.consumed = group.consumed > 0 ? (parseInt(group.consumed) == group.consumed ? group.consumed : group.consumed.toFixed(1)) : 0;
					total += group.consumed;
					group.lackEstimate = group.lackEstimate > 0 ? (parseInt(group.lackEstimate) == group.lackEstimate ? group.lackEstimate : group.lackEstimate.toFixed(1)) : 0;
					group.overestimate = group.overestimate > 0 ? (parseInt(group.overestimate) == group.overestimate ? group.overestimate : group.overestimate.toFixed(1)) : 0;
					if ($scope.search.lack && !$scope.search.task) {
						group.lacklogs.length > 0 ? teamLogsGroupByUser.unshift(group) : teamLogsGroupByUser.push(group);
					} else {
						group.logs.length > 0 ? teamLogsGroupByUser.unshift(group) : teamLogsGroupByUser.push(group);
					}
				});
				$scope.teamLogs = teamLogsGroupByUser;
				$scope.totalConsumed = total;
			});
		}, 400);

		$scope.$watch("search", function (v) {
			debounceSearch(v);
		}, true);

		$scope.downLoadWorkLog = debounce(function () {
			if (angular.isDefined($scope.search) && angular.isDefined($scope.search.week)) {
				var deptId = "0";
				if (angular.isDefined($scope.dept) && $scope.dept != null) deptId = $scope.dept.id;
				window.location.href = 'report/projectLog/' + $scope.search.week.st + "/" + $scope.search.week.et + "/" + $stateParams.project + "/" + deptId;
			} else {
				Messenger.error("请选择补全查询条件");
			}
		}, 500);

		$http.post("ws/getMembers", { projectId: $stateParams.project }).success(function (data) {
			$scope.projectUsers = [];
			angular.forEach(data.result, function (v, n) {
				$scope.projectUsers.push({ userId: v.userDTO.user_id, userRealname: v.userDTO.user_realname, icon: v.userDTO.remark1, userName: v.userDTO.user_name });
			});
		});
		//负责人
		$scope.userClick = function (user) {
			$scope.module.assignedTo = user.userName;
			$scope.module.assigned = user;
		}

		//时间选择器--截止日期
		$scope.showDeadline = function () {
			$('#deadline').focus();
		}

		$scope.saveModule = function () {
			$http.post("ws/saveModule", {
				module: $scope.module
			}).success(function (data) {
				$('aside#detail-panel').removeClass('toggled');
				$state.go('^');
				$scope.$emit("updateModule", { module: data.result });
			})
		}

		$scope.moduleEditFun = function () {
			$scope.edit = true;
		}

		$scope.canCelModule = function () {
			$scope.edit = false;
		}
		$scope.delModule = function () {
			if ($scope.module && $scope.module.storyList.length > 0) {
				Messenger.post('该模块下存在需求，请先删除需求再删除模块！')
				return;
			}
			Modal.openConfirm({
				title: "删除确认",
				message: "是否删除模块【" + $scope.module.name + "】？",
				btnText: "删除",
				btnClass: "btn-block bgm-lightblue simple-shadow",
				onClose: function () {
					$http.post("ws/delMoudleById", {
						module: $scope.module
					}).success(function (data) {
						$scope.$emit("deleteModule", { module: data.result });
					});
				}
			});

		}

		$scope.orderup=function(){

		}
	}
)


developmentCenter
	.controller('addPlans', ['$stateParams', '$compile', 'markdownConverter', 'Modal', 'ModelFactory', '$state', '$scope', '$http', '$rootScope', 'Messenger', '$timeout',
		function ($stateParams, $compile, markdownConverter, Modal, ModelFactory, $state, $scope, $http, $rootScope, Messenger, $timeout) {
			$scope.projectPlan = {};

			$scope.allLevels = [{ title: '紧急', value: 0 }, { title: '一般', value: 1 }, { title: '可延迟', value: 2 }]
			$scope.projectPlan.level = { title: '紧急', value: 0 };
			initDS();
			//数据源加载
			function initDS() {
				var currentUrl = $rootScope.$state.current.templateUrl;
				var taskProjects = [];
				$http.post("ws/task/getMyDoingProjects", { userId: $rootScope.subject.userId })
					.success(function (data) {
						$.each(data.result, function (index, ele) {
							//若从项目详情页加载，且该项目的项目id存在与用户参与的项目中，则默认选择当前项目
							if ($state.includes("project") && $stateParams.project == ele.id) {
								$scope.projectId = $rootScope.$state.params.project;
								$scope.project = { id: ele.id, name: ele.name, owner: ele.owner };
							}
							taskProjects.push({ id: ele.id, name: ele.name, owner: ele.owner });
						})

						$scope.projects = taskProjects;
					});

				$http.post("e/project/op/select", { arcStatus: 1 }).success(function (data) {
					allProjects = data.result;
				});
			}

			$http.post("ws/getMembers", { projectId: $stateParams.project }).success(function (data) {
				$scope.projectUsers = [];
				angular.forEach(data.result, function (v, n) {
					$scope.projectUsers.push({ userId: v.userDTO.user_id, userRealname: v.userDTO.user_realname, icon: v.userDTO.remark1, userName: v.userDTO.user_name });
				});

				$scope.assignTo = $scope.projectUsers.find(u => u.userId === $rootScope.subject.userId);
			});
			//负责人
			$scope.userClick = function (user) {
				$scope.projectPlan.assignedTo = user.userName;
				$scope.projectPlan.assigned = user;
			}

			$scope.projectClick = function (project) {
				if ($scope.newTask.projectId == project.id) return;
				refreshContent();
				$scope.newTask.projectId = project.id;
				$scope.project = { id: project.id, name: project.name, owner: project.owner };
				$scope.taskProjectRed = false;
			}

			$scope.createNewPlan = function () {
				if (angular.isUndefined($scope.projectPlan.name)) {
					$scope.projectPlanNameRed = true;
				} else if (angular.isUndefined($scope.assignTo.userName)) {
					$scope.projectPlanNameRed = true;
				} else if (angular.isUndefined($scope.projectPlan.startDate)) {
					$scope.projectPlanNameRed = true;
				} else if (angular.isUndefined($scope.projectPlan.endDate)) {
					$scope.projectPlanNameRed = true;
				} else {
					$scope.projectPlan.assigned = $scope.assignTo;
					$scope.projectPlan.assignedTo = $scope.assignTo.userName;
					$scope.projectPlan.projectId = $scope.project;
					$scope.projectPlan.level = $scope.projectPlan.level.value;
					//按钮失效几秒防止重复提交
					timer(5);
					$http.post("ProjectPlan/plan",
						$scope.projectPlan
					).success(function (data) {
						Messenger.post("计划【" + $scope.projectPlan.name + "】创建成功!");
						Modal.close(data);
					});
				}
			}

			function timer(time) {
				var btn = $(".btn-save-task");
				btn.attr("disabled", true);  //按钮禁止点击
				var hander = setInterval(function () {
					if (time <= 0) {
						clearInterval(hander); //清除倒计时
						btn.attr("disabled", false);
						return false;
					} else {
						time--;
					}
				}, 1000);
			}

		}
	])


/***
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 */

developmentCenter
	.controller('addTaskPlans', ['$stateParams', '$compile', 'markdownConverter', 'Modal', 'ModelFactory', '$state', '$scope', '$http', '$rootScope', 'Messenger', '$timeout',
		function ($stateParams, $compile, markdownConverter, Modal, ModelFactory, $state, $scope, $http, $rootScope, Messenger, $timeout) {
			var $params = $scope.$params = {};
			$scope.addType = 'task'

			//临时Label数组
			$scope.tempTaskLabels = [];
			$scope.hideUEditor = true;
			$scope.filterOnly = false;
			$scope.newTask = { taskType: 1, estimate: 7.5, deadline: moment().day(5).valueOf() };
			$scope.newTask.description = "";

			if ($scope.$parent.$parent.source == "bug") {
				var bug = $scope.$parent.$parent.bug;
				$scope.newTask.name = "修复"

				$scope.taskUser = { id: bug.userId, name: bug.userRealname, icon: bug.userIcon, userName: bug.userName };
				$scope.newTask.name = "修复BUG:" + bug.bugTitle;
				$scope.newTask.bugId = bug.bugId;
				$scope.newTask.bugNo = bug.bugNo;

				$scope.taskTypeDisabled = true;
				$scope.taskProjectDisabled = true;
				$scope.taskContinueButtonHide = true;
				$scope.project = { id: bug.bugProject, name: bug.bugProjectName };
				//			if(bug.bugStatus==4)$scope.taskWeek = unknow;
			} else if ($scope.$parent.$parent.source == "story") {
				var story = $scope.$parent.$parent.story;
				$scope.newTask.storyId = story.id;
				$scope.newTask.description = "[任务来源【需求：" + story.title + "】](#/project/" + story.project.id + "/story/" + story.id + ") ";
				$scope.description = "[任务来源【需求：" + story.title + "】](#/project/" + story.project.id + "/story/" + story.id + ") ";
				//        	$scope.hideUEditor = false;
				//需求添加任务应默认显示模块、里程碑、标签信息
				loadStoryAndComments();
				$scope.taskMilestone = story.milestone;
				$scope.taskModule = story.module;
				$scope.taskStory = story;
			}

			//选择添加类型
			$scope.selectAddType = function (type) {
				//var addStyle={"border-bottom-width":"3px","border-bottom-style":"solid","border-bottom-color":"#03a9f4"}
				if (type == "addtask") {
					if (!$('#addtask').hasClass('add-border')) {
						$('#addtask').addClass('add-border');
					}
					$('#addPlan').removeClass('add-border');
					$scope.addType = 'task';
				} else if (type == "addPlan") {
					if (!$('#addPlan').hasClass('add-border')) {
						$('#addPlan').addClass('add-border');
					}
					$('#addtask').removeClass('add-border');
					$scope.addType = 'plan';
				}
			}

			function loadStoryAndComments() {
				$http.post("ws/getStoryAndComments", {
					"storyId": $scope.newTask.storyId
				}).success(function (data) {
					if (data.result.storyLabel)
						$scope.tempTaskLabels = data.result.storyLabel;
				});
			};

			$scope.taskTypes = [{ name: "普通任务", id: 1 }, { name: "售前任务", id: 2 }, { name: "运维任务", id: 3 }, { name: "支持任务", id: 4 }];
			$scope.taskType = { name: "普通任务", id: 1 };

			var taskProjects = [];
			var projectUserOptions = [];
			var allProjects;
			var allUsers;
			initDS();

			//数据源加载
			function initDS() {
				var currentUrl = $rootScope.$state.current.templateUrl;
				setCurrentUser();

				$http.post("ws/task/getMyDoingProjects", { userId: $rootScope.subject.userId })
					.success(function (data) {
						$.each(data.result, function (index, ele) {
							//若从项目详情页加载，且该项目的项目id存在与用户参与的项目中，则默认选择当前项目
							if ($state.includes("project") && $stateParams.project == ele.id) {
								$scope.newTask.projectId = $rootScope.$state.params.project;
								$scope.project = { id: ele.id, name: ele.name, owner: ele.owner };
								$scope.taskProjectDisabled = true;//项目不允许切换
								afterSelectProject();
							} else if ($scope.project && $scope.project.id == ele.id) {
								$scope.newTask.projectId = $scope.project.id;
								$scope.project = { id: ele.id, name: ele.name, owner: ele.owner };
								$scope.taskProjectDisabled = true;//项目不允许切换
								afterSelectProject();
							}
							taskProjects.push({ id: ele.id, name: ele.name, owner: ele.owner });
						})
						$scope.projects = taskProjects;
					});

				$http.post("e/project/op/select", { arcStatus: 1 }).success(function (data) {
					allProjects = data.result;
				});
				/*$http.get("ProjectPlan/all/project", {
					params:{
						projectId: $stateParams.project,
					}
				}).success(function (data) {
					data.forEach(function (i) {
						if (i.id == $scope.planId) {
							$scope.taskprojectPlan = i;
						}
					})
					$scope.projectPlans = data;
					$timeout(function(){
						var ztree = $("#projectPlan").controller("c2-dropdown").getTree();
						ztree.expandNode(ztree.getNodes()[0], true, false, true);
					},400);
				})*/
			}

			function setCurrentUser() {
				$scope.taskUser = {
					user_id: $rootScope.subject.user_id,
					user_name: $rootScope.subject.user_name,
					user_realname: $rootScope.subject.user_realname,
					remark1: $rootScope.subject.remark1
				};

				$scope.newTask.assignedTo = $scope.taskUser.user_name;
				$scope.newTask.userId = $scope.taskUser.user_id;
				$scope.newTask.userRealname = $scope.taskUser.user_realname;
			}
			function cleanCurrentUser() {
				$scope.taskUser = null;
				$scope.newTask.assignedTo = null;
				$scope.newTask.userId = null;
				$scope.newTask.userRealname = null;
			}

			$scope.typeClick = function (taskType) {
				if ($scope.newTask.taskType == taskType.id) return;
				if ((($scope.newTask.taskType == 2 || $scope.newTask.taskType == 4) && taskType.id == 3) ||
					(($scope.newTask.taskType == 3 || $scope.newTask.taskType == 4) && taskType.id == 2) ||
					(($scope.newTask.taskType == 3 || $scope.newTask.taskType == 2) && taskType.id == 4)) {
					$scope.newTask.taskType = taskType.id;
					return;
				}
				$scope.newTask.taskType = taskType.id;
				setCurrentUser();

				if (taskType.id == 4) {//支持单类型
					$http.post("ws/getSupportList", {}).success(function (data) {
						$scope.supports = data.result;
					})
					//查询申请部门以及用户信息
					$http.post("ws/chinacreatorDepartment", {}).success(function (allDept) {
						$scope.supportOrgs = angular.copy(allDept.result);
						$http.post("ws/findUserBelongDepartment", { userId: $rootScope.subject.userId }).success(function (data) {
							$scope.org = $scope.supportOrgs.find(o => o.id == data.result);
						});
					});
					$http.post("ws/getMembersByWorgOrg", {}).success(function (data) {
						$scope.manageUsers = angular.copy(data.result);//执行负责人
					});
				}
				if (taskType.id == 1) {
					$scope.projects = taskProjects;

					var projectExist = false;
					if ($scope.project) {
						angular.forEach($scope.projects, function (p) {
							if (p.id == $scope.project.id) {
								projectExist = true;
								$scope.taskUsers = projectUserOptions;
								$scope.filterOnly = false;
								$("#taskUserDropdown").find(".dropdown-search").find("input").attr("placeholder", "项目成员搜索...");
								//        					cleanCurrentUser();
							}
						});
					}
					if (!projectExist) {
						$scope.project = null;
						$scope.newTask.projectId = null;
						refreshContent();
					}
				} else {
					$scope.projects = allProjects;

					if (angular.isUndefined(allUsers)) {
						$http.get("ws/dept/getUsersByDeptId").success(function (data) {
							allUsers = data.result;
							$scope.taskUsers = allUsers;
							$scope.filterOnly = true;
						});
					} else {
						$scope.taskUsers = allUsers;
						$scope.filterOnly = true;
					}

					$("#taskUserDropdown").find(".dropdown-search").find("input").attr("placeholder", "全员搜索...");
				}

			}

			$scope.showHideSupport = function () {
				$scope.taskDown = !$scope.taskDown;
				$("#taskDiv").slideToggle(400);
				$scope.newSupport = {};
			}

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
			};

			function zTreeOnClick(event, treeId, treeNode) {
				$scope.org = treeNode;
			}

			$scope.supportClick = function (support) {
				$scope.taskSupport = support;
			}

			//新增支持单
			$scope.creatSupport = function () {
				var check = true;
				$scope.addSupportButtonDisabled = true;
				if (angular.isUndefined($scope.newSupport.title)) {
					$scope.suppportTitleRed = true;
					check = false;
				}
				if (angular.isUndefined($scope.newSupport.manageUser)) {
					$scope.suppportUserRed = true;
					check = false;
				}
				if (angular.isUndefined($scope.project)) {
					$scope.taskProjectRed = true;
					check = false;
				}
				if (!check) {
					$scope.addSupportButtonDisabled = false;
					return;
				} else {
					$scope.newSupport.orgId = $scope.org.id;
					$scope.newSupport.projectId = $scope.project.id;
					$http.post("ws/support/createSupportNote", { support: $scope.newSupport }).success(function (data) {
						$scope.taskSupport = data.result;
						$scope.addSupportButtonDisabled = false;
					})
				}
			}

			$scope.treePlanOptions = {
				view: {
					selectedMulti: false
				},
				data: {
					simpleData: {
						enable: true,
						pIdKey: "parent"
					}
				},
				callback: {
					onClick: function (event, treeId, treeNode, clickFlag) {
						$scope.$apply(function () {
							$scope.taskprojectPlan = treeNode;
						});
					}
				}
			};

			$scope.projectClick = function (project) {
				if ($scope.newTask.projectId == project.id) return;
				refreshContent();
				$scope.newTask.projectId = project.id;
				$scope.project = { id: project.id, name: project.name, owner: project.owner };
				$scope.taskProjectRed = false;
				afterSelectProject();
			}

			$scope.cleanModule = function () {
				$scope.taskModule = null;
			}

			function refreshContent() {
				//刷新
				$scope.taskModule = null;
				$scope.taskStory = null;
				//        	$scope.taskUsers = [];
				$scope.taskMilestone = null;
				//          $scope.tempTaskLabels = [];
			}

			function afterSelectProject() {
				//普通任务才需要查机构用户
				if ($scope.taskType.id == 1) {
					//初始化成员下拉 - 默认当前用户userId
					//若新选择项目也有已选择的成员，不变
					if (angular.isUndefined($scope.taskUser) || $scope.taskUser == null) setCurrentUser();
					$http.post("ws/task/getMembersForAdd", { projectId: $scope.newTask.projectId })
						.success(function (data) {
							var userExistInProject = false;
							projectUserOptions = [];
							angular.forEach(data.result, function (u) {
								projectUserOptions.push(u);
								if (angular.equals(u.user_id, $scope.newTask.userId)) {
									$scope.taskUser = u;
									userExistInProject = true;
								}
							});
							if (!userExistInProject) setCurrentUser();

							$scope.taskUsers = projectUserOptions;
							//            		$scope.filterOnly = false;
						});
				} else {
					$http.post("ws/task/getMembersForAdd", { projectId: $scope.newTask.projectId }).success(function (data) {
						projectUserOptions = data.result;
					});
				}

				//模块
				/*$http.post("e/module/op/selectModule", { projectId: { id: $scope.newTask.projectId } })
					.success(function (data) {
						$scope.moduleNodes = data.result;
					});*/
				//需求
				$http.post("ws/getAllStory", { projectId: $scope.newTask.projectId, milestoneId: $scope.newTask.milestoneId }).success(function (data) {
					if (data.result.length > 0) {
						$scope.storys = data.result;
					} else {
						$scope.storys = [];
					}
				})
				//里程碑
				$http.post("ws/milestone/selectAllUnclosedMilestone", { milestone: { "projectId": $scope.newTask.projectId } })
					.success(function (data) {
						if (data.result.length > 0) {
							$scope.milestones = data.result;
						} else {
							$scope.milestones = [];
						}
					});
				$http.get("ProjectPlan/all/project", {
					params: {
						projectId: $scope.newTask.projectId,
					}
				}).success(function (data) {
					data.forEach(function (i) {
						if (i.id == $scope.planId) {
							$scope.taskprojectPlan = i;
						}
					})
					var rootNode = {
						id: 0,
						name: $scope.project.name,
					}
					data.push(rootNode)
					$scope.projectPlans = data;
					$timeout(function () {
						var ztree = $("#projectPlan").controller("c2-dropdown").getTree();
						ztree.expandNode(ztree.getNodes()[0], true, false, true);
					}, 400);
				})
				//项目关于任务确认的设置
				$http.post("ws/getProjectPreference", { projectId: $scope.newTask.projectId, preferNames: ["projectTaskConfirmState"] })
					.success(function (data, status, headers, config) {
						if (!angular.isUndefined(data.result)
							&& !angular.isUndefined(data.result.projectTaskConfirmState)
							&& !angular.isUndefined(data.result.projectTaskConfirmState.preferContent)) {
							$scope.taskConfirm = "true" == data.result.projectTaskConfirmState.preferContent;
						} else {
							$scope.taskConfirm = false;
						}
					});
				/*$http.post("e/c2_comunity_milestone/op/selectMilestone", {projectId: $scope.newTask.projectId})
						.success(function (data) {
								if (data.result.length > 0) {
										$scope.milestones = data.result;
								} else {
										$scope.milestones = [];
								}
						});*/

				//标签
				$http.post("ws/getAllLabels", { projectId: $scope.newTask.projectId })
					.success(function (data) {
						$.each(data.result.AllLabels, function (index, val) {
							val.backgroundStyle = {
								"background-color": val.color,
								"border-radius": "2px",
								"color": getTextColorByBackColor(val.color)
							};
						});
						$scope.allLabels = data.result.AllLabels;
					});
			}

			//module	
			$scope.treeOptions = {
				view: {
					selectedMulti: false
				},
				data: {
					simpleData: {
						enable: true,
						pIdKey: "parent"
					}
				},
				callback: {
					onClick: function (event, treeId, treeNode, clickFlag) {
						$scope.$apply(function () {
							$scope.taskModule = treeNode;
							$scope.newTask.moduleId = treeNode.id;
						});
						// $("#newModuleDropdown").controller("c2-dropdown").toggle();
					}
				}
			};

			//user
			$scope.userMenuClick = function (user) {
				if (!user.remark1) {//调用远程用户，没有用户图标处理
					$http.post("ws/user/getUserByUsername", { name: user.userName }).success(function (data) {
						user.remark1 = data.result.remark1 || "assets/img/profile-pics/1.jpg";
						$scope.newTask.userId = data.result.userId;
					});
				}

				$scope.newTask.assignedTo = user.user_name;
				$scope.newTask.userRealname = user.user_realname;
				$scope.taskUser = user;
			}

			//mileStone
			$scope.mileStoneMenuClick = function (mileStone) {
				$scope.taskMilestone = mileStone;
				$scope.newTask.milestoneId = mileStone.id;//切换里程碑后需求重新变化
				$scope.taskStory = undefined;
				$scope.newTask.storyId = undefined;
				$http.post("ws/getAllStory", { projectId: $scope.newTask.projectId, milestoneId: $scope.newTask.milestoneId }).success(function (data) {
					if (data.result.length > 0) {
						$scope.storys = data.result;
					} else {
						$scope.storys = [];
					}
				})
			}

			//story
			$scope.storyMenuClick = function (story) {
				$scope.taskStory = story;
				$scope.newTask.storyId = story.id;
			}

			var mouseDownPosition = undefined;

			$scope.ueMouseDown = function ($event) {
				mouseDownPosition = { x: $event.clientX, y: $event.clientY };
			}

			$scope.ueMouseUp = function ($event) {
				if (mouseDownPosition) {
					var moved = Math.abs($event.clientX - mouseDownPosition.x) + Math.abs($event.clientY - mouseDownPosition.y);
					if (moved < 10) {
						startEditing($event);
					}
					mouseDownPosition = undefined;
				}
			}

			function startEditing($event) {
				if ($($event.target).is('a') || $($event.target).is('img')) {
					return;
				}
				$scope.hideUEditor = false;
			}

			$scope.saveDesc = function () {
				$scope.newTask.description = $scope.description;
				$scope.hideUEditor = true;
			}
			$scope.cancelDesc = function () {
				$scope.hideUEditor = true;
				$scope.description = $scope.newTask.description;
			}

			//时间选择器--截止日期
			$scope.showDeadline = function () {
				$('#deadline').focus();
			}

			//保存新建的任务
			function saveTask(task) {
				var url = "ws/creatTask";
				$http.post(url, { task: task, stageName: task.stageName, taskConfirm: $scope.taskConfirm }).success(function (data) {
					Messenger.post("任务【" + $scope.newTask.name + "】创建成功!");
					//执行保存label的方法
					var labelTask = [];
					$.each($scope.tempTaskLabels, function (index, ele) {
						labelTask.push({
							taskId: data.result.id,
							labelId: ele.id
						});
					});
					$http.post("ws/updateTaskLabels", { taskId: data.result.id, labels: labelTask });

					if (task.bugId) {
						//修改bug状态为打开
						$http.post("ws/bug/changeBugStatus", { bugId: task.bugId, status: 2 }).success(function () {
							//修改bug负责人为任务分配的人
							$http.post("ws/bug/changeBugMan", { bugId: task.bugId, man: task.assignedTo, changeMan: $rootScope.subject.userName, manRealname: task.userRealname });
						});
					}
					//			$http.post("message/"+$scope.newTask.userId+"/pushTask",{});

					/* //发送通知消息
					if ($rootScope.subject.userName != $scope.newTask.assignedTo) {
						$http.post("ws/pushSystem", {
							msgTo: $scope.newTask.userId,
							msgContent: "[" + $rootScope.subject.userRealname + "]将任务[" + $scope.newTask.name + "]分配给了你:",
							link: "tasks/" + $scope.newTask.id,
							isPersistent: true
						});
					} */

					//关闭新建任务弹窗时，每个弹窗事件的回调各自处理页面刷新事件。
					Modal.close(data.result);
					//发送广播，刷新团队任务列表
					$rootScope.$broadcast("topCreateNewTask", { task: data.result, stageName: task.stageName });
				});
			}

			$scope.createNewTask = function () {
				var taskCheck = true;
				if (angular.isUndefined($scope.project) || $scope.project == null) {
					$scope.taskProjectRed = true;
					taskCheck = false;
				}
				if (angular.isUndefined($scope.newTask.name) || $scope.newTask.name == '') {
					$scope.taskNameRed = true;
					taskCheck = false;
				}
				if (angular.isUndefined($scope.taskUser)) {
					$scope.taskUserRed = true;
					taskCheck = false;
				}
				if (angular.isUndefined($scope.taskprojectPlan)) {
					$scope.taskPlanRed = true;
					taskCheck = false;
				}

				$scope.newTask.milestoneId = $scope.taskMilestone ? $scope.taskMilestone.id : null;
				$scope.newTask.storyId = $scope.taskStory ? $scope.taskStory.id : null;
				$scope.newTask.moduleId = $scope.taskStory ? ($scope.taskStory.module ? $scope.taskStory.module.id : null) : null;
				$scope.newTask.projectId = $scope.project ? $scope.project.id : null;
				$scope.newTask.stageId = $scope.$parent.$parent.stage ? $scope.$parent.$parent.stage.id : null;
				$scope.newTask.stageName = $scope.$parent.$parent.stage ? $scope.$parent.$parent.stage.name : null;
				$scope.newTask.projectPlan = $scope.taskprojectPlan ? $scope.taskprojectPlan : null;
				$scope.newTask.supportId = $scope.taskSupport ? $scope.taskSupport.id : null;
				if (taskCheck) {
					//将按钮失效几秒钟避免重复提交数据
					timer(5);
					$scope.newTask.left = $scope.newTask.estimate;
					//    			console.log("task",$scope.newTask);
					saveTask($scope.newTask);
				} else {
					$("#addTaskErrorMsg").addClass("see");
					$timeout(function () {
						$("#addTaskErrorMsg").removeClass("see");
					}, 2000);
				}

			}

			function timer(time) {
				var btn = $(".btn-save-task");
				btn.attr("disabled", true);  //按钮禁止点击
				var hander = setInterval(function () {
					if (time <= 0) {
						clearInterval(hander); //清除倒计时
						btn.attr("disabled", false);
						return false;
					} else {
						time--;
					}
				}, 1000);
			}

			function continueTask(task) {
				var url = "ws/creatTask";
				$http.post(url, { task: task, stageName: task.stageName, taskConfirm: $scope.taskConfirm }).success(function (data) {
					Messenger.post("任务【" + $scope.newTask.name + "】创建成功!");
					$scope.newTask.name = "";
					$scope.newTask.description = "";
					//执行保存label的方法
					var labelTask = [];
					$.each($scope.tempTaskLabels, function (index, ele) {
						labelTask.push({
							taskId: data.result.id,
							labelId: ele.id
						});
					});
					$http.post("ws/updateTaskLabels", { taskId: data.result.id, labels: labelTask });
					//			$http.post("message/"+$scope.newTask.userId+"/pushTask",{});

					/* 	//发送通知消息
						if ($rootScope.subject.userName != $scope.newTask.assignedTo) {
							$http.post("ws/pushSystem", {
								msgTo: $scope.newTask.userId,
								msgContent: "[" + $rootScope.subject.userRealname + "]将任务[" + $scope.newTask.name + "]分配给了你:",
								link: "tasks/" + $scope.newTask.id,
								isPersistent: true
							});
						} */

					//发送广播，刷新团队任务列表
					$rootScope.$broadcast("topCreateNewTask", { task: data.result, stageName: task.stageName });
				});

			}


			$scope.continueNewTask = function () {
				var taskCheck = true;
				if (angular.isUndefined($scope.project) || $scope.project == null) {
					$scope.taskProjectRed = true;
					taskCheck = false;
				}
				if (angular.isUndefined($scope.newTask.name) || $scope.newTask.name == '') {
					$scope.taskNameRed = true;
					taskCheck = false;
				}
				if (angular.isUndefined($scope.taskUser)) {
					$scope.taskUserRed = true;
					taskCheck = false;
				}
				if (angular.isUndefined($scope.taskprojectPlan)) {
					$scope.taskPlanRed = true;
					taskCheck = false;
				}
				$scope.newTask.milestoneId = $scope.taskMilestone ? $scope.taskMilestone.id : null;
				$scope.newTask.storyId = $scope.taskStory ? $scope.taskStory.id : null;
				$scope.newTask.stageId = $scope.$parent.$parent.stage ? $scope.$parent.$parent.stage.id : null;
				$scope.newTask.stageName = $scope.$parent.$parent.stage ? $scope.$parent.$parent.stage.name : null;
				$scope.newTask.taskprojectPlan = $scope.taskprojectPlan ? $scope.taskprojectPlan : null;
				$scope.newTask.supportId = $scope.taskSupport ? $scope.taskSupport.id : null;

				if (taskCheck) {
					$scope.newTask.left = $scope.newTask.estimate;
					//    			console.log("task",$scope.newTask);
					continueTask($scope.newTask);
				} else {
					$("#addTaskErrorMsg").addClass("see");
					$timeout(function () {
						$("#addTaskErrorMsg").removeClass("see");
					}, 2000);
				}

			}


			$scope.projectPlan = {};
			$scope.allLevels = [{ title: '紧急', value: 0 }, { title: '一般', value: 1 }, { title: '可延迟', value: 2 }]
			$scope.projectPlan.level = { title: '紧急', value: 0 };

			$scope.createNewPlan = function () {
				$scope.projectPlanNameRed = false;
				if (angular.isUndefined($scope.projectPlan.name)) {
					$scope.projectPlanNameRed = true;
				} else if (angular.isUndefined($scope.projectPlan.level)) {
					$scope.projectPlanNameRed = true;
				} else if (angular.isUndefined($scope.projectPlan.startDate)) {
					$scope.projectPlanNameRed = true;
				} else if (angular.isUndefined($scope.projectPlan.endDate)) {
					$scope.projectPlanNameRed = true;
				} else {
					$scope.projectPlan.projectId = $scope.project;
					$scope.projectPlan.parent = $scope.planId;
					$scope.projectPlan.level = $scope.projectPlan.level.value;
					//按钮失效几秒防止重复提交
					timer(5);
					$http.post("ProjectPlan/plan",
						$scope.projectPlan
					).success(function (data) {
						Messenger.post("项目【" + $scope.projectPlan.name + "】创建成功!");
						Modal.close(data);
					});
				}
			}

			$http.post("ws/getMembers", { projectId: $stateParams.project }).success(function (data) {
				$scope.projectUsers = [];
				angular.forEach(data.result, function (v, n) {
					$scope.projectUsers.push({ userId: v.userDTO.user_id, userRealname: v.userDTO.user_realname, icon: v.userDTO.remark1, userName: v.userDTO.user_name });
				});
			});
			//负责人
			$scope.userClick = function (user) {
				$scope.projectPlan.assignedTo = user.userName;
				$scope.projectPlan.assigned = user;
			}

		}]);
