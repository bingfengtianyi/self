// 定义服务层:
app.service("brandService",function($http){
	this.findAll = function(){
		return $http.get("../brand/findAllBrands.action");
	}
	
	this.findPage = function(page,rows){
		return $http.get("../brand/findBrandListByPage.action?pageNum="+page+"&pageSize="+rows);
	}
	
	this.add = function(entity){
		return $http.post("../brand/addBrand.action",entity);
	}
	
	this.update=function(entity){
		return $http.post("../brand/updateBrand.action",entity);
	}
	
	this.findOne=function(id){
		return $http.get("../brand/findOne.action?id="+id);
	}
	
	this.dele = function(ids){
		return $http.get("../brand/deleteBrandsByIds.action?ids="+ids);
	}
	
	this.search = function(page,rows,searchEntity){
		return $http.post("../brand/searchBrands.action?pageNum="+page+"&pageSize="+rows,searchEntity);
	}
	
	this.selectOptionList = function(){
		return $http.get("../brand/selectOptionList.action");
	}
});