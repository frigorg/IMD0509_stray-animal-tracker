package com.example.strayanimaltracker.entity

class Post() {

    var id: String = ""
    var idUsuario: String = ""
    var nome: String = ""
    var sexo: String = ""
    var especie: String = ""
    var data: String = ""

        constructor(id: String, idUsuario: String, nome: String, sexo: String, especie: String, data: String):this() {
            this.id = id
            this.idUsuario = idUsuario
            this.nome= nome
            this.sexo= sexo
            this.especie = especie
            this.data = data
        }
}