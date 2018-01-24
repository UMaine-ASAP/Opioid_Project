// a method to write to the log file written by a man who doesn't know js

//	function() {
	var fs = require('fs');
	var action = "test123"; // this is a test variable, normally would be passed by the parameters of the function
	var date = new Date();
	var log = "[" + date.getMonth() + "/" + date.getDate() + "/" + date.getFullYear()
 	          + " " + date.getHours() + ":" + date.getMinutes()+ "] " + action;

	// appends the log to the end of the log.txt file, and adds a \n to the end so that they occur on seperate lines
	fs.appendFile('log.txt',log + "\n", function(err){
		if(err){
			console.log("append error");
		}
		else {
			console.log("append success");
		}
	});

//	}
