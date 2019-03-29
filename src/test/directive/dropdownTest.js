describe("DropdownDirective", function() {
  var $compile,$rootScope,$controller,$timeout,$httpBackend;
  beforeEach(module('developmentCenter'));
  beforeEach(inject(function(_$compile_, _$rootScope_,_$controller_,_$timeout_,_$httpBackend_){
    $compile = _$compile_;
    $rootScope = _$rootScope_;
    $controller = _$controller_;
    $timeout = _$timeout_;
    $httpBackend = _$httpBackend_;

    // $httpBackend.when('GET', 'assets/js/pinyin/MooTools-Core-1.5.2.js').respond("GOOD");
  }));
  afterEach(function() {
  });
  describe("数组选项", function() {
    var $scope;
    beforeEach(inject(function(){
      $scope = $rootScope.$new();
      $scope.bind = {name:"aa",id:"1"};
      $scope.bindId = "1";
      $scope.items=[
        {name:"aa",id:"1"},
        {name:"bb",id:"2",good:'ccc'}
      ];
    }));
    it('选项', function() {
      var element = $compile("<c2-dropdown items='items' item-name='name'><button>abcd</button></c2-dropdown>")($scope);
      $scope.$digest();
      expect(element.controller("c2Dropdown").getDropdownItems().length).toBe(2);
      expect(element.children('ul.dropdown-menu').children('li').length).toBe(2);
    });
    it('模版ID赋值', function() {
      $scope.myId = "2";
      var element = $compile("<c2-dropdown bind-data-id='myId' bind-data='myBind' items='items' item-name='name' item-id='id'><span id='bindValue'>{{myBind.good}}</span></c2-dropdown>")($scope);
      $scope.$digest();
      expect(element.controller("c2Dropdown").getBindData()).toEqual($scope.items[1]);
      expect(element.find('#bindValue').text()).toBe('ccc');
    });
    it('模版对象赋值', function() {
      $scope.myBind = {id:"2"};
      var element = $compile("<c2-dropdown bind-data='myBind' items='items' item-name='name' item-id='id'><span id='bindValue'>{{myBind.good}}</span></c2-dropdown>")($scope);
      $scope.$digest();
      expect(element.controller("c2Dropdown").getBindData()).toEqual($scope.items[1]);
      expect(element.find('#bindValue').text()).toBe('ccc');
    });
    it('模版Id值变化', function() {
      $scope.myBind = {id:"2"};
      $scope.items.push({name:"ccc",id:"3",good:"great"});
      var element = $compile("<c2-dropdown bind-data='myBind' items='items' item-name='name' item-id='id'><span id='bindValue'>{{myBind.good}}</span></c2-dropdown>")($scope);
      $scope.$digest();
      expect(element.controller("c2Dropdown").getBindData()).toEqual($scope.items[1]);
      expect(element.find('#bindValue').text()).toBe('ccc');
      $scope.myBind = {id:"3"};
      $scope.$digest();
      expect(element.controller("c2Dropdown").getBindData()).toEqual($scope.items[2]);
      expect(element.find('#bindValue').text()).toBe('great');
    });
    it('初始化指令前设值', function() {
      var element = $compile("<c2-dropdown bind-data='bind' items='items' item-name='name'><span id='bindValue'>{{bind.name}}</span></c2-dropdown>")($scope);
      $scope.$digest();
      expect(element.find('#bindValue').text()).toBe('aa');
      expect($(element.children('ul.dropdown-menu').children('li')[0]).hasClass('selected')).toBe(true);
      var c = element.controller("c2Dropdown");
      expect($scope.bind).toEqual({name:"aa",id:"1"});
      expect(c.getBindData()).toEqual({name:"aa",id:"1"});
      expect(c.getBindDataId()).toEqual('aa');
    });
    it('初始化指令前id设值', function() {
      var element = $compile("<c2-dropdown bind-data-id='bindId' bind-data='bindData' items='items' item-name='name' item-id='id'><span id='bindValue'>{{bindData.name}}</span></c2-dropdown>")($scope);
      $scope.$digest();
      expect(element.find('#bindValue').text()).toBe('aa');
      expect($(element.children('ul.dropdown-menu').children('li')[0]).hasClass('selected')).toBe(true);
      var c = element.controller("c2Dropdown");
      expect(c.getBindData()).toEqual({name:"aa",id:"1"});
      expect(c.getBindDataId()).toEqual('1');
      expect($scope.bindData).toEqual({name:"aa",id:"1"});
      expect($scope.bindId).toEqual('1');
    });
    it('初始化指令后设值', function() {
      var element = $compile("<c2-dropdown bind-data='bind2' items='items' item-name='name'><button>abcd</button></c2-dropdown>")($scope);
      $scope.$digest();
      var c = element.controller("c2Dropdown");
      expect($(element.children('ul.dropdown-menu').children('li')[0]).hasClass('selected')).toBe(false);
      $scope.bind2 = {name:"aa",id:"1"};
      $scope.$digest();
      expect($(element.children('ul.dropdown-menu').children('li')[0]).hasClass('selected')).toBe(true);
      expect(c.getBindData()).toEqual({name:"aa",id:"1"});
      expect(c.getBindDataId()).toEqual('aa');
    });
    it('修改值', function() {
      var element = $compile("<c2-dropdown bind-data='bind' items='items' item-name='name'><button>abcd</button></c2-dropdown>")($scope);
      $scope.$digest();
      var c = element.controller("c2Dropdown");
      expect($(element.children('ul.dropdown-menu').children('li')[0]).hasClass('selected')).toBe(true);
      expect(c.getBindData()).toEqual({name:"aa",id:"1"});
      expect(c.getBindDataId()).toEqual('aa');
      $scope.bind = {name:"bb",id:"2"};
      $scope.$digest();
      expect($(element.children('ul.dropdown-menu').children('li')[1]).hasClass('selected')).toBe(true);
      expect(c.getBindData()).toEqual({name:"bb",id:"2",good:"ccc"});
      expect(c.getBindDataId()).toEqual('bb');
    });
    it('通过bindDataId修改值', function() {
      var element = $compile("<c2-dropdown bind-data-id='bindId' items='items' item-name='name' item-id='id'><button>abcd</button></c2-dropdown>")($scope);
      $scope.$digest();
      var c = element.controller("c2Dropdown");
      expect($(element.children('ul.dropdown-menu').children('li')[0]).hasClass('selected')).toBe(true);
      expect(c.getBindData()).toEqual({name:"aa",id:"1"});
      expect(c.getBindDataId()).toEqual('1');
      $scope.bindId = "2";
      $scope.$digest();
      expect($(element.children('ul.dropdown-menu').children('li')[1]).hasClass('selected')).toBe(true);
      expect(c.getBindData()).toEqual({name:"bb",id:"2",good:"ccc"});
      expect(c.getBindDataId()).toEqual('2');
    });
    it('初始化指令后设置选项', function() {
      var element = $compile("<c2-dropdown bind-data='bind' items='items2' item-name='name'><button>abcd</button></c2-dropdown>")($scope);
      $scope.$digest();
      var c = element.controller("c2Dropdown");
      expect(element.children('ul.dropdown-menu').children('li').length).toBe(0);
      $scope.items2 = [{name:"aa",id:"1"},{name:"bb",id:"2"}];
      $scope.$digest();
      expect($(element.children('ul.dropdown-menu').children('li')[0]).hasClass('selected')).toBe(true);
      expect(c.getBindData()).toEqual({name:"aa",id:"1"});
      expect(c.getBindDataId()).toEqual('aa');
    });
    it('分组', function() {
      $scope.itemsGroup = [{name:"aa",group:"A"},{name:"ab",group:"A"},{name:"ba",group:"B"},{name:"bb",group:"B"},{name:"cc"}];
      var element = $compile("<c2-dropdown items='itemsGroup' item-name='name' multiple='true'><button>abcd</button></c2-dropdown>")($scope);
      $scope.$digest();
      var c = element.controller("c2Dropdown");
      expect(element.children('ul.dropdown-menu').children('li').length).toBe(7);
      expect(element.children('ul.dropdown-menu').children('li.dropdown-header').length).toBe(2);
      expect(element.children('ul.dropdown-menu').children('li.dropdown-son').length).toBe(4);
      element.children('ul.dropdown-menu').children('li.dropdown-header')[0].click();
      expect(c.getBindDataId()).toEqual("aa,ab");
      expect(c.getBindData()).toEqual([$scope.itemsGroup[0],$scope.itemsGroup[1]]);
    });
    it('搜索', function() {
      var element = $compile("<c2-dropdown items='items' item-name='name' filter='true' filter-pinyin='false'><button>abcd</button></c2-dropdown>")($scope);
      $scope.$digest();
      expect(element.children('ul.dropdown-menu').children('li.dropdown-search').length).toBe(1);
      expect(element.children('ul.dropdown-menu').children('li').not('.dropdown-search').length).toBe(2);
      element.find("input").scope().dropdownFilterValue = "aa";
      $scope.$digest();
      expect(element.children('ul.dropdown-menu').children('li').not('.dropdown-search').length).toBe(1);
    });
    it('自定义搜索属性', function() {
      $scope.itemsGroup = [{name:"aa",group:"S"},{name:"ab",group:"S"},{name:"ba",group:"G"},{name:"bb",group:"G"},{name:"cc"}];
      var element = $compile("<c2-dropdown items='itemsGroup' item-name='name' filter='true' filter-pinyin='false' filter-property='group'><button>abcd</button></c2-dropdown>")($scope);
      $scope.$digest();
      element.find("input").scope().dropdownFilterValue = "S";
      $scope.$digest();
      expect(element.children('ul.dropdown-menu').children('li').not('.dropdown-search').length).toBe(2);
    });
    it('点击事件', function() {
      var clickEventGo=false;
      $scope.itemClickEven = function(item){
        expect(item.id).toBe("1");
        clickEventGo=true;
      };
      var element = $compile("<c2-dropdown id='clickTestId' items='items' item-name='name' item-id='id' item-click='itemClickEven(item)'><button>abcd</button></c2-dropdown>")($scope);
      $scope.$digest();
      var c = element.controller("c2Dropdown");
      // spyOn(c, 'toggle');
      expect(element.children('ul.dropdown-menu').children('li').length).toBe(2);
      element.children('ul.dropdown-menu').children('li')[0].click();
      $timeout.flush();
      expect(clickEventGo).toBe(true);
      expect(c.getBindData()).toEqual($scope.items[0]);
      expect(c.getBindDataId()).toBe("1");

      // expect(c.toggle.calls.count()).toEqual(1);
    });
    describe('多选',function(){
      var $scope;
      beforeEach(inject(function(){
        $scope = $rootScope.$new();
        $scope.binds = [{name:"aa",id:"1"},{name:"bb",id:"2"}];
        $scope.bindIds = "1,2";
        $scope.items=[
          {name:"aa",id:"1"},
          {name:"bb",id:"2",good:'goodBoy'},
          {name:"cc",id:"3"}
        ];
      }));
      it('多个值绑定',function(){
        var element = $compile("<c2-dropdown bind-data='binds' items='items' item-name='name' item-id='id' multiple='true'><span id='bindValue'>xxx</span></c2-dropdown>")($scope);
        $scope.$digest();
        expect(element.children('ul.dropdown-menu').children('li').length).toBe(3);
        expect($(element.children('ul.dropdown-menu').children('li')[0]).hasClass('selected')).toBe(true);
        expect($(element.children('ul.dropdown-menu').children('li')[1]).hasClass('selected')).toBe(true);
        expect($(element.children('ul.dropdown-menu').children('li')[2]).hasClass('selected')).toBe(false);
        var c = element.controller("c2Dropdown");
        expect($scope.binds).toEqual([$scope.items[0],$scope.items[1]]);
        expect(c.getBindData()).toEqual([$scope.items[0],$scope.items[1]]);
        expect(c.getBindDataId()).toEqual("1,2");
      });
      it('多个ID绑定',function(){
        var element = $compile("<c2-dropdown bind-data-id='bindIds' items='items' item-name='name' item-id='id' multiple='true'><span id='bindValue'>xxx</span></c2-dropdown>")($scope);
        $scope.$digest();
        expect(element.children('ul.dropdown-menu').children('li').length).toBe(3);
        expect($(element.children('ul.dropdown-menu').children('li')[0]).hasClass('selected')).toBe(true);
        expect($(element.children('ul.dropdown-menu').children('li')[1]).hasClass('selected')).toBe(true);
        expect($(element.children('ul.dropdown-menu').children('li')[2]).hasClass('selected')).toBe(false);
        var c = element.controller("c2Dropdown");
        expect($scope.bindIds).toEqual("1,2");
        expect(c.getBindData()).toEqual([$scope.items[0],$scope.items[1]]);
        expect(c.getBindDataId()).toEqual("1,2");
      });
      it('初始化后绑定值ID',function(){
        var element = $compile("<c2-dropdown bind-data-id='bindIdsA' items='items' item-name='name' item-id='id' multiple='true'><span id='bindValue'>xxx</span></c2-dropdown>")($scope);
        $scope.$digest();
        var c = element.controller("c2Dropdown");
        expect(element.children('ul.dropdown-menu').children('li').length).toBe(3);
        expect($(element.children('ul.dropdown-menu').children('li')[0]).hasClass('selected')).toBe(false);
        expect($(element.children('ul.dropdown-menu').children('li')[1]).hasClass('selected')).toBe(false);
        expect($(element.children('ul.dropdown-menu').children('li')[2]).hasClass('selected')).toBe(false);
        $scope.bindIdsA = "1,2";
        $scope.$digest();
        expect($(element.children('ul.dropdown-menu').children('li')[0]).hasClass('selected')).toBe(true);
        expect($(element.children('ul.dropdown-menu').children('li')[1]).hasClass('selected')).toBe(true);
        expect($scope.bindIdsA).toEqual("1,2");
        expect(c.getBindData()).toEqual([$scope.items[0],$scope.items[1]]);
        expect(c.getBindDataId()).toEqual("1,2");
      });
      it('初始化后绑定值对象',function(){
        var element = $compile("<c2-dropdown bind-data='bindsB' items='items' item-name='name' item-id='id' multiple='true'><span id='bindValue'>xxx</span></c2-dropdown>")($scope);
        $scope.$digest();
        var c = element.controller("c2Dropdown");
        expect(element.children('ul.dropdown-menu').children('li').length).toBe(3);
        expect($(element.children('ul.dropdown-menu').children('li')[0]).hasClass('selected')).toBe(false);
        expect($(element.children('ul.dropdown-menu').children('li')[1]).hasClass('selected')).toBe(false);
        expect($(element.children('ul.dropdown-menu').children('li')[2]).hasClass('selected')).toBe(false);
        $scope.bindsB = [{name:"aa",id:"1"},{name:"bb",id:"2"}];;
        $scope.$digest();
        expect($(element.children('ul.dropdown-menu').children('li')[0]).hasClass('selected')).toBe(true);
        expect($(element.children('ul.dropdown-menu').children('li')[1]).hasClass('selected')).toBe(true);
        expect($scope.bindsB).toEqual([$scope.items[0],$scope.items[1]]);
        expect(c.getBindData()).toEqual([$scope.items[0],$scope.items[1]]);
        expect(c.getBindDataId()).toEqual("1,2");
      });
      it('修改值',function(){
        var element = $compile("<c2-dropdown bind-data-id='bindIds' items='items' item-name='name' item-id='id' multiple='true'><span id='bindValue'>xxx</span></c2-dropdown>")($scope);
        $scope.$digest();
        expect(element.children('ul.dropdown-menu').children('li').length).toBe(3);
        expect($(element.children('ul.dropdown-menu').children('li')[0]).hasClass('selected')).toBe(true);
        expect($(element.children('ul.dropdown-menu').children('li')[1]).hasClass('selected')).toBe(true);
        expect($(element.children('ul.dropdown-menu').children('li')[2]).hasClass('selected')).toBe(false);
        var c = element.controller("c2Dropdown");
        expect($scope.bindIds).toEqual("1,2");
        expect(c.getBindData()).toEqual([$scope.items[0],$scope.items[1]]);
        expect(c.getBindDataId()).toEqual("1,2");
        $scope.bindIds = '1,3';
        $scope.$digest();
        expect($(element.children('ul.dropdown-menu').children('li')[0]).hasClass('selected')).toBe(true);
        expect($(element.children('ul.dropdown-menu').children('li')[1]).hasClass('selected')).toBe(false);
        expect($(element.children('ul.dropdown-menu').children('li')[2]).hasClass('selected')).toBe(true);
        expect($scope.bindIds).toEqual('1,3');
        expect(c.getBindData()).toEqual([$scope.items[0],$scope.items[2]]);
        expect(c.getBindDataId()).toEqual("1,3");
      });
      it('修改值对象',function(){
        var element = $compile("<c2-dropdown bind-data='binds' items='items' item-name='name' item-id='id' multiple='true'><span id='bindValue'>xxx</span></c2-dropdown>")($scope);
        $scope.$digest();
        var c = element.controller("c2Dropdown");
        expect(element.children('ul.dropdown-menu').children('li').length).toBe(3);
        expect($(element.children('ul.dropdown-menu').children('li')[0]).hasClass('selected')).toBe(true);
        expect($(element.children('ul.dropdown-menu').children('li')[1]).hasClass('selected')).toBe(true);
        expect($(element.children('ul.dropdown-menu').children('li')[2]).hasClass('selected')).toBe(false);
        $scope.binds = [{id:"2"},{id:"3"}];
        $scope.$digest();
        expect($(element.children('ul.dropdown-menu').children('li')[0]).hasClass('selected')).toBe(false);
        expect($(element.children('ul.dropdown-menu').children('li')[1]).hasClass('selected')).toBe(true);
        expect($(element.children('ul.dropdown-menu').children('li')[2]).hasClass('selected')).toBe(true);
        expect($scope.binds).toEqual([$scope.items[1],$scope.items[2]]);
        expect(c.getBindData()).toEqual([$scope.items[1],$scope.items[2]]);
        expect(c.getBindDataId()).toEqual("2,3");
      });
      it('点击事件', function() {
        var clickEventGo=false;
        $scope.itemClickEven = function(item){
          expect(item.id).toBe("3");
          clickEventGo=true;
        };
        var element = $compile("<c2-dropdown bind-data-id='bindIds' items='items' item-name='name' item-id='id' multiple='true' item-click='itemClickEven(item)'><button>abcd</button></c2-dropdown>")($scope);
        $scope.$digest();
        var c = element.controller("c2Dropdown");
        // spyOn(c, 'toggle');
        expect(element.children('ul.dropdown-menu').children('li').length).toBe(3);
        expect($(element.children('ul.dropdown-menu').children('li')[0]).hasClass('selected')).toBe(true);
        expect($(element.children('ul.dropdown-menu').children('li')[1]).hasClass('selected')).toBe(true);
        expect($(element.children('ul.dropdown-menu').children('li')[2]).hasClass('selected')).toBe(false);
        element.children('ul.dropdown-menu').children('li')[2].click();
        $timeout.flush();
        expect($(element.children('ul.dropdown-menu').children('li')[0]).hasClass('selected')).toBe(true);
        expect($(element.children('ul.dropdown-menu').children('li')[1]).hasClass('selected')).toBe(true);
        expect($(element.children('ul.dropdown-menu').children('li')[2]).hasClass('selected')).toBe(true);
        expect(clickEventGo).toBe(true);
        expect(c.getBindData()).toEqual([$scope.items[0],$scope.items[1],$scope.items[2]]);
        expect(c.getBindDataId()).toBe("1,2,3");

        // expect(c.toggle.calls.count()).toEqual(1);
      });
    });
  });
  describe("方法", function() {
    var $scope;
    beforeEach(inject(function(){
      $scope = $rootScope.$new();
      $scope.bindId = "1";
      $scope.bind = {name:"aa",id:"1"};
      $scope.items=[
        {name:"aa",id:"1"},
        {name:"bb",id:"2"}
      ];
    }));
    it('getDropdownItems', function() {
      $scope.bindId = 'aa';
      var element = $compile("<c2-dropdown bind-data-id='bindId' items='items' item-name='name'><button>abcd</button></c2-dropdown>")($scope);
      $scope.$digest();
      var controller = element.controller("c2Dropdown");
      expect(controller.getDropdownItems().length).toBe(2);
    });
    it("clean",function(){
      $scope.bindId = 'aa';
      var element = $compile("<c2-dropdown bind-data-id='bindId' items='items' item-name='name'><button>abcd</button></c2-dropdown>")($scope);
      $scope.$digest();
      var controller = element.controller("c2Dropdown");
      expect(controller.getBindData()).toBe($scope.items[0]);
      controller.clean();
      expect(controller.getBindData()).toBe(null);
    });
  });

});
