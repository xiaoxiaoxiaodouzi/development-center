/*项目共享页面控制器*/
angular.module('project').controller('docsController',['$scope','$http','Modal','ModelFactory','$stateParams','markdownConverter','Messenger','Upload','$timeout',function($scope,$http,Modal,ModelFactory,$stateParams,markdownConverter,Messenger,Upload,$timeout){
    	/*头部新增文档*/
    	if($stateParams.init=="add"){
    		$timeout(function(){
    			$scope.addDocState = true;
    		},300);
    	}
    	$scope.docTitleCheck = false;
    	$scope.refreshList=function(){
    		$http.post("ws/simpleSelectList",{statement:"selectDocList",parameter:{projectId:$stateParams.project}}).success(function(data){
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
    		var entityDataSource=ModelFactory.entity("doc","id",{id:doc.id});
			entityDataSource.$promise.then(function(){
				entityDataSource.$delete().then(function(){
					$scope.docs.splice($scope.docs.indexOf(doc),1);
				});
			});
    	};

		$scope.delFile=function(docFiles,file){
			if(file.id){
				var entityDataSource=ModelFactory.entity("docFile","id",{id:file.id,path:file.path});
				entityDataSource.$promise.then(function(){
					entityDataSource.$delete({path:file.path}).then(function(){
						docFiles.splice(docFiles.indexOf(file),1);
					});
				});
			}else{
				var wSource=ModelFactory.ws("delFile",{path :file.path});
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
				var newDocFile=ModelFactory.entity('docFile');
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
			
			$http.post("ws/saveDoc",{doc:entity,fileList:docFiles}
			).success(function(data){
				$scope.refreshList();
			});
			$scope.addDocState = false;
			$scope.cleanForm();
		};
		$scope.cleanForm = function(){
			$scope.entity = {docFiles:[],project : $stateParams.project};
		}
		
		$http.post("ws/isProjectPermitedByBatch",{projectId:$stateParams.project,permExprs:["doc_del"]})
		.success(function(data,status,headers,config){
			$scope.docDel = data.result["doc_del"];
		});
    }]);