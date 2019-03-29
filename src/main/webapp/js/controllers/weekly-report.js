developmentCenter.controller('weeklyReportListController', ['$http', '$state', '$stateParams', 'ModelFactory', '$scope', 'Modal', 'Messenger',
	function ($http, $state, $stateParams, ModelFactory, $scope, Modal, Messenger) {

		var $params = $scope.$params = {};

		$params.page = {
			pageSize: 10,
			pageIndex: 1
		}

		init();

		function init() {
			var allDs = ModelFactory.wrap([
				ModelFactory.entity("project", "id", { id: $stateParams.project }),
				ModelFactory.ws("wr/createWeeklyReportIfLack", { projectId: $stateParams.project, pageable: $params.page }),
				ModelFactory.ws("wr/getProjectWRSubmit", { projectId: $stateParams.project }),
			]);

			allDs.loadAll().then(function () {
				$params.project = allDs.datasources[0];

				$scope.reportList = allDs.datasources[1].result.contents;
				$params.page.total = allDs.datasources[1].result.total;

				$params.submit2 = allDs.datasources[2].result;

			});
		}

		function getSubmitMembers() {
			$http.post("ws/wr/getProjectWRSubmit", { projectId: $stateParams.project }).success(function (data) {
				$params.submit2 = data.result;
			});
		}

		function createWeeklyReportIfLack(pageIndex) {
			if (angular.isDefined(pageIndex)) {
				$params.page.pageIndex = pageIndex;
			}
			$http.post("ws/wr/createWeeklyReportIfLack", { projectId: $stateParams.project, pageable: $params.page }).success(function (data) {
				$scope.reportList = data.result.contents;
				$params.page.total = data.result.total;
			});
		}

		$scope.pageList = function (pageIndex) {
			//点击当前页码就不刷新了
			if (!angular.equals(pageIndex, $params.page.pageIndex)) {
				createWeeklyReportIfLack(pageIndex);
			}
		}

		function refresh() {
			init();
		}

		$scope.initWeekReport = function (report) {

			switch (report.status) {
				case "超前": report.statusClass = 'report-label-earlier'; break;
				case "正常": report.statusClass = 'report-label-normal'; break;
				case "逾期": report.statusClass = 'report-label-latter'; break;
				case "缺失": report.statusClass = 'report-label-lose'; break;
			}
			if (angular.isDefined(report.statistics)) {
				var statistics = report.statistics;
				var total = statistics.total;
				var normal = statistics.completed + statistics.crossWeek;
				var canceled = statistics.canceled;
				var consumed = statistics.consumed;
				var estimate = statistics.estimate;
				var member = statistics.member;
				var delayed = statistics.delayed;
				if (angular.equals(statistics.delayed, 0)) {
					delayed = total - normal;
				}
				var worked = statistics.worked;
				if (angular.isUndefined(worked)) {
					worked = 0;
				}

				var normalRate = normal / total * 100;
				var canceledRate = canceled / total * 100;
				var delayedRate = delayed / total * 100;

				report.statistics.normalRate = normalRate;
				report.statistics.delayedRate = delayedRate;
				report.statistics.delayed = delayed;
				report.statistics.normal = normal;

				report.statisticsDesc = "人数：" + member + "\n正常：" + normal + "\n取消：" + canceled + "\n逾期：" + delayed + "\n总任务：" + total + "\n周工时：" + worked + "\n总工时：" + consumed + "/" + estimate + "\n";
			}

		}

		$scope.addWeekReportSendTo = function () {
			Modal.open('project/weeklyReport/submit-to.html', {}, {
				title: "周报提交给",
				size: 'big',
				onClose: function (data) {

				},
				onDismiss: function (data) {
					getSubmitMembers();
				}
			});
		}

		$http.post("ws/isProjectPermitedByBatch", { projectId: $stateParams.project, permExprs: ["weekReport_submit", "advancedSet_menu"] })
			.success(function (data, status, headers, config) {
				$scope.weekReportSubmit = data.result["weekReport_submit"];
				$scope.advancedSetMenu = data.result["advancedSet_menu"];
			});

		$scope.weekReportClick = function (report) {
			//缺失则打开新建界面,不缺失则打开详情界面
			if (angular.equals(report.status, '缺失')) {
				if ($scope.weekReportSubmit) {
					$state.go('project.weeklyReport', { reportId: report.id });
				} else {
					Messenger.error("您没有可编辑权限！");
				}
			} else {
				$state.go('project.weeklyReport', { reportId: report.id });
			}

		}
	}]);

developmentCenter.controller('ReportFormListController',
	['Modal', 'ModelFactory', '$scope', '$http', '$stateParams', '$state', '$timeout',
		function (Modal, ModelFactory, $scope, $http, $stateParams, $state, $timeout) {

			var $params = $scope.$params = {};

			$params.isShowCurrentWeek = true;

			$params.page = {
				pageSize: 10,
				pageIndex: 1
			}

			$params.selectedProject = { name: "所有项目" };

			init();

			function init() {
				var allDs = ModelFactory.wrap([ModelFactory.ws("wr/getWeeklyReportProjectAuthorInfos", {}),
				ModelFactory.ws("wr/getWeeklyReportList", { pageable: $params.page })
				]);

				allDs.loadAll().then(function () {
					//有提交的周报
					if (allDs.datasources[0].result.length > 0) {
						allDs.datasources[0].result.splice(0, 0, { name: "所有项目" });
						$params.projects = allDs.datasources[0].result;
					}

					if (angular.isDefined(allDs.datasources[1].result)) {
						$scope.reportList = allDs.datasources[1].result.contents;
						$params.page.total = allDs.datasources[1].result.total;
					}
				});
			}

			function getWeeklyReports(projectId, pageIndex) {
				$params.currentProjectId = projectId;

				$params.page.pageIndex = 1;

				$http.post("ws/wr/getWeeklyReportListByProjectId", { projectId: projectId, pageable: $params.page, statusFilter: "缺失" }).success(function (data) {
					$scope.reportList = data.result.contents;
					$params.page.total = data.result.total;
				});
			}

			$scope.pageList = function (pageIndex) {
				if (!angular.equals(pageIndex, $params.page.pageIndex)) {
					$params.page.pageIndex = pageIndex;
					$http.post("ws/wr/getWeeklyReportListByProjectId", { projectId: $params.currentProjectId, pageable: $params.page, statusFilter: "缺失" }).success(function (data) {
						$scope.reportList = data.result.contents;
						$params.page.total = data.result.total;
					});
				}
			}

			$scope.initWeekReport = function (report) {

				switch (report.status) {
					case "超前": report.statusClass = 'report-label-earlier'; break;
					case "正常": report.statusClass = 'report-label-normal'; break;
					case "逾期": report.statusClass = 'report-label-latter'; break;
					case "缺失": report.statusClass = 'report-label-lose'; break;
				}
				if (angular.isDefined(report.statistics)) {
					var statistics = report.statistics;
					var total = statistics.total;
					var normal = statistics.completed + statistics.crossWeek;
					var canceled = statistics.canceled;
					var consumed = statistics.consumed;
					var estimate = statistics.estimate;
					var member = statistics.member;
					var delayed = statistics.delayed;
					if (angular.equals(statistics.delayed, 0)) {
						delayed = total - normal;
					}
					var worked = statistics.worked;
					if (angular.isUndefined(worked)) {
						worked = 0;
					}

					var normalRate = normal / total * 100;
					var canceledRate = canceled / total * 100;
					var delayedRate = delayed / total * 100;

					report.statistics.normalRate = normalRate;
					report.statistics.delayedRate = delayedRate;
					report.statistics.delayed = delayed;
					report.statistics.normal = normal;

					report.statisticsDesc = "人数：" + member + "\n正常：" + normal + "\n取消：" + canceled + "\n逾期：" + delayed + "\n总任务：" + total + "\n周工时：" + worked + "\n总工时：" + consumed + "/" + estimate + "\n";
				}

			}

			$scope.reportClick = function (report) {
				//同一时刻只能有一个report是打开状态的
				var openedReport = $('.report-open-title');
				if (openedReport.length != 0) {
					if (angular.equals(Number(openedReport.attr("index")), report.index)) {
						$scope.reportList[openedReport.attr("index")].open = !$scope.reportList[openedReport.attr("index")].open;
					} else {
						$scope.reportList[openedReport.attr("index")].open = !$scope.reportList[openedReport.attr("index")].open;
						if (angular.isUndefined(report.taskList)) {
							getTaskSnapshots(report);
						} else {
							report.open = !report.open;
						}
					}
				} else {
					if (angular.isUndefined(report.taskList)) {
						getTaskSnapshots(report)
					} else {
						report.open = !report.open;
					}
				}
			}

			function getTaskSnapshots(report) {
				$http.post("ws/wr/getTaskSnapshots", { snapshot: { reportId: report.id } }).success(function (data) {
					report.taskList = report.currentTaskList = data.result;
					report.open = true;
				});
			}

			$scope.projectClick = function (project) {
				$params.selectedProject = project;
				getWeeklyReports(project.id);
			}

			$scope.switchWeekReportView = function (report) {
				if (report.isShowCurrentWeek == true) {
					report.taskList = report.currentTaskList;
				} else {
					if (angular.isUndefined(report.nextWeekTaskList)) {
						var start = {
							year: $params.weekReport.year, week: $params.weekReport.week + 1
						}
						var end = {
							year: $params.weekReport.year, week: $params.weekReport.week + 1
						}
						$http.post("ws/wr/getWeeklyReportTasksOfNextWeek", { condition: { projectId: report.projectId, startParam: start, endParam: end } }).success(function (data) {
							report.taskList = report.nextWeekTaskList = data.result;
						});
					} else {
						report.taskList = report.nextWeekTaskList;
					}
				}
			}

		}]);

developmentCenter.controller('weeklyReportController', ['$ocLazyLoad', '$timeout', 'socketio', '$rootScope', '$compile', 'Modal', '$http', 'ModelFactory', '$scope', '$stateParams', '$state', 'debounce',
	function ($ocLazyLoad, $timeout, socketio, $rootScope, $compile, Modal, $http, ModelFactory, $scope, $stateParams, $state, debounce) {
		var $params = $scope.$params = {};
		$scope.reportStart = "";
		$scope.reportEnd = "";
		$params.isShowCurrentWeek = true;
		$scope.projectId = "";
		$params.startParam = {};
		$params.endParam = {};
		$params.storyIds=[];
		$params.StoryList=[];
		$scope.modelList=[];
		//ztree setting
		$scope.treeOptions = {
			data: {
				simpleData: {
					enable: true,
					idKey: "id",
					pIdKey: "parent",
					rootPId: 0
				}
			},
			callback: {
				onClick: function onClick(event, treeId, treeNode) {
					$scope.treeNodesIds = treeNode.id
					var label = $('body').find(".ui-tabs-active.ui-state-active").children().text();
					if(label=='日志'){
						logsList();
					}else{
						$http.post("wr/getWeeklyReportTaskSnapshots", { condition: { reportId: $params.weekReport.id, belongToNextWeek: "false", moduleId: $scope.treeNodesIds,projectId:$scope.projectId } }).success(function (result) {
							$params.taskList = $params.currentWeekTaskList = $scope.memberList = result.result;
							if($params.taskList.length==0)$scope.recordNullMsg=true;
							if($params.taskList.length>0){
								$.each($params.taskList,function(index,ele){
									$.each(ele.taskSnapShotList,function(i,item){
										if(item.storyId){
											if($params.storyIds.indexOf(item.storyId)==-1){
												$params.storyIds.push(item.storyId)
											}
										}
									})
								})
								$http.post('ws/getStoryListByIds',{ids:$params.storyIds}).success(function(result){
									$params.StoryList=result.result
								})
							}else{
								$params.StoryList=[];
							}
						})
					}
				},
			}
		};
		
		$scope.getWeekReportModule = function(projectId){
			//模块
			$http.post("ws/getWeekReportModule", { projectId: projectId,reportId:$stateParams.reportId })
				.success(function (data) {
					$scope.modelList = angular.copy(data.result);
					var modList = [{ id: 0, name: "所有模块", parent: undefined, projectId: {} }];
					for (var i = 0; i < data.result.length; i++) {
						data.result[i].name = data.result[i].name+"("+data.result[i].taskNum+")";
						modList.push(data.result[i]);
					}
					$scope.treeNodes = modList;
					$scope.treeNodesIds = 0;

					setTimeout(function () {
						//加载ztree
						$ocLazyLoad.load(['assets/ztree/jquery.ztree.all.min.js', 'assets/ztree/zTreeStyle.css'])
							.then(function () {
								$scope.$watch("treeNodes", function (v) {
									if (angular.isUndefined(v))
										return;
									$scope.moduleTree = $.fn.zTree.init($("#moduleTree"), $scope.treeOptions, $scope.treeNodes);
									$scope.moduleTree.expandNode($scope.moduleTree.getNodeByParam("id", '0', null), true, false, true);
									$scope.moduleTree.selectNode($scope.moduleTree.getNodeByParam("id", $stateParams.moduleId));
								});
									$( "#tabs" ).tabs()
							})
					}, 500)

				});
		}

		
		
		//切换tab页
		$scope.changeTab = function(label){
			$scope.recordNullMsg=false;
			var tempNodeList = angular.copy($scope.modelList);
			var treeObj = $.fn.zTree.getZTreeObj("moduleTree");
			var nodes = treeObj.transformToArray(treeObj.getNodes());
			if(label=='task'){
				$params.isShowCurrentWeek = true;
				$scope.memberList = $params.taskList;
				for(var i=0;i<nodes.length;i++){
					tempNodeList.forEach(t=>{
						if(t.id == nodes[i].id){
							nodes[i].name = t.name+"("+t.taskNum+")";
							treeObj.updateNode(nodes[i]);
						}
					})
				}
				$http.post("wr/getWeeklyReportTaskSnapshots", { condition: { reportId: $params.weekReport.id, belongToNextWeek: "false", moduleId: $scope.treeNodesIds,projectId:$scope.projectId } }).success(function (result) {
					$params.taskList = $params.currentWeekTaskList = $scope.memberList = result.result;
					if($params.taskList.length==0)$scope.recordNullMsg=true;
					if($params.taskList.length>0){
						$.each($params.taskList,function(index,ele){
							$.each(ele.taskSnapShotList,function(i,item){
								if(item.storyId){
									if($params.storyIds.indexOf(item.storyId)==-1){
										$params.storyIds.push(item.storyId)
									}
								}
							})
						})
						$http.post('ws/getStoryListByIds',{ids:$params.storyIds}).success(function(result){
							$params.StoryList=result.result
						})
					}else{
						$params.StoryList=[];
					}
				})
			}else if(label=='story'){
				var memberList=[];
				for(var i=0;i<$params.StoryList.length;i++){
					var userDTO = $params.StoryList[i].assignedTo;
					memberList.push({userDTO:userDTO});
				}
				$scope.memberList = memberList;
				for(var i=0;i<nodes.length;i++){
					tempNodeList.forEach(t=>{
						if(t.id == nodes[i].id){
							nodes[i].name = t.name+"("+t.storyNum+")";
							treeObj.updateNode(nodes[i]);
						}
					})
				}
				if($params.StoryList.length==0)$scope.recordNullMsg=true;
			}else{
				logsList();
				for(var i=0;i<nodes.length;i++){
					tempNodeList.forEach(t=>{
						if(t.id == nodes[i].id){
							nodes[i].name = t.name;
							treeObj.updateNode(nodes[i]);
						}
					})
				}
			}
		}
		/**
		 * 模块树
		 */

		//树数据加载完之后再去查看周报内容
		$http.post("ws/getWeekReportInfo", { reportId: $stateParams.reportId }).success(function (data) {
			$scope.getWeekReportModule(data.result.weeklyReport.projectId);
			if (angular.equals(data.result.weeklyReport.status, '缺失')) {
				$params.weekReport = data.result.weeklyReport;
				$params.startParam.year = $params.weekReport.year;
				$params.startParam.week = $params.weekReport.week;
				$params.endParam.year = $params.weekReport.year;
				$params.endParam.week = $params.weekReport.week;
				$scope.weekReportMainPageTemplateUrl = 'project/weeklyReport/create.html';
				initSubmitDS();
			} else {
				$params.weekReport = initWeekReportDetail(data.result.weeklyReport);
				$params.weekReport.preSummary = data.result.preSummary;
				$scope.projectId = $params.weekReport.projectId;
				$params.startParam.year = $params.weekReport.year;
				$params.startParam.week = $params.weekReport.week;
				$params.endParam.year = $params.weekReport.year;
				$params.endParam.week = $params.weekReport.week;

				$scope.reportStart = $params.startParam;
				$scope.reportEnd = $params.endParam;
				$scope.weekReportMainPageTemplateUrl = 'project/weeklyReport/detail.html';
				initDetailDS();
			}
			var thisWeek = {
				value: "本周完成",
				estStartDate: moment().year($params.weekReport.year).week($params.weekReport.week).startOf('week').valueOf(),
				deadline: moment().year($params.weekReport.year).week($params.weekReport.week).endOf('week').valueOf(),
				time: moment().year($params.weekReport.year).week($params.weekReport.week).startOf('week').format("YYYY/MM/DD") + "—" + moment().year($params.weekReport.year).week($params.weekReport.week).endOf('week').format("YYYY/MM/DD")
			};
			var nextWeek = {
				value: "下周完成",
				estStartDate: moment().year($params.weekReport.year).week($params.weekReport.week + 1).startOf('week').valueOf(),
				deadline: moment().year($params.weekReport.year).week($params.weekReport.week + 1).endOf('week').valueOf(),
				time: moment().year($params.weekReport.year).week($params.weekReport.week + 1).startOf('week').format("YYYY/MM/DD") + "—" + moment().year($params.weekReport.year).week($params.weekReport.week + 1).endOf('week').format("YYYY/MM/DD")
			};
			var unknow = { value: "暂不安排", estStartDate: null, deadline: null, time: "暂不安排" };
			$scope.teskWeeks = [thisWeek, nextWeek, unknow];
		})

		/*$scope.refreshTaskList = function () {
			$http.post("ws/wr/getWeeklyReportTasks", { projectId: $stateParams.project, startParam: $params.startParam, endParam: $params.endParam, reportId: $params.weekReport.id }).success(function (data) {
				$params.taskDs = data.result;

				$params.currentWeekTaskList = $params.taskDs.currentWeekTasks;

				$params.nextWeekTaskList = $params.taskDs.nextWeekTasks;

				if ($params.isShowCurrentWeek) {
					$scope.memberList = $params.taskList = $params.taskDs.currentWeekTasks;
				} else {
					$scope.memberList = $params.taskList = $params.taskDs.nextWeekTasks;
				}
			});
		}*/

		$scope.projectSwitch = function (project) {
			if (!angular.equals($params.weekReport.projectId, project.id)) {
				$params.isShowCurrentWeek = true;
				var oldReportId = $params.weekReport.id;
				$http.post("ws/getWeekReportInfo", { reportId: $stateParams.reportId }).success(function (data) {
					$params.weekReport = initWeekReportDetail(data.result.weeklyReport);
					$params.weekReport.preSummary = data.result.preSummary;
					$scope.projectId = $params.weekReport.projectId;

					$params.startParam.year = $params.weekReport.year;
					$params.startParam.week = $params.weekReport.week;
					$params.endParam.year = $params.weekReport.year;
					$params.endParam.week = $params.weekReport.week;

					$state.go('home.weeklyReport', { reportId: project.reportId }, { notify: true });
					$http.post("ws/wr/getWeeklyReportTaskSnapshots", { condition: { reportId: $params.weekReport.id, belongToNextWeek: "false", moduleId: $scope.treeNodesIds,projectId:$scope.projectId } }).success(function (data) {
						$params.taskList = $params.currentWeekTaskList = $scope.memberList = data.result;
						if($params.taskList.length==0)$scope.recordNullMsg=true;
						if($params.taskList.length>0){
							$.each($params.taskList,function(index,ele){
								$.each(ele.taskSnapShotList,function(i,item){
									if(item.storyId){
										if($params.storyIds.indexOf(item.storyId)==-1){
											$params.storyIds.push(item.storyId)
										}
									}
								})
							})
							$http.post('ws/getStoryListByIds',{ids:$params.storyIds}).success(function(result){
								$params.StoryList=result.result
							})
						}else{
							$params.StoryList=[];
						}
						$params.nextWeekTaskList = undefined;
					});
				});
			}
		}


		function initDetailDS() {

			var allDs = ModelFactory.wrap([
				ModelFactory.ws("wr/getWeeklyReportProjectAuthorInfos", { week: $params.weekReport.week, year: $params.weekReport.year }),
				ModelFactory.ws("wr/getWeeklyReportTaskSnapshots", { condition: { reportId: $params.weekReport.id, belongToNextWeek: "false", moduleId: $scope.treeNodesIds,projectId:$scope.projectId } })
			]);
			allDs.loadAll().then(function () {
				$scope.projects = allDs.datasources[0].result;
				//根据项目id找到当前项目内容
				$.each($scope.projects, function (index, ele) {
					if (angular.equals($params.weekReport.projectId, ele.id)) {
						$scope.currentProject = ele;
					}
				});
				$params.taskList = $params.currentWeekTaskList = $scope.memberList = allDs.datasources[1].result;
				if($params.taskList.length>0){
					$.each($params.taskList,function(index,ele){
						$.each(ele.taskSnapShotList,function(i,item){
							if(item.storyId){
								if($params.storyIds.indexOf(item.storyId)==-1){
									$params.storyIds.push(item.storyId)
								}
							}
						})
					})
					$http.post('ws/getStoryListByIds',{ids:$params.storyIds}).success(function(result){
						$params.StoryList=result.result
					})
				}else{
					$params.StoryList=[];
				}
			});
		}

		$scope.togglevalue = 0;
		$scope.weekIsShow = true;
		$scope.dayIsShow = false;
		$scope.radioValue=1;

		$scope.search = { task: true, lack: false, week: { name: "choose" } };
		$scope.project = {};

		$scope.showDayly2 = function () {
			if ($scope.togglevalue == 0) {
				$scope.togglevalue = 1;
				$scope.weekIsShow = !$scope.weekIsShow;
				$scope.dayIsShow = !$scope.dayIsShow;
				logsList();
			} else {
				$scope.togglevalue = 0;
				$scope.weekIsShow = !$scope.weekIsShow;
				$scope.dayIsShow = !$scope.dayIsShow;
				initDetailDS();
			}
		}

		$scope.RadioChange=function(e){
			$scope.radioValue=e
		}




		$scope.switchWeekReportSubmitView = function () {
			if ($params.isShowCurrentWeek == true) {
				$scope.memberList = $params.taskList = $params.currentWeekTaskList;
			} else {
				$scope.memberList = $params.taskList = $params.nextWeekTaskList;
			}
			$scope.memberSelect();
		}
		$scope.switchWeekReportDetailView = function () {
			if ($params.isShowCurrentWeek == true) {
				$scope.memberList = $params.taskList = $params.currentWeekTaskList;
			} else {
				if (angular.isUndefined($params.nextWeekTaskList)) {
					var start = {
						year: $params.weekReport.year, week: $params.weekReport.week + 1
					}
					var end = {
						year: $params.weekReport.year, week: $params.weekReport.week + 1
					}

					$http.post("ws/wr/getWeeklyReportTasksOfNextWeek", { startParam: start, endParam: end, projectId: $params.weekReport.projectId, reportId: $params.weekReport.id }).success(function (data) {
						$scope.memberList = $params.taskList = $params.nextWeekTaskList = data.result;
					});
				} else {
					$scope.memberList = $params.taskList = $params.nextWeekTaskList;
				}
			}
			$scope.memberSelect();
		}

		function initWeekReportDetail(report) {
			switch (report.status) {
				case "超前": report.statusClass = 'report-label-earlier'; break;
				case "正常": report.statusClass = 'report-label-normal'; break;
				case "逾期": report.statusClass = 'report-label-latter'; break;
				case "缺失": report.statusClass = 'report-label-lose'; break;
			}

			if (angular.isDefined(report.statistics)) {
				var statistics = report.statistics;
				var total = statistics.total;
				var normal = statistics.completed + statistics.crossWeek;
				var canceled = statistics.canceled;
				var consumed = statistics.consumed;
				var estimate = statistics.estimate;
				var member = statistics.member;
				var delayed = statistics.delayed;
				if (angular.equals(statistics.delayed, 0)) {
					delayed = total - normal;
				}
				var worked = statistics.worked;
				if (angular.isUndefined(worked)) {
					worked = 0;
				}

				var normalRate = normal / total * 100;
				var canceledRate = canceled / total * 100;
				var delayedRate = delayed / total * 100;

				report.statistics.normalRate = normalRate;
				report.statistics.delayedRate = delayedRate;
				report.statistics.delayed = delayed;
				report.statistics.normal = normal;

				report.statisticsDesc = "人数：" + member + "\n正常：" + normal + "\n取消：" + canceled + "\n逾期：" + delayed + "\n总任务：" + total + "\n周工时：" + worked + "\n总工时：" + consumed + "/" + estimate + "\n";
			}
			return report;
		}

		function initSubmitDS() {

			$params.evals = ['超前', '正常', '逾期'];

			$params.reasonItems = [{ name: "工作量大", group: "执行问题" },
			{ name: "复杂度高", group: "执行问题" }, { name: "效率低", group: "执行问题" },
			{ name: "被阻塞", group: "执行问题" }, { name: "任务取消", group: "其他" },
			{ name: "请假", group: "其他" }, { name: "临时任务干扰", group: "其他" }];

			$params.visibleRanges = [{ name: "项目成员可见", id: "member" }, { name: "项目管理人可见", id: "manager" }];

			$params.handleItems = [{ name: "延期下周" }, { name: "任务取消" }, { name: "加班完成" }, { name: "修订计划" }, { name: "需要支持" }];

			//进度评价必填警告
			$params.evalWarning = false;

			var allDs = ModelFactory.wrap([
				ModelFactory.entity("project", "id", { id: $stateParams.project }),
				ModelFactory.ws("wr/getWeeklyReportTasks", { projectId: $stateParams.project, startParam: $params.startParam, endParam: $params.endParam, reportId: $params.weekReport.id }),
				ModelFactory.ws("wr/getProjectWRSubmit", { projectId: $stateParams.project }),
				ModelFactory.ws("getProjectPreference", { projectId: $stateParams.project }),
			]);

			allDs.loadAll().then(function () {
				$scope.currentProject = allDs.datasources[0];

				$params.taskDs = allDs.datasources[1].result;
				$scope.memberList = $params.taskList = $params.currentWeekTaskList = $params.taskDs.currentWeekTasks;

				$params.nextWeekTaskList = $params.taskDs.nextWeekTasks;

				$params.submit2 = allDs.datasources[2].result;

				if (angular.isDefined(allDs.datasources[3].result.visibleRange)) {
					switch (allDs.datasources[3].result.visibleRange.preferContent) {
						case "manager": $params.visibleRange = { name: "项目管理人可见", id: "manager" }; $params.weekReport.visibleRange = "manager"; break;
						case "member": $params.visibleRange = { name: "项目成员可见", id: "member" }; $params.weekReport.visibleRange = "member"; break;
					}
				} else {
					$params.visibleRange = { name: "周报可见范围", id: "default" };
				}

				initAddTaskParams();
			});
		}

		$scope.taskDetailSideBarToggle = function (taskId) {
			//我的周报页面
			if ($state.includes("home.weeklyReport.**")) {
				$state.go('home.weeklyReport.taskDetail', { taskId: taskId, editable: false });
			} else if ($state.includes("project.**")) {//项目周报页面
				if (angular.equals($params.weekReport.status, '缺失') && !$params.isShowCurrentWeek) {//缺失页面的任务详情才允许编辑
					$state.go('.taskDetail', { taskId: taskId, editable: true });
				} else {
					$state.go('.taskDetail', { taskId: taskId, editable: false });
				}
			}
		}

		$scope.storyDetailSideBarToggle= function(storyId){
				$state.go('.storyDetail', { storyid: storyId});
		}

		//接受任务消息推送，如果能够在当前页找到该任务，则更新，如果不能找到，则判断是否为当前用户需要添加的任务
		socketio.on($scope, 'taskDetailUpdate', function (data) {
			//只有项目内周报提交时的下周计划任务才允许直接修改并同步
			if ($state.includes("project.**")
				&& angular.equals($params.weekReport.status, '缺失')
				&& angular.equals($params.isShowCurrentWeek, false)) {
				var messageData = angular.fromJson(data);
				//更新了任务
				if (messageData.type == "update" || messageData.type == "updateAdd") {
					updateSingleTask(messageData.id);
				} else if (messageData.type == "delete") {
					deleteSingleTask(messageData.id);
				}
			}
		});

		function deleteSingleTask(taskId) {
			angular.forEach($params.taskList, function (ele, nameIndex) {
				angular.forEach(ele.taskSnapShotList, function (task, taskIndex) {
					if (angular.equals(task.taskId, taskId)) {
						ele.taskSnapShotList.splice(taskIndex, 1);
						$('aside#detail-panel').removeClass('toggled');
						$state.go('^');
					}
				});
			});
		}
		//更新下周计划单个任务,如果返回为空,则从下周计划任务中移除该任务
		function updateSingleTask(taskId) {
			var pathParams = isTaskExist(taskId);
			if (angular.isDefined(pathParams) && angular.isDefined(pathParams.taskId)) {
				var start = moment().year($params.weekReport.year).week($params.weekReport.week + 1).startOf('week').valueOf();
				var end = moment().year($params.weekReport.year).week($params.weekReport.week + 1).endOf('week').valueOf();
				$http.post("ws/wr/getSingleWeeklyReportTask", { taskId: pathParams.taskId, reportId: $params.weekReport.id, start: start, end: end }).success(function (latestTask) {
					//任务已经被删除,或者不在本次周报时间范围内
					if (angular.isUndefined(latestTask.result)) {
						$params.taskList[pathParams.nameIndex].taskSnapShotList.splice(pathParams.taskIndex, 1);
						$('aside#detail-panel').removeClass('toggled');
						$state.go('^');
					} else {//任务只是修改了,但是还在周报时间范围内
						var latestTask = latestTask.result;
						//修改了任务,但是指派给没变
						if (angular.equals(latestTask.assignedTo, $params.taskList[pathParams.nameIndex].userDTO.userName)) {
							$params.taskList[pathParams.nameIndex].taskSnapShotList.splice(pathParams.taskIndex, 1, latestTask);
						} else {//修改了任务,但是指派给变成了其他人
							angular.forEach($params.taskList, function (ele, nameIndex) {
								if (angular.equals(ele.userDTO.userName, latestTask.assignedTo)) {
									ele.taskSnapShotList.push(latestTask);
								}
							});
							$params.taskList[pathParams.nameIndex].taskSnapShotList.splice(pathParams.taskIndex, 1);
						}
					}
				});
			}
		}

		//检查任务是否在下周计划中存在
		function isTaskExist(taskId) {
			var pathParams = {};
			angular.forEach($params.taskList, function (ele, nameIndex) {
				angular.forEach(ele.taskSnapShotList, function (task, taskIndex) {
					if (angular.equals(task.taskId, taskId)) {
						pathParams.nameIndex = nameIndex;
						pathParams.taskIndex = taskIndex;
						pathParams.taskId = taskId;
					}
				});
			});
			return pathParams;
		}

		$scope.hidedMemberSelected = function (member) {
			if (angular.isDefined($params.selectedHideMember)) {
				$params.selectedHideMember.selected = false;
			}
			member.selected = true;
			$scope.memberSelect(member);
			$params.selectedHideMember = member;
		}

		$scope.focusSelectedHideMemberIfExist = function () {
			if (angular.isDefined($params.selectedHideMember)) {
				$params.selectedHideMember.selected = true;
				$scope.memberSelect($params.selectedHideMember);
			}
		}

		$scope.memberSelect = function (member) {
			if (angular.isUndefined(member)) {
				if (angular.isDefined($params.memberFilter)) {
					angular.forEach($params.memberFilter, function (value, key) {
						value.selected = false;
					});
				}
				$params.memberFilter = undefined;
				$params.selectedHideMember = undefined;
			} else {
				if (angular.isDefined($params.memberFilter) && angular.isUndefined($params.memberFilter[member.userDTO.userName])) {
					angular.forEach($params.memberFilter, function (value, key) {
						value.selected = false;
					});
				}
				var filter = {};
				member.selected = true;
				filter[member.userDTO.userName] = member;
				$params.memberFilter = filter;
			}
		}

		$scope.taskInfoFilter = function (taskInfo) {
			//memberFilter未定义表示选择了全部成员
			if (angular.isUndefined($params.memberFilter)) {
				return true;
				//若memberFilter定义了,则只显示定义的那个成员的任务信息
			} else if (angular.isDefined($params.memberFilter[taskInfo.userDTO.userName])) {
				return true;
			}
			return false;
		}



		$scope.visibleRangeClick = function (item) {
			$params.weekReport.visibleRange = item.id;
		}

		$scope.saveWeekReport = function () {
			if (weekReportCheck()) {
				var taskSnapshotList = [];
				$.each($params.taskDs.currentWeekTasks, function (index, task) {
					taskSnapshotList = taskSnapshotList.concat(task.taskSnapShotList);
				});
				/*$.each($params.taskDs.nextWeekTasks,function(index,task){
					taskSnapshotList = taskSnapshotList.concat(task.taskSnapShotList) ;
				}) ;*/
				$http.post("ws/wr/submitWeeklyReport", { report: $params.weekReport, tasks: taskSnapshotList }).success(function (data) {
					$state.go('project.weeklyReports');
				});
			}
		}


		function logsList() {
			$scope.search.projects = $scope.projectId;
			$scope.search.week.st = $scope.reportStart;
			$scope.search.week.et = $scope.reportEnd;
			$scope.search.moduleId = $scope.treeNodesIds;
			$http.post("ws/workbench/teamLogListForReport", { search: $scope.search }).success(function (data) {
				$scope.teamLogs = $scope.memberList = $params.taskList = data.result;
				if($scope.teamLogs.length==0)$scope.recordNullMsg=true;
			});
		}



		function weekReportCheck() {
			$scope.memberSelect();
			var isSuccess = true;
			var filter = {};
			$.each($params.currentWeekTaskList, function (index, task) {
				//本项目任务中若有逾期任务没有处理的则check不通过
				//这个remark1起始就是这个成员的总体任务状态
				if (angular.equals(task.userDTO.remark1, '3')) {
					$.each(task.taskSnapShotList, function (index, ele) {
						//遍历任务集合,本项目任务中若有任务状态等于3(逾期)并且它的逾期处理信息没有则不保存
						if (angular.equals(ele.crossProject, false)
							&& angular.equals(ele.status, '3')) {
							if (angular.isUndefined(ele.reason)) {
								ele.reasonWarning = true;
								isSuccess = false;
								filter[task.userDTO.userName] = task.userDTO;
							}
							if (angular.isUndefined(ele.handle)) {
								ele.handleWarning = true;
								isSuccess = false;
								filter[task.userDTO.userName] = task.userDTO;
							}
						}
					});
				}
			});
			if (!angular.equals(filter, {})) {
				$params.memberFilter = filter;
			}
			if (angular.equals($params.weekReport.status, '缺失')) {
				$params.evalWarning = true;
				isSuccess = false;
			} else {
				$params.evalWarning = false;
			}
			return isSuccess;
		}

		//工时选择

		$scope.hideEstimatePopover = function (index) {
			$("#taskDiv_" + index + "#addEstimateButton").popover('hide');
		};

		$ocLazyLoad.load(['assets/js/jquery.mousewheel.js']);

		function initAddTaskParams() {
			if (!$scope.addTaskParamsInitiated) {
				//里程碑
				$http.post("e/c2_comunity_milestone/op/selectMilestone", { projectId: $stateParams.project }).success(function (data) {
					$scope.projectMilestones = angular.copy(data.result);
					$scope.milestones = data.result;
				});

				$http.post("ws/getMembers", {
					projectId: $stateParams.project
				}).success(function (data) {
					var taskMembers = [];
					angular.forEach(data.result, function (v, n) {
						taskMembers.push({
							name: v.userDTO.user_realname,
							icon: v.userDTO.remark1,
							userName: v.userDTO.user_name,
							id: v.userDTO.user_id
						});
					});
					$scope.taskUsers = taskMembers;
					$scope.projectUsers = angular.copy(taskMembers);
				});

				$http.post("e/label/op/selectLabel", {
					project: $stateParams.project
				}).success(function (data) {
					angular.forEach(data.result, function (v, n) {
						v.backgroundStyle = {
							"background-color": v.color,
							"border-radius": "2px",
							"color": getTextColorByBackColor(v.color)
						};
					});
					$scope.tags = data.result;
				});
				
				$http.post("ws/getProjectPreference", { projectId: $stateParams.project, preferNames: ["projectTaskConfirmState"] })
				.success(function (data, status, headers, config) {
					if (!angular.isUndefined(data.result)
						&& !angular.isUndefined(data.result.projectTaskConfirmState)
						&& !angular.isUndefined(data.result.projectTaskConfirmState.preferContent)) {
						$scope.taskConfirm = "true" == data.result.projectTaskConfirmState.preferContent;
					} else {
						$scope.taskConfirm = false;
					}
			});


				$scope.addTaskParamsInitiated = true;
			}
		}

		function getDefaultTask() {
			var defaultTask = {
				estimate: 8,
				taskUserRed: false,
				taskNameRed: false,
				taskWeekRed: false,
				taskUserRed: false,
				taskEstmateRed: false,
				taskWeek: $scope.teskWeeks[1],//获取nextweek
				projectId: $stateParams.project,
				belongToNextWeek: true,
				crossProject: false,
				worked: 0
			}
			return defaultTask;
		}


		$scope.openNewTask = function (index) {

			$scope.taskDown = !$scope.taskDown;

			if (angular.isUndefined($params.taskList[index].taskObj)) {
				$params.taskList[index].templateUrlOfAddTask = 'project/weeklyReport/task-add.html';
				$params.taskList[index].taskObj = getDefaultTask();
			}

			$.each($scope.taskUsers, function (i, ele) {
				if (angular.equals($params.taskList[index].userDTO.userRealname, ele.name)) {
					$params.taskList[index].taskObj.taskUser = ele;
				}
			});

			//模块
			if (!$params.taskList[index].zTreeNodes) {
				$params.taskList[index].treeOptions = {
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
								if ($params.taskList[index].taskObj.taskModule == treeNode) $params.taskList[index].taskObj.taskModule = null;
								else $params.taskList[index].taskObj.taskModule = treeNode;
							});
							$("#taskDiv_" + index + " #moduleDropdown").controller("c2-dropdown").toggle();
						}
					}
				};
				$http.post("e/module/op/selectModule", { projectId: { id: $stateParams.project } }).success(function (data) {
					$params.taskList[index].zTreeNodes = data.result;
				});
			}

			$("#taskDiv_" + index).slideToggle(true);

			//工时滑轮控制
			$timeout(function () {
				$("#taskDiv_" + index + " #addEstimateButton").popover({ content: "滚动鼠标滚轮快速调整工时", placement: "top", container: "#taskDiv_" + index + " #addTaskDiv" });
				$("#taskDiv_" + index + " #addEstimateButton").mousewheel(function (event, delta) {
					if (delta > 0) $params.taskList[index].taskObj.estimate++;
					if (delta < 0 && $params.taskList[index].taskObj.estimate > 0) $params.taskList[index].taskObj.estimate--;
					$("#taskDiv_" + index + " #addEstimateButton").popover('hide');
					$scope.$apply();
					return false;
				});
			}, 500);
		}



		$scope.cancelCreatTask = function (index) {
			$("#taskDiv_" + index).slideToggle(true);
		}

		$scope.creatTask = function (index) {

			var task = $params.taskList[index].taskObj;

			var taskCheck = true;
			if (angular.isUndefined(task.taskUser.name)) {
				task.taskUserRed = true;
				taskCheck = false;
			}
			if (angular.isUndefined(task.name)) {
				task.taskNameRed = true;
				taskCheck = false;
			}
			if (angular.isUndefined(task.taskWeek)) {
				task.taskWeekRed = true;
				taskCheck = false;
			}

			if (taskCheck) {
				task.estStartDate = task.taskWeek.estStartDate;
				task.deadline = task.taskWeek.deadline;
				task.assignedTo = task.taskUser.userName;
				task.userRealname = task.taskUser.name;
				task.userId = task.taskUser.id;
				task.userIcon = task.taskUser.icon;
				task.left = task.estimate;
				task.consumed = 0;
				task.milestoneId = task.taskMilestone ? task.taskMilestone.id : task.taskMilestone;
				task.milestoneTitle = task.taskMilestone ? task.taskMilestone.title : task.taskMilestone;
				task.labels = task.taskTag;
				task.labelTask = [];
				task.status = '1';
				angular.forEach(task.taskTag, function (tag) {
					task.labelTask.push({ "labelId": tag.id });
				});
				if (angular.isDefined(task.taskModule)) {
					task.moduleId = task.taskModule.id;
					task.moduleName = task.taskModule.name
				}
				task.projectId = $stateParams.project;
				task.isNew = true;

				$http.post("ws/creatTask", { task: task, stageName: null ,taskConfirm:$scope.taskConfirm}).success(function (data) {
					task.taskId = data.result.id;

					var newTask = angular.copy(task);
					newTask.isNewTask = true;
					//选择的成员还没有任务
					var isUserExist = false;
					$.each($params.taskList, function (i, ele) {
						if (angular.equals(ele.userDTO.userRealname, newTask.userRealname)) {
							$params.taskList[i].taskSnapShotList.unshift(newTask);
							isUserExist = true;
						}
					});
					if (!isUserExist) {
						$params.taskList.splice(index + 1, 0, {
							currentProjectEstimateCount: 0,
							otherProjectEstimateCount: 0,
							taskSnapShotList: [newTask],
							userDTO: {
								remark1: '1',
								userName: newTask.assignedTo,
								userRealname: newTask.userRealname
							}
						});
					}

					$timeout(function () {
						newTask.isNewTask = false;
						newTask.nomalAnimate = true;
					}, 2000);

					if (task.userId != $rootScope.subject.userId) {
						$http.post("ws/pushSystem", {
							msgTo: task.userId,
							msgContent: "[" + $rootScope.subject.userRealname + "]分配了你任务[" + task.name + "]",
							link: "tasks/" + task.id,
							isPersistent: true
						});
					}
					$http.post("message/" + task.userId + "/pushTask", {});
					$http.post("message/$all/taskDetailUpdate", { id: newTask.id, to: task.userId, type: "add", socketId: $rootScope.socketId });

					task.name = undefined;
					task.description = null;
				});

			} else {
				$("#taskDiv_" + index + "#taskErrorMsg").addClass("see");
				$timeout(function () {
					$("#taskDiv_" + index + "#taskErrorMsg").removeClass("see");
				}, 2000);
			}
		}

	}]);
developmentCenter.controller('weekReportSubmitMemberCtrl', ['ModelFactory', '$scope', '$http', 'Messenger', 'Modal', '$stateParams',
	function (ModelFactory, $scope, $http, Messenger, Modal, $stateParams) {

		$scope.addMemberChecked = {};

		init();

		function init() {
			var allDs = ModelFactory.wrap([	  //为了处理获取成员列表时不显示本项目内的人员的情况
				ModelFactory.ws("getAddMembers", { projectId: -1 }),
				ModelFactory.ws("wr/getProjectWRSubmit", { projectId: $stateParams.project })
			]);
			allDs.loadAll().then(function () {

				$scope.addMembers = allDs.datasources[0].result;
				if ((angular.isUndefined($scope.userRealname) || null == $scope.userRealname || "" == $scope.userRealname) && $scope.addMembers.length == 0) {
					$scope.isShowNull = true;
				} else {
					$scope.isShowNull = false;
				}

				if (angular.isDefined(allDs.datasources[1].result)) {
					var checkedMember = {};
					$.each(allDs.datasources[1].result, function (index, ele) {
						checkedMember[ele.userId] = ele;
					});
					$scope.addMemberChecked = checkedMember;
				}
			});
		}

		function getMembers() {
			$http.post("ws/getAddMembers", { projectId: -1, search: $scope.userRealname }).success(function (data) {

				$scope.addMembers = data.result;
				if ((angular.isUndefined($scope.userRealname) || null == $scope.userRealname || "" == $scope.userRealname) && $scope.addMembers.length == 0) {
					$scope.isShowNull = true;
				} else {
					$scope.isShowNull = false;
				}
			});
		}

		$scope.selectedMember = function (addMember) {

			if (angular.isUndefined($scope.addMemberChecked[addMember.userId])) {
				$scope.addMemberChecked[addMember.userId] = addMember;
			} else {
				delete $scope.addMemberChecked[addMember.userId];
			}
		}

		$scope.save = function () {
			var addMembers = [];
			angular.forEach($scope.addMemberChecked, function (value, key) {
				addMembers.push({ projectId: $stateParams.project, submitTo: value.userId });
			});

			$http.post("ws/wr/setWeekReportSend2", {
				weeklyReportSubmit2: addMembers, projectId: $stateParams.project
			})
				.success(function (data, status, headers, config) {
					Messenger.success("指派成功！");
					Modal.dismiss();
				});
		}

		$scope.close = function () {
			Modal.close();
		}

		$scope.$watch("userRealname", function (v, o) {
			if (angular.isUndefined(v)) {
				return;
			}
			getMembers();
		}, true);
	}]);
