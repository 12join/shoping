app.service('favoriteService',function($http){
    this.save=function (id) {
        return $http.get('../favorite/save.do?itemId='+id);
    }

    this.isFavor=function (id) {
        return $http.post('../favorite/isFavor.do?itemId='+id);
    }
});