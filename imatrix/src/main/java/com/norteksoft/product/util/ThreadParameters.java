package com.norteksoft.product.util;


public class ThreadParameters{
		private Long userId;
		private Long companyId;
		private Long pageSize;
		private Integer pageNumber;
		private String companyCode;
		private String userName;
		private String companyName;
		private String password;
		private String honorificTitle;
		private String loginName;
		private Long systemId;
		private String systemCode;
		private Long subCompanyId;//用户所在分支机构id
		private Long departmentId;//用户的正职部门id
		private String subCompanyCode;//用户所在分支机构编码
		private String subCompanyName;//用户所在分支机构名称
		private String subCompanyShortTitle;//用户所在分支机构简称
		
		public ThreadParameters() {
			super();
		}
		
		public ThreadParameters(Long companyId) {
			super();
			this.companyId = companyId;
		}
		
		public ThreadParameters(String loginName) {
			super();
			this.loginName = loginName;
		}
		
		public ThreadParameters(Long companyId,Long userId) {
			super();
			this.companyId = companyId;
			this.userId = userId;
		}

		public Long getUserId() {
			return userId;
		}
		public void setUserId(Long userId) {
			this.userId = userId;
		}
		public Long getCompanyId() {
			return companyId;
		}
		public void setCompanyId(Long companyId) {
			this.companyId = companyId;
		}
		public Long getPageSize() {
			return pageSize;
		}
		public void setPageSize(Long pageSize) {
			this.pageSize = pageSize;
		}
		public Integer getPageNumber() {
			return pageNumber;
		}
		public void setPageNumber(Integer pageNumber) {
			this.pageNumber = pageNumber;
		}

		public String getCompanyCode() {
			return companyCode;
		}
		
		public void setCompanyCode(String companyCode){
			this.companyCode=companyCode;
		}

		public String getUserName() {
			return userName;
		}
		public void setUserName(String userName) {
			this.userName = userName;
		}

		public String getCompanyName() {
			return companyName;
		}

		public String getPassword() {
			return password;
		}

		public String getHonorificTitle() {
			return honorificTitle;
		}

		public String getLoginName() {
			return loginName;
		}
		public void setLoginName(String loginName) {
			this.loginName = loginName;
		}

		public Long getSystemId() {
			return systemId;
		}

		public void setSystemId(Long systemId) {
			this.systemId = systemId;
		}

		public String getSystemCode() {
			return systemCode;
		}

		public void setSystemCode(String systemCode) {
			this.systemCode = systemCode;
		}

		public Long getSubCompanyId() {
			return subCompanyId;
		}

		public void setSubCompanyId(Long subCompanyId) {
			this.subCompanyId = subCompanyId;
		}

		public Long getDepartmentId() {
			return departmentId;
		}

		public String getSubCompanyCode() {
			return subCompanyCode;
		}

		public void setSubCompanyCode(String subCompanyCode) {
			this.subCompanyCode = subCompanyCode;
		}

		public String getSubCompanyName() {
			return subCompanyName;
		}


		public String getSubCompanyShortTitle() {
			return subCompanyShortTitle;
		}

	}