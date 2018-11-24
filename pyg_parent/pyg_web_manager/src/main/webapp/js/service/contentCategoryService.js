//服务层
app.service('contentCategoryService',function($http){
	    	
	//读取列表数据绑定到表单中
	this.findAll=function(){
		return $http.get('../contentCategory/findAll.action');
	}
	//分页 
	this.findPage=function(page,rows){
		return $http.get('../contentCategory/findPage.action?pageNum='+page+'&pageSize='+rows);
	}
	//查询实体
	this.findOne=function(id){
		return $http.get('../contentCategory/findOne.action?id='+id);
	}
	//增加 
	this.add=function(entity){
		return  $http.post('../contentCategory/add.action',entity );
	}
	//修改 
	this.update=function(entity){
		return  $http.post('../contentCategory/update.action',entity );
	}
	//删除
	this.dele=function(ids){
		return $http.get('../contentCategory/delete.action?ids='+ids);
	}
	//搜索
	this.search=function(page,rows,searchEntity){
		return $http.post('../contentCategory/search.action?pageNum='+page+"&pageSize="+rows, searchEntity);
	}    	
});
