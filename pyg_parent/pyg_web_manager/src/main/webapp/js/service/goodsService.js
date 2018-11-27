//服务层
app.service('goodsService',function($http){
	    	
	//读取列表数据绑定到表单中
	this.findAll=function(){
		return $http.get('../goods/findAll.action');
	}
	//分页 
	this.findPage=function(page,rows){
		return $http.get('../goods/findPage.action?pageNum='+page+'&pageSize='+rows);
	}
	//全部商品分页
    this.findPageAll = function(page,rows){
        return $http.get("../goods/findGoodsList.action?pageNum="+page+"&pageSize="+rows);
    }
	//查询实体
	this.findOne=function(id){
		return $http.get('../goods/findOne.action?id='+id);
	}
	//增加 
	this.add=function(entity){
		return  $http.post('../goods/add.action',entity );
	}
	//修改 
	this.update=function(entity){
		return  $http.post('../goods/update.action',entity );
	}
	//删除
	this.dele=function(ids){
		return $http.get('../goods/delete.action?ids='+ids);
	}
	//搜索
	this.search=function(page,rows,searchEntity){
		return $http.post('../goods/searchForManager.action?pageNum='+page+"&pageSize="+rows, searchEntity);
	}    
	
	this.updateStatus = function(ids,status){
		return $http.get('../goods/updateStatus.action?ids='+ids+"&status="+status);
	}
});
