nonRepeatingDigits = function(){
    var number = db.phones.find({},{"display": 1}).toArray();

    var res = [];
    for (var i=0 ; i<number.length ; i++){
        var number = number[i].display.split("-")[1];
        
        var arr = [];
        var flag = true;

        for(var j=0 ; j<number.length ; j++){
            if (arr.includes(number[j])){
                flag = false;
                break;
            }
            arr.push(number[j]);
        }

        if (flag){
            res.push(number[i]);
        }
    }
    
    return res;
}
