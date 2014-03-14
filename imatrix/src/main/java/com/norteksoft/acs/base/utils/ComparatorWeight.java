package com.norteksoft.acs.base.utils;

import java.util.Comparator;

import com.norteksoft.acs.entity.organization.Department;
import com.norteksoft.acs.entity.organization.User;

@SuppressWarnings("unchecked")
public class ComparatorWeight implements Comparator{

	public int compare(Object o1, Object o2) {
		User user1=null;
		User user2=null;
		Department dept1=null;
		Department dept2=null;
		if(o1 instanceof User&&o2 instanceof User){
			user1=(User)o1;
			user2=(User)o2;
		}
		if(o1 instanceof Department&&o2 instanceof Department){
			dept1=(Department)o1;
			dept2=(Department)o2;
		}
		if(user1!=null&&user2!=null){
			int flag=user1.getWeight().compareTo(user2.getWeight());
			  if(flag==0){
			   return user1.getName().compareTo(user2.getName());
			  }else{
			   return -flag;
			  }  
		}
		if(dept1!=null&&dept2!=null){
			int flag=dept1.getWeight().compareTo(dept2.getWeight());
			  if(flag==0){
			   return dept1.getName().compareTo(dept2.getName());
			  }else{
			   return -flag;
			  }  
		}
		return 0;
	}

}
