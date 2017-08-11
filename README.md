# Guía para revisar el test

* La interfaz [VendingMachine.java](vm/src/main/java/com/jcsastre/vendingmachine/VendingMachine.java)
define el contrato que de deben cumplir cualquiera de sus implementaciones.

* La clase [VendingMachineImpl.java](https://github.com/jcsastre/vm/blob/master/src/main/java/com/jcsastre/vendingmachine/VendingMachineImpl.java)
es una implementación de [VendingMachine.java](vm/src/main/java/com/jcsastre/vendingmachine/VendingMachine.java).

* Los tests [VendingMachineImplAtStateProductNotSelectedTests.java](vm/src/test/java/com/jcsastre/vendingmachine/VendingMachineImplAtStateProductNotSelectedTests.java),
[VendingMachineImplAtStateProductSelectedTests.java](vm/src/test/java/com/jcsastre/vendingmachine/VendingMachineImplAtStateProductSelectedTests.java),
[VendingMachineImplResetCommandTests.java](vm/src/test/java/com/jcsastre/vendingmachine/VendingMachineImplResetCommandTests.java), permiten entender
el comportamiento esperado por parte de [VendingMachineImpl.java](https://github.com/jcsastre/vm/blob/master/src/main/java/com/jcsastre/vendingmachine/VendingMachineImpl.java).

* [Issues](https://github.com/jcsastre/vm/issues) contiene algunos temas pendientes
de mejorar.
