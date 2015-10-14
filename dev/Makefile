JC = javac

.SUFFIXES: .java .class
.java.class:
	$(JC) $*.java

CLASSES = \
		Field.java\
		Relation.java\
        Parser.java\
		DataObj.java

default: classes

json: org/json/*.java
	$(JC) org/json/*.java

classes: $(CLASSES:.java=.class)

run: classes
	java Parser

clean:
	$(RM) *.class
