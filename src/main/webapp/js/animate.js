developmentCenter.animation(".animate-show-expend",function(){
	return {
		addClass:function(ele,clsName,done){
			console.log("animationAdd");
			ele.slideUp();
			console.log(clsName);
		},
		removeClass:function(ele,clsName,done){
			console.log("animationRemove");
			ele.slideDown();
			console.log(clsName);
		}
	}
});
developmentCenter.animation(".estimate-modal-open",function(){
	return {
		enter: function(element,done){
			$(element).css({
				opacity: 0,
		        position: "relative",
		        right: "5px"
			})
			.animate({
				right: 0,
		        opacity: 1
		        }, 500, done) ;
		}
	}
});
