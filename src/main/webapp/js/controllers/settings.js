developmentCenter
	/*助理配置页面控制器*/
    .controller('setAssistCtrl', ["$rootScope", "$timeout", "$state", "$http", "socketio","$stateParams","$scope","Messenger", function($rootScope, $timeout, $state, $http, socketio,$stateParams,$scope,Messenger){

		$scope.addMembers = [];
		$scope.isLoad = false;
		$scope.isShowNull = true;
		$scope.isSearchFail = false;
		$scope.oldMembers = [];
		$scope.addMemberChecked = {};
		
		initAssist();
		
		function initAssist() {
			$http.post("ws/getAllAssistantsInfo",{}).success(function(data) {
				$.each(data.result, function(i,v) {
					$scope.addMemberChecked[v.userId] = {
						userName: v.userName,
						userRealname: v.userRealname,
						remark1: v.icon,
						userId: v.userId,
						selected: true,
						isOld: true,
						orgId: v.orgId,
						orgShowName: v.orgShowName
					} 					
				});
			});
		}
		
		$scope.getAddMembers = function() {
			$scope.isSearchFail = false;
			$scope.isLoad = true;
			$scope.isShowNull = false;
			$http.post("ws/getUserInfoByURealname",{
				userRealname: $scope.userRealname
			})
			.success(function(data){		
				$scope.isLoad = false;
				$.each(data.result, function(i, v) {
					if(angular.isDefined($scope.addMemberChecked[v.userId]) && $scope.addMemberChecked[v.userId].selected)
						v.selected = true;
					else 
						v.selected = false;
				});
				$scope.addMembers = data.result;
				if((angular.isUndefined($scope.userRealname) || null==$scope.userRealname || ""==$scope.userRealname) && data.result.length==0){
					$scope.isShowNull = true;
				}else{
					$scope.isShowNull = false;
				}		
				if(0 == data.result.length) {
					$scope.isSearchFail = true;
				}
			});
		}
		
		//监控搜索框
		$scope.$watch("userRealname",function(v,o){
			if(angular.isUndefined(v)){
				return;
			}
			if(null != v && "" != v)
				$scope.getAddMembers();
			else {
				$scope.isLoad = false;
				$scope.isShowNull = true;
				$scope.isSearchFail = false;
				$scope.addMembers = [];
			}				
		},true);
				
		$scope.selectedMember = function(addMember){
			addMember.selected = !addMember.selected;
			
			if(addMember.selected){
				$scope.addMemberChecked[addMember.userId]=addMember;
			}else if(addMember.isOld){		
				$scope.oldMembers.push({
					userName: addMember.userName,
					userRealname: addMember.userRealname,
					orgId: addMember.orgId,
					orgName: addMember.orgShowName
				});
				delete $scope.addMemberChecked[addMember.userId];
			}else {
				delete $scope.addMemberChecked[addMember.userId];
			}
		}
		
		$scope.save = function(){
			var addAssistantNames = [];
			angular.forEach($scope.addMemberChecked, function (value, key) {
				if(!value.isOld) {
					addAssistantNames.push({
						userName: value.userName,
						userRealname: value.userRealname,
						orgId: value.orgId,
						orgName: value.orgShowName});					
				}
			});
			
			
			if(0 != $scope.oldMembers.length) {
				$http.post("ws/assis/delAssistant",{assistantList: $scope.oldMembers});
			}
			
			if(0 != addAssistantNames.length){
				$http.post("ws/assis/addAssistant",{assistantList: addAssistantNames})
				.success(function(data){
					Messenger.success("助理保存成功！")
				});
			}
			
			if(addAssistantNames.length==0&&$scope.oldMembers.length==0){
				Messenger.error("请选择需要设置成助理的成员！");
				return;
			}
			
		}
    }]);