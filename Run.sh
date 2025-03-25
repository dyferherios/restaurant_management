clear
#javac -proc:none Main.java
javac -d out -cp "libs/*" -sourcepath src $(find src -name "*.java")
#java -cp ":libs/postgresql-42.7.3.jar:out" Main
#java -cp "out:libs/*" org.junit.platform.console.ConsoleLauncher --select-class test.DishTest
#java -cp "out:libs/*" org.junit.platform.console.ConsoleLauncher --select-class test.IngredientTest
java -cp "out:libs/*" org.junit.platform.console.ConsoleLauncher --select-class test.OrderTest
#psql -h localhost -p 5432 -U dyferherios restaurant_management;