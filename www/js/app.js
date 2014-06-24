'use strict';

// Declare app level module which depends on filters, and services
angular.module('mojDan', [
  'ngRoute',
 // 'mojDan.filters',
 // 'mojDan.services',
 // 'mojDan.directives',
  'mojDan.controllers'
]).
config(['$routeProvider', function($routeProvider) {
  $routeProvider.when('/passreset/:otp', 
  		{templateUrl: 'www/partials/passreset.html', 
  		 controller: 'passResetCtrl'}
  );
  $routeProvider.otherwise({redirectTo: '/'});
}]);
