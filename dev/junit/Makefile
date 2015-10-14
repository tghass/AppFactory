JUNIT=.:junit-4.12.jar
HAM=:hamcrest-core-1.3.jar
CORE=org.junit.runner.JUnitCore
code: Calculator.java
	javac Calculator.java

tests: CalculatorTest.java
	javac -cp $(JUNIT) CalculatorTest.java

test: tests
	java -cp $(JUNIT)$(HAM) $(CORE) CalculatorTest

run: code
	java Calculator


