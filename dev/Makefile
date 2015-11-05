JC = javac

VAR = ../config.json 

.SUFFIXES: .java .class
.java.class:
	$(JC) $*.java

CLASSES = \
	Field.java\
	Relation.java\
	DataObj.java\
	PageObj.java\
	Parser.java\
	SqlGenerator.java\
	GenerateApp.java\
	ApiCreator.java\
	CordovaGenerator.java

default: classes

json: org/json/*.java
	$(JC) org/json/*.java

classes: $(CLASSES:.java=.class)

run: classes
	java GenerateApp $(VAR)

clean:
	$(RM) *.class
