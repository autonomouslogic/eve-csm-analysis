.PHONY: analysis test lint format clean
SHELL := /bin/bash

process:
	./gradlew csmProcess --stacktrace

thymeleaf:
	./gradlew csmThymelead --stacktrace

test:
	./gradlew test --stacktrace

lint:
	./gradlew spotlessCheck --stacktrace

format:
	./gradlew spotlessApply --stacktrace

clean:
	./gradlew clean --stacktrace

init:
	python3 -m venv .venv
	source .venv/bin/activate && pip install -r requirements.txt

serve:
	source .venv/bin/activate && mkdocs serve
