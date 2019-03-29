developmentCenter.controller('homeSharesController',['$scope','$http','Modal','ModelFactory','$stateParams','markdownConverter','Messenger','Upload','$timeout',function($scope,$http,Modal,ModelFactory,$stateParams,markdownConverter,Messenger,Upload,$timeout){
    	/*头部新增文档*/
    	if($stateParams.init=="add"){
    		$timeout(function(){
    			$scope.addDocState = true;
    		},300);
    	}
    	
    	$scope.addDoc=function(){
    		if($scope.docManager){
    			$scope.addDocState = true ;
    		}else{
    			Messenger.post('暂未开放!');
    		}
    	}
    	
    	$scope.docTitleCheck = false;
    	$scope.refreshList=function(){
    		$http.post("ws/simpleSelectList",{statement:"selectShareDocList",parameter:{projectId:$stateParams.project}}).success(function(data){
    			$scope.nothing = data.result.length==0
    			angular.forEach(data.result,function(v,i){
    				v.edit = false;
    				if(!v.content) return ;
    				var docHtml = v.content.length>400?markdownConverter.render(v.content.substring(0,400)+"..."):markdownConverter.render(v.content);
    				v.text = angular.element(docHtml).text();
    			});
	    		$scope.docs = data.result;
	    	});
    	};
    	$scope.refreshList();
    	$scope.entity = {
				project :  $stateParams.project,
				docFiles:[]
			};
    	$scope.delDoc=function(doc){
    		var entityDataSource=ModelFactory.entity("shareDoc","id",{id:doc.id});
			entityDataSource.$promise.then(function(){
				entityDataSource.$delete().then(function(){
					$scope.docs.splice($scope.docs.indexOf(doc),1);
				});
			});
    	};

		$scope.delFile=function(docFiles,file){
			if(file.id){
				var entityDataSource=ModelFactory.entity("shareDocFile","id",{id:file.id,path:file.path});
				entityDataSource.$promise.then(function(){
					entityDataSource.$delete({path:file.path}).then(function(){
						docFiles.splice(docFiles.indexOf(file),1);
					});
				});
			}else{
				var wSource=ModelFactory.ws("delShareFile",{path :file.path});
				wSource.$load().then(function(data){
					docFiles.splice(docFiles.indexOf(file),1);
				});
			}
		}
		$scope.fileSelected = function($files, $file, $event, $rejectedFiles,doc, docFiles) {
			if (!$file && $rejectedFiles.length > 0) {
				Messenger.error('不允许上传大于500M的文件');
				return;
			}
			if (!$file)
				return;

			var notify = Messenger.post('文件上传开始!');
			Upload.upload({
				url : 'file',
				file : $file,
				fileName : $file.name,
				fileFormDataName : 'file',
				sendFieldAs : 'form'
			}).progress(function(evt) {
				notify.update('message', '上传中：' + parseInt(100.0 * evt.loaded / evt.total) + '% ');
			}).success(function(data, status, headers, config) {
				var newDocFile=ModelFactory.entity('shareDocFile');
				newDocFile.name=data.name;
				newDocFile.path=data.url;
				newDocFile.docId=doc.docId;
				docFiles.unshift(newDocFile);
				notify.update('message', '上传成功!');
			}).error(function(data, status, headers, config) {
				notify.update('type', 'danger');
				notify.update('message', '上传失败，错误信息：' + data.message);
			});
		};
		$scope.submit = function(entity,docFiles) {
			if(!entity.name){
				if(!entity.id){
					Messenger.error("文档标题不能为空！");
					$scope.docTitleCheck = true; 
				}else{
					Messenger.error("文档标题不能为空！");
				}
				return ;
			}
			
			$http.post("ws/saveShareDoc",{doc:entity,fileList:docFiles}
			).success(function(data){
				$scope.refreshList();
			});
			$scope.addDocState = false;
			$scope.cleanForm();
		};
		$scope.cleanForm = function(){
			$scope.entity = {docFiles:[],project : $stateParams.project};
		}
		
		$http.post("ws/isPermitedMap",{permExprs:["doc_share_manager"]})
		.success(function(data,status,headers,config){
			$scope.docManager = data.result["doc_share_manager"];
		});
    }])
	/*共享详情页面控制器*/
    .controller("homeShareDocController",['$scope','$timeout','$ocLazyLoad','$http','$stateParams','ModelFactory','Messenger','Upload',function($scope,$timeout,$ocLazyLoad,$http,$stateParams,ModelFactory,Messenger,Upload){
    	/*获取文档*/
    	function getDoc(){
    		$scope.menuShow = true;
    		$http.post("ws/simpleSelectOne",{statement:"selectShareDocView",parameter:{docId:$stateParams.docId}}).success(function(data){
        		$scope.doc = data.result;
        		if(!$scope.doc.content) return ;
        		/*解析文档菜单*/
        		var tokens = marked.lexer($scope.doc.content);
        		tokens=$.grep(tokens,function(o,i){
        			return o.type=='heading';
        		});
        		var firstDepth,secondDepth,lastCharpter,docMenus = new Array(),x=0,y=0;
        		angular.forEach(tokens,function(v,i){
        			if(i==0){
        				firstDepth = v.depth;
        				secondDepth = v.depth+1;
        			}
        			if(v.depth==firstDepth){
        				++x;
        				lastCharpter = {t:v.text,id:x+"_"+y};
        				docMenus.push(lastCharpter);
        			}
        			if(v.depth==secondDepth){
        				++y;
        				if(angular.isUndefined(lastCharpter.c))lastCharpter.c = new Array();
        				lastCharpter.c.push({t:v.text,id:x+"_"+y});
        			}
        		});
        		if(docMenus.length>0){
        			$scope.docMenus = docMenus;
    				/*固定菜单*/
                	$("#docNav").affix({
            		    offset: {
            		        top: function(){
            		        	return $(".card-header").outerHeight(true);
            		        }
            		      }
            		    });
                	/*滚动关联菜单*/
                	$scope.spy = function(){
                		$timeout(function(){
            				$('body').scrollspy({"target":"#docMenus","offset":80});
            			},0);
                	}
                	$ocLazyLoad.load(['assets/js/jquery.scrollTo.min.js']).then(function(){
                		$scope.scrollTo = function(menu){
                    		$(window).scrollTo($("#"+menu.id),300, {offset:-80});
                    	}
                	});
        		}else{
        			$scope.menuShow = false;
        		}
        	});
    	}
    	/*获取文档附件*/
    	function getDocFiles(){
    		$http.post("e/shareDocFile/op/search",{docId:$stateParams.docId}).success(function(data){
        		$scope.docFiles = data.result;
        	});
    	}
    	getDoc();
    	getDocFiles();
    	
    	/*文档编辑*/
    	var projectId = $stateParams.project;
    	$scope.entity = {
				project : projectId,
				docFiles:[]
			};
    	$scope.editDocView = function(doc){
    		$scope.docEditState = true;
    		$scope.editDoc = angular.copy(doc);
    	}
    	$scope.delDoc=function(doc){
    		var entityDataSource=ModelFactory.entity("shareDoc","id",{id:doc.id});
			entityDataSource.$promise.then(function(){
				entityDataSource.$delete().then(function(){
					location.href="#/home/shares";
				});
			});
    	};
		$scope.delFile=function(docFiles,file){
			if(file.id){
				var entityDataSource=ModelFactory.entity("shareDocFile","id",{id:file.id,path:file.path});
				entityDataSource.$promise.then(function(){
					entityDataSource.$delete({path:file.path}).then(function(){
						docFiles.splice(docFiles.indexOf(file),1);
					});
				});
			}else{
				var wSource=ModelFactory.ws("delShareFile",{path :file.path});
				wSource.$load().then(function(data){
					docFiles.splice(docFiles.indexOf(file),1);
				});
			}
		}
		$scope.fileSelected = function($files, $file, $event, $rejectedFiles,doc, docFiles) {
			if (!$file && $rejectedFiles.length > 0) {
				Messenger.error('不允许上传大于500M的文件');
				return;
			}
			if (!$file)
				return;

			var notify = Messenger.post('文件上传开始!');
			Upload.upload({
				url : 'file',
				file : $file,
				fileName : $file.name,
				fileFormDataName : 'file',
				sendFieldAs : 'form'
			}).progress(function(evt) {
				notify.update('message', '上传中：' + parseInt(100.0 * evt.loaded / evt.total) + '% ');
			}).success(function(data, status, headers, config) {
				var newDocFile=ModelFactory.entity('shareDocFile');
				newDocFile.name=data.name;
				newDocFile.path=data.url;
				newDocFile.docId=doc.docId;
				docFiles.unshift(newDocFile);
				notify.update('message', '上传成功!');
			}).error(function(data, status, headers, config) {
				notify.update('type', 'danger');
				notify.update('message', '上传失败，错误信息：' + data.message);
			});
		};
		$scope.submit = function(entity,docFiles) {
			if(!entity.name){
				if(!entity.id){
					Messenger.error("文档标题不能为空！");
				}else{
					Messenger.error("文档标题不能为空！");
				}
				return ;
			}
			
			$http.post("ws/saveShareDoc",{doc:entity,fileList:docFiles}
			).success(function(data){
				getDoc();
				getDocFiles();
				$scope.docEditState = false;
			});
		};
		
		$http.post("ws/isPermitedMap",{permExprs:["doc_share_manager"]})
		.success(function(data,status,headers,config){
			$scope.docDel = data.result["doc_share_manager"];
		});
    }]);