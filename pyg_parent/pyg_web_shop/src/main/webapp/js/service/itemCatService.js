//服务层
app.service('itemCatService',function($http){
	    	
	//读取列表数据绑定到表单中
	this.findAll=function(){
		return $http.get('../itemCat/findAll.action');
	}
	//分页 
	this.findPage=function(page,rows){
		return $http.get('../itemCat/findPage.action?pageNum='+page+'&pageSize='+rows);
	}
	//查询实体
	this.findOne=function(id){
		return $http.get('../itemCat/findOne.action?id='+id);
	}
	//增加 
	this.add=function(entity){
		return  $http.post('../itemCat/add.action',entity );
	}
	//修改 
	this.update=function(entity){
		return  $http.post('../itemCat/update.action',entity );
	}
	//删除
	this.dele=function(ids){
		return $http.get('../itemCat/delete.action?ids='+ids);
	}
	//搜索
	this.search=function(page,rows,searchEntity){
		return $http.post('../itemCat/search.action?pageNum='+page+"&pageSize="+rows, searchEntity);
	}    	
	
	this.findByParentId = function(parentId){
		return $http.get("../itemCat/findByParentId.action?parentId="+parentId);
	}
});
