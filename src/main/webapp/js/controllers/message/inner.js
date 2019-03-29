angular.module("developmentCenter").controller('msgCtrl',function($scope,$rootScope,$http, $state, $stateParams, $timeout, Messenger,Modal) {
	
	$scope.initMessageList=function(){
		
		
		$scope.currentUser=$rootScope.subject;
		
		//加载发消息用户
		$scope.loadMsgFromUserList();
		
		if($stateParams&&$stateParams.msgFrom&&$stateParams.msgFrom!=""){
			$scope.currentFromUser=$stateParams.msgFrom;
			
			//加载当前active
			$scope.loadFromUserMsgDetail($scope.currentFromUser);
		}else{
			$scope.currentFromUser="broadcast";
			$scope.loadBroadcastMsgDetail();
		}
	}
	
	
	$scope.loadFromUserMsgDetail=function(msgFromUser){
		$http.post("ws/getFromUserMsgDetail", {
			"msgFromUser":msgFromUser,
			"condition":{}
			}).success(function(data) {
				$scope.fromUserMsgDetail=data.result;
			});
	}
	
	
	$scope.loadBroadcastMsgDetail=function(){
		$http.post("ws/getBroadcastMsgDetail", {
			"condition":{}
			}).success(function(data) {
				console.log(data);
				$scope.fromUserMsgDetail=data.result;
			});
	}
	
	
	$scope.loadMsgFromUserList=function(){
		$http.post("ws/getAllMsgFromUserList", {
			"condition":{}
			}).success(function(data) {
				console.log(data);
				$scope.msgFromUserList=data.result;
			});
	}

	
	$scope.initMessageList();
});