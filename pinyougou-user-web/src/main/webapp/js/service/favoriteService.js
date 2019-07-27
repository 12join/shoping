app.service('favoriteService',function($http){
    this.save=function (id) {
        return $http.get('../favorite/save.do?itemId='+id);
    }

    this.isFavor=function (id) {
        return $http.post('../favorite/isFavor.do?itemId='+id);
    }

    this.Nofavorite=function (id) {
        return $http.post('../favorite/NoFavor.do?itemId='+id);
    }
    this.findAll=function (id) {
        return $http.post('../favorite/findAll.do');
    }

});