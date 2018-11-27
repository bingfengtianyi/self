app.controller('seckillGoodsController',function ($scope,seckillGoodsService){
    $scope.findList = function () {
        seckillGoodsService.findList().success(
            function (response) {
                $scope.list = response;
            }
        )
    };

    // $scope.findOne = function () {
    //     seckillGoodsService.findOne($location.search()['id']).success(
    //         function (response) {
    //             $scope.seckillGood = response;
    //             //获取距离结束所剩的总秒数
    //             $scope.allSeconds = Math.floor(((new Date(response.endTime).getTime())-(new Date().getTime()))/1000);
    //              time = $interval(function () {
    //                 if ($scope.allSeconds>0){
    //                     $scope.allSeconds--;
    //                     $scope.timeStr = convertTimeStr($scope.allSeconds);
    //                 }else {
    //                     $interval.cancel(time);
    //                     alert("秒杀活动结束");
    //                 }
    //             },1000);
    //         }
    //     )
    // };

    // convertTimeStr = function (allSeconds) {
    //     var day = Math.floor(allSeconds/(60*60*24));
    //     var hour = Math.floor((allSeconds-day*24*60*60)/(60*60));
    //     var minute = Math.floor((allSeconds-day*24*60*60-hour*60*60)/60);
    //     var second = Math.floor(allSeconds-day*24*60*60-hour*60*60-minute*60);
    //     var timeStr = "";
    //     if (day>0){
    //         timeStr += day+" 天 ";
    //     }
    //     timeStr += hour+" 时 "+minute+" 分 "+second+" 秒";
    //     return timeStr;
    // }
    //
    // $scope.submitOrder=function () {
    //     seckillOrderService.submitOrder($scope.seckillGood.id).success(
    //         function (response) {
    //             if(response.flag){
    //                 //成功
    //                 alert("下单成功！");
    //                 location.href="pay.html";
    //             }else{
    //                 alert(response.message);
    //             }
    //         }
    //     )
    // }

});
