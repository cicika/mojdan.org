'use strict';

// Declare app level module which depends on filters, and services
angular.module('mojDan', [
  'ngRoute',
 // 'mojDan.filters',
  'mojDan.services',
 // 'mojDan.directives',
  'mojDan.controllers'
]).
config(['$routeProvider', function($routeProvider) {
	$routeProvider.when('/',
		{templateUrl: 'www/partials/comingsoon.html',
	   controller: 'indexController'}
	 );
  $routeProvider.when('/passreset/:otp', 
  		{templateUrl: 'www/partials/passreset.html', 
  		 controller: 'passResetCtrl'}
  );
   $routeProvider.when('/forgotpass', 
  		{templateUrl: 'www/partials/forgotpass.html', 
  		 controller: 'forgotPassCtrl'}
  );
  $routeProvider.when('/forgotpass/re', 
  		{templateUrl: 'www/partials/forgotpass.html', 
  		 controller: 'forgotPassCtrl'}
  );
  $routeProvider.otherwise({redirectTo: '/'});
}]);
