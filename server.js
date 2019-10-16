var fs = require('fs')
var express = require('express')
var multer = require('multer')

var app = express();
var meta_data = {name:"",extension:""}
var imageSavePath = 'C:/Projects/BE_Project/DownloadedDisease/'
var storage = multer.diskStorage({
  destination : function(req,file,cb){
      cb(null,imageSavePath)
  },
  filename : function(req,file,cb){
      var originalFileName = file.originalname;
      var ex = originalFileName.split(".")
      meta_data.name= ex[0]
      meta_data.extension = ex[ex.length-1]
      cb(null,ex[0]+"."+ex[ex.length-1])
  }
})
var uploadImage = multer({storage:storage})

app.post('/uploadImage',uploadImage.any(),function(req,res){
  if(meta_data.extension == "png" || meta_data.extension == "jpg" || meta_data.extension == "jpeg")
  {
    console.log("Upload Success");
    var status = {status:"OK"}
    res.send(JSON.stringify(status))
    }
  else {
    console.log("Upload Fail");
    fs.unlink(imageSavePath+meta_data.name+"."+meta_data.extension,function(err,d){})
    res.send("Please upload images only")
  }

})

app.listen(8000,'192.168.43.110')