    const screenSwitch = {
	  data() {
		return {
		  screenType: "",
		  pageType: ""
		}
	  },
	  methods: {
	    setPage(screenType, pageType) {
		  if(this.screenType != screenType) {
		    sessionStorage.pageType = pageType;
			location.href = "/" + screenType + "/index.html";
			return;
		  }
		  this.pageType = pageType;
		},
		display(pageType) {
		  return this.pageType == pageType;
		}
	  }
	}