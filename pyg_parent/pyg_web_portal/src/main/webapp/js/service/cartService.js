//购物车服务层
app.service('cartService',function($http){

	//向localStorage中存储购物车列表
	this.setCartList = function (cartList) {
		localStorage.setItem("cartList",JSON.stringify(cartList));
    }

    //清空localStorage中的购物车列表
	this.removeCartList = function () {
		localStorage.removeItem("cartList");
    }

    //从localStorage中获取购物车列表
	this.getCartList = function () {
		var cartListStr = localStorage.getItem("cartList");
		if (cartListStr == null){
			return [];
		} else {
            return JSON.parse(cartListStr);
        }
    }

    //添加商品到购物车
	this.addGoodsToCartList = function (cartList, itemId, num) {
		return $http.post("cart/addGoodsToCartList.action?itemId="+itemId+"&num="+num,cartList);
    }

    //合计数
	this.sum = function (cartList) {
		var totalValue = {totalNum:0,totalMoney:0};
		for (var i=0;i<cartList.length;i++){
			var cart = cartList[i];
			var orderItemList = cart.orderItemList;
			for (var j=0;j<orderItemList.length;j++){
				var orderItem = orderItemList[j];
				totalValue.totalNum += orderItem.num;
				totalValue.totalMoney += orderItem.totalFee;
			}
		}
		return totalValue;
    }

    //查找购物车
	this.findCartList = function (cartList) {
		return $http.post("cart/findCartList.action",cartList);
    }
	
});