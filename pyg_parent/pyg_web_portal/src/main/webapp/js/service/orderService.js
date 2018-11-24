app.service('orderService',function ($http) {
    //提交订单
   this.submitOrder = function (order) {
       return $http.post("order/add.action",order);
   }
});