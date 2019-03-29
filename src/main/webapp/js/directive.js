var directives = angular.module('directives', [ 'services', 'ngFileUpload']);
// 一些常用全局Dom元素基础功能的指令，比如在html等元素上启用nicescroller，在btn样式上启用Wave等等
directives.directive('html', [ 'nicescrollService', function(nicescrollService) {
	return {
		restrict : 'E',
		link : function(scope, element) {
			if (!element.hasClass('ismobile')) {
				if (!$('.login-content')[0]) {
					nicescrollService.niceScroll(element, 'rgba(0,0,0,0.3)', '5px');
			}
			}
		}
	}
} ]).directive('tableResponsive', [ 'nicescrollService', function(nicescrollService) {
	return {
		restrict : 'C',
		link : function(scope, element) {
			if (!$('html').hasClass('ismobile')) {
				nicescrollService.niceScroll(element, 'rgba(0,0,0,0.3)', '5px');
			}
		}
	}
} ]).directive('chosenResults', [ 'nicescrollService', function(nicescrollService) {
	return {
		restrict : 'C',
		link : function(scope, element) {
			if (!$('html').hasClass('ismobile')) {
				nicescrollService.niceScroll(element, 'rgba(0,0,0,0.3)', '5px');
			}
		}
	}
} ]).directive('tabNav', [ 'nicescrollService', function(nicescrollService) {
	return {
		restrict : 'C',
		link : function(scope, element) {
			if (!$('html').hasClass('ismobile')) {
				nicescrollService.niceScroll(element, 'rgba(0,0,0,0.3)', '1px');
			}
		}
	}
} ]).directive('cOverflow', [ 'nicescrollService', function(nicescrollService) {
	return {
		restrict : 'C',
		link : function(scope, element) {
			nicescrollService.niceScroll(element, 'rgba(0,0,0,0.4)', '5px');
		}
	}
} ]).directive('btn', function() {
	return {
		restrict : 'C',
		link : function(scope, element) {
			// Waves.attach(element);
			// Waves.init();
		}
	}
}).directive('btnWave', function() {
	return {
		restrict : 'C',
		link : function(scope, element) {
			// Waves.attach(element);
			// Waves.init();
		}
	}
});
// 布局类功能型指令
directives.directive('changeLayout', function() {
	return {
		restrict : 'A',
		scope : {
			changeLayout : '='
		},
		link : function(scope, element, attr) {
			// Default State
			if (scope.changeLayout === '1') {
				element.prop('checked', true);
			}
			// Change State
			element.on('change', function() {
				if (element.is(':checked')) {
					localStorage.setItem('ma-layout-status', 1);
					scope.$apply(function() {
						scope.changeLayout = '1';
					})
				} else {
					localStorage.setItem('ma-layout-status', 0);
					scope.$apply(function() {
						scope.changeLayout = '0';
					})
				}
			});
		}
	}
}).directive('toggleSidebar', function() {
	return {
		restrict : 'A',
		scope : {
			modelLeft : '=',
			modelRight : '='
		},
		link : function(scope, element, attr) {
			element.on('click', function() {
				if (element.data('target') === 'mainmenu') {
					if (scope.modelLeft === false) {
						scope.$apply(function() {
							scope.modelLeft = true;
						})
					} else {
						scope.$apply(function() {
							scope.modelLeft = false;
						})
					}
				}
				if (element.data('target') === 'chat') {
					if (scope.modelRight === false) {
						scope.$apply(function() {
							scope.modelRight = true;
						})
					} else {
						scope.$apply(function() {
							scope.modelRight = false;
						})
					}
				}
			});
		}
	}
}).directive('toggleSubmenu', [ '$timeout', function($timeout) {
	return {
		restrict : 'A',
		link : function(scope, element, attrs) {
			element.click(function() {
				element.parent().toggleClass('toggled');
				element.parent().find('ul').stop(true, false).slideToggle(200);
				$timeout(function() {
					// 展开的动画效果有200ms，必须200ms之后再计算滚动条
					element.parents('.c-overflow').getNiceScroll().resize();
				}, 205);
			});
		}
	}
} ]).directive('stopPropagate', function() {
	return {
		restrict : 'C',
		link : function(scope, element) {
			element.on('click', function(event) {
				event.stopPropagation();
			});
		}
	}
}).directive('aPrevent', function() {
	return {
		restrict : 'C',
		link : function(scope, element) {
			element.on('click', function(event) {
				event.preventDefault();
			});
		}
	}
});
// 子状态侧栏容器，如果激活的子状态要在侧边栏显示，那么对于的aside的元素必须带上该指令，才能实现按需滑入滑出
directives.directive('stateAsideContainer', ['$state', 'animateQueue','$timeout', function($state, animateQueue, $timeout) {
    return {
      restrict: 'A',
      scope: true,
      controller: function($scope, $element, $attrs) {
        var _this = this;
        var clickHandler = function(e) {
          //如果有弹出框，则不处理
          if($('.modal-backdrop.fade.in').length>0) return;

          //如果有图片放大，则不处理
          if($('.lg-backdrop').length>0) return;

          // 在chrome下面，在select上的点击事件鼠标位置都是0
          // 有时候只有X等于0...什么情况
          if (e.pageX == 0 || e.pageY == 0) return;
          var offset = $element.offset();
          var offsetX = e.pageX - offset.left;
          var offsetY = e.pageY - offset.top;
          if (offsetX > 0 && offsetY > 0) {
            return;
          } else {
            _this.close();
          }
        };
        this.open = function() {
          $('body').off('click', clickHandler);
          $('body').on('click', clickHandler);
          animateQueue.push(function() {
            $element.addClass('toggled');
          });
        };
        this.close = function() {
          var currState=$state.current;
          var params=$state.params;
          $state.go('^');
          animateQueue.push(function() {
            $element.removeClass('toggled');
          });
          $('body').off('click', clickHandler);
          $timeout(function(){
        	  $scope.$emit('aside.closed',{'state':currState,'params':params});
          },300);
          $scope.$apply();
        };
        $scope.stateOnload = function() {
          $timeout(function(){
            // 每次ui-view更新都会触发stateOnload，只有当ui-view内部加载到了内容时，才打开面板
            if ($element.find('ui-view').children().length == 0) {
              return;
            }
            _this.open();
          },300);
        };
      }
    }
  }
]);
//全局搜索侧滑指令
directives.directive('searchAsideContainer', ['animateQueue','$timeout', function(animateQueue, $timeout) {
	return {
		restrict: 'A',
//		scope: true,
		controller: function($scope, $element, $attrs) {
			var _this = this;
			var clickHandler = function(e) {
				//如果有弹出框，则不处理
				if($('.modal-backdrop.fade.in').length>0) return;
				//如果有图片放大，则不处理
				if($('.lg-backdrop').length>0) return;
				// 在chrome下面，在select上的点击事件鼠标位置都是0
				// 有时候只有X等于0...什么情况
				if (e.pageX == 0 || e.pageY == 0) return;
				var offset = $element.offset();
				var offsetX = e.pageX - offset.left;
				var offsetY = e.pageY - offset.top;
				if (offsetX > 0 && offsetY > 0) {
					return;
				} else {
					_this.close();
				}
			};
			this.open = function() {
				$('body').off('click', clickHandler);
				$('body').on('click', clickHandler);
				animateQueue.push(function() {
					$element.addClass('toggled');
				});
			};
			this.close = function() {
				animateQueue.push(function() {
					$element.removeClass('toggled');
				});
				$('body').off('click', clickHandler);
				
				$scope.$apply(function(){
					$scope.$parent.detailUrl = "";
					$scope.$parent.activeId = null;
				});
			};
			$scope.stateOnload = function() {
				$timeout(function(){
					// 每次ui-view更新都会触发stateOnload，只有当ui-view内部加载到了内容时，才打开面板
					if ($element.find('.detailContent').children().length == 0) {
						return;
					}
					_this.open();
				},300);
			};
		}
	}
}
]);
directives.directive('c2Dropdown', ['$compile', "$timeout", "$ocLazyLoad", "nicescrollService","$filter",
    function($compile, $timeout, $ocLazyLoad, nicescrollService,$filter) {
      return {
        scope: {
          bindDataId: "=?",
          bindData: '=?',
          autoHide: "=?",
          direction: "@?",
          multiple: "=?",
          disabled:"=?",
          items: "=",
          itemClick: "&",
          itemName: "@?",
          itemId: "@?",
          treeOptions: "=",
          treeNodes: "=",
          cleanButton: "=?",
		  		afterCleanEvent: "&",
          lastItemButton:"&",
          filter:"=?",
          dropdownFilterValue:"=?",
          filterPinyin:"=?",
          filterProperty:"@?",
          filterOnly:"=?",
          filterPlaceholder:"@?",
          maxHeight: "=?"
        },
        restrict: "E",
        controller: function($scope, $element, $attrs, $transclude) {
        	var controller = this;
			// 触发下拉的元素,通过.dropdown-toggle标记，如果没有则默认第一个子元素
			$scope.toggleElement = $element.find(".dropdown-toggle");
			if ($scope.toggleElement.length == 0) {
			  $scope.toggleElement = $element.children().first();
			}
			function onBodyDown(event) {
				// 单点击元素为下拉框或者为触发下拉框元素时，不进行处理（触发下拉框元素本身已经有了toggle事件）
				if (!($(event.target).hasClass("dropdown-menu") || $(event.target).parents(".dropdown-menu").length > 0 || $(event.target)[0] == $scope.toggleElement[0] || $(event.target)
						.parent()[0] == $scope.toggleElement[0])) {
					controller.toggle();
				}
			}
			this.openState = false;
			this.getBindDataId = function(){
				return $scope.bindDataId;
			}
			this.getBindData = function(){
				return $scope.bindData;
			}
			this.getTree = function() {
				return $scope.ztree;
			}
			this.getDropdownItems = function() {
				return $scope.dropdownItems;
			}
			this.toggle = function(open) {
				if(angular.isUndefined(open)) controller.openState = !controller.openState;
				else{
					if(controller.openState === open) return ;
					else controller.openState = open;
				}
				if (controller.openState) {
					$element.toggleClass("open");
					$("body").bind("mousedown", onBodyDown);
					$timeout(function() {//等动画完成之后再加载滚动条
						nicescrollService.niceScroll($scope.dropdownMenuEle, 'rgba(0,0,0,0.3)', '5px');
						$scope.dropdownMenuEle.getNiceScroll(0).doScrollTop(0,30);
					}, 200);
				} else {
					$scope.dropdownMenuEle.getNiceScroll().remove();
					$timeout(function() {//等滚动条消失后在开始收拢动画
						$element.toggleClass("open");
						$("body").unbind("mousedown", onBodyDown);
						if($scope.filter){
							$timeout(function(){//等动画完成之后再清空条件和排序
								if(!$attrs.dropdownFilterValue)$scope.dropdownFilterValue = undefined;
								//选中的排前面
								/*$scope.dropdownItems.sort(function(a,b){
									if(a.selected!=b.selected){
										if(a.selected)return -1;
										else return 1;
									}
								});*/
							},200);
						}
					}, 10);
				}
			}
			this.clean = function() {
				$scope.bindData = $scope.multiple ? [] : null;
				$scope.bindDataId = null;
				if ($scope.autoHide)
					controller.toggle();
				$scope.afterCleanEvent();
			}
		},
		link : function(scope, element, attrs, controller) {
			scope.itemsNoWatch = false;
			element.addClass("dropdown");
			scope.$watch("disabled",function(v){
				v?scope.toggleElement.unbind("click"):scope.toggleElement.bind("click",controller.toggle);
				v?element.addClass("disabled"):element.removeClass("disabled");
			});

			if (angular.isDefined(scope.maxHeight))
				scope.menuStyle = {
					"max-height" : scope.maxHeight + "px",
					"overflow" : "hidden"
				};
			scope.clean = function() {
				controller.clean();
			}
			// 是否为树
			if (angular.isDefined(attrs.treeOptions)) {
				var dropdownTreeId = angular.isDefined(attrs.id)?attrs.id+"Tree":"dropdownTree";
				var dropdownMenu = $("<div class='dropdown-menu' ng-style='menuStyle' ng-class='{\"pull-right\":direction==\"right\"}'><ul id='"+dropdownTreeId+"' class='ztree'></ul></div>");
				scope.toggleElement.after(dropdownMenu);
				if(scope.cleanButton)dropdownMenu.append('<button ng-click="clean()" class="btn btn-block btn-info"><i class="md md-clear"></i>清空</button>');
				$compile(dropdownMenu)(scope);
				scope.dropdownMenuEle = dropdownMenu;
				var promise = $ocLazyLoad.load([ 'assets/ztree/jquery.ztree.all.min.js', 'assets/ztree/zTreeStyle.css' ]);
				promise.then(function() {
					if (angular.isDefined(attrs.treeNodes)) {
						scope.$watch("treeNodes", function(v) {
							if (angular.isUndefined(v))
								return;
							scope.ztree = $.fn.zTree.init(dropdownMenu.find(".ztree").first(), scope.treeOptions, scope.treeNodes);
						});
					} else {
						scope.ztree = $.fn.zTree.init(dropdownMenu, scope.treeOptions);
					}
				});
				// 如果为树，不再继续执行
				return;
			}
			if (angular.isDefined(attrs.bindData))scope.bindType = "bindData";
			if (angular.isDefined(attrs.bindDataId))scope.bindType = "bindDataId";

			// 默认显示属性为"value",多选为false,点击选项自动隐藏选项框为false
			if (angular.isUndefined(scope.itemName))
				scope.itemName = "value";
			if (angular.isUndefined(scope.itemId))
				scope.itemId = scope.itemName;
			// 当多选为true时，默认自动隐藏为true
			if (angular.isUndefined(scope.multiple) || (typeof scope.multiple) != "boolean")
				scope.multiple = false;
			else {
				if (scope.multiple && angular.isUndefined(scope.autoHide))
					scope.autoHide = false;
			}
			if (angular.isUndefined(scope.autoHide) || (typeof scope.autoHide) != "boolean")
				scope.autoHide = true;
			if (angular.isUndefined(scope.cleanButton) || (typeof scope.cleanButton) != "boolean")
				scope.cleanButton = false;

			if (angular.isUndefined(scope.filter) || (typeof scope.filter) != "boolean")
				scope.filter = false;
			if (angular.isUndefined(scope.filterPinyin) || (typeof scope.filterPinyin) != "boolean")
				scope.filterPinyin = true;
			if (angular.isUndefined(scope.filterProperty)){
				scope.$filterPropertyArray = [scope.itemName];
				if(scope.filter&&scope.filterPinyin)scope.$filterPropertyArray = scope.$filterPropertyArray.concat("pinyinFull","pinyinCamel");
			}else scope.$filterPropertyArray = scope.filterProperty.split(",");
			if (angular.isUndefined(scope.filterOnly) || (typeof scope.filterOnly) != "boolean")
				scope.filterOnly = false;
			if (angular.isUndefined(scope.filterPlaceholder))
				scope.filterPlaceholder = "搜索...";

			//分组的选项，暂时只认 'group'
			function groupAndIndexItems(optionItems){
				var groupItems = new Array();
//				var orderGroupItems = $filter('orderBy')(optionItems,'group');
				var headItem,groupTemp="",dropdownItemIdex=0,dropdownOptionId=0;
				angular.forEach(optionItems,function(v,n){
					if(angular.isUndefined(v.group)){
						v.dropdownOptionId = dropdownOptionId++;
						groupItems.push(v);
						return ;
					}
					if(groupTemp!=v.group){
						headItem = {isDropdownHead:true,dropdownHeadChildren:[],dropdownItemIdex:dropdownItemIdex++};
						headItem[scope.itemName] = v.group
						groupItems.push(headItem);
					}
					v.dropdownItemIdex = dropdownItemIdex++;
					v.dropdownOptionId = dropdownOptionId++;
					v.isDropdownSon = true;
					headItem.dropdownHeadChildren.push(v);
					groupItems.push(v);
					groupTemp = v.group;
				});
				return groupItems;
			}

			//选中选项应用到数据绑定
			function applyBind(){
				scope.bindDataNoWatch = true;
				scope.bindDataIdNoWatch = true;
				var bindValue = scope.multiple?[]:null;
				var bindIdValue = scope.multiple?[]:null;
//				scope.bindData = scope.multiple?[]:null;
				angular.forEach(scope.dropdownItems,function(v,n){
					if (scope.multiple) {
						if(v.selected){
							bindValue.push(scope.items[v.dropdownOptionId]);
							bindIdValue.push(v[scope.itemId]);
						}
					}else{
						if(v.selected){
							bindValue = scope.items[v.dropdownOptionId];
							bindIdValue = v[scope.itemId];
						}
					}
				});
				scope.bindData = bindValue;
				scope.bindDataId = scope.multiple?bindIdValue.join(","):bindIdValue;
			}
			//数据绑定值应用到选项勾选
			function applyItemCheck(bindType){
				if(angular.isUndefined(scope.dropdownItems) || scope.dropdownItems.length==0) return ;
				if(angular.isUndefined(bindType)) return ;
//				scope.multiple?initItemMultipleSelected(scope.bindData):initItemSelected(scope.bindData);
				// scope.itemsNoWatch = true;
//				scope.bindDataNoWatch = true;
//				scope.bindDataIdNoWatch = true;
				if(scope.multiple){
					if(bindType =='bindData')scope.bindDataId="";
					if(bindType =='bindDataId')scope.bindData=[];
				}
				angular.forEach(scope.dropdownItems, function(item, n) {
					if(scope.multiple){
						item.selected = false;
						if(bindType=='bindData'){
							angular.forEach(scope.bindData, function(value, i) {
								if (item[scope.itemId] == value[scope.itemId]) {
									item.selected = true;
									angular.extend(value,scope.items[item.dropdownOptionId]);
									if(scope.bindDataId=="")scope.bindDataId = item[scope.itemId];
									else scope.bindDataId += ","+item[scope.itemId];
								}
							});
						}
						if(bindType=='bindDataId'){
							if(angular.isUndefined(scope.bindDataId)) return ;
							var bindIdArray = scope.bindDataId.split(",");
							angular.forEach(bindIdArray, function(value, i) {
								if (item[scope.itemId] == value) {
									item.selected = true;
									scope.bindData.push(scope.items[item.dropdownOptionId]);
								}
							});
						}
					}else{
						var onlySelected = false;
						if(bindType=='bindData'){
							onlySelected=scope.bindData && item[scope.itemId] == scope.bindData[scope.itemId];
							if(onlySelected){
								scope.bindDataId=item[scope.itemId];
								scope.bindData = angular.extend(scope.bindData,scope.items[item.dropdownOptionId]);
							}
						}
						if(bindType=='bindDataId'){
							onlySelected=scope.bindDataId && item[scope.itemId] == scope.bindDataId;
							if(onlySelected)scope.bindData = scope.items[item.dropdownOptionId];
						}

						item.selected = onlySelected;
					}
				});
			}

			scope.$watch("items", function(optionItems) {
				if (angular.isUndefined(optionItems)){
					scope.dropdownItems = [];
					return;
				}

				try{
					if(!angular.isArray(optionItems)) throw "dropdown指令items属性值不是数组";
				}catch(err){
					console.error(err);
					return ;
				}

//				if(angular.isArray(optionItems)&&optionItems.length==0)return ;

				if(scope.itemsNoWatch){
					scope.itemsNoWatch = false;
					return ;
				}

				var dropdownItems = angular.copy(optionItems);
				scope.dropdownItems = [];
				if(scope.filter&&scope.filterPinyin){//开启前端拼音搜索
					var promise = $ocLazyLoad.load(['assets/js/pinyin/MooTools-Core-1.5.2.js', 'assets/js/pinyin/pinyin.js' ],{serie:true});
					promise.then(function() {
						var pinyin = new Pinyin();
						if (angular.isArray(dropdownItems)) {
							angular.forEach(dropdownItems, function(v, i) {
								if(angular.isUndefined(v[scope.itemName]))console.error("c2Dropdown Error","item-name:"+scope.itemName+"对象属性中不存在");
								else{
									v.pinyinFull = pinyin.getFullChars(v[scope.itemName].toString());
									v.pinyinCamel = pinyin.getCamelChars(v[scope.itemName].toString());
								}
							});
							dropdownItems = groupAndIndexItems(dropdownItems);
							scope.dropdownItems = dropdownItems;
						} else {
							var dropdownOptionId = 0;
							scope.itemName = "value";
							angular.forEach(dropdownItems, function(v, k) {
								scope.dropdownItems.push({
									key : k,
									value : v,
									pinyinFull:pinyin.getFullChars(v.toString()),
									pinyinCamel:pinyin.getCamelChars(v.toString()),
									dropdownOptionId:dropdownOptionId++
								});
							});
						}
						applyItemCheck(scope.bindType);
					});
				}else{
					if (angular.isArray(dropdownItems)) {
						scope.dropdownItems = groupAndIndexItems(dropdownItems);
					} else {
						var dropdownOptionId = 0;
						scope.itemName = "value";
						angular.forEach(dropdownItems, function(v, k) {
							scope.dropdownItems.push({
								key : k,
								value : v,
								dropdownOptionId:dropdownOptionId++
							});
						});
					}
					applyItemCheck(scope.bindType);
				}

			},true);

			scope.$watch("bindData", function(nv, ov) {
				if(scope.bindDataNoWatch){
					scope.bindDataNoWatch = false;
					return ;
				}
				if (scope.multiple) {
					if (angular.isUndefined(scope.bindData))
						scope.bindData = [];
					if (!angular.isArray(scope.bindData)) {
						var tempValue = angular.copy(scope.bindData);
						scope.bindData = new Array();
						scope.bindData.push(tempValue);
					}
				}
				applyItemCheck('bindData');
			}, true);

			scope.$watch("bindDataId",function(nv,ov){
				if(scope.bindDataIdNoWatch){
					scope.bindDataIdNoWatch = false;
					return ;
				}
				applyItemCheck('bindDataId');
			});

			scope.dropdownItemClick = function(item) {
				//scope.itemsNoWatch = true;
				// 是否多选
				if (scope.multiple) {
					scope.bindDataNoWatch = true;
					scope.bindDataIdNoWatch = true;
					if(item.isDropdownHead){
						var headChildrenCheck = false
						angular.forEach(item.dropdownHeadChildren,function(v){
							if(!v.selected)headChildrenCheck=true;
						});
						angular.forEach(item.dropdownHeadChildren,function(v){
							v.selected = headChildrenCheck;
						});
					}else{
						item.selected = !item.selected;
					}
					applyBind();
				} else {
					if(item.isDropdownHead) return ;
					scope.bindData = scope.items[item.dropdownOptionId];
					scope.bindDataId = item[scope.itemId];
				}

				if (scope.autoHide){
					controller.toggle();
				}
				$timeout(function() {
					scope.itemClick({
						item : item
					});
				}, 0);
				$timeout(function() {//等动画完成之后再加载滚动条
					if(scope.dropdownMenuEle.getNiceScroll(0).hidden){
						scope.dropdownMenuEle.getNiceScroll(0).show();
						//scope.dropdownMenuEle.getNiceScroll(0).resize();
					}
				}, 200);
			}
			// 模版
			var itemTemplate = element.find("dropdown-item-template").html();
			element.find("dropdown-item-template").remove();
			if (angular.isUndefined(itemTemplate)) {
				itemTemplate = '<img class="dropdown-img" ng-if="item.icon" ng-src="{{item.icon}}">' + '<span class="text">{{item.' + scope.itemName + '}}</span>'
						+ '<span ng-if="item.icon" class="md md-check check-mark" style="margin-top: -2px;"></span>'
						+ '<span ng-if="!item.icon" class="md md-check check-mark"></span>';
			}
			scope.dropdownFilter = function(item){
				if(!scope.dropdownFilterValue)return !scope.filterOnly;
				var ye = false;
				if(item.isDropdownHead){
					angular.forEach(item.dropdownHeadChildren,function(si){
						angular.forEach(scope.$filterPropertyArray,function(p){
							if((typeof item[p] == "string")&&si[p].toLowerCase().indexOf(scope.dropdownFilterValue.toLowerCase())!=-1)ye = true;
						});
					});
				}else{
					angular.forEach(scope.$filterPropertyArray,function(p){
						if((typeof item[p] == "string")&&item[p].toLowerCase().indexOf(scope.dropdownFilterValue.toLowerCase())!=-1)ye = true;
					});
				}
				return ye;
			}
			var cleanButtonHTML = scope.cleanButton ? "<li><button ng-click='clean()' class='btn btn-block btn-info'><i class='md md-clear'/>清空</button></li>" : "";
			var lastItemButtonHtml = attrs.lastItemButton ? "<li><button ng-click='lastItemButton({e:$event})' class='btn btn-block btn-success'><i class='md md-add'/>添加标签</button></li>" : "";
			var searchInputHTML = scope.filter&&!attrs.dropdownFilterValue ? "<li class='dropdown-search'><input type='text' ng-model='dropdownFilterValue' placeholder='{{filterPlaceholder}}'></li>" : "";
			var filterHtml = scope.filter ? " | filter:dropdownFilter" : "";
			var ul = angular.element("<ul class='dropdown-menu' ng-style='menuStyle' ng-class='{\"pull-right\":direction==\"right\"}'>"
					+ searchInputHTML
					+ "<li ng-repeat='item in dropdownItems"+filterHtml+"' ng-class='{selected:item.selected,\"dropdown-header\":item.isDropdownHead,\"dropdown-son\":item.isDropdownSon}' ng-click='dropdownItemClick(item)'><span class='text' ng-if='item.isDropdownHead'>{{item." + scope.itemName + "}}</span>" + "<a ng-if='!item.isDropdownHead'>" + itemTemplate + "</a>" + "</li>"
					+ lastItemButtonHtml+cleanButtonHTML + "</ul>");
			scope.toggleElement.after(ul);
			$compile(ul)(scope);
			scope.dropdownMenuEle = ul;
			}
		}
	} ]);

directives.directive("c2Popover",['$parse',function($parse){
	return {
		link:function(scope, element, attrs,controller){
			var content = $parse(attrs.popoverContent)(scope);
			var title = $parse(attrs.popoverTitle)(scope);
			element.popover({trigger:"click",html:true,content:content,title:title});
		}
	}
}]);

directives.directive('dateTimePicker', function() {
  return {
    require: '?ngModel',
    restrict: 'A',
    scope: {
      viewMode: '@',
      format: '@',
      ignoreReadonly:'=',
      todayIcon:'@',
      clearIcon:'@',
      closeIcon:'@',
      showClear:'=',
      showTodayButton:'=',
      showClose:'=',
      nullVal:'@',
      disabled:'=pickerDisabled'
    },
    link: function(scope, element, attrs, ngModel) {
      var options = {
        viewMode: scope.viewMode,
        format: scope.format,
        ignoreReadonly: scope.ignoreReadonly,
        locale: 'zh-cn',
        icons: {
          time: 'md md-timer',
          date: 'md md-today',
          up: 'md md-expand-less',
          down: 'md md-expand-more',
          previous: 'md md-chevron-left',
          next: 'md md-chevron-right',
          today: scope.todayIcon,
          clear: scope.clearIcon,
          close: scope.closeIcon
        },
//        debug:true,
        showClear:scope.showClear,
        showTodayButton:scope.showTodayButton,
        showClose:scope.showClose,
        useCurrent:false
      };
      if (!element.is('input')) {
        options.inline = true;
      }
      ngModel.$formatters.push(function(modelValue) {
        if (angular.isUndefined(modelValue) || modelValue == null) {
          return scope.nullVal?scope.nullVal:"";
        }
        var momentdate = moment(modelValue);
        element.data("DateTimePicker").date(momentdate);
        return momentdate.format(scope.format);
      });
      ngModel.$parsers.push(function(viewValue) {
        if (angular.isUndefined(viewValue) || viewValue == "" || viewValue == null) {
        	return viewValue;
        }
        var momentdate = moment(viewValue, scope.format);
//        element.data("DateTimePicker").date(momentdate);
        return momentdate.toDate().getTime();
      });
      element.datetimepicker(options).on('dp.change', function(e) {
    	if(e.date){
    		ngModel.$setViewValue(e.date.format(scope.format));
    	}else{
    		ngModel.$setViewValue(null);
    	}
      });
      scope.$watch("disabled",function(v){
    	 if(angular.isUndefined(v)) return ;
    	 if(v)element.data("DateTimePicker").disable();
    	 else element.data("DateTimePicker").enable();
      });
    }
  }
});
directives.directive('fgLine', function() {
return {
	restrict : 'C',
link : function(scope, element) {
	if ($('.fg-line')[0]) {
$('body').on('focus', '.form-control', function() {
$(this).closest('.fg-line').addClass('fg-toggled');
})
$('body').on('blur', '.form-control', function() {
var p = $(this).closest('.form-group');
var i = p.find('.form-control').val();
if (p.hasClass('fg-float')) {
	if (i.length == 0) {
		$(this).closest('.fg-line').removeClass('fg-toggled');
	}
} else {
	$(this).closest('.fg-line').removeClass('fg-toggled');
					}
				});
			}
		}
	}
});
directives
		.directive('c2MarkdownEditor',['$timeout',
	'Modal',
	'$ocLazyLoad',
	'$compile',
	'Upload',
	'Messenger',
	'nicescrollService',
	function($timeout, Modal, $ocLazyLoad, $compile, Upload, Messenger,nicescrollService) {
		return {
			restrict : 'A',
			require : [ 'c2MarkdownEditor', 'ngModel' ],
			scope: {
				markdownAutoFocus:'='
			},
			controller : function($scope, $element, $attrs,$ocLazyLoad) {
				var _this = this;
				function configNgModelLink(codemirror, ngModel, scope) {
					// 通过ngModel绑定作用域内变量和codemirror的内容
					if (!ngModel) {
						return;
					}
					ngModel.$formatters.push(function(value) {
						if (angular.isUndefined(value) || value === null) {
							return '';
						} else if (angular.isObject(value) || angular.isArray(value)) {
							throw new Error('ui-codemirror cannot use an object or an array as a model');
						}
						return value;
					});
					ngModel.$render = function() {
						codemirror.operation(function(){
							var safeViewValue = ngModel.$viewValue || '';
							codemirror.setValue(safeViewValue);
							return true;
						});
					};
					codemirror.on('change', function(instance) {
						var newValue = instance.getValue();
						if (newValue !== ngModel.$viewValue) {
							scope.$evalAsync(function() {
								ngModel.$setViewValue(newValue);
							});
						}
					});
				}
				;

				this.init = function(editor, ngModelCtrl) {
					_this.editor = editor;
					configNgModelLink(editor.codemirror, ngModelCtrl, $scope);
				}
				this.focus=function(){
					_this.editor.codemirror.focus();
				}
				this.refresh = function(wait){
					if(angular.isUndefined(wait))wait=0;
         			$timeout(function(){_this.editor.codemirror.refresh();},wait);
				}
				
				$scope.fileSelected = function($files, $file, $event, $rejectedFiles, type) {
					if (!$file && $rejectedFiles.length > 0) {
						Messenger.error('不允许上传大于500M的文件');
						return;
					}
					if (!$file)
						return;

			      	var uploadFile=function(fileObj){
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
						   var RexStr = /\(|\)/g  
				      	   var url = data.url.replace(RexStr, function(MatchStr) {  
				      	        switch (MatchStr) {  
				      	        case "(":  
				      	            return "%28";  
				      	        case ")":  
				      	            return "%29"; 
				      	        default:  
				      	            break;  
				      	        }  
				      	    }) ;
							var value = linkPrefix + data.name + '](' + url + ')\n\n';
							_this.editor.codemirror.replaceSelection(value);
							notify.update('message', '上传成功!');
						}).error(function(data, status, headers, config) {
							notify.update('type', 'danger');
							notify.update('message', '上传失败，错误信息：' + data.message);
						});
			    	}

			    	var dataURItoBlob=function(dataURI) {
			    	    // convert base64/URLEncoded data component to raw binary data
						// held in a string
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

			    	var linkPrefix = (type === "images" ? "![" : "[");

			    	if(type === "images"){
			    		 //https://github.com/think2011/localResizeIMG4/wiki
			    		 //图片压缩质量
					    lrz($file,{quality:0.7}).then(function (rst){
							var blobFile=dataURItoBlob(rst.base64);
							blobFile.lastModifiedDate=new Date();
							blobFile.name=rst.origin.name;
							uploadFile(blobFile);
						    // 处理成功会执行
					     }).catch(function (err) {
							uploadFile($file);
						    // 处理失败会执行
					    }).always(function () {
						  // 不管是成功失败，都会执行
					    });
			    	}else{
			    		uploadFile($file);
			    	}
				};
				$scope.chapterTreeOptions = {
						view : {
							selectedMulti : false
						},
						data : {
							key : {    title : "text",    name : "text"   },
							simpleData : {
								idKey : "id",
								pIdKey : "parentId",
								rootPId : undefined,
								enable : true
							}
						},
						callback : {
							onClick : function(event, treeId, treeNode, clickFlag) {
								$scope.$apply(function() {
									$scope.chapterTreeSelected = treeNode.text;
								});
								var line=treeNode.line;
								// 收拢下拉框
								$("#chapterTree").controller("c2-dropdown").toggle();
								var promise = $ocLazyLoad.load(['js/markdown/searchcursor.js']);
								promise.then(function() {
									var id=treeNode.id;
									var text=treeNode.text;
									var depth=i=treeNode.depth;
									var prefix="#";
									while(i>1){
										prefix+="#";
										i--;
									}
									//^ *(#{1,6}) *([^\n]+?) *#* *(?:\n+|$)
									//拼凑查找指定章节的正则
									var p=new RegExp("^ *(#{"+depth+"}) *("+text+") *#* *(?:\n+|$)");
									var sc=_this.editor.codemirror.getSearchCursor(p);
									var posArr=[];
									while(sc.findNext()){
										posArr.push(sc.from());
									}
									if(posArr.length>1){//如果找到多个一样的章节，根据treeNode的位置进行定位
										var arr=$.grep($scope.tokens,function(o,i){
											return o.id<id&&o.depth==depth&&o.text==text;
										});
										_this.editor.codemirror.doc.setCursor(posArr[arr.length]);
									}else if(posArr.length==1){
										_this.editor.codemirror.doc.setCursor(posArr[0]);
									}
								});
								_this.editor.codemirror.focus();
								// _this.editor.codemirror.doc.setCursor(line,0);
								// event.preventDefault();
							}
						}
					};
				$scope.navChapter = function($event) {
					var options = {};
					var text = _this.editor.value();
					if (!text)
						return;
					var tokens = marked.lexer(text);
					tokens=$.grep(tokens,function(o,i){
							return o.type=='heading';
						}
					);
					$(tokens).each(function(i, o) {
							o.id = i;
							if (o.depth == 1) {
								o.parent = undefined;
								o.parentId = undefined;
							}

							if (i != tokens.length - 1) {
								if (tokens[i + 1].depth > o.depth) {
									tokens[i + 1].parent = o;
								} else if (tokens[i + 1].depth < o.depth) {
									tokens[i + 1].parent = o.parent ? o.parent.parent : undefined;
								} else {
									tokens[i + 1].parent = o.parent;
								}
								tokens[i + 1].parentId = tokens[i + 1].parent?tokens[i + 1].parent.id:undefined;
							}
					});
					$scope.tokens = tokens;
					// console.log(tokens);

				};

				$scope.showMarkdownHelp=function(){
					Modal.open('markdown-help.html',{},{title:"Markdown语法5分钟入门",size:"big"});
				}
			},
			link : function(scope, element, attrs, ctrls) {
				var markdownController = ctrls[0];
				var ngModel = ctrls[1];
				var imageFileExtensions = [ 'image/jpeg', 'image/png', 'image/jpg', 'image/gif' ];

				var editor;
				scope.langMenuClick=function(item){
					var code="\n```"+item+"\n\n```\n";
					editor.codemirror.replaceSelection(code);
					editor.codemirror.focus();
					var line=editor.codemirror.getCursor().line;
					editor.codemirror.setCursor(line-2,0);
				};
				scope.config={
						percentageHook:function percentage(element, referenceWindow) {
							  if (isVisibleByStyling(element)) {
								return 1;
							  }
							  return  percentage(element, referenceWindow);
							}
				}
//				scope.onEditorVisible=function(){
//					editor.codemirror.refresh();
//					var autoFocusDefined=angular.isDefined(attrs.markdownAutoFocus);
//					var autoFocusAttrValue=attrs.markdownAutoFocus;
//					if(autoFocusDefined&&(autoFocusAttrValue==""||scope.markdownAutoFocus==true)){
//						//markdown-auto-focus="" markdown-auto-focus markdown-auto-focus="true"（或表达式=true） 有焦点，其他情况无焦点
//						editor.codemirror.focus();
//						var contentWarpper=editor.codemirror.display.wrapper;
//						var per = VisSense.Utils.percentage(contentWarpper,window);
//						if(per<1){
//							var h=$(contentWarpper).height()*(1-per);
//							//alert($(window).scrollTop()+h);
//							$(window).scrollTop($(window).scrollTop()+h);
//						};
//					}
//				};
				var promise = $ocLazyLoad.load([ 'js/markdown/editor.js', 'js/markdown/highlight.min.js', 'css/markdown/xcode.css',
						'css/markdown/editor.css']);
				promise
						.then(function() {
							var toolbar = [
									{
										name : 'bold',
										title : '粗体',
										action : Editor.toggleBold,
										className : 'md md-format-bold'
									},
									{
										name : 'italic',
										title : '斜体',
										action : Editor.toggleItalic,
										className : 'md md-format-italic'
									},
									'|',
									{
										name : 'quote',
										title : '引用',
										action : Editor.toggleBlockquote,
										className : 'md md-format-quote'
									},
									{
										html :  $compile(
												"<c2-dropdown items=\"['javascript','java','xml','html','css','properties']\"\n" +
												"              item-click=\"langMenuClick(item)\">\n" +
												"            \n" +
												"              <a class=\"md md-settings-ethernet\" title='插入代码'></a>"+
												"            <dropdown-item-template> {{item}}\n" +
												"           </dropdown-item-template> </c2-dropdown>")(scope)[0]
									},
									{
										name : 'unordered-list',
										title : '无序列表',
										action : Editor.toggleUnOrderedList,
										className : 'md md-format-list-bulleted'
									},
									{
										name : 'ordered-list',
										title : '有序列表',
										action : Editor.toggleOrderedList,
										className : 'md md-format-list-numbered'
									},
									'|',
									{
										name : 'link',
										title : '添加超链接',
										action : Editor.drawLink,
										className : 'md  md-insert-link'
									},
									{
										html : $compile(
												'<a class="md md-insert-photo" ngf-select="true" accept="image/*"  ngf-max-size="524288000" ngf-change="fileSelected($files, $file, $event, $rejectedFiles,\'images\')" title="上传图片"></a>')
												(scope)[0]
									},
									{
										html : $compile(
												'<a class="md md-attach-file" ngf-select="true" ngf-max-size="524288000" ngf-change="fileSelected($files, $file, $event, $rejectedFiles)" title="上传文件"></a>')
												(scope)[0]
									},
									'|',
									{
										name : 'preview',
										title : '预览',
										action : Editor.togglePreview,
										className : 'md md-pageview'
									},
									{
										name : 'fullscreen',
										title : '全屏编辑',
										action : Editor.toggleFullScreen,
										className : 'md md-aspect-ratio'
									},
									'|',
									{
										html : $compile(
												"<c2-dropdown tree-options=\"chapterTreeOptions\"\n" + "                tree-nodes=\"tokens\"\n"
														+ "                id=\"chapterTree\">\n"
														+ "              <a class=\"md md-menu\" ng-click='navChapter()' title=\"章节导航\"></a>\n" + "              </c2-dropdown>")(scope)[0]
									},
									'|',
									{
										html : $compile('<a class="md md-help" ng-click="showMarkdownHelp()" title="Markdown编写帮助" config="config""></a>')(scope)[0]
									}];
							editor = new Editor({
								element : element[0],
								toolbar : toolbar
							});
							editor.codemirror.setSize(-1, attrs.height);
							/*
							 * editor.codemirror.on('blur',
							 * function() { if
							 * (angular.isDefined(scope.ueditorBlur))
							 * scope.ueditorBlur(); });
							 */

							markdownController.init(editor, ngModel);

							inlineAttachment.editors.codemirror3.attach(editor.codemirror, {
								uploadUrl : 'file',
								jsonFieldName : 'url',
								allowedTypes : [ 'image/jpeg', 'image/png', 'image/jpg', 'image/gif', "" ]
							});
						});
			}
		}
	} ]);

directives.directive('c2Pagination', [ '$compile', "$timeout", function($compile, $timeout) {
	return {
		scope : {
			total : '=',
			onPageChange : "&",
			pageSize : "@"
		},
		restrict : "EA",
		templateUrl : "template/pagination.html",
		controller : function($scope, $element, $attrs, $transclude) {
		},
		link : function($scope, element, attrs, controller) {
			$scope.total = $scope.total ? $scope.total : 0;
			$scope.pageIndex = $scope.pageIndex ? $scope.pageIndex : 1;
			$scope.pageSize = $scope.pageSize ? $scope.pageSize : 10;
			var calcTotPage = function() {
				var total = $scope.total;
				var pageSize = $scope.pageSize;
				var totPage = Math.ceil(total * 1.0 / pageSize);
				return totPage;
			};
			var genPages = function() {
				var pageIndex = $scope.pageIndex;
				var totPage = calcTotPage();
				var pages = [];
				var offsetPage = 2; // 当前页码居中，左右各显示多少个页码
				var startPage = pageIndex - offsetPage;
				var endPage = pageIndex + offsetPage;
				while (startPage < 1) {
					++startPage;
					endPage = endPage < totPage ? ++endPage : endPage;
				}
				while (endPage > totPage) {
					--endPage;
					startPage = startPage > 1 ? --startPage : startPage;
				}
				for ( var p = startPage; p <= endPage; p++) {
					pages.push(p);
				}
				$scope.startPage=startPage;
				$scope.endPage=endPage;
				$scope.pages = pages;
				$scope.totPage = totPage;
			};
			$scope.nextPage = function() {
				var p = $scope.pageIndex + 1;
				var totPage = calcTotPage();
				if(p > totPage){
					return;
				}
				p = p >= totPage ? totPage : p;
				$scope.pageIndex = p;
				genPages();
				$scope.onPageChange({
					pageIndex : p
				});
			};
			$scope.prevPage = function() {
				var p = $scope.pageIndex - 1;
				if(p < 1){
					return;
				}
				p = p <= 1 ? 1 : p;
				$scope.pageIndex = p;
				genPages();
				$scope.onPageChange({
					pageIndex : p
				});
			};
			$scope.goPage =  function(p) {
				$scope.pageIndex = p;
				genPages();
				$scope.onPageChange({
					pageIndex : p
				});
			};
			controller.goPage = function(p) {
				$scope.pageIndex = p;
				genPages();
			};
			$scope.$watch("total", function(v) { // alert(v);
				genPages();
			});
		}
	};
} ]);
directives.directive('nestList', [ '$compile', "$timeout", 'debounce', '$ocLazyLoad', function($compile, $timeout, debounce, $ocLazyLoad) {
	return {
		restrict : "E",
		templateUrl : function(elem, attrs) {
			return attrs.templateUrl;
		},
		scope : {
			change : "&change",
			changeStart : "&changeStart",
			controller : "=controller",
			items : "=items",
			events : "=events",
			editable: "="
		},
		replace : true,
		controllerAs : "nestList",
		controller : function($scope, $element, $attrs, $transclude) { // alert("control");
			if (angular.isDefined($attrs.controller))
				$scope.controller = this;
		},
		link : function($scope, $element, $attrs, $controller) {
			$scope.events = jQuery.extend(true, {}, $scope.events);
			$scope.events.currScope = $scope;
			$scope.nestRootScope = true;
			var init2 = $controller.init = function() {
				$element.nestable("destroy"); // tbw 重新加载前应该先重置一下列表
				var nest = $element.nestable();
				if(angular.isDefined($attrs.change)){
				nest.on("change", function(e, currentEleParentChild,currentEle) {
					$scope.change({
						currentEleParentChild : currentEleParentChild,
						currentEle:currentEle
					});
				});
				}
				if(angular.isDefined($attrs.changeStart)){
				nest.on("changeStart", function(e, dragItem) {
					$scope.changeStart({
						dragItem : dragItem
					});
				});
				}
				$controller.getNestable = function() {
					return nest;
				}

			};
			var i=0;
			$scope.$on('nestlist.item.done', function(e) {
				++i;
				var cLen=$scope.items.length;
				if(cLen==i){
					$timeout(function(){
						$ocLazyLoad.load('assets/js/jquery.nestable.min.js').then(function() {
							init2();
						})
					},500);
				}
			});
		}
	}
} ]);
directives.directive('nestChildren', [ '$compile', "$timeout", function($compile, $timeout) {
	return {
		restrict : "E",
		templateUrl : function(elem, attrs) {
			return attrs.templateUrl;
		},
		scope : {
			items : "=items",
			events : "=events",
			editable: "="
		},
		require : [ '^?nestList' ],
		replace : true,
		compile : function(el, tAttrs, transclude) {
			var contents = el.contents().remove();
			var compiledContents;
			return function($scope, ele, attrs, nestList) { // alert("childrenCompile");
				var count=0;
				var cLen=$scope.items?$scope.items.length:0;
				$scope.$on('nestlist.item.init', function(e, ele) {//console.log(e.currentScope==e.targetScope);
					e.stopPropagation();

					if (!e.currentScope.$parent.nestRootScope) {
						count++;
						if(cLen==count){
							e.currentScope.$parent.$emit('nestlist.item.init');
						}
					} else {
						e.currentScope.$parent.$emit('nestlist.item.done');
					}
				});
				if (!compiledContents) {
					compiledContents = $compile(contents);
				}
				compiledContents($scope, function(clone, $scope) {
					if($scope.items){
						var r = ele.append(clone);
						$scope.events = jQuery.extend(true, {}, $scope.events);
						$scope.events.currScope = $scope;

						// var nestable = nestList[0].getNestable();
						if (r.hasClass('dd')) {
							r.before(r.contents()); // tbw
							// 如果模板页面里面加了根节点元素，在编译子列表时应该移除掉这个根节点
							r.remove();
						}
					}else{
						ele.remove();
					}
					$scope.$emit('nestlist.item.init');
				});
			};
		}
	};
} ]);
directives.directive('focusMe', function($timeout, $parse) {
	return {
		// scope: true, // optionally create a child scope
		link : function(scope, element, attrs) {
			var model = $parse(attrs.focusMe);
			scope.$watch(model, function(value) {
				if (value === true) {
					$timeout(function() {
						element[0].focus();
					});
				}
			});
			// to address @blesh's comment, set attribute value to 'false'
			// on blur event:
			element.bind('blur', function() {

				// scope.$apply(model.assign(scope, false));
			});
		}
	};
});
directives.directive('c2FullCalendar', [ 'Messenger', '$timeout', '$parse', '$rootScope', '$ocLazyLoad', function(Messenger, $timeout, $parse, $rootScope, $ocLazyLoad) {
	return {
		scope : {
			startDate : '=',
			events : '=',
			format : '@',
			eventClick : '&',
			dayClick : '&',
			eventAfterRender : '&',
			eventAfterAllRender : '&',
			viewRender : '&'
		},
		restrict : 'A',
		controller : function($scope, $element, $attrs) {
			this.init = function(options) {
				return $element.fullCalendar(options);
			};
			this.getDom = function() {
				return $element;
			};
			this.destroy = function() {
				$element.fullCalendar('destroy');
			};
			this.next = function() {
				$element.fullCalendar('next');
			};
			this.prev = function() {
				$element.fullCalendar('prev');
			};
			this.getDate = function() {
				return $element.fullCalendar('getDate');
			};

		},
		require : [ 'c2FullCalendar' ],
		link : function(scope, element, attrs, ctrl) {

			var c2fullcalendarctrl = ctrl[0];

			var options = {
				header : {
					left : '',
					center : 'prev, title, next',
					right : ''
				},
				monthNames : [ '一月', '二月', '三月', '四月', '五月', '六月', '七月', '八月', '九月', '十月', '十一月', '十二月' ],
				dayNamesShort : [ '周日', '周一', '周二', '周三', '周四', '周五', '周六' ]
			};

			if (attrs.eventClick) {
				options.eventClick = function(event, jsEvent, view) {
					scope.eventClick({
						params : {
							event : event,
							jsEvent : jsEvent,
							view : view
						}
					});
				}
			}

			if (attrs.dayClick) {
				options.dayClick = function(event, jsEvent, view) {
					scope.dayClick({
						params : {
							event : event,
							jsEvent : jsEvent,
							view : view
						}
					});
				}
			}

			if (attrs.eventAfterRender) {
				options.eventAfterRender = function(event, element, view) {
					scope.eventAfterRender({
						params : {
							event : event,
							element : element,
							view : view
						}
					});
				}
			}

			if (attrs.viewRender) {
				options.viewRender = function(view, element) {
					scope.viewRender({
						params : {
							view : view,
							element : element
						}
					});
				}
			}

			if (attrs.eventAfterAllRender) {
				options.eventAfterAllRender = function(view) {
					scope.eventAfterAllRender({
						params : {
							view : view
						}
					});
				}
			}

			var c2FullCalendar = null;
			var promise = $ocLazyLoad.load([ 'assets/js/fullcalendar/fullcalendar.min.js', 'assets/css/fullcalendar.min.css' ]);
			promise.then(function() {
				if (attrs.events) {
					scope.$watch("events", function(v) {
						if (moment(scope.startDate).format(scope.format) == "Invalid date") {
							console.log("起始时间格式错误!正确的格式为:" + scope.format);
							options.defaultDate = moment();
						} else {
							options.defaultDate = moment(scope.startDate);
						}
						if (angular.isDefined(v)) {
							if (angular.equals(c2FullCalendar, null)) {
								options.events = v;
								$timeout(function() {
									c2FullCalendar = c2fullcalendarctrl.init(options);
								}, 200);
							} else {
								options.events = v;
								c2fullcalendarctrl.destroy();
								c2FullCalendar = c2fullcalendarctrl.init(options);
							}
						}

					});
				} else {
					var start = null;
					if (moment(scope.startDate).format(scope.format) == "Invalid date") {
						console.log("起始时间格式错误!正确的格式为:" + scope.format);
						start = moment();
					} else {
						start = moment(scope.startDate);
					}
					options.year = start.get('year');
					options.month = start.get('month');
					options.date = start.get('date');
					c2fullcalendarctrl.init(options);
				}
			});

		}
	}
} ]);

directives.directive('permRes', [ 'debounce', '$http', '$rootScope', function(debounce, $http, $rootScope) {
	var premResouces = {}
	var permitedDebounce = debounce(function(permRes, callback) {
		$http.post("ws/isPermitedMap", {
			permExprs : permRes
		}).success(function(data) {
			callback(data.result);
		});
	}, 500);
	function putPremResouce(k, v, specialAuth) {
		if(angular.isUndefined(premResouces[k])){
			premResouces[k] = new Array();
			premResouces[k].push(v);
		}else{
			premResouces[k].push(v);
		}
		var permRes = new Array();
		angular.forEach(premResouces, function(v, k) {
			permRes.push(k);
		});
		permitedDebounce(permRes, function(permitedResult) {
			angular.forEach(premResouces, function(v, k) {
				angular.forEach(premResouces[k],function(v,i){
					if (permitedResult[k]){
						premResouces[k][i].show().removeAttr("perm-res");
					}else{
						if (angular.isDefined(specialAuth) && specialAuth()) {
							premResouces[k][i].show().removeAttr("perm-res");
						} else {
							premResouces[k][i].remove();
						}
					}
				})
			})
			premResouces = {};
		});
	}
	return {
		scope:{
			specialAuth:"&"
		},
		link : function(scope, element, attrs) {
			element.hide();
			putPremResouce(attrs.permRes, element, scope.specialAuth);
		}
	};
} ]);

directives.directive('repeatDone', ['$timeout',function($timeout) {
	return {
		link : function(scope, element, attrs) {
			if (scope.$last) {
				scope.$eval(attrs.repeatDone);
            }
		}
	};
} ]);

directives.directive('c2lightbox', ['$ocLazyLoad','$timeout',function($ocLazyLoad,$timeout){
    return {
        restrict: 'C',
        link: function(scope, element) {

        	var dynamicGallery=function(){

        		var imgObj=[];

				element.find('img').each(function(c_index,c_img){
					//<h4 style="color:#E6DEDE">bug原因</h4><p>我也不知道原因</p>
					var title="";
					if(c_img.title){
						title="<h4 style=\"color:#E6DEDE\">"+c_img.title+"</h4>";
					}

	      			imgObj.push({
	      				src:c_img.src,
	      				thumb:c_img.src,
	      				subHtml:title
	      			});
        		});


				element.find('img').each(function(p_index,p_img){

					$(p_img).data("index",p_index);

					$(p_img).click(function(){

						var index_img=$(this).data("index");

						//只能通过这种方式设置当前index
						if(element.data('lightGallery')){
							element.data('lightGallery').index=index_img;
							element.data('lightGallery').s.index=index_img;
						}

						var slider= element.lightGallery({
					        dynamic: true,
					        dynamicEl: imgObj,
					        download:true,
					        index:index_img,
					        pager:true,
					        enableTouch:true
					    });

					});

        		});
        	}

			var promise = $ocLazyLoad.load(['assets/js/lightgallery.min.js','assets/js/lg-zoom.js','assets/js/lg-pager.js','assets/css/lightgallery.css']);
			promise.then(function(){

				MutationObserver = window.MutationObserver || window.WebKitMutationObserver;

	        	var observer = new MutationObserver(function(mutations, observer) {
	        		dynamicGallery();
	        	});

	        	observer.observe(element[0], {
		        	  subtree: true,
		        	  childList: true
		        });

	        	dynamicGallery();
			});
        }
    }

}]);

directives.directive('c2EchartsLine', ['$ocLazyLoad',
         function($ocLazyLoad) {
            return {
                    controller : function($scope, $element, $attrs) {
                    /**
                    * 初始化图表，如果不传参数则等于做刷新动作
                    */
                    this.init = function(options,events,theme){
                    	var promise = $ocLazyLoad.load('ext/lib/echarts-dist/echarts-all.js') ;
                    	promise.then(
                    			function(){
                              	  if(angular.isUndefined(theme)) theme="default" ;
                                    var echartLine = echarts.init($element[0], theme);
                                    if(angular.isDefined(options)){
                                     		$scope.echartLineOptions=options;
                                    }
                                    var realOpts=$scope.echartLineOptions;
                                    if(angular.isDefined($scope.echartBeforeRender)){
                                     		var output=$scope.echartBeforeRender({opts:realOpts});
                                     		//如果自定义的beforeRender有返回值，那么把该返回值作为图标的初始化参数
                                     		if(angular.isDefined(output)){
                                     			realOpts=output;
                                     		}
                                     }
                                     echartLine.setOption(realOpts);
                                     if(angular.isDefined(events)){
                                     		angular.forEach(events,function(func,name){
                                     				echartLine.on(name, func);
                                     		});
                                     }
                    			}
                    	) ;
                     };
                      this.getDom = function() {
                              return $element;
                      };
                      this.getOptions = function() {
                             return $scope.echartLineOptions;
                      };
                      this.getSeries=function(){
                             return $scope.series.series
                      };

                    },
                   scope : {
                             id : "@",
                             bindData : "=",
                             calculable:"@",
                             xaxisInterval:"@",
                             xaxisRotate:"@",
                             title : "@",
                             titleVice : "@",
                             titleX :"@",
                             titleY :"@",
                             legendOrient : "@",
                             legendData : "@",
                             legendX :"@",
                             legendY:"@",
                             toolboxShow : "@",
                             toolboxOrient : "@",
                             datazoomShow : "@",
                         	 datazoomStart: "=",
							 datazoomEnd: "=",
							 datazoomHeight: "@",
                             datazoomOrient : "@",
                             xaxisName : "@",
                             xaxisNameLocation : "@",
                             xaxisBoundaryGap :"=" ,
                             xaxisData : "=",
                             yaxisMax :"=",
                             yaxisName : "@",
                             yaxisPosition : "@",
                             yaxisNameLocation : "@",
                             echartLineClick : "&",
                             echartBeforeRender:"&",
                             gridWidth:"@",
                             theme:"@"
                           },
                    link : function(scope, element, attrs, echartsLineCtrl) {
                               					
                           scope.$watch("bindData", function(newValue) {
                               		if (newValue) {
                               			scope.series = newValue;
                               			var legends = new Array();
                               			for (i = 0; i < scope.series.series.length; i++) {
                               				legends[i] = newValue.series[i].name;
                               			}
                               			scope.legends = legends;
                               			var options  = {
                               									title : {
                               										text : scope.title,
                               										subtext : scope.titleVice,
                               										x: scope.titleX,
                               										y: scope.titleY
                               									},
                               									tooltip : {
                               										trigger : 'axis'
                               									},
                               									grid:{
                               										width:scope.gridWidth
                               									},
                               									legend : {
                               										orient : scope.legendOrient,
                               										data : (function() {
                               											var legend = scope.legendData;
                               											if (legend == '' || legend == undefined || legend == null) {
                               												return scope.legends;
                               											} else {
                               												return legend;
                               											}

                               										})(),
                               										x:scope.legendX,
                               										y:scope.legendY
                               									},
                               									toolbox : {
                               										show : scope.toolboxShow,
                               										orient : scope.toolboxOrient,
                               										feature : {
                               											mark : {
                               												show : true
                               											},
                               											dataView : {
                               												show : true,
                               												readOnly : false
                               											},
                               											magicType : {
                               												show : true,
                               												type : [ 'line', 'bar', 'stack', 'tiled' ]
                               											},
                               											restore : {
                               												show : true
                               											},
                               											saveAsImage : {
                               												show : true
                               											}
                               										}
                               									},
                               									dataZoom : {
                               										show : scope.datazoomShow,
                               										start: scope.datazoomStart,
                               										end: scope.datazoomEnd,
                               										height: scope.datazoomHeight,
                               										zoomLock:true
                               									},
                               									calculable : scope.calculable,
                               									xAxis : [ {
                               										type : "category",
                               										boundaryGap : scope.xaxisBoundaryGap,
                               										axisLabel:{'interval':scope.xaxisInterval,'rotate':scope.xaxisRotate},
                               										name : scope.xaxisName,
                               										nameLocation: scope.xaxisNameLocation,
                               										splitLine : {show : false},
                               										data : (function() {
                               											var xName = scope.xaxisData;
                               											if (xName == '' || xName == undefined || xName == null) {
                               												return scope.series.xAxis;
                               											} else {
                               												return xName;
                               											}

                               										})()
                               									// varx是在上面动态设置的
                               									} ],
                               									yAxis : [ {
                               										type : "value",
                               										name : scope.yaxisName,
                               										nameLocation:scope.yaxisNameLocation,
                               										position:scope.yaxisPosition,
                               										min:0,
                               										max:scope.yaxisMax
                               									} ],
                               									series : scope.series.series

                               								};// option
                               							var　events={};
                               							if(angular.isDefined(scope.echartLineClick)){
                               								events.click= function(param){
                               									 scope.echartLineClick({paramf:param});
                               								};
                               							}
                               							echartsLineCtrl.init(options,events,scope.theme);
                               						}
                               					}, true);

                               					 }// link
                               			};// return
                               		} ]);// function
directives.directive('c2EchartsPie', ['$ocLazyLoad',
                                       function($ocLazyLoad) {
                                          return {
                                                  controller : function($scope, $element, $attrs) {
                                                  /**
                                                  * 初始化图表，如果不传参数则等于做刷新动作
                                                  */
                                                  this.init = function(options,events,theme){
                                                  	var promise = $ocLazyLoad.load('ext/lib/echarts-dist/echarts-all.js') ;
                                                  	promise.then(
                                                  			function(){
                                                            	  if(angular.isUndefined(theme)) theme="default" ;
                                                                  var echartLine = echarts.init($element[0], theme);
                                                                  if(angular.isDefined(options)){
                                                                   		$scope.echartLineOptions=options;
                                                                  }
                                                                  var realOpts=$scope.echartLineOptions;
                                                                  if(angular.isDefined($scope.echartBeforeRender)){
                                                                   		var output=$scope.echartBeforeRender({opts:realOpts});
                                                                   		//如果自定义的beforeRender有返回值，那么把该返回值作为图标的初始化参数
                                                                   		if(angular.isDefined(output)){
                                                                   			realOpts=output;
                                                                   		}
                                                                   }
                                                                   echartLine.setOption(realOpts);
                                                                   if(angular.isDefined(events)){
                                                                   		angular.forEach(events,function(func,name){
                                                                   				echartLine.on(name, func);
                                                                   		});
                                                                   }
                                                  			}
                                                  	) ;
                                                   };
                                                    this.getDom = function() {
                                                            return $element;
                                                    };
                                                    this.getOptions = function() {
                                                           return $scope.echartLineOptions;
                                                    };
                                                    this.getSeries=function(){
                                                           return $scope.series.series
                                                    };

                                                  },
                                                 scope : {
                                                           id : "@",
                                                           bindData : "=",
                                                           calculable:"@",
                                                           xaxisInterval:"@",
                                                           xaxisRotate:"@",
                                                           title : "@",
                                                           titleVice : "@",
                                                           titleX :"@",
                                                           titleY :"@",
                                                           legendOrient : "@",
                                                           legendData : "@",
                                                           legendX :"@",
                                                           legendY:"@",
                                                           toolboxShow : "@",
                                                           toolboxOrient : "@",
                                                           datazoomShow : "@",
                                                       	   datazoomStart: "=",
                              							   datazoomEnd: "=",
                              							   datazoomHeight: "@",
                                                           datazoomOrient : "@",
                                                           xaxisName : "@",
                                                           xaxisNameLocation : "@",
                                                           xaxisBoundaryGap :"=" ,
                                                           xaxisData : "=",
                                                           yaxisMax :"=",
                                                           yaxisName : "@",
                                                           yaxisPosition : "@",
                                                           yaxisNameLocation : "@",
                                                           echartLineClick : "&",
                                                           echartBeforeRender:"&",
                                                           gridWidth:"@",
                                                           theme:"@"
                                                         },
                                                  link : function(scope, element, attrs, echartsLineCtrl) {
                                                             					
                                                         scope.$watch("bindData", function(newValue) {
                                                             		if (newValue) {
                                                             			scope.series = newValue;
                                                             			var legends = new Array();
                                                             			for (i = 0; i < scope.series.series[0].data.length; i++) {
                                                             				legends[i] = newValue.series[0].data[i].name;
                                                             			}
                                                             			scope.legends = legends;
                                                             			var options  = {
                                                             								title : {
                                                             								text : scope.title,
                                                             								subtext : scope.titleVice,
                                                             								x: scope.titleX,
                                                             								y: scope.titleY
                                                             								},
                                                             								tooltip : {
                                                             									trigger : 'axis'
                                                             								},
                                                             							
                                                             								legend : {
                                                           										orient : scope.legendOrient,
                                                           										data : (function() {
                                                          											var legend = scope.legendData;
                                                           											if (legend == '' || legend == undefined || legend == null) {
                                                           												return scope.legends;
                                                           											} else {
                                                           												return legend;
                                                           											}
                                                             									})(),
                                                             									x:scope.legendX,
                                                             									y:scope.legendY
                                                             								},
                                                             								toolbox : {
                                                             									show : scope.toolboxShow,
                                                             									orient : scope.toolboxOrient,
                                                             									feature : {
                                                            										mark : {
                                                            											show : true
                                                         											},
                                                        											dataView : {
                                                        												show : true,
                                                              												readOnly : false
                                                             											},
                                                           											magicType : {
                                                           												show : true,
                                                           												type : [ 'line', 'bar', 'stack', 'tiled' ]
                                                           											},
                                                           											restore : {
                                                           												show : true
                                                           											},
                                                           											saveAsImage : {
                                                           												show : true
                                                           											}
                                                           										}
                                                             									},
                                                            								calculable : scope.calculable,
                                                             								series : scope.series.series

                                                             							};// option
                                                             						var　events={};
                                                             						if(angular.isDefined(scope.echartLineClick)){
                                                             							events.click= function(param){
                                                             								 scope.echartLineClick({paramf:param});
                                                             							};
                                                             						}
                                                           							echartsLineCtrl.init(options,events,scope.theme);
                                                           						}
                                                           					}, true);

                                                             					 }// link
                                                             			};// return
                                                             		} ]);// function



directives.directive('timelineItem', ['DynamicTempalteCache','$compile', function (DynamicTempalteCache,$compile) {
	return {
		link: function ($scope, $element, $attrs) {
			DynamicTempalteCache.promise.success(function(){
				var template = DynamicTempalteCache.timeline($scope);
				if(template)$element.append($compile(template)($scope));
			});
		}
	};
}]);

directives.directive('reference', ['DynamicTempalteCache','$compile', function (DynamicTempalteCache,$compile) {
	return {
		scope:{
			data:"=",
			type:"=",
			click:"&?"
		},
		link: function ($scope, $element, $attrs) {
			var template = DynamicTempalteCache.reference($scope.type);
			if(template){
				//创建HTML和事件响应
				var dom = $compile(template)($scope);
				if($attrs.title)dom.attr('title',$attrs.title);
				if($attrs.class)dom.addClass($attrs.class);
				
				$element.replaceWith(dom);
				
				if(angular.isDefined($attrs.click)){
					dom.on('click',function(event){
						$scope.click({data:$scope.data,type:$scope.type});
						event.stopPropagation();
						event.preventDefault();
					});
				}
			}else{
				$element.replaceWith("<span class='ref-unkown'>未知类型的引用</span>");
			}
			
			//加载引用的完整数据，暂不实现
		}
	};
}]);
directives.directive('directiveKiller', [function () {
	return {
		restrict:"A",
		priority :1001,//号称ng-repeat优先级最高，这个还要高1
		terminal:true//优先级低于该指令的全部无效
	};
}]);

//任务列表指令
directives.directive('c2Tasks', [ '$compile', "$timeout","$http", function($compile, $timeout,$http) {
	return {
		scope:{
			search:"="
		},
		replace:false,
		restrict : "E",
		templateUrl : "template/tasks.html",
		controller : function($scope, $element, $attrs, $transclude) {
			this.getTaskList = function(searchParam,pageIndex){
		    	$http.post("ws/taskList",{search:searchParam,pageable:{pageSize:10,pageIndex:pageIndex}}).success(function(data){
		    		angular.forEach(data.result.contents,function(task){
		    			task.nomalAnimate = true;
		    			if(!task.isDone&&task.estStartDate&&task.deadline){
		    				var startDateString = "开始日期:"+moment(task.estStartDate).format("YYYY-MM-DD");
		    				var allTime = task.deadline-task.estStartDate;
		    				var useTime = new Date().getTime()-task.estStartDate;
		    				if(useTime>0){
		    					if(useTime<allTime){
			    					task.residueDate = "剩余"+moment(task.deadline).diff(moment(),'days')+"天("+startDateString+")";
			    					task.timeProgress = (Math.round(useTime/allTime*100))+"%";
			    					task.progress = true;
			    				}else{
			    					task.residueDate = "逾期"+moment(moment()).diff(task.deadline,'days')+"天("+startDateString+")";
			    					task.deplayed = true;
			    				}
		    				}else{
		    					task.residueDate = startDateString;
		    				}
		    			}
		    			
		    			angular.forEach(task.labels,function(label){
		    				label.backgroundStyle = {"background-color":label.color,"border-radius":"2px","color":getTextColorByBackColor(label.color)};
		    			});
		    		});
		//    		$scope.taskList = replaceWithOldElements(data.result.contents,$scope.taskList,"id");
		    		$scope.taskList = data.result.contents;
		    		$("#taskListDiv").css("height","auto");
		    		$scope.page.total = data.result.total;
		    		if($stateParams.init == "add"){
		    			$timeout(function(){$scope.openNewTask()},500);
		    		}
		    	});
			}
		},
		link : function(scope, element, attrs, controller) {
			controller.getTaskList(scope.search,1);
		}
	};
} ]);

directives.directive('c2Listview', [ '$compile', "$templateCache","$http", function($compile, $templateCache,$http) {
	return {
		scope:true,
		restrict : "E",
		controller:function($scope, $element, $attrs, $transclude){
			var controller = this;
			$scope.listviewLoading = false;
			var pageSize = $attrs.pageSize?$attrs.pageSize:10;
			$scope.page = {total:10,pageSize:pageSize,pageIndex:1};
			$scope.params = $scope.$parent[$attrs.params];
			
			this.load = function(params,isMore){
				$scope.listviewLoading = true;
				if(params)$scope.params=params;
				$scope.params.pageable = {pageIndex:$scope.page.pageIndex,pageSize:$scope.page.pageSize};
				$http.post($attrs.url,$scope.params).success(function(data){
					if($attrs.loadSuccess)$scope.$parent[$attrs.loadSuccess](data);
					if(isMore) $scope.listviewData = $scope.listviewData.concat(data.result.contents);
					else $scope.listviewData = data.result.contents;
					$scope.page = {pageIndex: data.result.pageIndex,pageSize: data.result.pageSize,total: data.result.total,totalPage: data.result.totalPage};
					$scope.listviewLoading = false;
				});
			};
			this.refresh = function(params){
				$scope.page.pageIndex = 1;
				controller.load(params);
			};
			this.getListViewData = function(){
				return $scope.listviewData;
			};
		},
		link : function(scope, element, attrs, controller) {
			var rowName = attrs.rowName?attrs.rowName:"row";
			var pageWay = attrs.pageWay?attrs.pageWay:"pagination";
			var initLoad = attrs.initLoad=="false"?false:true;
			
			if(initLoad)controller.load(scope[attrs.params]);
			
			if(element.find("listview-head").length>0){
				scope.headTemplate = element.find("listview-head").children();
				scope.hasHead = true;
			}
			var rowEle = element.find("listview-row");
			if(rowEle.length>0){
				var rowDom = rowEle[0];
				angular.forEach(rowDom.attributes,function(v,k){
					if(angular.isUndefined(scope.rowAttrs))scope.rowAttrs = {};
					scope.rowAttrs[v.name] = v.value;
				});
				rowEle.find("[directive-killer]").removeAttr("directive-killer");
				scope.rowTemplate = rowEle.html();
				scope.hasRow = true;
			}
//			else{
//				scope.rowTemplate = DynamicTempalteCache.listviewRow("task");
//				scope.hasRow = true;
//			}
			if(!scope.hasHead&&!scope.hasRow)scope.rowTemplate = element.children();
			element.empty();
			
			var listviewTemplateEle = $($templateCache.get("listviewTemplate"));
			if(scope.hasHead)listviewTemplateEle.find(".lv-body").before(scope.headTemplate);
			var listviewItemEle = listviewTemplateEle.find(".lv-item");
			if(angular.isDefined(scope.rowAttrs)){
				angular.forEach(scope.rowAttrs,function(v,k){
					listviewItemEle.attr(k,v);
				});
			}
			listviewItemEle.attr("ng-repeat",rowName+" in listviewData");
			listviewItemEle.append(scope.rowTemplate);
			element.append(listviewTemplateEle);
			
			var pageHtml;
			if(attrs.pageWay=="more"){
				scope.loadmore = function(){
					scope.page.pageIndex++;
					controller.load(scope[attrs.params],true);
				}
				var pageHtml = '<button ng-hide="page.pageIndex >= page.totalPage" ng-click="loadmore()" class="btn btn-default btn-block no-shadow load-more"><span ng-if="!listviewLoading">加载更多</span><span ng-if="listviewLoading"><i class="md md-replay"></i> 加载中...</span></button>';
			}else{
				scope.pageTrun = function(i){
					scope.page.pageIndex = i;
					controller.load(scope[attrs.params]);
				}
				var pageHtml = '<c2-pagination total="page.total" page-size="'+scope.page.pageSize+'" on-page-change="pageTrun(pageIndex)"></c2-pagination>';
			}
			element.append(pageHtml);
			$compile(element.children())(scope);
		}
	}
}]);

directives.directive('c2DateRangeDropdown', [ '$compile', "$templateCache","$http","$timeout", function($compile, $templateCache,$http,$timeout) {
	return {
		scope:{
			dateRange:"=",
			dateButtons:"=?",
			format:"@",
			startDate:"@",
			defaultButton:"@",
			buttomMessage:"@"
		},
		restrict : "EA",
		controller:function($scope, $element, $attrs, $transclude){
			var controller = this;
			$scope.toggleElement = $element.find(".dropdown-toggle");
			if ($scope.toggleElement.length == 0) {
			  $scope.toggleElement = $element.children().first();
			}

			function onBodyDown(event) {
				// 单点击元素为下拉框或者为触发下拉框元素时，不进行处理（触发下拉框元素本身已经有了toggle事件）
				if (!($(event.target).hasClass("dropdown-menu") || $(event.target).parents(".dropdown-menu").length > 0 || $(event.target)[0] == $scope.toggleElement[0] || $(event.target)
						.parent()[0] == $scope.toggleElement[0])) {
					controller.toggle();
				}
			}
			
			$scope.dateRange = $scope.dateRange || {};
			var allButtons = $scope.allButtons = {
					preWeek:{id:"preWeek",name:"上周",from:moment().subtract(7, 'd').startOf('week'),to:moment().subtract(7, 'd').endOf('week')},
					thisWeek:{id:"thisWeek",name:"本周",from:moment().startOf('week'),to:moment().endOf('week')},
					nextWeek:{id:"nextWeek",name:"下周",from:moment().add(7, 'd').startOf('week'),to:moment().add(7, 'd').endOf('week')},
					preMonth:{id:"preMonth",name:"上月",from:moment().subtract(1,"M").startOf('month'),to:moment().subtract(1,"M").endOf('month')},
					thisMonth:{id:"thisMonth",name:"本月",from:moment().startOf('month'),to:moment().endOf('month')},
					nextMonth:{id:"nextMonth",name:"下月",from:moment().add(1, 'M').startOf('month'),to:moment().add(1, 'M').endOf('month')},
					thisYear:{id:"thisYear",name:"今年",from:moment().startOf('year'),to:moment().endOf("year")}
				};
			$scope.dateChooseButtons = [];
			angular.forEach($scope.dateButtons,function(d,i){
				if(allButtons[d])$scope.dateChooseButtons.push(allButtons[d]);
			});
			
			var template = angular.element('<div class="dropdown-menu p-5" ng-class="{open:dropdownOpen}" style="width:460px;">'+
	        		'<div class="button-bar p-5" ng-if="dateChooseButtons.length>0">'+
	        		'<button ng-repeat="db in dateChooseButtons" ng-click="buttonDate(db)" ng-class="{\'btn-primary\':db.active}" class="btn btn-default picker-shadow">{{db.name}}</button>'+
	        		'</div>'+
	        		'<div class="row">'+
		        		'<div class="col-sm-6"><div class="datetimepickerFrom"></div></div>'+
		        		'<div class="col-sm-6"><div class="datetimepickerTo"></div></div>'+
	        		'</div>'+
	        		'<div class="buttom-message c-white" ng-if="buttomMessage"><i class="md md-error m-r-5"></i>{{buttomMessage}}</div>'+
	        		'</div>');
        	$element.addClass("dropdown");
        	$element.append(template);
        	$compile(template)($scope);
        	
        	this.dateStartEle = template.find(".datetimepickerFrom");
        	this.dateEndEle = template.find(".datetimepickerTo");
			
			this.openState = false;
			this.dateRange = $scope.dateRange;
			this.toggle = function(open) {
				if(angular.isUndefined(open)) controller.openState = !controller.openState;
				else{
					if(controller.openState === open) return ;
					else controller.openState = open;
				}
				if (controller.openState) {
					$element.toggleClass("open");
					$("body").bind("mousedown", onBodyDown);
				} else {
					$element.toggleClass("open");
					$("body").unbind("mousedown", onBodyDown);
				}
			};
			this.setRangeDate = function(p){
				if(angular.isString(p)){
					var dateBtn = allButtons[p];
					if(dateBtn){
						$scope.noApply = true;
						controller.dateStartEle.data("DateTimePicker").date(dateBtn.from);
						controller.dateEndEle.data("DateTimePicker").date(dateBtn.to);
					}
				}
			}
		},
		link : function(scope, element, attrs, controller) {
			scope.toggleElement.bind("click",function(){
				controller.toggle();
			});
        	//时间查询初始化
			var datetimepickerOption = {
					format:scope.format?scope.format:"YYYY-MM-dd",
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
			scope.buttonTriggerTime = true;
			controller.dateStartEle.datetimepicker(datetimepickerOption).on('dp.change', function(e) {
				applyWrap(function(){
					scope.dateRange.start = e.date.toDate().getTime();
					//高亮按钮
					var endDate = controller.dateEndEle.data("DateTimePicker").date();
					if(scope.format=='YYYY-MM'){
						scope.dateRange.show = e.date.format("YYYY/MM")+" - "+endDate.format("YYYY/MM");
					}else{
						scope.dateRange.show = e.date.format("MM/DD")+" - "+endDate.format("MM/DD");
					}
					angular.forEach(scope.dateChooseButtons,function(v){
						v.active = v.to.toDate().getTime() == endDate.toDate().getTime() && e.date.toDate().getTime() == v.from.toDate().getTime();
						if(v.active)scope.dateRange.show = v.name;
					});
				},!scope.noApply);
				$timeout(function(){
					controller.dateEndEle.data("DateTimePicker").minDate(e.date);
				});
			});
			if(scope.startDate){//设置时间选择框的最小起始时间
				controller.dateStartEle.data("DateTimePicker").minDate(moment(scope.startDate));
			}
			controller.dateEndEle.datetimepicker(datetimepickerOption).on('dp.change', function(e) {
				applyWrap(function(){
					scope.dateRange.end = e.date.toDate().getTime();
					
					var startDate = controller.dateStartEle.data("DateTimePicker").date();
					if(scope.format=='YYYY-MM'){
						scope.dateRange.show = startDate.format("YYYY/MM")+" - "+e.date.format("YYYY/MM");
					}else{
						scope.dateRange.show = startDate.format("MM/DD")+" - "+e.date.format("MM/DD");
					}
					angular.forEach(scope.dateChooseButtons,function(v){
						v.active = v.from.toDate().getTime() == startDate.toDate().getTime() && e.date.toDate().getTime() == v.to.toDate().getTime();
						if(v.active)scope.dateRange.show = v.name;
					});
				},!scope.noApply);
				controller.dateStartEle.data("DateTimePicker").maxDate(e.date);
				scope.noApply = false;
			});		
			if(scope.defaultButton){
				scope.noApply = true;
				controller.dateStartEle.data("DateTimePicker").date(scope.allButtons[scope.defaultButton].from);
				controller.dateEndEle.data("DateTimePicker").date(scope.allButtons[scope.defaultButton].to);
			}
			//没有默认按钮时采用dateRange
			if(scope.dateRange.start && scope.dateRange.end){
				scope.noApply = true;
				controller.dateStartEle.data("DateTimePicker").date(moment(scope.dateRange.start));
				controller.dateEndEle.data("DateTimePicker").date(moment(scope.dateRange.end));
			}
        	
        	scope.buttonDate = function(buttonDate){
    			angular.forEach(scope.dateChooseButtons,function(v){
    				v.active = buttonDate==v;
    			});
    			//定义按钮触发时间控件改变，防止时间控件Apply报错
    			scope.noApply = true;
    			controller.dateStartEle.data("DateTimePicker").maxDate(false);
    			controller.dateEndEle.data("DateTimePicker").minDate(false);
    			controller.dateStartEle.data("DateTimePicker").date(buttonDate.from);
    			controller.dateEndEle.data("DateTimePicker").date(buttonDate.to);
    		}
        	
        	/*scope.$watch("dateRange",function(v){
        		if(v.start){
        			scope.noApply = true;
        			controller.dateStartEle.data("DateTimePicker").date(moment(v.start));
        		}
        		if(v.end){
        			scope.noApply = true;
        			controller.dateStartEle.data("DateTimePicker").date(moment(v.end));
        		}
        	})*/
        	
        	function applyWrap(fn,isApply){
        		if(isApply){
        			scope.$apply(fn);
        		}else fn();
        	}
		}
	}
}]);

/*看板滚动分页指令
 *指令写至看板ul的父级div元素上，防止ul展示异常现象 
 * 
 */
directives.directive ('whenScrolled', function ($timeout) {
	return{
		restrict:'A',
		scope:{
			loadMore:'&'
		},
		link:function(scope,elm,attr){
			// 内层DIV的滚动加载
			$timeout(function(){
				var raw = elm.find("ul.group-task-list")[0];
				$(raw).bind ('scroll', function () {
					if ( raw.scrollTop + raw.offsetHeight >= raw.scrollHeight*0.8 ) {
						scope.$apply (scope.loadMore({name:$(raw).parent().find('.stage-name')[0].textContent}));
					}
				});
			},300)
		}
	}
});
