JC = javac

.SUFFIXES: .java .class
.java.class:
	$(JC) $*.java

CLASSES = \
        Test.java

default: classes

json: org/json/*.java
	$(JC) org/json/*.java

classes: $(CLASSES:.java=.class)

run: 
	java Test

clean:
	$(RM) *.class
