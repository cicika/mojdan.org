'use strict';

/* Services */

var mojDanREST = angular.module('mojDan.services', ['ngResource']);

mojDanREST.factory('mdREST', ['$resource', function($resource){
    return $resource('user/passreset', {}, {
      passReset: {method:'POST'},
      forgotPass: {method: 'POST', 
    							 url: 'user/forgotpass'}

    });
  }
]);
