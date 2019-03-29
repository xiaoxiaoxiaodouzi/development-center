developmentCenter.config(function ($stateProvider, $urlRouterProvider) {
    $urlRouterProvider.otherwise(function ($injector, $location) {
        var $state = $injector.get("$state");
        $state.go("home.workbench.myTasks");
    });

    $stateProvider.state('home', {
        url: '/home',
        templateUrl: 'home/home.html',
        resolve: {
            loadDynamicTemplates: ['DynamicTempalteCache', function (DynamicTempalteCache) {
                return DynamicTempalteCache.promise;
            }]
        }
    }).state('home.unauthorized', {
        url: '^/unauthorized',
        templateUrl: 'unauthorized.html'
    })
        /*平台文档链接*/
        .state("home.docs", {
            url: '^/docs',
            templateUrl: 'home/docs.html'
        })
        /*工作台*/
        .state("home.workbench", {
            url: '^/workbench',
            templateUrl: 'home/workbench/main.html',
            controller: 'workBenchController'
        })
        .state("home.workbench.myTasks", {
            url: '^/workbench/tasks',
            templateUrl: 'home/workbench/tasklist-board.html',
            controller: 'MyTaskListController'
        })
        .state("home.workbench.myBugs", {
            url: '^/workbench/bugs',
            templateUrl: 'home/workbench/buglist-board.html',
            controller: 'MyBugListController'
        })
        .state('home.workbench.myBugs.bug', {
            url: '/{project}/bug/{bugNo}',
            params: {
                edit: { value: 0 },
                commentType: { value: -1 }
            },
            templateUrl: 'project/bug/detail.html'
        })
        .state('home.workbench.myTasks.bug', {
            url: '/{project}/bug/{bugNo}',
            params: {
                edit: { value: 0 },
                commentType: { value: -1 }
            },
            templateUrl: 'project/bug/detail.html'
        })
        .state("home.workbench.myTasks.detail", {
            url: '/{taskId}',
            params: {
                task: {}
            },
            templateUrl: 'project/task/detail.html',
            controller: 'taskDetailCtrl'
        })
        .state("home.workbench.teamTask", {
            url: '^/workbench/teamTask',
            templateUrl: 'home/workbench/teamGroup/teamGroupTask.html',
            controller: 'teamTaskController'
        })
        .state("home.workbench.teamTask.detail", {
            url: '/{taskId}',
            params: {
                task: {}
            },
            templateUrl: 'project/task/detail.html',
            controller: 'taskDetailCtrl'
        })
        .state("home.workbench.teamLog", {
            url: '^/workbench/teamLog',
            templateUrl: 'home/workbench/teamGroup/teamGroupLog.html',
            controller: 'teamLogController'
        })
        .state("home.workbench.teamLog.detail", {
            url: '/{taskId}',
            params: {
                task: {}
            },
            templateUrl: 'project/task/detail.html',
            controller: 'taskDetailCtrl'
        })
        //文档中心
        .state('home.shares', {
            url: '/shares',
            params: {
                init: null
            },
            templateUrl: 'home/share/list.html',
            controller: "homeSharesController"
        })
        //公共资源
        .state('home.shareDoc', {
            url: '/doc/{docId}',
            templateUrl: 'home/share/detail.html',
            controller: "homeShareDocController"
        })
        //ERP
        .state('home.rzmx', {
            url: '^/money/rzmx',
            params: { urlName: "rzmx" },
            templateUrl: 'home/erprzmx.html'
        })
        .state('home.xmhz', {
            url: '^/money/xmhz',
            params: { urlName: "xmhz" },
            templateUrl: 'home/erpxmhz.html'
        })
        .state('home.ryhz', {
            url: '^/money/ryhz',
            params: { urlName: "ryhz" },
            templateUrl: 'home/erpryhz.html'
        })
        .state('home.yfrzmx', {
            url: '^/money/yfrzmx',
            params: { urlName: "yfrzmx" },
            templateUrl: 'home/erpyfrzmx.html'
        })
        /*全局报表顶级状态*/
        .state('home.reports', {
            url: '^/reports',
            templateUrl: 'home/report/reports.html',
            controller: 'reportsController'
        })
        .state('home.report', {
            url: '^/report/{project}',
            templateUrl: 'home/report/report.html',
            abstract: true,
            resolve: {
                loadCtrl: ['$ocLazyLoad', function ($ocLazyLoad) {
                    return $ocLazyLoad.load('js/controllers/project/main.js');
                }],
                project: ['$stateParams', 'ModelFactory', function ($stateParams, ModelFactory) {
                    return ModelFactory.entity('project', 'id', { id: $stateParams.project }).$promise;
                }]
            },
            controller: 'reportHomeController'
        })
        .state('home.report.summary', {
            url: '/summary',
            templateUrl: 'project/info/home.html',
            controller: 'projectHomeController'
        })
        .state('home.report.workLog', {
            url: '/workLog',
            templateUrl: 'home/report/worklog.html',
            controller: 'workLogController'
        })
        /*我的项目，控制器定义在全局的controllers.js*/
        .state('home.projects', {
            url: '^/projects',
            templateUrl: 'project/list.html',
            controller: 'myprojectsController'
        }).state('home.weeklyReports', {
            url: '^/weeklyReport',
            templateUrl: 'home/report/weekly-reports.html',
            controller: 'ReportFormListController'
        }).state('home.weeklyReport', {
            url: '^/weeklyReport/{reportId}',
            templateUrl: 'project/weeklyReport/main.html',
            controller: 'weeklyReportController'
        }).state('home.weeklyReport.taskDetail', {
            url: '/task/{taskId}',
            templateUrl: 'project/task/detail.html',
            controller: 'taskDetailCtrl'
        }).state('home.profile', {
            url: '^/profile',
            templateUrl: 'home/profile.html'
        }).state('home.about', {
            url: '^/about',
            templateUrl: 'about.html'
        }).state('project', {
            "abstract": true,
            url: '/project/{project}',
            templateUrl: 'project/main.html',
            resolve: {
                loadCtrl: ['$ocLazyLoad', function ($ocLazyLoad) {
                    return $ocLazyLoad.load('js/controllers/project/main.js');
                }],
                project: ['$stateParams', 'ModelFactory', function ($stateParams, ModelFactory) {
                    return ModelFactory.entity('project', 'id', { id: $stateParams.project }).$promise;
                }]
            },
            controller: 'projectMainController'
        }).state("project.teamLog", {
            url: '/teamLog/{milestoneId}',
            templateUrl: 'project/teamLog/teamGroupLog.html',
            controller: 'projectLogController'
        }).state("project.teamLog.detail", {
            url: '/{taskId}',
            params: {
                task: {}
            },
            templateUrl: 'project/task/detail.html',
            controller: 'taskDetailCtrl'
        }).state('project.profile', {
            url: '/profile',
            templateUrl: 'home/profile.html'
        }).state('project.about', {
            url: '/about',
            templateUrl: 'about.html'
        }).state('project.home', {
            url: '/home',
            templateUrl: 'project/info/home.html',
            controller: 'projectHomeController'
        }).state('project.projectPlan', {
            url: '/projectPlan?{init}',
            templateUrl: 'project/story/main.html',
            controller: 'storyHomeController'
        }).state('project.projectPlan.story', {
            url: '/stories/{moduleId}',
            templateUrl: 'project/story/list.html',
        }).state('project.projectPlan.tasks', {
            url: '/tasks',
            params: {
                init: null
            },
            templateUrl: 'project/task/list.html',
            resolve: {
                subject: function ($http) {
                    return $http.get('ws/getUserInfo');
                }
            },
            controller: 'TaskListController'
        }).state('project.projectPlan.tasks.detail', {
            url: '/{taskId}',
            templateUrl: 'project/task/detail.html',
            resolve: {
                myTaskDetail: function ($http, $stateParams) {
                    return $http.post('ws/projectTask', { id: $stateParams.taskId });
                }
            },
            params: {
                task: {}
            },
            controller: 'taskDetailCtrl'
        }).state('project.projectPlan.bugs', {
            url: '/bugs?{init}',
            templateUrl: 'project/bug/list.html'
        }).state('project.story', {
            url: '/stroies?{init}',
            templateUrl: 'project/story/main.html',
            controller: 'storyHomeController'
        }).state('project.story.storyList', {
            url: '/list/{moduleId}',
            templateUrl: 'project/story/list.html',
        }).state('project.story.storyComment', {
            url: '/detail/{storyid}',
            params: {
                edit: { value: 0 }
            },
            templateUrl: 'project/story/detail.html'
        }).state('project.story.storyComment.detail', {
            url: '/{taskId}',
            templateUrl: 'project/task/detail.html',
            params: {
                task: {}
            },
            controller: 'taskDetailCtrl'
        }).state('project.addStory', {
            url: '/stroies/add',
            templateUrl: 'project/story/add.html'
        }).state('project.settings', {
            "abstract": true,
            url: '/settings',
            template: '<div class="container" ui-view></div>'
        }).state('project.settings.members', {
            url: '/members',
            templateUrl: 'project/settings/members.html'
        }).state('project.modules', {
            url: '/modules',
            templateUrl: 'project/settings/modules.html'
        }).state('project.modules.storyComment', {
            url: '/story/{storyid}',
            templateUrl: 'project/story/detail.html'
        }).state('project.modules.moduleDetail', {
            url: '/{moduleId}',
            params:{'module':null},
            templateUrl: 'project/settings/editModule.html'
        }).state('project.planOfProject', {
            url: '/planOfProject',
            templateUrl: 'project/settings/planOfProject.html'
        }).state('project.planOfProject.taskDetail', {
            url: '/task/{taskId}',
            templateUrl: 'project/task/detail.html',
            controller: 'taskDetailCtrl'
        }).state('project.planOfProject.planDetail', {
            url: '/plan/{planId}',
            params:{'projectPlan':null},
            templateUrl: 'project/settings/projectPlanDetail.html'
        }).state('project.gantt', {
            url: '/gantt',
            templateUrl: 'project/settings/gantt.html'
        }).state('project.gantt.planDetail', {
            url: '/plan/{planId}',
            params:{'projectPlan':null},
            templateUrl: 'project/settings/projectPlanDetail.html'
        })
        .state('project.settings.labels', {
            url: '/labelList',
            templateUrl: 'project/settings/labels.html'
        }).state('project.settings.management', {
            url: '/management',
            templateUrl: 'project/settings/management.html',
            controller: 'projectManagementController'
        }).state('project.tasks', {
            url: '/tasks',
            params: {
                init: null
            },
            templateUrl: 'project/task/list.html',
            resolve: {
                subject: function ($http) {
                    return $http.get('ws/getUserInfo');
                }
            },
            controller: 'TaskListController'
        }).state('project.boardtask', {
            url: '/boardtask/{moduleId}',
            templateUrl: 'project/board/taskList.html',
            controller: 'boardListController',
        }).state('project.boardbug', {
            url: '/boardbug/{moduleId}',
            templateUrl: 'project/board/bugList.html',
            controller: 'boardBugListController'
        }).state('project.boardstory', {
            url: '/boardstory/{moduleId}',
            templateUrl: 'project/board/storyList.html',
            controller: 'boardStoryListController'
        }).state('project.boardstory.storyComment', {
        	url: '/detail/{storyid}',
            templateUrl: 'project/story/detail.html'
        }).state('project.boardbug.bug', {
            url: '/bug/{bugNo}',
            params: {
                edit: { value: 0 },
                commentType: { value: -1 }
            },
            templateUrl: 'project/bug/detail.html'
        }).state('project.boardtask.detail', {
            url: '/task/{taskId}',
            templateUrl: 'project/task/detail.html',
            params: {
                task: {}
            },
            controller: 'taskDetailCtrl'
        }).state('project.boardtask.bug', {
            url: '/bug/{bugNo}',
            params: {
                edit: { value: 0 },
                commentType: { value: -1 }
            },
            templateUrl: 'project/bug/detail.html'
        }).state("project.boardTeamLog", {
            url: '/boardTeamLog/{moduleId}',
            templateUrl: 'project/teamLog/teamGroupLog.html',
            controller: 'projectLogController'
        }).state("project.boardTeamLog.detail", {
            url: '/{taskId}',
            params: {
                task: {}
            },
            templateUrl: 'project/task/detail.html',
            controller: 'taskDetailCtrl'
        }).state('project.tasks.detail', {
            url: '/{taskId}',
            templateUrl: 'project/task/detail.html',
            resolve: {
                myTaskDetail: function ($http, $stateParams) {
                    return $http.post('ws/projectTask', { id: $stateParams.taskId });
                }
            },
            params: {
                task: {}
            },
            controller: 'taskDetailCtrl'
        }).state('project.tasks.bug', {
            url: '/bug/{bugNo}',
            params: {
                edit: { value: 0 },
                commentType: { value: -1 }
            },
            templateUrl: 'project/bug/detail.html'
        }).state('project.milestones', {
            url: '/milestones',
            templateUrl: 'project/milestone/list.html'
        }).state('project.milestoneDetail', {
            url: '/milestones/{milestoneId}',
            params: {
                edit: {
                    value: 0
                }
            },
            templateUrl: 'project/milestone/detail.html'
        }).state('project.milestoneDetail.changeList', {
            url: '/changes',
            views: {
                "detailUIView": {
                    templateUrl: 'project/milestone/changeList.html'
                }
            }
        }).state('project.milestoneDetail.storyList', {
            url: '/stories',
            views: {
                "detailUIView": {
                    templateUrl: 'project/milestone/storyList.html'
                }
            }
        }).state('project.milestoneDetail.taskList', {
            url: '/tasks',
            views: {
                "detailUIView": {
                    templateUrl: 'project/milestone/taskList.html'
                }
            }
        }).state('project.milestoneDetail.bugList', {
            url: '/bugs',
            views: {
                "detailUIView": {
                    templateUrl: 'project/milestone/bugList.html'
                }
            }
        }).state('project.milestoneDetail.bugList.bug', {
            url: '/{bugNo}',
            params: {
                edit: {
                    value: 0
                },
                commentType: {
                    value: -1
                }
            },
            views: {
                "sideUIView": {
                    templateUrl: 'project/bug/detail.html'
                }
            }
        }).state('project.milestoneDetail.bugList.reply', {
            url: '/{bugNo}/reply',
            views: {
                "sideUIView": {
                    templateUrl: 'project/bug/detail.html'
                }
            }
        }).state('project.milestoneDetail.bugList.reject', {
            url: '/{bugNo}/reject',
            params: {
                commentType: {
                    value: 5
                }
            },
            views: {
                "sideUIView": {
                    templateUrl: 'project/bug/detail.html'
                }
            }
        }).state('project.milestoneDetail.bugList.close', {
            url: '/{bugNo}/close',
            params: {
                commentType: {
                    value: 0
                }
            },
            views: {
                "sideUIView": {
                    templateUrl: 'project/bug/detail.html'
                }
            }
        }).state('project.milestoneDetail.bugList.reopen', {
            url: '/{bugNo}/reopen',
            params: {
                commentType: {
                    value: 3
                }
            },
            views: {
                "sideUIView": {
                    templateUrl: 'project/bug/detail.html'
                }
            }
        }).state('project.milestoneDetail.taskList.taskDetail', {
            url: '/task/{taskId}',
            views: {
                "sideUIView": {
                    templateUrl: 'project/task/detail.html'
                }
            },
            params: {
                task: {}
            }
        }).state('project.milestoneDetail.storyList.storyDetail', {
            url: '/story/{storyid}',
            views: {
                "sideUIView": {
                    templateUrl: 'project/story/detail.html'
                }
            }
        }).state('project.milestoneDetail.changeList.storyDetail', {
            url: '/story/{storyid}',
            views: {
                "sideUIView": {
                    templateUrl: 'project/story/detail.html'
                }
            }
        })
        /*项目共享*/
        .state('project.docs', {
            url: '/docs',
            params: {
                init: null
            },
            resolve: {
                loadCtrl: ['$ocLazyLoad', function ($ocLazyLoad) {
                    return $ocLazyLoad.load('js/controllers/project/shares.js');
                }]
            },
            templateUrl: 'project/share/list.html',
            controller: "docsController"
        }).state('project.doc', {
            url: '/doc/{docId}',
            templateUrl: 'project/share/detail.html',
            controller: "DocViewController"
        }).state('project.weeklyReports', {
            url: '/weeklyReports',
            templateUrl: 'project/weeklyReport/list.html',
            controller: 'weeklyReportListController'
        }).state('project.weeklyReport', {
            url: '/weeklyReport/{reportId}',
            templateUrl: 'project/weeklyReport/main.html',
            controller: 'weeklyReportController'
        }).state('project.weeklyReport.taskDetail', {
            url: '/task/{taskId}',
            templateUrl: 'project/task/detail.html',
            controller: 'taskDetailCtrl'
        }).state('project.weeklyReport.storyDetail', {
            url: '/story/{storyid}',
            templateUrl: 'project/story/detail.html',
        }).state('home.messages', {
            url: '/messages',
            templateUrl: 'home/message/messageList.html'
        }).state('home.messages.chat', {
            url: '/{user_id}',
            templateUrl: 'home/message/messageDetail.html'
        }).state('project.media', {
            url: '/media',
            templateUrl: 'project/media.html'
        })
        .state('project.case', {
            url: '/testcase',
            templateUrl: 'project/testcase/main.html',
            controller: 'caseController'
        })
        .state('project.case.caseList', {
            url: '/caseList/{typeId}',
            templateUrl: 'project/testcase/caseList.html',
            controller: 'caseListController'
        })
        .state('project.case.detail', {
            url: '/detail/{caseId}',
            templateUrl: 'project/testcase/edit.html',
            controller: 'caseDetailController'
        })
        .state('project.case.addCase', {
            url: '/add',
            templateUrl: 'project/testcase/add.html',
            controller: 'caseAddController'
        })
        .state('project.plan', {
            url: '/plan',
            templateUrl: 'project/plan/list.html',
            controller: 'planListController'
        })
        .state('project.instanceTree', {
            url: '/plan/{planId}?{state}',
            templateUrl: 'project/plan/main.html',
            controller: 'instanceTreeController'
        })
        .state('project.instanceTree.instance', {
            url: '/instance/{instanceId}',
            templateUrl: 'project/plan/instance.html',
            controller: 'instanceDetailController'
        })
        .state('project.instanceTree.list', {
            url: '/type/{typeId}',
            templateUrl: 'project/plan/instanceList.html',
            controller: 'instanceListController'
        })
        .state('project.bugs', {
            url: '/bugs?{init}',
            templateUrl: 'project/bug/list.html'
        }).state('project.addBug', {
            url: '/bugs/add',
            templateUrl: 'project/bug/add.html'
        }).state('project.bugs.bug', {
            url: '/{bugNo}',
            params: {
                edit: { value: 0 },
                commentType: { value: -1 }
            },
            templateUrl: 'project/bug/detail.html'
        }).state('project.bugs.reply', {
            url: '/{bugNo}/reply',
            templateUrl: 'project/bug/detail.html'
        }).state('project.bugs.reject', {
            url: '/{bugNo}/reject',
            params: { commentType: { value: 5 } },
            templateUrl: 'project/bug/detail.html'
        }).state('project.bugs.close', {
            url: '/{bugNo}/close',
            params: { commentType: { value: 0 } },
            templateUrl: 'project/bug/detail.html'
        }).state('project.bugs.reopen', {
            url: '/{bugNo}/reopen',
            params: { commentType: { value: 3 } },
            templateUrl: 'project/bug/detail.html'
        })
        /*支持单*/
        .state('home.support', {
            url: '^/supports',
            templateUrl: 'project/support/supports.html',
            controller: 'SupportListController'
        }).state('home.addSupport', {
            url: '^/support/add',
            templateUrl: 'project/support/supportAdd.html',
            controller: 'SupportAddController'
        }).state('home.supportInfo', {
            url: '^/support/{supportId}',
            templateUrl: 'project/support/supportInfo.html',
            controller: 'SupportInfoController'
        })
        /*系统管理*/
        .state('home.settings', {
            url: '/settings',
            templateUrl: 'home/setting.html'
        }).state('home.settings.assistants', {
            url: '/assistants',
            templateUrl: 'home/setAssistant.html'
        }).state('home.settings.assessments', {
            url: '/assessments',
            templateUrl: 'home/setAssessment.html'
        }).state('home.assess', {
            url: '/list',
            templateUrl: 'home/assess/list.html',
        }).state('home.assesdetail', {
            url: '/detail/{assessId}/org/{orgId}/{isManagedOrg}/{isAssessor}/{isReviewer}',
            templateUrl: 'home/assess/detail.html',
        }).state('home.assesdetail.taskList', {
            url: '/taskList',
            templateUrl: 'home/assess/workLog.html',
        }).state("home.assesdetail.taskList.taskDetail", {
            url: '/{taskId}',
            params: {
                task: {}
            },
            templateUrl: 'project/task/detail.html',
            controller: 'taskDetailCtrl'
        }).state('home.assesdetail.bugList', {
            url: '/bugList',
            templateUrl: 'home/assess/workBugList.html',
        }).state('home.assesdetail.bugList.bug', {
            url: '/{bugNo}/project/{project}',
            params: {
                edit: { value: 0 },
                commentType: { value: -1 },
            },
            templateUrl: 'project/bug/detail.html'
        })
        /**
         * 团队管理与团队周报
         */
        .state('home.team',{
        	url:'/team',
        	templateUrl:'team/setting.html'
        })
        .state('home.team.users',{
        	url:'/users',
        	templateUrl:'team/users.html'
        })
        .state('home.team.setting',{
        	url:'/{teamId}/setting',
        	templateUrl:'team/setTeam.html'
        })
        .state('home.team.reports',{
        	url:'/reports',
        	templateUrl:'team/reports.html'
        })
        .state('home.team.report',{
        	url:'/{teamId}/report/{reportId}',
        	templateUrl: 'team/main.html',
        	controller: 'teamReportController'
        })
        .state('home.team.report.taskDetail', {
            url: '/task/{taskId}',
            templateUrl: 'project/task/detail.html',
            controller: 'taskDetailCtrl'
        })
        ;
})
    .run(['$rootScope', '$state', function ($rootScope, $state) {
        $rootScope.$on('$stateChangeStart', function (e, toState, toParams) {
			$.each($("aside"),function(i,ele){
				if(ele.hasClass("toggled")){
					ele.removeClass("toggled");
				}
			})
            if ((toState.name.indexOf("project.board") > -1 && toState.name!='project.boardTeamLog') || toState.name.indexOf("home.workbench.my") > -1) {
                $rootScope.eyeShow = true;
            } else {
                $rootScope.eyeShow = false;
            }

            if (toState.name === "project.story.tab") {
                e.preventDefault();
                if ($state.current.name === "project.story.tab.info" || $state.current.name === "project.story.tab.task") {
                    $state.go('project.story', toParams);
                } else {
                    $state.go('project.story.tab.info', toParams);
                }
            } else if (toState.name == "project.milestoneDetail") {
                e.preventDefault();
                $state.go('project.milestoneDetail.storyList', toParams);
            } else if (toState.name == "home.workbench") {
                e.preventDefault();
                $state.go('home.workbench.myTasks', toParams);
            } else if (toState.name == "project.instanceTree") {
                e.preventDefault();
                var param = { planId: toParams.planId, typeId: 0, state: toParams.state };
                $state.go('project.instanceTree.list', param);//默认显示根节点列表
            } else if (toState.name == "project.case") {
                e.preventDefault();
                var param = { project: toParams.project, typeId: 0 };
                $state.go('project.case.caseList', param);//默认显示根节点列表
            } else if (toState.name == "home.settings") {
                e.preventDefault();
                $state.go('home.settings.assistants', toParams);
            } else if (toState.name == "project.story") {
                e.preventDefault();
                var param = { project: toParams.project, moduleId: 0 };
                $state.go('project.story.storyList', param);//默认显示根节点列表
            }else if (toState.name == "home.team") {
                e.preventDefault();
                $state.go('home.team.reports', toParams);
            } 
        });
    }]);