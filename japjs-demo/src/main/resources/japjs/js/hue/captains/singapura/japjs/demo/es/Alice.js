class AliceClass{
    constructor(index /*int*/){
        this.i = index;
    }

    name(){
        return "Alice No." + this.i;
    }
}

const Alice1 = new AliceClass(1);
const Alice2 = new AliceClass(2);