developmentCenter.directive('c2Tree', ['$timeout','UIDirectiveService', 'FormContainerFactory','ResInjector',
  function ($timeout,UIDirectiveService, FormContainer, ResInjector) {
    return { 
        scope: {
            bindData: "=",
            autoParam: "=",
            otherParam: "=?",
            expendLevelNumber: "=expendLevel",
            expendAllValue: "=expendAll",
            computeHeight:"=",
            computeHeightFix:"=",
            beforeAsync: "&",
            beforeCheck: "&",
            beforeClick: "&",
            beforeDblClick: "&",
            beforeDrag: "&",
            beforeDrop: "&",
            beforeRemove: "&",
            beforeRename: "&",
            beforeRightClick: "&",
            onAsyncError: "&",
            onAsyncSuccess: "&",
            nodeCheck: "&",
            nodeClick: "&",
            nodeDblClick: "&",
            nodeRightClick: "&",
            nodeDrag: "&",
            nodeDragMove: "&",
            nodeDrop: "&",
            nodeRemove: "&",
            nodeRename: "&",
            filter: "&",
            nameIsHtml:"=",
            selectedMulti: "=",
            showIcon: "=",
            showTitle: "=",
            searchEnable: "=",
            editorEnable: "=",
            dragMove: "=",
            dragCopy: "=",
            dragInner: "&",
            dragMoveable: "&",
            showRemoveBtn: "=showRemoveBtn",
            showRenameBtn: "=showRenameBtn",
            checkEnable: "=checkEnable",
            chkboxType: "=",
            autoCheckTrigger: "=",
            searchHide:"=",
            beforeInit:"&"
        },
        controller:function ($scope,$element,$attrs,$transclude){
        	ResInjector.addCss("ext/widget/tree/lib/zTreeStyle.css",$scope);
        	UIDirectiveService.checkUIAttrs("c2Tree",$attrs);
        	var form = FormContainer.findForm($scope);
            form[$attrs.treeId]=this; 
            var controller = this;
            
            //默认值
    		var defaultParams = {
    			otherParam:{}
    		};
    		angular.forEach(defaultParams,function(v,k){
    			if(angular.isUndefined($scope[k]))$scope[k]=v;
    		});
            
            //将树所有的方法给Controller，除了reAsyncChildNodes方法（重写了）。
	    	this.initController = function(ztree){
    			$scope.$ztree=ztree;
	         	angular.forEach(ztree,function(a,n){
	         		if(typeof a == "function"&&n!="reAsyncChildNodes"&&n!="refresh")controller[n] = function(){
	         			if(arguments.length==0)return ztree[n]();
	         			if(arguments.length==1)return ztree[n](arguments[0]);
	         			if(arguments.length==2)return ztree[n](arguments[0],arguments[1]);
	         			if(arguments.length==3)return ztree[n](arguments[0],arguments[1],arguments[2]);
	         			if(arguments.length==4)return ztree[n](arguments[0],arguments[1],arguments[2],arguments[3]);
	         			if(arguments.length==5)return ztree[n](arguments[0],arguments[1],arguments[2],arguments[3],arguments[4]);
	         		};
	         	});
	        }
	    	
	    	//（原生方法重写）强行异步加载父节点的子节点。parentNode:父节点对象,为空时加载根节点数据；reloadType："refresh" 表示清空后重新加载；其他表示追加子节点处理。isSilent：是否展开父节点，true为不展开，默认展开。
            this.reAsyncChildNodes = function (parentNode, reloadType, isSilent) {
            	if(!reloadType)reloadType="refresh";
            	controller.getZtree().reAsyncChildNodes(parentNode, reloadType, isSilent);
            }
            
            $scope.treeDom=$element.find(".ztree");
            
            this.id = $attrs.id;
    		this.name = "tree";
        	this.getDom = function(){
        		return $scope.treeDom;
        	}
        	this.getZtree = function(){
        	    return  $scope.$ztree;
        	}
        	this.getZtreeOptions = function(){
        		return $scope.options;
        	}
        	
        	function treeAsyncSuccess(ztree,openState,checkState,selectedState,openNodes,checkedNodes,selectedNodes){
            	var idKey = ztree.setting.data.simpleData.idKey;
            	if(openState){
        			angular.forEach(openNodes,function(v,n){
        				var theKey=v[idKey]?idKey:"name";
            			var nsn = ztree.getNodesByParam(theKey,v[theKey]);
            			if(nsn.length>0)ztree.expandNode(nsn[0],true);
            		});
        		}
            	if(checkState){
        			angular.forEach(checkedNodes,function(v,n){
        				var theKey=v[idKey]?idKey:"name";
        				var nsn = ztree.getNodesByParam(theKey,v[theKey]);
        				if(nsn.length>0)ztree.checkNode(nsn[0],true,false,false);
        			});
        		}
        		if(selectedState){
                	angular.forEach(selectedNodes,function(v,n){
                		var theKey=v[idKey]?idKey:"name";
                		var nsn = ztree.getNodesByParam(theKey,v[theKey]);
                		if(nsn.length>0)ztree.selectNode(nsn[0],true);
                	});
        		}
        	}
        	this.getParams = function(){
            	return controller.getZtree().setting.async.otherParam;
            }
        	
        	controller.treeStateKeep;
        	//刷新异步加载树。isSameState:是否刷新保持选中，勾选中，展开状态（执行效率好像不好）；url:更新树url;param：更新树参数；node：更新的树节点；
            this.refresh = function(isSameState,url,param,node){
            	var ztree = controller.getZtree();
            	if(url)ztree.setting.async.url = url;
            	if(param)ztree.setting.async.otherParam = param;
            	if(isSameState){
                	var idKey = ztree.setting.data.simpleData.idKey;
                	var openNodes = ztree.getNodesByParam("open",true);
                	var checkedNodes = ztree.getCheckedNodes(true);
                	var selectedNodes = ztree.getSelectedNodes();
                	controller.treeStateKeep = function(){
                		treeAsyncSuccess(ztree,true,true,true,openNodes,checkedNodes,selectedNodes);
                	}
            	}
            	ztree.reAsyncChildNodes(node,"refresh");
            }
            //刷新2
            this.refresh2 = function(params,openState,checkState,selectedState,node){
            	var ztree = controller.getZtree();
            	if(angular.isDefined(params)&&params!=null)ztree.setting.async.otherParam = params;
            	
            	var idKey = ztree.setting.data.simpleData.idKey;
            	var openNodes = ztree.getNodesByParam("open",true);
            	var checkedNodes = ztree.getCheckedNodes(true);
            	var selectedNodes = ztree.getSelectedNodes();
            	controller.treeStateKeep = function(){
            		treeAsyncSuccess(ztree,openState,checkState,selectedState,openNodes,checkedNodes,selectedNodes);
            	}
            	ztree.reAsyncChildNodes(node,"refresh");
            }
            
            this.deleteSelectedNode = function (callbackFlag) {
                var selectedNodes = controller.getZtree().getSelectedNodes();
                for (var i = 0, l = selectedNodes.length; i < l; i++) {
                	controller.getZtree().removeNode(selectedNodes[i], callbackFlag);
                }
                return selectedNodes;
            }
            this.setAsyncExpandNodeLevel = function (levelNum) {
                $scope.expendLevel = levelNum;
            }
            this.setAsyncExpandNodeAll = function(isExpand){
            	$scope.expendAll = isExpand;
            }
        },
        link: function (scope, element, attrs, controller) {
        	//树容器高度
        	$timeout(function(){
            	var treeHeight = attrs.treeHeight;
            	if(scope.computeHeight){
            		var containerHight = element.parents('.fixed-height-container').height();
            		treeHeight = containerHight-scope.computeHeightFix;
            	}
        		element.css("height",treeHeight);
        	},0);

            //初始展开所有
            scope.expendAll = scope.expendAllValue;
            //初始展开层级
            scope.expendLevel = scope.expendLevelNumber;
            //支持同步树
//            var asyncEnable = false;
//            var asyncUrl = "";
//            if (scope.bindData && scope.bindData.$url) {
//                asyncEnable = true;
//                asyncUrl = scope.bindData.$url;
//            }
            
            var setting = {
                async: {
                    enable: true,
                    url: scope.bindData.$url,
                    autoParam: scope.autoParam,
                    otherParam: scope.otherParam,
                    type: 'post',
                    dataType:"text",
                    dataFilter: function (treeId, parentNode, responseData) {
                        var params = {treeId: treeId, parentNode: parentNode, responseData: responseData};
                        return UIDirectiveService.invoke(attrs.filter, scope.filter, params, responseData);
                    }
                },
                callback: {
                    beforeAsync: function (treeId, treeNode) {
                        var params = {treeId: treeId, treeNode: treeNode};
                        return UIDirectiveService.invoke(attrs.beforeAsync, scope.beforeAsync, params, true);
                    },
                    beforeCheck: function (treeId, treeNode) {
                        var params = {treeId: treeId, treeNode: treeNode};
                        return UIDirectiveService.invoke(attrs.beforeCheck, scope.beforeCheck, params, true);
                    },
                    beforeClick: function (treeId, treeNode, clickFlag) {
                        var params = {treeId: treeId, treeNode: treeNode, clickFlag: clickFlag};
                        return UIDirectiveService.invoke(attrs.beforeClick, scope.beforeClick, params, true);
                    },
                    beforeDblClick: function (treeId, treeNode) {
                        var params = {treeId: treeId, treeNode: treeNode};
                        return UIDirectiveService.invoke(attrs.beforeDblClick, scope.beforeDblClick, params, true);
                    },
                    //用于捕获节点被拖拽之前的事件回调函数
                    beforeDrag: function (treeId, treeNodes) {
                        var params = {treeId: treeId, treeNodes: treeNodes};
                        return UIDirectiveService.invoke(attrs.beforeDrag, scope.beforeDrag, params, true);
                    },
                    //用于捕获节点拖拽操作结束之前的事件回调函数，并且根据返回值确定是否允许此拖拽操作
                    beforeDrop: function (treeId, treeNodes, targetNode, moveType, isCopy) {
                        var params = {treeId: treeId, treeNodes: treeNodes, targetNode: targetNode, moveType: moveType, isCopy: isCopy};
                        return UIDirectiveService.invoke(attrs.beforeDrop, scope.beforeDrop, params, true);
                    },
                    //用于捕获节点被删除之前的事件回调函数，并且根据返回值确定是否允许删除操作
                    beforeRemove: function (treeId, treeNode) {
                        var params = {treeId: treeId, treeNode: treeNode};
                        return UIDirectiveService.invoke(attrs.beforeRemove, scope.beforeRemove, params, true);
                    },
                    beforeRename: function(treeId, treeNode, newName, isCancel){
                    	var params = {treeId: treeId, treeNode: treeNode,newName:newName,isCancel:isCancel};
                        return UIDirectiveService.invoke(attrs.beforeRename, scope.beforeRename, params, true);
                    },
                    //用于捕获 鼠标右键点击之前的事件回调函数，并且根据返回值确定触发 onRightClick 事件回调函数
                    beforeRightClick: function (treeId, treeNode) {
                        var params = {treeId: treeId, treeNode: treeNode};
                        return UIDirectiveService.invoke(attrs.beforeRightClick, scope.beforeRightClick, params, true);
                    },
                    onAsyncError: function (event, treeId, treeNode, XMLHttpRequest, textStatus, errorThrown) {
                        scope.onAsyncError({event: event, treeId: treeId, treeNode: treeNode, XMLHttpRequest: XMLHttpRequest, textStatus: textStatus, errorThrown: errorThrown});
                    },
                	onAsyncSuccess:function(event, treeId, treeNode, msg){
                		var ztree = controller.getZtree();
                		if(scope.expendAll){
                			var expendTreeNodes = angular.isUndefined(treeNode)||treeNode==null?ztree.getNodes():treeNode.children;
                			angular.forEach(expendTreeNodes,function(node,n){
                				if(node.isParent)ztree.expandNode(node,true);
                			});
                		}else{
                			if (scope.expendLevel&&scope.expendLevel>0) {
                				var expendTreeNodes = angular.isUndefined(treeNode)||treeNode==null?ztree.getNodes():treeNode.children;
                				angular.forEach(expendTreeNodes,function(node,n){
                    				if(node.isParent&&node.level < scope.expendLevel)ztree.expandNode(node,true);
                    			});
                    		}
                		}
                		
                		if(angular.isDefined(controller.treeStateKeep))controller.treeStateKeep();
                		
                		scope.onAsyncSuccess({event:event,treeId:treeId,treeNode:treeNode,msg:msg});
                	},
                    onCheck: function (event, treeId, treeNode) {
                        scope.nodeCheck({event: event, treeId: treeId, treeNode: treeNode});
                    },
                    onClick: function (event, treeId, treeNode, clickFlag) {
                        scope.nodeClick({event: event, treeId: treeId, treeNode: treeNode, clickFlag:clickFlag});
                    },
                    onDblClick: function (event, treeId, treeNode) {
                        scope.nodeDblClick({event: event, treeId: treeId, treeNode: treeNode});
                    },
                    onRightClick: function (event, treeId, treeNode) {
                        scope.nodeRightClick({event: event, treeId: treeId, treeNode: treeNode});
                    },
                    onDrag: function (event, treeId, treeNodes) {
                        scope.nodeDrag({event: event, treeId: treeId, treeNodes: treeNodes});
                    },
                    onDragMove: function (event, treeId, treeNodes) {
                        scope.nodeDragMove({event: event, treeId: treeId, treeNodes: treeNodes});
                    },
                    onDrop: function (event, treeId, treeNodes, targetNode, moveType, isCopy) {
                        scope.nodeDrop({event: event, treeId: treeId, treeNodes: treeNodes, targetNode: targetNode, moveType: moveType, isCopy: isCopy});
                    },
                    onRemove: function (event, treeId, treeNode) {
                        scope.nodeRemove({event: event, treeId: treeId, treeNode: treeNode});
                    },
                    onRename: function (event, treeId, treeNode, isCancel) {
                        scope.nodeRename({event: event, treeId: treeId, treeNode: treeNode, isCancel: isCancel});
                    }
//                    onExpand: function (event, treeId, treeNode) {
                        //只需要简单数据模型进行model同步，普通模型在修改模型时，自动同步所有树的结构
//                        if (!asyncEnable) {
//                            var treeModelNode = UIDirectiveService.getTreeSimpleDataModelNode(scope.bindData, "id", treeNode.id);
//                            treeModelNode = true;
//                        }
//                    },
//                    onCollapse: function (event, treeId, treeNode) {
//                        if (!asyncEnable) {
//                            var treeModelNode = UIDirectiveService.getTreeSimpleDataModelNode(scope.bindData, "id", treeNode.id);
//                            treeModelNode = false;
//                        }
//                    }
                },
                data: {
                	keep:{
                		leaf:false,
                		parent:false
                	},
                    key: {
                        children: "children",
                        name: attrs.nodeName,
                        title: attrs.nodeTitle,
                        checked:"checked",
                        url:"url"
                    },
                    simpleData: {
                        enable: "true",
                        idKey: "id",
                        pIdKey: "pid",
                        rootPId: null //用于修正根节点父节点数据，即 pIdKey 指定的属性值
                    }
                },
                view: {
                	nameIsHTML:scope.nameIsHtml,
                    selectedMulti: scope.selectedMulti,
                    showIcon: scope.showIcon,
                    showTitle: scope.showTitle,
                    //搜索树的样式
                    fontCss: function (treeId, treeNode) {
                        return (!!treeNode.highlight) ? {color: "#A60000", "font-weight": "bold"} : {color: "#333", "font-weight": "normal","font-family":"微软雅黑, STHei, 华文黑体"};
                    }
                },
                check: {
                	autoCheckTrigger: scope.autoCheckTrigger,
                	chkboxType: scope.chkboxType,
                    enable: scope.checkEnable,
                    chkStyle: attrs.checkStyle,
                    radioType: "all",
                	nocheckInherit:false,
                	chkDisabledInherit:false
                },
                edit: {
                    enable: scope.editorEnable,
                    drag: {
                    	prev:function(treeId, treeNodes, targetNode){
                    		var params = {treeId: treeId, treeNodes: treeNodes, targetNode:targetNode};
                            return UIDirectiveService.invoke(attrs.dragMoveable, scope.dragMoveable, params, true);
                    	},
                    	next:function(treeId, treeNodes, targetNode){
                    		var params = {treeId: treeId, treeNodes: treeNodes, targetNode:targetNode};
                            return UIDirectiveService.invoke(attrs.dragMoveable, scope.dragMoveable, params, true);
                    	},
                    	inner:function(treeId, treeNodes, targetNode){
                    		var params = {treeId: treeId, treeNodes: treeNodes, targetNode:targetNode};
                            return UIDirectiveService.invoke(attrs.dragInner, scope.dragInner, params, true);
                    	},
                        isMove: scope.dragMove,
                        isCopy: scope.dragCopy
                    },
                    showRemoveBtn: scope.showRemoveBtn,
                    showRenameBtn: scope.showRenameBtn
                }
            };
            
            //初始化zTree之前事件，自定义修改zTree配置
            scope.beforeInit({zTreeOptions:setting});
//            console.log(setting);
            scope.options = setting;
            
            var ztree=undefined;
            
            //同步加载js
            ResInjector.addJsSync('ext/widget/tree/lib/jquery.ztree.all.min.js','ext/widget/tree/lib/jquery.ztree.exhide-3.5.min.js');
            //初始化树控件
            if (setting.async.enable) {
            	ztree=$.fn.zTree.init(scope.treeDom, setting);
            } else {
            	ztree=$.fn.zTree.init(scope.treeDom, setting, scope.bindData);
            }
            controller.initController(ztree);
            
            //树节点搜索功能
            if (scope.searchEnable) {
            	var treeObj = controller.getZtree();
            	var searchHide = scope.searchHide;
            	if(angular.isUndefined(searchHide))searchHide=true;
                var nodeList = [];
                scope.$parent.$watch(attrs.treeId+"_searchStr", function (value,old) {
                    if (value === undefined)return;
                    var allNodes = treeObj.transformToArray(treeObj.getNodes());
                    nodeList = treeObj.getNodesByParamFuzzy(attrs.nodeName, value);
                    lightNodes(allNodes, false);
                    if (value != ""){
                    	if(searchHide){
                    		treeObj.hideNodes(allNodes);
                    		var shouldShowNodes = [];
                    		angular.forEach(nodeList,function(node,n){
                    			expandNodeCascade(node);
                    			node.highlight = true;
                    			treeObj.updateNode(node,true);
                    			nodeParents(shouldShowNodes,node);
                    		});
                    		treeObj.showNodes(shouldShowNodes.concat(nodeList));
                    	}else{
                    		lightNodes(nodeList, true);
                    	}
                    }else{
                    	if(searchHide)treeObj.showNodes(allNodes);
                    }
                    //高亮显示节点
                    function lightNodes(searchNodes, highlight) {
                        for (var i = 0, l = searchNodes.length; i < l; i++) {
                            searchNodes[i].highlight = highlight;
                            treeObj.updateNode(searchNodes[i],highlight);
                            if(highlight){
                            	expandNodeCascade(searchNodes[i]);
                            }
                        }
                    }
                    //展开节点
                    function expandNodeCascade(node){
                    	var pn = node.getParentNode();
                        if(pn){
                        	if(!pn.open){
                        		treeObj.expandNode(pn,true);
                        	}else{
                        		expandNodeCascade(pn);
                        	}
                        }
                    }
                    //节点的所有父节点
                    function nodeParents(nodeArray,node){
                    	var parentNode = node.getParentNode();
                    	if(parentNode!=null){
                    		nodeArray.push(parentNode);
                    		nodeParents(nodeArray,parentNode);
                    	}
                    }
                });
            }
        }
    };
}]);
