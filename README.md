# Guía para revisar el test técnico

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

* Si os fijáis en el [historial de commits](https://github.com/jcsastre/vm/commits/master),
he seguido un enfoque test fails first, y luego implementar código hasta que los test
pasen correctamente.

* Si en algún momento he hecho alguna refactorización, los tests me han sido de utiliadad
para comprobar el progreso de la refactorización. Lógicamente cuando la refactorización
afecta a muy alto nivel, los tests deben ser refactorizados correctamente.