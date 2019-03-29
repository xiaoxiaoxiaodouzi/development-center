developmentCenter.filter('sex',function(){
	return function(input){
		var out = input;
		if("-1"==input){
			out = "未知";
		}else if("M"==input){
			out = "男";
		}else if("F"==input){
			out = "女";
		}
		return out;
	}
});


developmentCenter   
    .controller('personalCtrl', function($scope,$http,Messenger,Modal,ModelFactory,Upload,$rootScope){
    	
    	$scope.headIcons = [
    	    {id:"0",url:"assets/img/profile-pics/1.jpg",selected:true},
    	    {id:"1",url:"assets/img/profile-pics/2.jpg",selected:false},
    	    {id:"2",url:"assets/img/profile-pics/3.jpg",selected:false},
    	    {id:"3",url:"assets/img/profile-pics/4.jpg",selected:false},
    	    {id:"4",url:"assets/img/profile-pics/5.jpg",selected:false},
    	    {id:"5",url:"assets/img/profile-pics/6.jpg",selected:false},
    	    {id:"6",url:"assets/img/profile-pics/7.jpg",selected:false},
    	    {id:"7",url:"assets/img/profile-pics/8.jpg",selected:false},
    	    {id:"8",url:"assets/img/profile-pics/9.jpg",selected:false},
    	]; 
    	
    	$scope.selectedHi = {id:"0",url:"assets/img/profile-pics/1.jpg",selected:true};
    	
    	$scope.getLogUser=function(){
    		$http.post("ws/getUserInfo",{})
    		.success(function(data, status, headers, config){
    			$scope.currUserDTO = data;
    			if(!angular.isUndefined($scope.currUserDTO.remark1) && ""!=$scope.currUserDTO.remark1){
    				var isExist = false;
    				for(var i=0;i<$scope.headIcons.length;i++){
    					if($scope.headIcons[i].url==$scope.currUserDTO.remark1){
    						$scope.headIcons[$scope.selectedHi.id].selected=false;
    						$scope.selectedHi = $scope.headIcons[i];
    						$scope.selectedHi.selected = true;
    						isExist = true;
    						break;
    					}
    				}
    				if(!isExist){
    					$scope.selectedHi = {id:"-1",url:$scope.currUserDTO.remark1,selected:true};
    					$scope.headIcons["0"].selected=false;
    				}
    			}
    		});
    	}    	
    	
    	$scope.getLogUser();
    	
    	$scope.hi_mouseenter = function(hi){
    		if(!angular.isUndefined(hi)){
    			if(!angular.isUndefined($scope.selectedHi.id) && "-1"!=$scope.selectedHi.id){   				
    				$scope.headIcons[$scope.selectedHi.id].selected=false;
    			}
    					
    			hi.selected = true;
    		}
    	}
    	
    	$scope.hi_mouseleave = function(hi){
    		if(!angular.isUndefined(hi)){
    			hi.selected = false;
    			if(!angular.isUndefined($scope.selectedHi.id) && "-1"!=$scope.selectedHi.id){ 
    				$scope.headIcons[$scope.selectedHi.id].selected=true;
    			}
    		}
    	}
    	
    	$scope.hi_mousedown = function(hi){
    		if(!angular.isUndefined(hi)){
    			$scope.currUserDTO.remark1 = hi.url;
    			$http.post("ws/editUserImg",{userDto:$scope.currUserDTO})
        		.success(function(data, status, headers, config){
        			$scope.selectedHi = hi;
        			$scope.isShowSelected = false;
        			$rootScope.subject.remark1 = hi.url;
        			Messenger.success("保存成功！");
        		});
    		}
    	}
    	
    	/*$scope.editInfo=function(isEdit){
    		$scope.isEditInfo=isEdit;	
    	}
    	
    	$scope.editContact=function(isEdit){
    		$scope.isEditContact=isEdit;	
    	}
    	
    	$scope.saveInfo=function(){
    		$http.post("ws/editUser",{userDto:$scope.currUserDTO})
    		.success(function(data, status, headers, config){
    			$scope.cancelInfo();
    			Messenger.success("保存成功！");
    		});
    	}
    	
    	$scope.cancelInfo=function(){
    		$scope.editInfo(false);    		
    		$scope.getLogUser();
    	}
    	
    	$scope.saveContact=function(){
    		$http.post("ws/editUser",{userDto:$scope.currUserDTO})
    		.success(function(data, status, headers, config){
    			$scope.canceContact();
    			Messenger.success("保存成功！");
    		});
    	}
    	
    	$scope.canceContact=function(){
    		$scope.editContact(false);    		
    		$scope.getLogUser();
    	}*/
    	
    	$scope.editPwd=function(){
    		//Messenger.error("暂未开放");
    		window.location.href="http://www.c2cloud.cn/#/modifyPwd" ;
    	}
    	
    	
    	$scope.dataURItoBlob=function(dataURI) {
    	    // convert base64/URLEncoded data component to raw binary data held in a string
    	    var byteString;
    	    if (dataURI.split(',')[0].indexOf('base64') >= 0)
    	        byteString = atob(dataURI.split(',')[1]);
    	    else
    	        byteString = unescape(dataURI.split(',')[1]);

    	    // separate out the mime component
    	    var mimeString = dataURI.split(',')[0].split(':')[1].split(';')[0];

    	    // write the bytes of the string to a typed array
    	    var ia = new Uint8Array(byteString.length);
    	    for (var i = 0; i < byteString.length; i++) {
    	        ia[i] = byteString.charCodeAt(i);
    	    }

    	    return new Blob([ia], {type:mimeString});
    	}
    	
    	$scope.uploadFile=function(fileObj){
			var notify = Messenger.post('文件上传开始!');
			Upload.upload({
				url : 'file',
				file : fileObj,
				fileName : fileObj.name,
				fileFormDataName : 'file',
				sendFieldAs : 'form'
			}).progress(function(evt) {
				notify.update('message', '上传中：' + parseInt(100.0 * evt.loaded / evt.total) + '% ');
			}).success(function(data, status, headers, config) {				
				notify.update('message', '上传成功!');
				$scope.currUserDTO.remark1 = data.url;
    			$http.post("ws/editUserImg",{userDto:$scope.currUserDTO})
        		.success(function(data, status, headers, config){
        			$scope.selectedHi.selected=false;
        			$scope.selectedHi = {id:"-1",url:$scope.currUserDTO.remark1,selected:true};
        			$scope.isShowSelected = false;        			
        		});
			}).error(function(data, status, headers, config) {
				notify.update('type', 'danger');
				notify.update('message', '上传失败，错误信息：' + data.message);
			});
    	}
    	
    	$scope.fileSelected = function($files, $file, $event, $rejectedFiles, type) {
			if (!$file && $rejectedFiles.length > 0) {
				Messenger.error('不允许上传大于1M或非图片格式文件');
				return;
			}
			if (!$file){
				return;
			}
			
			//文件压缩处理
		    lrz($file,{width:240,quality:0.7}).then(function (rst){
		    	var blobFile=$scope.dataURItoBlob(rst.base64);
		    	blobFile.lastModifiedDate=new Date();
		    	blobFile.name=rst.origin.name;
		    	$scope.uploadFile(blobFile);
	            // 处理成功会执行
	        })['catch'](function (err) {
	        	$scope.uploadFile($file);
	            // 处理失败会执行
	        }).always(function () {
	            // 不管是成功失败，都会执行
	        });
		    
		};
    });

developmentCenter.controller('passwordCtrl',function($rootScope,$scope,$http,Messenger,Modal){
	var url = "http://auth.c2cloud.cn/c2cloud-portal/#/f/password?username=";
	var userName = $rootScope.subject.userName;
	$scope.modifyPwdUrl = url+userName;	
});

developmentCenter.controller('membersCtrl',function($scope,$http,Messenger,Modal,$stateParams){
	$scope.projectId = $stateParams.project;
	$scope.getMembers=function(){
		$http.post("ws/getMembers",{projectId:$stateParams.project})
		.success(function(data, status, headers, config){
			angular.forEach(data.result, function (value, key) {
				value.permDates = [
		     	    {jobId:"11",jobName:"项目负责人",icon:"md-person"},
		    	    {jobId:"12",jobName:"文档管理员",icon:"md-attachment"},
		    	    {jobId:"13",jobName:"开发",icon:"md-open-with"},
		    	    {jobId:"14",jobName:"测试",icon:"md-swap-calls"},
		    	];				
			});
			$scope.members = data.result;
		});
	}
	
	$http.post("ws/getProjectCreator",{projectId:$stateParams.project})
	.success(function(data,status,headers,config){
		$scope.projectCreator = data.result;
	});
	
	$http.post("ws/isProjectPermitedByBatch",{projectId:$stateParams.project,permExprs:["member_authorize","member_remove","advancedSet_menu"]})
	.success(function(data,status,headers,config){
		$scope.memberAuthorize = data.result["member_authorize"];
		$scope.memberRemove = data.result["member_remove"];	
		$scope.advancedSetMenu = data.result["advancedSet_menu"];
	});
	
	$scope.isShowMember = function(obj){
		if(!angular.isUndefined(obj)){
			return obj.userDTO.user_id!=$scope.projectCreator.user_id;			
		}
		return false;
	}
	
	$scope.isShowJob = function(obj){
		if(!angular.isUndefined(obj)){
			var jobId = obj.jobId; 
			if("11"==jobId){
				obj.icon = "md-person";
				obj.color = "m-r-5 bgm-cyan";
			}else if("12"==jobId){
				obj.icon = "md-attachment";
				obj.color = "m-r-5 bgm-teal";
			}else if("13"==jobId){
				obj.icon = "md-open-with";
				obj.color = "m-r-5 bgm-green";
			}else if("14"==jobId){
				obj.icon = "md-swap-calls";
				obj.color = "bgm-lightgreen";
			}else{
				return false;
			}
			return obj;
		}
		return false;
	}
	
	$scope.getMembers();
	
	$scope.newMember = function(){
		Modal.open('project/settings/member-invite.html',{},{
			title:"邀请成员",
			 onClose:function(data){		
			 },
			 onDismiss:function(data){				 
			 }
		});
	}
	
	$scope.addMember = function(){
		
		var projectMembers = new Array();
		projectMembers.push($scope.projectCreator.user_name);
		angular.forEach($scope.members,function(m){
			projectMembers.push(m.userDTO.user_name);
		});
		Modal.open('project/settings/member-import.html',{members:projectMembers},{
			title:"导入成员",
			size:'big',
			 onClose:function(data){
				 $scope.getMembers();
			 },
			 onDismiss:function(data){				 
			 }
		});
	}
	
	$scope.removeMembers = function(userId){
		$http.post("ws/removeMembers",{projectId:$stateParams.project,"userId":userId})
		.success(function(data, status, headers, config){			
			$scope.getMembers();
		});
	}
	
	$scope.itemClick = function(item,userId){
		$http.post("ws/setMembersByPerm",{projectId:$stateParams.project,"userId":userId,"jobId":item.jobId,"state":item.selected})
		.success(function(data, status, headers, config){
		});
	}
});

developmentCenter.controller('addMemberCtrl',function($scope,$http,Messenger,Modal,$stateParams){
	$scope.condition = {projectId:$stateParams.project};
	
	$scope.getAddMembers = function(){
		$scope.addMembers = [];
		$scope.isLoad = true;
		$scope.isShowNull = false;
		$scope.chooseAllColor = false;
		
		$http.post("ws/getAddMembers",$scope.condition).success(function(data, status, headers, config){
			$scope.isLoad = false;
			angular.forEach(data.result,function(d){
				angular.forEach($scope.members,function(name){
					if(d.userName == name){
						d.selected = true;
						d.choosed = true;
					}
				});
			});
			$scope.addMembers = data.result;
//			if((angular.isUndefined($scope.userRealname) || null==$scope.userRealname || ""==$scope.userRealname) && data.result.length==0){
//				$scope.isShowNull = true;
//			}else{
//				$scope.isShowNull = false;
//			}
		});
	}	
	
	$scope.addMemberChecked = {};
	
	$scope.selectedMember = function(addMember){
		if(addMember.choosed){
			Messenger.post({message:"该用户已加入项目成员",type:"info",icon:"md md-check"});
			return ;
		}
		addMember.selected = !addMember.selected;
		if(addMember.selected){
			$scope.addMemberChecked[addMember.userId]=addMember;
		}else{
			delete $scope.addMemberChecked[addMember.userId];
		}
	}
	
	$scope.save = function(){
		var addMemberNames = [];
		angular.forEach($scope.addMemberChecked, function (value, key) {
			addMemberNames.push(value.userName);
		});
		
		if(addMemberNames.length==0){
			Messenger.error("请选择需要加入的成员！");
			return;
		}
		$http.post("ws/addMembers",{
			projectId:$stateParams.project,
			userIds:addMemberNames		
		})
		.success(function(data, status, headers, config){			
			Modal.close();
		});
	}
	
	$scope.close = function(){
		Modal.dismiss();
	}
	
	$scope.chooseAll = function(){
		$scope.chooseAllColor = !$scope.chooseAllColor;
		angular.forEach($scope.addMembers,function(m){
			if(!m.selected&&$scope.chooseAllColor){
				$scope.addMemberChecked[m.userId]=m;
			}
			if(m.selected&&!$scope.chooseAllColor){
				delete $scope.addMemberChecked[m.userId];
			}
			if(!m.choosed)m.selected = $scope.chooseAllColor;
		});
	}
	
	$scope.$watch("condition",function(v,o){
		$scope.getAddMembers();
	},true);
	
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
	} ;
	
	function zTreeOnClick(event, treeId, treeNode){
		$scope.deptName = treeNode.name;
		$scope.condition.orgId = treeNode.id;
		$("#deptDropdown").controller("c2Dropdown").toggle();
	}
	$scope.treeClean = function(){
		$scope.deptName = null;
		$scope.condition.orgId = null;
		$("#deptDropdown").controller("c2Dropdown").toggle();
	}
	$http.get("ws/dept/getCurrentUserDeptTree").success(function(data){
		$scope.deptTree = data.result ;
	});
});


developmentCenter.controller('transformOwnerCtrl',function($scope,$http,Messenger,Modal,$stateParams){
	
	$http.post("ws/getMembers",{projectId:$stateParams.project})
	.success(function(data, status, headers, config){
		$scope.members = data.result;
	});	
	
	$http.post("ws/getProjectCreator",{projectId:$stateParams.project})
	.success(function(data,status,headers,config){
		$scope.projectCreator = data.result;
	});
	
	$scope.isShowMember = function(obj){
		if(!angular.isUndefined(obj)){
			return obj.userDTO.user_id!=$scope.projectCreator.user_id;			
		}
		return false;
	}	
	
	$scope.selectedMember = function(memberId,index){		
		$scope.ownerId = memberId;
		$scope.selectedIndex = index;
	}
	
	$scope.save = function(){
		if(!angular.isUndefined($scope.ownerId) && ""!=$scope.ownerId){
			$http.post("ws/transformOwner",{projectId:$stateParams.project,ownerId:$scope.ownerId})
			.success(function(data,status,headers,config){
				Messenger.success("移交成功！");
				Modal.close();
			});
		}else{
			Messenger.error("请选择移交的成员！");
		}
	}
	
	$scope.close = function(){
		Modal.dismiss();
	}	
});

developmentCenter.controller('deleteProjectCtrl',function($scope,$http,Messenger,Modal,$stateParams){
	
	$scope.onDelete=function(){
		$http.post("ws/deleteProject",{projectId:$stateParams.project})
		.success(function(data,status,headers,config){			
			Modal.close();
			window.location.href = '#/projects';
		});
	}

});

developmentCenter.controller('archiveProjectCtrl',function($scope,$http,Messenger,Modal,$stateParams){
	$scope.onArchive=function(){
		$http.post("ws/archiveProject",{projectId:$stateParams.project})
		.success(function(data,status,headers,config){			
			Modal.close();
			window.location.href = '#/projects';
		});
	}

});
developmentCenter.controller('activeProjectCtrl',function($scope,$http,Messenger,Modal,$stateParams){
	$scope.onActive=function(projectid){
		$http.post("ws/activeProject",{projectId:projectid})
		.success(function(data,status,headers,config){			
			Modal.close();
			window.location.href = '#/projects';
		});
	}
})
