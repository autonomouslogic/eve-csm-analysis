.PHONY: init process thymeleaf serve test lint format clean
SHELL := /bin/bash

init:
	python3 -m venv .venv
	source .venv/bin/activate && pip install -r requirements.txt

process:
	./gradlew csmProcess --stacktrace

thymeleaf:
	./gradlew csmThymelead --stacktrace

mkdocs-serve:
	source .venv/bin/activate && mkdocs serve

mkdocs-build:
	source .venv/bin/activate && mkdocs build

test:
	./gradlew test --stacktrace

lint:
	./gradlew spotlessCheck --stacktrace

format:
	./gradlew spotlessApply --stacktrace

clean:
	./gradlew clean --stacktrace

