developmentCenter.controller('reportsController',['$http','$state','$stateParams','ModelFactory','$scope','Modal','Messenger',
                                                           function($http,$state,$stateParams,ModelFactory,$scope,Modal,Messenger){
	var $params = $scope.$params = {} ;
	
	$params.page = {
			pageSize:10,
			pageIndex:1
	}
	
	init() ;
	
	function init(){
		var allDs = ModelFactory.wrap([
			 		                      ModelFactory.ws("report/getProjectStatisticsOfCurrentUser",{})
			                         ]) ;
			 		
		allDs.loadAll().then(function(){
			
			$params.projectList = allDs.datasources[0].result ;
			
			$params.page.total =  allDs.datasources[0].result.length ;
			
		});
	}
	
	$scope.pageList=function(pageIndex){
		$params.page.pageIndex = pageIndex ;
		init() ;
	}
	
	
}]) ;

developmentCenter.controller('reportHomeController',['$scope','Modal','$http','debounce','$stateParams','project',function($scope,Modal,$http,debounce,$stateParams,project){
	
	$scope.project = project ;
	
    $scope.squarePicUrl = function(url){
        if(url.indexOf("/sm/") > -1){
            return url.replace('/sm/','/square/');
        }else{
            return url;
        }
    }
    
}]) ;