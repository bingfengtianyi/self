//购物车控制层
app.controller('cartController',function($scope,$location,cartService,addressService,orderService){

	$scope.loginName = "";

	//页面初始化
	$scope.init = function () {
		$scope.cartList = cartService.getCartList();
		var itemId = $location.search()["itemId"];
		var num = $location.search()["num"];
		if (itemId!=null&&num!=null){
			$scope.addGoodsToCartList(itemId,num);
		}else {
			$scope.findCartList();
			/*cartService.findCartList($scope.cartList).success(
				function (response) {
					if (response.success){
						if (response.loginName==""){
							cartService.setCartList(response.data)
						}
						$scope.cartList=response.data;
					}
                }
			)*/
		}
    };

    //添加商品到购物车
    $scope.addGoodsToCartList = function (itemId,num) {
		cartService.addGoodsToCartList($scope.cartList,itemId,num).success(
			function (respone) {
				if (respone.success) {
                    $scope.cartList = respone.data;
                    if (""!=respone.loginName) {
                        $scope.findCartList();
                    }else {
                        cartService.setCartList(respone.data);
                    }
				   $scope.loginName = respone.loginName;
				}
			})
    };

    //查询购物车
    $scope.findCartList=function(){
        $scope.cartList= cartService.getCartList();//取出本地购物车
        cartService.findCartList($scope.cartList).success(
            function (response) {
                $scope.cartList=response.data;
                //如果用户登陆，清除本地购物车
                if(response.loginName!=""){
                    cartService.removeCartList();
                }
                $scope.loginName=response.loginName;

            }
        )
    };
    
    //合计数
	$scope.$watch("cartList",function (newValue, oldValue) {
		$scope.totalValue = cartService.sum(newValue);
    });

    //查询当前用户的地址列表
    $scope.findAddressList=function () {
        addressService.findListByLoginUser().success(
            function (response) {
                $scope.addressList=response;
                //默认地址选择
                for(var i=0;i<$scope.addressList.length;i++){
                    if($scope.addressList[i].isDefault=='1'){
                        $scope.address=$scope.addressList[i];
                        break;
                    }
                }
            }
        )
    };


    //选择地址
    $scope.selectAddress=function (address) {
        $scope.address=address;
    };

    //判断某地址对象是否是当前的地址
    $scope.isSelectedAddress=function (address) {
        if($scope.address==address){
            return true;
        }else{
            return false;
        }

    };

    $scope.order={paymentType:"1"} ;
    //选择支付方式
    $scope.selectPaymentType=function (type) {
        $scope.order.paymentType=type;
    };
    
    //提交订单
    $scope.submitOrder = function () {

        //收货人信息
        $scope.order.receiverAreaName=$scope.address.address;//地址
        $scope.order.receiverMobile=$scope.address.mobile;//电话
        $scope.order.receiver=$scope.address.contact;//收货人


        orderService.submitOrder($scope.order).success(
            function (response) {
                if (response.flag){//如果成功
                    if ($scope.order.paymentType=="1"){//如果是微信支付
                        location.href="pay.html";
                    }else {
                        location.href="orderSuccess.html"
                    }
                }else {
                    alert(response.message);
                }
            }
        )
    }

});