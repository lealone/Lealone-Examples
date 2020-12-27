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
		    this.screenType = screenType;
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