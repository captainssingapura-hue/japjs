class Bob{
    test1(){
        if(!(Alice1 instanceof AliceClass)){
            throw new TypeError("Alice must be of AliceClass")
        }
        return Alice1.name();
    }
    test2(){
        return Alice2.name();
    }
    createSvgElement(svgString){
        const parser = new DOMParser();
        const doc = parser.parseFromString(svgString, "image/svg+xml");
        return doc.documentElement;
    }
    wonderlandCharacters(){
        return [CheshireCat, WhiteRabbit, MadHatter, QueenOfHearts].map(
            svg => this.createSvgElement(svg)
        );
    }
}