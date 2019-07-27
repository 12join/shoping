app.service('evaluateService',function($http) {
    this.evaluate = function (id,page,size) {
        return $http.get('../evaluate/find.do?id=' + id+'&page='+page+'&size='+size);
    }
});