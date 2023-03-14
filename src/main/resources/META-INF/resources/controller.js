var app = angular.module("CashbackModule", []);



//Controller Part
var controller = app.controller('CashbackController', ['$scope', '$http', function($scope, $http) {
    //Initialize page with default data which is blank in this example
    $scope.cashback = [];

    $scope.page=0;
    $scope.size;
    $scope.customer_name_param="";
    $scope.pageCount;

    //Now load the data from server
    _findCustomers();

    $scope.previousPage = function(){
        if($scope.page>0)
            _changePage($scope.page-1);
    }
    $scope.nextPage = function(){
        if($scope.page<$scope.pageCount-1)
        _changePage($scope.page+1);
    }

    $scope.findByName = function(event){
        if($scope.customer_name_param.length >= 2){
            _findCustomers();
        }
    }

    /* Private Methods */
    _changePage =function (newPage){
        $scope.page=newPage;
        _findCustomers();
    }

    //HTTP GET- get paginated list of cashback
    function _findCustomers() {
        let url = '/customer?page='+$scope.page+'&size='+($scope.size == undefined ? 0 : $scope.size)
        if($scope.customer_name_param.length > 1){
            url=url+'&name='+$scope.customer_name_param
        }

        $http({
            method: 'GET',
            url: url
        }).then(function successCallback(response) {
            $scope.customers = response.data.customers;
            $scope.pageCount= response.data.pageCount;
        }, function errorCallback(response) {
            console.log(response.statusText);
        });
    }

    function _success(response) {
        _findCustomers();
    }

    function _error(response) {
        alert(response.data.message || response.statusText);
    }
}]);
