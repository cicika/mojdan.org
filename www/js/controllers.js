'use strict';

/* Controllers */

angular.module('mojDan.controllers', ["ngRoute", 'ngResource'])
  .controller('passResetCtrl', ['$scope', '$routeParams', function($scope, $routeParams) {
  	$scope.save = function(){
  		$scope.data = { new_password: $scope.new_password,
  						       otp: $routeParams.otp };
  		alert($scope.data.new_password + " " + $scope.data.otp);
  	};
  	$scope.passwdMatch = function(){
  		var res = !($scope.new_password == $scope.new_password_repeat)
  		return res;
  	}
  }])
  .controller('MyCtrl2', ['$scope', function($scope,  $location, $routeParams) {

  }]);
