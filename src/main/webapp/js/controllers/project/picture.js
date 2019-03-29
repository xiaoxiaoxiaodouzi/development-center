angular.module('project')
    /*项目封面图片切换对话框的控制器*/
    .registerCtrl('projectPicController',['$scope','Modal','Messenger','Upload', function($scope,Modal,Messenger,Upload){
        $scope.pics=[
            {disSrc:'assets/img/headers/square/1.png',src:"assets/img/headers/sm/1.png"},
            {disSrc:'assets/img/headers/square/2.png',src:"assets/img/headers/sm/2.png"},
            {disSrc:'assets/img/headers/square/3.png',src:"assets/img/headers/sm/3.png"},
            {disSrc:'assets/img/headers/square/4.png',src:"assets/img/headers/sm/4.png"},
            {disSrc:'assets/img/headers/square/5.png',src:"assets/img/headers/sm/5.png"},
            {disSrc:'assets/img/headers/square/6.png',src:"assets/img/headers/sm/6.png"},
            {disSrc:'assets/img/headers/square/7.png',src:"assets/img/headers/sm/7.png"},
            {disSrc:'assets/img/headers/square/8.png',src:"assets/img/headers/sm/8.png"},
            {disSrc:'assets/img/headers/square/9.png',src:"assets/img/headers/sm/9.png"},
            {disSrc:'assets/img/headers/square/10.png',src:"assets/img/headers/sm/10.png"},
            {disSrc:'assets/img/headers/square/11.png',src:"assets/img/headers/sm/11.png"},
            {disSrc:'assets/img/headers/square/12.png',src:"assets/img/headers/sm/12.png"},
            {disSrc:'assets/img/headers/square/13.png',src:"assets/img/headers/sm/13.png"},
            {disSrc:'assets/img/headers/square/14.png',src:"assets/img/headers/sm/14.png"},
            {disSrc:'assets/img/headers/square/15.png',src:"assets/img/headers/sm/15.png"},
            {disSrc:'assets/img/headers/square/16.png',src:"assets/img/headers/sm/16.png"},
            {disSrc:'assets/img/headers/square/17.png',src:"assets/img/headers/sm/17.png"},
            {disSrc:'assets/img/headers/square/18.png',src:"assets/img/headers/sm/18.png"},
            {disSrc:'assets/img/headers/square/19.png',src:"assets/img/headers/sm/19.png"},
            {disSrc:'assets/img/headers/square/20.png',src:"assets/img/headers/sm/20.png"}
        ]

        $scope.picSelected = function(pic){
          Modal.close(pic.src);
        }

        
        $scope.uploadStart = function($files, $file, $event, $rejectedFiles){
            if(!$file && $rejectedFiles.length > 0){
              Messenger.error('不允许上传大于10M的文件');
              return;
            }

            if(!$file)return;
          
	      	var uploadFile=function(fileObj){
	            var notify = Messenger.post('封面开始上传');
	            Upload.upload({
	              url:'file',
	              file:fileObj,
	              fileName:fileObj.name,
	              fileFormDataName:'file',
	              sendFieldAs:'form'
	            }).progress(function(evt) {
	              notify.update('message','上传中：' + parseInt(100.0 * evt.loaded / evt.total) + '% ');
	            }).success(function(data, status, headers, config) {
	              notify.update('message','上传成功!');
	              Modal.close(data.url);
	            }).error(function(data, status, headers, config) {
	              notify.update('type','danger');
	              notify.update('message','上传失败，错误信息：' + data.message);
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
	    	
            // 文件压缩处理
		    lrz($file,{width:520,quality:0.7}).then(function (rst){
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
       }
}])
