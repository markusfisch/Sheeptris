OBJECTS = src/*.java
JAR = sheeptris.jar
BIN = java -jar $(JAR)
BUILD = build

all: $(JAR)
	java -jar $(JAR)

two: $(JAR)
	java -jar $(JAR) \
		res/players/White.player \
		res/players/Black.player

three: $(JAR)
	java -jar $(JAR) \
		res/players/White.player \
		res/players/Black.player \
		res/players/Grey.player

$(JAR): $(OBJECTS)
	[ -d $(BUILD) ] || mkdir $(BUILD)
	javac -d $(BUILD) $(OBJECTS)
	(cd $(BUILD) && jar cfm ../$(JAR) ../src/MANIFEST ../res .)

clean:
	rm -rf $(BUILD) $(JAR)
