//首页控制器
app.controller('indexController', function ($scope, loginService) {
    $scope.showName = function () {
        loginService.showName().success(
            function (response) {
                $scope.loginName = response.loginName;
                //$scope.totalValue= orderService.sum($scope.cartList);
            }
        );
    }


    $scope.findOrderList = function () {
        loginService.findOrderList().success(
            function (response) {
                $scope.cartList = response;

            }
        );
    }

});