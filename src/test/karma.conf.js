// Karma configuration
// Generated on Thu Dec 10 2015 17:04:49 GMT+0800 (中国标准时间)

module.exports = function(config) {
  config.set({

    // base path that will be used to resolve all patterns (eg. files, exclude)
    basePath: '',


    // frameworks to use
    // available frameworks: https://npmjs.org/browse/keyword/karma-adapter
    frameworks: ['jasmine'],


    // list of files / patterns to load in the browser
    files: [
		'../main/webapp/assets/js/jquery.min.js',
		'../main/webapp/assets/js/jquery.nicescroll.min.js',
		'../main/webapp/assets/js/angular/angular.min.js',
		'../main/webapp/assets/js/angular/angular-route.min.js',
		'../main/webapp/assets/js/angular/angular-resource.min.js',
		'../main/webapp/assets/js/angular/angular-sanitize.min.js',
		'../main/webapp/assets/js/angular/angular-animate.min.js',
		'../main/webapp/assets/js/angular/ext/angular-ui-router.min.js',
		'../main/webapp/assets/js/angular/ext/loading-bar.js',
		'../main/webapp/assets/js/angular/ext/ocLazyLoad.min.js',
		'../main/webapp/assets/js/angular/ext/ng-file-upload.min.js',
		'../main/webapp/assets/js/moment.min.js',
		'resouce/angular-mocks.js',
		'resouce/MooTools-Core-1.5.2.js',
		'../main/webapp/js/common.js',
		'../main/webapp/js/services.js',
		'../main/webapp/js/directive.js',
		'../main/webapp/js/app.js',
		// 'resouce/base.js',
		'directive/*.js'
    ],


    // list of files to exclude
    exclude: [
    ],


    // preprocess matching files before serving them to the browser
    // available preprocessors: https://npmjs.org/browse/keyword/karma-preprocessor
    preprocessors: {
    },


    // test results reporter to use
    // possible values: 'dots', 'progress'
    // available reporters: https://npmjs.org/browse/keyword/karma-reporter
    reporters: ['progress','dots'],


    // web server port
    port: 9876,


    // enable / disable colors in the output (reporters and logs)
    colors: true,


    // level of logging
    // possible values: config.LOG_DISABLE || config.LOG_ERROR || config.LOG_WARN || config.LOG_INFO || config.LOG_DEBUG
    logLevel: config.LOG_INFO,


    // enable / disable watching file and executing tests whenever any file changes
    autoWatch: false,


    // start these browsers
    // available browser launchers: https://npmjs.org/browse/keyword/karma-launcher
    browsers: ['Chrome'],


    // Continuous Integration mode
    // if true, Karma captures browsers, runs the tests and exits
    singleRun: false
  })
}
