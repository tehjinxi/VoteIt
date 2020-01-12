package com.example.voteit.Database

class Member{
    var ic:String=""
    var pass:String=""
    var name:String=""
    var email:String=""
    var phone:String= ""
    var image:String=""

    constructor(ic:String,pass:String,name:String,email:String,phone:String,image:String){
        this.ic=ic
        this.pass=pass
        this.name=name
        this.phone=phone
        this.email=email
        this.image=image


    }
}