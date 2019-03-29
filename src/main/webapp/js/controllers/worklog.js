developmentCenter.controller('workLogController',['debounce','Messenger','Modal','ModelFactory','$scope','$http','$stateParams','$state',
                                                  function(debounce,Messenger,Modal,ModelFactory,$scope,$http,$stateParams,$state){
	
	$params = $scope.$params = {} ;
	
	$params.lackList=[] ;
	
	init() ;
	
	var deptTreeOptions = {
			check:{
				enable : true ,
				chkStyle : "checkbox" ,
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
				onCheck: zTreeOnCheck
			}
	} ;
	
	$params.selectedDeptNodes = [] ;
	
	function zTreeOnCheck(event, treeId, treeNode){
		$params.selectedDeptNodes = $('#deptDropdown').controller("c2Dropdown").getTree().getCheckedNodes() ;
		if(angular.isDefined($scope.search)){
			initLackInfos() ;
		}else{
			$http.post("ws/wl/updateWorkLogDeptPreference",{deptArrJsonStr:JSON.stringify(getSelectedDeptIds())}) ;
		}
	}

	function init(){
		$http.post("ws/wl/getDepartMentTree",{orgId:"0"}).success(function(data){
			$params.deptTreeOptions = deptTreeOptions ;
			$params.deptNodes = data.result ;
			$params.deptNodeAsis = {} ;
			var selectedDeptNodes = [] ;
			$.each($params.deptNodes,function(index,ele){
				if(ele.checked)
					selectedDeptNodes.push(ele) ;
				$params.deptNodeAsis[ele.id] = ele ;
			}) ;
			$params.selectedDeptNodes = selectedDeptNodes ;
		}) ;
	}
	
	function initLackInfos(){
		if(angular.isDefined($scope.search)){
			$http.post("ws/wl/getEstimateInfosOfLack",{start:$scope.search.week.st,end:$scope.search.week.et,deptIds:getSelectedDeptIds()}).success(function(data){
				$params.lackList = data.result ;
			}) ;
		}
	}
	
	function getSelectedDeptIds(){
		var deptIds = [] ;
		$.each($params.selectedDeptNodes,function(index,ele){
			deptIds.push(ele.id) ;
		}) ;
		return deptIds ;
	}
	
	//时间查询初始化
	var datetimepickerOption = {
			format:"YYYY-MM-dd",
			locale: 'zh-cn',
			icons: {
		          time: 'md md-timer',
		          date: 'md md-today',
		          up: 'md md-expand-less',
		          down: 'md md-expand-more',
		          previous: 'md md-chevron-left',
		          next: 'md md-chevron-right'
		        },
            inline: true};
	
	$("#datetimepickerFrom").datetimepicker(datetimepickerOption).on('dp.change', function(e) {
		if($scope.search&&!$scope.buttonTriggerTime){
			$("#datetimepickerTo").data("DateTimePicker").minDate(e.date);
			var ptd = $("#datetimepickerTo").data("DateTimePicker").date();
			$scope.$apply(function(){
				$scope.search.week.st = e.date.toDate().getTime();
				$scope.chooseTimeName = e.date.format("MM/DD")+" - "+ptd.format("MM/DD");
				angular.forEach($scope.dateChooseButtons,function(v){
					if(v.to.toDate().getTime() == ptd.toDate().getTime() && e.date.toDate().getTime() == v.from.toDate().getTime()){
						v.active = true;
						$scope.chooseTimeName = v.name;
					}else v.active = false
				});
			});
		}
	});
	
	$("#datetimepickerFrom").data("DateTimePicker").date(moment().startOf('month'));
	
	$("#datetimepickerTo").datetimepicker(datetimepickerOption).on('dp.change', function(e) {
		$("#datetimepickerFrom").data("DateTimePicker").maxDate(e.date);
		var pfd = $("#datetimepickerFrom").data("DateTimePicker").date();
		if($scope.search&&!$scope.buttonTriggerTime)$scope.$apply(function(){
			$scope.search.week.et = e.date.toDate().getTime();
			$scope.chooseTimeName = pfd.format("MM/DD")+" - "+e.date.format("MM/DD");
			angular.forEach($scope.dateChooseButtons,function(v){
				if(v.from.toDate().getTime() == pfd.toDate().getTime() && e.date.toDate().getTime() == v.to.toDate().getTime()){
					v.active = true;
					$scope.chooseTimeName = v.name;
				}else v.active = false
			});
		});
		$scope.buttonTriggerTime = false;
	});
	
	$("#datetimepickerTo").data("DateTimePicker").date(moment().endOf('month'));
	
	$scope.timeSearchOpen = false;
	$scope.searchTime = 4;
	$scope.chooseTimeName = "时间段";
	$scope.dateChooseButtons = [
        {name:"本周",from:moment().startOf('week'),to:moment().endOf('week')},
        {name:"下周",from:moment().add(7, 'd').startOf('week'),to:moment().add(7, 'd').endOf('week')},
		{name:"上周",from:moment().subtract(7, 'd').startOf('week'),to:moment().subtract(7, 'd').endOf('week')},
		{name:"本月",from:moment().startOf('month'),to:moment().endOf('month'),active:true},
		{name:"上月",from:moment().subtract(1,"M").date(1),to:moment().subtract(1,"M").endOf('month')},
		{name:"今年",from:moment().startOf('year'),to:moment().endOf("year")}
    ];
	$scope.buttonDate = function(buttonDate){
		angular.forEach($scope.dateChooseButtons,function(v){
			v.active = buttonDate==v;
		});
		//定义按钮触发时间控件改变，防止时间控件Apply报错
		$scope.buttonTriggerTime = true;
		$("#datetimepickerFrom").data("DateTimePicker").date(buttonDate.from);
		$("#datetimepickerTo").data("DateTimePicker").date(buttonDate.to);
		$scope.search.week = {name:"choose",st:buttonDate.from.toDate().getTime(),et:buttonDate.to.toDate().getTime()};
		$scope.chooseTimeName = buttonDate.name;
	}
	
	function searchGroupDateChoose(){
		$scope.timeSearchOpen = true;
		$("body").bind("mousedown", onBodyDown);
	}
	$scope.searchGroup = function(t){
		var week ;
		if(t==1){
			week = {name:"all"};
		}else if(t==2){
			week = {name:"no"};
		}else if(t==3){
			if($scope.chooseTimeName=="时间段")$scope.chooseTimeName=$scope.dateChooseButtons[3].name;
			var st = $("#datetimepickerFrom").data("DateTimePicker").date().toDate().getTime();
			var et = $("#datetimepickerTo").data("DateTimePicker").date().toDate().getTime();
			week = {name:"choose",st:st,et:et};
		}
		if(angular.isUndefined($scope.search)){
			$scope.search = {week:week} ;
		}else{
			$scope.search.week = week ;
		}
		searchGroupDateChoose();
	}
	
	function onBodyDown(event) {
		if (!($(event.target).hasClass("dropdown-menu") || $(event.target).parents(".dropdown-menu").length > 0)) {
			$scope.$apply(function(){$scope.timeSearchOpen = false;});
			$("body").unbind("mousedown", onBodyDown);
		}
	}
	
	function searchGroupDateChoose(){
		$scope.timeSearchOpen = true;
		$("body").bind("mousedown", onBodyDown);
	}

	$scope.$watch("search",function(v){
		if(angular.isDefined($scope.search) && angular.isUndefined($scope.search.week)){
			$("#datetimepickerFrom").data("DateTimePicker").date(moment().startOf('month'));
			$("#datetimepickerTo").data("DateTimePicker").date(moment().endOf('month'));
			$scope.search.week = {name:"choose",st:moment().startOf('month').valueOf(),et:moment().endOf('month').valueOf()};
			$scope.chooseTimeName = "本月";
		}else{
			searchDebounce();
		}
	},true);
	
	var searchDebounce=debounce(function(){
		initLackInfos();
	},600);
	
	//工时管理
	$scope.openWeekDayModal=function(){
		var options = {
				title:'工时管理<br><small style="margin-top:5px">点击日期切换工时数<small>',
				size:"",
				animation:true,
				onDismiss:function(params){
					
				}
		} ;
		Modal.open("project/task/weekday.html",{},options) ;
	}
	
	$scope.downLoadWorkLog=function(){
		if($params.selectedDeptNodes.length==0){
			Messenger.error("请指定部门...");
		}else if(angular.isDefined($scope.search) && angular.isDefined($scope.search.week)){
			var start = $scope.search.week.st ;
			var end = $scope.search.week.et ;
			$http.post("ws/wl/synchronizeWorkLog",{start:start,end:end,deptIds:getSelectedDeptIds()}).success(function(data){
				downWorkLog('report/workLog/',start,end) ;
			}) ;
		}else{
			Messenger.error("请选择时间范围...");
		}
	}
	
	$scope.downLoadFinancialLog=function(){
		if($params.selectedDeptNodes.length==0){
			Messenger.error("请指定部门...");
		}else if(angular.isDefined($scope.search) && angular.isDefined($scope.search.week)){
			downWorkLog('report/finance/',$scope.search.week.st,$scope.search.week.et)
		}else{
			Messenger.error("请选择时间范围...");
		}
	}
	
	function downWorkLog(urlPrefix,start,end){
		window.location.href= urlPrefix + start+"/"+end+"/"+JSON.stringify(getSelectedDeptIds());
	}
	
	$scope.downLoadLackLog=function(){
		if(angular.isDefined($scope.search) && angular.isDefined($scope.search.week)){
			downWorkLog('report/workLog/lack/',$scope.search.week.st,$scope.search.week.et)
		}else{
			Messenger.error("请选择时间范围...");
		}
	}
	
}])
.controller('weekDayController',['$compile','Modal','ModelFactory','$scope','$http','$stateParams','$state',function($compile,Modal,ModelFactory,$scope,$http,$stateParams,$state){
	$params = $scope.$params = {} ;
	//起始时间
	$params.startDate = moment() ;
	
	initWeekDayEvents() ;
	//初始化函数
	function initWeekDayEvents(){
		var now = moment($params.startDate) ;
 		
	    var startMonth = now.get('month')-1 ;
	    var startYear = now.get('year') ;
	    if(startMonth==-1){
	    	startYear = startYear - 1 ;
	    	startMonth = 12 ;
	    }else{
	    	startMonth = startMonth +1 ;
	    }
	    var start = startYear+'-'+startMonth+'-'+now.get('date') ;
	    var endMonth = now.get('month')+1 ;
	    var endYear = now.get('year') ;
	    if(endMonth == 12){
	    	endYear = startYear +1 ;
	    	endMonth = 1 ;
	    }else{
	    	endMonth = endMonth +1 ;
	    }
	    var end = endYear+'-'+endMonth+'-'+now.get('date') ;
	    
		$http.post("ws/getWeekDayEvents",{conditions:{"start":start,"end":end}}).success(
				function(data){
					$params.weekDayEvents = data.result ;
				}
		) ;
	}
	
	$scope.generateDefaultWeekDays=function(){
		$http.post("ws/generateDefaultWeekDays",{weekDays:getWeekDays()}).success(
				function(data){
					initWeekDayEvents() ;
				}
		) ;
	}
	
	function getWeekDays(){
		var current = moment($params.startDate).format('YYYY-MM-DD') ;
		var year = current.split('-')[0] ;
		var month = current.split('-')[1] ;
	    var tempTime = new Date(year,month,0);
	    var time = new Date();
	    var weekDay = new Array();
	    for(var i=1;i<=tempTime.getDate();i++){
	        time.setFullYear(year,month-1,i);
	        var day = time.getDay();
	        if(day != 6 && day != 0) {
	        	var workDate = new Date() ;
	        	workDate.setFullYear(year,month-1,i);
	        	var WeekDay = {workDate:workDate,estimate:7.5} ;
	        	weekDay.push(WeekDay) ;
	        }
	    }
	    return weekDay ;
	}
	
	$scope.estimateEventAfterAllRender=function(params){
		if($('.est-prev-button').length==0){
			var prevButtonHtml =  $compile('<a class="fc-prev-button fc-button fc-state-default fc-corner-left fc-corner-right wd-prev-button" ng-click="prevClick()">'+
					'<span class="fc-icon fc-icon-left-single-arrow">'+
					'</span></a>'
				)($scope) ;
			$('.fc-prev-button').replaceWith(prevButtonHtml) ;

			var nextButtonHtml =  $compile('<a class="fc-next-button fc-button fc-state-default fc-corner-left fc-corner-right wd-next-button" ng-click="nextClick()">'+
											'<span class="fc-icon fc-icon-right-single-arrow">'+
											'</span></a>'
									)($scope) ;
			$('.fc-next-button').replaceWith(nextButtonHtml) ;
		}
	}
	
	$scope.prevClick=function(){
		var now = moment($params.startDate) ;
		var month = now.get('month')-1 ;
		var year = now.get('year') ;
		if(month ==-1){
			month =11 ;
			now.year(year-1) ;
		}
		now.month(month) ;
		now.date(1) ;
		$params.startDate = now.format('YYYY-MM-DD') ;
		initWeekDayEvents() ;
	}
	
	$scope.nextClick=function(){
		var now = moment($params.startDate) ;
		var month = now.get('month')+1 ;
		var year = now.get('year') ;
		if(month == 12) {
			month = 0 ;
			now.year(year+1) ;
		}
		now.month(month) ;
		now.date(1) ;
		$params.startDate = now.format('YYYY-MM-DD') ;
		initWeekDayEvents() ;
	}
	
	$scope.eventClick=function(event,jsEvent,view){
		var event = event.event ;
		if(angular.equals(event.title,7.5)){
			event.title = 4 ;
		}else if(angular.equals(event.title,4)){
			event.title = 0 ;
		}else {
			event.title = 7.5 ;
		}
		$http.post("ws/updateWeekDayEstimate",{weekDay:{workDate:moment(event.start),estimate:event.title}}).success(
				function(data){
					initWeekDayEvents() ;
				}
		) ;
	}
	
}])