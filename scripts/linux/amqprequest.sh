#!/usr/bin/env bash
java -cp "$REACT_HOME/target/reactive-jar-with-dependencies.jar" consistency.AMQPRequest $1 $2
