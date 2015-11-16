'use strict';

/* Controllers */

angular.module('mojDan.controllers', ["ngRoute", 'ngResource'])
  .controller('passResetCtrl', ['$scope', '$routeParams', 'mdREST', '$location',  
    function($scope, $routeParams, mdREST, $location) {
 
    $scope.resolved = function(){
      if($scope.response == undefined) {
        return false
      } else {
        return $scope.response.$resolved 
      }
    };
    $scope.sent = function(){
      if($scope.response == undefined){
        return false
      } else {
        return true
      }
    };
    $scope.passTemplate = function(){
      if($scope.status == 404){
        return "www/partials/forgotpass.html";
      } else {
        return "www/partials/passreset.html";
      }
    };

  	$scope.save = function(){
  		$scope.data = { new_password: $scope.new_password,
  						       otp: $routeParams.otp };
      $scope.response = mdREST.passReset(angular.toJson($scope.data), 
        function(data, headers){
          console.log(headers);
        }, 
        function(response){
          $scope.response = response;
          $scope.status = response.status;
          if($scope.status == 404){
            $location.path("/forgotpass/re").replace();
          }
      });
  	};

  	$scope.passwdMatch = function(){
  		var res = ($scope.new_password == $scope.new_password_repeat);
  		return res;
  	};
    $scope.new_pw_def = function(){
      return ($scope.new_password != undefined)
    };
  }])
  .controller('forgotPassCtrl', ['$scope', '$location', 'mdREST', function($scope, $location, mdREST) {
    $scope.reMsg = function(){
      if($location.path() == "/forgotpass/re") return true
      else return false
    };
    $scope.sent = function(){
        if($scope.response == undefined){
          return false
        } else {
          return true
        }
      };
    $scope.resolved = function(){
        if($scope.response == undefined) {
          return false
        } else {
          alert($scope.response.$resolved);
          return $scope.response.$resolved 
        }
      };
    $scope.save = function(){
      $scope.data = {email: $scope.email};
      $scope.status = undefined;
      $scope.response = mdREST.forgotPass(angular.toJson($scope.data),
        function(data, headers){

        },
        function(response){
          $scope.status = response.status;
        }
      )
    };
  }])
  .controller('indexController', ['$scope', function($scope) {

  }]);
