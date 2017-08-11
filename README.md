# Guía para revisar el test técnico

* Empezar por [VendingMachine.java](https://github.com/jcsastre/vm/blob/master/src/main/java/com/jcsastre/vendingmachine/VendingMachine.java)
que es la interfaz que define el contrato que debe cumplir cualquier clase que pretendar implementar el
modelo de vending machine que he diseñado.

* [VendingMachineImpl.java](https://github.com/jcsastre/vm/blob/master/src/main/java/com/jcsastre/vendingmachine/VendingMachineImpl.java)
es una implementación de [VendingMachine.java](vm/src/main/java/com/jcsastre/vendingmachine/VendingMachine.java).

* [VendingMachineImpl.java](https://github.com/jcsastre/vm/blob/master/src/main/java/com/jcsastre/vendingmachine/VendingMachineImpl.java)
no es thread-safe porque asumo que solo hay una persona usando la vending machine (ya sea un cliente
o el operador).

* Examinar los tests:
  * [VendingMachineImplAtStateProductNotSelectedTests.java](https://github.com/jcsastre/vm/blob/master/src/test/java/com/jcsastre/vendingmachine/VendingMachineImplAtStateProductNotSelectedTests.java):
  Permite entender el comportamiento esperado de [VendingMachineImpl.java](https://github.com/jcsastre/vm/blob/master/src/main/java/com/jcsastre/vendingmachine/VendingMachineImpl.java) 
  en el estado de producto no seleccionado.
  * [VendingMachineImplAtStateProductSelectedTests.java](https://github.com/jcsastre/vm/blob/master/src/test/java/com/jcsastre/vendingmachine/VendingMachineImplAtStateProductSelectedTests.java):
  Permite entender el comportamiento esperado de [VendingMachineImpl.java](https://github.com/jcsastre/vm/blob/master/src/main/java/com/jcsastre/vendingmachine/VendingMachineImpl.java)
  en el estado de producto seleccionado.
  * [VendingMachineImplResetCommandTests.java](https://github.com/jcsastre/vm/blob/master/src/test/java/com/jcsastre/vendingmachine/VendingMachineImplResetCommandTests.java):
  Permite entender el comportamiento esperado de [VendingMachineImpl.java](https://github.com/jcsastre/vm/blob/master/src/main/java/com/jcsastre/vendingmachine/VendingMachineImpl.java)
  para el command reset.
  * [CoinChangeCalculatorAsBiggestAsPossibleTests.java](https://github.com/jcsastre/vm/blob/master/src/test/java/com/jcsastre/vendingmachine/CoinChangeCalculatorAsBiggestAsPossibleTests.java):
  Testea una implementación de la interfaz funcional [CoinsChangeCalculator.java](https://github.com/jcsastre/vm/blob/master/src/main/java/com/jcsastre/vendingmachine/CoinsChangeCalculator.java)
  que permite inyectar como dependencia en [VendingMachineImpl.java](https://github.com/jcsastre/vm/blob/master/src/main/java/com/jcsastre/vendingmachine/VendingMachineImpl.java)
  la lógica responsable de calcular el cambio en monedas.
  * [InventorizedDepositImplTests.java](https://github.com/jcsastre/vm/blob/master/src/test/java/com/jcsastre/vendingmachine/InventorizedDepositImplTests.java):
  Permite entender el comportamiento esperado para la clase genérica [InventorizedDepositImpl.java](https://github.com/jcsastre/vm/blob/master/src/main/java/com/jcsastre/vendingmachine/InventorizedDepositImpl.java)
  que es inyectada en [VendingMachineImpl.java](https://github.com/jcsastre/vm/blob/master/src/main/java/com/jcsastre/vendingmachine/VendingMachineImpl.java)
  tanto para el depósito de monedas, como para el depósito de productos.
  

* La sección [Issues](https://github.com/jcsastre/vm/issues) de este repositorio GitHub contiene
cosas pendientes de implementar o mejorar.

* Si os fijáis en el [historial de commits](https://github.com/jcsastre/vm/commits/master),
he seguido un enfoque test fails first, y luego implementar código hasta que los test
pasen correctamente.

* Si en algún momento he hecho alguna refactorización, los tests me han sido de utilidad
para comprobar el progreso de la refactorización. Lógicamente cuando la refactorización
afecta a muy alto nivel, los tests deben ser refactorizados correctamente. Esto me ha pasado
concretamente con el siguiente [commit](https://github.com/jcsastre/vm/commit/9ab73bdf2370f19c938ab98166265200ee6ef9e2).