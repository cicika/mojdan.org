'use strict';

/* Services */

var mojDanREST = angular.module('mojDan.services', ['ngResource']);

mojDanREST.factory('mdREST', ['$resource', '$location', function($resource, $location){
    return $resource($location.path(), {}, {
      passReset: {method:'POST',
    						  url: 'user/passreset'},
      forgotPass: {method: 'POST', 
    							 url: 'user/forgotpass'}

    });
  }
]);
