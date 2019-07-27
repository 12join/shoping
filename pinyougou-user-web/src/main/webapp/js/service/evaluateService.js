app.service('evaluateService',function($http){
    this.save=function (evaluate) {
        return $http.post('../evaluate/save.do',evaluate);
    }

});