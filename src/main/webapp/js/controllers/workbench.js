developmentCenter.controller('workBenchController',['$scope','$http','Modal','ModelFactory','$stateParams','Messenger','$timeout',
	function($scope,$http,Modal,ModelFactory,$stateParams,Messenger,$timeout){
	
		function init(){
			$http.get("ws/workbench/getStatisticAnalysisMenu").success(function(data){
				$scope.statMenus = data.result ;
			}) ;
		}
		
		//init();
		
		$scope.openAddStatisticAnalysis = function(){
			Modal.open('home/workbench/addStatisticAnalysis.html',{},{
				title:"创建任务统计视图",
				size:'big',
				onClose:function(statName){	
					init();
				}
			});
		}
    	
	}
])
developmentCenter.controller('statController',['debounce','$rootScope','$scope','$http','Modal','ModelFactory','$stateParams','Messenger','$timeout',
    function(debounce,$rootScope,$scope,$http,Modal,ModelFactory,$stateParams,Messenger,$timeout){
		$scope.activeTab = 'team';
		$params = $scope.$params = {} ;
		$scope.check = {statName:true} ;
		
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
		
		$scope.addStatDetailUrl = 'home/workbench/addStat/addStatName.html' ;
		
		$http.get("ws/workbench/getStatisticAnalysisWidgets").success(function(data){
			$scope.statWidgets = data.result ;
		}) ;
		
		$http.post("ws/dept/getDeptTree",{deepth:-1}).success(function(data){
			$scope.deptTree = data.result ;
		}) ;
	
		$scope.switch2StatDetailPage = function(){
			$scope.check.statName = !!$params.statName ;
			if($scope.check.statName){
				$scope.addStatDetailUrl = 'home/workbench/addStat/addStatDetail.html' ;
			}
		}
		
		function zTreeOnClick(event, treeId, treeNode){
			$scope.$apply(function(){
				$params.selectedDeptNode = treeNode ;
			}) ;
		}
		
		$scope.widgetSelect=function(widget){
			widget.selected = !widget.selected ;
		}
		
		var addStatDebounce = debounce(function(){
			var selectedWidget = [] ; 
			angular.forEach($scope.statWidgets,function(widget,index){
				if(widget.selected){
					if(widget.category==1 && $params.selectedDeptNode){
						selectedWidget.push({teamId:$params.selectedDeptNode.id,type:widget.type}) ;
					}
				}
			}) ;
			$http.post("ws/workbench/addStatisticAnalysis",{stat:{createBy:$rootScope.subject.userId,name:$params.statName,widgets:selectedWidget}}).success(function(){
				Modal.close() ;
			}) ;
		},600);
		
		$scope.addStat=function(){
			addStatDebounce();
		}
	}
])

