angular.module('project', ['developmentCenter']).config(['$httpProvider', '$controllerProvider', function ($httpProvider, $controllerProvider) {
	angular.module('project').registerCtrl = $controllerProvider.register;
}])
	.controller('projectMainController', ['$state', '$scope', 'project', '$http', 'Modal', 'Messenger', function ($state, $scope, project, $http, Modal, Messenger) {
		$scope.project = project;
		$scope.showView = false;
		/*显示当前项目*/
		$scope.currentProjectId = $state.params.project;
		/*切换项目事件*/
		$scope.itemClick = function (item) {
			/*定义详情与列表状态关联，如果在详情状态切换项目，跳转到关联的列表状态*/
			var stateRelation = {
				"project.milestoneDetail": "project.milestones",
				"project.weeklyReport": "project.weeklyReports",
				"project.story.storyComment": "project.story",
				"project.doc": "project.docs",
				"project.milestoneDetail.storyList": "project.milestones",
				"project.milestoneDetail.changeList": "project.milestones",
				"project.milestoneDetail.taskList": "project.milestones",
				"project.milestoneDetail.bugList": "project.milestones",
				"project.instanceTree.instance": "project.plan",
				"project.instanceTree.list": "project.plan",
				"project.case.detail": "project.case",
				"project.case.addCase": "project.case"
			};
			var goStateName = stateRelation[$state.current.name] ? stateRelation[$state.current.name] : $state.current.name;
			$state.go(goStateName, { project: item.id, milestoneId: 0 });
		}

		//查看项目归档任务
		$scope.closeView = function () {
			Modal.open('project/task/closeList.html', { projectId: $scope.currentProjectId }, { title: '查看归档' });
		}

		$scope.srefClick = function (e) {
			/* var moduleId = $('#mileId')[0].innerHTML;
			if ($('#mileName')[0].innerHTML == '研发模块') {
				moduleId = 0;
			} */
			$state.go("project.boardstory", { moduleId: 0 });
		}
		//查找项目模块树
		$http.post("ws/getModuleByProject", {
			projectId: $scope.currentProjectId
		}).success(function (data) {
			var storyNum = 0;
			var taskNum = 0;
			var bugNum = 0
			data.result.forEach(function (i) {
				storyNum += i.storyNum;
				taskNum += i.taskNum;
				bugNum += i.bugNum;
			})
			$scope.projectModules = [];
			$scope.projectModules.push({ id: 0, name: '所有模块', storyNum: storyNum, taskNum: taskNum, bugNum: bugNum })
			$scope.projectModules = $scope.projectModules.concat(data.result);
		})

		$scope.moduleClick = function (module) {
			$('#mileName')[0].innerHTML = module.name;
			$('#mileId')[0].innerHTML = module.id;
			$state.go("project.boardstory", { moduleId: module.id });
		}

		/*$scope.milestonClick = function(mileston){
			if(mileston.status!='2'){
				$('#mileName')[0].innerHTML=mileston.title;
				$('#mileId')[0].innerHTML=mileston.id;
					if($state.current.name.indexOf('project.board')<=-1){
						$state.go("project.boardtask",{milestoneId:mileston.id});
					}else{
						$state.go($state.current.name,{milestoneId:mileston.id});
					}
			}else{
				$state.go("project.milestoneDetail",{milestoneId:mileston.id});
			}
		}*/

		//查看已关闭的里程碑
		$scope.closeMilestone = function () {
			$http.post("ws/getCloseMilestoneListForPage", {
				"pageIndex": 1,
				"pageSize": 5,
				"condition": { product: $scope.currentProjectId }
			}).success(function (data) {

				if (data.result.rows.length == 0) {
					Messenger.post("未有已关闭的里程碑");
				} else {
					Modal.open('project/milestone/closeList.html', { projectId: $scope.currentProjectId }, { title: '已关闭的里程碑' });
				}
			});

		}

		var condition = { projectId: $scope.currentProjectId };
		//里程碑
		$http.post("ws/getMilestoneList", {
			"milestone": condition
		}).success(function (data) {
			$scope.projectMilestones = data.result.rows;
		});

		$scope.$on("milestoneRefresh", function (msg) {
			$http.post("ws/getMilestoneList", {
				"milestone": condition
			}).success(function (data) {
				$scope.projectMilestones = data.result.rows;
			});
		})

		/*$scope.dropdownAddMile = function(){
			Modal.open("project/milestone/add.html",{project:$scope.currentProjectId},{onClose:function(data){
				data.result.percent = 0;
				$scope.projectMilestones.push(data.result);
				if($state.current.name.indexOf('project.board')<=-1){
						$state.go("project.boardtask",{milestoneId:data.result.id});
					}else{
						$state.go($state.current.name,{milestoneId:data.result.id});
					}
			}});
		}*/

		$scope.squarePicUrl = function (url) {
			if (url.indexOf("/sm/") > -1) {
				return url.replace('/sm/', '/square/');
			} else {
				return url;
			}
		}

		//归档以及正在进行中的项目
		$http.post("ws/project/myprojects", {}).success(function (data) {
			$scope.myProjects = data.result;
		});

		$scope.textDiff = function (change) {
			if (angular.equals(change.name, "内容") || angular.equals(change.name, "描述")) {
				var diff = JsDiff.diffChars(change.oldVal, change.newVal);
				var diffStr = "";
				var changeTimes = 0;
				diff.forEach(function (part) {
					if (part.added) {
						diffStr += '<span style="color:green;margin: 0 5px;">' + part.value + '</span>';
						changeTimes++;
					} else if (part.removed) {
						diffStr += '<span class="red" style="margin: 0 5px;color:red;text-decoration:line-through">' + part.value + '</span>';
						changeTimes++;
					} else {
						diffStr += part.value;
					}
				});
				change.diffVal = diffStr;
				change.times = changeTimes;
			}
			change.showDiff = false;
		}

	}])
	.controller('projectHomeController', ['$scope', 'Modal', '$http', '$stateParams', function ($scope, Modal, $http, $stateParams) {
		$params = $scope.$params = {};

		$http.post('ws/project/statistics', { projectIds: [$stateParams.project] }).success(function (data) {
			$params.statistics = { loaded: true, data: data.result[0] };
		});


		$http.post("ws/report/getProjectEchartDatas", { params: { projectId: $stateParams.project } }).success(function (data) {
			$params.taskBugStatusData = data.result.taskBugStatusData;
			$params.allEstimateData = data.result.estimateData;
			$params.weekEstimateData = data.result.weekEstimateData;
			//Y轴最大值
			$params.taskBugStatusData.maxY = data.result.taskBugStatusDataMaxY;
			$params.allEstimateData.maxY = data.result.estiamteMaxY;
			$params.weekEstimateData.maxY = data.result.weekEstimateMaxY;
			//计算dataZoom起始位置
			if (angular.isDefined($params.weekEstimateData.series[0])
				&& $params.weekEstimateData.series[0].data.length > 12) {
				$params.weekEstimateData.dzStart = 100 - Math.round((12 / $params.weekEstimateData.series[0].data.length) * 100);
			} else {
				$params.weekEstimateData.dzStart = 0;
			}

			$params.statistics = data.result.statistics[0];

			$params.estimateCount = data.result.estiamteCount;
		});
	}])
	.controller('projectManagementController', ['$scope', 'Messenger', 'Modal', '$rootScope', '$state', '$http', function ($scope, Messenger, Modal, $rootScope, $state, $http) {
		$scope.projectCopy = angular.copy($scope.project);

		$scope.erpProject = { pcode: $scope.projectCopy.contractNo, pname: $scope.projectCopy.name };

		$http.post("ws/erp/projects", { code: $scope.erpProject.pcode, pageable: { pageSize: 99999, pageIndex: 1 } }).success(function (data) {
			$scope.projects = data.result.contents;
		});
		$scope.saveProject = function () {
			$scope.projectCopy.contractNo = $scope.erpProject.pcode;
			$http.post("ws/updateProject", { project: $scope.projectCopy }).success(function (data) {
				Messenger.success('项目基本信息修改成功!');
				$scope.projectCopy = data.result;
			})
		}

		$scope.erpClick = function (project) {
			$scope.erpProject = project;
			$scope.projectCopy.contractNo = project.pcode;
		}
		$scope.publishToCloud = function () {
			Messenger.error('暂未实现发布到云平台的功能');
		};

		$scope.publishToPortal = function () {
			Messenger.error('暂未实现发布到门户的功能');
		}

		$scope.transformOwner = function () {
			Modal.open('project/settings/transform-owner.html', { project: $scope.project }, {
				title: '移交管理权限',
				size: 'big',
				onClose: function () {
					$state.reload("project.settings.management", { project: $scope.project.id });
				}
			})
		}

		$scope.filingProject = function () {
			Modal.open('project/settings/archive-project.html', { project: $scope.project }, {
				title: '归档项目',
				onClose: function () {
				}
			})
		}

		$scope.deleteProject = function () {
			Modal.open('project/settings/delete-confirm.html', { project: $scope.project }, {
				title: '删除项目确认',
				onClose: function () {
				}
			})
		}

		$scope.changeProjectPic = function () {
			Modal.open('project/info/change-pic.html', { currPic: $scope.project.pic }, {
				size: 'big',
				onClose: function (data) {
					$scope.project.pic = data;
					$scope.project.$save();
				}
			});
		}

		$scope.permissionLoaded = false;

		$http.post("ws/isProjectPermitedByBatch", { projectId: $scope.project.id, permExprs: ["project_change", "advancedSet_menu"] })
			.success(function (data, status, headers, config) {
				$scope.permissionLoaded = true;
				$scope.projectChange = data.result["project_change"];
				$scope.advancedSetMenu = data.result["advancedSet_menu"];
			});

		$scope.setCuTask = function () {
			$scope.cuTask = !$scope.cuTask;
			$http.post("ws/putProjectPreference", { prefer: { preferName: "projectTaskCUState", preferContent: $scope.cuTask, preferDesc: "是否允许普通成员创建任务", projectId: $scope.project.id } })
				.success(function (data, status, headers, config) {
					Messenger.success("设置成功！");
				});
		}

		$scope.setTaskConfirm = function () {
			$scope.taskConfirm = !$scope.taskConfirm;
			$http.post("ws/putProjectPreference", { prefer: { preferName: "projectTaskConfirmState", preferContent: $scope.taskConfirm, preferDesc: "任务是否强制确认", projectId: $scope.project.id } })
				.success(function (data, status, headers, config) {
					Messenger.success("设置成功！");
				});
		}

		$http.post("ws/getProjectPreference", { projectId: $scope.project.id, preferNames: ["projectTaskCUState", "projectTaskConfirmState"] })
			.success(function (data, status, headers, config) {
				if (!angular.isUndefined(data.result)
					&& !angular.isUndefined(data.result.projectTaskCUState)
					&& !angular.isUndefined(data.result.projectTaskCUState.preferContent)) {
					$scope.cuTask = "true" == data.result.projectTaskCUState.preferContent;
				} else {
					$scope.cuTask = true;
				}
				if (!angular.isUndefined(data.result)
					&& !angular.isUndefined(data.result.projectTaskConfirmState)
					&& !angular.isUndefined(data.result.projectTaskConfirmState.preferContent)) {
					$scope.taskConfirm = "true" == data.result.projectTaskConfirmState.preferContent;
				} else {
					$scope.taskConfirm = false;
				}
			});
	}]);
