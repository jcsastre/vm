# Guía para revisar el test

* Empezar por [VendingMachine.java](vm/src/main/java/com/jcsastre/vendingmachine/VendingMachine.java)
que es la interfaz que define el contrato que debe cumplir cualquiera de sus implementaciones.

* [VendingMachineImpl.java](https://github.com/jcsastre/vm/blob/master/src/main/java/com/jcsastre/vendingmachine/VendingMachineImpl.java)
es una implementación de [VendingMachine.java](vm/src/main/java/com/jcsastre/vendingmachine/VendingMachine.java).

* [VendingMachineImpl.java](https://github.com/jcsastre/vm/blob/master/src/main/java/com/jcsastre/vendingmachine/VendingMachineImpl.java)
no es thread-safe porque asumo que solo hay una persona usando la vending machine (ya sea un cliente
o el operador).

* Los tests [VendingMachineImplAtStateProductNotSelectedTests.java](vm/src/test/java/com/jcsastre/vendingmachine/VendingMachineImplAtStateProductNotSelectedTests.java),
[VendingMachineImplAtStateProductSelectedTests.java](vm/src/test/java/com/jcsastre/vendingmachine/VendingMachineImplAtStateProductSelectedTests.java),
[VendingMachineImplResetCommandTests.java](vm/src/test/java/com/jcsastre/vendingmachine/VendingMachineImplResetCommandTests.java), permiten entender
el comportamiento esperado por parte de [VendingMachineImpl.java](https://github.com/jcsastre/vm/blob/master/src/main/java/com/jcsastre/vendingmachine/VendingMachineImpl.java).

* La sección [Issues](https://github.com/jcsastre/vm/issues) de este repositorio GitHub contiene
cosas pendientes de implementar o mejorar.