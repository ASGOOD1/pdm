package com.example.pdm
data class ProductCommand(val prodid: Int, val number: Int, val cmd: Int)
data class Commands (val commandid: Int, val clientid: Int, val courierid: Int, val price: Int, val room: String){
    companion object {
        var commandsList = mutableListOf<Commands>()
        fun insertCommand(c: Commands){
            commandsList.add(c);
        }
    }
    private var commandsProductsList = mutableListOf<ProductCommand>()
    fun insertCommandProduct(p: ProductCommand) {
        commandsProductsList.add(p);
    }
    fun getCommandsProducts() : MutableList<ProductCommand> {
        return commandsProductsList
    }

}