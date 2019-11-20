package com.example.strayanimaltracker.entity

data class User (var id: String) {

    var nome: String = ""
    var sobrenome: String = ""
    var email: String = ""

    constructor(id: String, nome: String, sobrenome: String, email: String): this(id){
        this.nome = nome
        this.sobrenome = sobrenome
        this.email = email
    }
}