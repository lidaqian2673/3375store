//控制层
app.controller('userController', function ($scope, $controller, userService) {
    $scope.entity = {};

    //修改注册个人信息
    $scope.updateInfo = function () {
        //填写昵称
        if ($scope.entity.nickName == null || $scope.entity.nickName == "") {
            alert("请填写昵称");
            return;
        }

    }

    //注册用户
    $scope.reg = function () {

        /*	if($scope.password == null || $scope.entity.password == null || $scope.entity.username == null){
                alert("密码不能为空");
                return ;
            }*/


        //比较两次输入的密码是否一致
        if ($scope.password != $scope.entity.password) {
            alert("两次输入密码不一致，请重新输入");
            $scope.entity.password = "";
            $scope.password = "";
            return;
        }
        //新增
        userService.add($scope.entity, $scope.smscode).success(
            function (response) {
                alert(response.message);
            }
        );
    }

    //发送验证码
    $scope.sendCode = function () {
        if ($scope.entity.phone == null || $scope.entity.phone == "") {
            alert("请填写手机号码");
            return;
        }

        userService.sendCode($scope.entity.phone).success(
            function (response) {
                alert(response.message);
            }
        );
    }


});	
