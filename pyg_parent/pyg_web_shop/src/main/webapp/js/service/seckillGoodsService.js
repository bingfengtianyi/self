app.service('seckillGoodsService',function ($http) {

    this.findList = function () {
        return $http.get("../seckillGoods/findList.action");
    };

    this.findOne = function (id) {
        return $http.get("seckillGoods/findOneFromRedis.action?id="+id);
    }
});